package com.nojom.ui.clientprofile;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.databinding.ActivityLeaveReviewBinding;
import com.nojom.model.ProjectByID;
import com.nojom.model.Questions;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.segment.SegmentedButtonGroup;
import com.nojom.ui.BaseActivity;
import com.nojom.util.RatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.nojom.util.Constants.API_ADD_CLIENT_REVIEW;
import static com.nojom.util.Constants.API_REVIEW_QUESTIONS;

public class LeaveReviewActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private ArrayList<Questions.Data> questionsList;
    private MutableLiveData<ArrayList<Questions.Data>> listMutableLiveData;
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadQuestions = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsLoadQuestions() {
        return isLoadQuestions;
    }

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<ArrayList<Questions.Data>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    void init(BaseActivity activity) {
        this.activity = activity;
    }

    void getQuestions() {
        if (!activity.isNetworkConnected())
            return;

        getIsLoadQuestions().postValue(true);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_REVIEW_QUESTIONS, null, false, this);

    }


    private String getReviews(ActivityLeaveReviewBinding binding) {
        JSONArray main = new JSONArray();
        for (int i = 0; i < questionsList.size(); i++) {
            View view = binding.rvQuestions.getChildAt(i);
            RatingBar ratingBar = view.findViewById(R.id.ratingbar);
            SegmentedButtonGroup segmentedButtonGroup = view.findViewById(R.id.segmentGroup);
            EditText etComment = view.findViewById(R.id.et_comment);

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("review_id", (i + 1) + "");
                if (questionsList.get(i).type == 1) {
                    jsonObject.put("rate", (segmentedButtonGroup.getPosition() == 0) ? "NO" : "YES");
                } else if (questionsList.get(i).type == 2) {
                    jsonObject.put("rate", ratingBar.getRating());
                } else if (questionsList.get(i).type == 3) {
                    jsonObject.put("rate", etComment.getText().toString());
                }
                main.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return main.toString();
    }

    boolean isComment(ActivityLeaveReviewBinding binding) {
        for (int i = 0; i < questionsList.size(); i++) {
            View view = binding.rvQuestions.getChildAt(i);
            EditText etComment = view.findViewById(R.id.et_comment);

            if (questionsList.get(i).type == 3) {
                if (!TextUtils.isEmpty(etComment.getText().toString().trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    void submitReview(ProjectByID jobPostBids, ActivityLeaveReviewBinding binding) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(true);
        try {
            CommonRequest.AddClientReview addClientReview = new CommonRequest.AddClientReview();
            addClientReview.setClient_id(String.valueOf(jobPostBids.profileId));
            if (jobPostBids.jobPostBids != null && jobPostBids.jobPostBids.jobPostId != 0) {
                addClientReview.setJob_post_id(String.valueOf(jobPostBids.jobPostBids.jobPostId));
            }
            addClientReview.setReview(getReviews(binding));

            APIRequest apiRequest = new APIRequest();
            apiRequest.makeAPIRequest(activity, API_ADD_CLIENT_REVIEW, addClientReview.toString(), true, this);
        } catch (Exception e) {
            e.printStackTrace();
            getIsShowProgress().postValue(false);
        }

    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_ADD_CLIENT_REVIEW)) {
            getIsShowProgress().postValue(false);
            activity.toastMessage(msg);
            activity.setResult(RESULT_OK);
            activity.onBackPressed();
        } else if (urlEndPoint.equalsIgnoreCase(API_REVIEW_QUESTIONS)) {
            getIsLoadQuestions().postValue(false);
            List<Questions.Data> questions = Questions.getQuestions(decryptedData);
            if (questions != null) {
                questionsList = new ArrayList<>();
                questionsList = (ArrayList<Questions.Data>) questions;
                getListMutableLiveData().postValue(questionsList);
            }
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
        getIsLoadQuestions().postValue(false);
    }
}

package com.nojom.ui.gigs;

import static android.app.Activity.RESULT_OK;
import static com.nojom.util.Constants.API_GIG_ADD_CLIENT_REVIEW;
import static com.nojom.util.Constants.API_GIG_REVIEW_QUESTION;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.api.APIRequest;
import com.nojom.databinding.ActivityClientGigReviewBinding;
import com.nojom.model.ContractDetails;
import com.nojom.model.Questions;
import com.nojom.segment.SegmentedButton;
import com.nojom.segment.SegmentedButtonGroup;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.RatingBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

class ClientGigReviewActivityVM extends AndroidViewModel implements RecyclerviewAdapter.OnViewBindListner, View.OnClickListener, APIRequest.JWTRequestResponseListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private ActivityClientGigReviewBinding binding;
    private RecyclerviewAdapter mAdapter;
    private ArrayList<Questions.Data> questionsList;
    private ContractDetails projectGigByID;

    ClientGigReviewActivityVM(Application application, ActivityClientGigReviewBinding clientReviewBinding, BaseActivity clientReviewActivity) {
        super(application);
        binding = clientReviewBinding;
        activity = clientReviewActivity;
        initData();
    }

    private void initData() {
        binding.tvCancel.setOnClickListener(this);
        binding.tvSubmit.setOnClickListener(this);
        binding.etComment.setVisibility(View.VISIBLE);

        if (activity.getIntent() != null) {
            projectGigByID = (ContractDetails) activity.getIntent().getSerializableExtra(Constants.USER_DATA);
        }

        binding.rvQuestions.setLayoutManager(new LinearLayoutManager(activity));

        getQuestions();
    }

    private void getQuestions() {
        if (!activity.isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GIG_REVIEW_QUESTION, false, null);

    }

    private void setAdapter() {
        if (questionsList != null && questionsList.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new RecyclerviewAdapter(questionsList, R.layout.item_client_gig_review, this);
            }

            mAdapter.doRefresh(questionsList);

            if (binding.rvQuestions.getAdapter() == null) {
                binding.rvQuestions.setAdapter(mAdapter);
            }
        } else {
            if (mAdapter != null)
                mAdapter.doRefresh(questionsList);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                activity.onBackPressed();
                break;
            case R.id.tv_submit:
                if (!TextUtils.isEmpty(binding.etComment.getText().toString())) {
                    submitReview();
                } else {
                    activity.toastMessage("Please enter your comment.");
                }
                break;
        }

    }

    private void submitReview() {
        if (!activity.isNetworkConnected())
            return;

        HashMap<String, RequestBody> map = new HashMap<>();

        RequestBody reviewB = RequestBody.create(getReviews() + "", MultipartBody.FORM);
        RequestBody gigIDB = RequestBody.create(projectGigByID.gigID + "", MultipartBody.FORM);
        RequestBody clientProfIdB = RequestBody.create(projectGigByID.clientProfileID + "", MultipartBody.FORM);
        RequestBody contractIdB = RequestBody.create(projectGigByID.id + "", MultipartBody.FORM);
        RequestBody commentB = RequestBody.create(binding.etComment.getText().toString(), MultipartBody.FORM);
        RequestBody feedbackOpB = RequestBody.create(getFeedbackOptionReviews() + "", MultipartBody.FORM);

        map.put("review", reviewB);
        map.put("gigID", gigIDB);
        map.put("clientProfileID", clientProfIdB);
        map.put("contractID", contractIdB);
        map.put("comment", commentB);
        map.put("feedbackOption", feedbackOpB);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, activity, API_GIG_ADD_CLIENT_REVIEW, map);
    }

    private String getReviews() {
        JSONArray main = new JSONArray();
        for (int i = 0; i < questionsList.size(); i++) {
            View view = binding.rvQuestions.getChildAt(i);
            RatingBar ratingBar = view.findViewById(R.id.ratingbar);

            try {
                if (questionsList.get(i).type == 2) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("reviewID", (i + 1) + "");
                    jsonObject.put("rate", ratingBar.getRating());
                    main.put(jsonObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return main.toString();
    }

    private String getFeedbackOptionReviews() {
        JSONArray main = new JSONArray();
        for (int i = 0; i < questionsList.size(); i++) {
            View view = binding.rvQuestions.getChildAt(i);
            SegmentedButtonGroup segmentedButtonGroup = view.findViewById(R.id.segmentGroup);
            try {
                if (questionsList.get(i).type == 1) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("reviewID", (i + 1) + "");
                    jsonObject.put("feedback", (segmentedButtonGroup.getPosition() == 0) ? "0" : "1");
                    main.put(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return main.toString();
    }


    @Override
    public void bindView(View view, int position) {
        RelativeLayout rlQuestions = view.findViewById(R.id.rl_question);
        TextView tvQuestions = view.findViewById(R.id.tv_question);
        RatingBar ratingBar = view.findViewById(R.id.ratingbar);
        SegmentedButtonGroup segmentedButtonGroup = view.findViewById(R.id.segmentGroup);
        SegmentedButton sgNo = view.findViewById(R.id.sb_no);
        SegmentedButton sgYes = view.findViewById(R.id.sb_yes);
        EditText etComment = view.findViewById(R.id.et_comment);

        Questions.Data item = questionsList.get(position);
        sgNo.setSelectorColor(activity.getResources().getColor(R.color.red_dark));
        sgYes.setSelectorColor(activity.getResources().getColor(R.color.colorPrimary));
        rlQuestions.setVisibility(View.VISIBLE);
        etComment.setVisibility(View.GONE);
        if (item.type == 1) {
            ratingBar.setVisibility(View.GONE);
            segmentedButtonGroup.setVisibility(View.VISIBLE);
        } else {
            ratingBar.setVisibility(View.VISIBLE);
            segmentedButtonGroup.setVisibility(View.GONE);
        }

        tvQuestions.setText(item.getQuestion(activity.language));
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_GIG_ADD_CLIENT_REVIEW)) {
            activity.toastMessage(message);
            activity.setResult(RESULT_OK);
            activity.finish();
        } else if (url.equalsIgnoreCase(API_GIG_REVIEW_QUESTION)) {
            Questions questions = Questions.getGigQuestions(responseBody);
            if (questions != null && questions.data != null) {
                questionsList = new ArrayList<>();
                questionsList = (ArrayList<Questions.Data>) questions.data;
                setAdapter();
            }
        }
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {

    }
}

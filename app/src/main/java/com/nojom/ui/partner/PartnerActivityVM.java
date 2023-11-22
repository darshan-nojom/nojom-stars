package com.nojom.ui.partner;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.ui.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.nojom.util.Constants.API_ADD_PARTNER_ANSWER;

public class PartnerActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;

    private MutableLiveData<Boolean> isSubmitAnswer = new MutableLiveData<>();
    private MutableLiveData<Boolean> isSubmitAnswerSuccess = new MutableLiveData<>();


    public MutableLiveData<Boolean> getIsSubmitAnswerSuccess() {
        return isSubmitAnswerSuccess;
    }

    public MutableLiveData<Boolean> getIsSubmitAnswer() {
        return isSubmitAnswer;
    }

    public PartnerActivityVM() {

    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    void addAppSurvey(JSONArray jsonArray) {
        if (!activity.isNetworkConnected())
            return;
        getIsSubmitAnswer().postValue(true);

        APIRequest apiRequest = new APIRequest();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("answers", jsonArray.toString());
            apiRequest.makeAPIRequest(activity, API_ADD_PARTNER_ANSWER, jsonObject.toString(), true, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        if (urlEndPoint.equalsIgnoreCase(API_ADD_PARTNER_ANSWER)) {
            getIsSubmitAnswerSuccess().postValue(true);
            getIsSubmitAnswer().postValue(false);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        //activity.failureError(activity.getString(R.string.something_went_wrong));
        if (urlEndPoint.equalsIgnoreCase(API_ADD_PARTNER_ANSWER)) {
            getIsSubmitAnswer().postValue(false);
        }
    }
}

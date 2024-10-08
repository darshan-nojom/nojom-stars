package com.nojom.apis;

import androidx.lifecycle.MutableLiveData;

import com.nojom.api.APIRequest;
import com.nojom.model.SocialSurveyListModel;
import com.nojom.ui.BaseActivity;

import static com.nojom.util.Constants.API_SOCIAL_SURVEY;

public class GetSocialSurveyAPI implements APIRequest.APIRequestListener {

    private BaseActivity activity;
    private MutableLiveData<SocialSurveyListModel> socialSurveyMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<SocialSurveyListModel> getSocialSurveyMutableLiveData() {
        return socialSurveyMutableLiveData;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    public void getSocialSurveyAPI() {
        if (!activity.isNetworkConnected())
            return;

//        fragment.activity.showProgress();

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_SOCIAL_SURVEY, null, false, this);
    }


    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_SOCIAL_SURVEY)) {
            SocialSurveyListModel surveyListModel = SocialSurveyListModel.getSocialSurveys(decryptedData);
            getSocialSurveyMutableLiveData().postValue(surveyListModel);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getSocialSurveyMutableLiveData().postValue(null);
    }
}

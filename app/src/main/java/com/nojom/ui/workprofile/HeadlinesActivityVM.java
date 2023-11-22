package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.GeneralModel;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class HeadlinesActivityVM extends ViewModel implements Constants, APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<Response<GeneralModel>> updateHeadlineRes = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgressDialog = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgressDialog() {
        return isShowProgressDialog;
    }

    public MutableLiveData<Response<GeneralModel>> getUpdateHeadlineRes() {
        return updateHeadlineRes;
    }

    public HeadlinesActivityVM() {

    }

    public void init(BaseActivity activity) {
        this.activity = activity;

    }

    void updateHeadlines(String headline) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgressDialog().postValue(true);

        CommonRequest.UpdateSummary addHeadline = new CommonRequest.UpdateSummary();
        addHeadline.setContent(headline);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_HEADLINE, addHeadline.toString(), true, this);
    }

    boolean isValid(String headline, int screen) {
        if (screen == HEADLINE && activity.isEmpty(headline)) {
            activity.validationError(activity.getString(R.string.enter_professional_headline));
            return false;
        }
        if (screen == HEADLINE && activity.isValidEmail(headline)) {
            activity.validationError(activity.getString(R.string.you_cannot_enter_email));
            return false;
        }
        if (screen == OFFICE_ADD && activity.isEmpty(headline)) {
            activity.validationError(activity.getString(R.string.enter_professional_address));
            return false;
        }
        if (screen == WEBSITE && !activity.isValidUrl(headline)) {
            activity.validationError(activity.getString(R.string.enter_valid_website));
            return false;
        }
        return true;
    }

    void updateWebsite(String headline) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgressDialog().postValue(true);

        CommonRequest.UpdateWebsite updateWebsite = new CommonRequest.UpdateWebsite();
        updateWebsite.setWebsite(headline);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_WEBSITE, updateWebsite.toString(), true, this);
    }

    void addProfAddress(String headline) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgressDialog().postValue(true);

        CommonRequest.AddProAddress addProAddress = new CommonRequest.AddProAddress();
        addProAddress.setPro_address(headline);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_PRO_ADDRESS, addProAddress.toString(), true, this);

    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        getIsShowProgressDialog().postValue(false);
        if (urlEndPoint.equalsIgnoreCase(API_ADD_WEBSITE) || urlEndPoint.equalsIgnoreCase(API_ADD_PRO_ADDRESS)) {
            activity.setResult(RESULT_OK);
            activity.finish();
        } else if (urlEndPoint.equalsIgnoreCase(API_ADD_HEADLINE)) {
            getUpdateHeadlineRes().postValue(null);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgressDialog().postValue(false);
    }
}

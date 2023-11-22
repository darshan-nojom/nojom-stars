package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.ui.BaseActivity;

import static com.nojom.util.Constants.API_EMAIL_VERIFICATION;

public class VerifyEmailActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<Boolean> isVerifyEmail = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsVerifyEmail() {
        return isVerifyEmail;
    }

    private MutableLiveData<Boolean> isShowLoading = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowLoading() {
        return isShowLoading;
    }

    void verifyEmail(BaseActivity activity) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;
        getIsShowLoading().postValue(true);
        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_EMAIL_VERIFICATION, null, false, this);
    }

    boolean isValid(BaseActivity activity, String email) {
        if (!activity.isValidEmail(email)) {
            activity.validationError(activity.getString(R.string.enter_valid_email));
            return false;
        }
        return true;
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_EMAIL_VERIFICATION)) {
            activity.toastMessage(msg);
            getIsVerifyEmail().postValue(true);
        }
        getIsShowLoading().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowLoading().postValue(false);
    }
}

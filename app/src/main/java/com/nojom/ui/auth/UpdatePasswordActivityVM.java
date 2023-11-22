package com.nojom.ui.auth;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import static com.nojom.util.Constants.API_UPDATE_PASSWORD;

public class UpdatePasswordActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;

    private MutableLiveData<Boolean> isProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsProgress() {
        return isProgress;
    }

    void updatePassword(BaseActivity activity, String oldPassword, String newPassword) {
        this.activity = activity;
        if (!activity.isNetworkConnected())
            return;

        getIsProgress().postValue(true);

        CommonRequest.UpdatePassword updatePassword = new CommonRequest.UpdatePassword();
        updatePassword.setOld_password(oldPassword);
        updatePassword.setPassword(newPassword);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_PASSWORD, updatePassword.toString(), true,this);
    }

    boolean isValid(BaseActivity activity, String oldPassword, String newPassword, String confirmPassword) {
        if (activity.isEmpty(oldPassword)) {
            activity.validationError(activity.getString(R.string.enter_your_old_password));
            return false;
        }

        if (activity.isEmpty(newPassword)) {
            activity.validationError(activity.getString(R.string.enter_your_new_password));
            return false;
        }

        if (newPassword.length() < 7) {
            activity.validationError(activity.getString(R.string.minimum_length_is_eight_characters));
            return false;
        }

        if (activity.isEmpty(confirmPassword)) {
            activity.validationError(activity.getString(R.string.enter_confirm_password));
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            activity.validationError(activity.getString(R.string.doesnt_match_password));
            return false;
        }
        return true;
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        getIsProgress().postValue(false);
        activity.toastMessage(msg);
        activity.onBackPressed();
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsProgress().postValue(false);
    }
}

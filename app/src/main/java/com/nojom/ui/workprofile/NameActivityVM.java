package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_UPDATE_PROFILE;

import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

import java.util.Objects;

public class NameActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    private ResponseListener responseListener;
    private BaseActivity activity;
    private String fName, lName;

    public void setNameActivityListener(ResponseListener nameActivityListener) {
        this.responseListener = nameActivityListener;
    }

    void updateName(BaseActivity activity, String firstName, String lastName) {
        if (!activity.isNetworkConnected())
            return;

        CommonRequest.UpdateProfileName updateProfileName = new CommonRequest.UpdateProfileName();
        updateProfileName.setFirst_name(firstName);
        updateProfileName.setLast_name(lastName);

        this.activity = activity;
        fName = firstName;
        lName = lastName;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_PROFILE, updateProfileName.toString(), true, this);
    }

    public boolean isValid(BaseActivity activity, String firstName, String lastName) {
        if (activity.isEmpty(firstName)) {
            activity.validationError(activity.getString(R.string.please_enter_first_name));
            return false;
        }

        if (activity.isEmpty(lastName)) {
            activity.validationError(activity.getString(R.string.please_enter_last_name));
            return false;
        }

        return true;
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (responseListener != null) {
            Objects.requireNonNull(Preferences.getUserData(activity)).firstName = fName;
            Objects.requireNonNull(Preferences.getUserData(activity)).lastName = lName;
            responseListener.onResponseSuccess(null);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (responseListener != null) {
            responseListener.onError();
        }
    }
}

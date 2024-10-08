package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_EDIT_PLATFORM;
import static com.nojom.util.Constants.API_SAVE_PLATFORM;
import static com.nojom.util.Constants.API_UPDATE_PROFILE;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;
import com.nojom.util.Preferences;

import java.io.File;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class NameActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    private ResponseListener responseListener;
    private BaseActivity activity;
    private String fName, lName;

    public void setNameActivityListener(ResponseListener nameActivityListener) {
        this.responseListener = nameActivityListener;
    }

    void updateName(BaseActivity activity, String firstName, String lastName, int registrationSteps) {
        if (!activity.isNetworkConnected())
            return;

        CommonRequest.UpdateProfileName updateProfileName = new CommonRequest.UpdateProfileName();
        if (!TextUtils.isEmpty(firstName)) {
            updateProfileName.setFirst_name(firstName);
        }
        if (!TextUtils.isEmpty(lastName)) {
            updateProfileName.setLast_name(lastName);
        }
        updateProfileName.setRegistration_step(registrationSteps);

        this.activity = activity;
        fName = firstName;
        lName = lastName;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_PROFILE, updateProfileName.toString(), true, this);
    }

    public void updateProfile(BaseActivity activity, String uname, int steps, int verify) {
        if (!activity.isNetworkConnected())
            return;

        CommonRequest.UpdateProfile updateProfileName = new CommonRequest.UpdateProfile();
        if (!TextUtils.isEmpty(uname)) {
            updateProfileName.setUsername(uname);
        }
        if (verify == 2) {
            updateProfileName.setIs_verify(verify);
        }
        updateProfileName.setRegistration_step(steps);


        this.activity = activity;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_PROFILE, updateProfileName.toString(), true, this);
    }

    public void updateProfile(BaseActivity activity, String uname, int steps, int publicStatus, int verify) {
        if (!activity.isNetworkConnected())
            return;

        CommonRequest.UpdateProfile updateProfileName = new CommonRequest.UpdateProfile();
        if (!TextUtils.isEmpty(uname)) {
            updateProfileName.setUsername(uname);
        }
        if (verify == 2) {
            updateProfileName.setIs_verify(verify);
        }
        updateProfileName.setLocation_public(publicStatus);
        updateProfileName.setRegistration_step(steps);


        this.activity = activity;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_PROFILE, updateProfileName.toString(), true, this);
    }

    public void updateProfile(BaseActivity activity, int gender, int steps) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;
        //getShowProgress().postValue(status);

        CommonRequest.UpdateProfile updateProfile = new CommonRequest.UpdateProfile();
        if (gender != -1) {
            updateProfile.setGender(gender);
        }
        updateProfile.setRegistration_step(steps);


        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_PROFILE, updateProfile.toString(), true, this);

    }

    public void updateProfile(BaseActivity activity, int gender, int status, int steps) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;
        //getShowProgress().postValue(status);

        CommonRequest.UpdateProfile updateProfile = new CommonRequest.UpdateProfile();
        if (gender != -1) {
            updateProfile.setGender(gender);
        }
        updateProfile.setRegistration_step(steps);
        updateProfile.setGender_public_status(status);


        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_PROFILE, updateProfile.toString(), true, this);

    }

    public void updateProfile(BaseActivity activity, String fNme, File profileFile, int steps) {
        if (!activity.isNetworkConnected()) return;

        this.activity = activity;

        MultipartBody.Part body = null;
        if (profileFile != null) {
            profileFile = CompressFile.getCompressedImageFile(profileFile);
            if (profileFile != null) {
                Uri selectedUri = Uri.fromFile(profileFile);
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());

                RequestBody requestFile = null;
                if (mimeType != null) {
                    requestFile = RequestBody.create(profileFile, MediaType.parse(mimeType));
                }

                Headers.Builder headers = new Headers.Builder();
                headers.addUnsafeNonAscii("Content-Disposition", "form-data; name=\"file\"; filename=\"" + profileFile.getName() + "\"");

                if (requestFile != null) {
                    body = MultipartBody.Part.create(headers.build(), requestFile);
                }
            }
        }

        CommonRequest.UpdateProfile updateProfile = new CommonRequest.UpdateProfile();
        if (!TextUtils.isEmpty(fNme)) {
            updateProfile.setFirst_name(fNme);
        }
        updateProfile.setRegistration_step(steps);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_UPDATE_PROFILE, updateProfile.toString(), this, body);

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
        if (urlEndPoint.equals(API_SAVE_PLATFORM)) {
            getSaveCompanyProgress().postValue(11);
            //activity.toastMessage(msg);
        } else {
            if (responseListener != null) {
                Objects.requireNonNull(Preferences.getUserData(activity)).firstName = fName;
                Objects.requireNonNull(Preferences.getUserData(activity)).lastName = lName;
                responseListener.onResponseSuccess(null);
            }
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (responseListener != null) {
            responseListener.onError();
        }
        getSaveCompanyProgress().postValue(11);
    }

    public void addPlatform(String username, String count, int pId, int pTypeId, int fStatus) {
        getSaveCompanyProgress().postValue(1);
        CommonRequest.AddSocialMedia deleteSurveyImage = new CommonRequest.AddSocialMedia();
        deleteSurveyImage.setSocial_platform_id(pId);
        deleteSurveyImage.setSocial_platform_type_id(pTypeId);
        deleteSurveyImage.setUsername(username);
        deleteSurveyImage.setIs_public(fStatus);
        if (!TextUtils.isEmpty(count)) {
            deleteSurveyImage.setFollowers(Integer.parseInt(count));
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_SAVE_PLATFORM, deleteSurveyImage.toString(), true, this);
    }

    private final MutableLiveData<Integer> saveCompanyProgress = new MutableLiveData<>();

    public MutableLiveData<Integer> getSaveCompanyProgress() {
        return saveCompanyProgress;
    }
}

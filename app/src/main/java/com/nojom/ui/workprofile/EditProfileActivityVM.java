package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_UPDATE_PROFILE;
import static com.nojom.util.Constants.COUNTRY_CODE;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.databinding.ActivityEditProfileBinding;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;
import com.nojom.util.Preferences;

import java.io.File;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditProfileActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    private BaseActivity activity;
    private MutableLiveData<Boolean> showProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> updateProfileSuccess = new MutableLiveData<>();
    private ActivityEditProfileBinding binding;

    public EditProfileActivityVM() {
    }

    public void init(BaseActivity activity, ActivityEditProfileBinding binding) {
        this.activity = activity;
        this.binding = binding;
    }

    public MutableLiveData<Boolean> getShowProgress() {
        return showProgress;
    }

    public MutableLiveData<Boolean> getUpdateProfileSuccess() {
        return updateProfileSuccess;
    }

    void updateProfile(String fNme, String emailId, String mobile, String mobPrefix,
                       String username, File profileFile, int gender, String arName, int msg, int offer, int email, int whatsapp) {
        if (!activity.isNetworkConnected()) return;

        getShowProgress().postValue(true);

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
        updateProfile.setWhatsapp_number(mobPrefix + "." + mobile);
        if (!TextUtils.isEmpty(emailId)) {
            updateProfile.setBussiness_email(emailId);
        } else {
            updateProfile.setBussiness_email("");
        }
        updateProfile.setFirst_name(fNme);
        if (!TextUtils.isEmpty(arName)) {
            updateProfile.setLast_name(arName);
        } else {
            updateProfile.setLast_name("");
        }
//        updateProfile.setMobile_prefix(mobPrefix);
        if (username != null && !TextUtils.isEmpty(username)) {
            updateProfile.setUsername(username);
        }
        if (gender != -1) {
            updateProfile.setGender(gender);
        }
        updateProfile.setShow_message_button(msg);
        updateProfile.setShow_send_offer_button(offer);
        updateProfile.setShow_email(email);
        updateProfile.setShow_whatsapp(whatsapp);
//        updateProfile.setShowAge(1);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_UPDATE_PROFILE, updateProfile.toString(), this, body);

    }

    public void updateProfile(String reOrder) {
        if (!activity.isNetworkConnected()) return;

        getShowProgress().postValue(true);


        CommonRequest.UpdateProfile updateProfile = new CommonRequest.UpdateProfile();
        updateProfile.setSettings_order(reOrder);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_PROFILE, updateProfile.toString(), true, this);

    }


    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (binding != null) {
            String nameCode = binding.ccp.getSelectedCountryNameCode();
            Preferences.writeString(activity, COUNTRY_CODE, nameCode);
        }
        activity.getProfile();
        getShowProgress().postValue(false);
        if (urlEndPoint.equals(API_UPDATE_PROFILE)) {
            getUpdateProfileSuccess().postValue(true);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getShowProgress().postValue(false);
        if (urlEndPoint.equals(API_UPDATE_PROFILE)) {
            getUpdateProfileSuccess().postValue(false);
        }
    }
}

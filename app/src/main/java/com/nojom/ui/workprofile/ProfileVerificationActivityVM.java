package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_ADD_HEADLINE;
import static com.nojom.util.Constants.API_ADD_WEBSITE;
import static com.nojom.util.Constants.API_UPDATE_PROFILE;
import static com.nojom.util.Constants.COUNTRY_CODE;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.databinding.ActivityPrivateInfoBinding;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;
import com.nojom.util.Preferences;

import java.io.File;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileVerificationActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<Integer> showProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> showProgressAds = new MutableLiveData<>();
    private ActivityPrivateInfoBinding binding;

    public ProfileVerificationActivityVM() {
    }

    public void init(BaseActivity activity, ActivityPrivateInfoBinding binding) {
        this.activity = activity;
        this.binding = binding;
    }

    public MutableLiveData<Integer> getShowProgress() {
        return showProgress;
    }

    public MutableLiveData<Integer> getShowProgressAds() {
        return showProgressAds;
    }

    void updateProfile(int status, String fNme, String lName, String username, File profileFile, String website,
                       int websiteStatus, int gender, String dob, double min, double max, String aboutme, int ageShow) {
        if (!activity.isNetworkConnected())
            return;

        getShowProgress().postValue(status);

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
//        updateProfile.setContactNo(mobile);
//        updateProfile.setEmail(emailId);
        if (fNme != null) {
            updateProfile.setFirst_name(fNme);
        }
        if (lName != null) {
            updateProfile.setLast_name(lName);
        }
//        updateProfile.setMobile_prefix(mobPrefix);
        if (username != null) {
            updateProfile.setUsername(username);
        }

        if (gender != -1) {
            updateProfile.setGender(gender);
        }
        if (dob != null) {
            updateProfile.setBirthDate(dob);
        }
        if (min != 0) {
            updateProfile.setMinPrice(min);
        }
        if (max != 0) {
            updateProfile.setMaxPrice(max);
        }
        if (!TextUtils.isEmpty(aboutme)) {
            updateProfile.setAbout_me(aboutme);
        }
        if (ageShow != 0) {
            updateProfile.setShowAge(ageShow);
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_UPDATE_PROFILE, updateProfile.toString(), this, body);

    }

    void updateProfile() {
        if (!activity.isNetworkConnected())
            return;

        CommonRequest.UpdateProfileVerification updateProfile = new CommonRequest.UpdateProfileVerification();
        updateProfile.setIs_verified(2);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_PROFILE, updateProfile.toString(), true, this);

    }

    void updateHeadlines(String headline) {
        if (!activity.isNetworkConnected())
            return;
        getShowProgress().postValue(1);

        CommonRequest.UpdateSummary addHeadline = new CommonRequest.UpdateSummary();
        addHeadline.setContent(headline);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_HEADLINE, addHeadline.toString(), true, this);
    }

    void updateProfileAds(int status, String fNme, String lName, String username, File profileFile, String website, int websiteStatus, int gender, String dob, double min, double max) {
        if (!activity.isNetworkConnected())
            return;

        getShowProgressAds().postValue(status);

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
//        updateProfile.setContactNo(mobile);
//        updateProfile.setEmail(emailId);
        if (fNme != null) {
            updateProfile.setFirst_name(fNme);
        }
        if (lName != null) {
            updateProfile.setLast_name(lName);
        }
//        updateProfile.setMobile_prefix(mobPrefix);
        if (username != null) {
            updateProfile.setUsername(username);
        }

        if (gender != -1) {
            updateProfile.setGender(gender);
        }
        if (dob != null) {
            updateProfile.setBirthDate(dob);
        }
        if (min != 0) {
            updateProfile.setMinPrice(min);
        }
        if (max != 0) {
            updateProfile.setMaxPrice(max);
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_UPDATE_PROFILE, updateProfile.toString(), this, body);

    }


    void updateProfile(int status, String website, int websiteStatus) {
        if (!activity.isNetworkConnected())
            return;

        getShowProgress().postValue(status);

        CommonRequest.UpdateWebsite updateWebsite = new CommonRequest.UpdateWebsite();
        updateWebsite.setWebsite(website);
        if (websiteStatus != -1) {
            updateWebsite.setStatus(websiteStatus);
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_WEBSITE, updateWebsite.toString(), true, this);
    }


    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (binding != null) {
            String nameCode = binding.ccp.getSelectedCountryNameCode();
            Preferences.writeString(activity, COUNTRY_CODE, nameCode);
        }
        activity.getProfile();
        getShowProgress().postValue(-1);
        getShowProgressAds().postValue(-1);
        activity.isClickableView=false;
        activity.disableEnableTouch(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getShowProgress().postValue(-1);
        getShowProgressAds().postValue(-1);
        activity.toastMessage(message);
        activity.isClickableView=false;
        activity.disableEnableTouch(false);
    }
}

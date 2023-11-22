package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_UPDATE_PROFILE;
import static com.nojom.util.Constants.COUNTRY_CODE;

import android.annotation.SuppressLint;
import android.net.Uri;
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

public class MyProfileActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<Boolean> showProgress = new MutableLiveData<>();
    private ActivityPrivateInfoBinding binding;

    public MyProfileActivityVM() {
    }

    public void init(BaseActivity activity, ActivityPrivateInfoBinding binding) {
        this.activity = activity;
        this.binding = binding;
    }

    public MutableLiveData<Boolean> getShowProgress() {
        return showProgress;
    }

    void updateProfile(String fNme, String lName, String username, File profileFile) {
        if (!activity.isNetworkConnected())
            return;

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

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_UPDATE_PROFILE, updateProfile.toString(), this, body);

    }


    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (binding != null) {
            String nameCode = binding.ccp.getSelectedCountryNameCode();
            Preferences.writeString(activity, COUNTRY_CODE, nameCode);
        }
        activity.getProfile();
        getShowProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getShowProgress().postValue(false);
    }
}

package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_ADD_PROFILE_VERIF;
import static com.nojom.util.Constants.API_PROFILE_VERIFICATION;

import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.VerifyID;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class VerifyIDActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    private List<VerifyID.Data> verifyIdsList;
    private MutableLiveData<List<VerifyID.Data>> listMutableLiveData;
    private BaseActivity activity;
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<List<VerifyID.Data>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    void verifyId(File file, BaseActivity activity) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;

        getIsShowProgress().postValue(true);

        MultipartBody.Part body = null;
        if (file != null) {
            if (file.getAbsolutePath().contains(".jpg") || file.getAbsolutePath().contains(".png")
                    || file.getAbsolutePath().contains(".jpeg")) {
                file = CompressFile.getCompressedImageFile(file);
            }

            if (file != null) {
                Uri selectedUri = Uri.fromFile(file);
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());

                RequestBody requestFile = null;
                if (mimeType != null) {
                    requestFile = RequestBody.create(file, MediaType.parse(mimeType));
                }

                Headers.Builder headers = new Headers.Builder();
                headers.addUnsafeNonAscii("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\"");

                if (requestFile != null) {
                    body = MultipartBody.Part.create(headers.build(), requestFile);
                }
            }
        }

        CommonRequest.ProfileVerification profileVerification = new CommonRequest.ProfileVerification();
        profileVerification.setType("1");

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_ADD_PROFILE_VERIF, profileVerification.toString(), this, body);
    }

    void getVerifyIdsList(BaseActivity activity) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;
//        activity.showProgress();

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_PROFILE_VERIFICATION, null, false, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_ADD_PROFILE_VERIF)) {
            getVerifyIdsList(activity);
        } else {
            List<VerifyID.Data> model = VerifyID.getProfileVerifications(decryptedData);
            if (model != null) {
                verifyIdsList = new ArrayList<>();
                verifyIdsList = model;
                getListMutableLiveData().postValue(verifyIdsList);
            }
            getIsShowProgress().postValue(false);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
    }
}

package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_ADD_MAWTHOUQ;
import static com.nojom.util.Constants.API_ADD_PROFILE_VERIF;
import static com.nojom.util.Constants.API_MAW_REQUEST_STATUS;
import static com.nojom.util.Constants.API_PROFILE_VERIFICATION;
import static com.nojom.util.Constants.API_PROFILE_VERIFICATION_MAW;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.GigDetails;
import com.nojom.model.MawthouqStatus;
import com.nojom.model.VerifyID;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.ResetPasswordDoneActivity;
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
    private MutableLiveData<MawthouqStatus> mawthouqStatusMutableLiveData;
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

    public MutableLiveData<MawthouqStatus> getMawthouqStatusMutableLiveData() {
        if (mawthouqStatusMutableLiveData == null) {
            mawthouqStatusMutableLiveData = new MutableLiveData<>();
        }
        return mawthouqStatusMutableLiveData;
    }

    void verifyId(File file, BaseActivity activity, int type, String mawNo) {
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
        profileVerification.setType("" + type);
        if (!TextUtils.isEmpty(mawNo)) {
            profileVerification.setMawthooq_number("" + mawNo);
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_ADD_PROFILE_VERIF, profileVerification.toString(), this, body);
    }

    void verifyId(File file, BaseActivity activity, int type, String mawNo, String pass) {
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
        profileVerification.setType("" + type);
        if (!TextUtils.isEmpty(mawNo)) {
            profileVerification.setMawthooq_number("" + mawNo);
        }

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_ADD_PROFILE_VERIF, profileVerification.toString(), this, body);
    }

    void submitMawthouq(BaseActivity activity, String oldMawNo, String mawNo, String pass) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;

        getIsShowProgress().postValue(true);

        if (TextUtils.isEmpty(oldMawNo)) {

            CommonRequest.MawSubmit profileVerification = new CommonRequest.MawSubmit();
            if (!TextUtils.isEmpty(mawNo)) {
                profileVerification.setMawthooq("" + mawNo);
            }
            profileVerification.setPassword(pass);

            APIRequest apiRequest = new APIRequest();
            apiRequest.submitMawthooq(activity, API_ADD_MAWTHOUQ, profileVerification.toString(), true, this);
        } else {

            CommonRequest.MawOldSubmit profileVerification = new CommonRequest.MawOldSubmit();
            if (!TextUtils.isEmpty(mawNo)) {
                CommonRequest.MawOld old = new CommonRequest.MawOld();
                old.setOld_number(oldMawNo);
                old.setNew_number(mawNo);
                profileVerification.setMawthooq(old);
            }
            profileVerification.setPassword(pass);

            APIRequest apiRequest = new APIRequest();
            apiRequest.submitMawthooq(activity, API_ADD_MAWTHOUQ, profileVerification.toString(), true, this);
        }
    }

    void getVerifyIdsList(BaseActivity activity) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;
//        activity.showProgress();

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_PROFILE_VERIFICATION, null, false, this);
    }

    String screen;

    void getMawthooqList(BaseActivity activity, String screen) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;
        this.screen = screen;
//        activity.showProgress();

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_PROFILE_VERIFICATION_MAW, null, false, this);
    }

    void getMawthooqStatus(BaseActivity activity) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;
//        activity.showProgress();

        APIRequest apiRequest = new APIRequest();
        apiRequest.getMawStatus(activity, API_MAW_REQUEST_STATUS, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_ADD_PROFILE_VERIF)) {
            if (screen != null && screen.equals("maw")) {
                activity.getProfile();
//                getMawthooqList(activity, screen);
                activity.toastMessage(msg);
                activity.onBackPressed();
            } else {
                getVerifyIdsList(activity);
            }

        } else if (urlEndPoint.equalsIgnoreCase(API_ADD_MAWTHOUQ)) {
            activity.getProfile();
            activity.toastMessage(msg);
            Intent intent = new Intent(activity, ResetPasswordDoneActivity.class);
            intent.putExtra("isFrom", true);
            activity.startActivity(intent);
        } else if (urlEndPoint.equalsIgnoreCase(API_MAW_REQUEST_STATUS)) {
            MawthouqStatus status = MawthouqStatus.getStatus(decryptedData);
            if (status != null) {
                getMawthouqStatusMutableLiveData().postValue(status);
            }
            getIsShowProgress().postValue(false);
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
        if (urlEndPoint.equals(API_PROFILE_VERIFICATION_MAW)) {
            verifyIdsList = new ArrayList<>();
            getListMutableLiveData().postValue(verifyIdsList);
        } else if (urlEndPoint.equalsIgnoreCase(API_MAW_REQUEST_STATUS) || urlEndPoint.equalsIgnoreCase(API_ADD_MAWTHOUQ)) {
            activity.toastMessage(message);
        }
        //activity.toastMessage(message);
    }
}

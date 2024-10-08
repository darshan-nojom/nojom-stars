package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_ADD_SOCIAL_PLATFORMS;
import static com.nojom.util.Constants.API_DELETE_PLATFORM;
import static com.nojom.util.Constants.API_EDIT_PLATFORM;
import static com.nojom.util.Constants.API_GET_CONNECTED_PLATFORM;
import static com.nojom.util.Constants.API_GET_INF_PLATFORM;
import static com.nojom.util.Constants.API_GET_PLATFORM;
import static com.nojom.util.Constants.API_RE_ORDER_MEDIA;
import static com.nojom.util.Constants.API_SAVE_PLATFORM;
import static com.nojom.util.Constants.API_SOCIAL_MEDIA_REQUEST;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.model.SocialMediaResponse;
import com.nojom.model.SocialPlatformResponse;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MyPlatformActivityVM extends ViewModel implements APIRequest.APIRequestListener, APIRequest.JWTRequestResponseListener {
    private BaseActivity activity;
    private MutableLiveData<Integer> isHideProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> isShowProgress = new MutableLiveData<>();

    private final MutableLiveData<Integer> saveCompanyProgress = new MutableLiveData<>();

    public MutableLiveData<Integer> getSaveCompanyProgress() {
        return saveCompanyProgress;
    }

    private MutableLiveData<ArrayList<SocialPlatformResponse.Data>> socialDataList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<SocialMediaResponse.Data>> socialMediaDataList = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ConnectedSocialMedia.Data>> connectedMediaDataList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<SocialPlatformResponse.Data>> getSocialDataList() {
        return socialDataList;
    }

    public MutableLiveData<ArrayList<SocialMediaResponse.Data>> getSocialMediaDataList() {
        return socialMediaDataList;
    }

    public MutableLiveData<ArrayList<ConnectedSocialMedia.Data>> getConnectedMediaDataList() {
        return connectedMediaDataList;
    }

    public MutableLiveData<Integer> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Integer> getIsHideProgress() {
        return isHideProgress;
    }


    void addSocialPlatforms(BaseActivity activity, String platformId, String urls, String follow) {
        if (!activity.isNetworkConnected())
            return;

        CommonRequest.SocialPlatform updateProfileName = new CommonRequest.SocialPlatform();
        updateProfileName.setPlatform_id(platformId);
        updateProfileName.setSocial_platform_url(urls);
        updateProfileName.setSocial_platform_followers(follow);

        this.activity = activity;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_SOCIAL_PLATFORMS, updateProfileName.toString(), true, this);
    }

//    void getSocialPlatforms(BaseActivity activity) {
//        if (!activity.isNetworkConnected())
//            return;
//
//        this.activity = activity;
//
//        APIRequest apiRequest = new APIRequest();
//        apiRequest.makeAPIRequest(activity, API_GET_SOCIAL_PLATFORMS, null, false, this);
//    }

    public void getInfluencerPlatform(BaseActivity activity) {
        if (!activity.isNetworkConnected())
            return;

        this.activity = activity;

        getIsShowProgress().postValue(14);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_PLATFORM, null, false, this);
    }

    public void getConnectedPlatform(BaseActivity activity) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;

//        getIsShowProgress().postValue(15);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_CONNECTED_PLATFORM, null, false, this);
    }

    public void reOrderMedia(BaseActivity activity, JSONArray object) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;
//        getIsShowProgress().postValue(15);

        CommonRequest.ReOrderMedia reOrderMedia = new CommonRequest.ReOrderMedia();
        reOrderMedia.setReorder(object.toString());

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_RE_ORDER_MEDIA, reOrderMedia.toString(), true, this);
    }

    public void addSocialMediaRequest(BaseActivity activity, String website, String path, String name) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;
//        getIsShowProgress().postValue(15);

        File file = new File(path);

        MultipartBody.Part body = null;
        file = CompressFile.getCompressedImageFile(file);
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

        CommonRequest.AddWebsiteRequest reOrderMedia = new CommonRequest.AddWebsiteRequest();
        reOrderMedia.setLink(website);
        reOrderMedia.setPlatoform_name(name);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_SOCIAL_MEDIA_REQUEST, reOrderMedia.toString(), this, body);
    }

    @Override
    public void onResponseSuccess(String responseBody, String urlEndPoint, String msg) {
        if (urlEndPoint.equals(API_GET_PLATFORM)) {
            SocialMediaResponse gigCategories = SocialMediaResponse.getSocialPlatforms(responseBody);
            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
                getSocialMediaDataList().postValue(gigCategories.data);
//                Preferences.saveGigCategory(activity, gigCategories.data);
            }
            getIsHideProgress().postValue(14);
        } else if (urlEndPoint.equals(API_GET_CONNECTED_PLATFORM)) {
            ConnectedSocialMedia gigCategories = ConnectedSocialMedia.getSocialPlatforms(responseBody);
            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
                getConnectedMediaDataList().postValue(gigCategories.data);
//                Preferences.saveGigCategory(activity, gigCategories.data);
            }
            getIsHideProgress().postValue(15);
        } else if (urlEndPoint.equals(API_EDIT_PLATFORM) || urlEndPoint.equals(API_SAVE_PLATFORM)) {
            getConnectedPlatform(activity);
            getSaveCompanyProgress().postValue(11);
            activity.toastMessage(msg);
        } else if (urlEndPoint.equals(API_RE_ORDER_MEDIA)) {
            activity.toastMessage(msg);
        } else if (urlEndPoint.equals(API_DELETE_PLATFORM)) {
            getConnectedPlatform(activity);
            activity.toastMessage(msg);
        } else if (urlEndPoint.equals(API_SOCIAL_MEDIA_REQUEST)) {
//            activity.toastMessage(msg);
            getSaveCompanyProgress().postValue(11);
            thanksForPaymentDialog();
        }
        activity.isClickableView = false;
        activity.disableEnableTouch(false);
        getSaveCompanyProgress().postValue(0);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (urlEndPoint.equalsIgnoreCase(API_GET_CONNECTED_PLATFORM)) {
//            activity.toastMessage(message);
            getIsHideProgress().postValue(15);
            getConnectedMediaDataList().postValue(new ArrayList<>());
        } else if (urlEndPoint.equals(API_RE_ORDER_MEDIA)) {
            activity.toastMessage(message);
        } else if (urlEndPoint.equals(API_SOCIAL_MEDIA_REQUEST)) {
            activity.toastMessage(message);

        }
        getSaveCompanyProgress().postValue(0);
        activity.isClickableView = false;
        activity.disableEnableTouch(false);
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_GET_INF_PLATFORM)) {
            SocialPlatformResponse gigCategories = SocialPlatformResponse.getSocialPlatforms(responseBody);
            if (gigCategories != null && gigCategories.data != null && gigCategories.data.size() > 0) {
                getSocialDataList().postValue(gigCategories.data);
//                Preferences.saveGigCategory(activity, gigCategories.data);
            }
            getIsHideProgress().postValue(14);
        }
        activity.isClickableView = false;
        activity.disableEnableTouch(false);
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        if (url.equalsIgnoreCase(API_GET_INF_PLATFORM)) {
            getIsHideProgress().postValue(14);
        }
        activity.isClickableView = false;
        activity.disableEnableTouch(false);
    }

    private void thanksForPaymentDialog() {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_bank_transfer_done);
        dialog.setCancelable(true);

        TextView txtGoto = dialog.findViewById(R.id.txt_goto_wallet);

        txtGoto.setOnClickListener(v -> {
            dialog.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);

        dialog.setOnDismissListener(dialog1 -> {
            dialog.dismiss();
        });
    }

    public void deletePlatform(int pId) {
        CommonRequest.DeleteSocialMedia deleteSurveyImage = new CommonRequest.DeleteSocialMedia();
        deleteSurveyImage.setSocial_platform_id(pId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_DELETE_PLATFORM, deleteSurveyImage.toString(), true, this);
    }

    public void editPlatform(String username, String follower, int pId, int status, int publicStatus) {
        getSaveCompanyProgress().postValue(1);
        CommonRequest.UpdateSocialMedia deleteSurveyImage = new CommonRequest.UpdateSocialMedia();
        deleteSurveyImage.setSocial_platform_id(pId);
        deleteSurveyImage.setUsername(username);
        deleteSurveyImage.setPublic_status(publicStatus);
        if (!TextUtils.isEmpty(follower)) {
            deleteSurveyImage.setFollowers(Integer.parseInt(follower.replaceAll(",","")));
        }
        deleteSurveyImage.setIs_public(status);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_EDIT_PLATFORM, deleteSurveyImage.toString(), true, this);
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

}

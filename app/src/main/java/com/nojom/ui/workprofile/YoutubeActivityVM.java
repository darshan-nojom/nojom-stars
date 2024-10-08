package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_ADD_YOUTUBE;
import static com.nojom.util.Constants.API_DELETE_YOUTUBE;
import static com.nojom.util.Constants.API_GET_YOUTUBE_LIST;
import static com.nojom.util.Constants.API_REORDER_YOUTUBE;
import static com.nojom.util.Constants.API_UPDATE_YOUTUBE;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.GetYoutube;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import org.json.JSONArray;

public class YoutubeActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private final MutableLiveData<Integer> saveCompanyProgress = new MutableLiveData<>();
    public final MutableLiveData<Integer> updateCompany = new MutableLiveData<>();
    private final MutableLiveData<GetYoutube> getYoutubeList = new MutableLiveData<>();

    public MutableLiveData<Integer> getSaveCompanyProgress() {
        return saveCompanyProgress;
    }

    public MutableLiveData<GetYoutube> getYoutubeListData() {
        return getYoutubeList;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
        initData();
    }

    private void initData() {
        getYoutubeList();
    }

    void getYoutubeList() {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

//        CommonRequest.ExpertiseRequest expertiseRequest = new CommonRequest.ExpertiseRequest();
//        expertiseRequest.setExperience(experience);
//        expertiseRequest.setService_category_id(serviceIds);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_YOUTUBE_LIST, null, false, this);
    }

    public void addYoutube(String link) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);
        getSaveCompanyProgress().postValue(1);

        CommonRequest.AddYoutube expertiseRequest = new CommonRequest.AddYoutube();
        expertiseRequest.setLink(link);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_YOUTUBE, expertiseRequest.toString(), true, this);
    }

    public void updateYoutube(int publicStatus, int id, String link) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

        CommonRequest.UpdateYoutube expertiseRequest = new CommonRequest.UpdateYoutube();
        expertiseRequest.setId(id);
        expertiseRequest.setPublic_status(publicStatus);
        expertiseRequest.setLink(link);


        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_UPDATE_YOUTUBE, expertiseRequest.toString(), true, this);
    }

    public void deleteYoutube(int cId) {
        if (!activity.isNetworkConnected()) return;
        activity.disableEnableTouch(true);

        CommonRequest.DeleteCompany expertiseRequest = new CommonRequest.DeleteCompany();
        expertiseRequest.setId(cId);//agent company id

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_DELETE_YOUTUBE, expertiseRequest.toString(), true, this);
    }

    public void reOrderMedia(BaseActivity activity, JSONArray object) {
        if (!activity.isNetworkConnected())
            return;
        this.activity = activity;

        CommonRequest.ReOrderMedia reOrderMedia = new CommonRequest.ReOrderMedia();
        reOrderMedia.setReorder(object.toString());

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_REORDER_YOUTUBE, reOrderMedia.toString(), true, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {

        if (urlEndPoint.equals(API_GET_YOUTUBE_LIST)) {
            GetYoutube gigCategories = GetYoutube.getYoutubeList(decryptedData);
            if (gigCategories != null) {
                getYoutubeListData().postValue(gigCategories);
            }
        } else if (urlEndPoint.equals(API_ADD_YOUTUBE) || urlEndPoint.equals(API_UPDATE_YOUTUBE)) {
            getYoutubeList();
            updateCompany.postValue(11);
            getSaveCompanyProgress().postValue(0);
            getSaveCompanyProgress().postValue(11);
            activity.toastMessage(message);
        } else if (urlEndPoint.equals(API_DELETE_YOUTUBE)) {
            getYoutubeList();
            getSaveCompanyProgress().postValue(2);
            activity.toastMessage(message);
        } else if (urlEndPoint.equals(API_REORDER_YOUTUBE)) {
            activity.toastMessage(message);
        }
        activity.disableEnableTouch(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (urlEndPoint.equals(API_ADD_YOUTUBE) || urlEndPoint.equals(API_UPDATE_YOUTUBE)) {
            getSaveCompanyProgress().postValue(0);
        }
        activity.disableEnableTouch(false);
    }
}

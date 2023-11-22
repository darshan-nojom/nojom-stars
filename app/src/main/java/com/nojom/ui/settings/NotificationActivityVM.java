package com.nojom.ui.settings;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.Notification;
import com.nojom.model.Topic;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import static com.nojom.util.Constants.API_ADD_NOTIFICATION_SETTINGS;
import static com.nojom.util.Constants.API_GET_NOTIFICATION_SETTINGS;
import static com.nojom.util.Constants.API_TOPIC_STATUS;

public class NotificationActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<ArrayList<Notification>> listMutableLiveData;
    private List<Topic.Data> topicDataList;
    private MutableLiveData<List<Topic.Data>> mutableTopicData;
    private MutableLiveData<Boolean> isShow = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShow() {
        return isShow;
    }

    public MutableLiveData<List<Topic.Data>> getMutableTopicData() {
        if (mutableTopicData == null) {
            mutableTopicData = new MutableLiveData<>();
        }
        return mutableTopicData;
    }

    public MutableLiveData<ArrayList<Notification>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public NotificationActivityVM() {

    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }


    void getNotificationList() {
        if (!activity.isNetworkConnected())
            return;

        getIsShow().postValue(true);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GET_NOTIFICATION_SETTINGS, null, false, this);
    }

//    private void getSubscribeTopicList() {
//        if (!activity.isNetworkConnected())
//            return;
//
//        getIsShow().postValue(true);
//
//        CommonRequest.NotificationStatus notificationStatus = new CommonRequest.NotificationStatus();
//        notificationStatus.setToken(activity.getToken());
//
//        APIRequest apiRequest = new APIRequest();
//        apiRequest.makeAPIRequest(activity, API_TOPIC_STATUS, notificationStatus.toString(), true, this);
//    }


    void addNotification(int notificationId, int status) {
        if (!activity.isNetworkConnected())
            return;

        //getIsShow().postValue(true);

        CommonRequest.AddNotification addNotification = new CommonRequest.AddNotification();
        addNotification.setNotification_id(notificationId);
        addNotification.setStatus(status);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_NOTIFICATION_SETTINGS, addNotification.toString(), true, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        if (urlEndPoint.equalsIgnoreCase(API_GET_NOTIFICATION_SETTINGS)) {
            List<Notification> model = Notification.getNotifications(decryptedData);
            if (model != null) {
                ArrayList<Notification> notificationList = (ArrayList<Notification>) model;

                getListMutableLiveData().postValue(notificationList);
            }
            getIsShow().postValue(false);

//            getSubscribeTopicList();
        } else if (urlEndPoint.equalsIgnoreCase(API_ADD_NOTIFICATION_SETTINGS)) {
            getIsShow().postValue(false);
        } else if (urlEndPoint.equalsIgnoreCase(API_TOPIC_STATUS)) {
            List<Topic.Data> model = Topic.getNotificationTopic(decryptedData);
            topicDataList = new ArrayList<>();
            if (model != null) {
                topicDataList = model;
            }
            getMutableTopicData().postValue(topicDataList);
            getIsShow().postValue(false);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        //activity.failureError(activity.getString(R.string.something_went_wrong));
        getIsShow().postValue(false);
    }
}

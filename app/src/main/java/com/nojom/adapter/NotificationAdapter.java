package com.nojom.adapter;

import static com.nojom.util.Constants.API_SUBSCRIBE_TOPIC;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.nojom.api.APIRequest;
import com.nojom.databinding.ItemNotificationBinding;
import com.nojom.model.Topic;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.SimpleViewHolder> implements APIRequest.APIRequestListener {

    private Context context;
    private List<Topic.Data> notificationList;
    private BaseActivity activity;
    private int selectedStatus;
    private String selectedTopic;

    public NotificationAdapter(Context context) {
        this.context = context;
        activity = (BaseActivity) context;
    }

    public void doRefresh(List<Topic.Data> notificationList) {
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemNotificationBinding notificationBinding =
                ItemNotificationBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(notificationBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        Topic.Data item = notificationList.get(position);
        holder.binding.setNotifData(item);
        ((Activity) context).runOnUiThread(() -> holder.binding.segmentGroup.setPosition(item.status == 1 ? 1 : 0));

        holder.binding.segmentGroup.setOnPositionChangedListener(status -> addNotification(item.topicName, status));
    }

    private void addNotification(String notificationTopic, int status) {
        if (!activity.isNetworkConnected())
            return;

//        activity.showProgress();

        CommonRequest.SubUnbTopic subUnbTopic = new CommonRequest.SubUnbTopic();
        subUnbTopic.setTopic(notificationTopic);
        subUnbTopic.setTopic_status(status == 0 ? "TOPIC_UNSUBSCRIBE" : "TOPIC_SUBSCRIBE");
        selectedStatus = status;
        selectedTopic = notificationTopic;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_SUBSCRIBE_TOPIC, subUnbTopic.toString(), true, this);
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {

        if (urlEndPoint.equalsIgnoreCase(API_SUBSCRIBE_TOPIC)) {
            activity.toastMessage(msg);
        }

        if (selectedStatus == 0) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(selectedTopic);
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic(selectedTopic);
        }
//        activity.hideProgress();
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
//        activity.hideProgress();
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;

        SimpleViewHolder(ItemNotificationBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}

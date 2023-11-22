package com.nojom.fcm;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nojom.ui.startup.MainActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.intercom.android.sdk.push.IntercomPushClient;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private final IntercomPushClient intercomPushClient = new IntercomPushClient();

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        if (!TextUtils.isEmpty(token)) {
            Log.e("NEW_TOKEN", token);
            intercomPushClient.sendTokenToIntercom(getApplication(), token);
            Preferences.writeString(this, Constants.FCM_TOKEN, token);

            Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
            registrationComplete.putExtra("token", token);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        /*  if (Preferences.readBoolean(MyFirebaseMessagingService.this, Constants.ALL_NOTIFICATION, false))
            return;*/

        if (remoteMessage.getNotification() != null) {
            Utils.trackFirebaseEvent(this, "notification_receive");

            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
//            handleNotification(remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            if (intercomPushClient.isIntercomPush(remoteMessage.getData())) {
                intercomPushClient.handlePush(getApplication(), remoteMessage.getData());
            } else {
                try {
                    Map<String, String> params = remoteMessage.getData();
                    JSONObject json = new JSONObject(params);
                    handleDataMessage(json);
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }
            }
        }
    }

//    private void handleNotification(String message) {
//        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//            //notificationUtils.playNotificationSound();
//        }  // If the app is in background, firebase itself handles the notification
//
//    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

            String title = json.getString("title");
            String message = json.getString("body");
            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);

            String chatId;
            if (json.has("chatId")) {
                chatId = json.getString("chatId");
                Log.e(TAG, "chatId: " + chatId);
                if (!TextUtils.isEmpty(chatId)) {
                    // Check if user present in same chat screen or not
                    // If present then notification is not send otherwise notify user
                    String currentChatId = Preferences.readString(getApplicationContext(), Constants.CHAT_OPEN_ID, "");
                    if (!currentChatId.equals(chatId)) {
                        boolean isMultipleMessages = false;
                        String lastTitle = null;
                        for (CharSequence cs : NotificationUtils.issuedMessages) {
                            if (!TextUtils.isEmpty(lastTitle)) {
                                if (!cs.toString().equals(lastTitle)) {
                                    isMultipleMessages = true;
                                    break;
                                }
                            }
                            lastTitle = title;
                        }
                        resultIntent.putExtra(Constants.CHAT_ID, isMultipleMessages ? "" : chatId);
                    }
                }
            }

            if (json.has("additionalData")) {
                String addData = json.getString("additionalData");
                JSONObject additionalData = new JSONObject(addData);
                if (additionalData.has(Constants.M_TYPE)) {
                    String mType = additionalData.getString(Constants.M_TYPE);
                    resultIntent.putExtra(Constants.M_TYPE, mType);
                }
                if (additionalData.has(Constants.M_PROJECTID)) {
                    String projectId = additionalData.getString(Constants.M_PROJECTID);
                    resultIntent.putExtra(Constants.M_PROJECTID, projectId);
                }

                /*Required following data in case of Chat*/
                if (additionalData.has("id")) {
                    int clientId = additionalData.getInt("id");
                    resultIntent.putExtra("id", clientId);
                }
                if (additionalData.has("username")) {
                    String clientUsername = additionalData.getString("username");
                    resultIntent.putExtra("username", clientUsername);
                }
                if (additionalData.has("profile_pic")) {
                    String clientProfilePic = additionalData.getString("profile_pic");
                    resultIntent.putExtra("profile_pic", clientProfilePic);
                }
                if (additionalData.has("path")) {
                    String path = additionalData.getString("path");
                    resultIntent.putExtra("path", path);
                }
            }

            showNotificationMessage(getApplicationContext(), title, message, String.valueOf(System.currentTimeMillis()), resultIntent);
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }
}

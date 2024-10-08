package com.nojom.fcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.nojom.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;
    static List<CharSequence> issuedMessages = new LinkedList<>();

    NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {

        int notificationIcon = R.mipmap.ic_launcher_round;
        int icon = R.mipmap.ic_launcher;

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
//        String GROUP_KEY_MESSAGE = "com.24task.MESSAGE";
//        int bundleNotificationId = 1000;
        issuedMessages.add(title + ": " + message);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(mContext.getString(R.string.nojom));
        for (CharSequence cs : issuedMessages) {
            inboxStyle.addLine(cs);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);

        String channelId = mContext.getString(R.string.notification_channel_id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = Objects.requireNonNull(notificationManager).getNotificationChannel(channelId);
            if (mChannel == null) {
                mChannel = new NotificationChannel(channelId, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, channelId);

        notification = mBuilder.setTicker(title)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setChannelId(channelId)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setColor(ContextCompat.getColor(mContext, R.color.white))
                .setSmallIcon(notificationIcon)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                //.setGroup(GROUP_KEY_MESSAGE)
                //.setGroupSummary(true)
                .setVibrate(new long[]{100, 200, 300, 400})
                .build();

        if (notificationManager != null) {
            notificationManager.notify(notificationID(), notification);
        }
    }

    private int notificationID() {
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(new Date()));
    }

    static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = null;
        if (am != null) {
            runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        }

        return isInBackground;
    }

    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

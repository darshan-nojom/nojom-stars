package com.nojom.ui.startup;

import static com.nojom.util.Constants.API_GET_GIG_LANGUAGE;
import static com.nojom.util.Constants.KEY_LIVE_BASE_URL;
import static com.nojom.util.Constants.KEY_LIVE_BASE_URL_GIG;
import static com.nojom.Task24Application.BASE_URL_GIG;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.nojom.BuildConfig;
import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.api.APIRequest;
import com.nojom.api.ApiClient;
import com.nojom.apis.GetGigPackages;
import com.nojom.interfaces.NetworkListener;
import com.nojom.model.Language;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

public class SplashActivityVM extends ViewModel implements APIRequest.JWTRequestResponseListener, NetworkListener {

    private static final String KEY_FORCE_UPDATE_REQUIRED = "agent_force_update";
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private String latestPlaystoreVersion = "";

    public void init(BaseActivity activity) {
        this.activity = activity;
        activity.setNetworkListener(this);

        activity.runOnUiThread(() -> {
            if (activity.isLogin()) {
                getGigLanguage();
                GetGigPackages gigPackages = new GetGigPackages();
                gigPackages.init(activity);
                gigPackages.getGigPackages();
                gigPackages.getStndrdServiceCategories();
                activity.getProfile();
            }
        });

        /*VersionChecker versionChecker = new VersionChecker();
        try {
            latestPlaystoreVersion = versionChecker.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/
    }

    void checkForNewVersion() {

        int code = BuildConfig.VERSION_CODE;
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(activity);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();


        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(activity, task -> {
                    if (!task.isSuccessful()) {
                        redirectIntent();
                        return;
                    }

                    appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {

                        if (!BuildConfig.DEBUG) {
                            String liveBaseUrl = remoteConfig.getString(KEY_LIVE_BASE_URL);
                            if (!TextUtils.isEmpty(liveBaseUrl)) {
                                Task24Application.LIVE_URL = liveBaseUrl;
                            }
                            Log.e("Remote LIVE BASE ", "" + Task24Application.LIVE_URL);
                            ApiClient.retrofit = null;
                        }

                        if (!BuildConfig.DEBUG) {
                            String liveBaseUrl = remoteConfig.getString(KEY_LIVE_BASE_URL_GIG);
                            Log.e("Remote LIVE BASE GIG ", "" + liveBaseUrl);
                            BASE_URL_GIG = liveBaseUrl;
                            ApiClient.retrofitGig = null;
                        }


                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            // This example applies an immediate update. To apply a flexible update
                            // instead, pass in AppUpdateType.FLEXIBLE
                            /*&& appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)*/) {
                            // Request the update.
                            if (!activity.isFinishing()) {
                                showUpdateDialog(remoteConfig.getBoolean(KEY_FORCE_UPDATE_REQUIRED));
                            }
                        } else {
                            redirectIntent();
                        }
                    });

                    appUpdateInfoTask.addOnFailureListener(fail -> {
                        redirectIntent();
                    });

                    /*String appVersion = BuildConfig.VERSION_NAME;

                    if (remoteConfig.getBoolean(KEY_FORCE_UPDATE_REQUIRED)) {//force update
                        if (!TextUtils.isEmpty(latestPlaystoreVersion) && !TextUtils.equals(latestPlaystoreVersion, appVersion)) {
                            showUpdateDialog(true);
                        } else {
                            redirectIntent();
                        }
                    } else {//check if version not match than open simple dialog
                        if (!TextUtils.isEmpty(latestPlaystoreVersion) && !TextUtils.equals(latestPlaystoreVersion, appVersion)) {//version not equal so open alert dialog
                            showUpdateDialog(false);
                        } else {
                            redirectIntent();
                        }
                    }*/
                });
    }

    private void showUpdateDialog(boolean isForceUpdate) {
        String title = activity.getString(R.string.a_new_version_available);
        String message = activity.getString(R.string.a_new_version_of_24task_Freelancer_is_available_Please_update_app_new_version_to_continue);
        String redirectUrl = "https://play.google.com/store/apps/details?id=com.task24.android.apps.freelancer.job.search&hl=en";
        try {
            AlertDialog dialog;
            if (isForceUpdate) {
                dialog = new AlertDialog.Builder(activity)
                        .setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(activity.getString(R.string.update),
                                (dialog1, which) -> {
                                    redirectStore(redirectUrl);
                                    dialog1.dismiss();
                                })
                        .setNegativeButton(activity.getString(R.string.exit),
                                (dialog12, which) -> activity.finish()).create();
            } else {
                dialog = new AlertDialog.Builder(activity)
                        .setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(activity.getString(R.string.update),
                                (dialog13, which) -> {
                                    redirectStore(redirectUrl);
                                    dialog13.dismiss();
                                })
                        .setNegativeButton(activity.getString(R.string.cancel),
                                (dialog14, which) -> {
                                    redirectIntent();
                                    dialog14.dismiss();
                                }).create();
            }
            dialog.show();

        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    private void redirectStore(String updateUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    private void redirectIntent() {
        if (activity.isNetworkConnected()) {
            new Handler().postDelayed(() -> redirectToScreen(), 4000);
        }
    }

    private void redirectToScreen() {
        Intent intentMain;
        if (activity.isLogin()) {
            intentMain = new Intent(activity, MainActivity.class);

            if (activity.getIntent().getExtras() != null && !activity.getIntent().getExtras().isEmpty()) {
                for (String key : activity.getIntent().getExtras().keySet()) {
                    Object value = activity.getIntent().getExtras().get(key);
                    Log.e("KEY VALUE ", "" + key + " ----------- " + value);
                    if (key.equalsIgnoreCase("additionalData")) {
                        //                                JSONObject jsonData = new JSONObject();
//                                JSONObject jsonValue = new JSONObject(value.toString());
//                                jsonData.put("additionalData", jsonValue);
                        intentMain.putExtra("notifData", value.toString());
                        Log.e("NOTIF JSON ", "" + value.toString());
                    }
                }
            }

        } else {
            intentMain = new Intent(activity, SelectAccountActivity.class);
        }
        intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intentMain);
        activity.openToLeft();

        activity.finish();
    }

    @Override
    public void networkConnected() {
        redirectToScreen();
    }

    public void getGigLanguage() {
        if (!activity.isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestJWT(this, activity, API_GET_GIG_LANGUAGE, false, null);
    }

    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_GET_GIG_LANGUAGE)) {
            Language languageList = Language.getLanguages(responseBody);
            if (languageList != null && languageList.data != null && languageList.data.size() > 0) {
                Preferences.saveGigLanguage(activity, languageList.data);
            }
        }
//        getIsHideProgress().postValue(2);
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
//        if (url.equalsIgnoreCase(API_GET_GIG_PACKAGE)) {
//            getIsHideProgress().postValue(1);
//        }
    }
}

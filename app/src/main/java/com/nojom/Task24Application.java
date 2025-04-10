package com.nojom;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.util.Base64;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Foreground;
import com.nojom.util.Preferences;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.util.Objects;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;
import io.socket.client.IO;
import io.socket.client.Socket;

public class Task24Application extends MultiDexApplication implements Constants {

    private BaseActivity activity;

    public BaseActivity getBaseActivity() {
        return activity;
    }

    private static Task24Application sInstance;

    public static Task24Application getActivity() {
        return sInstance;
    }

    public void setActivity(Task24Application activity) {
        sInstance = activity;
    }

    //appsflyer key
    private static final String AF_DEV_KEY = "FAF3TL8KbWZ79yZFeR84WU";
    private FirebaseAnalytics mFirebaseAnalytics;

    public FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

    public static String LIVE_URL = "https://agent-prod-api.nojom.com/";
    public static String BASE_URL_GIG = "https://zap31dqqtk.execute-api.me-central-1.amazonaws.com/prod/";
//    public WebSocketClient clientNew;

    private Socket mSocketMsg;

    {
        try {
            mSocketMsg = IO.socket(Constants.BASE_URL_CHAT_MSG);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocketMsg() {
        return mSocketMsg;
    }

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    @Override
    public void onCreate() {
        super.onCreate();
        setActivity(this);
        activity = new BaseActivity();
        Foreground.init(this);
//        new Thread(() -> {
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
        } catch (GooglePlayServicesRepairableException |
                 GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        if (!AppCenter.isConfigured()) {
            AppCenter.start(this, "959ada3b-7e59-47d2-a255-ecaba6cae3a5", Analytics.class, Crashes.class);
        }

        Intercom.initialize(this, "android_sdk-1c34857d121a5aca7c6b046b3ffe9ccc6f8605fa", "cmde9nds");
        Registration registration = Registration.create();
        if (Preferences.getUserData(this) != null) {
            registration.withEmail(Objects.requireNonNull(Preferences.getUserData(this)).email);
            registration.withUserId(String.valueOf(Objects.requireNonNull(Preferences.getUserData(this)).id));
            Intercom.client().registerIdentifiedUser(registration);
        } else {
            Intercom.client().registerUnidentifiedUser();
        }
//        }).start();

        // Obtain the FirebaseAnalytics instance.
        FirebaseApp.initializeApp(this);
//        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
//        firebaseAppCheck.installAppCheckProviderFactory(
//                PlayIntegrityAppCheckProviderFactory.getInstance());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Setting up remote configs
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        Preferences.writeString(this, SERVICE_NAME, "");
//
//        UXConfig config = new UXConfig.Builder("5kw4vn82gx74uv4")
//                .enableAutomaticScreenNameTagging(true)
//                .enableImprovedScreenCapture(true)
//                .build();
//        UXCam.startWithConfiguration(config);

        printHashKey();
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

//        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
//            @Override
//            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
//
//            }
//
//            @Override
//            public void onActivityStarted(@NonNull Activity activity1) {
//
//                if (++activityReferences == 1 && !isActivityChangingConfigurations) {
//                    // App enters foreground
//                    Log.e("onActivityStarted","foreground");
//                    if(clientNew!=null && clientNew.isConnected()){
//                        boolean isLogin= Preferences.readBoolean(getApplicationContext(), Constants.IS_LOGIN, false);
//                        if(isLogin) {
//                        onVerifyUser();
//                    }
//                    }
//                }
//            }
//
//            @Override
//            public void onActivityResumed(@NonNull Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityPaused(@NonNull Activity activity) {
//
//            }
//
//            @Override
//            public void onActivityStopped(@NonNull Activity activity1) {
//
//                isActivityChangingConfigurations = activity.isChangingConfigurations();
//                if (--activityReferences == 0 && !isActivityChangingConfigurations) {
//                    // App enters background
//                    Log.e("onActivityStopped","background");
//                    if(clientNew!=null && clientNew.isConnected()){
//                        boolean isLogin= Preferences.readBoolean(getApplicationContext(), Constants.IS_LOGIN, false);
//                    if(isLogin) {
//                        disconnect();
//                    }
//                }
//                }
//            }
//
//            @Override
//            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
//
//            }
//
//            @Override
//            public void onActivityDestroyed(@NonNull Activity activity) {
//
//            }
//        });
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null;
        // Toast.makeText(this, "connect to internet", Toast.LENGTH_SHORT).show();
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash Key: ", hashKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("Hash Key: ", e.getMessage());
        }
    }

//    public void disconnect() {
//        JSONObject mainJsonData = new JSONObject();
//        JSONObject jsonObject = new JSONObject();
//        try {
//                UserModel userData = Preferences.getUserData(this);
//                if(userData!=null) {
//                    jsonObject.put("profile_id", userData.id);
//                    jsonObject.put("profile_type_id", AGENT_PROFILE);
//                    mainJsonData.put("data", jsonObject);
//                    mainJsonData.put("action", "disconnect");
//                    Task24Application.getActivity().clientNew.send(mainJsonData.toString());
//                }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void onVerifyUser() {
//        JSONObject mainJsonData = new JSONObject();
//        JSONObject jsonObject = new JSONObject();
//        try {
//            UserModel userData = Preferences.getUserData(this);
//            if(userData!=null) {
//                jsonObject.put("profile_id", userData.id);
//                mainJsonData.put("data", jsonObject);
//                mainJsonData.put("action", "verifyuser");
//                Task24Application.getActivity().clientNew.send(mainJsonData.toString());
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
}

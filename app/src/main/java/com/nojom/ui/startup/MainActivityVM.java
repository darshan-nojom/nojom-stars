package com.nojom.ui.startup;

import static com.nojom.util.Constants.API_SET_COORDINATE;
import static com.nojom.util.Constants.TAB_HOME;
import static com.nojom.util.Constants.TAB_JOB_LIST;
import static com.nojom.util.Constants.TAB_PROFILE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TabHost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.R;
import com.nojom.api.ApiClient;
import com.nojom.api.ApiInterface;
import com.nojom.databinding.ActivityMainBinding;
import com.nojom.model.APIResponse;
import com.nojom.model.GeneralModel;
import com.nojom.model.ProfileResponse;
import com.nojom.model.UserModel;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.multitypepicker.FilePath;
import com.nojom.ui.auth.LoginSignUpActivity;
import com.nojom.ui.chat.ChatActivity;
import com.nojom.ui.chat.ChatMessagesActivity;
import com.nojom.ui.gigs.UserAccountActivity;
import com.nojom.ui.home.WorkHomeActivity;
import com.nojom.ui.projects.MyProjectsActivity;
import com.nojom.ui.workprofile.WorkMoreActivity;
import com.nojom.util.AESHelper;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityVM extends AndroidViewModel implements TabHost.OnTabChangeListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private ActivityMainBinding binding;
    @SuppressLint("StaticFieldLeak")
    private MainActivity activity;
    private int SCREEN_TAB = -1;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private double latitude;
    private double longitude;

    MainActivityVM(Application application, ActivityMainBinding mainBinding, MainActivity mainActivity) {
        super(application);
        binding = mainBinding;
        activity = mainActivity;
        initData();
    }

    private void initData() {

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        int screen = 0;
        if (activity.getIntent() != null) {
            //this screen tab is used when user going for bidding and have to redirect at 'Bidding' TAB
            if (activity.getIntent().hasExtra(Constants.SCREEN_TAB)) {
                SCREEN_TAB = activity.getIntent().getIntExtra(Constants.SCREEN_TAB, -1);
            }

            //tab selection based on this flag
            if (activity.getIntent().hasExtra("shortcut")) {//when redirect from app shortcut(i.e Long Click on app icon from launcher)
                String shortcutTab = activity.getIntent().getStringExtra("shortcut");
                if (shortcutTab != null && shortcutTab.equalsIgnoreCase("my_jobs")) {
                    screen = TAB_JOB_LIST;
                } else if (shortcutTab != null && shortcutTab.equalsIgnoreCase("my_profile")) {
                    screen = TAB_PROFILE;
                }
            } else if (activity.getIntent().hasExtra(Constants.SCREEN_NAME)) {
                screen = activity.getIntent().getIntExtra(Constants.SCREEN_NAME, 0);
            }
        }

        setTab("home", R.drawable.tab_home, WorkHomeActivity.class);
        setTab("chat", R.drawable.tab_chat, ChatActivity.class);
        setTab("plus", R.drawable.tab_profile, UserAccountActivity.class/* : Pro24TaskActivity.class*/);
        setTab("project", R.drawable.tab_project, MyProjectsActivity.class);
        setTab("profile", R.drawable.tab_more, WorkMoreActivity.class);

        binding.tabhost.setOnTabChangedListener(this);

        Log.e("USERID ", "LONG ");

        if (activity.getIntent() != null) {
            binding.tabhost.setCurrentTab(screen);
            checkForIntentData(activity.getIntent());
        }

        //get current location and update it to server
        if (mGoogleApiClient.isConnected())
            getLocation();
        else
            mGoogleApiClient.connect();
    }

    void checkForIntentData(Intent intent) {
        if (intent.hasExtra(Constants.CHAT_ID)) {
            binding.tabhost.setCurrentTab(Constants.TAB_CHAT);

            HashMap<String, String> chatMap = new HashMap<>();
            if (intent.hasExtra("id")) {
                chatMap.put(Constants.RECEIVER_ID, "" + intent.getIntExtra("id", 0));
            }

            if (intent.hasExtra("username")) {
                chatMap.put(Constants.RECEIVER_NAME, "" + intent.getStringExtra("username"));
            }

            if (intent.hasExtra("profile_pic") && !TextUtils.isEmpty(intent.getStringExtra("profile_pic"))) {
                chatMap.put(Constants.RECEIVER_PIC, intent.getStringExtra("path") + File.separator + intent.getStringExtra("profile_pic"));
            } else {
                chatMap.put(Constants.RECEIVER_PIC, "");
            }

            chatMap.put(Constants.SENDER_ID, getProfileData().id + "");
            chatMap.put(Constants.SENDER_NAME, getProfileData().username);
            chatMap.put(Constants.SENDER_PIC, getImageUrl() + getProfileData().profilePic);
            chatMap.put("isProject", "1");//1 mean updated record

            Intent i;
            i = new Intent(activity, ChatMessagesActivity.class);
            i.putExtra(Constants.CHAT_DATA, chatMap);
            activity.startActivity(i);
        } else if (intent.hasExtra(Constants.M_TYPE)) {
            String mType = intent.getStringExtra(Constants.M_TYPE);
            String projectId = intent.getStringExtra(Constants.M_PROJECTID);
            if (projectId != null)
                Preferences.writeString(activity, Constants.PROJECT_ID, projectId);

            if (mType != null) {
                switch (mType) {
                    case "Client pay":
                    case "Contract canceled":
                    case "Bid accepted":
                    case "Project closed":
                        binding.tabhost.setCurrentTab(TAB_JOB_LIST);
                        break;
                    case "New Job Posts":
                    default:
                        binding.tabhost.setCurrentTab(TAB_HOME);
                        break;
                }
            }
        } /*else if (intent.hasExtra("notifData")) {//when app is killed
            try {
                binding.tabhost.setCurrentTab(Constants.TAB_CHAT);
                JSONObject object = new JSONObject(intent.getStringExtra("notifData"));

                HashMap<String, String> chatMap = new HashMap<>();
                if (object.has("id")) {
                    chatMap.put(Constants.RECEIVER_ID, "" + object.getInt("id"));
                }

                if (object.has("username")) {
                    chatMap.put(Constants.RECEIVER_NAME, "" + object.getString("username"));
                }

                if (object.has("profile_pic") && !TextUtils.isEmpty(object.getString("profile_pic"))) {
                    chatMap.put(Constants.RECEIVER_PIC, object.getString("path") + File.separator + object.getString("profile_pic"));
                } else {
                    chatMap.put(Constants.RECEIVER_PIC, "");
                }

                chatMap.put(Constants.SENDER_ID, activity.getUserID() + "");
                chatMap.put(Constants.SENDER_NAME, getProfileData().username);
                chatMap.put(Constants.SENDER_PIC, getImageUrl() + getProfileData().profilePic);
                chatMap.put("isProject", "1");//1 mean updated record

                Intent i = new Intent(activity, ChatMessagesActivity.class);
                i.putExtra(Constants.CHAT_DATA, chatMap);
                activity.startActivity(i);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/


//                HashMap<String, String> chatMap = new HashMap<>();
//                if (intent.hasExtra("id")) {
//                    chatMap.put(Constants.RECEIVER_ID, "" + intent.getIntExtra("id", 0));
//                }
//
//                if (intent.hasExtra("username")) {
//                    chatMap.put(Constants.RECEIVER_NAME, "" + intent.getStringExtra("username"));
//                }
//
//                if (intent.hasExtra("profile_pic") && !TextUtils.isEmpty(intent.getStringExtra("profile_pic"))) {
//                    chatMap.put(Constants.RECEIVER_PIC, intent.getStringExtra("path") + File.separator + intent.getStringExtra("profile_pic"));
//                } else {
//                    chatMap.put(Constants.RECEIVER_PIC, "");
//                }
//
//                chatMap.put(Constants.SENDER_ID, activity.getUserID() + "");
//                chatMap.put(Constants.SENDER_NAME, getProfileData().username);
//                chatMap.put(Constants.SENDER_PIC, getImageUrl() + getProfileData().profilePic);
//                chatMap.put("isProject", "1");//1 mean updated record
//
//                Intent i = new Intent(activity, ChatMessagesActivity.class);
//                i.putExtra(Constants.CHAT_DATA, chatMap);
//                activity.startActivity(i);
//            }
//        }
    }

    private void setTab(String tag, int drawable, Class<?> activityClass) {
        Intent intent = new Intent(activity, activityClass);
        if (SCREEN_TAB != -1) {//the flag is used for Bidding TAB [After place bid have to redirect at Bidding TAB]
            intent.putExtra(Constants.SCREEN_TAB, SCREEN_TAB);
        }
        TabHost.TabSpec tabSpec = binding.tabhost.newTabSpec(tag)
                .setIndicator("", ContextCompat.getDrawable(activity, drawable))
                .setContent(intent);
        binding.tabhost.addTab(tabSpec);
    }

    @Override
    public void onTabChanged(String tabId) {

    }

    public void gotoMainActivity(int screen) {
        Intent i = new Intent(activity, MainActivity.class);
        i.putExtra(Constants.SCREEN_NAME, screen);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();
        activity.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    void clearTopActivity(Class<?> activityClass) {
        Intent i = new Intent(activity, activityClass);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();
        activity.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    public void redirectActivity(Class<?> activityClass) {
        activity.startActivity(new Intent(activity, activityClass));
        activity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    void goToLoginSignup() {
        Intent i = new Intent(activity, LoginSignUpActivity.class);
        i.putExtra(Constants.FROM_LOGIN, true);
        i.putExtra(Constants.LOGIN_FINISH, true);
        activity.startActivity(i);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    public String getJWT() {
        return Preferences.readString(activity, Constants.JWT, "");
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null;
    }

    public boolean checkStatus(GeneralModel model) {
        if (model == null)
            return false;

        if (model.success != null) {
            if ("1".equals(model.success)) {
                return true;
            }
        } else if (model.flag == 1) {
            return true;
        } else if (model.flag == 0) {
            if (model.msg != null && model.msg.equalsIgnoreCase("Expired Token.")) {
                Preferences.clearPreferences(activity);
                Preferences.saveUserData(activity, null);
                Preferences.setProfileData(activity, null);
                goToLoginSignup();
                return false;
            }
        }
        failureError(model.msg);
        return false;
    }

    public void failureError(String message) {
        if (!TextUtils.isEmpty(message))
            Utils.toastMessage(activity, message);
    }

    public String getUserID() {
        UserModel userData = Preferences.getUserData(activity);
        return String.valueOf(userData != null ? userData.id : 0);
    }

    public UserModel getUserData() {
        return Preferences.getUserData(activity);
    }

    public ProfileResponse getProfileData() {
        return Preferences.getProfileData(activity);
    }

    public String getClientAttachmentUrl() {
        if (TextUtils.isEmpty(FilePath.URL_C_ATTACHMENTS)) {
            ProfileResponse profileResponse = getProfileData();
            if (profileResponse != null && profileResponse.filePaths != null && !TextUtils.isEmpty(profileResponse.filePaths.clientAttachments)) {
                FilePath.URL_C_ATTACHMENTS = profileResponse.filePaths.clientAttachments;
                return profileResponse.filePaths.clientAttachments;
            }
        }
        return FilePath.URL_C_ATTACHMENTS;
    }

    public String getImageUrl() {
        if (TextUtils.isEmpty(FilePath.URL_IMAGE)) {
            ProfileResponse profileResponse = getProfileData();
            if (profileResponse != null && profileResponse.filePaths != null && !TextUtils.isEmpty(profileResponse.filePaths.img)) {
                FilePath.URL_IMAGE = profileResponse.filePaths.img;
                return profileResponse.filePaths.img;
            }
        }
        return FilePath.URL_IMAGE;
    }

    private void setCoordinatesAPI() {
        if (!activity.isNetworkConnected())
            return;

        CommonRequest.SetCoordinates setCoordinates = new CommonRequest.SetCoordinates();
        setCoordinates.setLatitude(latitude);
        setCoordinates.setLongitude(longitude);

        String encryptedData = "";
        try {
            encryptedData = AESHelper.encrypt(setCoordinates.toString());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(encryptedData)) {
            activity.failureError(activity.getString(R.string.invalid_request));
            return;
        }
        RequestBody body = RequestBody.create(encryptedData, MultipartBody.FORM);

        ApiInterface service = ApiClient.getClient().create(ApiInterface.class);

        Call<APIResponse> call = service.requestAPIHeader("6", activity.getJWT(), API_SET_COORDINATE, body);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {

            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.add_coordinates_failed));
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        settingRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Utils.toastMessage(activity, activity.getString(R.string.connection_suspended));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Utils.toastMessage(activity, activity.getString(R.string.connection_failed));
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(activity, 90000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Current Location", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private void settingRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);    // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1000);   // 1 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    getLocation();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(activity, 1000);
                    } catch (IntentSender.SendIntentException ignored) {
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
    }

    private void getLocation() {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            try {
                                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                                latitude = mLastLocation.getLatitude();
                                longitude = mLastLocation.getLongitude();
                                new Thread(() -> setCoordinatesAPI()).start();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Utils.toastMessage(activity, activity.getString(R.string.please_give_permission));
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }
}

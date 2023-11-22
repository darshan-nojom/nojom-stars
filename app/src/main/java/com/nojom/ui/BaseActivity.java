package com.nojom.ui;

import static androidx.core.content.FileProvider.getUriForFile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nojom.BuildConfig;
import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.api.APIRequest;
import com.nojom.api.ApiClient;
import com.nojom.api.ApiInterface;
import com.nojom.fragment.BaseFragment;
import com.nojom.interfaces.NetworkListener;
import com.nojom.model.ChatList;
import com.nojom.model.GeneralModel;
import com.nojom.model.ProfileResponse;
import com.nojom.model.UserModel;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.multitypepicker.FilePath;
import com.nojom.ui.auth.LoginSignUpActivity;
import com.nojom.ui.chat.chatInterface.ChatInterface;
import com.nojom.ui.chat.chatInterface.NewMessageForList;
import com.nojom.ui.chat.chatInterface.OnlineOfflineListener;
import com.nojom.ui.startup.MainActivity;
import com.nojom.ui.startup.SelectAccountActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.intercom.android.sdk.Intercom;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class BaseActivity extends AppCompatActivity implements APIRequest.APIRequestListener, Constants {

    private OnProfileLoadListener onProfileLoadListener;
    private AlertDialog alertDialog;
    LayoutInflater inflater;
    public boolean isClickableView;
    private NetworkListener networkListener;
    public Socket mSocket;
    public String language = "";


    public void setNetworkListener(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language = Preferences.readString(this, PREF_SELECTED_LANGUAGE, "en");
        if (language.equals("ar"))
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        else
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        loadAppLanguage();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        inflater = getLayoutInflater();
        if (isLogin()) {
            connectSocket(this);
        }
    }

    public void loadAppLanguage() {
        String language = Preferences.readString(this, Constants.PREF_SELECTED_LANGUAGE, "");
        if (!TextUtils.isEmpty(language)) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Resources res = getResources();
            DisplayMetrics dm = getBaseContext().getResources().getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.setLocale(locale); // API 17+ only.
            switch (language) {
                case "ar":
                case "es":
                    conf.setLayoutDirection(locale);
                    break;
                case "en":
                    conf.locale = Locale.ENGLISH;
                    conf.setLayoutDirection(new Locale("en"));
                    break;
            }
            res.updateConfiguration(conf, dm);
        }
    }

    public void setWindowFlag(BaseActivity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public ApiInterface getService() {
        return ApiClient.getClient().create(ApiInterface.class);
    }

    public ApiInterface getGigService() {
        return ApiClient.getGigClient().create(ApiInterface.class);
    }

    public void failureError(String message) {
        if (!isEmpty(message))
            toastMessage(message);
    }

    public void validationError(String message) {
        toastMessage(message);
    }

    public boolean isValidMobileWholeContent(String phone) {
        //Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");
        Pattern p = Pattern.compile("(([+][(]?[0-9]{1,3}[)]?)|([(]?[0-9]{4}[)]?))\\s*[)]?[-\\s.]?[(]?[0-9]{1,3}[)]?([-\\s.]?[0-9]{3})([-\\s.]?[0-9]{3,4})");
        // Pattern p = Pattern.compile("^\\+(?:[0-9] ?){6,14}[0-9]$");
        Matcher m = p.matcher(phone);

        while (m.find()) {
            return true;
        }
        return false;
    }

    public void openClientAppOnPlaystore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(
                "https://play.google.com/store/apps/details?id=com.nojom.client&hl=en_IN"));
//        intent.setPackage("com.android.vending");
        startActivity(intent);
    }

    public boolean isValidMail(String email) {
        Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(email);
        while (m.find()) {
            return true;
        }
        return false;

    }

    public void addFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .disallowAddToBackStack()
                .commit();
    }

    public void replaceFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
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
                Preferences.clearPreferences(this);
                Preferences.saveUserData(this, null);
                Preferences.setProfileData(this, null);
                goToLoginSignup(true, true);
                return false;
            }
        }
        failureError(model.msg);
        return false;
    }

    public boolean checkStatus(String response) {
        if (isEmpty(response))
            return false;

        try {
            JSONObject jsonObject = new JSONObject(response);
            String msg = null;
            if (jsonObject.has("msg")) {
                msg = jsonObject.getString("msg");
            }
            if (jsonObject.has("success")) {
                String success = jsonObject.getString("success");
                if (!isEmpty(success)) {
                    switch (success) {
                        case "0":
                            if (msg != null && !isEmpty(msg)) {
                                failureError(msg);
                            }
                            return false;
                        case "1":
                        case "1.0":
                            return true;
                    }
                }
            } else if (jsonObject.has("flag")) {
                String flag = jsonObject.getString("flag");
                if (flag.equals("1") || flag.equals("1.0")) {
                    return true;
                } else if (flag.equals("0") || flag.equals("0.0")) {
                    if (msg != null && msg.equalsIgnoreCase("Expired Token.")) {
                        Preferences.clearPreferences(this);
                        Preferences.saveUserData(this, null);
                        Preferences.setProfileData(this, null);
                        goToLoginSignup(true, true);
                        return false;
                    }
                }
            }
            failureError(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getProfileTypeId() {
        return Constants.AGENT_PROFILE;
    }

    public String get2DecimalPlaces(Object o) {
        if (o instanceof Double)
            return Utils.numberFormat((double) o, 2);
        else if (o instanceof Integer)
            return Utils.numberFormat((int) o, 2);
        else if (o instanceof Float)
            return Utils.numberFormat((float) o, 2);

        return Utils.numberFormat((String) o, 2);
    }

//    public String get1DecimalPlaces(Object o) {
//        if (o instanceof Double)
//            return Utils.numberFormat((double) o, 1);
//        else if (o instanceof Integer)
//            return Utils.numberFormat((int) o, 1);
//        else if (o instanceof Float)
//            return Utils.numberFormat((float) o, 1);
//
//        return Utils.numberFormat((String) o, 1);
//    }

    public boolean isLogin() {
        return Preferences.readBoolean(this, Constants.IS_LOGIN, false);
    }

    public String getToken() {
        return Preferences.readString(this, Constants.FCM_TOKEN, "");
    }

    public String getJWT() {
        try {
            return Preferences.readString(this, Constants.JWT, "");
        } catch (Exception e) {
            return null;
        }
    }

    public int getUserID() {
        UserModel userData = Preferences.getUserData(this);
        return userData != null ? userData.id : 0;
    }

    public String getPortfolioUrl() {
        if (TextUtils.isEmpty(FilePath.URL_PORTFOLIO)) {
            ProfileResponse profileResponse = getProfileData();
            if (profileResponse != null && profileResponse.filePaths != null && !TextUtils.isEmpty(profileResponse.filePaths.portfoliosFiles)) {
                FilePath.URL_PORTFOLIO = profileResponse.filePaths.portfoliosFiles;
                return profileResponse.filePaths.portfoliosFiles;
            }
        }
        return FilePath.URL_PORTFOLIO;
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

    public String getAgentAttachmentUrl() {
        if (TextUtils.isEmpty(FilePath.URL_A_ATTACHMENTS)) {
            ProfileResponse profileResponse = getProfileData();
            if (profileResponse != null && profileResponse.filePaths != null && !TextUtils.isEmpty(profileResponse.filePaths.agentAttachments)) {
                FilePath.URL_A_ATTACHMENTS = profileResponse.filePaths.agentAttachments;
                return profileResponse.filePaths.agentAttachments;
            }
        }
        return FilePath.URL_A_ATTACHMENTS;
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

    public String getImageIdUrl() {
        if (TextUtils.isEmpty(FilePath.URL_IMAGE_ID)) {
            ProfileResponse profileResponse = getProfileData();
            if (profileResponse != null && profileResponse.filePaths != null && !TextUtils.isEmpty(profileResponse.filePaths.imgId)) {
                FilePath.URL_IMAGE_ID = profileResponse.filePaths.imgId;
                return profileResponse.filePaths.imgId;
            }
        }
        return FilePath.URL_IMAGE_ID;
    }

    public String getClientImageUrl() {
        if (TextUtils.isEmpty(FilePath.URL_CLIENT_IMAGE)) {
            ProfileResponse profileResponse = getProfileData();
            if (profileResponse != null && profileResponse.filePaths != null && !TextUtils.isEmpty(profileResponse.filePaths.clientImg)) {
                FilePath.URL_CLIENT_IMAGE = profileResponse.filePaths.clientImg;
                return profileResponse.filePaths.clientImg;
            }
        }
        return FilePath.URL_CLIENT_IMAGE;
    }

    public String getResumeUrl() {
        if (TextUtils.isEmpty(FilePath.URL_RESUME)) {
            ProfileResponse profileResponse = getProfileData();
            if (profileResponse != null && profileResponse.filePaths != null && !TextUtils.isEmpty(profileResponse.filePaths.resume)) {
                FilePath.URL_RESUME = profileResponse.filePaths.resume;
                return profileResponse.filePaths.resume;
            }
        }
        return FilePath.URL_RESUME;
    }

    public String getSubmittedFileUrl() {
        if (TextUtils.isEmpty(FilePath.URL_SUBMITTED_FILES)) {
            ProfileResponse profileResponse = getProfileData();
            if (profileResponse != null && profileResponse.filePaths != null && !TextUtils.isEmpty(profileResponse.filePaths.submittedFiles)) {
                FilePath.URL_SUBMITTED_FILES = profileResponse.filePaths.submittedFiles;
                return profileResponse.filePaths.submittedFiles;
            }
        }
        return FilePath.URL_SUBMITTED_FILES;
    }


    public ProfileResponse getProfileData() {
        return Preferences.getProfileData(this);
    }

    public String getSUserID() {
        return String.valueOf(getUserID());
    }

    public String getDeviceType() {
        return "1"; // for Android
    }

    public String getTimeZone() {
        return TimeZone.getDefault().getID(); // for Android
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null && cm.getActiveNetworkInfo() != null) {
            return true;
        }
        //Toast.makeText(this, "connect to internet", Toast.LENGTH_SHORT).show();
        internetDialog();
        return false;
    }

    public boolean checkIfNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null && cm.getActiveNetworkInfo() != null) {
            return true;
        }
        //Toast.makeText(this, "connect to internet", Toast.LENGTH_SHORT).show();
        return false;
    }

    /*private void internetDialog() {
        try {
            if (alertDialog == null || !alertDialog.isShowing())
                alertDialog = new AlertDialog.Builder(this)
                        .setTitle("No Internet")
                        .setMessage("No internet connection, please try again later")
                        .setPositiveButton("Go to Settings", (dialog, which) -> {
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(intent);
                            dialog.dismiss();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void gotoMainActivity(int screen) {
        if (getParent() != null) {
            ((MainActivity) getParent()).gotoMainActivity(screen);
        } else {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(Constants.SCREEN_NAME, screen);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            finishToRight();
        }
    }

    public void gotoMainActivity(int screen, int tabInsideScreen) {
        if (getParent() != null) {
            ((MainActivity) getParent()).gotoMainActivity(screen);
        } else {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(Constants.SCREEN_NAME, screen);
            i.putExtra(Constants.SCREEN_TAB, tabInsideScreen);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            finishToRight();
        }
    }

    public void goToLoginSignup(boolean isLogin, boolean isNeedToFinish) {
        Intent i = new Intent(this, LoginSignUpActivity.class);
        i.putExtra(Constants.FROM_LOGIN, isLogin);
        i.putExtra(Constants.LOGIN_FINISH, isNeedToFinish);
        startActivity(i);
        openToTop();
    }

    public void clearTopActivity(Class<?> activityClass) {
        if (getParent() != null) {
            ((MainActivity) getParent()).clearTopActivity(activityClass);
        } else {
            Intent i = new Intent(this, activityClass);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            finishToRight();
        }
    }

    public void redirectTab(int tabIndex) {
        if (getParent() != null) {
            ((MainActivity) getParent()).getTabHost().setCurrentTab(tabIndex);
        }
    }

    public void redirectActivity(Class<?> activityClass) {
        if (getParent() != null) {
            ((MainActivity) getParent()).redirectActivity(activityClass);
        } else {
            startActivity(new Intent(this, activityClass));
            openToLeft();
        }
    }

    public void openToTop() {
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    public void openToLeft() {
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    public void finishToBottom() {
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
    }

    public void finishToRight() {
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    public boolean isValidEmail(String target) {
        if (!BuildConfig.DEBUG && !TextUtils.isEmpty(target) && target.contains("@mailinator")) {
            return false;
        }
        return (!isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean isValidMobile(String phone) {
        if (phone.contains("+"))
            phone = phone.replace("+", "");
        return (!isEmpty(phone) && Double.parseDouble(phone) > 0 && Patterns.PHONE.matcher(phone).matches() && phone.length() > 6);
    }

    public boolean isValidUrl(String url) {
        return (!isEmpty(url) && Patterns.WEB_URL.matcher(url.toLowerCase()).matches());
    }

    public boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    public void redirectUsingCustomTab(String url) {
        try {
            Uri uri = Uri.parse(url);
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
            intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            CustomTabsIntent customTabsIntent = intentBuilder.build();
            customTabsIntent.launchUrl(this, uri);
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage("Something went wrong");
        }
    }

    private Dialog dialogFeedback;
    private CircularProgressBar progressBarFeedback;
    private TextView txtSendFeedback;

    public void showFeedbackDialog(Integer screen, String message) {
        final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.setCancelable(true);
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvSend = dialog.findViewById(R.id.tv_send);
        CircularProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
        final EditText etFeedback = dialog.findViewById(R.id.et_feedback);

        tvTitle.setText(message);

        switch (screen) {
            case 1:
                etFeedback.setHint(getString(R.string.did_you_encounter_any_problems_tell_us_about_the_issue_you_are_facing_with_and_help_us_resolve_it));
                break;
            case 2:
                etFeedback.setHint(getString(R.string.specify_the_issue_that_you_are_facing_with_the_app_in_detail));
                break;
            case 3:
                etFeedback.setHint(getString(R.string.help_us_serve_you_better_give_us_your_feedback_and_suggestions));
                break;
            case 4:
                etFeedback.setHint(getString(R.string.do_you_want_to_say_anything_send_your_message_feedback_compliments_ideas_or_suggestions_to_improve_our_service));
                break;
        }


        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvSend.setOnClickListener(v -> {
            if (!isEmpty(etFeedback.getText().toString().trim())) {
                progressBar.setVisibility(View.VISIBLE);
                tvSend.setVisibility(View.INVISIBLE);
                disableEnableTouch(true);

                progressBarFeedback = progressBar;
                dialogFeedback = dialog;
                txtSendFeedback = tvSend;

                sendFeedback(etFeedback.getText().toString());

            } else {
                toastMessage(getString(R.string.enter_your_message));
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);

        Utils.trackFirebaseEvent(this, "Open_Feedback_Screen");
    }

    public void sendFeedback(String message) {
        if (!isNetworkConnected())
            return;

        CommonRequest.SenFeedback senFeedback = new CommonRequest.SenFeedback();
        senFeedback.setApp_version(BuildConfig.VERSION_NAME);
        senFeedback.setDevice_type(Constants.ANDROID);
        senFeedback.setNote(message);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(this, API_ADD_FEEDBACK, senFeedback.toString(), true, this);
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getPackageName());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share via..."));
    }

    public void viewFile(File file) {
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = getUriForFile(this, getPackageName() + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            String mime;
            if (file.getPath().contains(".doc") || file.getPath().contains(".docx")) {
                mime = "application/msword";
            } else if (file.getPath().contains(".txt")) {
                mime = "text/plain";
            } else if (file.getPath().contains(".pdf")) {
                mime = "application/pdf";
            } else if (file.getPath().contains(".ppt") || file.getPath().contains(".pptx")) {
                mime = "application/vnd.ms-powerpoint";
            } else if (file.getPath().contains(".xls") || file.getPath().contains(".xlsx")) {
                mime = "application/vnd.ms-excel";
            } else if (file.getPath().contains(".jpg") || file.getPath().contains(".png") || file.getPath().contains(".jpeg")) {
                mime = "image/*";
            } else if (file.getPath().contains(".mp4") || file.getPath().contains(".avi")) {
                mime = "video/*";
            } else {
                mime = "*/*";
            }
//            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//            String extension = mimeTypeMap.getFileExtensionFromUrl(uri.toString());
//            if (mimeTypeMap.hasExtension(extension))
//                mime = mimeTypeMap.getMimeTypeFromExtension(extension);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            toastMessage("No application available to view this type of file");
            e.printStackTrace();
        }
    }

    public void viewFile(String fileUrl) {
        try {
            String driveUrl = "http://drive.google.com/viewerng/viewer?embedded=true&url=";

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
            String finalFileUrl;
            String mime;
            if (fileUrl.contains(".doc") || fileUrl.contains(".docx")) {
                mime = "application/msword";
                finalFileUrl = driveUrl + fileUrl;
            } else if (fileUrl.contains(".txt")) {
                mime = "text/plain";
                finalFileUrl = driveUrl + fileUrl;
            } else if (fileUrl.contains(".pdf")) {
                mime = "application/pdf";
                finalFileUrl = driveUrl + fileUrl;
            } else if (fileUrl.contains(".ppt") || fileUrl.contains(".pptx")) {
                mime = "application/vnd.ms-powerpoint";
                finalFileUrl = driveUrl + fileUrl;
            } else if (fileUrl.contains(".xls") || fileUrl.contains(".xlsx")) {
                mime = "application/vnd.ms-excel";
                finalFileUrl = driveUrl + fileUrl;
            } else if (fileUrl.contains(".jpg") || fileUrl.contains(".png") || fileUrl.contains(".jpeg") || fileUrl.contains(".gif")) {
                mime = "image/*";
                finalFileUrl = fileUrl;
            } else if (fileUrl.contains(".zip") || fileUrl.contains(".rar")) {
                // WAV audio file
                mime = "application/x-wav";
                finalFileUrl = driveUrl + fileUrl;
            } else if (fileUrl.contains(".mp4") || fileUrl.contains(".avi")) {
                mime = "video/*";
                finalFileUrl = driveUrl + fileUrl;
            } else {
                mime = "*/*";
                finalFileUrl = driveUrl + fileUrl;
            }
            // Log.e("URL", "============= " + finalFileUrl + "\n" + Uri.parse(finalFileUrl));
            if (mime.equalsIgnoreCase("image/*")) {
                intent.setDataAndType(Uri.parse(finalFileUrl), mime);
            } else {
                intent.setData(Uri.parse(fileUrl));
            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            toastMessage("No application available to view this type of file");
            e.printStackTrace();
        }
    }

    public void showContactUsDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_chat_now);
        dialog.setCancelable(true);
        View call = dialog.findViewById(R.id.rl_call);
        View messanger = dialog.findViewById(R.id.rl_messanger);
        View whatsapp = dialog.findViewById(R.id.rl_whatsapp);
        View email = dialog.findViewById(R.id.rl_email);
        View sms = dialog.findViewById(R.id.rl_sms);
        TextView tvCancel = dialog.findViewById(R.id.btn_cancel);

        call.setOnClickListener(v -> makeCall());

        messanger.setOnClickListener(v -> openMessenger());

        whatsapp.setOnClickListener(v -> openWhatsApp());

        email.setOnClickListener(v -> openEmail());

        sms.setOnClickListener(v -> openSMS());

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    public void makeCall() {
        Dexter.withActivity(BaseActivity.this)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(new PermissionListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_CALL,
                                    Uri.parse("tel:" + getString(R.string.phone_number)));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            toastMessage("Failed to invoke call");
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        toastMessage("Please Give Permission to make call");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void openWhatsApp() {
        try {
            String toNumber = getString(R.string.phone_number_);
            toNumber = toNumber.replace("+", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replace("-", "")
                    .replace(" ", "");

            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "");
            sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage("Please install WhatsApp");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")));
        }
    }

    public void openMessenger() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("m.me/24Task")));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.messenger.com/t/24Task")));
        }
    }

    public void openEmail() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", getString(R.string.email_text).toLowerCase(), null));
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (Exception e) {
            toastMessage("Mail apps not installed");
        }
    }

    public void openSMS() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", getString(R.string.phone_number_), null)));
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage("Inbox not found");
        }
    }

    public void setOnProfileLoadListener(OnProfileLoadListener onProfileLoadListener) {
        this.onProfileLoadListener = onProfileLoadListener;
    }

    public void getProfile() {
        if (!isNetworkConnected())
            return;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(this, API_GET_PROFILE, null, false, BaseActivity.this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String message) {
        if (urlEndPoint.equalsIgnoreCase(API_GET_PROFILE)) {
//            new Thread(() -> {
            ProfileResponse profileObject = ProfileResponse.getProfileObject(decryptedData);
            if (profileObject != null) {
                Preferences.setProfileData(BaseActivity.this, profileObject);
                if (onProfileLoadListener != null) {
                    onProfileLoadListener.onProfileLoad(profileObject);
                }
            }
//            }).start();
        } else if (urlEndPoint.equalsIgnoreCase(API_ADD_FEEDBACK)) {
            progressBarFeedback.setVisibility(View.GONE);
            txtSendFeedback.setVisibility(View.VISIBLE);
            if (dialogFeedback != null)
                dialogFeedback.dismiss();

            disableEnableTouch(false);
            toastMessage(message);
        }

    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        if (urlEndPoint.equalsIgnoreCase(API_ADD_FEEDBACK)) {
            progressBarFeedback.setVisibility(View.GONE);
            txtSendFeedback.setVisibility(View.VISIBLE);

            disableEnableTouch(false);
        }
    }

    public interface OnProfileLoadListener {
        void onProfileLoad(ProfileResponse data);
    }

    public String getProperName(String firstName, String lastName, String username) {
        if (!isEmpty(username) && !username.equalsIgnoreCase("null"))
            return username;

        if (isEmpty(firstName) || firstName.equals("null")) {
            return username;
        }
        if (isEmpty(lastName) || lastName.equals("null")) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    public String printDifference(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;

        /*  System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);*/

        return String.valueOf(elapsedDays);

    }

    /*public void rateThisAppDialog(RateClickListener listener) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_rate_app);
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button
        TextViewSFTextPro textCancel = dialog.findViewById(R.id.tv_cancel);
        TextViewSFTextPro textRateNow = dialog.findViewById(R.id.tv_rate_now);

        textCancel.setOnClickListener(v -> {
            dialog.dismiss();
            listener.onClickRateDialog(true);
        });

        textRateNow.setOnClickListener(v -> {
            dialog.dismiss();
            listener.onClickRateDialog(false);
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(myAppLinkToMarket);
            } catch (ActivityNotFoundException e) {
                toastMessage(" Unable to find market app");
            }
        });

        dialog.show();
    }*/

    public static class LayoutBinderHelper extends BaseObservable {
        private Boolean isEdit;

        public LayoutBinderHelper() {
            this.isEdit = false;
        }

        public void setIsEdit(boolean presentationElementsVisible) {
            this.isEdit = presentationElementsVisible;
            notifyChange();
        }

        @Bindable
        public Boolean getIsEdit() {
            return isEdit;
        }

    }

    public void toastMessage(String message) {
        if (inflater != null) {
            View layout = inflater.inflate(R.layout.toast,
                    findViewById(R.id.toast_layout_root));

            TextView text = layout.findViewById(R.id.text);
            text.setText(message);

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.TOP, 0, 40);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
    }

    public void disableEnableTouch(boolean isLoading) {
        if (isLoading) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            if (!isClickableView)
                return super.dispatchTouchEvent(ev);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return true;
    }

    //    //region Chat Work
    public void connectSocket(Activity activity) {
        Task24Application app = (Task24Application) activity.getApplicationContext();
        mSocket = app.getSocketMsg();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_ERROR, onError);
        mSocket.on("offlineParticularUser", offlineParticularUser);
        mSocket.on("onlineParticularUser", onlineParticularUser);
        if (!mSocket.connected())
            mSocket.connect();
    }

    public void onVerifyUser() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("profile_id", getUserID());
            mSocket.emit("verifyUser", jsonObject);
            mSocket.on("userVerifyError", userVerifyError);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private final Emitter.Listener userVerifyError = args -> {
    };

    public void setChatInterface(ChatInterface chatInterface) {
        this.chatInterface = chatInterface;
    }

    public void setNewMessageForList(NewMessageForList newMessage) {
        this.newMessageForList = newMessage;
    }

    public void setOnlineOfflineListener(OnlineOfflineListener onlineOfflineListener) {
        this.onlineOfflineListener = onlineOfflineListener;
    }
    //endregion

    public ChatInterface chatInterface;
    public ChatList.Datum moUserStatus;
    public OnlineOfflineListener onlineOfflineListener;
    public NewMessageForList newMessageForList;
    public Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                onVerifyUser();
                Log.e("AAAAA", "start connect..");
                if (chatInterface != null)
                    chatInterface.onConnect(true);
            });
        }
    };

    private final Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                Log.e("AAAAA", "diconnected");
                if (chatInterface != null)
                    chatInterface.disconnect(true);
            });
        }
    };
    private final Emitter.Listener onlineParticularUser = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("AAAAAA", "onlineParticularUser args...." + args[0].toString());
            moUserStatus = new Gson().fromJson(args[0].toString(), ChatList.Datum.class);
            if (onlineOfflineListener != null)
                onlineOfflineListener.onlineUser(moUserStatus);
        }
    };
    private final Emitter.Listener offlineParticularUser = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("AAAAAA", "offlineParticularUser  args...." + args[0].toString());
            moUserStatus = new Gson().fromJson(args[0].toString(), ChatList.Datum.class);
            if (onlineOfflineListener != null)
                onlineOfflineListener.offlineUsr(moUserStatus);
        }
    };
    private final Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                if (chatInterface != null)
                    chatInterface.onError(args);

                if (!mSocket.connected())
                    mSocket.connect();
            });
        }
    };

    public void openLoginDialog() {
        // new LoginSignUpDialog(this);
    }

    public boolean checkStoragePermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public String getReferralCode() {
        ProfileResponse userData = Preferences.getProfileData(this);
        return userData != null ? userData.referralCode : "";
    }

    public void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void logout() {
        Intercom.client().logout();
        Preferences.saveUserData(this, null);
        Preferences.setProfileData(this, null);
        Preferences.refreshAccount(this, new HashMap<>());//remove all other accounts if exist
        Preferences.writeBoolean(this, Constants.IS_LOGIN, false);
        Preferences.writeString(this, Constants.JWT, "");
        if (this.mSocket != null && this.mSocket.connected()) {
            this.mSocket.disconnect();
        }

        Intent i = new Intent(this, SelectAccountActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        openToLeft();
    }

    private void internetDialog() {
        try {
            final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
            dialog.setTitle(null);
            dialog.setContentView(R.layout.dialog_no_internet);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            TextView btnRetry = dialog.findViewById(R.id.btn_retry);
            TextView btnSettings = dialog.findViewById(R.id.btn_settings);

            btnRetry.setOnClickListener(v -> {
                if (checkIfNetworkConnected()) {
                    dialog.dismiss();
//                    Intent intent = getIntent();
//                    if (getParent() instanceof MainActivity) {
//                        intent = getParentActivityIntent();
//                    }
//                    if (intent == null) {
//                        intent = getParent().getIntent();
//                    }
                    if (networkListener != null) {
                        networkListener.networkConnected();
                    } else {
                        finish();
                    }

//                    startActivity(intent);
                }
            });

            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
                dialog.dismiss();
            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openDocuments(BaseActivity activity, int numOfFile) {
        try {
            Intent intent;
            if (android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
                intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                intent.putExtra("CONTENT_TYPE", "*/*");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
            } else {

                String[] mimeTypes =
                        {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                "text/plain",
                                "application/pdf",
                                "application/zip", "application/vnd.android.package-archive"};

                intent = new Intent(Intent.ACTION_GET_CONTENT); // or ACTION_OPEN_DOCUMENT
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            }
            activity.startActivityForResult(intent, 4545);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void openDocuments(BaseFragment activity, int numOfFile) {
        try {
            Intent intent;
            if (android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
                intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                intent.putExtra("CONTENT_TYPE", "*/*");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
            } else {

                String[] mimeTypes =
                        {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                "text/plain",
                                "application/pdf",
                                "application/zip", "application/vnd.android.package-archive"};

                intent = new Intent(Intent.ACTION_GET_CONTENT); // or ACTION_OPEN_DOCUMENT
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            }
            activity.startActivityForResult(intent, 4545);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}

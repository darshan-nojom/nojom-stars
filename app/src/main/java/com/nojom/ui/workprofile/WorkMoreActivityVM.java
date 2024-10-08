package com.nojom.ui.workprofile;

import static com.nojom.util.Constants.API_LOGOUT;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.startup.OnboardingActivity;
import com.nojom.ui.startup.SelectAccountActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.HashMap;
import java.util.Objects;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.intercom.android.sdk.Intercom;

public class WorkMoreActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    private BaseActivity activity;

    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShow = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShow() {
        return isShow;
    }

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    void showLogoutDialog(BaseActivity activity) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        String s = activity.getString(R.string.logout_msg);
        String[] words = {activity.getString(R.string.sign_out_)};
        String[] fonts = {Constants.SFTEXT_BOLD};
        tvMessage.setText(Utils.getBoldString(activity, s, fonts, null, words));

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            logout(activity);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    void logout(BaseActivity activity) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(true);

        CommonRequest.Logout logout = new CommonRequest.Logout();
        logout.setDevice_token(activity.getToken());

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_LOGOUT, logout.toString(), true, this);

    }

    void deleteAccountRequestDialog(BaseActivity activity) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        String s = activity.getString(R.string.request_delete_account);
        String[] words = {activity.getString(R.string.delete)};
        String[] fonts = {Constants.SFTEXT_BOLD};
        tvMessage.setText(Utils.getBoldString(activity, s, fonts, null, words));

        tvCancel.setText(activity.getString(R.string.cancel));
        tvChatnow.setText(activity.getString(R.string.delete));

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            deleteAccountRequest(activity);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private void deleteAccountRequest(BaseActivity activity) {
        if (!activity.isNetworkConnected())
            return;

    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_LOGOUT)) {
            activity.toastMessage(msg);
            Intercom.client().logout();

            Preferences.saveUserData(activity, null);
            Preferences.setProfileData(activity, null);
            Preferences.refreshAccount(activity, new HashMap<>());//remove all other accounts if exist
            Preferences.writeBoolean(activity, Constants.IS_LOGIN, false);
            Preferences.writeString(activity, Constants.JWT, "");
            if (activity.mSocket != null && activity.mSocket.connected()) {
                activity.mSocket.disconnect();
            }

//            Intent i = new Intent(activity, SelectAccountActivity.class);
            Intent i = new Intent(activity, OnboardingActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(i);
            activity.openToLeft();
            getIsShowProgress().postValue(false);
        }
//        activity.hideProgress();
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
//        activity.hideProgress();
        getIsShow().postValue(false);
    }
}

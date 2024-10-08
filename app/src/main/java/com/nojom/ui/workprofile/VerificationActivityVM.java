package com.nojom.ui.workprofile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.ProfileResponse;
import com.nojom.model.TrustPoint;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.nojom.util.Constants.API_VERIFY_FACEBOOK;
import static com.nojom.util.Constants.API_VERIFY_GOOGLE;

public class VerificationActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<ArrayList<TrustPoint>> listMutableLiveData;

    public MutableLiveData<ArrayList<TrustPoint>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    public VerificationActivityVM() {

    }

    void init(BaseActivity activity) {
        this.activity = activity;
    }

    void initFacebook(CallbackManager callbackManager) {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getGraphRequest(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
//                        activity.hideProgress();
                    }

                    @Override
                    public void onError(FacebookException e) {
                        if (e instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                        if (!activity.isEmpty(e.getMessage()))
                            Log.e("LoginActivity", Objects.requireNonNull(e.getMessage()));
//                        activity.hideProgress();
                    }
                });
    }

    private void getGraphRequest(AccessToken token) {
        GraphRequest request = GraphRequest.newMeRequest(
                token,
                (object, response) -> {
//                    activity.hideProgress();
                    try {
                        if (object != null) {
                            String id = object.getString("id");
                            verifyFacebook(id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }


    void getList(ProfileResponse.TrustRate trustPoints) {
        ArrayList<TrustPoint> arrayList = new ArrayList<>();
        arrayList.add(new TrustPoint(activity.getString(R.string.email), trustPoints != null ? trustPoints.email : 0, 30));
        arrayList.add(new TrustPoint(activity.getString(R.string.phonenumber), trustPoints != null ? trustPoints.phoneNumber : 0, 30));
//        arrayList.add(new TrustPoint(activity.getString(R.string.google), trustPoints != null ? trustPoints.google : 0, 10));
//        arrayList.add(new TrustPoint(activity.getString(R.string.facebook), trustPoints != null ? trustPoints.facebook : 0, 20));
//        arrayList.add(new TrustPoint(activity.getString(R.string.payment), trustPoints != null ? trustPoints.payment : 0, 20));
        arrayList.add(new TrustPoint(activity.getString(R.string.mawthooq), trustPoints != null ? trustPoints.mawthooq : 0, 40));
//        arrayList.add(new TrustPoint(activity.getString(R.string.verify_id), trustPoints != null ? trustPoints.verifyId : 0, 35));

        getListMutableLiveData().postValue(arrayList);
    }


    void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.task_wants_to_use_facebook_to_sign_in));
        builder.setMessage(activity.getString(R.string.this_allows_the_app_and_website_to_share_info_about_you));
        builder.setCancelable(false);

        builder.setPositiveButton(
                activity.getString(R.string.continue_),
                (dialog, id) -> {
                    dialog.cancel();
//                    activity.showProgress();
                    LoginManager.getInstance().logInWithReadPermissions(activity, Collections.singletonList("public_profile"));
                });

        builder.setNegativeButton(
                activity.getString(R.string.cancel),
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void verifyFacebook(String fbId) {
        if (!activity.isNetworkConnected())
            return;

//        activity.showProgress();

        CommonRequest.VerifyFb verifyFb = new CommonRequest.VerifyFb();
        verifyFb.setFacebook_id(fbId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_VERIFY_FACEBOOK, verifyFb.toString(), true, this);

    }

    public void verifyGoogle(String gId) {
        if (!activity.isNetworkConnected())
            return;

//        activity.showProgress();

        CommonRequest.GoogleId verifyFb = new CommonRequest.GoogleId();
        verifyFb.setGoogle_id(gId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_VERIFY_GOOGLE, verifyFb.toString(), true, this);

    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
//        activity.hideProgress();
        activity.getProfile();
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {

//        activity.hideProgress();
    }
}

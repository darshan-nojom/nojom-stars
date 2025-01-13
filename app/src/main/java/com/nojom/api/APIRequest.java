package com.nojom.api;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nojom.R;
import com.nojom.model.APIResponse;
import com.nojom.model.APIResponseArray;
import com.nojom.model.AddCard;
import com.nojom.model.CampListResponse;
import com.nojom.model.CampaignType;
import com.nojom.model.CampaignUrls;
import com.nojom.model.CommonModel;
import com.nojom.model.MawData;
import com.nojom.model.ServicesData;
import com.nojom.model.UpdateCard;
import com.nojom.model.WalletResponse;
import com.nojom.model.WalletTxnResponse;
import com.nojom.model.WithdrawAmount;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.AESHelper;
import com.nojom.util.Utils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIRequest {

    public interface APIRequestListener {
        void onResponseSuccess(String decryptedData, String urlEndPoint, String msg);

        void onResponseError(Throwable t, String urlEndPoint, String message);
    }

    public interface JWTRequestResponseListener {

        void successResponseJWT(String responseBody, String url, String message, String data);

        void failureResponseJWT(Throwable throwable, String url, String message);

    }

    public void makeAPIRequestFileUpload(BaseActivity activity, String url, String bodyData, APIRequestListener apiRequestListener,
                                         MultipartBody.Part fileBody) {

        String jwtToken = activity.getJWT();

        Call<APIResponse> call;

        String encryptedData = "";
        try {
            encryptedData = AESHelper.encrypt(bodyData);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(encryptedData)) {
            activity.toastMessage("Invalid Request");
            return;
        }

        Log.e("Request ", url + "\nEncrypted Data----- " + encryptedData);
        RequestBody body = RequestBody.create(encryptedData, MultipartBody.FORM);
        call = activity.getService().requestAPIHeaderFileUpload(jwtToken, url, body, fileBody);

        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                if (response.isSuccessful()) {
                    APIResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status) {
                            try {
                                String decryptedData = null;
                                Log.e("Response   ", url + "\nDecrypted Data : " + apiResponse.data);
                                if (!TextUtils.isEmpty(apiResponse.data)) {
                                    decryptedData = AESHelper.decrypt(apiResponse.data);
                                    Log.e("Response ", url + "\nPlain Data----- " + decryptedData);
                                }

                                if (apiRequestListener != null) {
                                    apiRequestListener.onResponseSuccess(decryptedData, url, apiResponse.getMessage(activity.language));
                                }

                            } catch (UnsupportedEncodingException | NoSuchAlgorithmException |
                                     NoSuchPaddingException | InvalidAlgorithmParameterException |
                                     InvalidKeyException | BadPaddingException |
                                     IllegalBlockSizeException e) {
                                e.printStackTrace();

                                if (apiRequestListener != null) {
                                    apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                                }
                            }
                        } else {
                            displayMessage(url, apiResponse.getMessage(activity.language), activity);
                            if (apiRequestListener != null) {
                                apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                if (apiRequestListener != null) {
                    apiRequestListener.onResponseError(t, url, "");
                }
            }
        });
    }

    public void makeAPIRequestFileUpload(BaseActivity activity, String url, String bodyData, APIRequestListener apiRequestListener,
                                         MultipartBody.Part fileBody, MultipartBody.Part fileBody1) {

        String jwtToken = activity.getJWT();

        Call<APIResponse> call;

        String encryptedData = "";
        try {
            encryptedData = AESHelper.encrypt(bodyData);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(encryptedData)) {
            activity.toastMessage("Invalid Request");
            return;
        }

        Log.e("Request ", url + "\nEncrypted Data----- " + encryptedData);
        RequestBody body = RequestBody.create(encryptedData, MultipartBody.FORM);
        call = activity.getService().requestAPIHeaderFileUpload(jwtToken, url, body, fileBody, fileBody1);

        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                if (response.isSuccessful()) {
                    APIResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status) {
                            try {
                                String decryptedData = null;
                                Log.e("Response   ", url + "\nDecrypted Data : " + apiResponse.data);
                                if (!TextUtils.isEmpty(apiResponse.data)) {
                                    decryptedData = AESHelper.decrypt(apiResponse.data);
                                    Log.e("Response ", url + "\nPlain Data----- " + decryptedData);
                                }

                                if (apiRequestListener != null) {
                                    apiRequestListener.onResponseSuccess(decryptedData, url, apiResponse.getMessage(activity.language));
                                }

                            } catch (UnsupportedEncodingException | NoSuchAlgorithmException |
                                     NoSuchPaddingException | InvalidAlgorithmParameterException |
                                     InvalidKeyException | BadPaddingException |
                                     IllegalBlockSizeException e) {
                                e.printStackTrace();

                                if (apiRequestListener != null) {
                                    apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                                }
                            }
                        } else {
                            displayMessage(url, apiResponse.getMessage(activity.language), activity);
                            if (apiRequestListener != null) {
                                apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                if (apiRequestListener != null) {
                    apiRequestListener.onResponseError(t, url, activity.getString(R.string.please_give_permission));
                }
            }
        });
    }

    public void makeAPIRequestFileUpload(BaseActivity activity, String url, String bodyData, APIRequestListener apiRequestListener, MultipartBody.Part[] fileBody) {

        String jwtToken = activity.getJWT();

        Call<APIResponse> call;

        String encryptedData = "";
        try {
            encryptedData = AESHelper.encrypt(bodyData);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(encryptedData)) {
            activity.toastMessage("Invalid Request");
            return;
        }

        Log.e("Request ", url + "\nEncrypted Data----- " + encryptedData);
        RequestBody body = RequestBody.create(encryptedData, MultipartBody.FORM);
        call = activity.getService().requestAPIHeaderFileUpload(jwtToken, url, body, fileBody);

        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                if (response.isSuccessful()) {
                    APIResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status) {
                            try {
                                String decryptedData = null;
                                Log.e("Response   ", url + "\nDecrypted Data : " + apiResponse.data);
                                if (!TextUtils.isEmpty(apiResponse.data)) {
                                    decryptedData = AESHelper.decrypt(apiResponse.data);
                                    Log.e("Response ", url + "\nPlain Data----- " + decryptedData);
                                }

                                if (apiRequestListener != null) {
                                    apiRequestListener.onResponseSuccess(decryptedData, url, apiResponse.getMessage(activity.language));
                                }

                            } catch (UnsupportedEncodingException | NoSuchAlgorithmException |
                                     NoSuchPaddingException | InvalidAlgorithmParameterException |
                                     InvalidKeyException | BadPaddingException |
                                     IllegalBlockSizeException e) {
                                e.printStackTrace();

                                if (apiRequestListener != null) {
                                    apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                                }
                            }
                        } else {
                            displayMessage(url, apiResponse.getMessage(activity.language), activity);
                            if (apiRequestListener != null) {
                                apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                if (apiRequestListener != null) {
                    apiRequestListener.onResponseError(t, url, "");
                }
            }
        });
    }

    public void apiRequestJWT(JWTRequestResponseListener requestResponseListener, BaseActivity activity, String url, boolean isPostMethod, Map<String, String> map) {

        Call<APIResponse> orderCall;
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        if (isPostMethod) {
            orderCall = activity.getGigService().requestAPIPOST(url, (HashMap<String, String>) map, jwtToken);
        } else {
            orderCall = activity.getGigService().requestAPIGET(url, jwtToken);
        }

        orderCall.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                if (response.isSuccessful()) {
                    onResponseAPI(activity, url, response, requestResponseListener);
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                onFailureAPI(activity, requestResponseListener, t, url);
            }
        });
    }

    public void makeAPIRequest(BaseActivity activity, String url, String bodyData, boolean isPostMethod, APIRequestListener apiRequestListener) {

        String jwtToken = activity.getJWT();

        Call<APIResponse> call;
        if (isPostMethod) {//POST method

            String encryptedData = "";
            try {
                encryptedData = AESHelper.encrypt(bodyData);
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException |
                     NoSuchPaddingException | InvalidAlgorithmParameterException |
                     InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(encryptedData)) {
                activity.toastMessage("Invalid Request");
                return;
            }

            Log.e("Request ", url + "\nEncrypted Data----- " + encryptedData);
            RequestBody body = RequestBody.create(encryptedData, MultipartBody.FORM);
            call = activity.getService().requestAPIHeader("6", jwtToken, url, body);
        } else {//GET method
            call = activity.getService().requestAPIHeader(jwtToken, url);
        }

        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {

                if (response.isSuccessful()) {
                    APIResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status) {
                            try {
                                String decryptedData = null;
                                Log.e("Response   ", url + "\nDecrypted Data : " + apiResponse.data);
                                if (!TextUtils.isEmpty(apiResponse.data)) {
                                    decryptedData = AESHelper.decrypt(apiResponse.data);
                                    Log.e("Response ", url + "\nPlain Data----- " + decryptedData);
                                }

                                if (apiRequestListener != null) {
                                    apiRequestListener.onResponseSuccess(decryptedData, url, apiResponse.getMessage(activity.language));
                                }

                            } catch (UnsupportedEncodingException | NoSuchAlgorithmException |
                                     NoSuchPaddingException | InvalidAlgorithmParameterException |
                                     InvalidKeyException | BadPaddingException |
                                     IllegalBlockSizeException e) {
                                e.printStackTrace();

                                if (apiRequestListener != null) {
                                    apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                                }
                            }
                        } else {

                            displayMessage(url, apiResponse.getMessage(activity.language), activity);

                            if (apiRequestListener != null) {
                                apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                if (apiRequestListener != null) {
                    apiRequestListener.onResponseError(t, url, "");
                }
            }
        });
    }

    public void makeAPIRequest(String jwtToken, BaseActivity activity, String url, String bodyData, boolean isPostMethod, APIRequestListener apiRequestListener) {

        if (TextUtils.isEmpty(jwtToken)) {
            jwtToken = activity.getJWT();
        }

        Call<APIResponse> call;
        if (isPostMethod) {//POST method

            String encryptedData = "";
            try {
                encryptedData = AESHelper.encrypt(bodyData);
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException |
                     NoSuchPaddingException | InvalidAlgorithmParameterException |
                     InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(encryptedData)) {
                activity.toastMessage("Invalid Request");
                return;
            }

            Log.e("Request ", url + "\nEncrypted Data----- " + encryptedData);
            RequestBody body = RequestBody.create(encryptedData, MultipartBody.FORM);
            call = activity.getService().requestAPIHeader("6", jwtToken, url, body);
        } else {//GET method
            call = activity.getService().requestAPIHeader(jwtToken, url);
        }

        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {

                if (response.isSuccessful()) {
                    APIResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status) {
                            try {
                                String decryptedData = null;
                                Log.e("Response   ", url + "\nDecrypted Data : " + apiResponse.data);
                                if (!TextUtils.isEmpty(apiResponse.data)) {
                                    decryptedData = AESHelper.decrypt(apiResponse.data);
                                    Log.e("Response ", url + "\nPlain Data----- " + decryptedData);
                                }

                                if (apiRequestListener != null) {
                                    apiRequestListener.onResponseSuccess(apiResponse.token, url, apiResponse.getMessage(activity.language));
                                }

                            } catch (UnsupportedEncodingException | NoSuchAlgorithmException |
                                     NoSuchPaddingException | InvalidAlgorithmParameterException |
                                     InvalidKeyException | BadPaddingException |
                                     IllegalBlockSizeException e) {
                                e.printStackTrace();

                                if (apiRequestListener != null) {
                                    apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                                }
                            }
                        } else {

                            displayMessage(url, apiResponse.getMessage(activity.language), activity);

                            if (apiRequestListener != null) {
                                apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                if (apiRequestListener != null) {
                    apiRequestListener.onResponseError(t, url, "");
                }
            }
        });
    }

    public void apiImageUploadRequestBody(JWTRequestResponseListener requestResponseListener, BaseActivity activity, String url, MultipartBody.Part[] fileBody, RequestBody gigTitleBody, RequestBody gigDescBody,
                                          RequestBody gigCatIdBody, RequestBody gigSubCatIdBody, RequestBody gigPackagesBody, RequestBody status, RequestBody gigId, RequestBody searTagsList, RequestBody languages) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<APIResponse> orderCall = activity.getGigService().uploadFileBody(url, fileBody, gigTitleBody, gigCatIdBody, gigSubCatIdBody, gigDescBody, gigPackagesBody, searTagsList, status, gigId, languages, jwtToken);
        orderCall.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                if (response.isSuccessful()) {
                    onResponseAPI(activity, url, response, requestResponseListener);
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                onFailureAPI(activity, requestResponseListener, t, url);
            }
        });
    }

    public void gigDuplicateRequest(JWTRequestResponseListener requestResponseListener, BaseActivity activity, String url, MultipartBody.Part[] fileBody, RequestBody gigTitleBody, RequestBody gigDescBody,
                                    RequestBody gigCatIdBody, RequestBody gigSubCatIdBody, RequestBody gigPackagesBody, RequestBody status, RequestBody gigId, RequestBody searTagsList, RequestBody languages
            , RequestBody fileToDelete, RequestBody isDuplicate) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<APIResponse> orderCall = activity.getGigService().duplicateGigRequest(url, fileBody, gigTitleBody, gigCatIdBody, gigSubCatIdBody, gigDescBody, gigPackagesBody, searTagsList, status, gigId, languages, fileToDelete, isDuplicate, jwtToken);
        orderCall.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                if (response.isSuccessful()) {
                    onResponseAPI(activity, url, response, requestResponseListener);
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                onFailureAPI(activity, requestResponseListener, t, url);
            }
        });
    }

    public void apiImageUploadRequestBodyCustomGig(JWTRequestResponseListener requestResponseListener, BaseActivity activity, String url, MultipartBody.Part[] fileBody, RequestBody gigTitleBody, RequestBody gigDescBody,
                                                   RequestBody gigCatIdBody, RequestBody gigSubCatIdBody, RequestBody gigPackagesBody, RequestBody gigOtheReqBody, RequestBody status, RequestBody gigId, RequestBody searTagsList, RequestBody languages, RequestBody profileBody, RequestBody deadline, RequestBody mainPriceBody
            , RequestBody deadlineDesc, RequestBody platformBody) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<APIResponse> orderCall = activity.getGigService().uploadFileBodyCustomGig(url, fileBody, gigTitleBody, gigCatIdBody, gigSubCatIdBody, gigDescBody, gigPackagesBody, gigOtheReqBody, searTagsList, status, gigId, languages, profileBody, deadline, mainPriceBody, jwtToken, deadlineDesc, platformBody);
        orderCall.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                if (response.isSuccessful()) {
                    onResponseAPI(activity, url, response, requestResponseListener);
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                onFailureAPI(activity, requestResponseListener, t, url);
            }
        });
    }

    public void gigDuplicateRequestCustomGig(JWTRequestResponseListener requestResponseListener, BaseActivity activity, String url, MultipartBody.Part[] fileBody, RequestBody gigTitleBody, RequestBody gigDescBody,
                                             RequestBody gigCatIdBody, RequestBody gigSubCatIdBody, RequestBody gigPackagesBody, RequestBody gigOtherReqBody, RequestBody status, RequestBody gigId, RequestBody searTagsList, RequestBody languages
            , RequestBody fileToDelete, RequestBody isDuplicate, RequestBody profileBody, RequestBody deadlineType, RequestBody mainPriceBody
            , RequestBody deadlineDesc, RequestBody platformBody) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<APIResponse> orderCall = activity.getGigService().duplicateGigRequestCustomGig(url, fileBody, gigTitleBody, gigCatIdBody, gigSubCatIdBody, gigDescBody, gigPackagesBody, gigOtherReqBody, searTagsList, status, gigId, languages, fileToDelete, isDuplicate, profileBody, deadlineType, mainPriceBody, jwtToken, deadlineDesc, platformBody);
        orderCall.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                if (response.isSuccessful()) {
                    onResponseAPI(activity, url, response, requestResponseListener);
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                onFailureAPI(activity, requestResponseListener, t, url);
            }
        });
    }

    public void apiRequestBodyJWT(JWTRequestResponseListener requestResponseListener, BaseActivity activity, String url, HashMap<String, RequestBody> map) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }
        Call<APIResponse> orderCall = activity.getGigService().requestGigList(url, map, jwtToken);
        orderCall.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                if (response.isSuccessful()) {
                    onResponseAPI(activity, url, response, requestResponseListener);
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                onFailureAPI(activity, requestResponseListener, t, url);
            }
        });
    }

    public void apiRequestFileBodyJWT(JWTRequestResponseListener requestResponseListener, BaseActivity activity, String url, HashMap<String, RequestBody> map, MultipartBody.Part[] fileBody) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }
        Call<APIResponse> orderCall = activity.getGigService().requestAPIGIG(url, map, fileBody, jwtToken);
        orderCall.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                if (response.isSuccessful()) {
                    onResponseAPI(activity, url, response, requestResponseListener);
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                onFailureAPI(activity, requestResponseListener, t, url);
            }
        });
    }

    public void apiRequestFileBodyJWT(JWTRequestResponseListener requestResponseListener, BaseActivity activity, String url, HashMap<String, RequestBody> map, MultipartBody.Part fileBody) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }
        Call<APIResponse> orderCall = activity.getService().requestAPIGIG(url, map, fileBody, jwtToken);
        orderCall.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {
                if (response.isSuccessful()) {
                    onResponseAPI(activity, url, response, requestResponseListener);
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                onFailureAPI(activity, requestResponseListener, t, url);
            }
        });
    }

    private void onResponseAPI(BaseActivity activity, String url, Response<APIResponse> response, JWTRequestResponseListener requestResponseListener) {
//        activity.hideProgress();
        try {
            if (response.body() != null) {
                APIResponse commonResponse = response.body();
                if (commonResponse.status) {
                    if (!TextUtils.isEmpty(commonResponse.data)) {

                        requestResponseListener.successResponseJWT(Utils.decode(commonResponse.data), url, commonResponse.getMessage(activity.language), commonResponse.data);
                    } else {
                        requestResponseListener.successResponseJWT("", url, commonResponse.getMessage(activity.language), "");
                    }
                } else {
                    try {
                        displayMessage(url, commonResponse.getMessage(activity.language), activity);
                        requestResponseListener.failureResponseJWT(null, url, commonResponse.getMessage(activity.language));
                    } catch (Exception e) {
                        requestResponseListener.failureResponseJWT(null, url, commonResponse.getMessage(activity.language));
                    }
                }
            } else {
                activity.failureError(activity.getString(R.string.something_went_wrong));
            }
        } catch (Exception e) {
            Log.d("TTT", "Exception..." + e.getMessage());
            requestResponseListener.failureResponseJWT(null, url, "");
        }
    }

    private void displayMessage(String url, String message, BaseActivity activity) {
        switch (url) {
            case "get_income":
            case "get_withdrawal":
            case "get_payment_account":
            case "get_job_posts":
            case "get_portfolio":
            case "getSocialSurvey":
            case "getPromocodeHistory":
            case "get_agent_review":
            case "getSocialSurveyDetail":
            case "agent/getGigLists":
            case "agent/getSocialPlatformGigLists":
            case "getSocialPlatform":
            case "get_connected_social_platform":
            case "view_profile_verification_mawthooq":
            case "https://i8py5l9mxj.execute-api.us-east-2.amazonaws.com/dev/agent/getGigLists":
            case "https://gig.24task.com/agent/getGigLists":
                break;
            default:
                activity.runOnUiThread(() -> activity.toastMessage(TextUtils.isEmpty(message) ? activity.getString(R.string.something_went_wrong) : message));
                break;
        }
    }

    private void onFailureAPI(BaseActivity activity, JWTRequestResponseListener requestResponseListener, Throwable t, String url) {
//        activity.hideProgress();
        Log.d("TTT", "Throwable....." + t.getMessage());
        activity.failureError(activity.getString(R.string.something_went_wrong));
        requestResponseListener.failureResponseJWT(t, url, "");
    }

    public void makeSimpleAPIRequest(BaseActivity activity, String url, String bodyData, boolean isPostMethod, APIRequestListener apiRequestListener) {

        String jwtToken = activity.getJWT();

        Call<APIResponseArray> call;
        call = activity.getService().simpleRequestAPIHeader(jwtToken, url);


        call.enqueue(new Callback<APIResponseArray>() {
            @Override
            public void onResponse(@NonNull Call<APIResponseArray> call, @NonNull Response<APIResponseArray> response) {

                if (response.isSuccessful()) {
                    APIResponseArray apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status) {
                            if (apiRequestListener != null) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    String jsonArray
                                            = objectMapper.writeValueAsString(apiResponse.data);
                                    System.out.println(jsonArray);
                                    apiRequestListener.onResponseSuccess(jsonArray, url, apiResponse.getMessage(activity.language));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {

                            displayMessage(url, apiResponse.getMessage(activity.language), activity);

                            if (apiRequestListener != null) {
                                apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponseArray> call, @NonNull Throwable t) {
                if (apiRequestListener != null) {
                    apiRequestListener.onResponseError(t, url, "");
                }
            }
        });
    }

    public void getMawStatus(BaseActivity activity, String url, APIRequestListener apiRequestListener) {

        String jwtToken = activity.getJWT();

        Call<MawData> call;
        call = activity.getService().simpleMawStatus(jwtToken, url);


        call.enqueue(new Callback<MawData>() {
            @Override
            public void onResponse(@NonNull Call<MawData> call, @NonNull Response<MawData> response) {

                if (response.isSuccessful()) {
                    MawData apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status) {
                            if (apiRequestListener != null) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    String jsonArray
                                            = objectMapper.writeValueAsString(apiResponse.data);
                                    System.out.println(jsonArray);
                                    apiRequestListener.onResponseSuccess(jsonArray, url, apiResponse.getMessage(activity.language));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {

                            displayMessage(url, apiResponse.getMessage(activity.language), activity);

                            if (apiRequestListener != null) {
                                apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MawData> call, @NonNull Throwable t) {
                if (apiRequestListener != null) {
                    apiRequestListener.onResponseError(t, url, "");
                }
            }
        });
    }

    public void submitMawthooq(BaseActivity activity, String url, String bodyData, boolean isPostMethod, APIRequestListener apiRequestListener) {

        String jwtToken = activity.getJWT();

        Call<APIResponse> call;

        String encryptedData = "";
        try {
            encryptedData = AESHelper.encrypt(bodyData);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException |
                 NoSuchPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(encryptedData)) {
            activity.toastMessage("Invalid Request");
            return;
        }

        Log.e("Request ", url + "\nEncrypted Data----- " + encryptedData);
        RequestBody body = RequestBody.create(encryptedData, MultipartBody.FORM);
        call = activity.getService().requestAPIHeader("6", jwtToken, url, body);

        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(@NonNull Call<APIResponse> call, @NonNull Response<APIResponse> response) {

                if (response.isSuccessful()) {
                    APIResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status) {
                            if (apiRequestListener != null) {
                                apiRequestListener.onResponseSuccess(null, url, apiResponse.getMessage(activity.language));
                            }
                        } else {

                            displayMessage(url, apiResponse.getMessage(activity.language), activity);

                            if (apiRequestListener != null) {
                                apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        String errorBodyString = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorBodyString);
                        String message = jsonObject.getString("message");
                        if (!TextUtils.isEmpty(message)) {

                            if (apiRequestListener != null) {
                                apiRequestListener.onResponseError(null, url, message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<APIResponse> call, @NonNull Throwable t) {
                if (apiRequestListener != null) {
                    apiRequestListener.onResponseError(t, url, "");
                }
            }
        });
    }

    public void getServices(BaseActivity activity, String url, APIRequestListener apiRequestListener) {

        String jwtToken = activity.getJWT();

        Call<ServicesData> call;
        call = activity.getService().getServices(jwtToken, url);


        call.enqueue(new Callback<ServicesData>() {
            @Override
            public void onResponse(@NonNull Call<ServicesData> call, @NonNull Response<ServicesData> response) {

                if (response.isSuccessful()) {
                    ServicesData apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status) {
                            if (apiRequestListener != null) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    String jsonArray
                                            = objectMapper.writeValueAsString(apiResponse.data);
                                    System.out.println(jsonArray);
                                    apiRequestListener.onResponseSuccess(jsonArray, url, apiResponse.getMessage(activity.language));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {

                            displayMessage(url, apiResponse.getMessage(activity.language), activity);

                            if (apiRequestListener != null) {
                                apiRequestListener.onResponseError(null, url, apiResponse.getMessage(activity.language));
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    activity.logout();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServicesData> call, @NonNull Throwable t) {
                if (apiRequestListener != null) {
                    apiRequestListener.onResponseError(t, url, "");
                }
            }
        });
    }

    public void fetchCampaign(CampaignListener requestResponseListener, BaseActivity activity, String url, CampaignType body) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<CampListResponse> orderCall = activity.getService().getCampaign(url, jwtToken, "6");
        orderCall.enqueue(new Callback<CampListResponse>() {
            @Override
            public void onResponse(@NonNull Call<CampListResponse> call, @NonNull Response<CampListResponse> response) {
                if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        if (response.body() != null) {
                            CampListResponse commonResponse = response.body();
                            if (commonResponse.status) {
                                if (commonResponse.data != null) {
                                    requestResponseListener.successResponse(commonResponse.data, url, commonResponse.getMessage(activity.language));
                                } else {
                                    requestResponseListener.successResponse(null, url, commonResponse.getMessage(activity.language));
                                }
                            } else {
                                try {
                                    activity.runOnUiThread(() -> {
                                        switch (url) {
                                            case "getJobPosts":
                                            case "getGigListsV2":
                                            case "getJobPostsV2":
                                            case "getPortfolio":
                                            case "getProfileByCategory":
                                                break;
                                            case "checkPromocode":
                                                if (!commonResponse.getMessage(activity.language).contains("Empty")) {
                                                    activity.toastMessage(commonResponse.getMessage(activity.language));
                                                }
                                                break;
                                        }
                                    });
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                } catch (Exception e) {
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                }

                            }
                        } else {
                            //activity.failureError(activity.getString(R.string.something_went_wrong));
                            requestResponseListener.failureResponse(null, url, "");
                        }
                    } catch (Exception e) {
                        requestResponseListener.failureResponse(null, url, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CampListResponse> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.something_went_wrong));
                requestResponseListener.failureResponse(t, url, "");
            }
        });
    }

    public void updateCampStatus(CampaignListener requestResponseListener, BaseActivity activity, String url, CampaignType body) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<CampListResponse> orderCall = activity.getService().campStatus(url, jwtToken, "6");
        orderCall.enqueue(new Callback<CampListResponse>() {
            @Override
            public void onResponse(@NonNull Call<CampListResponse> call, @NonNull Response<CampListResponse> response) {
                if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        if (response.body() != null) {
                            CampListResponse commonResponse = response.body();
                            if (commonResponse.status) {
                                if (commonResponse.data != null) {
                                    requestResponseListener.successResponse(commonResponse.data, url, commonResponse.getMessage(activity.language));
                                } else {
                                    requestResponseListener.successResponse(null, url, commonResponse.getMessage(activity.language));
                                }
                            } else {
                                try {
                                    activity.runOnUiThread(() -> {
                                        switch (url) {
                                            case "getJobPosts":
                                            case "getGigListsV2":
                                            case "getJobPostsV2":
                                            case "getPortfolio":
                                            case "getProfileByCategory":
                                                break;
                                            case "checkPromocode":
                                                if (!commonResponse.getMessage(activity.language).contains("Empty")) {
                                                    activity.toastMessage(commonResponse.getMessage(activity.language));
                                                }
                                                break;
                                        }
                                    });
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                } catch (Exception e) {
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                }

                            }
                        } else {
                            //activity.failureError(activity.getString(R.string.something_went_wrong));
                            try {
                                String errorBodyString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(errorBodyString);
                                String message = jsonObject.getString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    requestResponseListener.failureResponse(null, url, message);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        requestResponseListener.failureResponse(null, url, response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CampListResponse> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.something_went_wrong));
                requestResponseListener.failureResponse(t, url, "");
            }
        });
    }

    public void uploadCampAttachment(BaseActivity activity, String url, CampaignListener apiRequestListener, MultipartBody.Part[] fileBody) {

        String jwtToken = activity.getJWT();

        Call<CampListResponse> call;

        call = activity.getService().uploadCampAttach(url, fileBody, jwtToken);

        call.enqueue(new Callback<CampListResponse>() {
            @Override
            public void onResponse(@NonNull Call<CampListResponse> call, @NonNull Response<CampListResponse> response) {
                if (response.isSuccessful()) {
                    CampListResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.status) {
                            if (apiRequestListener != null) {
                                apiRequestListener.successResponse(apiResponse.data, url, apiResponse.getMessage(activity.language));
                            }
                        } else {
                            displayMessage(url, apiResponse.getMessage(activity.language), activity);
                            if (apiRequestListener != null) {
                                apiRequestListener.failureResponse(null, url, apiResponse.getMessage(activity.language));
                            }
                        }
                    }
                } else if (response.code() == 401) {
                    activity.logout();
                } else {

                    try {
                        String errorBodyString = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorBodyString);
                        String message = jsonObject.getString("message");
                        if (!TextUtils.isEmpty(message)) {
                            if (apiRequestListener != null) {
                                apiRequestListener.failureResponse(null, url, message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CampListResponse> call, @NonNull Throwable t) {
                if (apiRequestListener != null) {
                    apiRequestListener.failureResponse(t, url, "");
                }
            }
        });
    }

    public void uploadCampAttachUrls(CampaignListener requestResponseListener, BaseActivity activity, String url, CampaignUrls body) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<CampListResponse> orderCall = activity.getService().uploadCampAttachUrls(url, jwtToken, body, "6");
        orderCall.enqueue(new Callback<CampListResponse>() {
            @Override
            public void onResponse(@NonNull Call<CampListResponse> call, @NonNull Response<CampListResponse> response) {
                if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        if (response.body() != null) {
                            CampListResponse commonResponse = response.body();
                            if (commonResponse.status) {
                                if (commonResponse.data != null) {
                                    requestResponseListener.successResponse(commonResponse.data, url, commonResponse.getMessage(activity.language));
                                } else {
                                    requestResponseListener.successResponse(null, url, commonResponse.getMessage(activity.language));
                                }
                            } else {
                                try {
                                    activity.runOnUiThread(() -> {
                                        switch (url) {
                                            case "getJobPosts":
                                            case "getGigListsV2":
                                            case "getJobPostsV2":
                                            case "getPortfolio":
                                            case "getProfileByCategory":
                                                break;
                                            case "checkPromocode":
                                                if (!commonResponse.getMessage(activity.language).contains("Empty")) {
                                                    activity.toastMessage(commonResponse.getMessage(activity.language));
                                                }
                                                break;
                                        }
                                    });
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                } catch (Exception e) {
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                }

                            }
                        } else {
                            activity.failureError(activity.getString(R.string.something_went_wrong));
                        }
                    } catch (Exception e) {
                        requestResponseListener.failureResponse(null, url, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CampListResponse> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.something_went_wrong));
                requestResponseListener.failureResponse(t, url, "");
            }
        });
    }

    public void getCampById(CampaignListener requestResponseListener, BaseActivity activity, String url) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<CampListResponse> orderCall = activity.getService().getCampById(url, jwtToken, "6");
        orderCall.enqueue(new Callback<CampListResponse>() {
            @Override
            public void onResponse(@NonNull Call<CampListResponse> call, @NonNull Response<CampListResponse> response) {
                if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        if (response.body() != null) {
                            CampListResponse commonResponse = response.body();
                            if (commonResponse.status) {
                                if (commonResponse.data != null) {
                                    requestResponseListener.successResponse(commonResponse.data, url, commonResponse.getMessage(activity.language));
                                } else {
                                    requestResponseListener.successResponse(null, url, commonResponse.getMessage(activity.language));
                                }
                            } else {
                                try {
                                    activity.runOnUiThread(() -> {
                                        switch (url) {
                                            case "getJobPosts":
                                            case "getGigListsV2":
                                            case "getJobPostsV2":
                                            case "getPortfolio":
                                            case "getProfileByCategory":
                                                break;
                                            case "checkPromocode":
                                                if (!commonResponse.getMessage(activity.language).contains("Empty")) {
                                                    activity.toastMessage(commonResponse.getMessage(activity.language));
                                                }
                                                break;
                                        }
                                    });
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                } catch (Exception e) {
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                }

                            }
                        } else {
                            //activity.failureError(activity.getString(R.string.something_went_wrong));
                            requestResponseListener.failureResponse(null, url, "");
                        }
                    } catch (Exception e) {
                        requestResponseListener.failureResponse(null, url, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CampListResponse> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.something_went_wrong));
                requestResponseListener.failureResponse(t, url, "");
            }
        });
    }

    public void getWalletBalance(WalletListener requestResponseListener, BaseActivity activity, String url) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<WalletResponse> orderCall = activity.getService().getWalletBalance(url, jwtToken, "6");
        orderCall.enqueue(new Callback<WalletResponse>() {
            @Override
            public void onResponse(@NonNull Call<WalletResponse> call, @NonNull Response<WalletResponse> response) {
                if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        if (response.body() != null) {
                            WalletResponse commonResponse = response.body();
                            if (commonResponse.status) {
                                if (commonResponse.data != null) {
                                    requestResponseListener.successResponse(commonResponse.data, url, commonResponse.getMessage(activity.language));
                                } else {
                                    requestResponseListener.successResponse(null, url, commonResponse.getMessage(activity.language));
                                }
                            } else {
                                requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                            }
                        } else {
                            activity.failureError(activity.getString(R.string.something_went_wrong));
                        }
                    } catch (Exception e) {
                        requestResponseListener.failureResponse(null, url, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WalletResponse> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.something_went_wrong));
                requestResponseListener.failureResponse(t, url, "");
            }
        });
    }

    public void getWalletTxn(WalletListener requestResponseListener, BaseActivity activity, String url) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<WalletTxnResponse> orderCall = activity.getService().getWallet(url, jwtToken, "6");
        orderCall.enqueue(new Callback<WalletTxnResponse>() {
            @Override
            public void onResponse(@NonNull Call<WalletTxnResponse> call, @NonNull Response<WalletTxnResponse> response) {
                if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        if (response.body() != null) {
                            WalletTxnResponse commonResponse = response.body();
                            if (commonResponse.status) {
                                if (commonResponse.data != null) {
                                    requestResponseListener.successTxnResponse(commonResponse.data, url, commonResponse.getMessage(activity.language));
                                } else {
                                    requestResponseListener.successResponse(null, url, commonResponse.getMessage(activity.language));
                                }
                            } else {
                                requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                            }
                        } else {
                            activity.failureError(activity.getString(R.string.something_went_wrong));
                        }
                    } catch (Exception e) {
                        requestResponseListener.failureResponse(null, url, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WalletTxnResponse> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.something_went_wrong));
                requestResponseListener.failureResponse(t, url, "");
            }
        });
    }

    public void getAccounts(WalletListener requestResponseListener, BaseActivity activity, String url) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<WalletResponse> orderCall = activity.getService().getAccounts(url, jwtToken, "6");
        orderCall.enqueue(new Callback<WalletResponse>() {
            @Override
            public void onResponse(@NonNull Call<WalletResponse> call, @NonNull Response<WalletResponse> response) {
                if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        if (response.body() != null) {
                            WalletResponse commonResponse = response.body();
                            if (commonResponse.status) {
                                if (commonResponse.data != null) {
                                    requestResponseListener.successResponse(commonResponse.data, url, commonResponse.getMessage(activity.language));
                                } else {
                                    requestResponseListener.successResponse(null, url, commonResponse.getMessage(activity.language));
                                }
                            } else {
                                requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                            }
                        } else {
                            activity.failureError(activity.getString(R.string.something_went_wrong));
                        }
                    } catch (Exception e) {
                        requestResponseListener.failureResponse(null, url, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WalletResponse> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.something_went_wrong));
                requestResponseListener.failureResponse(t, url, "");
            }
        });
    }

    public void withdrawMoney(CampaignListener requestResponseListener, BaseActivity activity, String url, WithdrawAmount body) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<CommonModel> orderCall = activity.getService().withdrawAmount(url, jwtToken, body, "6");
        orderCall.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(@NonNull Call<CommonModel> call, @NonNull Response<CommonModel> response) {
                if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        if (response.body() != null) {
                            CommonModel commonResponse = response.body();
                            if (commonResponse.status) {
                                requestResponseListener.successResponse(null, url, commonResponse.getMessage(activity.language));

                            } else {
                                try {
                                    activity.runOnUiThread(() -> {
                                        switch (url) {
                                            case "getJobPosts":
                                            case "getGigListsV2":
                                            case "getJobPostsV2":
                                            case "getPortfolio":
                                            case "getProfileByCategory":
                                                break;
                                            case "checkPromocode":
                                                if (!commonResponse.getMessage(activity.language).contains("Empty")) {
                                                    activity.toastMessage(commonResponse.getMessage(activity.language));
                                                }
                                                break;
                                        }
                                    });
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                } catch (Exception e) {
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                }

                            }
                        } else {
                            activity.failureError(activity.getString(R.string.something_went_wrong));
                        }
                    } catch (Exception e) {
                        requestResponseListener.failureResponse(null, url, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonModel> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.something_went_wrong));
                requestResponseListener.failureResponse(t, url, "");
            }
        });
    }

    public void addCard(CampaignListener requestResponseListener, BaseActivity activity, String url, AddCard body) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<CommonModel> orderCall = activity.getService().addCard(url, jwtToken, body, "6");
        orderCall.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(@NonNull Call<CommonModel> call, @NonNull Response<CommonModel> response) {
                if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        if (response.body() != null) {
                            CommonModel commonResponse = response.body();
                            if (commonResponse.status) {
                                requestResponseListener.successResponse(null, url, commonResponse.getMessage(activity.language));

                            } else {
                                try {
                                    activity.runOnUiThread(() -> {
                                        switch (url) {
                                            case "getJobPosts":
                                            case "getGigListsV2":
                                            case "getJobPostsV2":
                                            case "getPortfolio":
                                            case "getProfileByCategory":
                                                break;
                                            case "checkPromocode":
                                                if (!commonResponse.getMessage(activity.language).contains("Empty")) {
                                                    activity.toastMessage(commonResponse.getMessage(activity.language));
                                                }
                                                break;
                                        }
                                    });
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                } catch (Exception e) {
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                }

                            }
                        } else {
                            activity.failureError(activity.getString(R.string.something_went_wrong));
                        }
                    } catch (Exception e) {
                        requestResponseListener.failureResponse(null, url, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonModel> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.something_went_wrong));
                requestResponseListener.failureResponse(t, url, "");
            }
        });
    }

    public void getHistory(WalletListener requestResponseListener, BaseActivity activity, String url) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<WalletResponse> orderCall = activity.getService().getHistory(url, jwtToken, "6");
        orderCall.enqueue(new Callback<WalletResponse>() {
            @Override
            public void onResponse(@NonNull Call<WalletResponse> call, @NonNull Response<WalletResponse> response) {
                if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        if (response.body() != null) {
                            WalletResponse commonResponse = response.body();
                            if (commonResponse.status) {
                                if (commonResponse.data != null) {
                                    requestResponseListener.successResponse(commonResponse.data, url, commonResponse.getMessage(activity.language));
                                } else {
                                    requestResponseListener.successResponse(null, url, commonResponse.getMessage(activity.language));
                                }
                            } else {
                                requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                            }
                        } else {
                            activity.failureError(activity.getString(R.string.something_went_wrong));
                        }
                    } catch (Exception e) {
                        requestResponseListener.failureResponse(null, url, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WalletResponse> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.something_went_wrong));
                requestResponseListener.failureResponse(t, url, "");
            }
        });
    }

    public void updateBank(CampaignListener requestResponseListener, BaseActivity activity, String url, UpdateCard body) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<CommonModel> orderCall = activity.getService().updateCard(url, jwtToken, body, "6");
        orderCall.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(@NonNull Call<CommonModel> call, @NonNull Response<CommonModel> response) {
                if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        if (response.body() != null) {
                            CommonModel commonResponse = response.body();
                            if (commonResponse.status) {
                                requestResponseListener.successResponse(null, url, commonResponse.getMessage(activity.language));

                            } else {
                                try {
                                    activity.runOnUiThread(() -> {
                                        switch (url) {
                                            case "getJobPosts":
                                            case "getGigListsV2":
                                            case "getJobPostsV2":
                                            case "getPortfolio":
                                            case "getProfileByCategory":
                                                break;
                                            case "checkPromocode":
                                                if (!commonResponse.getMessage(activity.language).contains("Empty")) {
                                                    activity.toastMessage(commonResponse.getMessage(activity.language));
                                                }
                                                break;
                                        }
                                    });
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                } catch (Exception e) {
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                }

                            }
                        } else {
                            activity.failureError(activity.getString(R.string.something_went_wrong));
                        }
                    } catch (Exception e) {
                        requestResponseListener.failureResponse(null, url, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonModel> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.something_went_wrong));
                requestResponseListener.failureResponse(t, url, "");
            }
        });
    }

    public void deleteCard(CampaignListener requestResponseListener, BaseActivity activity, String url, CommonRequest.DeleteBank body) {
        String jwtToken = null;
        if (activity.getJWT() != null && !TextUtils.isEmpty(activity.getJWT())) {
            jwtToken = activity.getJWT();
        }

        Call<CommonModel> orderCall = activity.getService().deleteCard(url, jwtToken, body, "6");
        orderCall.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(@NonNull Call<CommonModel> call, @NonNull Response<CommonModel> response) {
                if (response.code() == 401) {
                    activity.logout();
                } else {
                    try {
                        if (response.body() != null) {
                            CommonModel commonResponse = response.body();
                            if (commonResponse.status) {
                                requestResponseListener.successResponse(null, url, commonResponse.getMessage(activity.language));

                            } else {
                                try {
                                    activity.runOnUiThread(() -> {
                                        switch (url) {
                                            case "getJobPosts":
                                            case "getGigListsV2":
                                            case "getJobPostsV2":
                                            case "getPortfolio":
                                            case "getProfileByCategory":
                                                break;
                                            case "checkPromocode":
                                                if (!commonResponse.getMessage(activity.language).contains("Empty")) {
                                                    activity.toastMessage(commonResponse.getMessage(activity.language));
                                                }
                                                break;
                                        }
                                    });
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                } catch (Exception e) {
                                    requestResponseListener.failureResponse(null, url, commonResponse.getMessage(activity.language));
                                }

                            }
                        } else {
                            activity.failureError(activity.getString(R.string.something_went_wrong));
                        }
                    } catch (Exception e) {
                        requestResponseListener.failureResponse(null, url, "");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CommonModel> call, @NonNull Throwable t) {
                activity.failureError(activity.getString(R.string.something_went_wrong));
                requestResponseListener.failureResponse(t, url, "");
            }
        });
    }

}

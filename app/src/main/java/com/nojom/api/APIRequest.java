package com.nojom.api;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.nojom.model.APIResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.AESHelper;
import com.nojom.util.Utils;

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

    public void makeAPIRequestFileUpload(BaseActivity activity, String url, String bodyData, APIRequestListener apiRequestListener, MultipartBody.Part fileBody) {

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

        Call<APIResponse> orderCall = activity.getGigService().duplicateGigRequestCustomGig(url, fileBody, gigTitleBody, gigCatIdBody, gigSubCatIdBody, gigDescBody, gigPackagesBody, gigOtherReqBody, status, gigId,  fileToDelete, isDuplicate, profileBody, deadlineType, mainPriceBody, jwtToken, deadlineDesc, platformBody);
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
                activity.failureError("Something went wrong");
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
            case "https://i8py5l9mxj.execute-api.us-east-2.amazonaws.com/dev/agent/getGigLists":
            case "https://gig.24task.com/agent/getGigLists":
                break;
            default:
                activity.runOnUiThread(() -> activity.toastMessage(TextUtils.isEmpty(message) ? "Something went wrong" : message));
                break;
        }
    }

    private void onFailureAPI(BaseActivity activity, JWTRequestResponseListener requestResponseListener, Throwable t, String url) {
//        activity.hideProgress();
        Log.d("TTT", "Throwable....." + t.getMessage());
        activity.failureError("Something went wrong");
        requestResponseListener.failureResponseJWT(t, url, "");
    }
}

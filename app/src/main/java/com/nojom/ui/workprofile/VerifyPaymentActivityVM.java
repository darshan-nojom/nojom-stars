package com.nojom.ui.workprofile;

import static android.app.Activity.RESULT_OK;
import static com.nojom.util.Constants.API_ADD_PAYMENT_ACCOUNT;
import static com.nojom.util.Constants.API_ADD_PROFILE_VERIF;
import static com.nojom.util.Constants.API_GENERATE_BRAINTREE_TOKEN;
import static com.nojom.util.Constants.API_VERIFY_PAYPAL_PAYMENT;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nojom.api.APIRequest;
import com.nojom.model.BraintreeToken;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

public class VerifyPaymentActivityVM extends ViewModel implements APIRequest.APIRequestListener {

    private MutableLiveData<String> verifySuccessUrl = new MutableLiveData<>();
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private String generatedNonce;
    private MutableLiveData<Boolean> isShow = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowPayonner = new MutableLiveData<>();
    private MutableLiveData<String> tokenSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> addPaySuccess = new MutableLiveData<>();

    public MutableLiveData<Boolean> getAddPaySuccess() {
        return addPaySuccess;
    }

    public MutableLiveData<String> getGenerateTokenSuccess() {
        return tokenSuccess;
    }

    public MutableLiveData<Boolean> getIsShowPayonner() {
        return isShowPayonner;
    }

    public MutableLiveData<Boolean> getIsShow() {
        return isShow;
    }

    public MutableLiveData<String> getVerifySuccessUrl() {
        if (verifySuccessUrl == null) {
            verifySuccessUrl = new MutableLiveData<>();
        }
        return verifySuccessUrl;
    }


    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    public void verifyPaypal(String generatedNonce) {
        if (!activity.isNetworkConnected())
            return;

        getIsShow().postValue(true);

        CommonRequest.PaypalVerification paypalVerification = new CommonRequest.PaypalVerification();
        paypalVerification.setNonce(generatedNonce);
        paypalVerification.setPayment_type_id("1");

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_VERIFY_PAYPAL_PAYMENT, paypalVerification.toString(), true, this);

    }

    public void addPayonnerAccount(String emailAddress) {
        if (!activity.isNetworkConnected())
            return;
        getIsShowPayonner().postValue(true);

        CommonRequest.PayonnerPaymentAccount payonnerPaymentAccount = new CommonRequest.PayonnerPaymentAccount();
        payonnerPaymentAccount.setAccount(emailAddress);
        payonnerPaymentAccount.setPayment_type_id(2);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_ADD_PAYMENT_ACCOUNT, payonnerPaymentAccount.toString(), true, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_ADD_PROFILE_VERIF)) {
            activity.setResult(RESULT_OK);
            activity.finish();
        } else if (urlEndPoint.equalsIgnoreCase(API_ADD_PAYMENT_ACCOUNT)) {
            getIsShowPayonner().postValue(false);
            getAddPaySuccess().postValue(true);
            activity.toastMessage(msg);
        } else if (urlEndPoint.equalsIgnoreCase(API_GENERATE_BRAINTREE_TOKEN)) {
            BraintreeToken braintreeToken = BraintreeToken.getBraintreeToken(decryptedData);
            if (braintreeToken != null && braintreeToken.token != null) {
                Log.e("TOKEN  ", "" + braintreeToken.token);
                getGenerateTokenSuccess().postValue(braintreeToken.token);
                generateNonce(braintreeToken.token);
            }
        } else if (urlEndPoint.equalsIgnoreCase(API_VERIFY_PAYPAL_PAYMENT)) {
            getVerifySuccessUrl().postValue(null);
            getIsShow().postValue(false);
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShow().postValue(false);
        getIsShowPayonner().postValue(false);
        if (urlEndPoint.equalsIgnoreCase(API_ADD_PAYMENT_ACCOUNT)) {
            activity.toastMessage(message);
        }
    }

    public void generateBrantreeToken() {
        getIsShow().postValue(true);
        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_GENERATE_BRAINTREE_TOKEN, null, false, this);
    }

    public void generateNonce(String token) {

        // drpc.launchDropIn(drp);

//        BraintreeFragment mBraintreeFragment;
//        try {
//            mBraintreeFragment = BraintreeFragment.newInstance(activity, token);
//
//            mBraintreeFragment.addListener(VerifyPaymentActivityVM.this);
//
//            PayPalRequest request = new PayPalRequest()
//                    .localeCode("US")
//                    .billingAgreementDescription("Paypal Verification");
//
//            PayPal.requestBillingAgreement(mBraintreeFragment, request);
//        } catch (InvalidArgumentException e) {
//            e.printStackTrace();
//        }
        getIsShow().postValue(false);
    }

//    @Override
//    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
//        generatedNonce = paymentMethodNonce.getNonce();
//        Log.e("NONCE ", "-------------- " + generatedNonce);
//        verifyPaypal();
//    }

}

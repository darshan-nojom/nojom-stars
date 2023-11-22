package com.nojom.ui.workprofile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.braintreepayments.api.ClientTokenCallback;
import com.braintreepayments.api.ClientTokenProvider;
import com.braintreepayments.api.DropInClient;
import com.braintreepayments.api.DropInListener;
import com.braintreepayments.api.DropInRequest;
import com.braintreepayments.api.DropInResult;
import com.nojom.R;
import com.nojom.databinding.ActivityVerifyPaymentBinding;
import com.nojom.databinding.DialogAddEmailBinding;
import com.nojom.ui.BaseActivity;

import java.util.Objects;

public class VerifyPaymentActivity extends BaseActivity {

    private VerifyPaymentActivityVM verifyPaymentActivityVM;
    private ActivityVerifyPaymentBinding binding;
    private final int REQ_PAYMENT_CODE = 9001;
    private String token = "",clientToken;
    public static DropInClient client = null;
    DropInRequest dropInRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_payment);
        verifyPaymentActivityVM = ViewModelProviders.of(this).get(VerifyPaymentActivityVM.class);
        verifyPaymentActivityVM.init(this);
        initData();
    }

    private void initData() {
        dropInRequest = new DropInRequest();
        dropInRequest.setCardDisabled(true);
        dropInRequest.setVenmoDisabled(true);
        dropInRequest.setGooglePayDisabled(true);

        client = new DropInClient(this, new ClientTokenProvider() {
            @Override
            public void getClientToken(@NonNull ClientTokenCallback callback) {
                callback.onSuccess(clientToken);
            }
        });

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.rlPaypal.setOnClickListener(v -> {
//            verifyPaymentActivityVM.verifyPaypal();
            verifyPaymentActivityVM.generateBrantreeToken();
        });

        verifyPaymentActivityVM.getVerifySuccessUrl().observe(this, url -> {
            getProfile();
            finish();
        });

        verifyPaymentActivityVM.getIsShow().observe(this, isShow -> {
            disableEnableTouch(isShow);
            if (isShow) {
                binding.rlPaypal.setBackgroundResource(R.drawable.transp_rounded_corner_10);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.rlPaypal.setBackgroundResource(R.drawable.white_rounded_corner_10);
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        binding.rlPaynoeer.setOnClickListener(view -> showAddAccountDialog(this));

        verifyPaymentActivityVM.getAddPaySuccess().observe(this, isShow -> {
            if (isShow) {
                getProfile();
                finish();
            }
        });

        verifyPaymentActivityVM.getGenerateTokenSuccess().observe(this, token -> {
            clientToken=token;
            DropInClient dropInClient = client;
            dropInClient.invalidateClientToken();
            dropInClient.setListener(new DropInListener() {
                @Override
                public void onDropInSuccess(@NonNull DropInResult dropInResult) {
                    String paymentMethodNonce = dropInResult.getPaymentMethodNonce().getString();
                    Log.e("NONCE ", "-------------- " + paymentMethodNonce);
                    verifyPaymentActivityVM.verifyPaypal(paymentMethodNonce);
                }

                @Override
                public void onDropInFailure(@NonNull Exception error) {

                    Log.e("error ", "-------------- " + error);
                }
            });
            dropInClient.launchDropIn(dropInRequest);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    void showAddAccountDialog(BaseActivity activity) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        DialogAddEmailBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity.getApplicationContext()), R.layout.dialog_add_email, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(true);

        binding.imgClose.setOnClickListener(view ->
                dialog.dismiss());

        binding.tvContinue.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                binding.etEmail.setError(getString(R.string.enter_valid_email));
                return;
            }
            if (!isValidEmail(binding.etEmail.getText().toString().trim())) {
                binding.etEmail.setError(getString(R.string.enter_valid_email));
                return;
            }
            binding.etEmail.setError(null);
            dialog.dismiss();
            verifyPaymentActivityVM.addPayonnerAccount(binding.etEmail.getText().toString());
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }
}

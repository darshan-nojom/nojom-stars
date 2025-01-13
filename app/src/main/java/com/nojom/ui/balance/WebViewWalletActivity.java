package com.nojom.ui.balance;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nojom.R;
import com.nojom.databinding.ActivityWebViewBinding;
import com.nojom.databinding.DialogPayDoneBinding;
import com.nojom.model.User;
import com.nojom.ui.BaseActivity;

import java.util.HashMap;
import java.util.Objects;

public class WebViewWalletActivity extends BaseActivity {

    private ActivityWebViewBinding binding;
    private String paymentUrl, intentData, bal;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        paymentUrl = getIntent().getStringExtra("url");
        bal = getIntent().getStringExtra("bal");
        intentData = getIntent().getStringExtra("intent");
//        campId = getIntent().getStringExtra("campId");
        // Configure WebView settings
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript if needed

        // Load a URL in WebView
        binding.webView.setWebViewClient(new WebViewClient());
        binding.webView.loadUrl(paymentUrl);

        User user = new User(getUserID(), intentData);
        Log.e("data--", "intent " + intentData + " " + user.id + " " + intentData);
        mDatabase.child("payments").child(intentData).setValue(user);
        mDatabase.addValueEventListener(postListener);

        binding.imgBack.setOnClickListener(view -> onBackPressed());
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
//            Post post = dataSnapshot.getValue(Post.class);
            // ..
            Log.e("snap", dataSnapshot.getValue().toString());
            HashMap hashMap = (HashMap) dataSnapshot.getValue();
            HashMap map = (HashMap) hashMap.get("payments");
            if (map != null) {
                HashMap hm = (HashMap) map.get(intentData);
                if (hm != null) {
                    try {
                        String status = (String) hm.get("status");
                        String cId = (String) hm.get("campaign_id");

                        if (intentData.equals(cId) && status != null) {
                            switch (status) {
                                case "success":
                                    postDoneDialog(true);
                                    break;
                                case "pending":
                                    break;
                                case "failed":
                                    postDoneDialog(false);
                                    break;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.e("TAG", "loadPost:onCancelled", databaseError.toException());
        }
    };

    @Override
    public void onBackPressed() {
        // Handle back button to navigate within WebView history
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void postDoneDialog(boolean isSuccess) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(null);
        DialogPayDoneBinding bindingDialog = DialogPayDoneBinding.inflate(LayoutInflater.from(this));
        dialog.setContentView(bindingDialog.getRoot());
        dialog.setCancelable(true);

        if (!isSuccess) {
//            bindingDialog.imgDone.setImageResource(R.drawable.ic_pay_fail);
//            bindingDialog.txtTitle.setText(getString(R.string.payment_failed));
//            bindingDialog.txtDesc.setText(getString(R.string.unfortunately_we_were_unable_to_process_your_payment_please_try_again_or_use_a_different_payment_method) + "\n");
//            bindingDialog.txtDesc1.setText(getString(R.string.if_you_continue_to_experience_issues_our_support_team_is_here_to_help));
//            bindingDialog.btnContinuePrice.setText(getString(R.string.try_again));
        } else {
            bindingDialog.txtTitle.setText(getString(R.string.successful));
            bindingDialog.txtDesc.setText(getString(R.string.your_wallet_has_been_charged_successfully_new_balance) + bal + " " + getString(R.string.sar));
            bindingDialog.txtDesc1.setText("");
            bindingDialog.btnContinuePrice.setText(getString(R.string.view_balance));
        }

        bindingDialog.btnContinuePrice.setOnClickListener(view -> {
            if (isSuccess) {
                dialog.dismiss();
                AddBalanceActivity.sActivity.finish();
                finish();
            } else {
                dialog.dismiss();
                finish();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        lp.gravity = Gravity.CENTER;
        lp.width = (int) (displaymetrics.widthPixels * 0.9);
        lp.height = (int) (displaymetrics.heightPixels * 0.7);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        dialog.setOnDismissListener(dialog1 -> {
            isClickableView = false;
            if (isSuccess) {
                dialog.dismiss();
                AddBalanceActivity.sActivity.finish();
                finish();
            } else {
                dialog.dismiss();
                finish();
            }
        });
    }
}

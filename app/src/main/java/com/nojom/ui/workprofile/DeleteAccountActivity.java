package com.nojom.ui.workprofile;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.databinding.ActivityDeleteAccountBinding;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;

import java.util.Objects;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class DeleteAccountActivity extends BaseActivity implements APIRequest.APIRequestListener {
    private ActivityDeleteAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_delete_account);
        initData();
    }

    private void initData() {
        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvTitle.setText(getString(R.string.data_privacy));

        binding.rlDeleteAccount.setOnClickListener(v -> deleteAccountDialog());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
    }

    private Dialog dialogBlock;
    private TextView tvConfirm;
    private CircularProgressBar progressBarBlock;

    void deleteAccountDialog() {
        dialogBlock = new Dialog(this);
        dialogBlock.setTitle(null);
        dialogBlock.setContentView(R.layout.dialog_refund_reason);
        dialogBlock.setCancelable(true);

        tvConfirm = dialogBlock.findViewById(R.id.tv_submit);
        progressBarBlock = dialogBlock.findViewById(R.id.progress_bar);
        TextView tvCancel = dialogBlock.findViewById(R.id.tv_cancel);
        TextView etReason = dialogBlock.findViewById(R.id.edit_reason);
        TextView tvTitle = dialogBlock.findViewById(R.id.tv_title);
        TextView txt1 = dialogBlock.findViewById(R.id.txt1);
        TextView txt2 = dialogBlock.findViewById(R.id.txt2);
        RadioGroup radioGroup = dialogBlock.findViewById(R.id.radioGroup);
        RadioButton rb1 = dialogBlock.findViewById(R.id.rb_inappropriate);
        RadioButton rb2 = dialogBlock.findViewById(R.id.rb_irrelevant);
        RadioButton rb4 = dialogBlock.findViewById(R.id.rb_other);
        RadioButton rb3 = dialogBlock.findViewById(R.id.rb_scam);
        RelativeLayout relSubmit = dialogBlock.findViewById(R.id.rel_submit);
        relSubmit.setBackgroundResource(R.drawable.red_button_bg);
        tvConfirm.setText(getString(R.string.delete));

        radioGroup.clearCheck();

        etReason.setHint(getString(R.string.write_your_reason));
        txt2.setVisibility(View.INVISIBLE);

        tvTitle.setText(getString(R.string.delete_account_heading));
        txt1.setText(getString(R.string.let_us_know_why_delete_account));
        rb1.setText(getString(R.string.bad_exp_with_app));
        rb2.setText(getString(R.string.found_batter_alternative));
        rb3.setText(getString(R.string.delete_account_for_no_reason));

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = group.findViewById(checkedId);
            if (null != rb) {
                if (rb.getText().equals(getString(R.string.other))) {
                    etReason.setVisibility(VISIBLE);
                } else {
                    etReason.setVisibility(GONE);
                }
            }
        });

        tvConfirm.setOnClickListener(v -> {
            RadioButton rb = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
            String selectedReason = "";
            boolean isOtherSelect = false;
            if (rb != null && !TextUtils.isEmpty(rb.getText())) {
                if (rb.getText().equals(getString(R.string.other))) {
                    selectedReason = etReason.getText().toString();
                    isOtherSelect = true;
                } else {
                    selectedReason = rb.getText().toString();
                    isOtherSelect = false;
                }
            }
            if (TextUtils.isEmpty(selectedReason)) {
                toastMessage(getString(R.string.please_select_reason));
                return;
            }

            deleteOwnAccount(selectedReason);
        });

        tvCancel.setOnClickListener(v -> {
            radioGroup.clearCheck();
            dialogBlock.dismiss();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialogBlock.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.TOP;
        dialogBlock.show();
        dialogBlock.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBlock.getWindow().setAttributes(lp);
    }

    void deleteOwnAccount(String reason) {
        if (!isNetworkConnected())
            return;

        isClickableView = true;
        progressBarBlock.setVisibility(VISIBLE);
        tvConfirm.setVisibility(View.INVISIBLE);

        CommonRequest.DeleteUserAccount logout = new CommonRequest.DeleteUserAccount();
        logout.setReason(reason);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(this, API_DELETE_ACCOUNT, logout.toString(), true, this);
    }



    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_DELETE_ACCOUNT)) {
            if (dialogBlock != null) {
                dialogBlock.dismiss();
            }
            isClickableView = false;
            toastMessage(msg);
            logout();
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        isClickableView = false;
        progressBarBlock.setVisibility(GONE);
        tvConfirm.setVisibility(VISIBLE);
        toastMessage(message);
    }
}

package com.nojom.ui.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.databinding.ActivityUsernameBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.GeneralModel;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.workprofile.NameActivityVM;
import com.nojom.util.Constants;

import java.util.Objects;

import retrofit2.Response;

public class UsernameActivity extends BaseActivity implements ResponseListener, BaseActivity.OnProfileLoadListener {

    private ActivityUsernameBinding binding;
    private NameActivityVM nameActivityVM;
    String PREFIX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_username);
        nameActivityVM = ViewModelProviders.of(this).get(NameActivityVM.class);
        nameActivityVM.setNameActivityListener(this);
        setOnProfileLoadListener(this);
        if (language.equals("ar")) {
            setArFont(binding.tv1, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tv2, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv3, Constants.FONT_AR_REGULAR);
            setArFont(binding.txtStatus, Constants.FONT_AR_BOLD);
            setArFont(binding.txtSkip, Constants.FONT_AR_MEDIUM);
            setArFont(binding.btnLogin, Constants.FONT_AR_BOLD);
            setArFont(binding.etUsername, Constants.FONT_AR_REGULAR);
        }
        initData();
    }

    private void initData() {
        PREFIX = getString(R.string.nojom_com_e);
        binding.etUsername.setHint(getString(R.string.username_e).toLowerCase());
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.txtSkip.setOnClickListener(view -> {
//            setOnProfileLoadListener(null);
            nameActivityVM.updateProfile(this, "", RS_7_DASHBOARD, 2);
        });

        binding.relLogin.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(getUsername())) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnLogin.setVisibility(View.INVISIBLE);
                nameActivityVM.updateProfile(this, getUsername(), RS_7_DASHBOARD, 2);
            }
        });

        binding.etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    // If user tries to change the base URL, reset it
//                    binding.etUsername.setText(PREFIX);
//                    binding.etUsername.setSelection(binding.etUsername.getText().length());
                    DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(UsernameActivity.this, R.color.C_E5E5EA));
                    binding.btnLogin.setTextColor(getResources().getColor(R.color.c_AEAEB2));
                } else {
                    // Remove any text after the base URL
//                    String remainingText = input.substring(PREFIX.length());
//                    if (remainingText.contains("/")) {
//                        String updatedText = PREFIX + remainingText.split("/")[0];
//                        binding.etUsername.setText(updatedText);
//                        binding.etUsername.setSelection(updatedText.length());
//                    }
                    binding.btnLogin.setTextColor(getResources().getColor(R.color.white));
                    DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(UsernameActivity.this, R.color.black));
                }
            }
        });
    }

    @Override
    public void onResponseSuccess(Response<GeneralModel> response) {
        getProfile();
    }

    @Override
    public void onError() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        gotoMain(Constants.TAB_HOME);
        binding.btnLogin.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    public String getUsername() {
        return Objects.requireNonNull(binding.etUsername.getText()).toString().trim().replaceAll(PREFIX, "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

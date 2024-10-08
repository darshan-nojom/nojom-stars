package com.nojom.ui.workprofile;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.nojom.R;
import com.nojom.databinding.ActivityNameNewBinding;
import com.nojom.interfaces.ResponseListener;
import com.nojom.model.GeneralModel;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.LoginActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.Objects;

import retrofit2.Response;

public class NewNameActivity extends BaseActivity implements ResponseListener, BaseActivity.OnProfileLoadListener {
    private NameActivityVM nameActivityVM;
    private ActivityNameNewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_name_new);
        nameActivityVM = ViewModelProviders.of(this).get(NameActivityVM.class);
        nameActivityVM.setNameActivityListener(this);
        setOnProfileLoadListener(this);
        if (language.equals("ar")) {
            setArFont(binding.tv1, Constants.FONT_AR_MEDIUM);
            setArFont(binding.tv2, Constants.FONT_AR_REGULAR);
            setArFont(binding.tv3, Constants.FONT_AR_REGULAR);
//            setArFont(binding.txtStatus, Constants.FONT_AR_BOLD);
            setArFont(binding.txtSkip, Constants.FONT_AR_MEDIUM);
            setArFont(binding.etName, Constants.FONT_AR_REGULAR);
            setArFont(binding.etArName, Constants.FONT_AR_REGULAR);
            setArFont(binding.btnLogin, Constants.FONT_AR_BOLD);
        }
        binding.etArName.setFilters(new InputFilter[]{new ArabicInputFilter()});
        binding.etName.setFilters(new InputFilter[]{new EnglishInputFilter()});
        initData();
    }

    private void initData() {
        //binding.toolbar.tvCancel.setOnClickListener(v -> gotoMainActivity(Constants.TAB_HOME));
        //binding.toolbar.progress.setProgress(0);
        Utils.trackFirebaseEvent(this, "First_Last_Name_Screen");

        binding.etName.addTextChangedListener(watcher);
        //binding.etArName.addTextChangedListener(watcher);

        binding.relLogin.setOnClickListener(view -> {
            if (isValid()) {
                binding.btnLogin.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                nameActivityVM.updateName(this, getFirstName(), getLastName(), RS_2_LOCATION);
            }
        });

        binding.txtSkip.setOnClickListener(view -> {
            //setOnProfileLoadListener(null);
            nameActivityVM.updateName(this, "", "", RS_2_LOCATION);
            //redirectActivity(LocationActivity.class);
        });
        binding.imgBack.setOnClickListener(view -> onBackPressed());
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (isValid()) {
                DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(NewNameActivity.this, R.color.black));
                binding.btnLogin.setTextColor(getResources().getColor(R.color.white));
            } else {
                DrawableCompat.setTint(binding.relLogin.getBackground(), ContextCompat.getColor(NewNameActivity.this, R.color.C_E5E5EA));
                binding.btnLogin.setTextColor(getResources().getColor(R.color.c_AAAAAC));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private boolean isValid() {
        if (TextUtils.isEmpty(binding.etName.getText().toString().trim())) {
            return false;
        }
        if (binding.etName.getText().toString().length() < 3) {
            return false;
        }
        if (!TextUtils.isEmpty(binding.etArName.getText().toString().trim())) {
            if (binding.etArName.getText().toString().length() < 3) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishToRight();
        setOnProfileLoadListener(null);
//        nameActivityVM.updateName(this, "", "", RS_2_LOCATION);
        redirectActivity(LoginActivity.class);
        finish();
    }

//    public void onClickNext() {
//        if (nameActivityVM.isValid(this, getFirstName(), getLastName())) {
//            binding.btnLogin.setVisibility(View.INVISIBLE);
//            binding.progressBar.setVisibility(View.VISIBLE);
//            nameActivityVM.updateName(this, getFirstName(), getLastName());
//        }
//    }

    public String getFirstName() {
        return Objects.requireNonNull(binding.etName.getText()).toString().trim();
    }

    public String getLastName() {
        return Objects.requireNonNull(binding.etArName.getText()).toString().trim();
    }

    @Override
    public void onResponseSuccess(Response<GeneralModel> response) {
        getProfile();
    }

    @Override
    public void onError() {
        binding.btnLogin.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onProfileLoad(ProfileResponse data) {
        redirectActivity(LocationActivity.class);
        //gotoMainActivity(Constants.TAB_HOME);
        binding.btnLogin.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }


}

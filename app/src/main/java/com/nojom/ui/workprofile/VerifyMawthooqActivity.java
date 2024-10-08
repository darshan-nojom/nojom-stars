package com.nojom.ui.workprofile;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.R;
import com.nojom.adapter.VerifyFilesAdapter;
import com.nojom.databinding.ActivityMawthooqNewBinding;
import com.nojom.databinding.ActivityVerifyIdBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.MawthouqStatus;
import com.nojom.model.VerifyID;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.activity.NormalFilePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.multitypepicker.filter.entity.NormalFile;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.auth.ResetPasswordDoneActivity;
import com.nojom.util.Utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VerifyMawthooqActivity extends BaseActivity implements VerifyFilesAdapter.DeleteSuccessListener {

    private VerifyIDActivityVM verifyIDActivityVM;
    private ActivityMawthooqNewBinding binding;
    private VerifyFilesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mawthooq_new);
        verifyIDActivityVM = ViewModelProviders.of(this).get(VerifyIDActivityVM.class);
        initData();
    }

    String screen = null;

    private void initData() {

        binding.etMaNoNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().isEmpty()) {
//                    binding.tvSubmit.setVisibility(View.GONE);
//                } else {
//                    binding.tvSubmit.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.txtUpdate.setOnClickListener(view -> {
            binding.txtUpdate.setVisibility(View.INVISIBLE);
            binding.relNewNo.setVisibility(View.VISIBLE);
            binding.relPass.setVisibility(View.VISIBLE);
            binding.relBtn.setVisibility(View.VISIBLE);
            binding.tvSubmit.setTextColor(getResources().getColor(R.color.C_F2F2F7));
        });

        binding.imgBack.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        binding.tvSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etMaNoNew.getText().toString().trim())) {
                return;
            }
            if (TextUtils.isEmpty(binding.etMaNoPas.getText().toString().trim())) {
                return;
            }
            Utils.hideSoftKeyboard(this);
            verifyIDActivityVM.submitMawthouq(this, binding.etMaNo.getText().toString(), binding.etMaNoNew.getText().toString(), binding.etMaNoPas.getText().toString());
        });


        verifyIDActivityVM.getMawthooqStatus(this);

//        verifyIDActivityVM.getListMutableLiveData().observe(this, this::setAdapter);
        verifyIDActivityVM.getMawthouqStatusMutableLiveData().observe(this, this::setAdapter);

        verifyIDActivityVM.getIsShowProgress().observe(this, isShow -> {
            isClickableView = isShow;
            if (isShow) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.tvSubmit.setVisibility(View.INVISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.tvSubmit.setVisibility(View.VISIBLE);
            }
        });
    }

    public void checkPermission(final boolean isDocument) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    if (isDocument) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            openDocuments(VerifyMawthooqActivity.this, 1);
                        } else {
                            Intent intent = new Intent(VerifyMawthooqActivity.this, NormalFilePickActivity.class);
                            intent.putExtra(Constant.MAX_NUMBER, 1);
                            intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                            startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                        }
                    } else {
                        Intent intent = new Intent(VerifyMawthooqActivity.this, ImagePickActivity.class);
                        intent.putExtra(IS_NEED_CAMERA, true);
                        intent.putExtra(Constant.MAX_NUMBER, 5);
                        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
                    }
                }

                if (report.isAnyPermissionPermanentlyDenied()) {
                    toastMessage(getString(R.string.please_give_permission));
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread().check();
    }


    private void setAdapter(MawthouqStatus verifyIdsList) {

        if (verifyIdsList != null) {

            if (!TextUtils.isEmpty(verifyIdsList.mawthooq_number)) {
                binding.etMaNo.setText(verifyIdsList.mawthooq_number);
                binding.relOld.setVisibility(View.VISIBLE);
            } else {
                binding.relOld.setVisibility(View.GONE);
                binding.relNewNo.setVisibility(View.VISIBLE);
                binding.relPass.setVisibility(View.VISIBLE);
                binding.txtUpdate.setVisibility(View.GONE);
                binding.relBtn.setVisibility(View.VISIBLE);
            }
        } else {
            binding.relOld.setVisibility(View.GONE);
            binding.relNewNo.setVisibility(View.VISIBLE);
            binding.relPass.setVisibility(View.VISIBLE);
            binding.txtUpdate.setVisibility(View.GONE);
            binding.relBtn.setVisibility(View.VISIBLE);
            binding.etMaNo.setText("");
        }
    }


    @Override
    public void onDeleteSuccess(boolean isDelete) {
        getProfile();
        if (screen != null && screen.equals("maw")) {
            verifyIDActivityVM.getMawthooqStatus(this);
        } else {
            verifyIDActivityVM.getVerifyIdsList(this);
        }
    }
}

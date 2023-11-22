package com.nojom.ui.workprofile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.karumi.dexter.MultiplePermissionsReport;
import com.nojom.R;
import com.nojom.adapter.VerifyFilesAdapter;
import com.nojom.databinding.ActivityVerifyIdBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.VerifyID;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;

public class VerifyIDActivity extends BaseActivity implements PermissionListener {

    private VerifyIDActivityVM verifyIDActivityVM;
    private ActivityVerifyIdBinding binding;
    private VerifyFilesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_id);
        verifyIDActivityVM = ViewModelProviders.of(this).get(VerifyIDActivityVM.class);
        initData();
    }

    String screen = null;

    private void initData() {

        if (getIntent() != null) {
            screen = getIntent().getStringExtra("screen");
            if (screen != null && screen.equals("maw")) {
                binding.txtIdTitle.setText(getString(R.string.upload_mawthooq));
                binding.txtDesc.setText(getString(R.string.please_provide_your_photo_for_your_mawthooq_so_that_we_can_verify_your_account_this_mawthooq_will_be_kept_private_and_will_be_used_for_verification_purposes));
                binding.tvSubmit.setText(getString(R.string.upload_mawthooq));
            }
        }

        binding.toolbar.imgBack.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        binding.tvAttachFile.setOnClickListener(v -> checkPermission());
        binding.tvSubmit.setOnClickListener(v -> {
        });

        binding.rvFiles.setLayoutManager(new LinearLayoutManager(this));
        verifyIDActivityVM.getVerifyIdsList(this);

        verifyIDActivityVM.getListMutableLiveData().observe(this, this::setAdapter);

        verifyIDActivityVM.getIsShowProgress().observe(this, isShow -> {
            isClickableView = isShow;
            if (isShow) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void checkPermission() {
        com.nojom.util.PermissionRequest permissionRequest = new com.nojom.util.PermissionRequest();
        permissionRequest.setPermissionListener(this);
        permissionRequest.checkStorageCameraRequest(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                try {
                    ArrayList<ImageFile> imgPaths = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    if (imgPaths != null && imgPaths.size() > 0 && imgPaths.get(0).getPath() != null) {
                        if (screen != null) {
                            //TODO: set API for upload mawthooq
                        } else {
                            verifyIDActivityVM.verifyId(new File(imgPaths.get(0).getPath()), this);
                        }

                    } else {
                        toastMessage(getString(R.string.file_not_selected));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setAdapter(List<VerifyID.Data> verifyIdsList) {
        if (verifyIdsList != null && verifyIdsList.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new VerifyFilesAdapter(this);
            }
            mAdapter.doRefresh(verifyIdsList);
            if (binding.rvFiles.getAdapter() == null) {
                binding.rvFiles.setAdapter(mAdapter);
            }
        } else {
            if (mAdapter != null) {
                mAdapter.doRefresh(verifyIdsList);
            }
        }
    }

    @Override
    public void onPermissionGranted(MultiplePermissionsReport report) {
        Intent intent = new Intent(this, ImagePickActivity.class);
        intent.putExtra(IS_NEED_CAMERA, true);
        intent.putExtra(Constant.MAX_NUMBER, 1);
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
    }
}

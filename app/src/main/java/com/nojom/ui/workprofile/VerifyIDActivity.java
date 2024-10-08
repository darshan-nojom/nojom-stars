package com.nojom.ui.workprofile;

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
import com.nojom.databinding.ActivityVerifyIdBinding;
import com.nojom.interfaces.PermissionListener;
import com.nojom.model.Attachment;
import com.nojom.model.VerifyID;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.activity.NormalFilePickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.multitypepicker.filter.entity.NormalFile;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;

public class VerifyIDActivity extends BaseActivity implements PermissionListener, VerifyFilesAdapter.DeleteSuccessListener {

    private VerifyIDActivityVM verifyIDActivityVM;
    private ActivityVerifyIdBinding binding;
    private VerifyFilesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
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
                binding.tvSubmit.setText(getString(R.string.save));

                binding.tvOr.setVisibility(View.VISIBLE);
                binding.txtMawTitle.setVisibility(View.VISIBLE);
                binding.etMawId.setVisibility(View.VISIBLE);

                binding.etMawId.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().isEmpty()) {
                            binding.tvSubmit.setVisibility(View.GONE);
                        } else {
                            binding.tvSubmit.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }

        binding.toolbar.imgBack.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
        binding.tvAttachFile.setOnClickListener(v -> selectFileDialog());
        binding.tvSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etMawId.getText().toString().trim())) {
                return;
            }

            if (screen != null && screen.equals("maw")) {
                verifyIDActivityVM.verifyId(null, this, 4, binding.etMawId.getText().toString());
            }
            //upload ID on server we need API over here
        });

        binding.rvFiles.setLayoutManager(new LinearLayoutManager(this));
        if (screen != null && screen.equals("maw")) {
            verifyIDActivityVM.getMawthooqList(this, screen);
        } else {
            verifyIDActivityVM.getVerifyIdsList(this);
        }

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

    public void checkPermission(final boolean isDocument) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    if (isDocument) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            openDocuments(VerifyIDActivity.this, 1);
                        } else {
                            Intent intent = new Intent(VerifyIDActivity.this, NormalFilePickActivity.class);
                            intent.putExtra(Constant.MAX_NUMBER, 1);
                            intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                            startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                        }
                    } else {
                        Intent intent = new Intent(VerifyIDActivity.this, ImagePickActivity.class);
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

    File selectedMawFile;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE) {
//            if (resultCode == RESULT_OK && data != null) {
//                try {
//                    ArrayList<ImageFile> imgPaths = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
//                    if (imgPaths != null && imgPaths.size() > 0 && imgPaths.get(0).getPath() != null) {
//                        if (screen != null && screen.equals("maw")) {
//                            verifyIDActivityVM.verifyId(new File(imgPaths.get(0).getPath()), this, 4, "");
//                        } else {
//                            verifyIDActivityVM.verifyId(new File(imgPaths.get(0).getPath()), this, 1, "");
//                        }
//
//                    } else {
//                        toastMessage(getString(R.string.file_not_selected));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }


        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<NormalFile> docPaths = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    if (docPaths != null && docPaths.size() > 0) {
                        Log.e("Doc Path == > ", docPaths.get(0).getPath());
                        if (screen != null && screen.equals("maw")) {
                            selectedMawFile=new File(docPaths.get(0).getPath());
                            verifyIDActivityVM.verifyId(selectedMawFile, this, 4, "");
                        } else {
                            verifyIDActivityVM.verifyId(new File(docPaths.get(0).getPath()), this, 1, "");
                        }

                    } else {
                        toastMessage(getString(R.string.file_not_selected));
                    }
                }
                break;
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<ImageFile> imgPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    if (imgPath != null && imgPath.size() > 0) {
                        Log.e("Image Path == > ", imgPath.get(0).getPath());
                        if (screen != null && screen.equals("maw")) {
                            verifyIDActivityVM.verifyId(new File(imgPath.get(0).getPath()), this, 4, "");
                        } else {
                            verifyIDActivityVM.verifyId(new File(imgPath.get(0).getPath()), this, 1, "");
                        }

                    } else {
                        toastMessage(getString(R.string.file_not_selected));
                    }
                }
                break;
            case 4545://doc picker for android 10+
                try {
//                    String fileName = getFileName(data.getData());
                    String path = null;
                    if (data != null && data.getData() != null) {
                        path = Utils.getFilePath(this, data.getData());
                    }

                    if (path != null) {
                        Log.e("Doc Path == > ", path);
                        if (screen != null && screen.equals("maw")) {
                            verifyIDActivityVM.verifyId(new File(path), this, 4, "");
                        } else {
                            verifyIDActivityVM.verifyId(new File(path), this, 1, "");
                        }
                    } else {
                        toastMessage(getString(R.string.file_not_selected));
                    }

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setAdapter(List<VerifyID.Data> verifyIdsList) {

        if (verifyIdsList != null && verifyIdsList.size() > 0) {

            if (verifyIdsList.get(0).is_number.equals("1")) {
                binding.etMawId.setText(verifyIdsList.get(0).data);
                binding.etMawId.setVisibility(View.VISIBLE);
                binding.tvOr.setVisibility(View.VISIBLE);
                binding.tvSubmit.setVisibility(View.VISIBLE);
            } else {
                binding.etMawId.setText("");
                binding.etMawId.setVisibility(View.VISIBLE);
                binding.tvOr.setVisibility(View.VISIBLE);
                binding.tvSubmit.setVisibility(View.GONE);
            }
            if (mAdapter == null) {
                mAdapter = new VerifyFilesAdapter(this, screen, VerifyIDActivity.this);
            }
            mAdapter.doRefresh(verifyIdsList);
            if (binding.rvFiles.getAdapter() == null) {
                binding.rvFiles.setAdapter(mAdapter);
            }
            binding.relReview.setVisibility(View.VISIBLE);
        } else {
            binding.relReview.setVisibility(View.GONE);
            binding.etMawId.setText("");
            binding.etMawId.setVisibility(View.VISIBLE);
            binding.tvOr.setVisibility(View.VISIBLE);
            binding.tvSubmit.setVisibility(View.GONE);

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

    @Override
    public void onDeleteSuccess(boolean isDelete) {
        getProfile();
        if (screen != null && screen.equals("maw")) {
            verifyIDActivityVM.getMawthooqList(this, screen);
        } else {
            verifyIDActivityVM.getVerifyIdsList(this);
        }
    }

    void selectFileDialog() {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_camera_document_select);
        dialog.setCancelable(true);
        TextView tvCancel = dialog.findViewById(R.id.btn_cancel);
        LinearLayout llCamera = dialog.findViewById(R.id.ll_camera);
        LinearLayout llDocument = dialog.findViewById(R.id.ll_document);

        llCamera.setOnClickListener(v -> {
            checkPermission(false);
            dialog.dismiss();
        });

        llDocument.setOnClickListener(v -> {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                    activity.openDocuments(activity, 5);
//                } else { //request for the permission
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
//                    intent.setData(uri);
//                    activity.startActivity(intent);
//                }
//            } else {
            checkPermission(true);
//            }

            dialog.dismiss();
        });

        tvCancel.setOnClickListener(v -> dialog.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }
}

package com.nojom.ui.helper;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.R;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.activity.VideoPickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.multitypepicker.filter.entity.NormalFile;
import com.nojom.multitypepicker.filter.entity.VideoFile;
import com.nojom.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ImagePickerActivity extends BaseActivity {

    public void selectImages() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(ImagePickerActivity.this, ImagePickActivity.class);
                            intent.putExtra(IS_NEED_CAMERA, true);
                            intent.putExtra(Constant.MAX_NUMBER, 1);
                            startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            toastMessage(getString(R.string.please_give_permission));
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    public void selectVideos() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(ImagePickerActivity.this, VideoPickActivity.class);
                            intent.putExtra(IS_NEED_CAMERA, true);
                            intent.putExtra(Constant.MAX_NUMBER, 1);
                            startActivityForResult(intent, Constant.REQUEST_CODE_PICK_VIDEO);
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            toastMessage(getString(R.string.please_give_permission));
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    public void selectDocuments() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
//                            Intent intent = new Intent(ImagePickerActivity.this, NormalFilePickActivity.class);
//                            intent.putExtra(Constant.MAX_NUMBER, 3);
//                            intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                            }
//                            startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                            //openDocuments(this);
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            toastMessage(getString(R.string.please_give_permission));
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<ImageFile> imgPaths = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    if (imgPaths != null && imgPaths.size() > 0) {
                        Log.e("Image path", imgPaths.get(0).getPath());
                        onImageSelect(imgPaths);
                    }
                }
                break;
            case Constant.REQUEST_CODE_PICK_VIDEO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<VideoFile> videoPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                    if (videoPath != null && videoPath.size() > 0) {
                        Log.e("Video path", videoPath.get(0).getPath());
                        onVideoSelect(videoPath);
                    }
                }
                break;
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<NormalFile> docPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    if (docPath != null && docPath.size() > 0) {
                        Log.e("Doc path", docPath.get(0).getPath());
                        onDocumentSelect(docPath);
                    }
                }
                break;
        }
    }

    public void selectFileDialog() {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(this, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_file_open);
        dialog.setCancelable(true);
        TextView tvCancel = dialog.findViewById(R.id.btn_cancel);
        LinearLayout llCamera = dialog.findViewById(R.id.ll_camera);
        LinearLayout llGallery = dialog.findViewById(R.id.ll_gallery);
        LinearLayout llDocument = dialog.findViewById(R.id.ll_document);

        llCamera.setOnClickListener(v -> {
            selectImages();
            dialog.dismiss();
        });

        llGallery.setOnClickListener(v -> {
            selectVideos();
            dialog.dismiss();
        });

        llDocument.setOnClickListener(v -> {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                    selectDocuments();
//                } else { //request for the permission
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                    Uri uri = Uri.fromParts("package", getPackageName(), null);
//                    intent.setData(uri);
//                    startActivity(intent);
//                }
//            } else {
                selectDocuments();
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

    public abstract void onImageSelect(ArrayList<ImageFile> imgPaths);

    public abstract void onVideoSelect(ArrayList<VideoFile> videoPath);

    public abstract void onDocumentSelect(ArrayList<NormalFile> docPath);
}

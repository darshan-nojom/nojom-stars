package com.nojom.ui.projects;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;
import static com.nojom.util.Constants.API_ADD_FILES;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.Attachment;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.activity.NormalFilePickActivity;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SubmitJobActivityVM extends ViewModel implements APIRequest.APIRequestListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;

    private MutableLiveData<ArrayList<Attachment>> listMutableLiveData;
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<ArrayList<Attachment>> getListMutableLiveData() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        return listMutableLiveData;
    }

    void init(BaseActivity activity) {
        this.activity = activity;
    }


    void submitJob(String projectBidId, String descriptionTxt) {
        if (!activity.isNetworkConnected()) return;

        getIsShowProgress().postValue(true);

        MultipartBody.Part[] body = null;
        long fileSize = 0;
        ArrayList<Attachment> fileList = getListMutableLiveData().getValue();
        if (fileList != null && fileList.size() > 0) {
            body = new MultipartBody.Part[fileList.size()];
            for (int i = 0; i < fileList.size(); i++) {
                File file;
                if (fileList.get(i).filepath.contains(".jpg") || fileList.get(i).filepath.contains(".png") || fileList.get(i).filepath.contains(".jpeg")) {
                    file = CompressFile.getCompressedImageFile(new File(fileList.get(i).filepath));
                } else {
                    file = new File(fileList.get(i).filepath);
                }

                if (file != null) {
                    Uri selectedUri = Uri.fromFile(file);
                    String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());

                    RequestBody requestFile = null;
                    if (mimeType != null) {
                        requestFile = RequestBody.create(file, MediaType.parse(mimeType));
                    }

                    Headers.Builder headers = new Headers.Builder();
                    headers.addUnsafeNonAscii("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\"");

                    if (requestFile != null) {
                        body[i] = MultipartBody.Part.create(headers.build(), requestFile);
                    }

                    fileSize += getFolderSize(file);
                }
//                Log.e("MIME TYPE", "" + mimeType + " ----- " + MediaType.parse(mimeType));
            }
        }

        if (fileSize > 6) {
            activity.toastMessage(activity.getString(R.string.file_size_validation));
            getIsShowProgress().postValue(false);
            return;
        }

        CommonRequest.AddFile addFile = new CommonRequest.AddFile();
        addFile.setDescription(descriptionTxt);
        addFile.setJob_post_bid_id(projectBidId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_ADD_FILES, addFile.toString(), this, body);

    }

    void selectFileDialog() {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(activity, R.style.Theme_Design_Light_BottomSheetDialog);
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

    public void checkPermission(final boolean isDocument) {
        Dexter.withActivity(activity).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    if (isDocument) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            activity.openDocuments(activity, 1);
                        } else {
                            Intent intent = new Intent(activity, NormalFilePickActivity.class);
                            intent.putExtra(Constant.MAX_NUMBER, 5);
                            intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                            activity.startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                        }
                    } else {
                        Intent intent = new Intent(activity, ImagePickActivity.class);
                        intent.putExtra(IS_NEED_CAMERA, true);
                        intent.putExtra(Constant.MAX_NUMBER, 5);
                        activity.startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
                    }
                }

                if (report.isAnyPermissionPermanentlyDenied()) {
                    activity.toastMessage(activity.getString(R.string.please_give_permission));
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread().check();
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        try {
            activity.toastMessage(msg);
            Preferences.writeBoolean(activity, Constants.SUBMIT_FILE_DONE, true);
            getIsShowProgress().postValue(false);
            activity.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
    }

    public long getFolderSize(File file) {
        long size = 0;
        try {
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    size += getFolderSize(child);
                }
            } else {
                size = file.length();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        long fileSizeInKB = size / 1024;
        //  Convert the KB to MegaBytes (1 MB = 1024 KBytes)

        return fileSizeInKB / 1024;//in mb
    }
}

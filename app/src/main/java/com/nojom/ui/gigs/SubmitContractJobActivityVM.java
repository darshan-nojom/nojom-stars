package com.nojom.ui.gigs;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;
import static com.nojom.util.Constants.API_SUBMIT_CONTRACT_JOB;

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
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.activity.NormalFilePickActivity;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SubmitContractJobActivityVM extends ViewModel implements APIRequest.JWTRequestResponseListener {
    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private int clientId;
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


    void submitJob(int contractId, String descriptionTxt) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(true);

        MultipartBody.Part[] body = null;
        ArrayList<Attachment> fileList = getListMutableLiveData().getValue();
        if (fileList != null && fileList.size() > 0) {
            body = new MultipartBody.Part[fileList.size()];
            for (int i = 0; i < fileList.size(); i++) {
                File file;
                if (fileList.get(i).filepath.contains(".jpg") || fileList.get(i).filepath.contains(".png")
                        || fileList.get(i).filepath.contains(".jpeg")) {
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
                }
//                Log.e("MIME TYPE", "" + mimeType + " ----- " + MediaType.parse(mimeType));
            }
        }

        RequestBody contractIdBody = RequestBody.create("" + contractId, MultipartBody.FORM);
        RequestBody descBody = RequestBody.create(descriptionTxt, MultipartBody.FORM);

        HashMap<String, RequestBody> map = new HashMap<>();
        APIRequest apiRequest = new APIRequest();
        map.put("contractID", contractIdBody);
        map.put("description", descBody);

        apiRequest.apiRequestFileBodyJWT(this, activity, API_SUBMIT_CONTRACT_JOB, map, body);

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
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
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
                })
                .onSameThread()
                .check();
    }


    @Override
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        activity.toastMessage(message);
        getIsShowProgress().postValue(false);
        Preferences.writeBoolean(activity, Constants.SUBMIT_FILE_DONE, true);
        activity.onBackPressed();

    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        getIsShowProgress().postValue(false);
    }
}

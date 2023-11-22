package com.nojom.ui.workprofile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.textview.TextViewSFTextPro;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.R;
import com.nojom.api.APIRequest;
import com.nojom.model.ProfileResponse;
import com.nojom.model.Skill;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.NormalFilePickActivity;
import com.nojom.segment.SegmentedButtonGroup;
import com.nojom.ui.BaseActivity;
import com.nojom.util.CompressFile;
import com.nojom.util.Constants;
import com.nojom.util.MyDownloadManager;
import com.nojom.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class NewProfessionalInfoActivityVM extends ViewModel implements Constants, APIRequest.APIRequestListener {

    @SuppressLint("StaticFieldLeak")
    private BaseActivity activity;
    private MutableLiveData<ArrayList<Skill>> mDataEmployment;
    private MutableLiveData<ArrayList<Skill>> mDataEducation;
    private MutableLiveData<Boolean> isShowWebView = new MutableLiveData<>();
    private MutableLiveData<Boolean> isShowProgress = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsShowProgress() {
        return isShowProgress;
    }

    public MutableLiveData<Boolean> getIsShowWebView() {
        return isShowWebView;
    }

    public NewProfessionalInfoActivityVM() {

    }

    public void init(BaseActivity activity) {
        this.activity = activity;
    }

    public MutableLiveData<ArrayList<Skill>> getDataEmployment() {
        if (mDataEmployment == null) {
            mDataEmployment = new MutableLiveData<>();
        }
        return mDataEmployment;
    }

    public MutableLiveData<ArrayList<Skill>> getDataEducation() {
        if (mDataEducation == null) {
            mDataEducation = new MutableLiveData<>();
        }
        return mDataEducation;
    }

    void getEmploymentData(ProfileResponse profileData) {
        try {
            ArrayList<Skill> employmentList = new ArrayList<>();
            if (profileData.experiences != null && profileData.experiences.size() > 0) {
                for (ProfileResponse.Experiences experiences : profileData.experiences) {
                    if (!activity.isEmpty(experiences.companyName)) {
                        if (!activity.isEmpty(experiences.startDate)) {
                            String sDate = Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM yyyy", experiences.startDate);
                            if (experiences.isCurrent == 1) {
                                employmentList.add(new Skill(experiences.companyName, sDate + " - Present"));
                            } else {
                                String eDate = Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM yyyy", experiences.endDate);
                                employmentList.add(new Skill(experiences.companyName, sDate + " - " + eDate));
                            }
                        }
                    }
                }
            }
            getDataEmployment().postValue(employmentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getEducationData(ProfileResponse profileData) {
        ArrayList<Skill> educationList = new ArrayList<>();
        try {
            if (profileData.educations != null && profileData.educations.size() > 0) {
                for (ProfileResponse.Education educations : profileData.educations) {
                    if (!activity.isEmpty(educations.startDate) && !activity.isEmpty(educations.endDate)) {
                        String sDate = Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM yyyy", educations.startDate);
                        String eDate = Utils.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss", "MMM yyyy", educations.endDate);
                        educationList.add(new Skill(educations.degree + " - " + educations.schoolName, sDate + " - " + eDate));
                    }
                }
            }
            getDataEducation().postValue(educationList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void showOptionDialog(String resumeUrl) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(activity, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_file_option_menu);
        dialog.setCancelable(true);

        View llView = dialog.findViewById(R.id.ll_view);
        View llDownload = dialog.findViewById(R.id.ll_download);
        View llUpload = dialog.findViewById(R.id.ll_upload);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);

        if (activity.isEmpty(resumeUrl)) {
            llDownload.setVisibility(View.GONE);
            llView.setVisibility(View.GONE);
        }

        llView.setOnClickListener(v -> {
            dialog.dismiss();
            getIsShowWebView().postValue(true);
        });

        llDownload.setOnClickListener(v -> {
            dialog.dismiss();
            checkPermission(true, false, resumeUrl);
        });

        llUpload.setOnClickListener(v -> {
            dialog.dismiss();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                activity.openDocuments(activity, 1);
//                } else {
//                    Intent intent = new Intent();
//                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
//                    intent.setData(uri);
//                    activity.startActivity(intent);
//                }
//            } else {
            checkPermission(false, true, resumeUrl);
//            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private void downloadFile(final boolean isDownload, String resumeUrl) {
        File folder = new File(Environment.getExternalStorageDirectory(), "/Download/" + activity.getString(R.string.app_name));
        if (!folder.exists())
            folder.mkdir();

        String fileName = Utils.getFileNameFromUrl(resumeUrl);
        final File file = new File(folder, fileName);
        if (!file.exists()) {
            getIsShowProgress().postValue(true);

            String downloadURL = activity.getImageUrl() + fileName + File.separator + RESUME_TAG + ".json";
            Log.e("Download Resume URL", "-------- " + downloadURL);
            if (!TextUtils.isEmpty(downloadURL) && (downloadURL.startsWith("http:") || downloadURL.startsWith("https:"))) {
                MyDownloadManager downloadManager = new MyDownloadManager(activity)
                        .setDownloadUrl(downloadURL)
                        .setTitle(fileName)
                        .setDestinationUri(file)
                        .setDownloadCompleteListener(new MyDownloadManager.DownloadCompleteListener() {
                            @Override
                            public void onDownloadComplete() {
                                getIsShowProgress().postValue(false);
                                showOutput(activity.getString(R.string.download_complete), isDownload, file);
                            }

                            @Override
                            public void onDownloadFailure() {
                                getIsShowProgress().postValue(false);
                                activity.toastMessage(activity.getString(R.string.download_failed));
                            }
                        });
                downloadManager.startDownload();
            }
        } else {
            showOutput(activity.getString(R.string.already_downloaded), isDownload, file);
        }
    }

    private void showOutput(String message, boolean isDownload, File file) {
        if (!isDownload) {
            activity.viewFile(file);
        } else {
            activity.toastMessage(message);
        }
    }

    private void checkPermission(final boolean isDownload, final boolean isUpload, String resumeUrl) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (isUpload) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    activity.openDocuments(activity, 1);
                                } else {
                                    Intent intent = new Intent(activity, NormalFilePickActivity.class);
                                    intent.putExtra(Constant.MAX_NUMBER, 1);
                                    intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"doc", "docx", "ppt", "pptx", "pdf"});
                                    activity.startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                                }
                            } else {
                                downloadFile(isDownload, resumeUrl);
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

    void makePublicPrivate(SegmentedButtonGroup segmentedButtonGroup) {
        if (!activity.isNetworkConnected())
            return;

//        getIsShowProgress().postValue(true);

        CommonRequest.ProfilePublicityTypeId profilePublicity = new CommonRequest.ProfilePublicityTypeId();
        profilePublicity.setProfilePublicityTypeID(String.valueOf(segmentedButtonGroup.getTag()));
        profilePublicity.setStatus(String.valueOf(segmentedButtonGroup.getPosition()));

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_PROFILE_PUBLICITY, profilePublicity.toString(), true, this);

    }

    void updateResume(File file, String mimeType) {
        if (!activity.isNetworkConnected())
            return;

        getIsShowProgress().postValue(true);

        MultipartBody.Part body = null;
        try {
            if (file != null) {
                if (file.getAbsolutePath().contains(".jpg") || file.getAbsolutePath().contains(".png")
                        || file.getAbsolutePath().contains(".jpeg")) {
                    file = CompressFile.getCompressedImageFile(file);
                }

                if (file != null) {
                    Uri selectedUri = Uri.fromFile(file);
                    String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                    if (TextUtils.isEmpty(mimeType)) {
                        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
                    }

                    RequestBody requestFile = null;
                    if (mimeType != null) {
                        requestFile = RequestBody.create(file, MediaType.parse(mimeType));
                    }

                    Headers.Builder headers = new Headers.Builder();
                    headers.addUnsafeNonAscii("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\"");

                    if (requestFile != null) {
                        body = MultipartBody.Part.create(headers.build(), requestFile);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CommonRequest.AddResume addResume = new CommonRequest.AddResume();
        addResume.setProfile_id(activity.getUserID());

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequestFileUpload(activity, API_ADD_RESUME, addResume.toString(), this, body);
    }

    Dialog dialogHourlyRate(TextViewSFTextPro tvHourlyRate) {
        ContextThemeWrapper cw = new ContextThemeWrapper(activity, R.style.AlertDialogTheme);
        AlertDialog.Builder builder = new AlertDialog.Builder(cw);
        final CharSequence[] array = {activity.getString(R.string.hourly_rate), activity.getString(R.string.fixed_rate)};
        builder.setItems(array, (dialog, which) -> {
            tvHourlyRate.setText(array[which].toString());
            dialog.dismiss();
        });
        return builder.create();
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (urlEndPoint.equalsIgnoreCase(API_PROFILE_PUBLICITY)) {
            activity.toastMessage(msg);
        }
        activity.getProfile();
        getIsShowProgress().postValue(false);
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
        getIsShowProgress().postValue(false);
    }
}

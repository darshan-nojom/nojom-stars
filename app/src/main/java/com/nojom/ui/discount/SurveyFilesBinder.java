package com.nojom.ui.discount;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.ItemBinder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nojom.BuildConfig;
import com.nojom.R;
import com.nojom.databinding.ItemListFilesSurveyBinding;
import com.nojom.model.SocialDetailModel;
import com.nojom.ui.BaseActivity;
import com.nojom.util.MyDownloadManager;
import com.nojom.util.StorageDisclosureDialog;
import com.nojom.util.Utils;

import java.io.File;
import java.util.Objects;

public class SurveyFilesBinder extends ItemBinder<SocialDetailModel.Data, SurveyFilesBinder.FilesViewHolder> {

    private Context mContext;
    private String filePath;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setListener(OnClickFileListener listener) {
        this.listener = listener;
    }

    private OnClickFileListener listener;

    public interface OnClickFileListener {
        void onClickDeleteFile(SocialDetailModel.Data data);
    }

    @Override
    public FilesViewHolder create(LayoutInflater inflater, ViewGroup parent) {
        mContext = parent.getContext();
        ItemListFilesSurveyBinding itemListFilesBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_list_files_survey, parent, false);
        return new FilesViewHolder(itemListFilesBinding);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof SocialDetailModel.Data;
    }

    @Override
    public void bind(FilesViewHolder holder, final SocialDetailModel.Data item) {
        holder.binding.tvFileName.setText(item.screenshot);
        holder.binding.tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", "dd/MM/yyyy", item.timestamp));
        holder.binding.imgView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.eye_gray));

        holder.binding.imgView.setOnClickListener(v -> showOptionDialog(item));

        holder.binding.imgDelete.setOnClickListener(view -> {
            if (listener != null) {
                listener.onClickDeleteFile(item);
            }
        });
    }

    static class FilesViewHolder extends BaseViewHolder<SocialDetailModel.Data> {

        ItemListFilesSurveyBinding binding;

        FilesViewHolder(ItemListFilesSurveyBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    private void showOptionDialog(final SocialDetailModel.Data userFiles) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(mContext, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_file_option_menu);
        dialog.setCancelable(true);

        View llView = dialog.findViewById(R.id.ll_view);
        View llDownload = dialog.findViewById(R.id.ll_download);
        View llEmail = dialog.findViewById(R.id.ll_email);
        View llShare = dialog.findViewById(R.id.ll_share);
        View llUpload = dialog.findViewById(R.id.ll_upload);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);

        llEmail.setVisibility(View.VISIBLE);
        llShare.setVisibility(View.VISIBLE);
        llUpload.setVisibility(View.GONE);

        llView.setOnClickListener(v -> {
            dialog.dismiss();
            if (!TextUtils.isEmpty(userFiles.localFilePath)) {
                ((BaseActivity) mContext).viewFile(userFiles.localFilePath);
            } else {
                ((BaseActivity) mContext).viewFile(filePath + userFiles.screenshot);
            }

        });

        llDownload.setOnClickListener(v -> {
            dialog.dismiss();
            if (((BaseActivity) mContext).checkStoragePermission()) {
                checkPermission(userFiles, true, false, false);
            } else {
                new StorageDisclosureDialog(((BaseActivity) mContext), new StorageDisclosureDialog.OnClickListener() {
                    @Override
                    public void onClickOk() {
                        checkPermission(userFiles, true, false, false);
                    }
                });
            }

        });

        llShare.setOnClickListener(v -> {
            dialog.dismiss();
            if (((BaseActivity) mContext).checkStoragePermission()) {
                checkPermission(userFiles, false, false, true);
            } else {
                new StorageDisclosureDialog(((BaseActivity) mContext), new StorageDisclosureDialog.OnClickListener() {
                    @Override
                    public void onClickOk() {
                        checkPermission(userFiles, false, false, true);
                    }
                });
            }
        });

        llEmail.setOnClickListener(v -> {
            dialog.dismiss();
            if (((BaseActivity) mContext).checkStoragePermission()) {
                checkPermission(userFiles, false, true, false);
            } else {
                new StorageDisclosureDialog(((BaseActivity) mContext), new StorageDisclosureDialog.OnClickListener() {
                    @Override
                    public void onClickOk() {
                        checkPermission(userFiles, false, true, false);
                    }
                });
            }
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

    private void downloadFile(final SocialDetailModel.Data attachments, final boolean isDownload, final boolean isEmailShare, final boolean isShare) {
        File folder = new File(Environment.getExternalStorageDirectory(), "/Download/" + mContext.getString(R.string.app_name));
        if (!folder.exists())
            folder.mkdir();

        final File file = new File(folder, attachments.screenshot);
        if (!file.exists()) {
//            ((BaseActivity) mContext).showProgress();
            String url = filePath + attachments.screenshot;
            if (!TextUtils.isEmpty(attachments.screenshot) && (url.startsWith("http:") || url.startsWith("https:"))) {
                MyDownloadManager downloadManager = new MyDownloadManager(mContext)
                        .setDownloadUrl(filePath + attachments.screenshot)
                        .setTitle(attachments.screenshot)
                        .setDestinationUri(file)
                        .setDownloadCompleteListener(new MyDownloadManager.DownloadCompleteListener() {
                            @Override
                            public void onDownloadComplete() {
//                                ((BaseActivity) mContext).hideProgress();
                                showOutput("Download complete", isDownload, file, isEmailShare, isShare);
                            }

                            @Override
                            public void onDownloadFailure() {
//                                ((BaseActivity) mContext).hideProgress();
                                ((BaseActivity) mContext).toastMessage("Download failed");
                            }
                        });
                downloadManager.startDownload();
            }


//            Log.e("File Id", "---------- " + attachments.id);
//            Log.e("File Name", "---------- " + attachments.filename);
//            Call<ResponseBody> call = ((BaseActivity) mContext).getService()
//                    .downloadFile(attachments.id, ((BaseActivity) mContext).getUserID());
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    ((BaseActivity) mContext).hideProgress();
//                    Log.e("Resp ", "" + response.body());
//                    if (response.body() != null && ((BaseActivity) mContext).writeResponseBodyToDisk(response.body(), attachments.filename)) {
//                        showOutput("Download complete", isDownload, file, isEmailShare, isShare);
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    ((BaseActivity) mContext).hideProgress();
//                    ((BaseActivity) mContext).toastMessage("Download failed");
//                }
//            });


        } else {
            showOutput("Already Downloaded", isDownload, file, isEmailShare, isShare);
        }
    }

    private void showOutput(String message, boolean isDownload, File file, boolean isEmailShare, boolean isShare) {
        if (!isDownload) {
            if (isShare || isEmailShare)
                shareFile(file, isEmailShare);
            else
                ((BaseActivity) mContext).viewFile(file);
        } else {
            ((BaseActivity) mContext).toastMessage(message);
        }
    }

    private void shareFile(File file, boolean isEmailShare) {
        Uri uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", file);
        Intent intentShareFile = new Intent(isEmailShare ? Intent.ACTION_SENDTO : Intent.ACTION_SEND);
        intentShareFile.setType("/");
        if (isEmailShare) {
            String mime;
            if (file.getAbsolutePath().contains(".doc") || file.getAbsolutePath().contains(".docx")) {
                mime = "application/msword";
            } else if (file.getAbsolutePath().contains(".txt")) {
                mime = "text/plain";
            } else if (file.getAbsolutePath().contains(".pdf")) {
                mime = "application/pdf";
            } else if (file.getAbsolutePath().contains(".ppt") || file.getAbsolutePath().contains(".pptx")) {
                mime = "application/vnd.ms-powerpoint";
            } else if (file.getAbsolutePath().contains(".xls") || file.getAbsolutePath().contains(".xlsx")) {
                mime = "application/vnd.ms-excel";
            } else if (file.getAbsolutePath().contains(".jpg") || file.getAbsolutePath().contains(".png") ||
                    file.getAbsolutePath().contains(".jpeg") || file.getAbsolutePath().contains(".gif")) {
                mime = "image/*";
            } else if (file.getAbsolutePath().contains(".zip") || file.getAbsolutePath().contains(".rar")) {
                // WAV audio file
                mime = "application/x-wav";
            } else if (file.getAbsolutePath().contains(".mp4") || file.getAbsolutePath().contains(".avi")) {
                mime = "video/*";
            } else {
                mime = "/";
            }
            intentShareFile.setType(mime);
            uri = Uri.parse("file://" + file.toString());
            intentShareFile.setData(Uri.parse("mailto:"));
            intentShareFile.putExtra(Intent.EXTRA_EMAIL, "");
        }
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentShareFile.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
        mContext.startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }

    private void checkPermission(final SocialDetailModel.Data userFiles, final boolean isDownload, final boolean isEmailShare, final boolean isShare) {
        Dexter.withActivity((Activity) mContext)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        downloadFile(userFiles, isDownload, isEmailShare, isShare);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        ((BaseActivity) mContext).toastMessage("give storage permission first");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}
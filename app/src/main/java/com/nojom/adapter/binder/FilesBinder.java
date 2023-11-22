package com.nojom.adapter.binder;

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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import com.nojom.databinding.ItemListFilesBinding;
import com.nojom.model.ProjectByID;
import com.nojom.ui.BaseActivity;
import com.nojom.util.MyDownloadManager;
import com.nojom.util.Utils;

import java.io.File;
import java.util.Objects;

import static com.nojom.util.Constants.GIG_ATTACHMENT;

public class FilesBinder extends ItemBinder<ProjectByID.Attachments, FilesBinder.FilesViewHolder> {

    private Context mContext;
    private String endPoint, gigFilePath;

    public FilesBinder(String tag) {
        endPoint = tag;
    }

    public FilesBinder(String tag, String gigFilePath) {
        endPoint = tag;
        this.gigFilePath = gigFilePath;
    }

    public FilesBinder() {
    }

    @Override
    public FilesViewHolder create(LayoutInflater inflater, ViewGroup parent) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemListFilesBinding filesBinding =
                ItemListFilesBinding.inflate(layoutInflater, parent, false);
        return new FilesViewHolder(filesBinding);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof ProjectByID.Attachments;
    }

    @Override
    public void bind(FilesViewHolder holder, final ProjectByID.Attachments item) {

        holder.binding.tvFileName.setText(item.filename);
       
        holder.binding.tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", "dd/MM/yyyy", item.timestamp));
        holder.binding.tvOwner.setText("");
        holder.binding.imgView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.eye_gray));

        holder.binding.imgView.setOnClickListener(v -> showOptionDialog(item));
    }

    static class FilesViewHolder extends BaseViewHolder<ProjectByID.Attachments> {

        ItemListFilesBinding binding;

        FilesViewHolder(ItemListFilesBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    private void showOptionDialog(final ProjectByID.Attachments userFiles) {
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
//                checkPermission(userFiles, false, false, false);
            if (!TextUtils.isEmpty(endPoint)) {//client attachment case
                if (endPoint.equalsIgnoreCase(GIG_ATTACHMENT)) {
                    ((BaseActivity) mContext).viewFile(gigFilePath + userFiles.filename/* + File.separator + endPoint + ".json"*/);
                } else {
                    ((BaseActivity) mContext).viewFile(((BaseActivity) mContext).getClientAttachmentUrl() + userFiles.filename/* + File.separator + endPoint + ".json"*/);
                }
            } else {
                ((BaseActivity) mContext).viewFile(((BaseActivity) mContext).getAgentAttachmentUrl() + userFiles.filename /*+ ".json"*/);
            }
        });

        llDownload.setOnClickListener(v -> {
            dialog.dismiss();
            checkPermission(userFiles, true, false, false);
        });

        llShare.setOnClickListener(v -> {
            dialog.dismiss();
            checkPermission(userFiles, false, false, true);
        });

        llEmail.setOnClickListener(v -> {
            dialog.dismiss();
            checkPermission(userFiles, false, true, false);
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

    private void downloadFile(final ProjectByID.Attachments attachments, final boolean isDownload, final boolean isEmailShare, final boolean isShare) {
        File folder = new File(Environment.getExternalStorageDirectory(), "/Download/" + mContext.getString(R.string.app_name));
        if (!folder.exists())
            folder.mkdir();
        String newFileName = attachments.filename;
        final File file = new File(folder, newFileName);
        if (!file.exists()) {
            //((BaseActivity) mContext).showProgress();

            String downloadURL;
            if (!TextUtils.isEmpty(endPoint)) {//client attachment case
                if (endPoint.equalsIgnoreCase(GIG_ATTACHMENT)) {
                    downloadURL = gigFilePath + newFileName;
                } else {
                    downloadURL = ((BaseActivity) mContext).getClientAttachmentUrl() + newFileName;
                }
            } else {
                downloadURL = ((BaseActivity) mContext).getAgentAttachmentUrl() + newFileName;
            }

            if (!downloadURL.startsWith("http")) {
                //((BaseActivity) mContext).hideProgress();
                ((BaseActivity) mContext).toastMessage(mContext.getString(R.string.something_went_wrong));
                return;
            }

            Log.e("FILE URL ", "------------------  " + downloadURL);
            MyDownloadManager downloadManager = new MyDownloadManager(mContext)
//                    .setDownloadUrl(Constants.ATTACHMENTS_URL + attachments.filename)
                    .setDownloadUrl(downloadURL)
                    .setTitle(newFileName)
                    .setDestinationUri(file)
                    .setDownloadCompleteListener(new MyDownloadManager.DownloadCompleteListener() {
                        @Override
                        public void onDownloadComplete() {
                            //((BaseActivity) mContext).hideProgress();
                            showOutput("Download complete", isDownload, file, isEmailShare, isShare);
                        }

                        @Override
                        public void onDownloadFailure() {
                            // ((BaseActivity) mContext).hideProgress();
                            ((BaseActivity) mContext).toastMessage("Download failed");
                        }
                    });
            downloadManager.startDownload();
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
        intentShareFile.setType("*/*");
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
            } else if (file.getAbsolutePath().contains(".jpg") || file.getAbsolutePath().contains(".png") || file.getAbsolutePath().contains(".jpeg") || file.getAbsolutePath().contains(".gif")) {
                mime = "image/*";
            } else if (file.getAbsolutePath().contains(".zip") || file.getAbsolutePath().contains(".rar")) {
                // WAV audio file
                mime = "application/x-wav";
            } else if (file.getAbsolutePath().contains(".mp4") || file.getAbsolutePath().contains(".avi")) {
                mime = "video/*";
            } else {
                mime = "*/*";
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

    private void checkPermission(final ProjectByID.Attachments userFiles, final boolean isDownload, final boolean isEmailShare, final boolean isShare) {
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
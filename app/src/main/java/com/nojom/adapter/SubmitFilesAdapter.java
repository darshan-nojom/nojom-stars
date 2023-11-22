package com.nojom.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.ArrayList;
import java.util.Objects;

public class SubmitFilesAdapter extends RecyclerView.Adapter<SubmitFilesAdapter.SimpleViewHolder> {

    private ArrayList<ProjectByID.FileList> mDataset;
    private Context context;
    private BaseActivity activity;

    public SubmitFilesAdapter(Context context, ArrayList<ProjectByID.FileList> objects) {
        this.mDataset = objects;
        this.context = context;
        activity = (BaseActivity) context;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemListFilesBinding listFilesBinding =
                ItemListFilesBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(listFilesBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        ProjectByID.FileList item = mDataset.get(position);
        holder.binding.tvFileName.setText(item.fileName);
        holder.binding.tvDate.setText(Utils.formatFileSize(item.size));
        holder.binding.tvOwner.setVisibility(View.GONE);
        holder.binding.imgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.eye_gray));
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public ArrayList<ProjectByID.FileList> getData() {
        return mDataset;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemListFilesBinding binding;

        SimpleViewHolder(ItemListFilesBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.imgView.setOnClickListener(v -> showOptionDialog(mDataset.get(getAdapterPosition())));
        }
    }

    private void showOptionDialog(final ProjectByID.FileList userFiles) {
        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(activity, R.style.Theme_Design_Light_BottomSheetDialog);
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
            ((BaseActivity) context).viewFile(activity.getSubmittedFileUrl() + userFiles.fileName);
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

    private void downloadFile(final ProjectByID.FileList attachments, final boolean isDownload, final boolean isEmailShare, final boolean isShare) {
        File folder = new File(Environment.getExternalStorageDirectory(), "/Download/" + context.getString(R.string.app_name));
        if (!folder.exists())
            folder.mkdir();

        final File file = new File(folder, attachments.fileName);
        if (!file.exists()) {
//            activity.showProgress();
            String url = activity.getSubmittedFileUrl() + attachments.fileName;
            if (!TextUtils.isEmpty(url) && !url.startsWith("http")) {
                //((BaseActivity) mContext).hideProgress();
                activity.toastMessage(activity.getString(R.string.something_went_wrong));
                return;
            }

            MyDownloadManager downloadManager = new MyDownloadManager(activity)
                    .setDownloadUrl(activity.getSubmittedFileUrl() + attachments.fileName)
                    .setTitle(attachments.fileName)
                    .setDestinationUri(file)
                    .setDownloadCompleteListener(new MyDownloadManager.DownloadCompleteListener() {
                        @Override
                        public void onDownloadComplete() {
//                            activity.hideProgress();
                            showOutput(context.getString(R.string.download_complete), isDownload, file, isEmailShare, isShare);
                        }

                        @Override
                        public void onDownloadFailure() {
//                            activity.hideProgress();
                            activity.toastMessage(context.getString(R.string.download_failed));
                        }
                    });
            downloadManager.startDownload();
        } else {
            showOutput(context.getString(R.string.already_downloaded), isDownload, file, isEmailShare, isShare);
        }
    }

    private void showOutput(String message, boolean isDownload, File file, boolean isEmailShare, boolean isShare) {
        if (!isDownload) {
            if (isShare || isEmailShare)
                shareFile(file, isEmailShare);
            else
                activity.viewFile(file);
        } else {
            activity.toastMessage(message);
        }
    }

    private void shareFile(File file, boolean isEmailShare) {
        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
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
            } else if (file.getAbsolutePath().contains(".jpg") || file.getAbsolutePath().contains(".png") ||
                    file.getAbsolutePath().contains(".jpeg") || file.getAbsolutePath().contains(".gif")) {
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
        context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }

    private void checkPermission(final ProjectByID.FileList userFiles, final boolean isDownload, final boolean isEmailShare, final boolean isShare) {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        downloadFile(userFiles, isDownload, isEmailShare, isShare);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        activity.toastMessage(context.getString(R.string.please_give_permission));
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}

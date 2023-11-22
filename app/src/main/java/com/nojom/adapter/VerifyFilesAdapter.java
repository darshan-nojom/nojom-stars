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
import com.nojom.api.APIRequest;
import com.nojom.databinding.ItemListVerifyIdBinding;
import com.nojom.model.VerifyID;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.MyDownloadManager;
import com.nojom.util.Utils;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class VerifyFilesAdapter extends RecyclerView.Adapter<VerifyFilesAdapter.SimpleViewHolder> implements Constants, APIRequest.APIRequestListener {

    private List<VerifyID.Data> mDataset;
    private Context context;
    private BaseActivity activity;
    private int selectedPos = -1;

    public VerifyFilesAdapter(Context context) {
        this.context = context;
        activity = (BaseActivity) context;
    }

    public void doRefresh(List<VerifyID.Data> mDataset) {
        this.mDataset = mDataset;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemListVerifyIdBinding verifyIdBinding =
                ItemListVerifyIdBinding.inflate(layoutInflater, parent, false);
        return new SimpleViewHolder(verifyIdBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        try {
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            VerifyID.Data item = mDataset.get(position);
            holder.binding.tvFileName.setText(item.data);
            holder.binding.tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", "dd/MM/yyyy", item.timestamp));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public List<VerifyID.Data> getData() {
        return mDataset;
    }


    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemListVerifyIdBinding binding;

        SimpleViewHolder(ItemListVerifyIdBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.imgView.setOnClickListener(v -> {
                if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                    return;
                }
                if (mDataset != null && mDataset.size() > 0 && getAdapterPosition() >= 0) {
                    showOptionDialog(mDataset.get(getAdapterPosition()));
                }
            });

            binding.imgDelete.setOnClickListener(v -> {
                if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                    return;
                }
                if (mDataset != null && mDataset.size() > 0 && getAdapterPosition() >= 0) {
                    deleteId(mDataset.get(getAdapterPosition()).id, getAdapterPosition());
                }
            });
        }
    }

    private void deleteId(int id, int position) {
        if (!activity.isNetworkConnected()) {
            return;
        }

//        activity.showProgress();

        CommonRequest.DeleteProfileVerification deleteProfileVerification = new CommonRequest.DeleteProfileVerification();
        deleteProfileVerification.setProfile_verification_id(id);
        selectedPos = position;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(activity, API_DELETE_PROFILE_VERIFICATION, deleteProfileVerification.toString(), true, this);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (selectedPos != -1 && mDataset != null && mDataset.size() > 0) {
            mDataset.remove(selectedPos);
            notifyItemRemoved(selectedPos);
            notifyItemRangeChanged(selectedPos, mDataset.size());
        }
        selectedPos = -1;
//        activity.hideProgress();
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
//        activity.hideProgress();
        selectedPos = -1;
    }

    private void showOptionDialog(final VerifyID.Data userFiles) {
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
            ((BaseActivity) context).viewFile(activity.getImageIdUrl() + userFiles.data);
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

    private void downloadFile(final VerifyID.Data attachments, final boolean isDownload, final boolean isEmailShare, final boolean isShare) {
        File folder = new File(Environment.getExternalStorageDirectory(), "/Download/" + context.getString(R.string.app_name));
        if (!folder.exists())
            folder.mkdir();

        final File file = new File(folder, attachments.data);
        if (!file.exists()) {
//            activity.showProgress();
            String url = activity.getImageIdUrl() + attachments.data;
            if (!TextUtils.isEmpty(url) && (url.startsWith("http:") || url.startsWith("https:"))) {
                MyDownloadManager downloadManager = new MyDownloadManager(activity)
                        .setDownloadUrl(activity.getImageIdUrl() + attachments.data)
                        .setTitle(attachments.data)
                        .setDestinationUri(file)
                        .setDownloadCompleteListener(new MyDownloadManager.DownloadCompleteListener() {
                            @Override
                            public void onDownloadComplete() {
//                            activity.hideProgress();
                                showOutput(activity.getString(R.string.download_complete), isDownload, file, isEmailShare, isShare);
                            }

                            @Override
                            public void onDownloadFailure() {
//                            activity.hideProgress();
                                activity.toastMessage(activity.getString(R.string.download_failed));
                            }
                        });
                downloadManager.startDownload();
            }
        } else {
            showOutput(activity.getString(R.string.already_downloaded), isDownload, file, isEmailShare, isShare);
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
        context.startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }

    private void checkPermission(final VerifyID.Data userFiles, final boolean isDownload, final boolean isEmailShare, final boolean isShare) {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        downloadFile(userFiles, isDownload, isEmailShare, isShare);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        activity.toastMessage(activity.getString(R.string.please_give_permission));
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}

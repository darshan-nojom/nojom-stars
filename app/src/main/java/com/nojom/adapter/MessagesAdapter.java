package com.nojom.adapter;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.R;
import com.nojom.databinding.ItemChatMessagesBinding;
import com.nojom.model.Messages;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.MyDownloadManager;
import com.nojom.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private Context context;
    private ArrayList<Messages> messagesList;
    private BaseActivity activity;

    public MessagesAdapter(Context context) {
        this.context = context;
        activity = (BaseActivity) context;
        messagesList = new ArrayList<>();
    }

    public void doRefresh(ArrayList<Messages> messagesList) {
        this.messagesList = messagesList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        ItemChatMessagesBinding itemChatMessagesBinding =
                ItemChatMessagesBinding.inflate(layoutInflater, parent, false);
        return new MessagesViewHolder(itemChatMessagesBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder holder, final int position) {
        final Messages item = messagesList.get(position);

        if (item.isDayChange) {
            holder.binding.dayDate.setVisibility(View.VISIBLE);
            holder.binding.dayDate.setText(Utils.changeDateFormat(item.date, "dd MMM yyyy"));
        } else {
            holder.binding.dayDate.setVisibility(View.GONE);
        }

        try {

            if (item.id.equals(activity.getSUserID())) { // Messages sent by me
                holder.binding.frameOutgoing.setVisibility(View.VISIBLE);
                holder.binding.frameIncoming.setVisibility(View.GONE);
                holder.binding.imgSeen.setImageResource(item.seen ? R.drawable.seen : R.drawable.sent);
                switch (item.type) {
                    case Constants.TEXT:
                        holder.showTextView(true);
                        holder.binding.tvOutgoingMessage.setText(item.message);
                        break;
                    case Constants.IMAGE:
                        holder.showImageView(true);
                        loadImage(item.message, holder.binding.imgOutgoing);
                        showDownloadOrNot(item.fileName, holder.binding.iBlurSender, holder.binding.iProgressSender, holder.binding.iDownloadSender);
                        break;
                    case Constants.VIDEO:
                        holder.showVideoView(true);
                        loadVideoThumbnail(item.message, holder.binding.videoOutgoing);
                        showDownloadOrNot(item.fileName, holder.binding.vBlurSender, holder.binding.vProgressSender, holder.binding.vDownloadSender);
                        break;
                    case Constants.DOC:
                        holder.showFileView(true);
                        holder.binding.tvFileOutgoing.setText(item.fileName);
                        showDownloadOrNot(item.fileName, null, holder.binding.fProgressSender, holder.binding.fDownloadSender);
                        break;
                    default:
                        holder.hideAllViews(true);
                        break;
                }
                holder.binding.outgoingDate.setText(Utils.changeDateFormat(item.date, "hh:mm a"));

                holder.binding.rlImageSender.setOnClickListener(v -> checkPermission(item, holder.binding.iBlurSender, holder.binding.iProgressSender, holder.binding.iDownloadSender));

                holder.binding.rlVideoSender.setOnClickListener(v -> checkPermission(item, holder.binding.vBlurSender, holder.binding.vProgressSender, holder.binding.vDownloadSender));

                holder.binding.rlFileSender.setOnClickListener(v -> checkPermission(item, null, holder.binding.fProgressSender, holder.binding.fDownloadSender));
            } else {
                holder.binding.frameOutgoing.setVisibility(View.GONE);
                holder.binding.frameIncoming.setVisibility(View.VISIBLE);
                switch (item.type) {
                    case Constants.TEXT:
                        holder.showTextView(false);
                        holder.binding.tvIncomingMessage.setText(item.message);
                        break;
                    case Constants.IMAGE:
                        holder.showImageView(false);
                        loadImage(item.message, holder.binding.imgIncoming);
                        showDownloadOrNot(item.fileName, holder.binding.iBlurReceiver, holder.binding.iProgressReceiver, holder.binding.iDownloadReceiver);
                        break;
                    case Constants.VIDEO:
                        holder.showVideoView(false);
                        loadVideoThumbnail(item.message, holder.binding.videoIncoming);
                        showDownloadOrNot(item.fileName, holder.binding.vBlurReceiver, holder.binding.vProgressReceiver, holder.binding.vDownloadReceiver);
                        break;
                    case Constants.DOC:
                        holder.showFileView(false);
                        holder.binding.tvFileIncoming.setText(item.fileName);
                        showDownloadOrNot(item.fileName, null, holder.binding.fProgressReceiver, holder.binding.fDownloadReceiver);
                        break;
                    default:
                        holder.hideAllViews(false);
                        break;
                }
                holder.binding.incomingDate.setText(Utils.changeDateFormat(item.date, "hh:mm a"));

                holder.binding.rlImageReceiver.setOnClickListener(v -> checkPermission(item, holder.binding.iBlurReceiver, holder.binding.iProgressReceiver, holder.binding.iDownloadReceiver));

                holder.binding.rlVideoReceiver.setOnClickListener(v -> checkPermission(item, holder.binding.vBlurReceiver, holder.binding.vProgressReceiver, holder.binding.vDownloadReceiver));

                holder.binding.rlFileReceiver.setOnClickListener(v -> checkPermission(item, null, holder.binding.fProgressReceiver, holder.binding.fDownloadReceiver));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception while loading", "----------------- " + e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return messagesList != null ? messagesList.size() : 0;
    }

    private void showDownloadOrNot(String fileName, ImageView imgBlur, ProgressBar progressBar, ImageView imgDownload) {
        if (isFileExist(fileName)) {
            if (imgBlur != null)
                imgBlur.setAlpha(0f);

            progressBar.setVisibility(View.GONE);
            imgDownload.setVisibility(View.GONE);
        } else {
            if (imgBlur != null)
                imgBlur.setAlpha(0.7f);

            progressBar.setVisibility(View.GONE);
            imgDownload.setVisibility(View.VISIBLE);
        }
    }

    private void downloadFile(String url, String fileName, final ImageView imgBlur, final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);

        File file = getFilePath(fileName);

        if (!TextUtils.isEmpty(url) && !url.startsWith("http")) {
            //((BaseActivity) mContext).hideProgress();
            activity.toastMessage(activity.getString(R.string.something_went_wrong));
            return;
        }

        MyDownloadManager downloadManager = new MyDownloadManager(activity)
                .setDownloadUrl(url)
                .setTitle(fileName)
                .setDestinationUri(file)
                .setDownloadCompleteListener(new MyDownloadManager.DownloadCompleteListener() {
                    @Override
                    public void onDownloadComplete() {
                        progressBar.setVisibility(View.GONE);
                        if (imgBlur != null)
                            imgBlur.setAlpha(0f);
                    }

                    @Override
                    public void onDownloadFailure() {
                        //activity.hideProgress();
                        activity.toastMessage(context.getString(R.string.download_failed));
                    }
                });
        downloadManager.startDownload();
    }

    private boolean isFileExist(String fileName) {
        if (getFilePathCamera(fileName).exists()) {
            return true;
        }
        return getFilePath(fileName).exists();
    }

    private File getFilePathCamera(String fileName) {
        return new File(getFolderCamera(), fileName);
    }

    private File getFolderCamera() {
        File folder = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera");
        if (!folder.exists())
            folder.mkdir();

        return folder;
    }

    private File getFolder() {
        File folder = new File(Environment.getExternalStorageDirectory(), "/Download/" + activity.getString(R.string.app_name));
        if (!folder.exists())
            folder.mkdir();

        return folder;
    }

    private File getFilePath(String fileName) {
        if (getFilePathCamera(fileName).exists()) {
            return new File(getFolderCamera(), fileName);
        }
        return new File(getFolder(), fileName);
    }

    private void loadImage(String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().placeholder(R.color.lightgray))
                .into(imageView);
    }

    private void loadVideoThumbnail(String url, ImageView imageView) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().placeholder(R.color.lightgray))
                .into(imageView);
    }

    private void checkPermission(final Messages item, final ImageView imgBlur, final ProgressBar progressBar, final ImageView imgDownload) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (isFileExist(item.fileName)) {
                                activity.viewFile(getFilePath(item.fileName));
                            } else {
                                imgDownload.setVisibility(View.GONE);
                                if (progressBar.getVisibility() == View.GONE)
                                    downloadFile(item.message, item.fileName, imgBlur, progressBar);
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


    class MessagesViewHolder extends RecyclerView.ViewHolder {

        ItemChatMessagesBinding binding;

        MessagesViewHolder(@NonNull ItemChatMessagesBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

        }

        void showTextView(boolean isSender) {
            if (isSender) {
                binding.tvOutgoingMessage.setVisibility(View.VISIBLE);
                binding.rlImageSender.setVisibility(View.GONE);
                binding.rlVideoSender.setVisibility(View.GONE);
                binding.rlFileSender.setVisibility(View.GONE);
            } else {
                binding.tvIncomingMessage.setVisibility(View.VISIBLE);
                binding.rlImageReceiver.setVisibility(View.GONE);
                binding.rlVideoReceiver.setVisibility(View.GONE);
                binding.rlFileReceiver.setVisibility(View.GONE);
            }
        }

        void showImageView(boolean isSender) {
            if (isSender) {
                binding.tvOutgoingMessage.setVisibility(View.GONE);
                binding.rlImageSender.setVisibility(View.VISIBLE);
                binding.rlVideoSender.setVisibility(View.GONE);
                binding.rlFileSender.setVisibility(View.GONE);
            } else {
                binding.tvIncomingMessage.setVisibility(View.GONE);
                binding.rlImageReceiver.setVisibility(View.VISIBLE);
                binding.rlVideoReceiver.setVisibility(View.GONE);
                binding.rlFileReceiver.setVisibility(View.GONE);
            }
        }

        void showVideoView(boolean isSender) {
            if (isSender) {
                binding.tvOutgoingMessage.setVisibility(View.GONE);
                binding.rlImageSender.setVisibility(View.GONE);
                binding.rlVideoSender.setVisibility(View.VISIBLE);
                binding.rlFileSender.setVisibility(View.GONE);
            } else {
                binding.tvIncomingMessage.setVisibility(View.GONE);
                binding.rlImageReceiver.setVisibility(View.GONE);
                binding.rlVideoReceiver.setVisibility(View.VISIBLE);
                binding.rlFileReceiver.setVisibility(View.GONE);
            }
        }

        void showFileView(boolean isSender) {
            if (isSender) {
                binding.tvOutgoingMessage.setVisibility(View.GONE);
                binding.rlImageSender.setVisibility(View.GONE);
                binding.rlVideoSender.setVisibility(View.GONE);
                binding.rlFileSender.setVisibility(View.VISIBLE);
            } else {
                binding.tvIncomingMessage.setVisibility(View.GONE);
                binding.rlImageReceiver.setVisibility(View.GONE);
                binding.rlVideoReceiver.setVisibility(View.GONE);
                binding.rlFileReceiver.setVisibility(View.VISIBLE);
            }
        }

        void hideAllViews(boolean isSender) {
            if (isSender) {
                binding.tvOutgoingMessage.setVisibility(View.GONE);
                binding.rlImageSender.setVisibility(View.GONE);
                binding.rlVideoSender.setVisibility(View.GONE);
                binding.rlFileSender.setVisibility(View.GONE);
            } else {
                binding.tvIncomingMessage.setVisibility(View.GONE);
                binding.rlImageReceiver.setVisibility(View.GONE);
                binding.rlVideoReceiver.setVisibility(View.GONE);
                binding.rlFileReceiver.setVisibility(View.GONE);
            }
        }
    }
}

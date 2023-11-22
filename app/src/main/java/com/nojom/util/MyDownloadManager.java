package com.nojom.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class MyDownloadManager {

    private DownloadCompleteListener downloadCompleteListener;
    private DownloadManager downloadManager;
    private String downloadUrl;
    private String title;
    private File destinationFile;

    public MyDownloadManager(Context mContext) {
        downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
        mContext.registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public MyDownloadManager setDownloadUrl(String url) {
        downloadUrl = url;
        return this;
    }

    public MyDownloadManager setTitle(String title) {
        this.title = title;
        return this;
    }

    public MyDownloadManager setDestinationUri(File file) {
        destinationFile = file;
        return this;
    }

    public void startDownload() {
        if (downloadManager != null && !TextUtils.isEmpty(downloadUrl) && downloadUrl.startsWith("http")) {
            Uri downloadUri = Uri.parse(downloadUrl.replaceAll(" ", "%20"));
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setTitle(title);
            request.allowScanningByMediaScanner();
            request.setDestinationUri(Uri.fromFile(destinationFile));
            downloadManager.enqueue(request);
        } else {
            if (downloadCompleteListener != null) {
                downloadCompleteListener.onDownloadFailure();
            }
        }
    }

    public MyDownloadManager setDownloadCompleteListener(DownloadCompleteListener downloadCompleteListener) {
        this.downloadCompleteListener = downloadCompleteListener;
        return this;
    }

    public interface DownloadCompleteListener {
        void onDownloadComplete();

        void onDownloadFailure();
    }

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (referenceId != -1) {
                if (downloadCompleteListener != null) {
                    downloadCompleteListener.onDownloadComplete();
                }
            } else {
                if (downloadCompleteListener != null) {
                    downloadCompleteListener.onDownloadFailure();
                }
            }
            context.unregisterReceiver(downloadReceiver);
        }
    };
}

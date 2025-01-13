package com.nojom.ui.projects;

import static android.app.Activity.RESULT_OK;
import static com.nojom.util.Constants.API_CAMP_ATTACH;
import static com.nojom.util.Constants.API_CAMP_ATTACH_LINK;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.adapter.FileAdapter;
import com.nojom.api.APIRequest;
import com.nojom.api.CampaignListener;
import com.nojom.databinding.DialogAddLinkCopyBinding;
import com.nojom.databinding.DialogPickFileBinding;
import com.nojom.databinding.FragmentCampFilesBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.CampFile;
import com.nojom.model.CampList;
import com.nojom.model.CampListData;
import com.nojom.model.CampaignUrls;
import com.nojom.util.CompressFile;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CampFileFragment extends BaseFragment implements CampaignListener {
    private FragmentCampFilesBinding binding;
    private CampList campList;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    ArrayList<CampFile> campFiles = new ArrayList<>();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camp_files, container, false);
        campList = ((CampaignDetailActivity2) activity).campList;
        initData();
        return binding.getRoot();
    }

    private void initData() {
        binding.relPickFile.setOnClickListener(view -> {
            int selTab = ((CampaignDetailActivity2) activity).selectedTab;
            if (selTab == 0) {
                dialogPickFile();
            } else {
                activity.toastMessage("Action not allowed for pending/rejected campaigns");
            }
        });
        binding.relPick.setOnClickListener(view -> {
            int selTab = ((CampaignDetailActivity2) activity).selectedTab;
            if (selTab == 0) {
                dialogPickFile();
            } else {
                activity.toastMessage("Action not allowed for pending/rejected campaigns");
            }
        });
        binding.linSubmit.setOnClickListener(view -> {
            if (campFiles != null && campFiles.size() > 0) {
                uploadCampFiles();
            }
        });

        if (campList.star_details != null && campList.star_details.attachments != null) {
            renderFileView(campList.star_details.attachments);
        }

        // Register the ActivityResultLauncher
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                List<String> filePaths = new ArrayList<>();

                if (data.getClipData() != null) {
                    // Multiple files selected
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {

                        Uri fileUri = data.getClipData().getItemAt(i).getUri();
                        String filePath = getFilePathFromUri(fileUri);
                        if (filePath != null) {
                            filePaths.add(filePath);
                        }
                    }
                } else if (data.getData() != null) {
                    // Single file selected
                    Uri fileUri = data.getData();
                    String filePath = getFilePathFromUri(fileUri);
                    if (filePath != null) {
                        filePaths.add(filePath);
                    }
                }

                // Log the file paths
                for (String path : filePaths) {
                    CampFile cmpF = new CampFile();
                    Log.d("FilePicker", "File Path: " + path);
                    File file = new File(path);

                    // Check if file exists
                    if (file.exists()) {
                        // Get the filename
                        String filename = file.getName();

                        // Get the file size in bytes
                        long fileSize = file.length();

                        // Output the details
                        System.out.println("Filename: " + filename);
                        System.out.println("File Size: " + fileSize + " bytes");
                        cmpF.fileSize = fileSize;
                        cmpF.fileName = filename;
                        cmpF.isImage = true;
                    }
                    cmpF.filepath = path;
                    campFiles.add(cmpF);
                }

                setAdapter();
            }
        });
    }

    private void renderFileView(List<String> attachments) {

        if (attachments != null && attachments.size() > 0) {
            for (String link : attachments) {
                CampFile campFile = new CampFile();

                campFile.fileName = getFileNameFromUrl(link);
                if (campFile.fileName != null && (campFile.fileName.endsWith(".jpg") || campFile.fileName.endsWith(".jpeg") || campFile.fileName.endsWith(".png") || campFile.fileName.endsWith(".pdf") || campFile.fileName.endsWith(".xls"))) {
                    campFile.isImage = true;
                }
                campFile.filepath = link;
                campFile.fileSize = 0;
                campFile.isUploaded = true;
                campFiles.add(campFile);
            }
            setAdapter();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void dialogPickFile() {
        final Dialog dialog = new Dialog(activity, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        DialogPickFileBinding pickFileBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_pick_file, null, false);
        dialog.setCancelable(true);
        dialog.setContentView(pickFileBinding.getRoot());

        pickFileBinding.tvCancel.setOnClickListener(view -> dialog.dismiss());

        pickFileBinding.relLink.setOnClickListener(view -> {
            dialog.dismiss();
            dialogAddLink();
        });
        pickFileBinding.relFile.setOnClickListener(view -> {
            dialog.dismiss();
            pickFiles();
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    public void dialogAddLink() {
        final Dialog dialog = new Dialog(activity, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        DialogAddLinkCopyBinding pickFileBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_add_link_copy, null, false);
        dialog.setCancelable(true);
        dialog.setContentView(pickFileBinding.getRoot());

        pickFileBinding.imgCancel.setOnClickListener(view -> dialog.dismiss());

        pickFileBinding.tvAdd.setOnClickListener(view -> {

            if (TextUtils.isEmpty(pickFileBinding.etLink.getText().toString().trim())) {
                activity.toastMessage(getString(R.string.add_link));
                return;
            }

            CampFile campFile = new CampFile();
            campFile.isImage = false;
            campFile.isLink = true;
            campFile.fileName = getFileNameFromUrl(pickFileBinding.etLink.getText().toString());
            campFile.filepath = pickFileBinding.etLink.getText().toString();
            campFiles.add(campFile);
            setAdapter();
            dialog.dismiss();
        });
        pickFileBinding.tvCancel.setOnClickListener(view -> dialog.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    private void setAdapter() {
        FileAdapter fileAdapter = new FileAdapter(activity, campFiles, "");
        binding.rvFiles.setAdapter(fileAdapter);

        binding.linFiles.setVisibility(View.VISIBLE);
        binding.linSubmit.setVisibility(View.VISIBLE);
        binding.relPickFile.setVisibility(View.GONE);
    }

    private void pickFiles() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow all file types
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple selection
        filePickerLauncher.launch(Intent.createChooser(intent, "Select Files"));
    }

    // Get the file path from a URI
    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        Cursor cursor = null;

        try {
            // Check if the URI scheme is "content"
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {MediaStore.MediaColumns.DATA};
                cursor = activity.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    filePath = cursor.getString(columnIndex);
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                filePath = uri.getPath();
            }

            // For Scoped Storage (API 29+), copy to app's private directory if needed
            if (filePath == null) {
                filePath = copyUriToFile(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return filePath;
    }

    // Copy the file from URI to app's private storage
    private String copyUriToFile(Uri uri) {
        String filePath = null;
        try {
            String fileName = getFileNameFromUri(uri);
            File tempFile = new File(activity.getCacheDir(), fileName);
            try (InputStream inputStream = activity.getContentResolver().openInputStream(uri); OutputStream outputStream = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
            }
            filePath = tempFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    // Get the file name from a URI
    private String getFileNameFromUri(Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            String fileName = cursor.getString(nameIndex);
            cursor.close();
            return fileName;
        }
        return null;
    }

    private void uploadCampFiles() {
        MultipartBody.Part[] body = null;
        long fileSize = 0;
        boolean isOnlyLink = true;
        if (campFiles != null && campFiles.size() > 0) {
            body = new MultipartBody.Part[campFiles.size()];
            for (int i = 0; i < campFiles.size(); i++) {
                File file;
                if (!campFiles.get(i).isLink && !campFiles.get(i).isUploaded) {
                    isOnlyLink = false;
                    if (campFiles.get(i).filepath.contains(".jpg") || campFiles.get(i).filepath.contains(".png") || campFiles.get(i).filepath.contains(".jpeg")) {
                        file = CompressFile.getCompressedImageFile(new File(campFiles.get(i).filepath));
                    } else {
                        file = new File(campFiles.get(i).filepath);
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
                        headers.addUnsafeNonAscii("Content-Disposition", "form-data; name=\"attachments\"; filename=\"" + file.getName() + "\"");

                        if (requestFile != null) {
                            body[i] = MultipartBody.Part.create(headers.build(), requestFile);
                        }

                        //fileSize += getFolderSize(file);
                    }
                }
//                Log.e("MIME TYPE", "" + mimeType + " ----- " + MediaType.parse(mimeType));
            }
        }

        if (isOnlyLink) {
            uploadUrls(campList.campaignId, new ArrayList<>());
        } else {
            APIRequest apiRequest = new APIRequest();
            apiRequest.uploadCampAttachment(activity, API_CAMP_ATTACH, this, body);
        }
    }

    private void uploadUrls(int campId, List<String> urls) {

        if (campFiles != null && campFiles.size() > 0) {
            for (CampFile campFile : campFiles) {
                if (campFile.isLink) {
                    urls.add(campFile.filepath);
                }
            }
        }

        CampaignUrls campaignUrls = new CampaignUrls();
        campaignUrls.attachment_urls = urls;

        if (campaignUrls.attachment_urls != null && campaignUrls.attachment_urls.size() > 0) {
            APIRequest apiRequest = new APIRequest();
            String url = API_CAMP_ATTACH_LINK + campId + "/attachments";
            apiRequest.uploadCampAttachUrls(this, activity, url, campaignUrls);
        }
    }

    @Override
    public void successResponse(CampListData responseBody, String url, String message) {
        if (url.equals(API_CAMP_ATTACH)) {
            //after upload file need one more API call
            activity.toastMessage(message);
            //add link which is added by user and direct upload to this below APIs

            uploadUrls(campList.campaignId, responseBody.urls);
        } else if (url.equals(this.url)) {

            if (campList.star_details.star_id.equals(responseBody.star_details.id)) {
                if (responseBody.star_details.attachments != null) {
                    campFiles = new ArrayList<>();
                    renderFileView(responseBody.star_details.attachments);
                }
            }

        } else {
            activity.toastMessage(message);
            getCampById(campList.campaignId);
        }
    }

    @Override
    public void failureResponse(Throwable throwable, String url, String message) {
        activity.toastMessage(message);
    }

    public String getFileNameFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            String path = url.getPath();
            return path.substring(path.lastIndexOf("/") + 1); // Extract file name from URL path
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    String url;

    private void getCampById(int campId) {

        APIRequest apiRequest = new APIRequest();
        url = API_CAMP_ATTACH_LINK + campId;
        apiRequest.getCampById(this, activity, url);
    }
}

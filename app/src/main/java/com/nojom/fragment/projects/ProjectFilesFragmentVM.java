package com.nojom.fragment.projects;

import static com.nojom.multitypepicker.activity.VideoPickActivity.IS_NEED_CAMERA;
import static com.nojom.util.Constants.CLIENT_ATTACHMENT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahamed.multiviewadapter.SimpleRecyclerAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nojom.R;
import com.nojom.adapter.binder.FilesBinder;
import com.nojom.databinding.FragmentProjectFilesBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ProjectByID;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.activity.ImagePickActivity;
import com.nojom.multitypepicker.activity.NormalFilePickActivity;
import com.nojom.multitypepicker.activity.VideoPickActivity;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.multitypepicker.filter.entity.NormalFile;
import com.nojom.multitypepicker.filter.entity.VideoFile;
import com.nojom.ui.projects.ProjectDetailsActivity;
import com.nojom.util.CompressFile;
import com.nojom.util.Utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

class ProjectFilesFragmentVM extends AndroidViewModel implements View.OnClickListener {
    private final FragmentProjectFilesBinding binding;
    private final BaseFragment fragment;
    private ProjectByID projectData;
    private List<ProjectByID.Attachments> filesModelList;
    private SimpleRecyclerAdapter myFilesAdapter;
    private boolean isMyFileShowAll = false;
    private ArrayList<File> fileList;

    ProjectFilesFragmentVM(Application application, FragmentProjectFilesBinding filesBinding, BaseFragment projectFilesFragment) {
        super(application);
        binding = filesBinding;
        fragment = projectFilesFragment;
        initData();
    }

    private void initData() {
        binding.addFiles.setOnClickListener(this);
        binding.imgMyShowall.setOnClickListener(this);
        binding.imgWriterShowall.setOnClickListener(this);
        if (fragment.activity != null) {
            projectData = ((ProjectDetailsActivity) fragment.activity).getProjectData();
        }
        binding.addFiles.setVisibility(View.GONE);

        binding.noData.tvNoTitle.setText(fragment.getString(R.string.no_files));
        binding.noData.tvNoDescription.setText(fragment.activity.getString(R.string.no_attached_file_desc));

        if (projectData != null) {
            filesModelList = new ArrayList<>();
            List<ProjectByID.Attachments> attachmentsList = new ArrayList<>();
            //wrFilesModelList = new ArrayList<>();

            binding.imgMyShowall.setPaintFlags(binding.imgMyShowall.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            binding.rvMyFiles.setLayoutManager(new LinearLayoutManager(fragment.activity));
            //rvWriterFiles.setLayoutManager(new LinearLayoutManager(activity));
            attachmentsList = projectData.attachments;
            setFileAdapter();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_files:
                selectFileDialog();
                break;
            case R.id.img_my_showall:
                isMyFileShowAll = true;
                binding.imgMyShowall.setVisibility(View.GONE);
                setFileAdapter();
                break;
            case R.id.img_writer_showall:
                binding.imgWriterShowall.setVisibility(View.GONE);
                //setWriterFileAdapter();
                break;
        }

    }

    private void selectFileDialog() {
        if (fragment.activity == null)
            return;

        @SuppressLint("PrivateResource") final Dialog dialog = new Dialog(fragment.activity, R.style.Theme_Design_Light_BottomSheetDialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_camera_document_select);
        dialog.setCancelable(true);
        TextView tvCancel = dialog.findViewById(R.id.btn_cancel);
        LinearLayout llCamera = dialog.findViewById(R.id.ll_camera);
        //LinearLayout llVideo = dialog.findViewById(R.id.ll_video);
        LinearLayout llDocument = dialog.findViewById(R.id.ll_document);

        llCamera.setOnClickListener(v -> {
            checkPermission(false, false);
            dialog.dismiss();
        });

        llDocument.setOnClickListener(v -> {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                    fragment.activity.openDocuments(fragment.activity, 5);
//                } else {
//                    Intent intent = new Intent();
//                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                    Uri uri = Uri.fromParts("package", fragment.activity.getPackageName(), null);
//                    intent.setData(uri);
//                    fragment.startActivity(intent);
//                }
//            } else {
            checkPermission(true, false);
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

    public void checkPermission(final boolean isDocument, final boolean isVideo) {
        Dexter.withActivity(fragment.activity)
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
                                    fragment.activity.openDocuments(fragment, 1);
                                } else {
                                    Intent intent = new Intent(fragment.activity, NormalFilePickActivity.class);
                                    intent.putExtra(Constant.MAX_NUMBER, 5);
                                    intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"xlsx", "xls", "doc", "docx", "ppt", "pptx", "pdf"});
                                    fragment.startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                                }
                            } else if (isVideo) {
                                Intent intent = new Intent(fragment.activity, VideoPickActivity.class);
                                intent.putExtra(IS_NEED_CAMERA, true);
                                intent.putExtra(Constant.MAX_NUMBER, 2);
                                fragment.startActivityForResult(intent, Constant.REQUEST_CODE_PICK_VIDEO);
                            } else {
                                Intent intent = new Intent(fragment.activity, ImagePickActivity.class);
                                intent.putExtra(IS_NEED_CAMERA, true);
                                intent.putExtra(Constant.MAX_NUMBER, 5);
                                fragment.startActivityForResult(intent, Constant.REQUEST_CODE_PICK_IMAGE);
                            }
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            fragment.activity.toastMessage("Please give permission");
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

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        fileList = new ArrayList<>();
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<NormalFile> docPaths = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    if (docPaths != null && docPaths.size() > 0) {
                        for (NormalFile file : docPaths) {
                            fileList.add(new File(file.getPath()));
                        }
                        Log.e("Doc Path == > ", docPaths.get(0).getPath());
                        uploadFile();
                    } else {
                        fragment.activity.toastMessage("File not selected");
                    }
                }
                break;
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<ImageFile> imgPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    if (imgPath != null && imgPath.size() > 0) {
                        for (ImageFile file : imgPath) {
                            fileList.add(new File(file.getPath()));
                        }
                        Log.e("Image Path == > ", imgPath.get(0).getPath());
                        uploadFile();
                    } else {
                        fragment.activity.toastMessage("File not selected");
                    }
                }
                break;
            case Constant.REQUEST_CODE_PICK_VIDEO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<VideoFile> videoPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                    if (videoPath != null && videoPath.size() > 0) {
                        for (VideoFile file : videoPath) {
                            fileList.add(new File(file.getPath()));
                        }
                        Log.e("Video Path == > ", videoPath.get(0).getPath());
                        uploadFile();
                    } else {
                        fragment.activity.toastMessage("File not selected");
                    }
                }

                break;
            case 4545://doc picker for android 10+
                try {
//                    String fileName = getFileName(data.getData());
                    String path = null;
                    if (data != null && data.getData() != null) {
                        path = Utils.getFilePath(fragment.activity, data.getData());

                        if (path != null) {
                            fileList.add(new File(path));
                            uploadFile();
                        } else {
                            fragment.activity.toastMessage("File not selected");
                        }
                    }


                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void uploadFile() {
        if (!fragment.activity.isNetworkConnected())
            return;

//        fragment.activity.showProgress();

        MultipartBody.Part[] body;
        if (fileList != null && fileList.size() > 0) {
            body = new MultipartBody.Part[fileList.size()];
            for (int i = 0; i < fileList.size(); i++) {
                File file;
                if (fileList.get(i).getAbsolutePath().contains(".jpg") || fileList.get(i).getAbsolutePath().contains(".png")
                        || fileList.get(i).getAbsolutePath().contains(".jpeg")) {
                    file = CompressFile.getCompressedImageFile(fileList.get(i));
                } else {
                    file = fileList.get(i);
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
                    headers.addUnsafeNonAscii("Content-Disposition", "form-data; name=\"file[]\"; filename=\"" + file.getName() + "\"");

                    if (requestFile != null) {
                        body[i] = MultipartBody.Part.create(headers.build(), requestFile);
                    }
                }
            }
        }
    }

    private void setFileAdapter() {
        if (projectData.attachments != null && projectData.attachments.size() > 0) {
            filesModelList.clear();
            if (!isMyFileShowAll) {
                if (projectData.attachments.size() > 3) {
                    for (int i = 0; i < 3; i++) {
                        filesModelList.add(projectData.attachments.get(i));
                    }
                    binding.imgMyShowall.setVisibility(View.VISIBLE);
                } else {
                    filesModelList.addAll(projectData.attachments);
                }
            } else {
                filesModelList.addAll(projectData.attachments);
            }

            if (myFilesAdapter == null) {
                myFilesAdapter = new SimpleRecyclerAdapter<>(new FilesBinder(CLIENT_ATTACHMENT));
            }

            if (binding.rvMyFiles.getAdapter() == null) {
                binding.rvMyFiles.setAdapter(myFilesAdapter);
            }

            myFilesAdapter.setData(filesModelList);
            binding.rvMyFiles.setVisibility(View.VISIBLE);
            binding.noData.llNoData.setVisibility(View.GONE);
        } else {
            binding.rvMyFiles.setVisibility(View.GONE);
            binding.noData.llNoData.setVisibility(View.VISIBLE);
        }
    }
}

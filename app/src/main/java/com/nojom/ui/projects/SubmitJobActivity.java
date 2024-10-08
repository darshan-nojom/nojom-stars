package com.nojom.ui.projects;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nojom.R;
import com.nojom.adapter.UploadFileAdapter;
import com.nojom.databinding.ActivitySubmitJobBinding;
import com.nojom.model.Attachment;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.multitypepicker.filter.entity.NormalFile;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class SubmitJobActivity extends BaseActivity {

    private SubmitJobActivityVM submitJobActivityVM;
    private ActivitySubmitJobBinding binding;
    private UploadFileAdapter uploadFileAdapter;
    private String projectBidId;
    private ArrayList<Attachment> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_submit_job);
        submitJobActivityVM = ViewModelProviders.of(this).get(SubmitJobActivityVM.class);
        submitJobActivityVM.init(this);
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.tvAttachFile.setOnClickListener(v -> submitJobActivityVM.selectFileDialog());

        binding.tvSubmitJob.setOnClickListener(v -> {
            if (TextUtils.isEmpty(getDescription())) {
                validationError(getString(R.string.please_enter_description));
                return;
            }

            if (fileList == null || fileList.size() == 0) {
                validationError(getString(R.string.atleast_one_file));
                return;
            }

            submitJobActivityVM.submitJob(projectBidId, getDescription());
        });

        if (getIntent() != null) {
            projectBidId = getIntent().getStringExtra(Constants.PROJECT_BID_ID);
        }

        fileList = new ArrayList<>();

        binding.rvFiles.setLayoutManager(new LinearLayoutManager(this));

        submitJobActivityVM.getIsShowProgress().observe(this, isShowProgress -> {
            disableEnableTouch(isShowProgress);
            if (isShowProgress) {
                binding.tvSubmitJob.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.tvSubmitJob.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private String getDescription() {
        return binding.etDescribe.getText().toString().trim();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<NormalFile> docPaths = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    if (docPaths != null && docPaths.size() > 0) {
                        Log.e("Doc Path == > ", docPaths.get(0).getPath());
                        for (NormalFile file : docPaths) {
                            fileList.add(new Attachment(file.getPath(), false));
                        }
                        submitJobActivityVM.getListMutableLiveData().setValue(fileList);
                        Preferences.writeString(this, Constants.ATTACH_FILE, docPaths.get(0).getPath());
                        setFileAdapter(fileList);
                    } else {
                        toastMessage(getString(R.string.file_not_selected));
                    }
                }
                break;
            case Constant.REQUEST_CODE_PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<ImageFile> imgPath = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                    if (imgPath != null && imgPath.size() > 0) {
                        Log.e("Image Path == > ", imgPath.get(0).getPath());
                        for (ImageFile file : imgPath) {
                            fileList.add(new Attachment(file.getPath(), true));
                        }
                        submitJobActivityVM.getListMutableLiveData().setValue(fileList);
                        Preferences.writeString(this, Constants.ATTACH_FILE, imgPath.get(0).getPath());
                        setFileAdapter(fileList);
                    } else {
                        toastMessage(getString(R.string.file_not_selected));
                    }
                }
                break;
            case 4545://doc picker for android 10+
                try {
//                    String fileName = getFileName(data.getData());
                    String path = null;
                    if (data != null && data.getData() != null) {
                        path = Utils.getFilePath(this, data.getData());
                    }

                    if (path != null) {
                        Log.e("Doc Path == > ", path);
                        fileList.add(new Attachment(path, false));
                        submitJobActivityVM.getListMutableLiveData().setValue(fileList);
                        Preferences.writeString(this, Constants.ATTACH_FILE, path);
                        setFileAdapter(fileList);
                    } else {
                        toastMessage(getString(R.string.file_not_selected));
                    }

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setFileAdapter(ArrayList<Attachment> uploadedfiles) {
        this.fileList = uploadedfiles;
        if (uploadedfiles != null && uploadedfiles.size() > 0) {
            if (uploadFileAdapter == null) {
                uploadFileAdapter = new UploadFileAdapter(this);
                uploadFileAdapter.setOnFileDeleteListener(this::onFileDelete);
            }

            uploadFileAdapter.doRefresh(uploadedfiles);

            if (binding.rvFiles.getAdapter() == null) {
                binding.rvFiles.setAdapter(uploadFileAdapter);
            }
        } else {
            if (uploadFileAdapter != null) {
                uploadFileAdapter.doRefresh(uploadedfiles);
            }
        }
    }

    private void onFileDelete(ArrayList<Attachment> mDataset) {
        setFileAdapter(mDataset);
    }

}

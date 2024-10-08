package com.nojom.ui.jobs;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahamed.multiviewadapter.SimpleRecyclerAdapter;
import com.nojom.R;
import com.nojom.adapter.binder.FilesBinder;
import com.nojom.databinding.ActivityEmployerFilesBinding;
import com.nojom.model.ProjectByID;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class EmployerFilesActivity extends BaseActivity {
    private ActivityEmployerFilesBinding binding;
    private List<ProjectByID.Attachments> filesModelList;
    private SimpleRecyclerAdapter myFilesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_employer_files);
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(view -> onBackPressed());
        filesModelList = new ArrayList<>();
        if (getIntent() != null) {
            filesModelList = (List<ProjectByID.Attachments>) getIntent().getSerializableExtra(Constants.ATTACH_FILE);
        }

        binding.rvMyFiles.setLayoutManager(new LinearLayoutManager(this));
        setFileAdapter();
    }

    private void setFileAdapter() {
        if (filesModelList != null && filesModelList.size() > 0) {
            if (myFilesAdapter == null) {
                myFilesAdapter = new SimpleRecyclerAdapter<>(new FilesBinder(CLIENT_ATTACHMENT));
            }

            if (binding.rvMyFiles.getAdapter() == null) {
                binding.rvMyFiles.setAdapter(myFilesAdapter);
            }
            myFilesAdapter.setData(filesModelList);
        }
    }

}

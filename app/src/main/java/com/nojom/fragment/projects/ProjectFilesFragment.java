package com.nojom.fragment.projects;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.FragmentProjectFilesBinding;
import com.nojom.fragment.BaseFragment;

public class ProjectFilesFragment extends BaseFragment {

    private ProjectFilesFragmentVM projectFilesFragmentVM;
    private FragmentProjectFilesBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_project_files, container, false);
        projectFilesFragmentVM = new ProjectFilesFragmentVM(Task24Application.getActivity(), binding, this);
        return binding.getRoot();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        projectFilesFragmentVM.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

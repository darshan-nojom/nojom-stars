package com.nojom.fragment.projects;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.FragmentProjectRateBinding;
import com.nojom.fragment.BaseFragment;

public class ProjectRateFragment extends BaseFragment {

    private ProjectRateFragmentVM projectRateFragmentVM;
    private FragmentProjectRateBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_project_rate, container, false);
        projectRateFragmentVM = new ProjectRateFragmentVM(Task24Application.getActivity(), binding, this);
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        projectRateFragmentVM.onActivityResult(requestCode, resultCode, data);
    }

    public void refreshPage() {
        projectRateFragmentVM.initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

package com.nojom.ui.gigs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.FragmentProjectSubmitBinding;
import com.nojom.fragment.BaseFragment;

public class ContractSubmitFragment extends BaseFragment {
    private ContractSubmitFragmentVM projectSubmitFragmentVM;
    private FragmentProjectSubmitBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_project_submit, container, false);
        projectSubmitFragmentVM = new ContractSubmitFragmentVM(Task24Application.getActivity(), binding, this);
        return binding.getRoot();
    }

    public void refreshPage() {
        if (projectSubmitFragmentVM != null) {
            projectSubmitFragmentVM.initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

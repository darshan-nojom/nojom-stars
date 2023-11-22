package com.nojom.ui.gigs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.FragmentProjectStatusBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ContractDetails;

public class ContractStatusFragment extends BaseFragment {

    private ContractStatusFragmentVM projectStatusFragmentVM;
    private FragmentProjectStatusBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_project_status, container, false);
        projectStatusFragmentVM = new ContractStatusFragmentVM(Task24Application.getActivity(), binding, this);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        projectStatusFragmentVM.onPauseMathod();
        binding = null;
    }

    public void refreshBidPrice() {
        if (projectStatusFragmentVM != null) {
            if (activity != null) {
                ContractDetails projectData = ((ContractDetailsActivity) activity).getProjectData();
                projectStatusFragmentVM.updateBidPrice(projectData);
            }
        }
    }
}

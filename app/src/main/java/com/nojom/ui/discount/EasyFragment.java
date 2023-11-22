package com.nojom.ui.discount;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.FragmentWinBinding;
import com.nojom.fragment.BaseFragment;

import org.jetbrains.annotations.NotNull;

public class EasyFragment extends BaseFragment {
    private EasyFragmentVM winFragmentVM;
    private FragmentWinBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_win, container, false);
        winFragmentVM = new EasyFragmentVM(Task24Application.getActivity(), binding, this);
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        winFragmentVM.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

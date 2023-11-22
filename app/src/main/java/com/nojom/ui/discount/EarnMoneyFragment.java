package com.nojom.ui.discount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.FragmentEarnMoneyBinding;
import com.nojom.fragment.BaseFragment;

import org.jetbrains.annotations.NotNull;

public class EarnMoneyFragment extends BaseFragment {
    private EarnMoneyFragmentVM earnMoneyFragmentVM;
    private FragmentEarnMoneyBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_earn_money, container, false);
        earnMoneyFragmentVM = new EarnMoneyFragmentVM(Task24Application.getActivity(), binding, this);
        return binding.getRoot();
    }

    void setLink() {
        if (earnMoneyFragmentVM != null) {
            earnMoneyFragmentVM.setLink();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

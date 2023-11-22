package com.nojom.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nojom.ui.BaseActivity;

public class BaseFragment extends Fragment {

    public BaseActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BaseActivity) getActivity();
    }

    public void popOffSubscreens() {
        while (activity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            activity.getSupportFragmentManager().popBackStackImmediate();
        }
    }

    protected void goBack() {
        activity.getSupportFragmentManager().popBackStack();
    }

    public void goBackTo() {
        activity.getSupportFragmentManager().popBackStack();
    }

    protected void goTo(String className) {
        activity.getSupportFragmentManager().popBackStack(className, 0);
    }

    public void goToBack(String className) {
        activity.getSupportFragmentManager().popBackStack(className, 0);
    }
}

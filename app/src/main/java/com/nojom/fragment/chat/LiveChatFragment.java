package com.nojom.fragment.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.FragmentLiveChatBinding;
import com.nojom.fragment.BaseFragment;

public class LiveChatFragment extends BaseFragment {

    private FragmentLiveChatBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live_chat, container, false);
        new LiveChatFragmentVM(Task24Application.getActivity(), binding,activity);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

package com.nojom.fragment.chat;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.nojom.databinding.FragmentLiveChatBinding;
import com.nojom.ui.BaseActivity;

import io.intercom.android.sdk.Intercom;

class LiveChatFragmentVM extends AndroidViewModel {
    private FragmentLiveChatBinding binding;
    BaseActivity activity;

    LiveChatFragmentVM(Application application, FragmentLiveChatBinding liveChatBinding, BaseActivity activity) {
        super(application);
        binding = liveChatBinding;
        this.activity = activity;
        initData();
    }

    private void initData() {
        binding.llChatItem.setOnClickListener(view -> Intercom.client().displayMessageComposer());
    }
}

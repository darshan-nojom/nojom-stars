package com.nojom.fragment.chat;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.nojom.databinding.FragmentLiveChatBinding;

import io.intercom.android.sdk.Intercom;

class LiveChatFragmentVM extends AndroidViewModel {
    private FragmentLiveChatBinding binding;

    LiveChatFragmentVM(Application application, FragmentLiveChatBinding liveChatBinding) {
        super(application);
        binding = liveChatBinding;
        initData();
    }

    private void initData() {
        binding.llChatItem.setOnClickListener(view -> Intercom.client().displayMessageComposer());
    }
}

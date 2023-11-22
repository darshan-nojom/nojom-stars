package com.nojom.fragment.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.FragmentChatListBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ChatList;
import com.nojom.ui.chat.chatInterface.ChatInterface;
import com.nojom.ui.chat.chatInterface.NewMessageForList;
import com.nojom.ui.chat.chatInterface.OnlineOfflineListener;

import org.jetbrains.annotations.NotNull;

public class ChatListFragment extends BaseFragment implements ChatInterface, OnlineOfflineListener,
        NewMessageForList {
    private ChatListFragmentVM chatListFragmentVM;
    private FragmentChatListBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity.setChatInterface(this);
        activity.setOnlineOfflineListener(this);
        activity.setNewMessageForList(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_list, container, false);
        chatListFragmentVM = new ChatListFragmentVM(Task24Application.getActivity(), binding, this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.mSocket.off("getMessageSeenEvent");
        chatListFragmentVM.onResumeMethod();
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.mSocket.off("getMessage");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onConnect(boolean isConnect) {

    }

    @Override
    public void disconnect(boolean isConnect) {
        chatListFragmentVM.disconnect(isConnect);
    }

    @Override
    public void onError(Object args) {
        chatListFragmentVM.onError(args);
    }

    @Override
    public void getNewMessage(Object args) {

    }


    @Override
    public void onlineUser(ChatList.Datum args) {
        if (chatListFragmentVM != null) {
            chatListFragmentVM.manageUserStatus(args, 1);
        }
    }

    @Override
    public void offlineUsr(ChatList.Datum args) {
        if (chatListFragmentVM != null) {
            chatListFragmentVM.manageUserStatus(args, 0);
        }
    }
}

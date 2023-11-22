package com.nojom.fragment.chat;

import static com.nojom.util.Constants.AGENT_PROFILE;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.nojom.R;
import com.nojom.adapter.ChatListAdapter;
import com.nojom.databinding.FragmentChatListBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ChatList;
import com.nojom.model.ChatMessageList;
import com.nojom.model.Typing;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ChatListFragmentVM extends AndroidViewModel {
    private FragmentChatListBinding binding;
    private BaseFragment fragment;
    private ChatListAdapter chatListAdapter;
    private List<ChatList.Datum> arrChatList;
    private String imgPath = "";

    ChatListFragmentVM(Application application, FragmentChatListBinding chatListBinding, BaseFragment chatListFragment) {
        super(application);
        binding = chatListBinding;
        fragment = chatListFragment;
        initData();
    }

    private void initData() {

        binding.noData.tvNoTitle.setText(fragment.getString(R.string.no_messages));
        binding.noData.tvNoDescription.setText(fragment.getString(R.string.chat_msg_desc));
        //swipe refresh
        binding.swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragment.activity);
        binding.rvChatList.setLayoutManager(linearLayoutManager);
    }

    SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (chatListAdapter == null || chatListAdapter.getItemCount() == 0) {
                binding.shimmerLayout.setVisibility(View.VISIBLE);
                binding.shimmerLayout.startShimmer();
            }
            getAllUser();
        }
    };

    private void getAllChatList() {
        if (chatListAdapter == null || chatListAdapter.getItemCount() == 0) {
            binding.shimmerLayout.setVisibility(View.VISIBLE);
            binding.shimmerLayout.startShimmer();
        }
        getAllUser();
    }


    void onResumeMethod() {
        if (fragment.activity.isLogin()) {
            getAllChatList();
            getNewMessage();
            getTyping();
        } else {
            binding.linPlaceholderLogin.setVisibility(View.VISIBLE);
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.rvChatList.setVisibility(View.GONE);
            binding.btnLogin.setOnClickListener(v -> fragment.activity.openLoginDialog());
        }
    }

    private void setAdapter() {
        if (arrChatList != null && arrChatList.size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            chatListAdapter = new ChatListAdapter(fragment.activity, arrChatList, imgPath);
            binding.rvChatList.setAdapter(chatListAdapter);
        } else {
            binding.noData.llNoData.setVisibility(View.VISIBLE);
            if (chatListAdapter != null)
                chatListAdapter.doRefresh(arrChatList);
        }
        binding.rvChatList.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setRefreshing(false);
    }


    public void disconnect(boolean isConnect) {
        Log.e("AAAAA", "disconnect..." + isConnect);
    }

    public void onError(Object args) {
        Log.d("AAAAA", "onError..." + args.toString());
    }

    public void getNewMessage() {
        fragment.activity.mSocket.on("getMessage", args -> {
            Log.e("AAAAAA", "getMessageChatList" + args[0].toString());
            fragment.activity.runOnUiThread(() -> {
                try {
                    ChatMessageList.DataChatList newMessage = new Gson().fromJson(args[0].toString(), ChatMessageList.DataChatList.class);
                    if (newMessage != null) {
                        for (int i = 0; i < arrChatList.size(); i++) {
                            if (!newMessage.self) {
                                if (newMessage.senderId.equalsIgnoreCase(String.valueOf(arrChatList.get(i).id))) {
                                    updateChatListItem(i, newMessage);
                                    break;
                                }
                            } else {
                                if (newMessage.receiverId.equalsIgnoreCase(String.valueOf(arrChatList.get(i).id))) {
                                    updateChatListItem(i, newMessage);
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public void getAllUser() {
        if (!fragment.activity.isNetworkConnected()) {
            return;
        }
        arrChatList = new ArrayList<>();
        Call<ChatList> call = fragment.activity.getService().getUser(String.valueOf(fragment.activity.getUserID()), "" + AGENT_PROFILE);
        call.enqueue(new Callback<ChatList>() {
            @Override
            public void onResponse(@NonNull Call<ChatList> call, @NonNull Response<ChatList> response) {
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    ChatList user = response.body();
                    try {
                        if (user != null && user.data != null && user.data.data != null && user.data.data.size() > 0) {
                            arrChatList = user.data.data;
                            imgPath = user.data.path;
                        }
                        setAdapter();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    fragment.activity.logout();
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ChatList> call, @NonNull Throwable t) {
                fragment.activity.failureError("Get user list failed");
                binding.shimmerLayout.stopShimmer();
                binding.swipeRefreshLayout.setRefreshing(false);
                binding.shimmerLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getTyping() {
        fragment.activity.mSocket.on("getTyping", args -> {
            Log.e("AAAAA", "getTypingChatList" + args[0].toString());
            fragment.activity.runOnUiThread(() -> {
                Typing typing = new Gson().fromJson(args[0].toString(), Typing.class);
                if (arrChatList != null && arrChatList.size() > 0)
                    for (int i = 0; i < arrChatList.size(); i++) {
                        if (typing.senderId.equals(arrChatList.get(i).id)) {
                            arrChatList.get(i).typing = typing.type;
                            chatListAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
            });
        });
    }

//    public void loadTyping(Typing typing) {
//        fragment.activity.runOnUiThread(() -> {
//            if (arrChatList != null && arrChatList.size() > 0)
//                for (int i = 0; i < arrChatList.size(); i++) {
//                    if (typing.senderId.equals(arrChatList.get(i).id)) {
//                        arrChatList.get(i).typing = typing.type;
//                        chatListAdapter.notifyItemChanged(i);
//                        break;
//                    }
//                }
//        });
//    }

    public void loadNewMessage(ChatMessageList.DataChatList newMessage) {
        Log.e("AAAAAA", "getMessage " + newMessage.toString());
        fragment.activity.runOnUiThread(() -> {
            try {
//                ChatMessageList.DataChatList newMessage = new Gson().fromJson(args[0].toString(), ChatMessageList.DataChatList.class);
                for (int i = 0; i < arrChatList.size(); i++) {
                    if (!newMessage.self) {
                        if (newMessage.senderId.equalsIgnoreCase(String.valueOf(arrChatList.get(i).id))) {
                            updateChatListItem(i, newMessage);
                            break;
                        }
                    } else {
                        if (newMessage.receiverId.equalsIgnoreCase(String.valueOf(arrChatList.get(i).id))) {
                            updateChatListItem(i, newMessage);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void manageUserStatus(ChatList.Datum moUserStatus, int status) {
        fragment.activity.runOnUiThread(() -> {
            if (moUserStatus != null && arrChatList != null && arrChatList.size() > 0) {
                for (int i = 0; i < arrChatList.size(); i++) {
                    if (moUserStatus.id.equals(arrChatList.get(i).id)) {
                        arrChatList.get(i).isSocketOnline = status;// moUserStatus.status;
                        chatListAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
    }

    private void updateChatListItem(int position, ChatMessageList.DataChatList newMessage) {
        try {
            if (newMessage != null) {
                arrChatList.get(position).lastMessageData.message = newMessage.message;
                arrChatList.get(position).lastMessageData.messageCreatedAt = newMessage.messageCreatedAt;
                arrChatList.get(position).lastMessageData.isSeenMessage = newMessage.isSeenMessage;
                if (newMessage.message == null) {
                    if (newMessage.file.files.get(0).file != null) {

                        if (newMessage.file.files.get(0).fileStorage != null && newMessage.file.files.get(0).fileStorage.equalsIgnoreCase("firebase")) {//firebase URL
                            ChatList.FileImages fileImages = new ChatList.FileImages();
                            fileImages.file = newMessage.file.files.get(0).firebaseUrl;
                            arrChatList.get(position).lastMessageData.file.files.set(0, fileImages);
                        } else {
                            ChatList.FileImages fileImages = new ChatList.FileImages();
                            fileImages.file = newMessage.file.files.get(0).file;
                            arrChatList.get(position).lastMessageData.file.files.set(0, fileImages);
                        }


                    }
                }
                arrChatList.get(position).isSocketOnline = Integer.parseInt(newMessage.isSeenMessage);
                //arrChatList.get(position).isDeleted = (newMessage.isMessageDeleted);
                chatListAdapter.notifyItemChanged(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.nojom.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.databinding.ActivityChatMessagesBinding;
import com.nojom.model.ChatList;
import com.nojom.model.CreateOfferResponse;
import com.nojom.multitypepicker.Constant;
import com.nojom.multitypepicker.filter.entity.AudioFile;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.multitypepicker.filter.entity.NormalFile;
import com.nojom.multitypepicker.filter.entity.VideoFile;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.chat.chatInterface.OnlineOfflineListener;
import com.nojom.util.Constants;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class ChatMessagesActivity extends BaseActivity implements OnlineOfflineListener {

    private ChatMessagesActivityVM chatMessagesActivityVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        setOnlineOfflineListener(this);
        ActivityChatMessagesBinding chatMessagesBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_messages);
        chatMessagesActivityVM = new ChatMessagesActivityVM(Task24Application.getActivity(), chatMessagesBinding, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatMessagesActivityVM.onResumeMethod();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Preferences.writeString(this, Constants.CHAT_OPEN_ID, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("loadMessageHistory");
//        mSocket.off("getMessageSeenEvent");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_CODE_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<ImageFile> imgPaths = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                if (imgPaths != null && imgPaths.size() > 0) {
                    chatMessagesActivityVM.showProgress(imgPaths.size());
                    for (ImageFile imageFile : imgPaths) {
                        chatMessagesActivityVM.onFileSelect(new File(imageFile.getPath()), imageFile.getName());
                    }
                }
            }
        } else if (requestCode == Constant.REQUEST_CODE_PICK_FILE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                if (list != null && list.size() > 0) {
                    chatMessagesActivityVM.showProgress(list.size());
                    for (NormalFile imageFile : list) {
                        chatMessagesActivityVM.onFileSelect(new File(imageFile.getPath()), imageFile.getName());
                    }
                }
            }
        } else if (requestCode == 4545) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                try {
                    if (data.getData() != null) {
                        String fileName = Utils.getFileName(this, data.getData());
                        String path = null;
                        path = Utils.getFilePath(this, data.getData());

                        if (path != null) {
                            Log.e("Doc Path == > ", path);
                            chatMessagesActivityVM.showProgress(1);
                            chatMessagesActivityVM.onFileSelect(new File(path), fileName);
                        } else {
                            toastMessage(getString(R.string.file_not_selected));
                        }
                    }

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == Constant.REQUEST_CODE_PICK_VIDEO) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<VideoFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                if (list != null && list.size() > 0) {
                    chatMessagesActivityVM.showProgress(list.size());
                    for (VideoFile imageFile : list) {
                        chatMessagesActivityVM.onFileSelect(new File(imageFile.getPath()), imageFile.getName());
                    }
                }

            }
        } else if (requestCode == Constant.REQUEST_CODE_PICK_AUDIO) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
                if (list != null && list.size() > 0) {
                    chatMessagesActivityVM.showProgress(list.size());
                    for (AudioFile imageFile : list) {
                        chatMessagesActivityVM.onFileSelect(new File(imageFile.getPath()), imageFile.getName());
                    }
                }

            }
        } else {
            CreateOfferResponse createOfferResponse = Preferences.getCreateOffer(this);
            if (createOfferResponse != null) {
                chatMessagesActivityVM.senOfferMessageAPI("", "", createOfferResponse);
            }
        }
    }

    @Override
    public void onlineUser(ChatList.Datum args) {
        if (chatMessagesActivityVM != null) {
            chatMessagesActivityVM.manageUserStatus(args, 1);
        }
    }

    @Override
    public void offlineUsr(ChatList.Datum args) {
        if (chatMessagesActivityVM != null) {
            chatMessagesActivityVM.manageUserStatus(args, 0);
        }
    }
}

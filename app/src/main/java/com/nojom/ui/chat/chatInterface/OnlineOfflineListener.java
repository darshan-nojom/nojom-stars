package com.nojom.ui.chat.chatInterface;


import com.nojom.model.ChatList;

public interface OnlineOfflineListener {

    void onlineUser(ChatList.Datum args);

    void offlineUsr(ChatList.Datum args);

}

package com.nojom.ui.chat.chatInterface;

public interface ChatInterface {
    void onConnect(boolean isConnect);

    void disconnect(boolean isConnect);

    void onError(Object args);
}

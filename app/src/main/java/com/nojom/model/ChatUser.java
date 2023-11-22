package com.nojom.model;

import java.io.Serializable;

public class ChatUser implements Serializable {

    public String name;
    public String profile_pic;
    public long last_active;
    public String id;
    public boolean online;

    public ChatUser() {
    }
}

package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatSeenModel implements Serializable {
    @SerializedName("partitionKey")
    @Expose
    public String partitionKey;
    @SerializedName("receiverId")
    @Expose
    public String receiverId;
    @SerializedName("senderId")
    @Expose
    public String senderId;
    @SerializedName("messageIds")
    @Expose
    public Long[] messageIds;

}
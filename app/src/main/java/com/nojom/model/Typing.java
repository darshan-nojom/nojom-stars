package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Typing implements Serializable {

    @Expose
    @SerializedName("type")
    public boolean type;

    @Expose
    @SerializedName("senderId")
    public Integer senderId;

    @Expose
    @SerializedName("receiverId")
    public Integer receiverId;

}

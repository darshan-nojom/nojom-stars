package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatSeenSocket implements Serializable {

    @Expose
    @SerializedName("data")
    public ChatSeenModel data;

}

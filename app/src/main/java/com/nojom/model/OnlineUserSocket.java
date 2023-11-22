package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OnlineUserSocket implements Serializable {

    @Expose
    @SerializedName("data")
    public ChatList.Datum data;

}

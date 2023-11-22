package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WebSocketResponse implements Serializable {

    @Expose
    @SerializedName("action")
    public String action;
}

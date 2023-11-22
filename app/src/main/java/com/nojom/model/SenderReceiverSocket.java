package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SenderReceiverSocket implements Serializable {

    @Expose
    @SerializedName("data")
    public SenderReceiverData data;

}

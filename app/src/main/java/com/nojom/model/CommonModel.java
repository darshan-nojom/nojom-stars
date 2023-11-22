package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommonModel implements Serializable {

    @Expose
    @SerializedName("status")
    public boolean status;
    @Expose
    @SerializedName("msg")
    public String msg;
    @Expose
    @SerializedName("message")
    public String message;

    @Expose
    @SerializedName("messageAr")
    public String messageAr;

    public String getMessage(String lang) {
        if (lang.equals("ar")) {
            return messageAr != null ? messageAr : message;
        }
        return message;
    }

    @Expose
    @SerializedName("error")
    public Object error;
}

package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class APIResponse extends CommonModel {
    @Expose
    @SerializedName("data")
    public String data;
    @Expose
    @SerializedName("jwt")
    public String jwt;
}

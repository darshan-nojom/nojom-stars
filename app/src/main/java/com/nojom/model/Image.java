package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Image implements Serializable {

    @SerializedName("path")
    @Expose
    public String path;
    @SerializedName("file_name")
    @Expose
    public String fileName;
}

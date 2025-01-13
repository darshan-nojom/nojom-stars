package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Stars implements Serializable {
    @SerializedName(value = "id", alternate = {"star_id"})
    @Expose
    public Integer id;
    @SerializedName("attachments")
    @Expose
    public List<String> attachments;
}

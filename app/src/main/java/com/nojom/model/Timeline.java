package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Timeline implements Serializable {

    @SerializedName("curr_status")
    @Expose
    public String curr_status;
    @SerializedName("occurred_at")
    @Expose
    public String occurred_at;
    @SerializedName("prev_status")
    @Expose
    public String prev_status;

}

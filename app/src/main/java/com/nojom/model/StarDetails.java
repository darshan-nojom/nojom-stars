package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StarDetails implements Serializable {

    @SerializedName("star_id")
    @Expose
    public Integer star_id;
    @SerializedName("req_status")
    @Expose
    public String req_status;
    @SerializedName("attachments")
    @Expose
    public List<String> attachments;
    @SerializedName("client_note")
    @Expose
    public String client_note;
    @SerializedName("is_released")
    @Expose
    public boolean is_released;
    @SerializedName("total_service_price")
    @Expose
    public double total_service_price;

}

package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MawData extends CommonModel {
    @Expose
    @SerializedName("data")
    public Maw data;
    @Expose
    @SerializedName("jwt")
    public String jwt;
}
class Maw {
    @Expose
    @SerializedName("mawthooq_number")
    public String mawthooq_number;
    @Expose
    @SerializedName("status")
    public String status;

}
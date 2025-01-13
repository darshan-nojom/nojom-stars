package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AgentCategory implements Serializable {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("name_ar")
    @Expose
    public String nameAr;

    public String getCategory(String lang) {
        if (lang.equals("ar")) {
            return nameAr == null ? name : nameAr;
        } else {
            return name;
        }
    }
}

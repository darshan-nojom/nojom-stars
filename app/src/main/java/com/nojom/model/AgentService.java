package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AgentService implements Serializable {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("price")
    @Expose
    public Integer price;
    @SerializedName("name_ar")
    @Expose
    public String nameAr;
    @SerializedName("filename")
    @Expose
    public String filename;
    @SerializedName("followers")
    @Expose
    public String followers;
    @SerializedName("service_id")
    @Expose
    public Integer serviceId;
    @SerializedName("social_platform_id")
    @Expose
    public Integer socialPlatformId;

    public String getName(String lang) {
        if (lang.equals("ar")) {
            return nameAr != null ? nameAr : name;
        }
        return name;
    }

    public boolean isChecked;
}

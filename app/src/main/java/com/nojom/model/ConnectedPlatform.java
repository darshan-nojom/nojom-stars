package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConnectedPlatform implements Serializable {
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("followers")
    public int followers;
    @Expose
    @SerializedName("nameAr")
    public String nameAr;

    public String getName(String lang) {
        if (lang.equals("ar")) {
            return nameAr != null ? nameAr : name;
        }
        return name;
    }

    @Expose
    @SerializedName("web_url")
    public String web_url;
    @Expose
    @SerializedName("username")
    public String username;
    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("display_order")
    public int display_order;
    @Expose
    @SerializedName("filename")
    public String filename;
    @Expose
    @SerializedName("social_platform_type_id")
    public int social_platform_type_id;
    @Expose
    @SerializedName("social_platform_id")
    public int social_platform_id;

    public boolean isSelected;

}
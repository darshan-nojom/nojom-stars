package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CampSocialPlatform implements Serializable {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("name_ar")
    @Expose
    public String nameAr;
    @SerializedName("web_url")
    @Expose
    public String webUrl;
    @SerializedName("filename")
    @Expose
    public String filename;
    @SerializedName("filename_gray")
    @Expose
    public String filenameGray;
    @SerializedName("followers")
    @Expose
    public String followers;
    @SerializedName("price")
    @Expose
    public Double price = 0.0;

    public String getName(String lang) {
        if (lang.equals("ar")) {
            return nameAr != null ? nameAr : name;
        }
        return name;
    }

}

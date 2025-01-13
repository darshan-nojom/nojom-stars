package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Serv implements Serializable {

    public Serv(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("social_platform_type_id")
    public int social_platform_type_id;
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("filename")
    public String filename;
    @Expose
    @SerializedName("filename_gray")
    public String filename_gray;
    @Expose
    @SerializedName("web_url")
    public String web_url;
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("name_ar")
    public String name_ar;
    @Expose
    @SerializedName("price")
    public double price;
    @Expose
    @SerializedName("service_id")
    public int service_id;
    @Expose
    @SerializedName("followers")
    public int followers;

    public String getName(String lang) {
        if (lang.equals("ar")) {
            return name_ar != null ? name_ar : name;
        }
        return name;
    }
}

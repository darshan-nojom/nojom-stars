package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ConnectedSocialMedia extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;
    @Expose
    @SerializedName("path")
    public String path;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("nameAr")
        public String nameAr;

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
        @SerializedName("followers")
        public int followers;
        @Expose
        @SerializedName("public_status")
        public int public_status;
        @Expose
        @SerializedName("display_order")
        public int display_order;
        @Expose
        @SerializedName("is_public")
        public int is_public;
        @Expose
        @SerializedName("filename")
        public String filename;
        @Expose
        @SerializedName("filename_gray")
        public String filename_gray;
        @Expose
        @SerializedName("social_platform_type_id")
        public int social_platform_type_id;
        @Expose
        @SerializedName("social_platform_id")
        public int social_platform_id;

        public boolean isSelected;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return nameAr != null ? nameAr : name;
            }
            return name;
        }
    }


    public static ConnectedSocialMedia getSocialPlatforms(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody, ConnectedSocialMedia.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

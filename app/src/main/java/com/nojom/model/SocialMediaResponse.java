package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SocialMediaResponse extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("nameAr")
        public String nameAr;
        @Expose
        @SerializedName("social_platforms")
        public ArrayList<SocialPlatform> social_platforms;
        @Expose
        @SerializedName("id")
        public int id;

        public boolean isSelected;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return nameAr != null ? nameAr : name;
            }
            return name;
        }
    }

    public static class SocialPlatform implements Serializable {
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
        @SerializedName("filename")
        public String filename;
        @Expose
        @SerializedName("social_platform_type_id")
        public int social_platform_type_id;
        @Expose
        @SerializedName("id")
        public int id;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return nameAr != null ? nameAr : name;
            }
            return name;
        }
    }

    public static SocialMediaResponse getSocialPlatforms(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    SocialMediaResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

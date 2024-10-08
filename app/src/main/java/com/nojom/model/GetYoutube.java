package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetYoutube extends GeneralModel {

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
        @SerializedName("name_ar")
        public String nameAr;

        @Expose
        @SerializedName("link")
        public String link;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("profile_id")
        public int profile_id;
        @Expose
        @SerializedName("public_status")
        public int public_status;
        @Expose
        @SerializedName("display_order")
        public int display_order;
        @Expose
        @SerializedName("status")
        public int status;

        public boolean isSelected;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return nameAr != null ? nameAr : name;
            }
            return name;
        }
    }


    public static GetYoutube getYoutubeList(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GetYoutube.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

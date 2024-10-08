package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetStores extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;
    @Expose
    @SerializedName("path")
    public String path;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("title")
        public String title;
        @Expose
        @SerializedName("link")
        public String link;

        @Expose
        @SerializedName("filename")
        public String filename;
        @Expose
        @SerializedName("profile_id")
        public int profile_id;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("display_order")
        public int display_order;
        @Expose
        @SerializedName("public_status")
        public int public_status;


        public boolean isSelected;
    }


    public static GetStores getStores(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GetStores.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

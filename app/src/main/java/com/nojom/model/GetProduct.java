package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetProduct extends GeneralModel {

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
        @SerializedName("url")
        public String url;

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
        @SerializedName("currency")
        public int currency;
        @Expose
        @SerializedName("price")
        public double price;
        @Expose
        @SerializedName("display_order")
        public int display_order;
        @Expose
        @SerializedName("public_status")
        public int public_status;


        public boolean isSelected;
    }


    public static GetProduct getProducts(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GetProduct.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

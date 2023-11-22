package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GigCategoryModel extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("name_app")
        public String nameApp;
        @Expose
        @SerializedName("id")
        public int id;

        public boolean isSelected;
    }

    public static class Deadline implements Serializable {
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("price")
        public double price;
        @Expose
        @SerializedName("type")
        public int type;
        @Expose
        @SerializedName("value")
        public int value;

    }

    public static GigCategoryModel getGigCategories(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GigCategoryModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

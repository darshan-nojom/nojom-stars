package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GigSubCategoryModel extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("arabic_name")
        public String arabic_name;
        @Expose
        @SerializedName("parent_service_category_id")
        public int parent_service_category_id;
        @Expose
        @SerializedName("id")
        public int id;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return arabic_name == null ? name : arabic_name;
            }
            return name;
        }

        public boolean isSelected;
    }

    public static GigSubCategoryModel getGigSubCategories(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GigSubCategoryModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

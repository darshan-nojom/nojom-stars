package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class CategoryResponse extends GeneralModel {

    @Expose
    @SerializedName("data")
    public List<CategoryData> data;

    public static class CategoryData {
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("arabic_name")
        public String arabic_name;
        @Expose
        @SerializedName("name_app")
        public String name_app;

        @Expose
        @SerializedName("services")
        public List<CategoryService> services;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return arabic_name != null ? arabic_name : name;
            }
            return name;
        }
        public boolean isChecked;

    }

    public static class CategoryService {
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("name_ar")
        public String name_ar;
        @Expose
        @SerializedName("name_app")
        public String name_app;
        public boolean isChecked;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return name_ar != null ? name_ar : name;
            }
            return name;
        }
    }

    public static List<CategoryResponse.CategoryData> getCategories(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<CategoryResponse.CategoryData>>() {
        }.getType());
    }
}

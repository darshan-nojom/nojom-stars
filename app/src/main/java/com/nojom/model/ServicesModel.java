package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ServicesModel extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Data {
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("name_app")
        public String name;
        @Expose
        @SerializedName("name")
        public String nameApp;
        @Expose
        @SerializedName("id")
        public int id;

        public String experience;

        public int experienceId;

        public boolean isSelected;
    }

    public static List<Data> getServiceData(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<Data>>() {
        }.getType());
    }

    public static ServicesModel getGigCategories(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    ServicesModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

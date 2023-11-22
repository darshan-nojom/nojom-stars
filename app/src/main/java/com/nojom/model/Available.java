package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Available extends GeneralModel {

    @Expose
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("detail")
        public String detail;

        public boolean isChecked;
        public boolean isLoading;
    }

    public static List<Available.Data> getAvailableList(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<Available.Data>>() {
        }.getType());
    }
}

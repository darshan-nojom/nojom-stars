package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class VerifyID extends GeneralModel {

    @Expose
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @Expose
        @SerializedName("status")
        public String status;
        @Expose
        @SerializedName("data")
        public String data;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static List<VerifyID.Data> getProfileVerifications(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<VerifyID.Data>>() {
        }.getType());
    }
}

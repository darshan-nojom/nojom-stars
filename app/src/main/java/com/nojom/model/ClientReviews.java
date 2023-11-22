package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ClientReviews extends GeneralModel {

    @Expose
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @Expose
        @SerializedName("comment")
        public String comment;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("rate")
        public double rate;
        @Expose
        @SerializedName("title")
        public String title;
    }

    public static List<ClientReviews.Data> getClientReview(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<ClientReviews.Data>>() {
        }.getType());
    }
}

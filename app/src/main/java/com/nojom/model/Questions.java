package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Questions extends GeneralModel {

    @Expose
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @Expose
        @SerializedName("type")
        public int type;
        @Expose
        @SerializedName("question")
        public String question;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static List<Questions.Data> getQuestions(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<Questions.Data>>() {
        }.getType());
    }

    public static Questions getGigQuestions(String responseBody) {
        return new Gson().fromJson(responseBody, Questions.class);
    }
}

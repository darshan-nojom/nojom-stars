package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Topic extends GeneralModel {

    @Expose
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("topic_name")
        public String topicName;
        @Expose
        @SerializedName("name")
        public String name;
    }

    public static List<Topic.Data> getNotificationTopic(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<Topic.Data>>() {
        }.getType());
    }
}

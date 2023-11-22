package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Notification extends GeneralModel {

    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("status")
    public String status;

    public static List<Notification> getNotifications(String jsonData) {
        try {
            return new Gson().fromJson(jsonData, new TypeToken<List<Notification>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

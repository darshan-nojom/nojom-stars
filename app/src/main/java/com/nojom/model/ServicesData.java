package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServicesData extends CommonModel {
    @Expose
    @SerializedName("data")
    public Services data;
    @Expose
    @SerializedName("jwt")
    public String jwt;

    public static Services getServiceData(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    Services.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


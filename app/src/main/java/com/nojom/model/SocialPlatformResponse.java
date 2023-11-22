package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SocialPlatformResponse extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("platform_icon")
        public String platformIcon;
        @Expose
        @SerializedName("id")
        public int id;

        public String username;
        public int followers = -1;
        public String platformFollower;

        public boolean isSelected;

    }

    public static SocialPlatformResponse getSocialPlatforms(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    SocialPlatformResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

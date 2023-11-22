package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SocialPlatformList extends CommonModel {

    @Expose
    @SerializedName("data")
    public List<Data> data;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("gigTitle")
        public String gigTitle;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("minPrice")
        public Double minPrice;
        @Expose
        @SerializedName("deadlineValue")
        public int deadlineValue;
        @Expose
        @SerializedName("deadlineType")
        public String deadlineType;
        @Expose
        @SerializedName("gigType")
        public String gigType;
        @Expose
        @SerializedName("platformName")
        public String platformName;
        @Expose
        @SerializedName("platform_icon")
        public String platform_icon;

        @Expose
        @SerializedName("followersCount")
        public Integer followersCount;

        @Expose
        @SerializedName("sum_rating")
        public Double sum_rating;
        @Expose
        @SerializedName("gig_count_rating")
        public Integer gig_count_rating;
        @Expose
        @SerializedName("starpoints")
        public Float starpoints;
        @Expose
        @SerializedName("real_count")
        public Integer real_count;
        @Expose
        @SerializedName("username")
        public String username;
        @Expose
        @SerializedName("redirectUrl")
        public String redirectUrl;

        public boolean isShowProgress;
    }

    public static SocialPlatformList getSocialPlatforms(String responseBody) {
        return new Gson().fromJson(responseBody, SocialPlatformList.class);
    }

//    public static ArrayList<SocialPlatformList.Data> getSocialPlatforms(String jsonData) {
//        return new Gson().fromJson(jsonData, new TypeToken<ArrayList<SocialPlatformList.Data>>() {
//        }.getType());
//    }
}

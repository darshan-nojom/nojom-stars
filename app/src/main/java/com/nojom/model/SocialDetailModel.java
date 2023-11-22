package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SocialDetailModel extends GeneralModel {
    @Expose
    @SerializedName("screenshots")
    public List<Data> data;
    @Expose
    @SerializedName("path")
    public String path;

    public static class Data {
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("survey_status")
        public int surveyStatus;
        @Expose
        @SerializedName("screenshot")
        public String screenshot;
        @Expose
        @SerializedName("social_media")
        public int socialMedia;
        @Expose
        @SerializedName("id")
        public int id;
        public String localFilePath;
    }

    public static SocialDetailModel getSurveyDetails(String responseBody) {
        return new Gson().fromJson(responseBody, SocialDetailModel.class);
    }
}
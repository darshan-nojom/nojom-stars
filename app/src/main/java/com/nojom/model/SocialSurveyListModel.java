package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SocialSurveyListModel extends GeneralModel {

    @Expose
    @SerializedName("data")
    public List<Data> data;

    public static class Data {
        @Expose
        @SerializedName("survey_status")
        public int surveyStatus;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("note")
        public String note;
    }

    public static SocialSurveyListModel getSocialSurveys(String responseBody) {
        return new Gson().fromJson(responseBody, SocialSurveyListModel.class);
    }

}

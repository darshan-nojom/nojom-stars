package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;

public class PartnerWithUsResponse extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("question")
        public String question;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("placeholder")
        public String placeholder;
        @Expose
        @SerializedName("type")
        public int type;
        @Expose
        @SerializedName("page")
        public int page;
        @Expose
        @SerializedName("answers_option")
        public ArrayList<Answers> answers_option;
        @Expose
        @SerializedName("answer")
        public String answer;
        @Expose
        @SerializedName("partnershipAnswerID")
        public int partnershipAnswerID;

        public int partnershipAnsId = 0;
        public String aboutAnswer;
        public String selectedAnswer;

    }

    public static class Answers implements Serializable {
        @Expose
        @SerializedName("answer")
        public String answer;
        @Expose
        @SerializedName("id")
        public int id;

        public boolean isSelected;
    }

    public static ArrayList<PartnerWithUsResponse.Data> getPartnerQuestionList(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<ArrayList<PartnerWithUsResponse.Data>>() {
        }.getType());
    }
}

package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UserSkillsModel extends GeneralModel implements Serializable {

    @Expose
    @SerializedName("skill_lists")
    public ArrayList<SkillLists> skillLists;
    @Expose
    @SerializedName("skill_count")
    public int skillCount;

    public static class SkillLists implements Serializable {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("name_ar")
        public String name_ar;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("ps_id")
        public Integer psId;
        @Expose
        @SerializedName("rating")
        public Integer rating;

        public boolean isSelected;
        public int selectedRating;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return name_ar != null ? name_ar : name;
            }
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            return this.id == ((SkillLists) obj).id;
        }
    }

    //string to model conversation
    public static UserSkillsModel getSkills(String jsonData) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonData,
                    UserSkillsModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

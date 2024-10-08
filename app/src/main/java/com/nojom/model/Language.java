package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Language extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName(value = "nameAr", alternate = {"name_ar"})
        public String nameAr;
        @Expose
        @SerializedName("id")
        public int id;

        public String level;

        public int levelId;

        public boolean isSelected;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return nameAr != null ? nameAr : name;
            }
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            return this.id == ((Language.Data) obj).id;
        }
    }

    public static List<Language.Data> getLanguageList(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<Language.Data>>() {
        }.getType());
    }

    public static Language getLanguages(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    Language.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

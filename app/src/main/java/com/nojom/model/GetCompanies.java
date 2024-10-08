package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetCompanies extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;
    @Expose
    @SerializedName("path")
    public String path;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("name_ar")
        public String nameAr;

        @Expose
        @SerializedName("filename")
        public String filename;
        @Expose
        @SerializedName("link")
        public String link;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("status")
        public int status;

        public Data(String name, String nameAr, String filename, String link, int id, int status) {
            this.name = name;
            this.nameAr = nameAr;
            this.filename = filename;
            this.link = link;
            this.id = id;
            this.status = status;
        }

        public boolean isSelected;

        public Data() {

        }

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return nameAr != null ? nameAr : name;
            }
            return name;
        }
    }


    public static GetCompanies getCompanies(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GetCompanies.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

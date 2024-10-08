package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetAgentCompanies extends GeneralModel {

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
        @SerializedName("profile_id")
        public int profile_id;
        @Expose
        @SerializedName("public_status")
        public int public_status;
        @Expose
        @SerializedName("company_id")
        public int company_id;
        @Expose
        @SerializedName("times")
        public int times;
        @Expose
        @SerializedName("title")
        public String title;
        @Expose
        @SerializedName("code")
        public String code;
        @Expose
        @SerializedName("display_order")
        public int display_order;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("times_public_status")
        public int times_public_status;
        @Expose
        @SerializedName("campaign_date")
        public String campaign_date;
        @Expose
        @SerializedName("campaign_date_public_status")
        public Integer campaign_date_public_status;
        @Expose
        @SerializedName("contract_start_date")
        public String contract_start_date;
        @Expose
        @SerializedName("contract_end_date")
        public String contract_end_date;
        @Expose
        @SerializedName("contract_public_status")
        public Integer contract_public_status;

        public boolean isSelected;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return nameAr != null ? nameAr : name;
            }
            return name;
        }
    }


    public static GetAgentCompanies getAgentCompanies(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GetAgentCompanies.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

public class Portfolios extends GeneralModel implements Serializable {
    public List<Portfolios> data;


    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("profile_id")
    public int profileId;
    @Expose
    @SerializedName("title")
    public String title;
    @Expose
    @SerializedName("timestamp")
    public String timestamp;
    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("portfolio_files")
    public List<PortfolioFiles> portfolioFiles;

    @SerializedName("filename")
    public String filename;
    @SerializedName("company_id")
    public int company_id;
    @SerializedName("display_order")
    public int display_order;
    @SerializedName("public_status")
    public int public_status;

    @SerializedName("company_name")
    public String company_name;
    @SerializedName("company_name_ar")
    public String company_name_ar;
    @SerializedName("company_filename")
    public String company_filename;

    public String getName(String lang) {
        if (lang.equals("ar")) {
            return company_name_ar != null ? company_name_ar : company_name;
        }
        return company_name;
    }

    public static class PortfolioFiles implements Serializable {
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("path")
        public String path;
        @Expose
        @SerializedName("type")
        public int type;
        @Expose
        @SerializedName("sort")
        public int sort;
        @Expose
        @SerializedName("status")
        public int status;

        public PortfolioFiles(int id, String path, int type, int sort, int status) {
            this.id = id;
            this.path = path;
            this.type = type;
            this.sort = sort;
            this.status = status;
        }
    }

    public static List<Portfolios> getPortfolios(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<Portfolios>>() {
        }.getType());
    }

    //string to model conversation
    public static Portfolios getPortfolio(String jsonData) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonData,
                    Portfolios.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

public class Portfolios extends GeneralModel implements Serializable {


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

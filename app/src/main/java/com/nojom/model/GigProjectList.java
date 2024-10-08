package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GigProjectList extends CommonModel {

    @Expose
    @SerializedName("data")
    public List<GigProjectList.Data> data;

    public static class Data {
        @Expose
        @SerializedName("contractID")
        public int contractID;
        @Expose
        @SerializedName("quantity")
        public int quantity;
        @Expose
        @SerializedName("gigStateID")
        public int gigStateID;
        @Expose
        @SerializedName("gigStateName")
        public String gigStateName;
        @Expose
        @SerializedName("gigStateNameAr")
        public String gigStateNameAr;
        @Expose
        @SerializedName("totalPrice")
        public Double totalPrice;
        @Expose
        @SerializedName("createdAt")
        public String createdAt;
        @Expose
        @SerializedName("job_refunds")
        public int jobRefunds;

        public boolean isSelected;
        public boolean isShowProgress;

        public String getStateName(String lang) {
            if (lang.equals("ar")) {
                return gigStateNameAr != null ? gigStateNameAr : gigStateName;
            }
            return gigStateName;
        }
    }

    public static class GigImage {
        @Expose
        @SerializedName("imageName")
        public String imageName;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static GigProjectList getGigProjectList(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GigProjectList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

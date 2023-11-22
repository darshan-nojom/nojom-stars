package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GigCatCharges extends CommonModel {

    @Expose
    @SerializedName("data")
    public ArrayList<GigCatCharges.Data> data;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("percent_charge")
        public double percentCharge;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("amount_charge")
        public double amountCharge;
        @Expose
        @SerializedName("service_category_id")
        public int serviceCategoryId;
        @Expose
        @SerializedName("status")
        public String status;
    }


    public static GigCatCharges getGigCatCharges(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GigCatCharges.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

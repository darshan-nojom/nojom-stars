package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GigPackages extends CommonModel {

    @Expose
    @SerializedName("data")
    public ArrayList<GigPackages.Data> data;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("packageName")
        public String packageName;
        @Expose
        @SerializedName("id")
        public int id;

        public boolean isSelected;

        public String packageDescription;
        public String name;
        public int revisions;
        public int deliveryTimeID;
        public String deliveryTime;
        public double price;
        public ArrayList<GigRequirementsModel.Data> requirements;

        public GigDeliveryTimeModel.Data delivery;
    }



    public static GigPackages getGigPackages(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GigPackages.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

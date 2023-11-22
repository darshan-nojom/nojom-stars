package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GigDeliveryTimeModel extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("deliveryTitle")
        public String deliveryTitle;//gj19ba0856
        @Expose
        @SerializedName("id")
        public int id;

        public boolean isSelected;
    }

    public static GigDeliveryTimeModel getDeliveryTime(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GigDeliveryTimeModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

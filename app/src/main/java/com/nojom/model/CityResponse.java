package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

public class CityResponse extends GeneralModel implements Serializable {

    @Expose
    @SerializedName("data")
    public List<CityData> data;

    public static class CityData implements Serializable {
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("cityName")
        public String cityName;
        @Expose
        @SerializedName("stateID")
        public int stateID;

        public boolean isSelected;
    }

    public static List<CityResponse.CityData> getCityData(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<CityData>>() {
        }.getType());
    }
}

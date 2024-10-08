package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

public class CountryResponse extends GeneralModel implements Serializable {

    @Expose
    @SerializedName("data")
    public List<CountryData> data;

    public static class CountryData implements Serializable {
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("countryName")
        public String countryName;
        @Expose
        @SerializedName("countryNameAr")
        public String countryNameAr;
        @Expose
        @SerializedName("countryCode")
        public String countryCode;
        @Expose
        @SerializedName("phoneCode")
        public String phoneCode;

        public boolean isSelected;

        public String getCountryName(String lang) {
            if (lang.equals("ar")) {
                return countryNameAr != null ? countryNameAr : countryName;
            }
            return countryName;
        }
    }

    public static List<CountryResponse.CountryData> getCountryData(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<CountryData>>() {
        }.getType());
    }
}

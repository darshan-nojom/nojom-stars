package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

public class StateResponse extends GeneralModel implements Serializable {

    @Expose
    @SerializedName("data")
    public List<StateData> data;

    public static class StateData implements Serializable {
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("stateName")
        public String stateName;
        @Expose
        @SerializedName("stateNameAr")
        public String stateNameAr;
        @Expose
        @SerializedName("stateCode")
        public String stateCode;

        public String getStateName(String lang) {
            if (lang.equals("ar")) {
                return stateNameAr != null ? stateNameAr : stateName;
            }
            return stateName;
        }

        public boolean isSelected;
    }

    public static List<StateResponse.StateData> getStateData(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<StateData>>() {
        }.getType());
    }
}

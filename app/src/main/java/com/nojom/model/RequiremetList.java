package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RequiremetList extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Data implements Serializable, Comparable, Cloneable {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public Integer id;
        @Expose
        @SerializedName("isOther")
        public int isOther;
        @Expose
        @SerializedName("serviceCategoryID")
        public int serviceCategoryID;
        @Expose
        @SerializedName("inputType")
        public int inputType;

        public ArrayList<CustomData> customData;

        public Integer getId() {
            return id;
        }

        public boolean isSelected;
        public String dataValue;
        public String dataReq;
        public String featureTitle;
        public int gigReqType;
        public int gigOtherInputType;
        public boolean isOtherReq;
        public String reqDescription;

        public double selectedPrice = 0;
        public double selectedQty = 0;

        @Override
        public boolean equals(Object obj) {
            return this.id == ((RequiremetList.Data) obj).id;
        }

        @Override
        public int compareTo(Object o) {
            Data compare = (Data) o;

            if (compare.id == this.id && compare.name.equals(this.name) && compare.isOther == (this.isOther)
                    && compare.serviceCategoryID == (this.serviceCategoryID)
                    && compare.inputType == (this.inputType) && compare.isSelected == (this.isSelected)) {
                return 0;
            }
            return 1;
        }

        @Override
        public Data clone() {

            Data clone;
            try {
                clone = (Data) super.clone();

            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e); //should not happen
            }

            return clone;
        }
    }

    public static List<RequiremetList.Data> getRequirementList(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<RequiremetList.Data>>() {
        }.getType());
    }

    public static RequiremetList getRequirement(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    RequiremetList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class CustomData implements Serializable {
        public String dataValue = "";
        public String dataReq = "";
    }
}

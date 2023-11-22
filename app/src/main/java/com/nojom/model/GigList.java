package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GigList extends CommonModel {

    @Expose
    @SerializedName("gigData")
    public List<GigList.Data> data;
    @Expose
    @SerializedName("gigImagesPath")
    public String gigImagesPath;

    public static class Data {
        @Expose
        @SerializedName("gigTitle")
        public String gigTitle;
        @Expose
        @SerializedName("isNew")
        public String isNew;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("gigImages")
        public List<GigImage> gigImages;
        @Expose
        @SerializedName("totalCompleted")
        public int totalCompleted;
        @Expose
        @SerializedName("totalInProgress")
        public int totalInProgress;
        @Expose
        @SerializedName("totalPending")
        public int totalPending;
        @Expose
        @SerializedName("gigType")
        public String gigType; //1=Custom Gig & 2=Standard Gig

        public boolean isSelected;
        public boolean isShowProgress;
    }

    public static class GigImage {
        @Expose
        @SerializedName("imageName")
        public String imageName;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static GigList getGigList(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GigList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

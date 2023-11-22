package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GigDetails extends CommonModel implements Serializable {

    @Expose
    @SerializedName("gigID")
    public int gigID;
    @Expose
    @SerializedName("gigTitle")
    public String gigTitle;
    @Expose
    @SerializedName("gigDescription")
    public String gigDescription;
    @Expose
    @SerializedName("parentCategoryNamme")
    public String parentCategoryNamme;
    @Expose
    @SerializedName("parentCategoryID")
    public int parentCategoryID;
    @Expose
    @SerializedName("subCategoryNamme")
    public String subCategoryNamme;
    @Expose
    @SerializedName("subCategoryID")
    public int subCategoryID;
    @Expose
    @SerializedName("deadlineValue")
    public int deadlineValue;
    @Expose
    @SerializedName("deadlineType")
    public String deadlineType;
    @Expose
    @SerializedName("deadlineDescription")
    public String deadlineDescription;
    @Expose
    @SerializedName("agentProfileID")
    public int agentProfileID;
    @Expose
    @SerializedName("agentUserName")
    public String agentUserName;
    @Expose
    @SerializedName("agentProfileImg")
    public String agentProfileImg;
    @Expose
    @SerializedName("rate")
    public float rate;
    @Expose
    @SerializedName("count_rating")
    public double count_rating;
    @Expose
    @SerializedName("gigImagesPath")
    public String gigImagesPath;
    @Expose
    @SerializedName("packages")
    public List<Packages> packages;
    @Expose
    @SerializedName("customPackages")
    public List<CustomRequirements> customPackages;
    @Expose
    @SerializedName("gigImages")
    public List<GigImage> gigImages;
    @Expose
    @SerializedName("languages")
    public ArrayList<Language.Data> languages;
    @Expose
    @SerializedName("searchTags")
    public List<TagName> searchTags;
    @Expose
    @SerializedName("deadlines")
    public ArrayList<GigCategoryModel.Deadline> deadlines;
    @Expose
    @SerializedName("gigType")
    public String gigType; //1=Custom Gig & 2=Standard Gig
    @Expose
    @SerializedName("minPrice")
    public Double minPrice;
    @Expose
    @SerializedName("social_platform")
    public ArrayList<SocialPlatform> socialPlatform;
    @Expose
    @SerializedName("firebaseLink")
    public String firebaseLink;
    @Expose
    @SerializedName("dynamicLink")
    public String dynamicLink;

    public boolean isSelected;
    public boolean isShowProgress;

    public static class SocialPlatform implements Serializable {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("platform_icon")
        public String platformIcon;
        @Expose
        @SerializedName("id")
        public int id;

        @Expose
        @SerializedName("username")
        public String username;
        @Expose
        @SerializedName("followersCount")
        public int followers = -1;
        @Expose
        @SerializedName("socialPlatformID")
        public int socialPlatformID;
    }

    public static class Packages implements Serializable {
        @Expose
        @SerializedName("deliveryTitle")
        public String deliveryTitle;
        @Expose
        @SerializedName("price")
        public Double price;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("requirements")
        public ArrayList<GigRequirementsModel.Data> requirements;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("revisions")
        public int revisions;
        @Expose
        @SerializedName("deliveryTimeID")
        public int deliveryTimeID;
        @Expose
        @SerializedName("packageID")
        public int packageID;
        @Expose
        @SerializedName("packageName")
        public String packageName;
    }


    public static class GigImage implements Serializable {
        @Expose
        @SerializedName("imageName")
        public String imageName;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class TagName implements Serializable {
        @Expose
        @SerializedName("tagName")
        public String tagName;
    }

    public static class CustomRequirements implements Serializable {
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("reqOrOtherReqID")
        public Integer reqOrOtherReqID;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("inputType")
        public int inputType;
        @Expose
        @SerializedName("gigRequirementType")
        public int gigRequirementType;
        @Expose
        @SerializedName("isOther")
        public boolean isOther;
        @Expose
        @SerializedName("requirmentDetails")
        public List<ReqDetail> requirmentDetails;
    }

    public static class ReqDetail implements Serializable {
        @Expose
        @SerializedName("price")
        public Double price;
        @Expose
        @SerializedName("from_quantity")
        public Double fromQuantity;
        @Expose
        @SerializedName("toQuantity")
        public Double toQuantity;
        @Expose
        @SerializedName("featureName")
        public String featureName;
    }

    public static GigDetails getGigDetails(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    GigDetails.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

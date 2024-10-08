package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContractDetails extends CommonModel implements Serializable {

    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("gigID")
    public int gigID;
    @Expose
    @SerializedName("gigPackageID")
    public int gigPackageID;
    @Expose
    @SerializedName("clientProfileID")
    public int clientProfileID;
    @Expose
    @SerializedName("gigStateID")
    public int gigStateID;
    @Expose
    @SerializedName("packagePrice")
    public double packagePrice;
    @Expose
    @SerializedName("quantity")
    public int quantity;
    @Expose
    @SerializedName("bidCharges")
    public double bidCharges;
    @Expose
    @SerializedName("totalPrice")
    public double totalPrice;
    @Expose
    @SerializedName("gigPackageName")
    public String gigPackageName;
    @Expose
    @SerializedName("gigPackageDescription")
    public String gigPackageDescription;
    @Expose
    @SerializedName("revisions")
    public int revisions;
    @Expose
    @SerializedName("duration")
    public int duration;
    @Expose
    @SerializedName("packageName")
    public String packageName;
    @Expose
    @SerializedName("requirements")
    public ArrayList<GigRequirementsModel.Data> requirements;
    @Expose
//    @SerializedName("clientDetails")
    @SerializedName(value = "clientDetails", alternate = "clientProfileData")
    public ClientDetail clientDetails;
    @Expose
    @SerializedName("timer")
    public ProjectByID.Timer timer;
    @Expose
    @SerializedName("gigTitle")
    public String gigTitle;
    @Expose
    @SerializedName("gigDescription")
    public String gigDescription;
    @Expose
    @SerializedName("clientAttachmentsPath")
    public String clientAttachmentsPath;
    @SerializedName("clientAttachments")
    @Expose
    public List<ProjectByID.Attachments> attachments = null;
    @Expose
    @SerializedName("clientJobDescribe")
    public String clientJobDescribe;
    @SerializedName("submittedFiles")
    @Expose
    public List<ProjectByID.Files> submittedFiles = null;
    @Expose
    @SerializedName("isClientReview")
    public int isClientReview;
    @Expose
    @SerializedName("agentReview")
    public AgentReview agentReview;
    @Expose
    @SerializedName("clientReview")
    public ClientReview clientReview;
    @SerializedName("agentSubmittedPath")
    @Expose
    public String agentSubmittedPath;
    @Expose
    @SerializedName("customPackages")
    public ArrayList<CustomPackages> customPackages;
    @Expose
    @SerializedName("deadlineType")
    public String deadlineType;//1=Hour 2=Days
    @Expose
    @SerializedName("deadlineValue")
    public Double deadlineValue;
    @Expose
    @SerializedName("deadlinePrice")
    public Double deadlinePrice;
    @Expose
    @SerializedName("gigType")
    public String gigType;//1=custom
    @Expose
    @SerializedName("minPrice")
    public Double minPrice;
    @SerializedName("job_post_charges")
    @Expose
    public JobPostCharge jobPostCharges = null;
    @Expose
    @SerializedName("social_platform")
    public ArrayList<GigDetails.SocialPlatform> socialPlatform;
    @Expose
    @SerializedName("refundStatus")
    public String refundStatus;

    public boolean isSelected;
    public boolean isShowProgress;


    public static class ClientDetail implements Serializable {
        @Expose
        @SerializedName("first_name")
        public String first_name;
        @Expose
        @SerializedName("clientID")
        public int clientID;
        @Expose
        @SerializedName("last_name")
        public String last_name;
        @Expose
        @SerializedName("username")
        public String username;
        @Expose
        @SerializedName("photo")
        public String photo;
        @Expose
        @SerializedName("profilePath")
        public String profilePath;
        @Expose
        @SerializedName("address")
        public Address address;
    }

    public static class JobPostCharge implements Serializable {
        @Expose
        @SerializedName("bid_charges")
        public int bidCharges = 10;
        @Expose
        @SerializedName("bid_percent_charges")
        public int bidPercentCharges;
        @Expose
        @SerializedName("deposit_charges")
        public int depositCharges;
        @Expose
        @SerializedName("bid_dollar_charges")
        public int bidDollarCharges;
    }

    public static class Address implements Serializable {
        @Expose
        @SerializedName("country")
        public String country;
        @Expose
        @SerializedName("countryAr")
        public String countryAr;

        public String getCountry(String lang) {
            if (lang.equals("ar")) {
                return countryAr != null ? countryAr : country;
            }
            return country;
        }

        @Expose
        @SerializedName("city")
        public String city;
    }

    public static class ClientReview implements Serializable {
        @Expose
        @SerializedName("contractID")
        public int contractID;
        @Expose
        @SerializedName("rate")
        public Float rate;
        @Expose
        @SerializedName("comment")
        public String comment;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
    }

    public static class AgentReview implements Serializable {
        @Expose
        @SerializedName("contractID")
        public int contractID;
        @Expose
        @SerializedName("rate")
        public Float rate;
        @Expose
        @SerializedName("comment")
        public String comment;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
    }


    public static class CustomPackages implements Serializable {
        @Expose
        @SerializedName("gigs_requirementsID")
        public int gigsRequirementsID;
        @Expose
        @SerializedName("cgprID")
        public int cgprID;
        @Expose
        @SerializedName("custom_packagesID")
        public int customPackagesID;
        @Expose
        @SerializedName("quantity")
        public int quantity;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("reqOrOtherReqID")
        public int reqOrOtherReqID;
        @Expose
        @SerializedName("price")
        public String price;
        @Expose
        @SerializedName("featureName")
        public String featureName;
        @Expose
        @SerializedName("gigRequirementType")
        public String gigRequirementType;
        @Expose
        @SerializedName("inputType")
        public String inputType;
        @Expose
        @SerializedName("isOtherRequirment")
        public Integer isOtherRequirment;
    }


    public static ContractDetails getContractDetails(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    ContractDetails.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

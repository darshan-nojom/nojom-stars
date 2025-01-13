package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProjectByID extends GeneralModel implements Serializable {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("profile_id")
    @Expose
    public Integer profileId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("brief")
    @Expose
    public String brief;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("client_rate_id")
    @Expose
    public Integer clientRateId;
    @SerializedName("job_post_state_id")
    @Expose
    public Integer jobPostStateId;
    @SerializedName("edited_by_admin")
    @Expose
    public String editedByAdmin;
    @SerializedName("offered")
    @Expose
    public String offered;
    @SerializedName("date_completed")
    @Expose
    public Object dateCompleted;
    @SerializedName("pages")
    @Expose
    public Integer pages;
    @SerializedName("sys_id")
    @Expose
    public Integer sysId;
    @SerializedName("legal_help_id")
    @Expose
    public Integer legalHelpId;
    @SerializedName("sort")
    @Expose
    public Integer sort;
    @SerializedName("timestamp")
    @Expose
    public String timestamp;
    @SerializedName("deadline")
    @Expose
    public String deadline;
    @SerializedName("longitude")
    @Expose
    public Double longitude;
    @SerializedName("latitude")
    @Expose
    public Double latitude;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("coupon_id")
    @Expose
    public Object couponId;
    @SerializedName("job_budget")
    @Expose
    public Double jobBudget;
    @SerializedName("job_pay_type_id")
    @Expose
    public Integer jobPayTypeId;
    @SerializedName("job_pay_type_name")
    @Expose
    public String jobPayTypeName;
    @SerializedName("job_pay_type_detail")
    @Expose
    public String jobPayTypeDetail;
    @SerializedName("job_post_states_name")
    @Expose
    public String jobPostStatesName;
    @SerializedName("bids_count")
    @Expose
    public Integer bidsCount;
    @SerializedName("sc_id")
    @Expose
    public Integer scId;
    @SerializedName("sc_name")
    @Expose
    public String scName;
    @SerializedName("sc_name_app")
    @Expose
    public String scNameApp;
    @SerializedName("sc_name_app_fastpaper")
    @Expose
    public String scNameAppFastpaper;
    @SerializedName("services_id")
    @Expose
    public Integer servicesId;
    @SerializedName("services_name")
    @Expose
    public String servicesName;
    @SerializedName("services_name_ar")
    @Expose
    public String servicesNameAr;
    @SerializedName("client_id")
    @Expose
    public Integer clientId;
    @SerializedName("client_username")
    @Expose
    public String clientUsername;
    @SerializedName("client_first_name")
    @Expose
    public String clientFirstName;
    @SerializedName("client_last_name")
    @Expose
    public String clientLastName;
    @SerializedName("client_country")
    @Expose
    public String clientCountry;
    @SerializedName("countryNameAr")
    @Expose
    public String clientCountryAr;

    public String getCountryName(String lang) {
        if (lang.equals("ar")) {
            return clientCountryAr != null ? clientCountryAr : clientCountry;
        }
        return clientCountry;
    }

    public String getServiceName(String lang) {
        if (lang.equals("ar")) {
            return servicesNameAr != null ? servicesNameAr : servicesName;
        }
        return servicesName;
    }

    @SerializedName("client_region")
    @Expose
    public String clientRegion;
    @SerializedName("client_city")
    @Expose
    public String clientCity;
    @SerializedName("client_city_ar")
    @Expose
    public String clientCityAr;
    @SerializedName("client_photos")
    @Expose
    public String clientPhotos;
    @SerializedName("review")
    @Expose
    public Integer review;
    @SerializedName("job_post_bids")
    @Expose
    public JobPostBids jobPostBids;
    @SerializedName("job_post_charges")
    @Expose
    public JobPostCharges jobPostCharges;
    @SerializedName("attachments")
    @Expose
    public List<Attachments> attachments = null;
    @SerializedName("attachment_count")
    @Expose
    public Integer attachmentCount;
    @SerializedName("submitted_files")
    @Expose
    public List<Files> submittedFiles;
    @SerializedName("timer")
    @Expose
    public Timer timer;
    @SerializedName("agent_review")
    @Expose
    public AgentReview agentReview;
    @SerializedName("client_review")
    @Expose
    public ClientReview clientReview;
    @SerializedName("client_rate")
    @Expose
    public ClientRate clientRate;
    @SerializedName("refundStatus")
    @Expose
    public String refundStatus;


    public static class JobPostBids implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("job_post_id")
        @Expose
        public Integer jobPostId;
        @SerializedName("profile_id")
        @Expose
        public Integer profileId;
        @SerializedName("amount")
        @Expose
        public Double amount;
        @SerializedName("bid_charges")
        @Expose
        public Double bidCharges = 10.0;
        @SerializedName("currency")
        @Expose
        public String currency;
        @SerializedName("deadline_type")
        @Expose
        public String deadlineType;
        @SerializedName("deadline_value")
        @Expose
        public Integer deadlineValue;
        @SerializedName("message")
        @Expose
        public String message;
        @SerializedName("job_post_state_id")
        @Expose
        public Integer jobPostStateId;
        @SerializedName("release_status")
        @Expose
        public String releaseStatus;
        @SerializedName("is_awarded")
        @Expose
        public Integer isAwarded;
        @SerializedName("timestamp")
        @Expose
        public String timestamp;
        @SerializedName("date_accepted")
        @Expose
        public String dateAccepted;
        @SerializedName("status")
        @Expose
        public Integer status;
        @SerializedName("jpc_id")
        @Expose
        public Integer jpcId;
        @SerializedName("jpc_pay_type_id")
        @Expose
        public Integer jpcPayTypeId;
        @SerializedName("jpc_currency")
        @Expose
        public String jpcCurrency;
        @SerializedName("jpc_fixed_price")
        @Expose
        public Double jpcFixedPrice;

    }

    public static class JobPostCharges implements Serializable {

        @SerializedName("bid_charges")
        @Expose
        public Double bidCharges = 10.0;
        @SerializedName("bid_percent_charges")
        @Expose
        public Double bidPercentCharges;
        @SerializedName("deposit_charges")
        @Expose
        public Double depositCharges;
        @SerializedName("bid_dollar_charges")
        @Expose
        public Double bidDollarCharges;

    }

    public static class ClientReview implements Serializable {
        @Expose
        @SerializedName("title")
        public String title;
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
        @SerializedName("title")
        public String title;
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

    public static class Files implements Serializable {
        @Expose
        @SerializedName(value = "files", alternate = "files_data")
        public List<FileList> fileList;
        @Expose
        @SerializedName("type")
        public int type;
        @Expose
        @SerializedName("timer")
        public Timer timer;
        @Expose
        @SerializedName(value = "timestamp", alternate = "createdAt")
        public String timestamp;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class FileList implements Serializable {
        @Expose
        @SerializedName("size")
        public String size;
        @Expose
        @SerializedName("filename")
        public String fileName;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName(value = "timestamp", alternate = "createdAt")
        public String createdAt;
    }

//    public static class JobPostContracts implements Serializable {
//        @Expose
//        @SerializedName("deposit_charges")
//        public int depositCharges;
//        @Expose
//        @SerializedName("bid_charges")
//        public int bidCharges;
//        @Expose
//        @SerializedName("bid_percent_charges")
//        public int bidPercentCharges;
//        @Expose
//        @SerializedName("bid_dollar_charges")
//        public int bidDollarCharges;
//        @Expose
//        @SerializedName("fixed_price")
//        public double fixedPrice;
//        @Expose
//        @SerializedName("currency")
//        public String currency;
//        @Expose
//        @SerializedName("pay_type_id")
//        public int payTypeId;
//        @Expose
//        @SerializedName("id")
//        public int id;
//    }

    public static class Timer implements Serializable {
        @SerializedName("days")
        @Expose
        public Integer days;
        @SerializedName("hours")
        @Expose
        public Integer hours;
        @SerializedName("minutes")
        @Expose
        public Integer minutes;
        @SerializedName("seconds")
        @Expose
        public Integer seconds;
        @SerializedName("isDue")
        @Expose
        public Boolean isDue;
    }

//    public static class Profiles implements Serializable {
//        @Expose
//        @SerializedName("id")
//        public int id;
//        @Expose
//        @SerializedName("username")
//        public String username;
//        @Expose
//        @SerializedName("last_name")
//        public String lastName;
//        @Expose
//        @SerializedName("first_name")
//        public String firstName;
//    }
//
//    public static class Photo implements Serializable {
//        @Expose
//        @SerializedName("img")
//        public String img;
//    }

    public static class Address implements Serializable {
        @Expose
        @SerializedName("city")
        public String city;
        @Expose
        @SerializedName("region")
        public String region;
        @Expose
        @SerializedName("country")
        public String country;
    }

    public static class ClientRate implements Serializable {
        @Expose
        @SerializedName("range_to")
        public Double rangeTo;
        @Expose
        @SerializedName("range_from")
        public Double rangeFrom;
        @Expose
        @SerializedName("pay_type_id")
        public int payTypeId;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
    }

//    public static class Services implements Serializable {
//        @Expose
//        @SerializedName("service_category_id")
//        public int serviceCategoryId;
//        @Expose
//        @SerializedName("name")
//        public String name;
//        @Expose
//        @SerializedName("id")
//        public int id;
//    }

//    public static class ServiceCategories implements Serializable {
//        @Expose
//        @SerializedName("name_app")
//        public String nameApp;
//        @Expose
//        @SerializedName("id")
//        public int id;
//    }

//    public static class JobPostState implements Serializable {
//        @Expose
//        @SerializedName("status")
//        public String status;
//        @Expose
//        @SerializedName("timestamp")
//        public String timestamp;
//        @Expose
//        @SerializedName("name")
//        public String name;
//        @Expose
//        @SerializedName("id")
//        public int id;
//    }

//    public static class JobPayType implements Serializable {
//        @Expose
//        @SerializedName("name")
//        public String name;
//        @Expose
//        @SerializedName("id")
//        public int id;
//    }

    public static class Attachments implements Serializable {
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName(value = "timestamp", alternate = "createdAt")
        public String timestamp;
        @Expose
        @SerializedName(value = "filename", alternate = "fileName")
        public String filename;
        @Expose
        @SerializedName("job_post_id")
        public int jobPostId;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class Services implements Serializable {
        @Expose
        @SerializedName("price")
        public double price;
        @Expose
        @SerializedName("attachment")
        public String attachment;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("platform_logo")
        public String platform_logo;

        @Expose
        @SerializedName("platform_name")
        public PlatformName platform_name;

    }

    public static class PlatformName implements Serializable {
        @Expose
        @SerializedName("ar")
        public String ar;
        @Expose
        @SerializedName("en")
        public String en;
    }

//    public static class JobPostBudget implements Serializable {
//        @Expose
//        @SerializedName("pay_type_id")
//        public int payTypeId;
//        @Expose
//        @SerializedName("budget")
//        public int budget;
//        @Expose
//        @SerializedName("job_post_id")
//        public int jobPostId;
//        @Expose
//        @SerializedName("id")
//        public int id;
//    }

    //string to model conversation
    public static ProjectByID getJobDetail(String jsonData) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonData,
                    ProjectByID.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

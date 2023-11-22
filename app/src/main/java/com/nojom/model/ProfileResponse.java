package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

public class ProfileResponse implements Serializable {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("google_id")
    @Expose
    public String googleId;
    @SerializedName("facebook_id")
    @Expose
    public String facebookId;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("working_experience")
    @Expose
    public Integer workingExperience;
    @SerializedName("workbase")
    @Expose
    public String workbase;
    @SerializedName("pay_rate")
    @Expose
    public Double payRate;
    @SerializedName("referral_code")
    @Expose
    public String referralCode;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("email_confirmation_date")
    @Expose
    public String emailConfirmationDate;
    @SerializedName("profile_status")
    @Expose
    public ProfileStatus profileStatus;
    @SerializedName("trust_rate")
    @Expose
    public TrustRate trustRate;
    @SerializedName("profile_type_id")
    @Expose
    public Integer profileTypeId;
    @SerializedName("trust_rate_status")
    @Expose
    public TrustRateStatus trustRateStatus;
    @SerializedName("signup_mode")
    @Expose
    public String signupMode;
    @SerializedName("service_category_id")
    @Expose
    public Integer serviceCategoryId;
    @SerializedName("isJobPostFree")
    @Expose
    public String isJobPostFree;
    @SerializedName("img")
    @Expose
    public String profilePic;
    @SerializedName("headlines")
    @Expose
    public String headlines;
    @SerializedName("websites")
    @Expose
    public String websites;
    @SerializedName("contact_no")
    @Expose
    public String contactNo;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("summaries")
    @Expose
    public String summaries;
    @SerializedName("resumes")
    @Expose
    public String resumes;

    @SerializedName("average_rate")
    @Expose
    public Double averageRate;

    @SerializedName("countryID")
    @Expose
    public Integer countryID;
    @SerializedName("countryName")
    @Expose
    public String countryName;
    @SerializedName("stateID")
    @Expose
    public Integer stateID;
    @SerializedName("stateName")
    @Expose
    public String stateName;
    @SerializedName("cityID")
    @Expose
    public Integer cityID;
    @SerializedName("cityName")
    @Expose
    public String cityName;
    @SerializedName("agent_survey")
    @Expose
    public int agentSurvey;

    //    @SerializedName("add_country")
//    @Expose
//    public String addCountry;
//    @SerializedName("add_region")
//    @Expose
//    public String addRegion;
//    @SerializedName("add_city")
//    @Expose
//    public String addCity;
    @SerializedName("add_longitude")
    @Expose
    public String addLongitude;
    @SerializedName("add_latitude")
    @Expose
    public String addLatitude;
    @SerializedName("add_pro_address")
    @Expose
    public String addProAddress;


    @SerializedName("percentage")
    @Expose
    public Percentage percentage;
    @SerializedName("expertise")
    @Expose
    public Expertise expertise;
    @SerializedName("language")
    @Expose
    public List<Language> language = null;
    @SerializedName("skills")
    @Expose
    public List<Skill> skills = null;
    @SerializedName("experiences")
    @Expose
    public List<Experiences> experiences;
    @SerializedName("educations")
    @Expose
    public List<Education> educations = null;
    @SerializedName("profile_pay_types")
    @Expose
    public List<ProfilePayType> profilePayTypes = null;
    @SerializedName("file_paths")
    @Expose
    public FilePaths filePaths;
    @SerializedName("profile_publicity")
    @Expose
    public List<ProfilePublicity> profilePublicity = null;
    @SerializedName("firebaseUrl")
    @Expose
    public String firebaseUrl;

    @SerializedName("profile_agencies")
    @Expose
    public ProfileAgencies profile_agencies;

    @SerializedName("profile_social_platform")
    @Expose
    public List<SocialPlatform> profile_social_platform = null;

    public static class Expertise implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("name_app")
        @Expose
        public String nameApp;
        @SerializedName("length")
        @Expose
        public Integer length;
    }

    public static class ProfilePayType implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("detail")
        @Expose
        public String detail;
        @SerializedName("status")
        @Expose
        public Integer status;

        public String getDetail() {
            return detail.replace("Free", "");
        }
    }

    public static class Education implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("degree")
        @Expose
        public String degree;
        @SerializedName("school_name")
        @Expose
        public String schoolName;
        @SerializedName("start_date")
        @Expose
        public String startDate;
        @SerializedName("end_date")
        @Expose
        public String endDate;
        @SerializedName("level")
        @Expose
        public Integer level;
    }

    public static class Skill implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("rating")
        @Expose
        public Integer rating;
        @SerializedName("working_experience")
        @Expose
        public Object workingExperience;
        @SerializedName("name")
        @Expose
        public String name;
    }

    public static class SocialPlatform implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("profile_id")
        @Expose
        public Integer profile_id;

        @SerializedName("platform_id")
        @Expose
        public Integer platform_id;
        @SerializedName("social_platform_url")
        @Expose
        public String social_platform_url;
        @SerializedName("followers")
        @Expose
        public String followers;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("name_ar")
        @Expose
        public String name_ar;
        @SerializedName("platform_icon")
        @Expose
        public String platform_icon;
        @SerializedName("colorCode")
        @Expose
        public String colorCode;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return name_ar != null ? name_ar : name;
            }
            return name;
        }
    }

    public static class Language implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("level")
        @Expose
        public Integer level;
        @SerializedName("name")
        @Expose
        public String name;
    }

    public static class Experiences implements Serializable {
        @Expose
        @SerializedName("service_categories")
        public Service service;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("length")
        public int length;
        @Expose
        @SerializedName("end_date")
        public String endDate;
        @Expose
        @SerializedName("start_date")
        public String startDate;
        @Expose
        @SerializedName("company_name")
        public String companyName;
        @Expose
        @SerializedName("service_id")
        public int serviceId;
        @Expose
        @SerializedName("profile_id")
        public int profileId;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("is_current")
        public Integer isCurrent;
    }

    public static class Addresses implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("profile_id")
        @Expose
        public Integer profileId;
        @SerializedName("country")
        @Expose
        public String country;
        @SerializedName("region")
        @Expose
        public String region;
        @SerializedName("city")
        @Expose
        public String city;
        @SerializedName("longitude")
        @Expose
        public Float longitude;
        @SerializedName("latitude")
        @Expose
        public Float latitude;
        @SerializedName("pro_address")
        @Expose
        public String proAddress;
        @SerializedName("timestamp")
        @Expose
        public String timestamp;
        @SerializedName("status")
        @Expose
        public Integer status;
    }

    public static class Percentage implements Serializable {

        @SerializedName("private_info")
        @Expose
        public Integer privateInfo;
        @SerializedName("skill")
        @Expose
        public Integer skill;
        @SerializedName("professional_info")
        @Expose
        public Integer professionalInfo;
        @SerializedName("verification")
        @Expose
        public Integer verification;
        @SerializedName("total_percentage")
        @Expose
        public Double totalPercentage;
    }

    public static class ProfileStatus implements Serializable {

        @SerializedName("private_info")
        @Expose
        public Integer privateInfo;
        @SerializedName("skill")
        @Expose
        public Integer skill;
        @SerializedName("professional_info")
        @Expose
        public Integer professionalInfo;
        @SerializedName("verification")
        @Expose
        public Integer verification;
        @SerializedName("total_percentage")
        @Expose
        public Double totalPercentage;
    }

    public static class TrustRate implements Serializable {

        @SerializedName("email")
        @Expose
        public Integer email;
        @SerializedName("phone_number")
        @Expose
        public Integer phoneNumber;

        @SerializedName("google")
        @Expose
        public Integer google = 0;
        @SerializedName("facebook")
        @Expose
        public Integer facebook;
        @SerializedName("payment")
        @Expose
        public Integer payment;
        @SerializedName("mawthooq")
        @Expose
        public Integer mawthooq = 0;
        @SerializedName("Verify_id")
        @Expose
        public Integer verifyId;
        @SerializedName("total_points")
        @Expose
        public Integer totalPoints;
        @SerializedName("points_needed")
        @Expose
        public Integer pointsNeeded;
    }

    public static class ProfileAgencies implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("profile_id")
        @Expose
        public Integer profile_id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("about")
        @Expose
        public String about;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("website")
        @Expose
        public String website;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("note")
        @Expose
        public String note;

        @SerializedName("name_public")
        @Expose
        public Integer name_public;
        @SerializedName("email_public")
        @Expose
        public Integer email_public;
        @SerializedName("phone_public")
        @Expose
        public Integer phone_public;
        @SerializedName("website_public")
        @Expose
        public Integer website_public;
        @SerializedName("about_public")
        @Expose
        public Integer about_public;
        @SerializedName("address_public")
        @Expose
        public Integer address_public;
        @SerializedName("note_public")
        @Expose
        public Integer note_public;
    }

    public static class TrustRateStatus implements Serializable {

        @SerializedName("email")
        @Expose
        public Integer email;
        @SerializedName("facebook")
        @Expose
        public Integer facebook;
        @SerializedName("payment")
        @Expose
        public Integer payment;
        @SerializedName("phone_number")
        @Expose
        public Integer phoneNumber;
        @SerializedName("Verify_id")
        @Expose
        public Integer verifyId;
    }

    public static class FilePaths implements Serializable {

        @SerializedName("client_attachments")
        @Expose
        public String clientAttachments;
        @SerializedName("agent_attachments")
        @Expose
        public String agentAttachments;
        @SerializedName("resume")
        @Expose
        public String resume;
        @SerializedName("submitted_files")
        @Expose
        public String submittedFiles;
        @SerializedName("portfolios_files")
        @Expose
        public String portfoliosFiles;
        @SerializedName("img")
        @Expose
        public String img;
        @SerializedName("img_id")
        @Expose
        public String imgId;
        @SerializedName("client_img")
        @Expose
        public String clientImg;
    }

    //string to model conversation
    public static ProfileResponse getProfileObject(String jsonData) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonData,
                    ProfileResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ProfileResponse.Language> getSelectedLanguageList(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<ProfileResponse.Language>>() {
        }.getType());
    }

    //string to model conversation
    public static Addresses getAddress(String jsonData) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonData,
                    Addresses.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class ProjectReview {//not delete
        @Expose
        @SerializedName("comment")
        public String comment;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("rate")
        public float rate;
        @Expose
        @SerializedName("title")
        public String title;
    }

    public static class Service {
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("name_app")
        public String nameApp;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class ProfilePublicity implements Serializable {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("publicity_type")
        @Expose
        public String publicityType;
        @SerializedName("status")
        @Expose
        public String status;
    }

    public static class VerifiedWith {

        public String name;
        public Integer isVerified;

        public VerifiedWith(String name, Integer isVerified) {
            this.name = name;
            this.isVerified = isVerified;
        }
    }

    public static List<ProjectReview> getAgentReview(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<ProjectReview>>() {
        }.getType());
    }
}
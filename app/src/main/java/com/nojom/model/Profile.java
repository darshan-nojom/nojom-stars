package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Profile extends GeneralModel {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("profile_picture")
    @Expose
    public String profile_picture;
    @SerializedName("req_status")
    @Expose
    public String req_status;
    @SerializedName("req_status_updated_at")
    @Expose
    public String req_status_updated_at;
    @SerializedName("client_balance")
    @Expose
    public Object clientBalance;
    @SerializedName("total_service_price")
    @Expose
    public Double total_service_price;
    @SerializedName("client_note")
    @Expose
    public String client_note;
    @SerializedName("client_survey")
    @Expose
    public Integer clientSurvey = 0;
    @SerializedName("working_experience")
    @Expose
    public Integer workingExperience;
    @SerializedName("cr_status")
    @Expose
    public Integer cr_status;//0 or null = not uploaded, 2=waiting, 1= Approved
    @SerializedName("vat_status")
    @Expose
    public Integer vat_status;//0 or null = not uploaded, 2=waiting, 1= Approved
    @SerializedName("workbase")
    @Expose
    public String workbase;
    @SerializedName("is_verified")
    @Expose
    public Integer is_verified;//0=unverified, 2=Submit for verified, 1= verified
    @SerializedName("company_name")
    @Expose
    public String company_name;
    @SerializedName("brand_name")
    @Expose
    public String brand_name;
    @SerializedName("cr_number")
    @Expose
    public String cr_number;
    @SerializedName("cr_file")
    @Expose
    public String cr_file;
    @SerializedName("commercial_registration_id")
    @Expose
    public Integer commercial_registration_id;

    @SerializedName("vat_registration_id")
    @Expose
    public Integer vat_registration_id;
    @SerializedName("vat_number")
    @Expose
    public String vat_number;
    @SerializedName("vat_file")
    @Expose
    public String vat_file;
    @SerializedName("pay_rate")
    @Expose
    public Object payRate;
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
    //    @SerializedName("trust_rate_status")
//    @Expose
//    public Object trustRateStatus;
    @SerializedName("signup_mode")
    @Expose
    public String signupMode;
    @SerializedName("profile_pic")
    @Expose
    public String profilePic;
    @SerializedName(value = "countryName", alternate = "country")
    @Expose
    public String countryName;
    @SerializedName("countryNameAr")
    @Expose
    public String countryNameAr;
    //    @SerializedName("country")
//    @Expose
//    public String country;
    @SerializedName("countryID")
    @Expose
    public Integer countryID;
    @SerializedName("stateName")
    @Expose
    public String stateName;
    @SerializedName("stateNameAr")
    @Expose
    public String stateNameAr;
    @SerializedName("region")
    @Expose
    public String region;
    @SerializedName("stateID")
    @Expose
    public Integer stateID;
    @SerializedName("cityName")
    @Expose
    public String cityName;
    @SerializedName("cityNameAr")
    @Expose
    public String cityNameAr;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("cityID")
    @Expose
    public Integer cityID;
    @SerializedName("longitude")
    @Expose
    public Float longitude;
    @SerializedName("latitude")
    @Expose
    public Float latitude;
    @SerializedName("pro_address")
    @Expose
    public String proAddress;
    @SerializedName("contact_no")
    @Expose
    public String contactNo;

    @SerializedName("aboutus_id")
    @Expose
    public int aboutus_id;

    @SerializedName("other_aboutus")
    @Expose
    public String other_aboutus;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("count_rating")
    @Expose
    public Integer countRating;
    @SerializedName("rate")
    @Expose
    public Object rate;
    @SerializedName("percentage")
    @Expose
    public Percentage percentage;
    @SerializedName("path")
    @Expose
    public String path;
    @SerializedName("mobile_prefix")
    @Expose
    public String mobilePrefix;
    @SerializedName("profile_type")
    @Expose
    public ProfileType profileType;
    @SerializedName("file_path")
    @Expose
    public FilePath filePath;
    @SerializedName("aboutus")
    @Expose
    public List<About> about;
    @SerializedName("categories")
    @Expose
    public List<Category> categories;


    public String getMobilePrefix(String contactNo) {
        if (contactNo != null) {
            String[] split = contactNo.split("\\.");
            if (split.length == 2) {
                return split[0].replace(" ", "");
            }
        }
        return mobilePrefix;
    }

    public static Profile getProfileInfo(String responseBody) {
        return new Gson().fromJson(responseBody, Profile.class);
    }

    public String getCountryName(String lang) {
        if (lang.equals("ar")) {
            return countryNameAr == null ? countryName : countryNameAr;
        } else {
            return countryName;
        }
    }

    public String getStateName(String lang) {
        if (lang.equals("ar")) {
            return stateNameAr == null ? stateName : stateNameAr;
        } else {
            return stateName;
        }
    }

    public String getCityName(String lang) {
        if (lang.equals("ar")) {
            return cityNameAr == null ? cityName : cityNameAr;
        } else {
            return cityName;
        }
    }

    public static class ProfileType {
        @Expose
        @SerializedName("type")
        public String type;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class About {
        @Expose
        @SerializedName("id")
        public Integer id;
        @Expose
        @SerializedName("name")
        public String name;
        @SerializedName("nameAr")
        public String nameAr;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return nameAr == null ? name : nameAr;
            } else {
                return name;

            }
        }
    }

    public static class Category implements Serializable {
        @Expose
        @SerializedName("name")
        public String name;
        @SerializedName("name_ar")
        public String name_ar;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return name_ar == null ? name : name_ar;
            } else {
                return name;

            }
        }
    }

    public static class Percentage {
        @Expose
        @SerializedName("total_percentage")
        public int totalPercentage = 0;
        @Expose
        @SerializedName("verification")
        public int verification;
        @Expose
        @SerializedName("professional_info")
        public int professionalInfo;
        @Expose
        @SerializedName("skill")
        public int skill;
        @Expose
        @SerializedName("private_info")
        public int privateInfo;
        @Expose
        @SerializedName("profile")
        public int profile;
    }

    public static class TrustPoints {
        @Expose
        @SerializedName("total_points")
        public int totalPoints;
        @Expose
        @SerializedName("Verify_id")
        public int verifyId;
        @Expose
        @SerializedName("payment")
        public int payment;
        @Expose
        @SerializedName("facebook")
        public int facebook;
        @Expose
        @SerializedName("phone_number")
        public int phoneNumber;
        @Expose
        @SerializedName("email")
        public int email;
    }

    public static class Photo {
        @Expose
        @SerializedName("img")
        public String img;
    }

    public static class Expertise {
        @Expose
        @SerializedName("service_category")
        public Services services;
        @Expose
        @SerializedName("length")
        public int length;
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
        @SerializedName("service_category_id")
        public int serviceCategoryId;
    }

    public static class Educations {
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("level")
        public int level;
        @Expose
        @SerializedName("end_date")
        public String endDate;
        @Expose
        @SerializedName("start_date")
        public String startDate;
        @Expose
        @SerializedName("school_name")
        public String schoolName;
        @Expose
        @SerializedName("degree")
        public String degree;
        @Expose
        @SerializedName("profile_id")
        public int profileId;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class Experiences {
        @Expose
        @SerializedName("service_category")
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
    }

    public static class Services {
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

    public static class Website {
        @Expose
        @SerializedName("website")
        public String website;
    }

    public static class Headline {
        @Expose
        @SerializedName("content")
        public String content;
    }

    public static class ProfileLanguages {
        @Expose
        @SerializedName("language")
        public Language language;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("level")
        public int level;
        @Expose
        @SerializedName("language_id")
        public int languageId;
        @Expose
        @SerializedName("profile_id")
        public int profileId;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class Language {
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("order_by")
        public int orderBy;
        @Expose
        @SerializedName("is_popular")
        public int isPopular;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class ProfilePayTypes {
        @Expose
        @SerializedName("pay_type")
        public PayType payType;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("pay_type_id")
        public int payTypeId;
        @Expose
        @SerializedName("profile_id")
        public int profileId;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class PayType {
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
        @SerializedName("detail")
        public String detail;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class Resume {
        @Expose
        @SerializedName("file")
        public String file;
    }

    public static class ProfileSkills {
        @Expose
        @SerializedName("skill")
        public Skill skill;
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("rating")
        public int rating;
        @Expose
        @SerializedName("skill_id")
        public int skillId;
        @Expose
        @SerializedName("profile_id")
        public int profileId;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class Skill {
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("skill_category_id")
        public int skillCategoryId;
        @Expose
        @SerializedName("description")
        public String description;
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("id")
        public int id;
    }

    public static class Summary {
        @Expose
        @SerializedName("content")
        public String content;
    }

    public static class ProfilePublicity {
        @Expose
        @SerializedName("headline")
        public int headline;
        @Expose
        @SerializedName("summary")
        public int summary;
        @Expose
        @SerializedName("resume")
        public int resume;
        @Expose
        @SerializedName("hour_rate")
        public int hourRate;
        @Expose
        @SerializedName("employment")
        public int employment;
        @Expose
        @SerializedName("education")
        public int education;
        @Expose
        @SerializedName("address")
        public int address;
        @Expose
        @SerializedName("pro_address")
        public int proAddress;
        @Expose
        @SerializedName("phone")
        public int phone;
        @Expose
        @SerializedName("email")
        public int email;
        @Expose
        @SerializedName("website")
        public int website;
    }

    public static class ProfileStatus {

        @SerializedName("profile")
        @Expose
        public Integer profile;
        @SerializedName("total_percentage")
        @Expose
        public Integer totalPercentage;
        @SerializedName("verification")
        @Expose
        public Integer verification;
    }

    public static class TrustRate {

        @SerializedName("email")
        @Expose
        public Integer email;
        @SerializedName("phone_number")
        @Expose
        public Integer phoneNumber;
        @SerializedName("facebook")
        @Expose
        public Integer facebook;

        @SerializedName("cr_id")
        @Expose
        public Integer cr_id;

        @SerializedName("payment")
        @Expose
        public Integer payment;
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

    public static class VerifiedWith {

        public String name;
        public Integer isVerified;

        public VerifiedWith(String name, Integer isVerified) {
            this.name = name;
            this.isVerified = isVerified;
        }
    }

    public class FilePath {
        @SerializedName("PATH_PROFILE_PIC_AGENT")
        @Expose
        public String pathProfilePicAgent;
        @SerializedName("PATH_AGENT_RESUME")
        @Expose
        public String pathAgentResume;
        @SerializedName("PATH_SOCIAL_SURVEY_SCREENSHOT")
        @Expose
        public String pathSocialSurveyScreenshot;
        @SerializedName("PATH_PROFILE_PIC_CLIENT")
        @Expose
        public String pathProfilePicClient;
        @SerializedName("PATH_PROFILE_VERIFICATION_PROOF_SCREENSHOT")
        @Expose
        public String pathProfileVerificationProofScreenshot;
        @SerializedName("PATH_PROFILE_JOB_ATTACHMENT")
        @Expose
        public String pathProfileJobAttachment;
        @SerializedName("PATH_AGENT_FILE")
        @Expose
        public String pathAgentFile;
        @SerializedName("PATH_AGENT_PORTFOLIO")
        @Expose
        public String pathAgentPortfolio;
        @SerializedName("PATH_GIGS_ATTACHMENT")
        @Expose
        public String pathGigsAttachment;
        @SerializedName("PATH_CLIENT_GIGS_ATTACHMENT")
        @Expose
        public String pathClientGigsAttachment;
        @SerializedName("PATH_AGENT_SUBMITTED_ATTACHMENT")
        @Expose
        public String pathAgentSubmittedAttachment;

        @SerializedName("PATH_COMMERCIAL_ATTECHMENT")
        @Expose
        public String path_commercial_attechment;

        @SerializedName("PATH_VAT_ATTECHMENT")
        @Expose
        public String path_vat_attechment;
    }
}

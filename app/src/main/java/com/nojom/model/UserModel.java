package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserModel extends GeneralModel implements Serializable {

    @Expose
    @SerializedName("profile_type")
    public ProfileType profileType;
    @Expose
    @SerializedName("referral_code")
    public String referralCode;
    @Expose
    @SerializedName("last_name")
    public String lastName;
    @Expose
    @SerializedName("first_name")
    public String firstName;
    @Expose
    @SerializedName("username")
    public String username;
    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("contact")
    public String contact;
    @Expose
    @SerializedName("profile_pic")
    public String profilePic;
    @Expose
    @SerializedName("email")
    public String email;

    public String jwt;

    public static class ProfileType {
        @Expose
        @SerializedName("type")
        public String type;
        @Expose
        @SerializedName("id")
        public int id;
    }
}

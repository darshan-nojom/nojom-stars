package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileClient extends GeneralModel implements Serializable {


    @Expose
    @SerializedName("block_status")
    public int blockStatus;
    @Expose
    @SerializedName("average_rate")
    public float averageRate;
    @Expose
    @SerializedName("trust_verification_points")
    public TrustVerificationPoints trustVerificationPoints;
    @Expose
    @SerializedName("img")
    public String img;
    @Expose
    @SerializedName("trust_rate")
    public String trustRate;
    @Expose
    @SerializedName("profile_status")
    public String profileStatus;
    @Expose
    @SerializedName("status")
    public int status;
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

    public static class TrustVerificationPoints implements Serializable {
        @Expose
        @SerializedName("points_needed")
        public int pointsNeeded;
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

    //string to model conversation
    public static ProfileClient getClientProfile(String jsonData) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonData,
                    ProfileClient.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

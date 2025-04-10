package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CampListByIdData implements Serializable {

    @SerializedName("id")
    @Expose
    public Integer id;
    /*@SerializedName("status")
    @Expose
    public String status;*/
    @SerializedName("stars")
    @Expose
    public List<Stars> stars;
    @SerializedName("star_details")
    @Expose
    public StarDetails star_details;

    /*By Id Response*/

    /*by id response data*/
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("launch_date")
    @Expose
    public String launch_date;
    @SerializedName("created_at")
    @Expose
    public String created_at;
    @SerializedName("brief")
    @Expose
    public String brief;
    @SerializedName("client_profile_picture")
    @Expose
    public String client_profile_picture;
    @SerializedName("attachment")
    @Expose
    public String attachment;

    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("profiles")
    @Expose
    public List<Profile> profiles;
    @SerializedName("social_platforms")
    @Expose
    public List<CampSocialPlatform> socialPlatforms;
    @SerializedName("client_name")
    @Expose
    public CampList.ClientName client_name;
    @SerializedName("services")
    @Expose
    public List<CampService> services;

    @SerializedName(value = "total_price", alternate = {"amount", "subtotal"})
    @Expose
    public double totalPrice;
    @SerializedName("tax_rate")
    @Expose
    public double tax_rate;
    @SerializedName("agency_fee_rate")
    @Expose
    public double agency_fee_rate;
    @SerializedName("progress")
    @Expose
    public double progress;
}


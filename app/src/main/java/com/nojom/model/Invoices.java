package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Invoices implements Serializable {

    @SerializedName("invoice_id")
    @Expose
    public Integer invoiceId;
    @SerializedName("invoice_no")
    @Expose
    public String invoiceNo;
    @SerializedName("total_price")
    @Expose
    public Float totalPrice;
    @SerializedName("currency")
    @Expose
    public String currency;
    @SerializedName("campaign_id")
    @Expose
    public Integer campaignId;
    @SerializedName("campaign_title")
    @Expose
    public String campaignTitle;
    @SerializedName("campaign_launch_date")
    @Expose
    public String campaignLaunchDate;
    @SerializedName("campaign_attachment_url")
    @Expose
    public Object campaignAttachmentUrl;
    @SerializedName("campaign_brief")
    @Expose
    public String campaignBrief;
    @SerializedName("campaign_status")
    @Expose
    public String campaignStatus;
    @SerializedName("campaign_created_at")
    @Expose
    public String campaignCreatedAt;
    @SerializedName("client_profile_id")
    @Expose
    public Integer clientProfileId;
    @SerializedName("client_first_name")
    @Expose
    public String clientFirstName;
    @SerializedName("client_last_name")
    @Expose
    public String clientLastName;
    @SerializedName("profiles")
    @Expose
    public List<Profile> profiles;
    @SerializedName("social_platforms")
    @Expose
    public List<CampSocialPlatform> socialPlatforms;
    @SerializedName("services")
    @Expose
    public List<CampService> services;
    public boolean isShowProgress;
}

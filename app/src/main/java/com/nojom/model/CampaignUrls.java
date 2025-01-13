package com.nojom.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CampaignUrls {

    @SerializedName("attachment_urls")
    public List<String> attachment_urls;


    public CampaignUrls(List<String> attachment_urls) {
        this.attachment_urls= attachment_urls;
    }

    public CampaignUrls() {

    }
}

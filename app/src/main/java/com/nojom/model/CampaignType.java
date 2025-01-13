package com.nojom.model;

import com.google.gson.annotations.SerializedName;

public class CampaignType {

    @SerializedName("job_post_type")
    private String job_post_type;


    public CampaignType(String job_post_type) {
        this.job_post_type = job_post_type;
    }
}

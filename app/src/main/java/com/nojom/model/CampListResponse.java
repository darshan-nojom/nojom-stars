package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CampListResponse extends CommonModel implements Serializable {
    @Expose
    @SerializedName("data")
    public CampListData data;

    public static CampListResponse getCampaignDataList(String responseBody) {
        return new Gson().fromJson(responseBody, CampListResponse.class);
    }
}
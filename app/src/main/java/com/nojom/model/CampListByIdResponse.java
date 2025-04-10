package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CampListByIdResponse extends CommonModel implements Serializable {
    @Expose
    @SerializedName("data")
    public CampListByIdData data;

    public static CampListByIdResponse getCampaignDataList(String responseBody) {
        return new Gson().fromJson(responseBody, CampListByIdResponse.class);
    }
}
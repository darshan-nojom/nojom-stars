package com.nojom.api;


import com.nojom.model.CampListData;

public interface CampaignListener {

    void successResponse(CampListData responseBody, String url, String message);

    void failureResponse(Throwable throwable, String url, String message);

}
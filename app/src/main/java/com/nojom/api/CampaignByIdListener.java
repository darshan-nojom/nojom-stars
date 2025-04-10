package com.nojom.api;


import com.nojom.model.CampListByIdData;
import com.nojom.model.CampListData;

public interface CampaignByIdListener {

    void successResponse(CampListByIdData responseBody, String url, String message);

    void failureResponse(Throwable throwable, String url, String message);

}
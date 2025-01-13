package com.nojom.api;

public interface RequestResponseListener {

    void successResponse(String responseBody, String url, String message, String data);

    void failureResponse(Throwable throwable, String url, String message);

}
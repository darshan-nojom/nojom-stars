package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BraintreeToken extends GeneralModel {

    @Expose
    @SerializedName("token")
    public String token;

    public static BraintreeToken getBraintreeToken(String responseBody) {
        return new Gson().fromJson(responseBody, BraintreeToken.class);
    }
}

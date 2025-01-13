package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BankAccountResponse extends CommonModel implements Serializable {
    @Expose
    @SerializedName("data")
    public WalletAccount data;

    public static BankAccountResponse getWalletData(String responseBody) {
        return new Gson().fromJson(responseBody, BankAccountResponse.class);
    }
}
package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WalletTxnResponse extends CommonModel implements Serializable {
    @Expose
    @SerializedName("data")
    public List<WalletData> data;

    public static WalletTxnResponse getWalletData(String responseBody) {
        return new Gson().fromJson(responseBody, WalletTxnResponse.class);
    }
}
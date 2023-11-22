package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReferralHistory extends GeneralModel {

    @Expose
    @SerializedName("total_balance")
    public Double totalBalance;
    @Expose
    @SerializedName("referral_data")
    public List<Data> data;

    public static class Data {
        @Expose
        @SerializedName("username")
        public String username;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("amount")
        public Double amount;
        @Expose
        @SerializedName("transaction_status")
        public int transactionStatus;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
    }

    public static ReferralHistory getPromoCodeHistory(String responseBody) {
        return new Gson().fromJson(responseBody, ReferralHistory.class);
    }
}

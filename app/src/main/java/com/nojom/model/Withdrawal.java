package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class Withdrawal extends GeneralModel {

    @Expose
    @SerializedName("status")
    public int status;
    @Expose
    @SerializedName("timestamp")
    public String timestamp;
    @Expose
    @SerializedName("payment_account_id")
    public int paymentAccountId;
    @Expose
    @SerializedName("amount")
    public double amount;
    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("account")
    public String account;
    @Expose
    @SerializedName("provider")
    public String provider;

    public static List<Withdrawal> getWithdrawals(String jsonData) {
        try {
            return new Gson().fromJson(jsonData, new TypeToken<List<Withdrawal>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

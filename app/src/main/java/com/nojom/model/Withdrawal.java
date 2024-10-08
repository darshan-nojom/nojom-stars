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
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("nameAr")
    public String nameAr;
    @Expose
    @SerializedName("beneficiary_name")
    public String beneficiary_name;
    @Expose
    @SerializedName("iban")
    public String iban;

    public String getName(String lang) {
        if (lang.equals("ar")) {
            return nameAr != null ? nameAr : name;
        }
        return name;
    }

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

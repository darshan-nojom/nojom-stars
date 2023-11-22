package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Payment extends GeneralModel implements Serializable {

    @Expose
    @SerializedName("provider")
    public String provider;
    @Expose
    @SerializedName("timestamp")
    public String timestamp;
    @Expose
    @SerializedName("is_primary")
    public String isPrimary;
    @Expose
    @SerializedName("verified")
    public String verified;
    @Expose
    @SerializedName("payment_type_id")
    public int paymentTypeId;
    @Expose
    @SerializedName("account")
    public String account;
    @Expose
    @SerializedName("profile_id")
    public int profileId;
    @Expose
    @SerializedName("id")
    public int id;
    @Expose
    @SerializedName("token")
    public String token;

    public static ArrayList<Payment> getAccounts(String jsonData) {
        try {
            return new Gson().fromJson(jsonData, new TypeToken<List<Payment>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

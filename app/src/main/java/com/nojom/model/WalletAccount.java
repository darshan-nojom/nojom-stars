package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WalletAccount implements Serializable {
    /*get wallet balance response*/
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("user_id")
    @Expose
    public int user_id;
    @SerializedName("bank_name")
    @Expose
    public String bank_name;
    @SerializedName("withdrawal_amount")
    @Expose
    public String withdrawal_amount;
    @SerializedName("beneficiary_name")
    @Expose
    public String beneficiary_name;
    @SerializedName("iban")
    @Expose
    public String iban;
    @SerializedName("account_number")
    @Expose
    public String account_number;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("updated_at")
    @Expose
    public String updated_at;

    public boolean isSelect;
}
package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WithdrawAccount implements Serializable {
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
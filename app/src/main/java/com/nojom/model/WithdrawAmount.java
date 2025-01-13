package com.nojom.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WithdrawAmount {

    @SerializedName("amount")
    public Double amount;
    @SerializedName("bank_account_id")
    public int bank_account_id;


    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setBank_account_id(int bank_account_id) {
        this.bank_account_id = bank_account_id;
    }

    public WithdrawAmount() {

    }
}

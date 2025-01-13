package com.nojom.model;

import com.google.gson.annotations.SerializedName;

public class AddCard {

    @SerializedName("bank_name")
    public String bank_name;
    @SerializedName("beneficiary_name")
    public String beneficiary_name;
    @SerializedName("iban")
    public String iban;
    @SerializedName("account_number")
    public String account_number;

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public void setBeneficiary_name(String beneficiary_name) {
        this.beneficiary_name = beneficiary_name;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public AddCard() {

    }
}

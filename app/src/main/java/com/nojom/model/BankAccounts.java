package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BankAccounts extends GeneralModel {

    @Expose
    @SerializedName("data")
    public ArrayList<Data> data;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("name")
        public String name;
        @Expose
        @SerializedName("nameAr")
        public String nameAr;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("beneficiary_name")
        public String beneficiary_name;
        @Expose
        @SerializedName("iban")
        public String iban;
        @Expose
        @SerializedName("bank_certificate")
        public String bank_certificate;
        @Expose
        @SerializedName("is_primary")
        public int is_primary;
        @Expose
        @SerializedName("status")
        public String status;
        @Expose
        @SerializedName("bank_id")
        public int bank_id;


        public boolean isSelected;

        public String getName(String lang) {
            if (lang.equals("ar")) {
                return nameAr != null ? nameAr : name;
            }
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            return this.id == ((BankAccounts.Data) obj).id;
        }
    }

    public static List<BankAccounts.Data> getBankList(String jsonData) {
        return new Gson().fromJson(jsonData, new TypeToken<List<BankAccounts.Data>>() {
        }.getType());
    }

    public static BankAccounts getBankAccount(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    BankAccounts.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

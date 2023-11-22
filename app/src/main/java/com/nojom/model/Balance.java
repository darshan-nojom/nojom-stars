package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Balance extends GeneralModel {

    @Expose
    @SerializedName("income")
    public List<Income> income;
    @Expose
    @SerializedName("total_balance")
    public double totalBalance;
    @Expose
    @SerializedName("pending_balance")
    public double pendingBalance;
    @Expose
    @SerializedName("available_balance")
    public double availableBalance;

    public static class Income implements Serializable {
        @Expose
        @SerializedName("status")
        public int status;
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("amount")
        public double amount;
        @Expose
        @SerializedName("job_post_id")
        public int jobPostId;
        @Expose
        @SerializedName("profile_id")
        public int profileId;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("type")
        public String type;//1==gig & 2==job
        @Expose
        @SerializedName("gigType")
        public String gigType;//1==custom
        @Expose
        @SerializedName("refundType")
        public String refundType; //1 = PARTIAL REFUND 2 = FULL REFUND
        @Expose
        @SerializedName("refunded_amount")
        public double refundedAmount;

        public boolean isShowProgress;
    }

    //string to model conversation
    public static Balance getBalance(String jsonData) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonData,
                    Balance.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

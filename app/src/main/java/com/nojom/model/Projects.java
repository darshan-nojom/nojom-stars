package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Projects extends GeneralModel implements Serializable {

    @Expose
    @SerializedName("count")
    public int count;
    @Expose
    @SerializedName("data")
    public List<Data> data;

    public static class Data implements Serializable {
        @Expose
        @SerializedName("job_refunds")
        public Object jobRefunds;//if is there any value that means label REFUND display otherwise not displayed in case null (In Progress,Completed & Submit Waiting For Payment)
        @Expose
        @SerializedName("timestamp")
        public String timestamp;
        @Expose
        @SerializedName("client_rate_id")
        public int clientRateId;//0= budget, 1-6= range, -1= Free
        @Expose
        @SerializedName("title")
        public String title;
        @Expose
        @SerializedName("id")
        public int id;
        @Expose
        @SerializedName("bids_count")
        public int bidsCount;
        @Expose
        @SerializedName("seen")
        public int seen;
        @Expose
        @SerializedName("profile_id")
        public int profileId;
        @Expose
        @SerializedName("offered")
        public String offered;
        @Expose
        @SerializedName("date_completed")
        public String dateCompleted;
        @Expose
        @SerializedName("job_post_state_id")
        public Integer jobPostStateId;
        @Expose
        @SerializedName("job_post_state_name")
        public String jobPostStateName;
        @Expose
        @SerializedName("job_post_state_name_ar")
        public String jobPostStateNameAr;

        public String getStateName(String lang) {
            if (lang.equals("ar")) {
                return jobPostStateNameAr != null ? jobPostStateNameAr : jobPostStateName;
            }
            return jobPostStateName;
        }

        @Expose
        @SerializedName("range_to")
        public String rangeTo;
        @Expose
        @SerializedName("range_from")
        public String rangeFrom;
        @Expose
        @SerializedName("budget")
        public Double budget;
        @Expose
        @SerializedName("job_post_budget_id")
        public Integer jobPostBudgetId;
        @Expose
        @SerializedName("job_post_id")
        public Integer jobPostId;
        @Expose
        @SerializedName("pay_type_id")
        public Integer payTypeId;
        @Expose
        @SerializedName("job")
        public String job;//gig & job
        @Expose
        @SerializedName("gigType")
        public String gigType;//1= Custom & 2 = standard

        @SerializedName("amount")
        @Expose
        public Double amount;
        @SerializedName("brief")
        @Expose
        public String brief;
        @SerializedName("attachment")
        @Expose
        public String attachment;
        @SerializedName("launch_date")
        @Expose
        public String launch_date;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("created_at")
        @Expose
        public String created_at;
        @SerializedName("type")
        @Expose
        public String type;
        @SerializedName("services")
        @Expose
        public List<ProjectByID.Services> services;

        public boolean isShowProgress;
    }

    //string to model conversation
    public static Projects getJobPostObject(String jsonData) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonData,
                    Projects.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

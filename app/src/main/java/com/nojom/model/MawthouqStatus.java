package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MawthouqStatus {
    @Expose
    @SerializedName("mawthooq_number")
    public String mawthooq_number;
    @Expose
    @SerializedName("status")
    public String status;

    public static MawthouqStatus getStatus(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    MawthouqStatus.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

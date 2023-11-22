package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeneralModel implements Serializable {

    @Expose
    @SerializedName("msg")
    public String msg;
    @Expose
    @SerializedName("success")
    public String success;
    @Expose
    @SerializedName("flag")
    public int flag;

    //string to model conversation
    public static GeneralModel getResponse(String jsonData) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonData,
                    GeneralModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

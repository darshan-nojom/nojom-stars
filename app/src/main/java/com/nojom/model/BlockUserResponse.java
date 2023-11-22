package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlockUserResponse extends GeneralModel {

    @Expose
    @SerializedName("profile_id")
    public String profile_id;

    //string to model conversation
    public static BlockUserResponse getBlockUnblockUser(String jsonData) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonData,
                    BlockUserResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

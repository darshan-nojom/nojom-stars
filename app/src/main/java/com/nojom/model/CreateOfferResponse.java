package com.nojom.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateOfferResponse extends GeneralModel {

    @Expose
    @SerializedName("agentID")
    public int agentID;
    @Expose
    @SerializedName("clientID")
    public String clientID;
    @Expose
    @SerializedName("gigID")
    public int gigID;
    @Expose
    @SerializedName("offerID")
    public int offerID;
    @Expose
    @SerializedName("offerTitle")
    public String offerTitle;
    @Expose
    @SerializedName("description")
    public String description;
    @Expose
    @SerializedName("parentServiceCategoryID")
    public String parentServiceCategoryID;
    @Expose
    @SerializedName("deadlineType")
    public int deadlineType;
    @Expose
    @SerializedName("deadlineValue")
    public int deadlineValue;
    @Expose
    @SerializedName("price")
    public double price;
    @Expose
    @SerializedName("gigType")
    public String gigType;
    @Expose
    @SerializedName("offerStatus")
    public int offerStatus;

    public static CreateOfferResponse getCreateOffer(String responseBody) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(responseBody,
                    CreateOfferResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

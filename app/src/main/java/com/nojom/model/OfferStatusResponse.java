package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OfferStatusResponse {

    @SerializedName("partitionKey")
    @Expose
    public String partitionKey;
    @SerializedName("senderId")
    @Expose
    public Integer senderId;
    @SerializedName("offerStatus")
    @Expose
    public Integer offerStatus;
    @SerializedName("receiverId")
    @Expose
    public Integer receiverId;
    @SerializedName("messageId")
    @Expose
    public Long messageId;
    @SerializedName("price")
    @Expose
    public Double price;
    @SerializedName("contractID")
    @Expose
    public Integer contractID;

}
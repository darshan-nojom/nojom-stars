package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Agents implements Serializable {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("last_name")
    @Expose
    public String lastName;
    @SerializedName("image")
    @Expose
    public Image image;
    @SerializedName("services")
    @Expose
    public List<AgentService> services;
    @SerializedName("categories")
    @Expose
    public List<AgentCategory> categories;

    public String notes;

    public boolean isShowProgress;

    public double getPrice(List<AgentService> services) {
        double finalPrice = 0;
        for (AgentService service : services) {
            if (service.price > 0 && service.isChecked && service.socialPlatformId == -1) {
                finalPrice = service.price;
                break;
            } else if (service.price > 0 && service.isChecked) {
                finalPrice = finalPrice + service.price;
            }
        }
        return finalPrice;
    }
}

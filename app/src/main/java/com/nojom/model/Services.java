package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Services implements Serializable {
    @Expose
    @SerializedName("services")
    public ArrayList<Serv> services;
    @Expose
    @SerializedName("service_description")
    public String service_description;
}

package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CampListData extends CommonModel implements Serializable {
    @SerializedName("currentPage")
    @Expose
    public String currentPage;
    @SerializedName("pageSize")
    @Expose
    public Integer pageSize;
    @SerializedName("totalRecords")
    @Expose
    public Integer totalRecords;
    @SerializedName("total")
    @Expose
    public Integer total;
    @SerializedName("totalPages")
    @Expose
    public Integer totalPages;
    @SerializedName("campaigns")
    @Expose
    public List<CampList> campaigns;
    @SerializedName("urls")
    @Expose
    public List<String> urls;

//    @SerializedName("invoices")
//    @Expose
//    public List<Invoices> invoices;

//    @SerializedName("agents")
//    @Expose
//    public List<Agents> agents;

    @SerializedName("id")
    @Expose
    public Integer id;
    /*@SerializedName("status")
    @Expose
    public String status;*/
    @SerializedName("stars")
    @Expose
    public List<Stars> stars;
    @SerializedName("star_details")
    @Expose
    public Stars star_details;
}


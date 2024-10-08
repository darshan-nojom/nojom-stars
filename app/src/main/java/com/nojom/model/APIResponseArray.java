package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class APIResponseArray extends CommonModel {
    @Expose
    @SerializedName("data")
    public List<CategoryResponse.CategoryData> data;
    @Expose
    @SerializedName("jwt")
    public String jwt;
}

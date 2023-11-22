package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MuteUnmute implements Serializable {

    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {
        @SerializedName("partitionKey")
        @Expose
        public String partitionKey;
        @SerializedName("c_mute")
        @Expose
        public boolean cMute;
        @SerializedName("a_mute")
        @Expose
        public boolean aMute;
    }
}

package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SenderReceiverData implements Serializable {

        @SerializedName("senderData")
        @Expose
        public ReceiverData senderData;
        @SerializedName("receiverData")
        @Expose
        public ReceiverData receiverData;

//    public class SenderData implements Serializable{
//
//        @SerializedName("id")
//        @Expose
//        public int id;
//        @SerializedName("first_name")
//        @Expose
//        public String first_name;
//        @SerializedName("last_name")
//        @Expose
//        public String last_name;
//        @SerializedName("username")
//        @Expose
//        public String username;
//        @SerializedName("profile_type_id")
//        @Expose
//        public int profile_type_id;
//        @SerializedName("isSocketOnline")
//        @Expose
//        public String isSocketOnline;
//        @SerializedName("is_chat")
//        @Expose
//        public String is_chat;
//        @SerializedName("profile_pic")
//        @Expose
//        public String profile_pic;
//        @SerializedName("path")
//        @Expose
//        public String path;
//
//    }

    public class ReceiverData implements Serializable{

        @SerializedName("id")
        @Expose
        public int id;
        @SerializedName("first_name")
        @Expose
        public String first_name;
        @SerializedName("last_name")
        @Expose
        public String last_name;
        @SerializedName("username")
        @Expose
        public String username;
        @SerializedName("profile_type_id")
        @Expose
        public int profile_type_id;
        @SerializedName("isSocketOnline")
        @Expose
        public String isSocketOnline;
        @SerializedName("is_chat")
        @Expose
        public String is_chat;
        @SerializedName("profile_pic")
        @Expose
        public String profile_pic;
        @SerializedName("path")
        @Expose
        public String path;

    }

}


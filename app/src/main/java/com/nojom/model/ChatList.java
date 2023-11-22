package com.nojom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ChatList implements Serializable {
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data {

        @SerializedName("data")
        @Expose
        public List<Datum> data = null;
        @SerializedName("path")
        @Expose
        public String path;
    }

    public class Datum {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("first_name")
        @Expose
        public String firstName;
        @SerializedName("last_name")
        @Expose
        public String lastName;
        @SerializedName("username")
        @Expose
        public String username;
        @SerializedName("profile_type_id")
        @Expose
        public Integer profileTypeId;
        @SerializedName("socketId")
        @Expose
        public String socketId;
        @SerializedName("isSocketOnline")
        @Expose
        public int isSocketOnline;
        @SerializedName("profile_pic")
        @Expose
        public String profilePic;
        @Expose
        @SerializedName("lastMessageData")
        public LastMessageData lastMessageData;
        @Expose
        @SerializedName("typing")
        public boolean typing;
    }

    public class LastMessageData {

        @SerializedName("messageSeenAt")
        @Expose
        public long messageSeenAt;
        @SerializedName("messageId")
        @Expose
        public String messageId;
//        @SerializedName("receiverId")
//        @Expose
//        public int receiverId;
        @SerializedName("senderId")
        @Expose
        public long senderId;
        @SerializedName("isMessageDeleted")
        @Expose
        public boolean isMessageDeleted;
        @SerializedName("messageCreatedAt")
        @Expose
        public long messageCreatedAt;
        //        @SerializedName("SK")
//        @Expose
//        public double sK;
        @SerializedName("message")
        @Expose
        public String message;
        //        @SerializedName("PK")
//        @Expose
//        public String pK;
        @SerializedName("isSeenMessage")
        @Expose
        public String isSeenMessage;
        @SerializedName("file")
        @Expose
        public File file;
    }

    public class File {
        @SerializedName("path")
        @Expose
        public String path;
        @SerializedName("files")
        @Expose
        public List<FileImages> files = null;
    }

    public static class FileImages {
        @SerializedName("fileId")
        @Expose
        public Double fileId;
        @SerializedName("SK")
        @Expose
        public Double sK;
        @SerializedName("file")
        @Expose
        public String file;

    }
}


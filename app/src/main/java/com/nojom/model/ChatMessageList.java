package com.nojom.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ChatMessageList implements Serializable {
    @SerializedName("data")
    @Expose
    public Data data;

    public class Data implements Serializable{
        @SerializedName("data")
        @Expose
        public List<DataChatList> dataChatList = null;
        @SerializedName("count")
        @Expose
        public Integer count;
        @SerializedName("scannedCount")
        @Expose
        public Integer scannedCount;
        @SerializedName("lastEvaluatedKey")
        @Expose
        public LastEvaluatedKey lastEvaluatedKey;
        @SerializedName("project")
        @Expose
        public Project project;

        @NonNull
        @Override
        public String toString() {
            return new Gson().toJson(lastEvaluatedKey);
        }
    }

    public class DataChatList {

        @SerializedName("messageSeenAt")
        @Expose
        public long messageSeenAt;
        @SerializedName("messageId")
        @Expose
        public Long messageId;
        @SerializedName("receiverId")
        @Expose
        public String receiverId;
        @SerializedName("senderId")
        @Expose
        public String senderId;
        @SerializedName("isMessageDeleted")
        @Expose
        public boolean isMessageDeleted;
        @SerializedName("messageCreatedAt")
        @Expose
        public long messageCreatedAt;
        @SerializedName("SK")
        @Expose
        public long sK;
        @SerializedName("message")
        @Expose
        public String message;
        @SerializedName("PK")
        @Expose
        public String pK;
        @SerializedName("isSeenMessage")
        @Expose
        public String isSeenMessage;
        @SerializedName("file")
        @Expose
        public File file;
        @SerializedName("offer")
        @Expose
        public Offer offer;
        @SerializedName("self")
        @Expose
        public boolean self;
        public boolean isDayChange = false;
        public boolean isShowOfferProgress;
    }

    public class LastEvaluatedKey {
        @SerializedName("PK")
        @Expose
        public String pK;
        @SerializedName("SK")
        @Expose
        public double sK;
    }

    public class Project {
        @SerializedName("projectId")
        @Expose
        public int projectId;
        @SerializedName("projectType")
        @Expose
        public String projectType;
        @SerializedName("isMute")
        @Expose
        public boolean isMute;
        @SerializedName("c_mute")
        @Expose
        public boolean c_mute;
        @SerializedName("a_mute")
        @Expose
        public boolean a_mute;
    }

    public class File {
        @SerializedName("path")
        @Expose
        public String path;
        @SerializedName("files")
        @Expose
        public List<FileImages> files = null;

    }

    public class Offer {
        @SerializedName("offerTitle")
        @Expose
        public String offerTitle;
        @SerializedName("offerID")
        @Expose
        public String offerID;
        @SerializedName("gigType")
        @Expose
        public String gigType;
        @SerializedName("clientID")
        @Expose
        public String clientID;
        @SerializedName("agentID")
        @Expose
        public String agentID;
        @SerializedName("contractID")
        @Expose
        public int contractID;
        @SerializedName("deadlineType")
        @Expose
        public String deadlineType;
        @SerializedName("deadlineValue")
        @Expose
        public String deadlineValue;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("gigID")
        @Expose
        public String gigID;
        @SerializedName("parentServiceCategoryID")
        @Expose
        public String parentServiceCategoryID;
        @SerializedName("price")
        @Expose
        public Double price;
        @SerializedName("offerStatus")
        @Expose
        public int offerStatus;
    }

    public class FileImages {
        @SerializedName("fileId")
        @Expose
        public double fileId;
        @SerializedName("SK")
        @Expose
        public double sK;
        @SerializedName("file")
        @Expose
        public String file;
        @SerializedName("fileStorage")
        @Expose
        public String fileStorage;
        @SerializedName("firebaseUrl")
        @Expose
        public String firebaseUrl;

    }

}


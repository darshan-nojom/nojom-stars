package com.nojom.model;

import java.io.Serializable;
import java.util.Date;

public class Chat implements Serializable {

    public String sender_id;
    public String sender_name;
    public String sender_pic;
    //public long s_last_active;
    public String receiver_id;
    public String receiver_name;
    public String receiver_pic;
    //public long r_last_active;
    public Date date;
    public String last_message;
    public String id;
    public boolean s_online;
    public boolean r_online;
    public boolean s_seen;
    public boolean s_chatscreen_open;
    public boolean r_seen;
    public boolean r_chatscreen_open;
    public boolean s_mute;
    public boolean r_mute;
    public boolean is_keywords;
    public boolean is_redflag;
    public boolean is_chat;
    public boolean is_alert;
    public String projectId;

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public void setSender_pic(String sender_pic) {
        this.sender_pic = sender_pic;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public void setReceiver_pic(String receiver_pic) {
        this.receiver_pic = receiver_pic;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setS_online(boolean s_online) {
        this.s_online = s_online;
    }

    public void setR_online(boolean r_online) {
        this.r_online = r_online;
    }

    public void setS_seen(boolean s_seen) {
        this.s_seen = s_seen;
    }

    public void setS_chatscreen_open(boolean s_chatscreen_open) {
        this.s_chatscreen_open = s_chatscreen_open;
    }

    public void setR_seen(boolean r_seen) {
        this.r_seen = r_seen;
    }

    public void setR_chatscreen_open(boolean r_chatscreen_open) {
        this.r_chatscreen_open = r_chatscreen_open;
    }

    public void setS_mute(boolean s_mute) {
        this.s_mute = s_mute;
    }

    public void setR_mute(boolean r_mute) {
        this.r_mute = r_mute;
    }

    public void setIs_keywords(boolean is_keywords) {
        this.is_keywords = is_keywords;
    }

    public void setIs_redflag(boolean is_redflag) {
        this.is_redflag = is_redflag;
    }

    public void setIs_chat(boolean is_chat) {
        this.is_chat = is_chat;
    }

    public void setIs_alert(boolean is_alert) {
        this.is_alert = is_alert;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}

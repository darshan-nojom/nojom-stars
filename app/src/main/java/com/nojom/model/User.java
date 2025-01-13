package com.nojom.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String campaign_id;
    public int id;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(int id, String campId) {
        this.id = id;
        this.campaign_id = campId;
    }

}
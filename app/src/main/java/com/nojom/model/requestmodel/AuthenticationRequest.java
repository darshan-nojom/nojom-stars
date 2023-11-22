package com.nojom.model.requestmodel;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.nojom.util.AESHelper;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static com.nojom.util.Constants.AGENT_PROFILE;
import static com.nojom.util.Constants.PLATFORM;

public class AuthenticationRequest {
    String username, password, facebook_id, google_id, email, device_token, first_name, last_name;
    int profile_type_id = AGENT_PROFILE, device_type = PLATFORM;

    public AuthenticationRequest() {

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFacebook_id(String facebook_id) {
        this.facebook_id = facebook_id;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public void setProfile_type_id(int profile_type_id) {
        this.profile_type_id = profile_type_id;
    }

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    @NonNull
    @Override
    public String toString() {
        try {
            return AESHelper.encrypt(new GsonBuilder().create().toJson(this, AuthenticationRequest.class));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return "";
    }
}

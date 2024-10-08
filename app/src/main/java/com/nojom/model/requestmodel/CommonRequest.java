package com.nojom.model.requestmodel;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;

import java.util.List;

public class CommonRequest {

    public static class EditPaymentAccount {
        int is_primary, payment_account_id;

        public void setIs_primary(int is_primary) {
            this.is_primary = is_primary;
        }

        public void setPayment_account_id(int payment_account_id) {
            this.payment_account_id = payment_account_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, EditPaymentAccount.class);
        }
    }

    public static class PayonnerPaymentAccount {
        int payment_type_id;
        String account;


        public void setPayment_type_id(int payment_type_id) {
            this.payment_type_id = payment_type_id;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, PayonnerPaymentAccount.class);
        }
    }


    public static class DeleteAccount {
        int payment_account_id;

        public void setPayment_account_id(int payment_account_id) {
            this.payment_account_id = payment_account_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, DeleteAccount.class);
        }
    }

    public static class UpdatePassword {
        String password, old_password;

        public void setPassword(String password) {
            this.password = password;
        }

        public void setOld_password(String old_password) {
            this.old_password = old_password;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdatePassword.class);
        }
    }

    public static class ForgetPassword {
        String email;

        public void setEmail(String email) {
            this.email = email;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, ForgetPassword.class);
        }
    }

    public static class SendOTP {
        String email;
        String phone;

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, SendOTP.class);
        }
    }

    public static class CheckContacts {
        String email;
        String contact;

        public void setEmail(String email) {
            this.email = email;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, CheckContacts.class);
        }
    }

    public static class SendOtpPhone {
        String phone;

        public void setPhone(String phone) {
            this.phone = phone;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, SendOtpPhone.class);
        }
    }

    public static class ResetPassword {
        String email, password, otp;

        public void setEmail(String email) {
            this.email = email;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, ResetPassword.class);
        }
    }

    public static class ResetPasswordByToken {
        String password;


        public void setPassword(String password) {
            this.password = password;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, ResetPasswordByToken.class);
        }
    }

    public static class ResetPasswordByPhone {
        String phone, password;

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setPassword(String password) {
            this.password = password;
        }


        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, ResetPasswordByPhone.class);
        }
    }

    public static class CheckContact {
        String contact;

        public void setContact(String contact) {
            this.contact = contact;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, CheckContact.class);
        }
    }

    public static class UpdateWebsite {
        String website;
        int status;

        public void setWebsite(String website) {
            this.website = website;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateWebsite.class);
        }
    }

    public static class UpdateSummary {
        String content;
        Integer public_status;

        public void setPublic_status(Integer public_status) {
            this.public_status = public_status;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateSummary.class);
        }
    }

    public static class AddEducations {
        String degree, school_name, start_date, end_date, level;

        public void setDegree(String degree) {
            this.degree = degree;
        }

        public void setSchool_name(String school_name) {
            this.school_name = school_name;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddEducations.class);
        }
    }

    public static class AddClientReview {
        String review, job_post_id, client_id;

        public void setReview(String review) {
            this.review = review;
        }

        public void setJob_post_id(String job_post_id) {
            this.job_post_id = job_post_id;
        }

        public void setClient_id(String client_id) {
            this.client_id = client_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddClientReview.class);
        }
    }

    public static class AddPortfolio {
        String type, title, portfolio_id;

        public void setType(String type) {
            this.type = type;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setPortfolio_id(String portfolio_id) {
            this.portfolio_id = portfolio_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddPortfolio.class);
        }
    }

    public static class UpdatePortfolio {
        String type, title, portfolio_id, sort, file_id;

        public void setType(String type) {
            this.type = type;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setPortfolio_id(String portfolio_id) {
            this.portfolio_id = portfolio_id;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public void setFile_id(String file_id) {
            this.file_id = file_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdatePortfolio.class);
        }
    }

    public static class AddBid {
        String job_post_id, message, currency, deadline_type, deadline_value, bid_charges, amount;

        public void setJob_post_id(String job_post_id) {
            this.job_post_id = job_post_id;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public void setDeadline_type(String deadline_type) {
            this.deadline_type = deadline_type;
        }

        public void setDeadline_value(String deadline_value) {
            this.deadline_value = deadline_value;
        }

        public void setBid_charges(String bid_charges) {
            this.bid_charges = bid_charges;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddBid.class);
        }
    }

    public static class EditBid {
        String job_post_bid_id, bid_charges, amount, message, deadline_type, deadline_value;

        public void setJob_post_bid_id(String job_post_bid_id) {
            this.job_post_bid_id = job_post_bid_id;
        }

        public void setBid_charges(String bid_charges) {
            this.bid_charges = bid_charges;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setDeadline_type(String deadline_type) {
            this.deadline_type = deadline_type;
        }

        public void setDeadline_value(String deadline_value) {
            this.deadline_value = deadline_value;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, EditBid.class);
        }
    }

    public static class DeleteBid {
        int job_post_bid_id;

        public void setJob_post_bid_id(int job_post_bid_id) {
            this.job_post_bid_id = job_post_bid_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, DeleteBid.class);
        }
    }

    public static class AcceptRejectJob {
        int job_post_bid_id, job_post_id;

        public void setJob_post_bid_id(int job_post_bid_id) {
            this.job_post_bid_id = job_post_bid_id;
        }

        public void setJob_post_id(int job_post_id) {
            this.job_post_id = job_post_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AcceptRejectJob.class);
        }
    }

    public static class BlockUser {
        int reported_user, other;
        String reason;

        public void setReported_user(int reported_user) {
            this.reported_user = reported_user;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public void setOther(int other) {
            this.other = other;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, BlockUser.class);
        }
    }

    public static class UnBlockUser {
        int reported_user;

        public void setReported_user(int reported_user) {
            this.reported_user = reported_user;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UnBlockUser.class);
        }
    }

    public static class AddAddress {
        String cityID, stateID, countryID;
//        int registration_step;

//        public void setRegistration_step(int registration_step) {
//            this.registration_step = registration_step;
//        }

        public void setCityID(String cityID) {
            this.cityID = cityID;
        }

        public void setStateID(String stateID) {
            this.stateID = stateID;
        }

        public void setCountryID(String countryID) {
            this.countryID = countryID;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddAddress.class);
        }
    }

    public static class SetCoordinates {
        double longitude, latitude;

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, SetCoordinates.class);
        }
    }

    public static class AddProAddress {
        String pro_address;

        public void setPro_address(String pro_address) {
            this.pro_address = pro_address;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddProAddress.class);
        }
    }

    public static class ClientProfile {
        long client_id;

        public void setClient_id(long client_id) {
            this.client_id = client_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, ClientProfile.class);
        }
    }

    public static class Logout {
        String device_token;

        public void setDevice_token(String device_token) {
            this.device_token = device_token;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, Logout.class);
        }
    }

    public static class DeleteUserAccount {
        String reason;

        public void setReason(String reason) {
            this.reason = reason;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, DeleteUserAccount.class);
        }
    }

    public static class SenFeedback {
        String note, device_type, app_version;

        public void setNote(String note) {
            this.note = note;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public void setApp_version(String app_version) {
            this.app_version = app_version;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, SenFeedback.class);
        }
    }


    public static class ProfilePublicityTypeId {
        String profilePublicityTypeID, status;

        public void setProfilePublicityTypeID(String profilePublicityTypeID) {
            this.profilePublicityTypeID = profilePublicityTypeID;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, ProfilePublicityTypeId.class);
        }
    }

    public static class AddAgencyInfo {
        String name, about, phone, email, website, address, note;
        int id, name_public, about_public, phone_public, email_public, website_public, address_public;

        public void setName(String name) {
            this.name = name;
        }

        public void setAbout(String about) {
            this.about = about;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName_public(int name_public) {
            this.name_public = name_public;
        }

        public void setAbout_public(int about_public) {
            this.about_public = about_public;
        }

        public void setPhone_public(int phone_public) {
            this.phone_public = phone_public;
        }

        public void setEmail_public(int email_public) {
            this.email_public = email_public;
        }

        public void setWebsite_public(int website_public) {
            this.website_public = website_public;
        }

        public void setAddress_public(int address_public) {
            this.address_public = address_public;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddAgencyInfo.class);
        }
    }

    public static class AddResume {
        int profile_id;

        public void setProfile_id(int profile_id) {
            this.profile_id = profile_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddResume.class);
        }
    }

    public static class AddJobReport {
        int job_post_id, other;
        String reason;

        public void setJob_post_id(int job_post_id) {
            this.job_post_id = job_post_id;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public void setOther(int other) {
            this.other = other;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddJobReport.class);
        }
    }

    public static class DeletePortfolioFile {
        int portfolio_id, file_id;

        public void setPortfolio_id(int portfolio_id) {
            this.portfolio_id = portfolio_id;
        }

        public void setFile_id(int file_id) {
            this.file_id = file_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, DeletePortfolioFile.class);
        }
    }

    public static class DeletePortfolio {
        int portfolio_id;

        public void setPortfolio_id(int portfolio_id) {
            this.portfolio_id = portfolio_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, DeletePortfolio.class);
        }
    }

    public static class DeleteStore {
        int id;

        public void setId(int portfolio_id) {
            this.id = portfolio_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, DeleteStore.class);
        }
    }

    public static class AddNotification {
        int notification_id, status;

        public void setNotification_id(int notification_id) {
            this.notification_id = notification_id;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddNotification.class);
        }
    }

    public static class NotificationStatus {
        String token;

        public void setToken(String token) {
            this.token = token;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, NotificationStatus.class);
        }
    }

    public static class SubUnbTopic {
        String topic, topic_status;

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public void setTopic_status(String topic_status) {
            this.topic_status = topic_status;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, SubUnbTopic.class);
        }
    }

    public static class Skills {
        int page_no;
        String search;

        public void setPage_no(int page_no) {
            this.page_no = page_no;
        }

        public void setSearch(String search) {
            this.search = search;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, Skills.class);
        }
    }

    public static class AddSkills {
        String skill_id, rating;
//        Integer public_status;

//        public void setPublic_status(int public_status) {
//            this.public_status = public_status;
//        }

        public void setSkill_id(String skill_id) {
            this.skill_id = skill_id;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddSkills.class);
        }
    }

    public static class AddCategory {
        List<Integer> category_ids, ratings;

        public void setCategory_ids(List<Integer> category_ids) {
            this.category_ids = category_ids;
        }

        public void setRatings(List<Integer> ratings) {
            this.ratings = ratings;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddCategory.class);
        }
    }

    public static class AddTags {
        List<Integer> tag_ids, ratings;

        public void setTag_ids(List<Integer> tag_ids) {
            this.tag_ids = tag_ids;
        }

        public void setRatings(List<Integer> ratings) {
            this.ratings = ratings;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddTags.class);
        }
    }

    public static class DeleteProfileVerification {
        int profile_verification_id;

        public void setProfile_verification_id(int profile_verification_id) {
            this.profile_verification_id = profile_verification_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, DeleteProfileVerification.class);
        }
    }

    public static class AddFile {
        String description, job_post_bid_id;

        public void setDescription(String description) {
            this.description = description;
        }

        public void setJob_post_bid_id(String job_post_bid_id) {
            this.job_post_bid_id = job_post_bid_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddFile.class);
        }
    }

    public static class AddSurvey {
        String social_media;

        public void setSocial_media(String social_media) {
            this.social_media = social_media;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddSurvey.class);
        }
    }

    public static class DeleteFiles {
        int file_attribute_id;

        public void setFile_attribute_id(int file_attribute_id) {
            this.file_attribute_id = file_attribute_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, DeleteFiles.class);
        }
    }

    public static class AgentReview {
        int page_no;

        public void setPage_no(int page_no) {
            this.page_no = page_no;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AgentReview.class);
        }
    }

    public static class UpdateProfile {
        String first_name, last_name, email, mobile_prefix, contactNo, username, birth_date, about_me, whatsapp_number, bussiness_email, settings_order;
        Integer gender, show_age, show_email, show_whatsapp, show_message_button, show_send_offer_button, price_range_public_status, gender_public_status, registration_step, is_verified, location_public;
        Double min_price, max_price;

        public void setLocation_public(Integer location_public) {
            this.location_public = location_public;
        }

        public void setIs_verify(Integer is_verify) {
            this.is_verified = is_verify;
        }

        public void setRegistration_step(Integer registration_step) {
            this.registration_step = registration_step;
        }

        public void setPrice_range_public_status(Integer price_range_public_status) {
            this.price_range_public_status = price_range_public_status;
        }

        public void setGender_public_status(Integer gender_public_status) {
            this.gender_public_status = gender_public_status;
        }

        public void setSettings_order(String settings_order) {
            this.settings_order = settings_order;
        }

        public void setShow_email(Integer show_email) {
            this.show_email = show_email;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setMobile_prefix(String mobile_prefix) {
            this.mobile_prefix = mobile_prefix;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setGender(Integer gender) {
            this.gender = gender;
        }

        public void setShowAge(Integer age) {
            this.show_age = age;
        }

        public void setContactNo(String contactNo) {
            this.contactNo = contactNo;
        }

        public void setBirthDate(String dob) {
            this.birth_date = dob;
        }

        public void setMinPrice(Double minPrice) {
            this.min_price = minPrice;
        }

        public void setMaxPrice(Double maxPrice) {
            this.max_price = maxPrice;
        }

        public void setAbout_me(String about_me) {
            this.about_me = about_me;
        }

        public void setWhatsapp_number(String whatsapp_number) {
            this.whatsapp_number = whatsapp_number;
        }

        public void setBussiness_email(String bussiness_email) {
            this.bussiness_email = bussiness_email;
        }

        public void setShow_whatsapp(Integer show_whatsapp) {
            this.show_whatsapp = show_whatsapp;
        }

        public void setShow_message_button(Integer show_message_button) {
            this.show_message_button = show_message_button;
        }

        public void setShow_send_offer_button(Integer show_send_offer_button) {
            this.show_send_offer_button = show_send_offer_button;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateProfile.class);
        }
    }

    public static class UpdateMawthooqStatus {
        Integer public_status, id;

        public void setPublic_status(Integer public_status) {
            this.public_status = public_status;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateMawthooqStatus.class);
        }
    }

    public static class UpdateProfileVerification {
        Integer is_verified;

        public void setIs_verified(Integer is_verified) {
            this.is_verified = is_verified;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateProfileVerification.class);
        }
    }

    public static class UpdateProfileName {
        String first_name, last_name;
        int registration_step;

        public void setRegistration_step(int registration_step) {
            this.registration_step = registration_step;
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
            return new GsonBuilder().create().toJson(this, UpdateProfileName.class);
        }
    }

    public static class SocialPlatform {
        String social_platform_url, platform_id, social_platform_followers;

        public void setSocial_platform_url(String social_platform_url) {
            this.social_platform_url = social_platform_url;
        }

        public void setPlatform_id(String platform_id) {
            this.platform_id = platform_id;
        }

        public void setSocial_platform_followers(String social_platform_followers) {
            this.social_platform_followers = social_platform_followers;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, SocialPlatform.class);
        }
    }

    public static class VerifyFb {
        String facebook_id;

        public void setFacebook_id(String facebook_id) {
            this.facebook_id = facebook_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, VerifyFb.class);
        }
    }

    public static class GoogleId {
        String google_id;

        public void setGoogle_id(String google_id) {
            this.google_id = google_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, GoogleId.class);
        }
    }

    public static class CancelWithdraw {
        int withdrawal_id;

        public void setWithdrawal_id(int withdrawal_id) {
            this.withdrawal_id = withdrawal_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, CancelWithdraw.class);
        }
    }

    public static class AddWithdraw {
        int payment_account_id, payment_platform_id;
        double amount;

        public void setPayment_account_id(int payment_account_id) {
            this.payment_account_id = payment_account_id;
        }

        public void setPayment_platform_id(int payment_platform_id) {
            this.payment_platform_id = payment_platform_id;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddWithdraw.class);
        }
    }

    public static class UpdateWorkbase {
        String workbase;

        public void setWorkbase(String workbase) {
            this.workbase = workbase;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateWorkbase.class);
        }
    }

    public static class UpdatePayRate {
        String pay_rate;

        public void setPay_rate(String pay_rate) {
            this.pay_rate = pay_rate;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdatePayRate.class);
        }
    }

    public static class ProfileVerification {
        String type, mawthooq_number;

        public void setType(String type) {
            this.type = type;
        }

        public void setMawthooq_number(String mawthooq_number) {
            this.mawthooq_number = mawthooq_number;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, ProfileVerification.class);
        }
    }

    public static class MawSubmit {
        String mawthooq, password;

        public void setMawthooq(String mawthooq) {
            this.mawthooq = mawthooq;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, MawSubmit.class);
        }
    }

    public static class MawOldSubmit {
        String password;
        MawOld mawthooq;

        public void setMawthooq(MawOld mawthooq) {
            this.mawthooq = mawthooq;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, MawOldSubmit.class);
        }
    }

    public static class MawOld {
        String old_number, new_number;

        public void setOld_number(String old_number) {
            this.old_number = old_number;
        }

        public void setNew_number(String new_number) {
            this.new_number = new_number;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, MawOld.class);
        }
    }

    public static class EmailOtp {
        String email, otp, phone;

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, EmailOtp.class);
        }
    }

    public static class PhoneOtp {
        String otp, phone;

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, PhoneOtp.class);
        }
    }

    public static class AddExperience {
        String service_category_id, level, company_name, start_date, end_date, is_current, experience_id;

        public void setService_category_id(String service_category_id) {
            this.service_category_id = service_category_id;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public void setStart_date(String start_date) {
            this.start_date = start_date;
        }

        public void setIs_current(String is_current) {
            this.is_current = is_current;
        }

        public void setExperience_id(String experience_id) {
            this.experience_id = experience_id;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddExperience.class);
        }
    }

    public static class JobDetail {
        Integer job_post_id;

        public void setJob_post_id(Integer job_post_id) {
            this.job_post_id = job_post_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, JobDetail.class);
        }
    }

    public static class SendEmailVerif {
        Integer payment_account_id;

        public void setPayment_account_id(Integer payment_account_id) {
            this.payment_account_id = payment_account_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, SendEmailVerif.class);
        }
    }

    public static class PaypalVerification {
        String nonce, payment_type_id;

        public void setPayment_type_id(String payment_type_id) {
            this.payment_type_id = payment_type_id;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, PaypalVerification.class);
        }
    }

    public static class AddPayment {
        String beneficiary_name, iban;
        Integer bank_id, id, is_primary;

        public void setBeneficiary_name(String beneficiary_name) {
            this.beneficiary_name = beneficiary_name;
        }

        public void setIban(String iban) {
            this.iban = iban;
        }

        public void setBank_id(int bank_id) {
            this.bank_id = bank_id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public void setIs_primary(Integer is_primary) {
            this.is_primary = is_primary;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddPayment.class);
        }
    }

    public static class DeleteBank {
        Integer id;

        public void setId(Integer id) {
            this.id = id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, DeleteBank.class);
        }
    }

    public static class SendFileMail {
        int file_attribute_id;

        public void setFile_attribute_id(int file_attribute_id) {
            this.file_attribute_id = file_attribute_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, SendFileMail.class);
        }
    }

    public static class ClientReview {
        int page_no, client_id;

        public void setPage_no(int page_no) {
            this.page_no = page_no;
        }

        public void setClient_id(int client_id) {
            this.client_id = client_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, ClientReview.class);
        }
    }

    public static class SendEmail {
        String email;

        public void setEmail(String email) {
            this.email = email;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, SendEmail.class);
        }
    }

    public static class GetState {
        int countryID;

        public void setCountryID(int countryID) {
            this.countryID = countryID;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, GetState.class);
        }
    }

    public static class GetCity {
        int stateID;

        public void setStateID(int stateID) {
            this.stateID = stateID;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, GetCity.class);
        }
    }

    public static class AddProfileLanguage {
        String language_id, level;

        public void setLanguage_id(String language_id) {
            this.language_id = language_id;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddProfileLanguage.class);
        }
    }

    public static class ExpertiseRequest {
        String experience, service_category_id;
        Integer public_status;

        public void setCategory_public_status(Integer category_public_status) {
            this.public_status = category_public_status;
        }

        public void setExperience(String experience) {
            this.experience = experience;
        }

        public void setService_category_id(String service_category_id) {
            this.service_category_id = service_category_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, ExpertiseRequest.class);
        }
    }

    public static class JobPostRequest {
        String job_post_type, search_by;
        int service_category_id, page_no;

        public void setJob_post_type(String job_post_type) {
            this.job_post_type = job_post_type;
        }

        public void setService_category_id(int service_category_id) {
            this.service_category_id = service_category_id;
        }

        public void setPage_no(int page_no) {
            this.page_no = page_no;
        }

        public void setSearch_by(String search_by) {
            this.search_by = search_by;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, JobPostRequest.class);
        }
    }

    public static class PayTypesRequest {
        int pay_type_id, status;

        public void setPay_type_id(int pay_type_id) {
            this.pay_type_id = pay_type_id;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, PayTypesRequest.class);
        }
    }

    public static class AddSurveyForm {
        String survey_answers;

        public void setSurvey_answers(String survey_answers) {
            this.survey_answers = survey_answers;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddSurveyForm.class);
        }
    }

    public static class DeleteSurveyImage {
        String social_survey_id;

        public void setSocial_survey_id(String social_survey_id) {
            this.social_survey_id = social_survey_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, DeleteSurveyImage.class);
        }
    }

    public static class AddSocialMedia {
        String username;
        int social_platform_type_id, social_platform_id, followers, is_public;

        public void setFollowers(int followers) {
            this.followers = followers;
        }

        public void setIs_public(int is_public) {
            this.is_public = is_public;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setSocial_platform_type_id(int social_platform_type_id) {
            this.social_platform_type_id = social_platform_type_id;
        }

        public void setSocial_platform_id(int social_platform_id) {
            this.social_platform_id = social_platform_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddSocialMedia.class);
        }
    }

    public static class EditSocialMedia {
        String username;
        int social_platform_id;

        public void setUsername(String username) {
            this.username = username;
        }

        public void setSocial_platform_id(int social_platform_id) {
            this.social_platform_id = social_platform_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, EditSocialMedia.class);
        }
    }

    public static class DeleteSocialMedia {
        int social_platform_id;

        public void setSocial_platform_id(int social_platform_id) {
            this.social_platform_id = social_platform_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, DeleteSocialMedia.class);
        }
    }

    public static class ReOrderMedia {
        //        JSONObject jsonObject;
        String reorder;

//        public void setJsonObject(JSONObject jsonObject) {
//            this.jsonObject = jsonObject;
//        }

        public void setReorder(String reorder) {
            this.reorder = reorder;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, ReOrderMedia.class);
        }
    }

    public static class AddWebsiteRequest {
        String link, platform_name;

        public void setPlatoform_name(String platoform_name) {
            this.platform_name = platoform_name;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddWebsiteRequest.class);
        }
    }

    public static class AddCompany {
        Integer company_id, times, times_public_status, public_status, campaign_date_public_status, contract_public_status;
        String campaign_date, contract_start_date, contract_end_date;
        String company_name, company_link;

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public void setCompany_link(String company_link) {
            this.company_link = company_link;
        }

        public void setCompany_id(Integer company_id) {
            this.company_id = company_id;
        }

        public void setTimes(Integer times) {
            this.times = times;
        }

        public void setTimes_public_status(Integer times_public_status) {
            this.times_public_status = times_public_status;
        }

        public void setPublic_status(Integer public_status) {
            this.public_status = public_status;
        }

        public void setCampaign_date_public_status(Integer campaign_date_public_status) {
            this.campaign_date_public_status = campaign_date_public_status;
        }

        public void setCampaign_date(String campaign_date) {
            this.campaign_date = campaign_date;
        }

        public void setContract_start_date(String contract_start_date) {
            this.contract_start_date = contract_start_date;
        }

        public void setContract_end_date(String contract_end_date) {
            this.contract_end_date = contract_end_date;
        }

        public void setContract_public_status(Integer contract_public_status) {
            this.contract_public_status = contract_public_status;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddCompany.class);
        }
    }

    public static class AddPartners {
        int company_id;
        String link, code;
        String company_name, company_link;

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public void setCompany_link(String company_link) {
            this.company_link = company_link;
        }

        public void setCompany_id(int company_id) {
            this.company_id = company_id;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddPartners.class);
        }
    }

    public static class UpdateCompany {
        Integer company_id, times, status, times_public_status, public_status, campaign_date_public_status, contract_public_status;
        int id;
        String campaign_date, contract_start_date, contract_end_date;
        String company_name, company_link;
//        1 - public
//2 - brand
//3- only me

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public void setCompany_link(String company_link) {
            this.company_link = company_link;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setPublic_status(Integer public_status) {
            this.public_status = public_status;
        }

        public void setCompany_id(Integer company_id) {
            this.company_id = company_id;
        }

        public void setTimes(Integer times) {
            this.times = times;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public void setTimes_public_status(Integer times_public_status) {
            this.times_public_status = times_public_status;
        }

        public void setCampaign_date_public_status(Integer campaign_date_public_status) {
            this.campaign_date_public_status = campaign_date_public_status;
        }

        public void setContract_public_status(Integer contract_public_status) {
            this.contract_public_status = contract_public_status;
        }

        public void setCampaign_date(String campaign_date) {
            this.campaign_date = campaign_date;
        }

        public void setContract_start_date(String contract_start_date) {
            this.contract_start_date = contract_start_date;
        }

        public void setContract_end_date(String contract_end_date) {
            this.contract_end_date = contract_end_date;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateCompany.class);
        }
    }

    public static class UpdatePartner {
        int company_id, status; /*1 or 2 = 1 = active, 2 = hide*/
        int public_status, id;
        String link, code;
        String company_name, company_link;
//        1 - public
//2 - brand
//3- only me


        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public void setCompany_link(String company_link) {
            this.company_link = company_link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setPublic_status(int public_status) {
            this.public_status = public_status;
        }

        public void setCompany_id(int company_id) {
            this.company_id = company_id;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdatePartner.class);
        }
    }

    public static class DeleteCompany {
        int id;

        public void setId(int id) {
            this.id = id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, DeleteCompany.class);
        }
    }

    public static class AddNewPortfolio {
        Integer company_id, public_status;
        String company_name, company_link;

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public void setCompany_link(String company_link) {
            this.company_link = company_link;
        }

        public void setPublic_status(Integer public_status) {
            this.public_status = public_status;
        }

        public void setCompany_id(Integer company_id) {
            this.company_id = company_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddNewPortfolio.class);
        }
    }

    public static class AddNewCompany {
        String company_name, company_link;

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }

        public void setCompany_link(String company_link) {
            this.company_link = company_link;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddNewCompany.class);
        }
    }

    public static class AddStores {
        String title, link;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddStores.class);
        }
    }

    public static class AddProduct {
        String title, url, price;
        int currency;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public void setCurrency(int currency) {
            this.currency = currency;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddProduct.class);
        }
    }

    public static class UpdateProduct {
        String title, url, price;
        int currency, id;
        Integer public_status;

        public void setPublic_status(Integer public_status) {
            this.public_status = public_status;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public void setCurrency(int currency) {
            this.currency = currency;
        }

        public void setId(int id) {
            this.id = id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateProduct.class);
        }
    }

    public static class UpdateNewPortfolio {
        int company_id, portfolio_id, status, public_status; /*1 or 2 = 1 = active, 2 = hide*/

        public void setPublic_status(int public_status) {
            this.public_status = public_status;
        }

        public void setCompany_id(int company_id) {
            this.company_id = company_id;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setPortfolio_id(int portfolio_id) {
            this.portfolio_id = portfolio_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateNewPortfolio.class);
        }
    }

    public static class UpdateStores {
        int id, status, public_status; /*1 or 2 = 1 = active, 2 = hide*/
        String title, link;

        public void setPublic_status(int public_status) {
            this.public_status = public_status;
        }


        public void setStatus(int status) {
            this.status = status;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateStores.class);
        }
    }

    public static class UpdateSocialMedia {
        String username;
        int social_platform_id, followers, is_public, public_status;

        public void setPublic_status(int public_status) {
            this.public_status = public_status;
        }

        public void setFollowers(int followers) {
            this.followers = followers;
        }

        public void setIs_public(int is_public) {
            this.is_public = is_public;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setSocial_platform_id(int social_platform_id) {
            this.social_platform_id = social_platform_id;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateSocialMedia.class);
        }
    }

    public static class AddYoutube {
        String link;

        public void setLink(String link) {
            this.link = link;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, AddYoutube.class);
        }
    }

    public static class UpdateYoutube {
        Integer public_status, id;
        String link;

        //        1 - public
        public void setId(Integer id) {
            this.id = id;
        }

        public void setPublic_status(Integer public_status) {
            this.public_status = public_status;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateYoutube.class);
        }
    }
}

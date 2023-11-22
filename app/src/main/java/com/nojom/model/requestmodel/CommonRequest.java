package com.nojom.model.requestmodel;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;

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

    public static class UpdateWebsite {
        String website;

        public void setWebsite(String website) {
            this.website = website;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateWebsite.class);
        }
    }

    public static class UpdateSummary {
        String content;

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
        Integer cityID, stateID, countryID;

        public void setCityID(Integer cityID) {
            this.cityID = cityID;
        }

        public void setStateID(Integer stateID) {
            this.stateID = stateID;
        }

        public void setCountryID(Integer countryID) {
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
        int id,name_public,about_public,phone_public,email_public,website_public,address_public,note_public;

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

        public void setNote_public(int note_public) {
            this.note_public = note_public;
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
        String first_name, last_name, email, mobile_prefix, contactNo, username;

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

        public void setContactNo(String contactNo) {
            this.contactNo = contactNo;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, UpdateProfile.class);
        }
    }

    public static class UpdateProfileName {
        String first_name, last_name;

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
        String social_platform_url,platform_id,social_platform_followers;

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
        String type;

        public void setType(String type) {
            this.type = type;
        }

        @NonNull
        @Override
        public String toString() {
            return new GsonBuilder().create().toJson(this, ProfileVerification.class);
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
}

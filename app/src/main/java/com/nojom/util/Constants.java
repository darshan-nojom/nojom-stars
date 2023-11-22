package com.nojom.util;


public interface Constants {

    //CHAT LIVE URL
//    String BASE_URL_CHAT = "https://geaxdtp8z0.execute-api.us-east-2.amazonaws.com/prod/";
//    String BASE_URL_WEBSOCKET = "wss://4zpnqwqsn5.execute-api.us-east-2.amazonaws.com/prod/";

    String BASE_URL_CHAT_MSG = "http://3.85.154.200:4000/";
    String BASE_URL_CHAT = "http://3.85.154.200:4000/api/";

//    String BASE_URL_CHAT = "https://rv0h39xor3.execute-api.us-east-2.amazonaws.com/dev/";
//    String BASE_URL_WEBSOCKET = "wss://qcjaqghd7k.execute-api.us-east-2.amazonaws.com/dev/";

    String TERMS_USE = "https://24task.com/terms-of-use";
    String FAQS = "https://24task.com/faqs";
    String PRIVACY_POLICY = "https://24task.com/privacy-policy";
    String CAREERS = "https://careers.24task.com/";

    String FACEBOOK_URL = "https://www.facebook.com/pg/24Task/reviews/?ref=page_internal";
    String GOOGLE_URL = "https://g.page/24Task/review?gm";
    String TRUSTPILOT_URL = "https://www.trustpilot.com/evaluate/24task.com";
    String JABBER_URL = "https://www.sitejabber.com/online-business-review#24task.com_(24+Task+-)";
    String APPSTORE_URL = "https://apps.apple.com/app/id1397804027";
    String PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=com.nojom.client";

    int AGENT_PROFILE = 1;
    int CLIENT_PROFILE = 2;

    int TAB_HOME = 0;
    int TAB_CHAT = 1;
    int TAB_GIG = 2;
    int TAB_JOB_LIST = 3;
    int TAB_PROFILE = 4;

    int HEADLINE = 1;
    int ADDRESS = 2;
    int PHONE = 3;
    int EMAIL = 4;
    int WEBSITE = 5;
    int OFFICE_ADD = 6;

    String SFTEXT_REGULAR = "font/SanFranciscoText-Regular.otf";
    String SFTEXT_BOLD = "font/SanFranciscoText-Bold.otf";
    String SFTEXT_MEDIUM = "font/SanFranciscoText-Medium.otf";
    String SFTEXT_SEMIBOLD = "font/SanFranciscoText-Semibold.otf";
    //    String SFDISPLAY_BOLD = "font/SF-Pro-Display-Bold.otf";
    String SFDISPLAY_REGULAR = "font/SF-Pro-Display-Regular.otf";

    String KEY_FOR_MAINTENANCE = "Agent24Task_Maintenance_Android";
    String KEY_LIVE_BASE_URL = "agent_live_base_url";
    String KEY_LIVE_BASE_URL_GIG = "agent_live_base_url_gig";

    String REGISTRATION_COMPLETE = "registrationComplete";
    String FCM_TOKEN = "fcm_token";
    String JWT = "JWT";
    String FROM_LOGIN = "from login";
    String LOGIN_FINISH = "login_finish";
    String USER_DATA = "user_data";
    String CLIENT_PROFILE_DATA = "clientprofiledata";
    String SUBMIT_FILE_DONE = "submit_file";
    String PROFILE_DATA = "profile_data";
    String IS_LOGIN = "isLogin";
    String SCREEN_NAME = "screen_name";
    String SCREEN_TAB = "screen_tab";
    String IS_EDIT = "isEdit";
    String SERVICE_IDS = "Service ids";
    String SERVICE_ID = "ServiceId";
    String FILTER_ID = "filter_id";
    String ATTACH_FILE = "AttachFile";
    String PROPOSAL = "Proposal";
    String DESCRIBE_BID = "describe_bid";
    String ANDROID = "1";
    String PAYPAL_URL = "Paypal Url";
    String CLIENT_BANNER = "client_banner";

    String PREF_SERVICES = "services";
    String PREF_TOP_SERVICES = "topServices";
    String SERVICE_NAME = "service_name";
    String IS_REFRESH_JOB = "isRefreshJob";

    String WORK_IN_PROGRESS = "Work In Progress";
    String PAST_PROJECTS = "Past Projects";
    String GIG_IN_PROGRESS = "WORK_IN_PROGRESS";
    String GIG_PAST_PROJECT = "PAST_PROJECT";
    String IS_WORK_INPROGRESS = "isWorkInProgress";
    String AVAILABLE_JOBS = "Available Jobs";
    String IS_BIDDING = "isBidding";
    String IS_OFFER = "isOffer";

    String IS_SOCIAL_LOGIN = "is_social_login";

    String OFFER = "Offer";

    int SYS_ID = 4;
    int IN_PROGRESS = 2;
    int WAITING_FOR_DEPOSIT = 1;
    int WAITING_FOR_ACCEPTANCE = 6;
    int BIDDING = 3;
    int SUBMIT_WAITING_FOR_PAYMENT = 8;
    int COMPLETED = 4;
    int CANCELLED = 5;
    int REFUNDED = 9;
    int PLATFORM = 1;

    String M_TYPE = "m_type";
    String M_PROJECTID = "project_id";
    String PROJECT_ID = "projectId";
    String PROJECT = "project";
    String GIG_DETAILS = "gigdetails";
    String PROJECT_DATA = "projectData";
    String ACCOUNT_DATA = "accountData";
    String EDIT_BID_ID = "editBidId";
    String PROJECT_BID_ID = "projectBidId";
    String SKILLS = "skills";
    String SKILL_COUNT = "skill_count";
    String SELECTED_SKILL = "selected_skill";
    String COUNTRY_CODE = "country_code";

    int BALANCE_WITHDRAW = 1;
    int BALANCE_ACCOUNT = 2;

    String TAB_BALANCE = "balance_tab";
    String AVAILABLE_BALANCE = "available_balance";
    String WITHDRAW_AMOUNT = "withdraw_amount";
    String ACCOUNT_ID = "account_id";
    String ACCOUNTS = "accounts";

    //Firebase Chat
    String CHAT_ID = "ChatID";
    String CHAT_DATA = "ChatData";
    String TEXT = "text";
    String IMAGE = "image";
    String VIDEO = "video";
    String DOC = "document";
    String SENDER_ID = "sender_id";
    String SENDER_NAME = "sender_name";
    String SENDER_PIC = "sender_pic";
    String RECEIVER_ID = "receiver_id";
    String RECEIVER_NAME = "receiver_name";
    String RECEIVER_PIC = "receiver_pic";
    String CHAT_OPEN_ID = "chatOpenScreenId";

    String ALL_NOTIFICATION = "allNotification";

    // 0 - Basic
    // 1 - Conversational
    // 2 - Fluent
    // 3 - Native
    int BASIC_ID = 0;
    int CONVERSATIONAL_ID = 1;
    int FLUENT_ID = 2;
    int NATIVE_ID = 3;

    String BASIC = "Basic";
    String CONVERSATIONAL = "Conversational";
    String FLUENT = "Fluent";
    String NATIVE = "Native";

    int LESS_THAN_1_ID = 0;
    int YEAR_1_3_ID = 1;
    int YEAR_4_6_ID = 2;
    int YEAR_7_9_ID = 3;
    int YEAR_10_12_ID = 4;
    int YEAR_13_15_ID = 5;
    int YEAR_16_18_ID = 6;
    int YEAR_19_21_ID = 7;
    int YEAR_22_24_ID = 8;
    int YEAR_25_27_ID = 9;
    int YEAR_28_30_ID = 10;
    int YEAR_31_ID = 11;

    String LESS_THAN_1 = "Less than 1 year";
    String YEAR_1_3 = "1–3 years";
    String YEAR_4_6 = "4–6 years";
    String YEAR_7_9 = "7–9 years";
    String YEAR_10_12 = "10–12 years";
    String YEAR_13_15 = "13–15 years";
    String YEAR_16_18 = "16–18 years";
    String YEAR_19_21 = "19–21 years";
    String YEAR_22_24 = "22–24 years";
    String YEAR_25_27 = "25–27 years";
    String YEAR_28_30 = "28–30 years";
    String YEAR_31_ = "31+ years";

    String SELECT_FOLLOWER = "Select Followers";
    String FOLLOWER_1 = "1k - 10k";
    String FOLLOWER_2 = "10k - 50k";
    String FOLLOWER_3 = "50k - 100k";
    String FOLLOWER_4 = "100k - 500k";
    String FOLLOWER_5 = "500k - 1M";
    String FOLLOWER_6 = "1M - 10M";
    String FOLLOWER_7 = "10M+";

    //0 - associate, 1 - bachelor, 2 - master, 3 - doctorate
    int Associate_ID = 0;
    int Bachelor_ID = 1;
    int Master_ID = 2;
    int Doctorate_ID = 3;

    String Associate = "Associate";
    String Bachelor = "Bachelor";
    String Master = "Master";
    String Doctorate = "Doctorate";

    //0 - Beginner 1 - Intermediate 2 - Expert
    int Beginner_ID = 0;
    int Intermediate_ID = 1;
    int Expert_ID = 2;

    int GIG_TITLE = 10;
    int GIG_DESC = 11;
    int GIG_PACKAGE_NAME = 12;
    int GIG_PACKAGE_DESC = 13;
    int GIG_PRICE = 14;
    int GIG_REVISION = 15;
    int GIG_REQUIREMENT_DESC = 17;
    int GIG_DEAD_REQUIREMENT_DESC = 18;

    String Beginner = "Beginner";
    String Intermediate = "Intermediate";
    String Expert = "Expert";

    String CLIENT_ATTACHMENT = "CLIENT_ATTACHMENT";
    String GIG_ATTACHMENT = "GIG_ATTACHMENT";
    String RESUME_TAG = "RESUME";
    String SKILL_ID = "skillId";
    String PREF_PARTNER_APP = "partner_app";
    String PREF_PARTNER_ABOUT = "partner_about";

    String API_LOGIN = "login";
    //        String API_LOGIN = "dev_login";
    String API_SIGNUP = "signup";
    String API_GET_JOBPOST = "get_job_posts";
    String API_SERVICES_CATEGORY = "get_service_categories";
    String API_PAY_TYPES = "get_pay_types";
    String API_ADD_EXPERTISE = "add_expertises";
    String API_LANGUAGE = "get_language";
    String API_ADD_LANGUAGE = "add_profile_language";
    String API_UPDATE_PAYTYPE = "update_pay_types";
    String API_GET_PROFILE = "get_profile_info";
    //Balance module
    String API_GET_INCOME = "get_income";
    String API_GET_WITHDRAWAL = "get_withdrawal";
    String API_GET_PAYMENT_ACCOUNTS = "get_payment_account";//Done

    String API_GET_NOTIFICATION_SETTINGS = "get_notification_setting";
    String API_ADD_PAYMENT_ACCOUNT = "add_payment_account";
    //Payment account
    String API_EDIT_PAYMENT_ACCOUNT = "edit_payment_account";
    String API_DELETE_PAYMENT_ACCOUNT = "delete_payment_account";
    String API_VERIFY_PAYMENT_ACCOUNT = "verify_payment_account";
    //Password
    String API_UPDATE_PASSWORD = "update_password";
    String API_FORGET_PASSWORD = "forgot_password";
    String API_RESET_PASSWORD = "reset_password";

    String API_EMAIL_VERIFICATION = "send_email_verification";
    //Professional Info
    String API_ADD_WEBSITE = "add_website";
    String API_ADD_SUMMARY = "add_summary";
    String API_ADD_HEADLINE = "add_headline";
    String API_ADD_EDUCATIONS = "add_education";
    String API_ADD_CLIENT_REVIEW = "add_client_review";
    //Portfolio
    String API_ADD_PORTFOLIO = "add_portfolio";
    String API_EDIT_PORTFOLIO = "edit_portfolio";
    String API_GET_PORTFOLIO = "get_portfolio";//Done
    String API_DELETE_PORTFOLIO = "delete_portfolio";
    String API_DELETE_PORTFOLIO_FILE = "delete_portfolio_file";
    //05-11-2020
    String API_ADD_BID = "add_bid";
    String API_EDIT_BID = "edit_bid";
    //11-11-2020
    String API_DELETE_BID = "delete_bid";
    String API_ACCEPT_JOB = "accept_job";
    String API_REJECT_JOB = "reject_job";
    String API_BLOCK_USER = "block_user";
    String API_UNBLOCK_USER = "unblock_user";
    String API_REVIEW_QUESTIONS = "get_review_questions";
    String API_ADD_ADDRESS = "add_addresses";
    String API_SET_COORDINATE = "set_coordinates";
    String API_ADD_PRO_ADDRESS = "add_pro_addresses";
    String API_VIEW_CLIENT_PROFILE = "view_client_profile";//TODO:
    String API_LOGOUT = "logout";
    String API_ADD_FEEDBACK = "add_feedback";
    String API_PROFILE_PUBLICITY = "add_profile_publicity";
    //12-11-2020AP
    String API_ADD_RESUME = "add_resume";
    String API_ADD_JOB_REPORT = "add_job_report";
    //19-11-2020
    String API_ADD_NOTIFICATION_SETTINGS = "add_notification_setting";
    String API_TOPIC_STATUS = "get_topic_status";
    String API_SUBSCRIBE_TOPIC = "subscribe_topic";
    String API_VIEW_SKILL = "view_skills";//Done
    String API_ADD_SKILL = "add_skills";
    String API_PROFILE_VERIFICATION = "view_profile_verification";
    String API_DELETE_PROFILE_VERIFICATION = "delete_profile_verification";
    String API_ADD_FILES = "add_files";
    String API_DELETE_FILES = "delete_files";
    //20-11-2020
    String API_AGENT_REVIEW = "get_agent_review";
    String API_UPDATE_PROFILE = "update_profile";//TODO:
    //21-11-2020
    String API_VERIFY_FACEBOOK = "verify_facebook";
    String API_CANCEL_WITHDRAWALS = "cancel_withdrawal_request";
    String API_ADD_WITHDRAWALS = "add_withdrawal_request";
    //23-11-2020
    String API_ADD_PROFILE_VERIF = "add_profile_verification";
    //24-11-2020
    String API_ADD_EXPERIENCE = "add_experiences";
    String API_JOB_DETAIL = "get_job_detail";//Done
    //26-11-2020
    String API_GENERATE_BRAINTREE_TOKEN = "generate_braintree_token";
    //27-11-2020
    String API_VERIFY_PAYPAL_PAYMENT = "verify_paypal_payment";
    //30-11-2020
    String API_SEND_FILE_MAIL = "send_file_mail";
    //04-11-2020
    String API_CLIENT_REVIEW = "get_client_review";
    //23-11-2020
    String API_GET_COUNTRIES = "get_countries";//Done
    String API_GET_STATE = "get_states";
    String API_GET_CITIES = "get_cities";

    String API_GET_GIG_PACKAGE = "gigs/getGigPackages";//Done
    String API_GET_GIG_SERVICE_CAT = "category/getServiceCategories";//Done
    String API_GET_GIG_STAND_SERVICE_CAT = "category/getStandardGigServiceCategories";//Done
    String API_GET_GIG_CUSTOM_SERVICE_CAT = "category/getGigServiceCategories";
    String API_GET_GIG_SERVICE_BY_CAT = "category/getServicesByCategoryId/";//TODO:
    String API_GET_INF_PLATFORM = "category/getSocialPlatforms";
    String API_GET_GIG_LANGUAGE = "language/getLanguage";//TODO:
    String API_GET_GIG_REQUIREMENTS = "gigs/getRequirements/";
    String API_GET_GIG_DELIVERY_TIME = "gigs/getDeliveryTimes";
    String API_CREATE_GIG = "agent/createGigs";
    String API_EDIT_GIG = "agent/updateGigs";
    String API_GET_GIG_LIST = "agent/getGigLists";
    String API_GIG_DETAILS = "agent/getGigDetails/";//Done
    String API_DELETE_GIG = "agent/deleteGig";//TODO:
    String API_ACTIVE_INACTIVE_GIG = "agent/activeInactiveGig";//TODO:
    String API_GIG_CAT_CHARGES = "gigs/getCategoryCharges";//TODO:
    String API_GET_CONTRACT_LIST = "agent/getContractsList";
    String API_CANCEL_CONTRACT = "agent/cancelContracts";
    String API_CONTRACT_DETAIL = "agent/getContractDetails/";//TODO:
    String API_CUSTOM_CONTRACT_DETAIL = "agent/getCustomContractDetails/";//TODO:
    String API_ACCEPT_CONTRACT = "agent/acceptContract";
    String API_SUBMIT_CONTRACT_JOB = "agent/submitJob";
    String API_DELETE_CONTRACT_JOB = "agent/deleteSubmitJobFiles";
    String API_EMAIL_CONTRACT_JOB = "agent/sendSubmittedFilesMail";
    String API_GIG_ADD_CLIENT_REVIEW = "agent/addFeedback";
    String API_GIG_REVIEW_QUESTION = "gigs/getFeedbackLists";
    String API_DELETE_GIG_PHOTOS = "agent/removeGigsImage";
    String API_IMAGE_PRIMARY = "agent/makeImagePrimary";
    String API_GET_REQUIREMENTS_CATEGORY = "agent/getRequirmentsByCategory";
    String API_CREATE_CUSTOM_GIG = "agent/createCustomGigs";
    String API_UPDATE_CUSTOM_GIG = "agent/updateCustomGig";
    String API_CUSTOM_GIG_DETAILS = "agent/getCustomGigDetails/";//Done

    String API_CHAT_FILE_UPLOAD = BASE_URL_CHAT + "chats/chatUploadFiles";


    String API_GET_PARTNER_QUESTION = "get_partner_app_questions";//Done
    String API_ADD_PARTNER_ANSWER = "add_partner_app_answer";
    String API_ADD_PARTNER_ABOUT_ANSWER = "add_partner_about_answer";

    String API_SOCIAL_SURVEY = "getSocialSurvey";//Done
    String API_ADD_SOCIAL_SURVEY = "addAgentSurveys";
    String API_ADD_S_SURVEY = "addSocialSurveys";
    String API_DELETE_SURVEY_IMG = "deleteSurveysImg";
    String API_SURVEY_DETAIL = "getSocialSurveyDetail";
    String API_GET_PROMOCODE_HISTORY = "getPromocodeHistory";

    String API_DELETE_ACCOUNT = "deleteProfile";
    String API_CREATE_OFFER = "agent/createOffer";
    String API_WITHDRAW_OFFER = "agent/withdrawOffer";

    String PREF_SELECTED_LANGUAGE = "selectedLanguage";
    String PREF_SELECTED_CURRENCY = "selectedCurrency";

    String ADD_AGENCY = "add_profile_agencies";

    String API_GET_SOCIAL_PLATFORMS = "get_profile_social_platforms";
    String API_ADD_SOCIAL_PLATFORMS = "add_profile_social_platforms";
}

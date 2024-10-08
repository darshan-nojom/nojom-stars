package com.nojom.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nojom.model.CreateOfferResponse;
import com.nojom.model.GigCatCharges;
import com.nojom.model.GigCategoryModel;
import com.nojom.model.GigPackages;
import com.nojom.model.GigSubCategoryModel;
import com.nojom.model.Language;
import com.nojom.model.ProfileResponse;
import com.nojom.model.ServicesModel;
import com.nojom.model.UserModel;
import com.nojom.ui.BaseActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Preferences {

    public static final String PREF_NAME = "24task";

    public static final int MODE = MODE_PRIVATE;

    public static void writeBoolean(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).commit();
    }

    public static boolean readBoolean(Context context, String key, boolean defValue) {
        return getPreferences(context).getBoolean(key, defValue);
    }

    public static void writeInteger(Context context, String key, int value) {
        getEditor(context).putInt(key, value).commit();

    }

    public static int readInteger(Context context, String key, int defValue) {
        return getPreferences(context).getInt(key, defValue);
    }

    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();

    }

    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    public static void writeFloat(Context context, String key, float value) {
        getEditor(context).putFloat(key, value).commit();
    }

    public static float readFloat(Context context, String key, float defValue) {
        return getPreferences(context).getFloat(key, defValue);
    }

    public static void writeLong(Context context, String key, long value) {
        getEditor(context).putLong(key, value).commit();
    }

    public static long readLong(Context context, String key, long defValue) {
        return getPreferences(context).getLong(key, defValue);
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static void clearPreferences(Context context) {
        getEditor(context).clear().apply();
    }

    public static void saveServices(Context context, List<ServicesModel.Data> services) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_SERVICES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("myJson", new Gson().toJson(services));
        prefsEditor.apply();
    }

    public static void saveGigPackages(Context context, List<GigPackages.Data> services) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_SERVICES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("gigpackage", new Gson().toJson(services));
        prefsEditor.apply();
    }

    public static ArrayList<GigPackages.Data> getGigPackages(Context context) {
        ArrayList<GigPackages.Data> services;
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_SERVICES, MODE_PRIVATE);
        String json = mPrefs.getString("gigpackage", "");
        if (json.isEmpty()) {
            services = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<GigPackages.Data>>() {
            }.getType();
            services = new Gson().fromJson(json, type);
        }
        return services;
    }

    public static void saveStndrdGigCategory(Context context, List<GigCategoryModel.Data> services) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_SERVICES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("gigstncategory", new Gson().toJson(services));
        prefsEditor.apply();
    }

    public static ArrayList<GigCategoryModel.Data> getStndrdGigCategory(Context context) {
        ArrayList<GigCategoryModel.Data> services;
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_SERVICES, MODE_PRIVATE);
        String json = mPrefs.getString("gigstncategory", "");
        if (json.isEmpty()) {
            services = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<GigCategoryModel.Data>>() {
            }.getType();
            services = new Gson().fromJson(json, type);
        }
        return services;
    }

    public static void saveGigLanguage(Context context, List<Language.Data> services) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_SERVICES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("giglang", new Gson().toJson(services));
        prefsEditor.apply();
    }

    public static ArrayList<Language.Data> getGigLanguage(Context context) {
        ArrayList<Language.Data> services;
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_SERVICES, MODE_PRIVATE);
        String json = mPrefs.getString("giglang", "");
        if (json.isEmpty()) {
            services = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<Language.Data>>() {
            }.getType();
            services = new Gson().fromJson(json, type);
        }
        return services;
    }

//    public static void saveGigLanguage(Context context, List<Language.Data> languages) {
//        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_SERVICES, Context.MODE_PRIVATE);
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        prefsEditor.putString("gigLang", new Gson().toJson(languages));
//        prefsEditor.apply();
//    }
//
//    public static ArrayList<Language.Data> getGigLanguage(Context context) {
//        ArrayList<Language.Data> languages = new ArrayList<>();
//        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_SERVICES, Context.MODE_PRIVATE);
//        String json = mPrefs.getString("gigLang", "");
//        if (json != null) {
//            if (json.isEmpty()) {
//                languages = new ArrayList<>();
//            } else {
//                Type type = new TypeToken<List<Language.Data>>() {
//                }.getType();
//                languages = new Gson().fromJson(json, type);
//            }
//        }
//        return languages;
//    }

    public static List<ServicesModel.Data> getServices(Context context) {
        List<ServicesModel.Data> services;
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_SERVICES, MODE_PRIVATE);
        String json = mPrefs.getString("myJson", "");
        if (json.isEmpty()) {
            services = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<ServicesModel.Data>>() {
            }.getType();
            services = new Gson().fromJson(json, type);
        }
        return services;
    }

    public static void saveCategoryCharges(Context context, List<GigCatCharges.Data> services) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_SERVICES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("catCharge", new Gson().toJson(services));
        prefsEditor.apply();
    }

    public static ArrayList<GigCatCharges.Data> getCategoryCharges(Context context) {
        ArrayList<GigCatCharges.Data> services;
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_SERVICES, MODE_PRIVATE);
        String json = mPrefs.getString("catCharge", "");
        if (json.isEmpty()) {
            services = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<GigCatCharges.Data>>() {
            }.getType();
            services = new Gson().fromJson(json, type);
        }
        return services;
    }

    public static void saveTopServices(Context context, List<ServicesModel.Data> services) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_TOP_SERVICES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("myJson", new Gson().toJson(services));
        prefsEditor.apply();
    }

    public static void setInflSubCategory(Context context, List<GigSubCategoryModel.Data> services) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_TOP_SERVICES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("inf_sub_cat", new Gson().toJson(services));
        prefsEditor.apply();
    }

    public static List<GigSubCategoryModel.Data> getInfSubCatList(Context context) {
        List<GigSubCategoryModel.Data> services;
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_TOP_SERVICES, MODE_PRIVATE);
        String json = mPrefs.getString("inf_sub_cat", "");
        if (json.isEmpty()) {
            services = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<GigSubCategoryModel.Data>>() {
            }.getType();
            services = new Gson().fromJson(json, type);
        }
        return services;
    }

    public static List<ServicesModel.Data> getTopServices(Context context) {
        List<ServicesModel.Data> services;
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PREF_TOP_SERVICES, MODE_PRIVATE);
        String json = mPrefs.getString("myJson", "");
        if (json.isEmpty()) {
            services = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<ServicesModel.Data>>() {
            }.getType();
            services = new Gson().fromJson(json, type);
        }
        return services;
    }

    public static void saveUserData(Context context, UserModel userData) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("myJson", new Gson().toJson(userData));
        prefsEditor.apply();
    }

    public static UserModel getUserData(Context context) {
        try {
            SharedPreferences mPrefs = context.getSharedPreferences(Constants.USER_DATA, MODE_PRIVATE);
            String json = mPrefs.getString("myJson", "");
            return new Gson().fromJson(json, UserModel.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveCreateOffer(Context context, CreateOfferResponse userData) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("createOffer", new Gson().toJson(userData));
        prefsEditor.apply();
    }

    public static CreateOfferResponse getCreateOffer(Context context) {
        try {
            SharedPreferences mPrefs = context.getSharedPreferences(Constants.USER_DATA, MODE_PRIVATE);
            String json = mPrefs.getString("createOffer", "");
            return new Gson().fromJson(json, CreateOfferResponse.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setProfileData(Context context, ProfileResponse userData) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PROFILE_DATA, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("profileData", new Gson().toJson(userData));
        prefsEditor.apply();
    }

    public static ProfileResponse getProfileData(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(Constants.PROFILE_DATA, MODE_PRIVATE);
        String json = mPrefs.getString("profileData", "");
        return new Gson().fromJson(json, ProfileResponse.class);
    }

    public static void addMultipleAccounts(Context context, String jwt, String username) {
        HashMap<String, String> testHashMap = Preferences.getMultipleAccounts(context);
        if (testHashMap == null || testHashMap.size() == 0) {
            testHashMap = new HashMap<>();
        }
        testHashMap.put(username, jwt);

        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(testHashMap);

        //save in shared prefs
        SharedPreferences mPrefs = context.getSharedPreferences("accounts", MODE_PRIVATE);
        mPrefs.edit().putString("hashAccount", hashMapString).apply();
    }

    public static void refreshAccount(Context context, HashMap<String, String> accounts) {
        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(accounts);

        //save in shared prefs
        SharedPreferences mPrefs = context.getSharedPreferences("accounts", MODE_PRIVATE);
        mPrefs.edit().putString("hashAccount", hashMapString).apply();
    }

    public static HashMap<String, String> getMultipleAccounts(Context context) {
        //get from shared prefs
        try {
            SharedPreferences mPrefs = context.getSharedPreferences("accounts", MODE_PRIVATE);
            String storedHashMapString = "";
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            if (mPrefs != null) {
                storedHashMapString = mPrefs.getString("hashAccount", "");

            }
            //convert to string using gson
            Gson gson = new Gson();

            return gson.fromJson(storedHashMapString, type);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

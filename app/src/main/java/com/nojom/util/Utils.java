package com.nojom.util;

import static com.nojom.util.FilePath.getDataColumn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.nojom.R;
import com.nojom.Task24Application;
import com.nojom.model.ServicesModel;
import com.nojom.model.UserModel;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.MaintenanceActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final String ENCODING_UTF_8 = "UTF-8";
    private static final String BYTES = "Bytes";
    private static final String MEGABYTES = "MB";
    private static final String KILOBYTES = "kB";
    private static final String GIGABYTES = "GB";
    private static final long KILO = 1024;
    private static final long MEGA = KILO * 1024;
    private static final long GIGA = MEGA * 1024;

    public static String formatFileSize(String pBytes) {
        long bytes = Long.parseLong(pBytes);
        if (bytes < KILO) {
            return pBytes + BYTES;
        } else if (bytes < MEGA) {
            return (int) (0.5 + (bytes / (double) KILO)) + KILOBYTES;
        } else if (bytes < GIGA) {
            return (int) (0.5 + (bytes / (double) MEGA)) + MEGABYTES;
        } else {
            return (int) (0.5 + (bytes / (double) GIGA)) + GIGABYTES;
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static SpannableStringBuilder getBoldString(Context context, String s, String[] fonts, int[] colorList, String[] words) {
        if (TextUtils.isEmpty(s))
            return null;

        SpannableStringBuilder ss = new SpannableStringBuilder(s);
        try {
            for (int i = 0; i < words.length; i++) {
                if (s.contains(words[i])) {
                    if (colorList != null) {
                        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, colorList[i])), s.indexOf(words[i]),
                                s.indexOf(words[i]) + words[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    if (fonts != null && fonts.length > i) {
                        Typeface font = Typeface.createFromAsset(context.getAssets(), fonts[i]);
                        ss.setSpan(new CustomTypefaceSpan("", font), s.indexOf(words[i]), s.indexOf(words[i]) + words[i].length(),
                                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ss;
    }

    public static SpannableStringBuilder getColorString(Context context, String s, String word, int color) {
        SpannableStringBuilder ss = new SpannableStringBuilder(s);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, color)), s.indexOf(word),
                s.indexOf(word) + word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink,
                    startIndexOfLink + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    public static void makeLinks(CheckBox checkBox, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(checkBox.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = checkBox.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink,
                    startIndexOfLink + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        checkBox.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());
        checkBox.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity == null)
            return;

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static String priceWith$(Object object, BaseActivity activity) {
        String price = String.valueOf(object);
        if (TextUtils.isEmpty(price)) {
            return activity.getString(R.string.dollar) + "0";
        }
        if (price.contains(activity.getString(R.string.dollar))) {
            price = price.replace(activity.getString(R.string.dollar), "");
        }
        return activity.getString(R.string.dollar) + price.trim();
    }

    public static String priceWithSAR(BaseActivity activity, Object object) {
        String price = String.valueOf(object);
        if (TextUtils.isEmpty(price)) {
            return "0 " + activity.getString(R.string.sar);
        }
        if (price.contains("SAR")) {
            price = price.replace(activity.getString(R.string.sar), "");
        }
        return price.trim() + " " + activity.getString(R.string.sar);
    }

    public static String priceWithout$(Object object) {
        String price = String.valueOf(object);
        if (TextUtils.isEmpty(price)) {
            return "0";
        }
        if (price.contains("$")) {
            price = price.replace("$", "");
        } else if (price.contains("ريال")) {
            price = price.replace("ريال", "");
        }
        return price.trim();
    }

    public static String priceWithoutSAR(BaseActivity activity, Object object) {
        String price = String.valueOf(object);
        if (TextUtils.isEmpty(price)) {
            return "0";
        }
        if (price.contains(activity.getString(R.string.sar))) {
            price = price.replace(activity.getString(R.string.sar), "");
        }
        return price.trim();
    }

    public static String numberFormat(String number) {
        try {
            Double d = Double.parseDouble(number);
            NumberFormat nf = new DecimalFormat("#.####");
            return nf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    public static String currencyFormat(double number) {
        try {
            DecimalFormat nf = new DecimalFormat("0.00");
            Double d = Double.parseDouble(nf.format(number));
            NumberFormat defaultFormat = NumberFormat.getCurrencyInstance(Locale.US);
            return defaultFormat.format(d);
        } catch (Exception e) {
            number = 0.00;
            e.printStackTrace();
        }
        return String.valueOf(number);
    }

    public static String doubleDigit(double number) {
        String s = numberFormat(String.valueOf(number));
        if (s.length() == 1) {
            s = "0" + s;
        } else if (s.length() > 3) {
            s = s.substring(0, 2) + "..";
        }
        return s;
    }

    public static void openSoftKeyboard(Activity activity, View view) {
        if (activity == null)
            return;

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static String numberFormat(String number, int decimalPoint) {
        try {
            DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(Locale.ENGLISH);
            Double d = Double.parseDouble(number);
            StringBuilder pattern = new StringBuilder();
            pattern.append("0.");
            for (int i = 0; i < decimalPoint; i++) {
                pattern.append("0");
            }
            NumberFormat nf = new DecimalFormat(pattern.toString(), dfs);
            return nf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    public static String numberFormat(double number, int decimalPoint) {
        try {
            DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(Locale.ENGLISH);
            StringBuilder pattern = new StringBuilder();
            pattern.append("0.");
            for (int i = 0; i < decimalPoint; i++) {
                pattern.append("0");
            }
            NumberFormat nf = new DecimalFormat(pattern.toString(), dfs);
            return nf.format(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(number);
    }

    public static int getServiceId(Context context, String serviceName) {
        List<ServicesModel.Data> mData = Preferences.getTopServices(context);
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).name.equalsIgnoreCase(serviceName)) {
                return mData.get(i).id;
            }
        }
        return 0;
    }

    public static String getExperienceLevel(int exp) {
        String level = "";
        try {
            switch (exp) {
                case Constants.LESS_THAN_1_ID:
                    level = Constants.LESS_THAN_1;
                    break;
                case Constants.YEAR_1_3_ID:
                    level = Constants.YEAR_1_3;
                    break;
                case Constants.YEAR_4_6_ID:
                    level = Constants.YEAR_4_6;
                    break;
                case Constants.YEAR_7_9_ID:
                    level = Constants.YEAR_7_9;
                    break;
                case Constants.YEAR_10_12_ID:
                    level = Constants.YEAR_10_12;
                    break;
                case Constants.YEAR_13_15_ID:
                    level = Constants.YEAR_13_15;
                    break;
                case Constants.YEAR_16_18_ID:
                    level = Constants.YEAR_16_18;
                    break;
                case Constants.YEAR_19_21_ID:
                    level = Constants.YEAR_19_21;
                    break;
                case Constants.YEAR_22_24_ID:
                    level = Constants.YEAR_22_24;
                    break;
                case Constants.YEAR_25_27_ID:
                    level = Constants.YEAR_25_27;
                    break;
                case Constants.YEAR_28_30_ID:
                    level = Constants.YEAR_28_30;
                    break;
                case Constants.YEAR_31_ID:
                    level = Constants.YEAR_31_;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return level;
    }

    public static int getExperienceLevel(String exp) {
        int level = 0;
        switch (exp) {
            case Constants.LESS_THAN_1:
                level = Constants.LESS_THAN_1_ID;
                break;
            case Constants.YEAR_1_3:
                level = Constants.YEAR_1_3_ID;
                break;
            case Constants.YEAR_4_6:
                level = Constants.YEAR_4_6_ID;
                break;
            case Constants.YEAR_7_9:
                level = Constants.YEAR_7_9_ID;
                break;
            case Constants.YEAR_10_12:
                level = Constants.YEAR_10_12_ID;
                break;
            case Constants.YEAR_13_15:
                level = Constants.YEAR_13_15_ID;
                break;
            case Constants.YEAR_16_18:
                level = Constants.YEAR_16_18_ID;
                break;
            case Constants.YEAR_19_21:
                level = Constants.YEAR_19_21_ID;
                break;
            case Constants.YEAR_22_24:
                level = Constants.YEAR_22_24_ID;
                break;
            case Constants.YEAR_25_27:
                level = Constants.YEAR_25_27_ID;
                break;
            case Constants.YEAR_28_30:
                level = Constants.YEAR_28_30_ID;
                break;
            case Constants.YEAR_31_:
                level = Constants.YEAR_31_ID;
                break;
        }
        return level;
    }

    public static int getPlatformId(String platform, BaseActivity context) {
        int level = 0;
        if (platform.equals(context.getString(R.string.select_followers))) {
            level = 0;
        } else if (platform.equals(context.getString(R.string._1k_10k))) {
            level = 1;
        } else if (platform.equals(context.getString(R.string._10k_50k))) {
            level = 2;
        } else if (platform.equals(context.getString(R.string._50k_100k))) {
            level = 3;
        } else if (platform.equals(context.getString(R.string._100k_500k))) {
            level = 4;
        } else if (platform.equals(context.getString(R.string._500k_1m))) {
            level = 5;
        } else if (platform.equals(context.getString(R.string._1m_10m))) {
            level = 6;
        } else if (platform.equals(context.getString(R.string._10m))) {
            level = 7;
        }

        return level;
    }

    public static String getPlatformTxt(int platformId, BaseActivity context) {
        String level = "";

        if (platformId == 0) {
            level = context.getString(R.string.select_followers);
        } else if (platformId == 1) {
            level = context.getString(R.string._1k_10k);
        } else if (platformId == 2) {
            level = context.getString(R.string._10k_50k);
        } else if (platformId == 3) {
            level = context.getString(R.string._50k_100k);
        } else if (platformId == 4) {
            level = context.getString(R.string._100k_500k);
        } else if (platformId == 5) {
            level = context.getString(R.string._500k_1m);
        } else if (platformId == 6) {
            level = context.getString(R.string._1m_10m);
        } else if (platformId == 7) {
            level = context.getString(R.string._10m);
        }

        return level;
    }

    public static String getLanguageLevel(int levelId) {
        String level = "";
        switch (levelId) {
            case Constants.BASIC_ID:
                level = Constants.BASIC;
                break;
            case Constants.CONVERSATIONAL_ID:
                level = Constants.CONVERSATIONAL;
                break;
            case Constants.FLUENT_ID:
                level = Constants.FLUENT;
                break;
            case Constants.NATIVE_ID:
                level = Constants.NATIVE;
                break;
        }
        return level;
    }

    public static String getEducationLevel(int levelId) {
        String level = "";
        switch (levelId) {
            case Constants.Associate_ID:
                level = Constants.Associate;
                break;
            case Constants.Bachelor_ID:
                level = Constants.Bachelor;
                break;
            case Constants.Master_ID:
                level = Constants.Master;
                break;
            case Constants.Doctorate_ID:
                level = Constants.Doctorate;
                break;
        }
        return level;
    }

    public static int getEducationLevel(String level) {
        int levelId = 0;
        switch (level) {
            case Constants.Associate:
                levelId = Constants.Associate_ID;
                break;
            case Constants.Bachelor:
                levelId = Constants.Bachelor_ID;
                break;
            case Constants.Master:
                levelId = Constants.Master_ID;
                break;
            case Constants.Doctorate:
                levelId = Constants.Doctorate_ID;
                break;
        }
        return levelId;
    }

    public static String getRatingLevel(int rating) {
        String level = "";
        switch (rating) {
            case Constants.Beginner_ID:
                level = Constants.Beginner;
                break;
            case Constants.Intermediate_ID:
                level = Constants.Intermediate;
                break;
            case Constants.Expert_ID:
                level = Constants.Expert;
                break;
        }
        return level;
    }

    public static int getRatingId(String rating) {
        int ratingId = 0;
        switch (rating) {
            case Constants.Beginner:
                ratingId = Constants.Beginner_ID;
                break;
            case Constants.Intermediate:
                ratingId = Constants.Intermediate_ID;
                break;
            case Constants.Expert:
                ratingId = Constants.Expert_ID;
                break;
        }
        return ratingId;
    }

    public static String getRatingFromId(int rating) {
        String ratingId = Constants.Beginner;
        switch (rating) {
            case 0:
                ratingId = Constants.Beginner;
                break;
            case 1:
                ratingId = Constants.Intermediate;
                break;
            case 2:
                ratingId = Constants.Expert;
                break;
        }
        return ratingId;
    }

    public static String changeDateFormat(String source, String target, String dateString) {
        SimpleDateFormat input = new SimpleDateFormat(source, Locale.getDefault());
        SimpleDateFormat output = new SimpleDateFormat(target, Locale.getDefault());
        input.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            if (!TextUtils.isEmpty(dateString)) {
                Date date = input.parse(dateString);
                return output.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public static Date changeDateFormat(String source, String dateString) {
        SimpleDateFormat input = new SimpleDateFormat(source, Locale.US);
        input.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return input.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null)
            return false;

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static String changeDateFormat(Date date, String target) {
        SimpleDateFormat output = new SimpleDateFormat(target);
        return output.format(date);
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        if (context != null && imageView != null)
            Glide.with(context).load(url).into(imageView);
    }

    public static void loadImage(Context context, int drawable, ImageView imageView) {
        if (context != null && imageView != null)
            Glide.with(context).load(drawable).into(imageView);
    }

    public static String getFileExtFromBytes(File f) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            byte[] buf = new byte[5]; //max ext size + 1
            fis.read(buf, 0, buf.length);
            StringBuilder builder = new StringBuilder(buf.length);
            for (int i = 1; i < buf.length && buf[i] != '\r' && buf[i] != '\n'; i++) {
                builder.append((char) buf[i]);
            }
            return builder.toString().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String base64Decode(String string) {
        String decoded;
        try {
            byte[] bytes = Base64.decode(string, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            decoded = new String(bytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new DecodeException("Received bytes didn't correspond to a valid Base64 encoded string.", e);
        }
        return decoded;
    }

    public static String decode(String token) {
        final String[] parts = splitToken(token);
        return base64Decode(parts[1]);
    }

    private static String[] splitToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length == 2 && token.endsWith(".")) {
            //Tokens with alg='none' have empty String as Signature.
            parts = new String[]{parts[0], parts[1], ""};
        }
        if (parts.length != 3) {
            throw new DecodeException(String.format("The token was expected to have 3 parts, but got %s.", parts.length));
        }
        return parts;
    }

    public static String getFileNameFromUrl(String url) {
        try {
            URL resource = new URL(url);
            String urlString = resource.getFile();
            return urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

//    public static void trackAppsFlayerEvent(Context context, String eventName) {
//        new Thread(() -> {
//            UserModel userData = Preferences.getUserData(context);
////            try {
////                if (userData != null && userData.id != 0) {
////                    Map<String, Object> eventValues = new HashMap<>();
////                    eventValues.put("UserId", userData.id);
////                }
////
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//
//            //firebase analytic
//            try {
//                Bundle bundle = new Bundle();
//                bundle.putString("UserId", userData != null ? String.valueOf(userData.id) : "");
//                bundle.putString("Screen", eventName + " - Agent");
//                Task24Application.getActivity().getFirebaseAnalytics().logEvent(eventName, bundle);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }

    public static void trackFirebaseEvent(Context context, String eventName) {
        new Thread(() -> {
            UserModel userData = Preferences.getUserData(context);

            //firebase analytic
            try {
                String name = eventName + "_Agent";
                Bundle bundle = new Bundle();
                bundle.putString("UserId", userData != null ? String.valueOf(userData.id) : "");
                bundle.putString("Screen", name);
                Task24Application.getActivity().getFirebaseAnalytics().logEvent(name, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static String setDeadLine(BaseActivity activity, String deadline) {
        try {
            String[] deadlineText = deadline.split(" ");
            String date = deadlineText[0];
            String time = deadlineText[1];
            String outputFormat = "EEEE, dd MMMM yyyy";
            String outputFormatTime = "hh:mm a";

            if (activity.language.equals("ar")) {
                outputFormat = "EEEE, dd MMMM yyyy";
                outputFormatTime = "hh:mm a";
            }

            String inputFormat = "yyyy-MM-dd";
            String inputFormatTime = "HH:mm";

            String formattedDate = TimeStampConverter(inputFormat, date, outputFormat);
            String formattedTime = TimeStampConverter(inputFormatTime, time, outputFormatTime);
            return (String.format("%s %s %s", formattedDate, activity.getString(R.string.at), formattedTime));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String setDeadLine1(BaseActivity activity, String deadline) {
        try {
            String[] deadlineText = deadline.split(" ");
            String date = deadlineText[0];
            String time = deadlineText[1];
            String outputFormat = "EEEE, dd MMMM yyyy";
            String outputFormatTime = "hh:mm a";

            if (activity.language.equals("ar")) {
                outputFormat = "EEEE, dd MMMM yyyy";
                outputFormatTime = "hh:mm a";
            }

            String inputFormat = "yyyy-MM-dd";
            String inputFormatTime = "HH:mm";

            String formattedDate = TimeStampConverter(inputFormat, date, outputFormat);
            String formattedTime = TimeStampConverter(inputFormatTime, time, outputFormatTime);
            return (String.format("%s, %s", formattedDate, formattedTime));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String TimeStampConverter(final String inputFormat,
                                            String inputTimeStamp, final String outputFormat)
            throws ParseException {
        return new SimpleDateFormat(outputFormat, Locale.getDefault()).format(new SimpleDateFormat(
                inputFormat, Locale.getDefault()).parse(inputTimeStamp));
    }

    public static String jobsDisplayDate(String timestamp) {
        PrettyTime p = new PrettyTime();
        Date date1 = Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", timestamp);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dfFinal2 = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        String result = "";
        if (date1 != null) {
            if (printDifference(date1, date).equalsIgnoreCase("0")) {
                result = p.format(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", timestamp));
            } else {
                result = dfFinal2.format(date1);
            }
        }
        return result;
    }

    public static String printDifference(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

      /*  System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);*/

        return String.valueOf(elapsedDays);

    }

    public static void toastMessage(Activity context, String message) {
        try {
            LayoutInflater inflater = context.getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast,
                    (ViewGroup) context.findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(message);

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.BOTTOM, 0, 40);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkForMaintenance(Activity activity) {
        try {
            final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
            remoteConfig.fetchAndActivate()
                    .addOnCompleteListener(activity, task -> {
                        if (!task.isSuccessful()) {
                            return;
                        }

//                        if (!BuildConfig.DEBUG) {
//                            String liveBaseUrl = remoteConfig.getString(KEY_LIVE_BASE_URL);
//                            if (!TextUtils.isEmpty(liveBaseUrl)) {
//                                Task24Application.LIVE_URL = liveBaseUrl;
//                            }
//                            Log.e("Remote LIVE BASE ", "" + Task24Application.LIVE_URL);
//                            ApiClient.retrofit = null;
//                        }
//
//                        if (!BuildConfig.DEBUG) {
//                            String liveBaseUrl = remoteConfig.getString(KEY_LIVE_BASE_URL_GIG);
//                            Log.e("Remote LIVE BASE GIG ", "" + liveBaseUrl);
//                            BASE_URL_GIG = liveBaseUrl;
//                            ApiClient.retrofitGig = null;
//                        }

                        if (remoteConfig.getString(Constants.KEY_FOR_MAINTENANCE).equalsIgnoreCase("true")) {
                            if (!isActivityRunning(MaintenanceActivity.class, activity, false)) {
//                                activity.finish();
                                Intent intent = new Intent(activity, MaintenanceActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                activity.startActivity(intent);
                            }
                        } else {
                            isActivityRunning(MaintenanceActivity.class, activity, true);
                        }

                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean isActivityRunning(Class activityClass, Activity activity, boolean isFinish) {
        ActivityManager activityManager = (ActivityManager) activity.getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName())) {
                if (isFinish) {
                    activity.finish();

                }
                return true;
            }
        }

        return false;
    }

    public static String get2DecimalPlaces(Object o) {
        if (o instanceof Double)
            return numberFormat((double) o, 2);
        else if (o instanceof Integer)
            return numberFormat((int) o, 2);
        else if (o instanceof Float)
            return numberFormat((float) o, 1);

        return numberFormat((String) o, 2);
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public static String getDecimalValue(String value) {
        try {
            DecimalFormatSymbols nf = DecimalFormatSymbols.getInstance(Locale.ENGLISH);
            DecimalFormat format = new DecimalFormat("0.##", nf);
            return format.format(Float.parseFloat(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    @SuppressLint("NewApi")
    public static String getFilePath(BaseActivity context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                try {
                    uri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(context, uri);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return copyFileToInternalStorage(context, uri, "24task");
            } else {
                return getDataColumn(context, uri, null, null);
            }

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static String getDriveFilePath(BaseActivity activity, Uri uri) {
        Uri returnUri = uri;
        Cursor returnCursor = activity.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(activity.getCacheDir(), name);
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1024 * 1024 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    public static String getFileName(BaseActivity activity, Uri uri) throws IllegalArgumentException {
        // Obtain a cursor with information regarding this uri
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }

        cursor.moveToFirst();

        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));

        cursor.close();

        return fileName;
    }

    private static String copyFileToInternalStorage(BaseActivity activity, Uri uri, String newDirName) {
        Uri returnUri = uri;

        Cursor returnCursor = activity.getContentResolver().query(returnUri, new String[]{
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
        }, null, null, null);


        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));

        File output;
        if (!newDirName.equals("")) {
            File dir = new File(activity.getFilesDir() + "/" + newDirName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            output = new File(activity.getFilesDir() + "/" + newDirName + "/" + name);
        } else {
            output = new File(activity.getFilesDir() + "/" + name);
        }
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(output);
            int read = 0;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {

            Log.e("Exception", e.getMessage());
        }

        return output.getPath();
    }

    public static String nFormate(double d) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setMaximumFractionDigits(2);
        return nf.format(d);
    }

    public static String nFormate(int d) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setMaximumFractionDigits(2);
        return nf.format(d);
    }

    public enum WindowScreen {
        NAME, USERNAME, WEBSITE, OFFER, MESSAGE, IMG, GENDER, DOB, COUNTRY, ABOUT_ME,EMAIL
    }

    public static boolean validateWebsite(String input) {
        String regex = "^https://.*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            // If the input doesn't match the expected pattern, show an error
            return false;
        } else {
            // Clear the error if the input is valid
            return true;
        }
    }

    public static int calculateAge(String birthdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            // Parse the birthdate string into a Date object
            Date birthDate = sdf.parse(birthdate);

            // Get the current date
            Calendar currentDate = Calendar.getInstance();

            // Get the birthdate year and month
            Calendar birthDateCal = Calendar.getInstance();
            if (birthDate != null) {
                birthDateCal.setTime(birthDate);
            }
            int birthYear = birthDateCal.get(Calendar.YEAR);
            int birthMonth = birthDateCal.get(Calendar.MONTH);
            int birthDay = birthDateCal.get(Calendar.DAY_OF_MONTH);

            // Get the current year, month, and day
            int currentYear = currentDate.get(Calendar.YEAR);
            int currentMonth = currentDate.get(Calendar.MONTH);
            int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

            // Calculate the age
            int age = currentYear - birthYear;

            // Check if the birthday has occurred this year
            if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
                age--;
            }

            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Return -1 if there's an error parsing the date
        }
    }

    public static String decimalFormat(String number) {
        try {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            Double d = Double.parseDouble(String.valueOf(number));
            DecimalFormat format = new DecimalFormat();
            format.setDecimalFormatSymbols(symbols);
            format.setDecimalSeparatorAlwaysShown(false);
            return format.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }
}

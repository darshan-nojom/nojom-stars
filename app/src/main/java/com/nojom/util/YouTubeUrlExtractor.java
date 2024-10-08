package com.nojom.util;

import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class YouTubeUrlExtractor {

    public static String extractYouTubeUrl(String inputUrl) {
        try {
            Uri uri = Uri.parse(inputUrl);
            String queryParameter = uri.getQueryParameter("url");
            if (queryParameter != null) {
                return URLDecoder.decode(queryParameter, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
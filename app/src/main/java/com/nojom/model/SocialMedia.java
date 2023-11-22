package com.nojom.model;

import java.io.Serializable;
import java.util.List;

public class SocialMedia {

    public String title;
    public List<SocialPlatform> socialPlatformList;

    public SocialMedia(String title, List<SocialPlatform> socialPlatformList) {
        this.title = title;
        this.socialPlatformList = socialPlatformList;
    }

    public static class SocialPlatform implements Serializable {

        public String socialPlatformUrl;
        public String socialPlatTitle;

        public SocialPlatform(String socialPlatformUrl, String socialPlatTitle) {
            this.socialPlatformUrl = socialPlatformUrl;
            this.socialPlatTitle = socialPlatTitle;
        }
    }
}

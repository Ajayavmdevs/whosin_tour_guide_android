package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import java.util.ArrayList;
import java.util.List;

public class ContactUsBlockModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";

    @SerializedName("title")
    @Expose
    private String title = "";

    @SerializedName("description")
    @Expose
    private String desc = "";

    @SerializedName("platform")
    @Expose
    private String platform = "";

    @SerializedName("cta")
    @Expose
    private List<CTAModel> cta = new ArrayList<>();

    @SerializedName("screen")
    @Expose
    private List<ScreenModel> screen = new ArrayList<>();

    @SerializedName("media")
    @Expose
    private MediaModel media;

    public String getId() {
        return Utils.notNullString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return Utils.notNullString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return Utils.notNullString(desc);
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPlatform() {
        return Utils.notNullString(platform);
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<CTAModel> getCta() {
        return cta == null ? new ArrayList<>() : cta;
    }

    public void setCta(List<CTAModel> cta) {
        this.cta = cta;
    }

    public List<ScreenModel> getScreen() {
        return screen == null ? new ArrayList<>() : screen;
    }

    public void setScreen(List<ScreenModel> screen) {
        this.screen = screen;
    }

    public MediaModel getMedia() {
        return media;
    }

    public void setMedia(MediaModel media) {
        this.media = media;
    }

    private boolean screenMatches(String value, ContactBlockScreens screenName) {
        String v = Utils.notNullString(value);
        String target = screenName.getValue();
        if (v.equalsIgnoreCase(target)) return true;
        return false;
    }

    public Double height(ContactBlockScreens screenName) {
        if (screen != null) {
            for (ScreenModel s : screen) {
                if (screenMatches(s.getScreenName(), screenName)) {
                    try {
                        return Double.parseDouble(s.getSize());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    public boolean isEnabled(ContactBlockScreens screenName) {
        if (screen != null) {
            for (ScreenModel s : screen) {
                if (screenMatches(s.getScreenName(), screenName)) {
                    return s.isEnabled();
                }
            }
        }
        return false;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    // Inner Classes

    public static class MediaModel implements DiffIdentifier, ModelProtocol {

        @SerializedName("type")
        @Expose
        private String type = "";

        @SerializedName("url")
        @Expose
        private String url = "";

        @SerializedName("backgroundColor")
        @Expose
        private String backgroundColor = "#191919";

        @SerializedName("height")
        @Expose
        private Double height = 0.0;

        @SerializedName("ratio")
        @Expose
        private String ratio = "";

        public String getType() {
            return Utils.notNullString(type);
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return Utils.notNullString(url);
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getBackgroundColor() {
            return Utils.notNullString(backgroundColor);
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public Double getHeight() {
            return height;
        }

        public void setHeight(Double height) {
            this.height = height;
        }

        public String getRatio() {
            return Utils.notNullString(ratio);
        }

        public void setRatio(String ratio) {
            this.ratio = ratio;
        }

        @Override
        public int getIdentifier() {
            return 0;
        }

        @Override
        public boolean isValidModel() {
            return true;
        }
    }

    public static class ScreenModel implements DiffIdentifier, ModelProtocol {

        @SerializedName("screenName")
        @Expose
        private String screenName = "";

        @SerializedName("isEnabled")
        @Expose
        private boolean isEnabled = false;

        @SerializedName("size")
        @Expose
        private String size = "280";

        public String getScreenName() {
            return Utils.notNullString(screenName);
        }

        public void setScreenName(String screenName) {
            this.screenName = screenName;
        }

        public boolean isEnabled() {
            return isEnabled;
        }

        public void setEnabled(boolean enabled) {
            isEnabled = enabled;
        }

        public String getSize() {
            return Utils.notNullString(size);
        }

        public void setSize(String size) {
            this.size = size;
        }

        @Override
        public int getIdentifier() {
            return 0;
        }

        @Override
        public boolean isValidModel() {
            return true;
        }
    }

    public static class CTAModel implements DiffIdentifier, ModelProtocol {

        @SerializedName("text")
        @Expose
        private String text = "";

        @SerializedName("actionType")
        @Expose
        private String actionType = "";

        @SerializedName("link")
        @Expose
        private String link = "";

        @SerializedName("backgroundColor")
        @Expose
        private String backgroundColor = "";

        public String getText() {
            return Utils.notNullString(text);
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getActionType() {
            return Utils.notNullString(actionType);
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public String getLink() {
            return Utils.notNullString(link);
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getBackgroundColor() {
            return Utils.notNullString(backgroundColor);
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        @Override
        public int getIdentifier() {
            return 0;
        }

        @Override
        public boolean isValidModel() {
            return true;
        }
    }
    
    public enum ContactBlockScreens {
        // Add cases as needed, these are placeholders
        HOME("homeBlock"),
        DETAILS("ticket"),
        EXPLORE("exploreBlock"),
        CART("cart");

        private final String value;

        ContactBlockScreens(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}

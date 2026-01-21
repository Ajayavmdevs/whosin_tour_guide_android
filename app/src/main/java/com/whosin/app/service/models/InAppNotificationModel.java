package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

public class InAppNotificationModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("title")
    @Expose
    private IANComponentModel title;

    @SerializedName("subtitle")
    @Expose
    private IANComponentModel subtitle;

    @SerializedName("description")
    @Expose
    private IANComponentModel description;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("button1")
    @Expose
    private IANComponentModel button1;

    @SerializedName("button2")
    @Expose
    private IANComponentModel button2;

    @SerializedName("background")
    @Expose
    private IANComponentModel background;

    @SerializedName("layout")
    @Expose
    private String layout;

    @SerializedName("viewType")
    @Expose
    private String viewType;

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("readStatus")
    @Expose
    private boolean readStatus = false;

    @SerializedName("showOnAppLoad")
    @Expose
    private boolean showOnAppLoad = false;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("userType")
    @Expose
    private String userType = "";

    public IANComponentModel getTitle() {
        return title;
    }

    public void setTitle(IANComponentModel title) {
        this.title = title;
    }

    public IANComponentModel getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(IANComponentModel subtitle) {
        this.subtitle = subtitle;
    }

    public IANComponentModel getDescription() {
        return description;
    }

    public void setDescription(IANComponentModel description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public IANComponentModel getButton1() {
        return button1;
    }

    public void setButton1(IANComponentModel button1) {
        this.button1 = button1;
    }

    public IANComponentModel getButton2() {
        return button2;
    }

    public void setButton2(IANComponentModel button2) {
        this.button2 = button2;
    }

    public IANComponentModel getBackground() {
        return background;
    }

    public void setBackground(IANComponentModel background) {
        this.background = background;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getViewType() {
        return Utils.notNullString(viewType);
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getId() {
        return Utils.notNullString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public boolean isShowOnAppLoad() {
        return showOnAppLoad;
    }

    public void setShowOnAppLoad(boolean showOnAppLoad) {
        this.showOnAppLoad = showOnAppLoad;
    }

    public String getUserId() {
        return Utils.notNullString(userId);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return Utils.notNullString(userType);
    }

    public void setUserType(String userType) {
        this.userType = userType;
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

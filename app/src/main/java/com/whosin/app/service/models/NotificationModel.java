package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class NotificationModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("promoterId")
    @Expose
    private String promoterId = "";
    @SerializedName("subAdminId")
    @Expose
    private String subAdminId = "";
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("description")
    @Expose
    private String description = "";
    @SerializedName("image")
    @Expose
    private String image = "";
    @SerializedName("platform")
    @Expose
    private String platform = "";
    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("venueId")
    @Expose
    private String venueId = "";
    @SerializedName("offerId")
    @Expose
    private String offerId = "";
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt = "";
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("readStatus")
    @Expose
    private boolean readStatus = false;
    @SerializedName("promoterStatus")
    @Expose
    private String promoterStatus = "";
    @SerializedName("subAdminStatus")
    @Expose
    private String subAdminStatus = "";
    @SerializedName("categoryId")
    @Expose
    private String categoryId = "";
    @SerializedName("__v")
    @Expose
    private int v;
    @SerializedName("isDeleted")
    @Expose
    private boolean isDeleted;
    @SerializedName("images")
    @Expose
    private List<String> images;

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    @SerializedName("requestStatus")
    @Expose
    private String requestStatus = "";

    @SerializedName("promoterList")
    @Expose
    private List<PromoterListModel> promoterList;
    @SerializedName("typeId")
    @Expose
    private String typeId = "";

    @SerializedName("plusOneStatus")
    @Expose
    private String plusOneStatus = "";

    @SerializedName("adminStatusOnPlusOne")
    @Expose
    private String adminStatusOnPlusOne = "";
    @SerializedName("promoterEvent")
    @Expose
    private List<PromoterEventModel> promoterEvent;

    @SerializedName("list")
    @Expose
    private List<PromoterListModel> list;

    @SerializedName("event")
    @Expose
    private PromoterEventModel event;

    @SerializedName("userId")
    @Expose
    private String userId;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    //    public List<PromoterListModel> getPromoterList() {
//        return promoterList  == null ? new ArrayList<>() : promoterList;
//    }
//    public void setPromoterList(List<PromoterListModel> promoterList) {
//        this.promoterList = promoterList;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(String promoterId) {
        this.promoterId = promoterId;
    }

    public String getSubAdminId() {
        return subAdminId;
    }

    public void setSubAdminId(String subAdminId) {
        this.subAdminId = subAdminId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPromoterStatus() {
        return promoterStatus;
    }

    public void setPromoterStatus(String promoterStatus) {
        this.promoterStatus = promoterStatus;
    }

    public String getSubAdminStatus() {
        return subAdminStatus;
    }

    public void setSubAdminStatus(String subAdminStatus) {
        this.subAdminStatus = subAdminStatus;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<PromoterListModel> getList() {
        return list;
    }

    public void setList(List<PromoterListModel> list) {
        this.list = list;
    }

    public PromoterEventModel getEvent() {
        return event;
    }

    public void setEvent(PromoterEventModel event) {
        this.event = event;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public String getPlusOneStatus() {
        return plusOneStatus;
    }

    public void setPlusOneStatus(String plusOneStatus) {
        this.plusOneStatus = plusOneStatus;
    }

    public String getAdminStatusOnPlusOne() {
        return adminStatusOnPlusOne;
    }

    public void setAdminStatusOnPlusOne(String adminStatusOnPlusOne) {
        this.adminStatusOnPlusOne = adminStatusOnPlusOne;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.Repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;

public class BucketChatProfileModel implements DiffIdentifier,ModelProtocol{

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("coverImage")
    @Expose
    private String coverImage;
    @SerializedName("shared_with")
    @Expose
    private List<String> sharedWith;

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("items")
    @Expose
    private List<BucketItemModel> items;

    @SerializedName("offers")
    @Expose
    private List<OffersModel> offers;
    @SerializedName("events")
    @Expose
    private List<EventModel> events;

    @SerializedName("activities")
    @Expose
    private List<ActivityDetailModel> activity;

    @SerializedName("galleries")
    @Expose
    private List<String> galleries;

    @SerializedName("user")
    @Expose
    private UserDetailModel user;

    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return Utils.notNullString(name);

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return Utils.notNullString(coverImage);
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<String > getSharedWith() {
        return sharedWith != null ? sharedWith : new ArrayList<>();
    }

    public void setSharedWith(List<String> sharedWith) {
        this.sharedWith = sharedWith;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<BucketItemModel> getItems() {
        return items != null ? items : new ArrayList<>() ;
    }

    public void setItems(List<BucketItemModel> items) {
        this.items = items;
    }

    public List<OffersModel> getOffers() {
        return offers != null ? offers : new ArrayList<>();
    }

    public void setOffers(List<OffersModel> offers) {
        this.offers = offers;
    }

    public List<EventModel> getEvents() {
        return events != null ? events : new ArrayList<>();
    }

    public void setEvents(List<EventModel> events) {
        this.events = events;
    }


    public void setGalleries(List<String> galleries) {
        this.galleries = galleries;
    }

    public List<String> getGalleries() {
        return galleries != null ? galleries : new ArrayList<>();
    }


    public List<ActivityDetailModel> getActivity() {
        return activity != null ? activity : new ArrayList<>();
    }

    public void setActivity(List<ActivityDetailModel> activity) {
        this.activity = activity;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public UserDetailModel getUser() {
        return user;
    }

    public void setUser(UserDetailModel user) {
        this.user = user;
    }

    public ChatMessageModel getLastMsg() {
        ChatMessageModel model =  ChatRepository.shared(Graphics.context).getLastMessages(id);
        if (model == null) {
            return new ChatMessageModel();
        }
        return model;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

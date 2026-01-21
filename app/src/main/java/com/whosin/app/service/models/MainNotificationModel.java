package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;

import java.util.ArrayList;
import java.util.List;

public class MainNotificationModel implements DiffIdentifier, ModelProtocol {
    @SerializedName("total")
    @Expose
    private int total;
    @SerializedName("notification")
    @Expose
    private List<NotificationModel> notification;
    @SerializedName("category")
    @Expose
    private List<CategoriesModel> category;
    @SerializedName("offer")
    @Expose
    private List<OffersModel> offer;
    @SerializedName("venue")
    @Expose
    private List<VenueObjectModel> venue;
    @SerializedName("user")
    @Expose
    private List<UserDetailModel> user;
    @SerializedName("ticket")
    @Expose
    private List<RaynaTicketDetailModel> tickets;
    @SerializedName("socialAccount")
    @Expose
    private List<SocialAccountsToMentionModel> socialAccountsToMention;

    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("count")
    @Expose
    private int count ;
    @SerializedName("readStatus")
    @Expose
    private boolean readStatus = false;


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<NotificationModel> getNotification() {
        return notification == null ? new ArrayList<>() : notification;
    }

    public void setNotification(List<NotificationModel> notification) {
        this.notification = notification;
    }

    public List<CategoriesModel> getCategory() {
        return category  == null ? new ArrayList<>() : category;
    }

    public void setCategory(List<CategoriesModel> category) {
        this.category = category;
    }

    public List<OffersModel> getOffer() {
        return offer  == null ? new ArrayList<>() : offer;
    }

    public void setOffer(List<OffersModel> offer) {
        this.offer = offer;
    }

    public List<VenueObjectModel> getVenue() {
        return venue  == null ? new ArrayList<>() : venue;
    }

    public void setVenue(List<VenueObjectModel> venue) {
        this.venue = venue;
    }
    public List<SocialAccountsToMentionModel> getSocialAccountsToMention() {
        return socialAccountsToMention  == null ? new ArrayList<>() : socialAccountsToMention;
    }

    public void setSocialAccountsToMention(List<SocialAccountsToMentionModel> socialAccountsToMention) {
        this.socialAccountsToMention = socialAccountsToMention;
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public List<UserDetailModel> getUser() {
        return user == null ? new ArrayList<>() : user;
    }

    public void setUser(List<UserDetailModel> user) {
        this.user = user;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public List<RaynaTicketDetailModel> getTickets() {
        return Utils.notEmptyList(tickets);
    }

    public void setTickets(List<RaynaTicketDetailModel> tickets) {
        this.tickets = tickets;
    }
}

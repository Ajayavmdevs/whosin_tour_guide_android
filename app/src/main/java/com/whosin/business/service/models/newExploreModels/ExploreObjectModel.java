package com.whosin.business.service.models.newExploreModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.service.models.ActivityDetailModel;
import com.whosin.business.service.models.BannerModel;
import com.whosin.business.service.models.CategoriesModel;
import com.whosin.business.service.models.ContactListModel;
import com.whosin.business.service.models.CustomComponentModel;
import com.whosin.business.service.models.EventModel;
import com.whosin.business.service.models.MemberShipModel;
import com.whosin.business.service.models.ModelProtocol;
import com.whosin.business.service.models.OffersModel;
import com.whosin.business.service.models.VenueObjectModel;
import com.whosin.business.service.models.YachtDetailModel;
import com.whosin.business.service.models.YachtsOfferModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;

import java.util.ArrayList;
import java.util.List;

public class ExploreObjectModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("blocks")
    @Expose
    private List<ExploreBlockModel> blocks;
    @SerializedName("stories")
    @Expose
    private List<VenueObjectModel> stories;
    @SerializedName("categories")
    @Expose
    private List<CategoriesModel> categories;

    @SerializedName("activities")
    @Expose
    private List<ActivityDetailModel> activities;

    @SerializedName("events")
    @Expose
    private List<EventModel> events;

    @SerializedName("offers")
    @Expose
    private List<OffersModel> offers;

    @SerializedName("venues")
    @Expose
    private List<VenueObjectModel> venues;
    @SerializedName("yachts")
    @Expose
    private List<YachtDetailModel> yachts;
    @SerializedName("yachtOffers")
    @Expose
    private List<YachtsOfferModel> yachtOffers;
    @SerializedName("users")
    @Expose
    private List<ContactListModel> users;
    @SerializedName("membershipPackages")
    @Expose
    private List<MemberShipModel> membershipPackages;
    @SerializedName("tickets")
    @Expose
    private List<RaynaTicketDetailModel> tickets;
    @SerializedName("cities")
    @Expose
    private List<CategoriesModel> cities;
    @SerializedName("banners")
    @Expose
    private List<BannerModel> banners;
    @SerializedName("customComponents")
    @Expose
    private List<CustomComponentModel> customComponents;
    @SerializedName("ticketCategories")
    @Expose
    private List<CategoriesModel> ticketCategories;



    public List<ExploreBlockModel> getBlocks() {
        return (blocks == null) ? new ArrayList<>() : blocks;
    }

    public void setBlocks(List<ExploreBlockModel> blocks) {
        this.blocks = blocks;
    }

    public List<VenueObjectModel> getStories() {
        return (stories == null) ? new ArrayList<>() : stories;
    }

    public void setStories(List<VenueObjectModel> stories) {
        this.stories = stories;
    }

    public List<CategoriesModel> getCategories() {
        return (categories == null) ? new ArrayList<>() : categories;

    }

    public void setCategories(List<CategoriesModel> categories) {
        this.categories = categories;
    }


    public List<ActivityDetailModel> getActivities() {
        return (activities == null) ? new ArrayList<>() : activities;
    }

    public void setActivities(List<ActivityDetailModel> activities) {
        this.activities = activities;
    }

    public List<EventModel> getEvents() {
        return (events == null) ? new ArrayList<>() : events;
    }

    public void setEvents(List<EventModel> events) {
        this.events = events;
    }

    public List<OffersModel> getOffers() {
        return (offers == null) ? new ArrayList<>() : offers;
    }

    public void setOffers(List<OffersModel> offers) {
        this.offers = offers;
    }

    public List<VenueObjectModel> getVenues() {
        return (venues == null) ? new ArrayList<>() : venues;
    }

    public void setVenues(List<VenueObjectModel> venues) {
        this.venues = venues;
    }

    public List<YachtDetailModel> getYachts() {
        return (yachts == null) ? new ArrayList<>() : yachts;
    }

    public void setYachts(List<YachtDetailModel> yachts) {this.yachts = yachts;
    }
    public List<YachtsOfferModel> getYachtOffers() {
        return (yachtOffers == null) ? new ArrayList<>() : yachtOffers;
    }

    public void setYachtOffers(List<YachtsOfferModel> yachtOffers) {
        this.yachtOffers = yachtOffers;
    }
    public List<ContactListModel> getUsers() {
        return (users == null) ? new ArrayList<>() : users;
    }

    public void setUsers(List<ContactListModel> users) {
        this.users = users;
    }

    public List<MemberShipModel> getMembershipPackages() {
        return membershipPackages;
    }

    public void setMembershipPackages(List<MemberShipModel> membershipPackages) {
        this.membershipPackages = membershipPackages;
    }

    public List<RaynaTicketDetailModel> getTickets() {
        return tickets;
    }

    public void setTickets(List<RaynaTicketDetailModel> tickets) {
        this.tickets = tickets;
    }

    public List<CategoriesModel> getCities() {
        return cities;
    }

    public void setCities(List<CategoriesModel> cities) {
        this.cities = cities;
    }

    public List<BannerModel> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerModel> banners) {
        this.banners = banners;
    }

    public List<CustomComponentModel> getCustomComponents() {
        return customComponents;
    }

    public void setCustomComponents(List<CustomComponentModel> customComponents) {
        this.customComponents = customComponents;
    }

    public List<CategoriesModel> getTicketCategories() {
        return ticketCategories;
    }

    public void setTicketCategories(List<CategoriesModel> ticketCategories) {
        this.ticketCategories = ticketCategories;
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

package com.whosin.app.service.models.newExploreModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.BannerModel;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.ContactUsBlockModel;
import com.whosin.app.service.models.CustomComponentModel;
import com.whosin.app.service.models.CustomVenueModel;
import com.whosin.app.service.models.ExclusiveDealModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.ModelProtocol;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.SizeModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VideoComponentModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;

import java.util.ArrayList;
import java.util.List;

public class ExploreBlockModel implements DiffIdentifier, ModelProtocol {
    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("description")
    @Expose
    private String description = "";
    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("venues")
    @Expose
    private List<String> venues;
    @SerializedName("yachts")
    @Expose
    private List<String> yachts;
    @SerializedName("cities")
    @Expose
    private List<String> cities;
    @SerializedName("categories")
    @Expose
    private List<String> categories;

    @SerializedName("banners")
    @Expose
    private List<String> banners;

    @SerializedName("yachtOffers")
    @Expose
    private List<String> yachtOffers;

    @SerializedName("customVenues")
    @Expose
    private List<String> customVenues;
    @SerializedName("offers")
    @Expose
    private List<String> offers;
    @SerializedName("customOffers")
    @Expose
    private List<CustomVenueModel> customOffers;
    @SerializedName("sliders")
    @Expose
    private List<Object> sliders;
    @SerializedName("videos")
    @Expose
    private List<VideoComponentModel> videos;
    @SerializedName("deals")
    @Expose
    private List<ExclusiveDealModel> deals;
    @SerializedName("customComponents")
    @Expose
    private List<String> customComponents;
    @SerializedName("promoterEvents")
    @Expose
    private List<PromoterEventModel> promoterEvents;
    @SerializedName("tickets")
    @Expose
    private List<String> tickets;
    @SerializedName("hotels")
    @Expose
    private List<String> hotels;
    @SerializedName("myOuting")
    @Expose
    private List<InviteFriendModel> myOuting;
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";
    @SerializedName("size")
    @Expose
    private SizeModel size;
    @SerializedName("activities")
    @Expose
    private List<String> activities;
    @SerializedName("events")
    @Expose
    private List<String> events;
    @SerializedName("membershipPackages")
    @Expose
    private List<String> membershipPackages;
    @SerializedName("suggestedUsers")
    @Expose
    private List<UserDetailModel> suggestedUsers;
    @SerializedName("suggestedVenues")
    @Expose
    private List<VenueObjectModel> suggestedVenue;

    @SerializedName("color")
    @Expose
    private String color;

    @SerializedName("backgroundImage")
    @Expose
    private String backgroundImage;

    @SerializedName("applicationStatus")
    @Expose
    private String applicationStatus;

    @SerializedName("shape")
    @Expose
    private String shape = "";

    @SerializedName("showTitle")
    @Expose
    private boolean showTitle = true;

    @SerializedName("ticketCategories")
    @Expose
    private List<String> ticketCategories;

    @SerializedName("contactUsBlock")
    @Expose
    private List<ContactUsBlockModel> contactUsBlock;


    public List<RaynaTicketDetailModel> cmTicketList = new ArrayList<>();
    public List<RaynaTicketDetailModel> ticketList = new ArrayList<>();
    public List<CategoriesModel> citiesList = new ArrayList<>();
    public List<CategoriesModel> categoryList = new ArrayList<>();
    public List<BannerModel> bigCategoryList = new ArrayList<>();
    public List<BannerModel> smallCategoryList = new ArrayList<>();
    public List<BannerModel> bannerList = new ArrayList<>();
    public List<CustomComponentModel> customComponentModelList = new ArrayList<>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public AppConstants.ExploreBlockType getBlockType() {
        return switch (getType()) {
            case "ticket" -> AppConstants.ExploreBlockType.TICKET;
            case "city" -> AppConstants.ExploreBlockType.CITY;
            case "category" -> AppConstants.ExploreBlockType.CATEGORY;
            case "big-category" -> AppConstants.ExploreBlockType.BIG_CATEGORY;
            case "small-category" -> AppConstants.ExploreBlockType.SMALL_CATEGORY;
            case "banner" -> AppConstants.ExploreBlockType.BANNER;
            case "custom-component" -> AppConstants.ExploreBlockType.CUSTOM_COMPONENT;
            case "juniper-hotel" -> AppConstants.ExploreBlockType.JUNIPER_HOTEL;
            case "contact-us" -> AppConstants.ExploreBlockType.CONTACT_US;
            case AppConstants.ADTYPE -> AppConstants.ExploreBlockType.HOME_AD;
            default -> AppConstants.ExploreBlockType.NONE;
        };
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getCustomVenues() {
        return customVenues == null ? new ArrayList<>() : customVenues;
    }

    public void setCustomVenues(List<String> customVenues) {
        this.customVenues = customVenues;
    }


    public List<CustomVenueModel> getCustomOffers() {
        return customOffers;
    }

    public void setCustomOffers(List<CustomVenueModel> customOffers) {
        this.customOffers = customOffers;
    }

    public List<VideoComponentModel> getVideos() {
        return videos == null ? new ArrayList<>() : videos;
    }

    public void setVideos(List<VideoComponentModel> videos) {
        this.videos = videos;
    }

    public List<ExclusiveDealModel> getDeals() {
        if (deals == null) {
            return new ArrayList<>();
        }
        return deals;
    }

    public void setDeals(List<ExclusiveDealModel> deals) {
        this.deals = deals;
    }

    public List<String> getCustomComponents() {
        return customComponents;
    }

    public void setCustomComponents(List<String> customComponents) {
        this.customComponents = customComponents;
    }

    public List<PromoterEventModel> getPromoterEvents() {
        return promoterEvents;
    }

    public void setPromoterEvents(List<PromoterEventModel> promoterEvents) {
        this.promoterEvents = promoterEvents;
    }

    public List<String> getTickets() {
        return tickets;
    }

    public void setTickets(List<String> tickets) {
        this.tickets = tickets;
    }

    public List<String> getHotels() {
        return hotels;
    }

    public void setHotels(List<String> hotels) {
        this.hotels = hotels;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public SizeModel getSize() {
        return size;
    }

    public void setSize(SizeModel size) {
        this.size = size;
    }

    @Override
    public int getIdentifier() {
        return id.hashCode();
    }

    @Override
    public boolean isValidModel() {
        return true;
    }


    public List<String> getVenues() {
        return venues == null ? new ArrayList<>() : venues;
    }

    public void setVenues(List<String> venues) {
        this.venues = venues;
    }

    public List<String> getYachts() {
        return yachts == null ? new ArrayList<>() : yachts;
    }

    public void setYachts(List<String> yachts) {
        this.yachts = yachts;
    }

    public List<String> getYachtOffers() {
        return yachtOffers == null ? new ArrayList<>() : yachtOffers;
    }

    public void setYachtOffers(List<String> yachtOffers) {
        this.yachtOffers = yachtOffers;
    }

    public List<String> getOffers() {
        return offers == null ? new ArrayList<>() : offers;
    }

    public void setOffers(List<String> offers) {
        this.offers = offers;
    }

    public List<String> getActivities() {
        return activities == null ? new ArrayList<>() : activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }

    public List<String> getEvents() {
        return events == null ? new ArrayList<>() : events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public List<String> getMembershipPackages() {
        return membershipPackages == null ? new ArrayList<>() : membershipPackages;
    }

    public void setMembershipPackages(List<String> membershipPackages) {
        this.membershipPackages = membershipPackages;
    }

    public List<InviteFriendModel> getMyOuting() {
        return myOuting == null ? new ArrayList<>() : myOuting;
    }

    public void setMyOuting(List<InviteFriendModel> myOuting) {
        this.myOuting = myOuting;
    }

    public List<UserDetailModel> getSuggestedUsers() {
        return suggestedUsers;
    }

    public void setSuggestedUsers(List<UserDetailModel> suggestedUsers) {
        this.suggestedUsers = suggestedUsers;
    }

    public List<VenueObjectModel> getSuggestedVenue() {
        return suggestedVenue;
    }

    public void setSuggestedVenue(List<VenueObjectModel> suggestedVenue) {
        this.suggestedVenue = suggestedVenue;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getApplicationStatus() {
        return Utils.notNullString(applicationStatus);
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public List<RaynaTicketDetailModel> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<RaynaTicketDetailModel> ticketList) {
        this.ticketList = ticketList;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getBanners() {
        return banners;
    }

    public void setBanners(List<String> banners) {
        this.banners = banners;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public List<String> getTicketCategories() {
        return ticketCategories;
    }

    public void setTicketCategories(List<String> ticketCategories) {
        this.ticketCategories = ticketCategories;
    }

    public List<ContactUsBlockModel> getContactUsBlock() {
        return contactUsBlock == null ? new ArrayList<>() : contactUsBlock;
    }

    public void setContactUsBlock(List<ContactUsBlockModel> contactUsBlock) {
        this.contactUsBlock = contactUsBlock;
    }
}

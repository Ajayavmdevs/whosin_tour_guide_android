package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import java.util.ArrayList;
import java.util.List;

public class VenueObjectModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("business_id")
    @Expose
    private String businessId = "";
    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("logo")
    @Expose
    private String logo = "";
    @SerializedName("cover")
    @Expose
    private String cover = "";
    @SerializedName("address")
    @Expose
    private String address = "";
    @SerializedName("stories")
    @Expose
    private List<StoryObjectModel> stories;
    @SerializedName("about")
    @Expose
    private String about = "";
    @SerializedName("phone")
    @Expose
    private String phone = "";
    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("email")
    @Expose
    private String email = "";
    @SerializedName("website")
    @Expose
    private String website = "";
    @SerializedName("booking_url")
    @Expose
    private String bookingUrl = "";
    @SerializedName("menu_url")
    @Expose
    private String menuUrl = "";
    @SerializedName("dress_code")
    @Expose
    private String dressCode = "";
    @SerializedName("discountText")
    @Expose
    private String discountText = "";
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";
    @SerializedName("distance")
    @Expose
    private double distance;
    @SerializedName("business")
    @Expose
    private BusinessModel business;
    @SerializedName("galleries")
    @Expose
    private List<String> galleries;
    @SerializedName("timings")
    @Expose
    private List<VenueTimingModel> timing;

    @SerializedName("deals")
    @Expose
    private List<ExclusiveDealModel> deals;

    @SerializedName("ratings")
    @Expose
    private List<Object> ratings;
    @SerializedName("themes")
    @Expose
    private List<String> theme;
    @SerializedName("music")
    @Expose
    private List<String> music;
    @SerializedName("features")
    @Expose
    private List<String> feature;
    @SerializedName("cuisines")
    @Expose
    private List<String> cuisine;
    @SerializedName("avg_ratings")
    @Expose
    private float avgRatings;
    @SerializedName("specialOffers")
    @Expose
    private List<SpecialOfferModel> specialOffers;
    @SerializedName("location")
    @Expose
    private LocationModel location;
    @SerializedName("currentUserReview")
    @Expose
    private CurrentUserRatingModel currentUserRating;
    @SerializedName("isOpen")
    private boolean isOpen;
    @SerializedName("isFollowing")
    @Expose
    private boolean isFollowing;
    @SerializedName("isAllowReview")
    @Expose
    private boolean isAllowReview;
    @SerializedName("isAllowRatting")
    @Expose
    private boolean isAllowRatting;
    @SerializedName("reviews")
    @Expose
    private List<CurrentUserRatingModel> reviews;
    @SerializedName("users")
    @Expose
    private List<ContactListModel> users;

    @SerializedName("lng")
    @Expose
    private double lng;

    @SerializedName("lat")
    @Expose
    private double lat;

    @SerializedName("isRecommendation")
    @Expose
    private boolean isRecommendation;

    @SerializedName("frequencyOfVisitForCm")
    @Expose
    private int frequencyOfVisitForCm = 0;


    private String eventID = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return Utils.notNullString(logo);
    }

    public String getSmallLogo() {
//        return Utils.addResolutionSuffix(getLogo(), "-150");
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCover() {
        String coverImage = Utils.notNullString(cover);
        if (coverImage.contains("-600")) {
            return coverImage;
        }
        return Utils.addResolutionSuffix(coverImage, "-600");
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAddress() {
        return Utils.notNullString(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<StoryObjectModel> getStories() {
        return stories == null ? new ArrayList<>() : stories;
    }

    public void setStories(List<StoryObjectModel> stories) {
        this.stories = stories;
    }


    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBookingUrl() {
        return bookingUrl;
    }

    public void setBookingUrl(String bookingUrl) {
        this.bookingUrl = bookingUrl;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getDressCode() {
        return Utils.notNullString(dressCode);
    }

    public void setDressCode(String dressCode) {
        this.dressCode = dressCode;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public BusinessModel getBusiness() {
        return business;
    }

    public void setBusiness(BusinessModel business) {
        this.business = business;
    }

    public List<String> getGalleries() {
        return galleries == null ? new ArrayList<>() : galleries;
    }

    public void setGalleries(List<String> galleries) {
        this.galleries = galleries;
    }

    public List<VenueTimingModel> getTiming() {
        return timing == null ? new ArrayList<>() : timing;
    }

    public void setTiming(List<VenueTimingModel> timing) {
        this.timing = timing;
    }

    public List<ExclusiveDealModel> getDeals() {
        return deals == null ? new ArrayList<>() : deals;
    }

    public void setDeals(List<ExclusiveDealModel> deals) {
        this.deals = deals;
    }

    public List<Object> getRatings() {
        return ratings == null ? new ArrayList<>() : ratings;
    }

    public void setRatings(List<Object> ratings) {
        this.ratings = ratings;
    }

    public List<String> getTheme() {
        return theme == null ? new ArrayList<>() : theme;
    }

    public void setTheme(List<String> theme) {
        this.theme = theme;
    }

    public List<String> getMusic() {
        return music == null ? new ArrayList<>() : music;
    }

    public void setMusic(List<String> music) {
        this.music = music;
    }

    public List<String> getFeature() {
        return feature == null ? new ArrayList<>() : feature;
    }

    public void setFeature(List<String> feature) {
        this.feature = feature;
    }

    public List<String> getCuisine() {
        return cuisine == null ? new ArrayList<>() : cuisine;
    }

    public void setCuisine(List<String> cuisine) {
        this.cuisine = cuisine;
    }

    public float getAvgRatings() {
        return avgRatings;
    }

    public void setAvgRatings(float avgRatings) {
        this.avgRatings = avgRatings;
    }

    public List<SpecialOfferModel> getSpecialOffers() {
        return specialOffers == null ? new ArrayList<>() : specialOffers;
    }

    public void setSpecialOffers(List<SpecialOfferModel> specialOffers) {
        this.specialOffers = specialOffers;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }


    public String getLatLng() {
        if (location == null || location.getCoordinates() == null) {
            return "";
        }
        return location.getCoordinates().size() < 2 ? "" : location.getCoordinates().get(1) + "," +
                location.getCoordinates().get(0);
    }

    public String getLongitude() {
        if (location == null || location.getCoordinates() == null) {
            return "";
        }
        return location.getCoordinates().size() < 2 ? "" : "" + location.getCoordinates().get(1);
    }

    public String getLatitude() {
        if (location == null || location.getCoordinates() == null) {
            return "";
        }
        return location.getCoordinates().size() < 2 ? "" : "" + location.getCoordinates().get(0);
    }

    public CurrentUserRatingModel getCurrentUserRating() {
        return currentUserRating == null ? new CurrentUserRatingModel() : currentUserRating;
    }

    public void setCurrentUserRating(CurrentUserRatingModel currentUserRating) {
        this.currentUserRating = currentUserRating;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public boolean isIsAllowReview() {
        return isAllowReview;
    }

    public void setIsAllowReview(boolean isAllowReview) {
        this.isAllowReview = isAllowReview;
    }

    public boolean isIsAllowRatting() {
        return isAllowRatting;
    }

    public void setIsAllowRatting(boolean isAllowRatting) {
        this.isAllowRatting = isAllowRatting;
    }

    public List<CurrentUserRatingModel> getReviews() {
        return reviews;
    }

    public void setReviews(List<CurrentUserRatingModel> reviews) {
        this.reviews = reviews;
    }

    public List<ContactListModel> getUsers() {
        return users;
    }

    public void setUsers(List<ContactListModel> users) {
        this.users = users;
    }


    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public boolean isAllowReview() {
        return isAllowReview;
    }

    public void setAllowReview(boolean allowReview) {
        isAllowReview = allowReview;
    }

    public boolean isAllowRatting() {
        return isAllowRatting;
    }

    public void setAllowRatting(boolean allowRatting) {
        isAllowRatting = allowRatting;
    }

    public boolean isRecommendation() {
        return isRecommendation;
    }

    public void setRecommendation(boolean recommendation) {
        isRecommendation = recommendation;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public String getDiscountText() {
        return discountText;
    }

    public void setDiscountText(String discountText) {
        this.discountText = discountText;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getFrequencyOfVisitForCm() {
        return frequencyOfVisitForCm;
    }

    public void setFrequencyOfVisitForCm(int frequencyOfVisitForCm) {
        this.frequencyOfVisitForCm = frequencyOfVisitForCm;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

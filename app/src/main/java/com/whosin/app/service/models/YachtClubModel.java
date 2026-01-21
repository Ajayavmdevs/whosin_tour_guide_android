package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

import java.util.ArrayList;
import java.util.List;

public class YachtClubModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("name")
    @Expose
    private String name ="";
    @SerializedName("about")
    @Expose
    private String about ="";
    @SerializedName("address")
    @Expose
    private String address ="";
    @SerializedName("location")
    @Expose
    private LocationModel location;
    @SerializedName("logo")
    @Expose
    private String logo;
    @SerializedName("cover")
    @Expose
    private String cover ="";
    @SerializedName("phone")
    @Expose
    private String phone = "";
    @SerializedName("email")
    @Expose
    private String email ="";
    @SerializedName("website")
    @Expose
    private String website ="";
    @SerializedName("booking_url")
    @Expose
    private String bookingUrl ="";
    @SerializedName("isAllowReview")
    @Expose
    private boolean isAllowReview;
    @SerializedName("features")
    @Expose
    private List<String> features;

    @SerializedName("timings")
    @Expose
    private List<VenueTimingModel> timings;
    @SerializedName("galleries")
    @Expose
    private List<String> galleries;
    @SerializedName("isAllowRating")
    @Expose
    private boolean isAllowRating;
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";
    @SerializedName("yachts")
    @Expose
    private List<YachtDetailModel> yachts;
    @SerializedName("offers")
    @Expose
    private List<YachtsOfferModel> offers;

    @SerializedName("currentUserReview")
    @Expose
    private CurrentUserRatingModel currentUserRating;

    @SerializedName("isAllowRatting")
    @Expose
    private boolean isAllowRatting;

    @SerializedName("isOpen")
    private boolean isOpen;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Utils.notNullString( name );
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getAddress() {
        return Utils.notNullString(  address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPhone() {
        return Utils.notNullString(phone  );
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

    public CurrentUserRatingModel getCurrentUserRating() {
        return currentUserRating == null ? new CurrentUserRatingModel() : currentUserRating;
    }

    public void setCurrentUserRating(CurrentUserRatingModel currentUserRating) {
        this.currentUserRating = currentUserRating;
    }

    public boolean isIsAllowRatting() {
        return isAllowRatting;
    }

    public void setIsAllowRatting(boolean isAllowRatting) {
        this.isAllowRatting = isAllowRatting;
    }


    public void setBookingUrl(String bookingUrl) {
        this.bookingUrl = bookingUrl;
    }

    public boolean isIsAllowReview() {
        return isAllowReview;
    }

    public void setIsAllowReview(boolean isAllowReview) {
        this.isAllowReview = isAllowReview;
    }

    public List<String> getFeatures() {
        return features == null ? new ArrayList<>() : features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }



    public List<VenueTimingModel> getTimings() {
        return timings == null ? new ArrayList<>() : timings;
    }

    public void setTimings(List<VenueTimingModel> timings) {
        this.timings = timings;
    }

    public List<String> getGalleries() {
        return galleries == null ? new ArrayList<>() : galleries;

    }

    public void setGalleries(List<String> galleries) {
        this.galleries = galleries;
    }

    public boolean isIsAllowRating() {
        return isAllowRating;
    }

    public void setIsAllowRating(boolean isAllowRating) {
        this.isAllowRating = isAllowRating;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public List<YachtDetailModel> getYachts() {
        return yachts == null ? new ArrayList<>() : yachts;
    }

    public void setYachts(List<YachtDetailModel> yachts) {
        this.yachts = yachts;
    }

    public List<YachtsOfferModel> getOffers() {
        return offers == null ? new ArrayList<>() : offers;


    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setOffers(List<YachtsOfferModel> offers) {
        this.offers = offers;
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

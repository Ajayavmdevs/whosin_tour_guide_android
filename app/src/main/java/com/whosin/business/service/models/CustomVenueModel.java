package com.whosin.business.service.models;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

public class CustomVenueModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("offerId")
    @Expose
    private String offerId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("subTitle")
    @Expose
    private String subTitle;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("info")
    @Expose
    private String info;
    @SerializedName("badge")
    @Expose
    private String badge;
    @SerializedName("offer")
    @Expose
    private OffersModel offer;

    @SerializedName("venueId")
    @Expose
    private String venueId;

    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;

    @SerializedName("image")
    @Expose
    private String  image;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("lng")
    @Expose
    private double lng;

    @SerializedName("lat")
    @Expose
    private double lat;

    @SerializedName("logo")
    @Expose
    private String logo = "";


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public VenueObjectModel getVenue() {
        return venue;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }


    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public OffersModel getOffer() {
        return offer;
    }

    public void setOffer(OffersModel offer) {
        this.offer = offer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getLogo() {
        if (TextUtils.isEmpty(logo)) return "";
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}

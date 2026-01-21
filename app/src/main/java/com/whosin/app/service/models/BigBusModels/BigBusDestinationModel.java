package com.whosin.app.service.models.BigBusModels;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class BigBusDestinationModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("country")
    @Expose
    private String country = "";

    @SerializedName("featured")
    @Expose
    private boolean featured = false;

    @SerializedName("coverImageUrl")
    @Expose
    private String coverImageUrl = "";

    @SerializedName("latitude")
    @Expose
    private float latitude;

    @SerializedName("twitterUrl")
    @Expose
    private float twitterUrl;

    @SerializedName("title")
    @Expose
    private String title = "";

    @SerializedName("default")
    @Expose
    private boolean jsonMemberDefault = false;

    @SerializedName("facebookUrl")
    @Expose
    private String facebookUrl = "";

    @SerializedName("videoUrl")
    @Expose
    private String videoUrl = "";

    @SerializedName("googleUrl")
    @Expose
    private String googleUrl = "";

    @SerializedName("youtubeUrl")
    @Expose
    private String youtubeUrl = "";

    @SerializedName("defaultCurrency")
    @Expose
    private String defaultCurrency = "";

    @SerializedName("contact")
    @Expose
    private BigBusContactModel contact;

    @SerializedName("bannerImageUrl")
    @Expose
    private String bannerImageUrl = "";

    @SerializedName("availableCurrencies")
    @Expose
    private List<String> availableCurrencies;

    @SerializedName("id")
    @Expose
    private String id = "";

    @SerializedName("brand")
    @Expose
    private BigBusBrandModel brand;

    @SerializedName("longitude")
    @Expose
    private float longitude;

    @SerializedName("address")
    @Expose
    private String address = "";

    @SerializedName("googlePlaceId")
    @Expose
    private String googlePlaceId = "";

    @SerializedName("shortDescription")
    @Expose
    private String shortDescription = "";

    @SerializedName("tripadvisorUrl")
    @Expose
    private String tripadvisorUrl = "";

    @SerializedName("tags")
    @Expose
    private List<String> tags;

    @SerializedName("notices")
    @Expose
    private List<Object> notices;

    @SerializedName("name")
    @Expose
    private String name = "";

    @SerializedName("instagramUrl")
    @Expose
    private String instagramUrl = "";

    public String getCountry() {
        return Utils.notNullString(country);
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public String getCoverImageUrl() {
        return Utils.notNullString(coverImageUrl);
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(float twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getTitle() {
        return Utils.notNullString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isJsonMemberDefault() {
        return jsonMemberDefault;
    }

    public void setJsonMemberDefault(boolean jsonMemberDefault) {
        this.jsonMemberDefault = jsonMemberDefault;
    }

    public String getFacebookUrl() {
        return Utils.notNullString(facebookUrl);
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getVideoUrl() {
        return Utils.notNullString(videoUrl);
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getGoogleUrl() {
        return Utils.notNullString(googleUrl);
    }

    public void setGoogleUrl(String googleUrl) {
        this.googleUrl = googleUrl;
    }

    public String getYoutubeUrl() {
        return Utils.notNullString(youtubeUrl);
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public String getDefaultCurrency() {
        return Utils.notNullString(defaultCurrency);
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public BigBusContactModel getContact() {
        return contact;
    }

    public void setContact(BigBusContactModel contact) {
        this.contact = contact;
    }

    public String getBannerImageUrl() {
        return Utils.notNullString(bannerImageUrl);
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }

    public List<String> getAvailableCurrencies() {
        return Utils.notEmptyList(availableCurrencies);
    }

    public void setAvailableCurrencies(List<String> availableCurrencies) {
        this.availableCurrencies = availableCurrencies;
    }

    public String getId() {
        return Utils.notNullString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigBusBrandModel getBrand() {
        return brand;
    }

    public void setBrand(BigBusBrandModel brand) {
        this.brand = brand;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return Utils.notNullString(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGooglePlaceId() {
        return Utils.notNullString(googlePlaceId);
    }

    public void setGooglePlaceId(String googlePlaceId) {
        this.googlePlaceId = googlePlaceId;
    }

    public String getShortDescription() {
        return Utils.notNullString(shortDescription);
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getTripadvisorUrl() {
        return Utils.notNullString(tripadvisorUrl);
    }

    public void setTripadvisorUrl(String tripadvisorUrl) {
        this.tripadvisorUrl = tripadvisorUrl;
    }

    public List<String> getTags() {
        return Utils.notEmptyList(tags);
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Object> getNotices() {
        return Utils.notEmptyList(notices);
    }

    public void setNotices(List<Object> notices) {
        this.notices = notices;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstagramUrl() {
        return Utils.notNullString(instagramUrl);
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
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
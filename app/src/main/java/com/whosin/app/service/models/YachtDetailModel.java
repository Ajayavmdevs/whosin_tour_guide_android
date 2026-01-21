package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

import java.util.ArrayList;
import java.util.List;

public class YachtDetailModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("yachtClubId")
    @Expose
    private String yachtClubId = "";
    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("images")
    @Expose
    private List<String> images;
    @SerializedName("year")
    @Expose
    private String year = "";
    @SerializedName("people")
    @Expose
    private String people = "";
    @SerializedName("cabins")
    @Expose
    private String cabins = "";
    @SerializedName("size")
    @Expose
    private String size = "";
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";
    @SerializedName("yachtClub")
    @Expose
    private YachtClubModel yachtClub;
    @SerializedName("features")
    @Expose
    private List<YachtFeatureModel> features;

    @SerializedName("specifications")
    @Expose
    private List<YachtsSpecificationModel> specifications;

    @SerializedName("location")
    @Expose
    private LocationModel location;

    @SerializedName("isDeleted")
    @Expose
    private boolean isDeleted;

    private String yachtOfferId = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYachtClubId() {
        return yachtClubId;
    }

    public void setYachtClubId(String yachtClubId) {
        this.yachtClubId = yachtClubId;
    }

    public String getName() {
        return Utils.notNullString( name );
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return Utils.notNullString( about );
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<String> getImages() {
        return images == null ? new ArrayList<>() : images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getCabins() {
        return cabins;
    }

    public void setCabins(String cabins) {
        this.cabins = cabins;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public YachtClubModel getYachtClub() {
        return yachtClub == null ? new YachtClubModel() : yachtClub;

    }

    public void setYachtClub(YachtClubModel yachtClub) {
        this.yachtClub = yachtClub;
    }

    public LocationModel getLocation() {
        return location == null ? new LocationModel() : location;
    }
    public void setLocation(LocationModel location) {
        this.location = location;
    }


    public String getLatLng() {
        if (location == null || location.getCoordinates() == null) {
            return "";
        }
        return location.getCoordinates().size() < 2 ? "" : location.getCoordinates().get( 1 ) + "," +
                location.getCoordinates().get( 0 );
    }

    public String getLongitude() {
        if (location == null || location.getCoordinates() == null) {
            return "";
        }
        return location.getCoordinates().size() < 2 ? "" : "" + location.getCoordinates().get( 1 );
    }

    public String getLatitude() {
        if (location == null || location.getCoordinates() == null) {
            return "";
        }
        return location.getCoordinates().size() < 2 ? "" : "" + location.getCoordinates().get( 0 );
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public List<YachtFeatureModel> getFeatures() {
        return features != null ? features : new ArrayList<>();
    }

    public void setFeatures(List<YachtFeatureModel> features) {
        this.features = features;
    }


    public List<YachtsSpecificationModel> getSpecifications() {
        return (specifications == null) ? new ArrayList<>() : specifications;
    }

    public void setSpecifications(List<YachtsSpecificationModel> specifications) {
        this.specifications = specifications;
    }

    public String getYachtOfferId() {
        return yachtOfferId;
    }

    public void setYachtOfferId(String yachtOfferId) {
        this.yachtOfferId = yachtOfferId;
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

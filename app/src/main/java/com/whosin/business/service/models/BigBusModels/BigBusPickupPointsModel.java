package com.whosin.business.service.models.BigBusModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class BigBusPickupPointsModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("country")
	@Expose
    private String country = "";

    @SerializedName("address")
	@Expose
    private String address = "";

    @SerializedName("googlePlaceId")
	@Expose
    private String googlePlaceId = "";

    @SerializedName("latitude")
	@Expose
    private Float latitude;

    @SerializedName("postalCode")
	@Expose
    private Object postalCode;

    @SerializedName("locality")
	@Expose
    private String locality = "";

    @SerializedName("directions")
	@Expose
    private String directions = "";

    @SerializedName("street")
	@Expose
    private String street = "";

    @SerializedName("name")
	@Expose
    private String name = "";

    @SerializedName("id")
	@Expose
    private String id = "";

    @SerializedName("state")
	@Expose
    private String state = "";

    @SerializedName("region")
    @Expose
    private String region = "";

    @SerializedName("longitude")
    @Expose
    private Float longitude;


    public String getCountry() {
        return Utils.notNullString(country);
    }

    public void setCountry(String country) {
        this.country = country;
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

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Object getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Object postalCode) {
        this.postalCode = postalCode;
    }

    public String getLocality() {
        return Utils.notNullString(locality);
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getDirections() {
        return Utils.notNullString(directions);
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public String getStreet() {
        return Utils.notNullString(street);
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return Utils.notNullString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return Utils.notNullString(state);
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRegion() {
        return Utils.notNullString(region);
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
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
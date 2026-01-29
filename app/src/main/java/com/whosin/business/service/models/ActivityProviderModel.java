package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

public class ActivityProviderModel implements DiffIdentifier, ModelProtocol{
    @SerializedName("_id")
    @Expose
    private String id="";
    @SerializedName("name")
    @Expose
    private String name="";
    @SerializedName("logo")
    @Expose
    private String logo="";
    @SerializedName("address")
    @Expose
    private String address="";
    @SerializedName("email")
    @Expose
    private String email="";
    @SerializedName("phone")
    @Expose
    private String phone="";

    @SerializedName("location")
    @Expose
    private LocationModel location;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAddress() {
        return Utils.notNullString(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return false;
    }
}

package com.whosin.app.service.models.rayna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.service.models.ModelProtocol;

public class RaynaPassengerModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("serviceType")
    @Expose
    private String serviceType;

    @SerializedName("prefix")
    @Expose
    private String prefix;

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("nationality")
    @Expose
    private String nationality;

    @SerializedName("pickup")
    @Expose
    private String pickup;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("leadPassenger")
    @Expose
    private Object leadPassenger;

    @SerializedName("paxType")
    @Expose
    private String paxType;

    @SerializedName("countryCode")
    @Expose
    private String countryCode;

    public RaynaPassengerModel() {
    }

    public RaynaPassengerModel(String serviceType, String prefix, String firstName, String lastName, String email, String mobile, String nationality, String pickup, String message, Object leadPassenger, String paxType,String countryCode) {
        this.serviceType = serviceType;
        this.prefix = prefix;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.nationality = nationality;
        this.pickup = pickup;
        this.message = message;
        this.leadPassenger = leadPassenger;
        this.paxType = paxType;
        this.countryCode = countryCode;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getLeadPassenger() {
        return leadPassenger;
    }

    public void setLeadPassenger(int leadPassenger) {
        this.leadPassenger = leadPassenger;
    }

    public String getPaxType() {
        return paxType;
    }

    public void setPaxType(String paxType) {
        this.paxType = paxType;
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

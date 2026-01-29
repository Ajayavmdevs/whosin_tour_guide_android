package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserTokenModel implements ModelProtocol{

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("userDetail")
    @Expose
    private UserDetailModel userDetail;

    @SerializedName("isAuthenticationPending")
    @Expose
    private boolean isAuthenticationPending;

    @SerializedName("loginType")
    @Expose
    private String loginType = "";

    @SerializedName("promoterId")
    @Expose
    private String promoterId = "";

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("userId")
    @Expose
    private String userId = "";

    @SerializedName("phone")
    @Expose
    private String phone = "";
    @SerializedName("country_code")
    @Expose
    private String countryCode = "";
    @SerializedName("first_name")
    @Expose
    private String firstName = "";
    @SerializedName("last_name")
    @Expose
    private String lastName = "";
    @SerializedName("gender")
    @Expose
    private String gender = "";
    @SerializedName("image")
    @Expose
    private String image = "";
    @SerializedName("email")
    @Expose
    private String email = "";

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDetailModel getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetailModel userDetail) {
        this.userDetail = userDetail;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(String promoterId) {
        this.promoterId = promoterId;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public boolean isAuthenticationPending() {
        return isAuthenticationPending;
    }

    public void setAuthenticationPending(boolean authenticationPending) {
        isAuthenticationPending = authenticationPending;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

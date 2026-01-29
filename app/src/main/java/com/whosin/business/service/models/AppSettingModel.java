package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import java.util.ArrayList;
import java.util.List;

public class AppSettingModel implements DiffIdentifier, ModelProtocol {


    @SerializedName("gender")
    @Expose
    private List<String> gender;
    @SerializedName("countries")
    @Expose
    private List<CountryModel> countries;
    @SerializedName("pages")
    @Expose
    private List<PageModel> pages;
    @SerializedName("cuisine")
    @Expose
    private List<AppSettingTitelCommonModel> cuisine;
    @SerializedName("music")
    @Expose
    private List<AppSettingTitelCommonModel> music;
    @SerializedName("feature")
    @Expose
    private List<AppSettingTitelCommonModel> feature;
    @SerializedName("themes")
    @Expose
    private List<AppSettingTitelCommonModel> themes;
    @SerializedName("categories")
    @Expose
    private List<AppSettingTitelCommonModel> categories;

    @SerializedName("membershipPackage")
    @Expose
    private List<MemberShipPackageModel> membershipPackage;

    @SerializedName("activityType")
    @Expose
    private List<AppSettingActivityTypeModel> activityType;

    @SerializedName("loginRequests")
    @Expose
    private List<LoginRequestModel> loginRequests;

    @SerializedName("allowTabbyPayments")
    @Expose
    private boolean allowTabbyPayments ;

    @SerializedName("allowStripePayments")
    @Expose
    private boolean allowStripePayments ;

    @SerializedName("forceUpdate")
    @Expose
    private boolean forceUpdate = false;

    @SerializedName("currencies")
    @Expose
    private List<CurrencyModel> currencies;

    @SerializedName("languages")
    @Expose
    private List<LanguagesModel> languages;

    public List<LanguagesModel> getLanguages() {
        return Utils.notEmptyList(languages);
    }

    public void setLanguages(List<LanguagesModel> languages) {
        this.languages = languages;
    }

    public List<CurrencyModel> getCurrencies() {
        return Utils.notEmptyList(currencies);
    }

    public void setCurrencies(List<CurrencyModel> currencies) {
        this.currencies = currencies;
    }

    public List<String> getGender() {
        if (gender == null){
            return new ArrayList<>();
        }
        return gender;
    }

    public void setGender(List<String> gender) {
        this.gender = gender;
    }

    public List<CountryModel> getCountries() {
        if (countries == null){
            return new ArrayList<>();
        }
        return countries;
    }

    public void setCountries(List<CountryModel> countries) {
        this.countries = countries;
    }

    public List<PageModel> getPages() {
        if (pages == null){
            return new ArrayList<>();
        }
        return pages;
    }

    public void setPages(List<PageModel> pages) {
        this.pages = pages;
    }

    public List<AppSettingTitelCommonModel> getCuisine() {
        if (cuisine == null){
            return new ArrayList<>();
        }
        return cuisine;
    }

    public void setCuisine(List<AppSettingTitelCommonModel> cuisine) {
        this.cuisine = cuisine;
    }

    public List<AppSettingTitelCommonModel> getMusic() {
        if (music == null){
            return new ArrayList<>();
        }
        return music;
    }

    public void setMusic(List<AppSettingTitelCommonModel> music) {
        this.music = music;
    }

    public List<AppSettingTitelCommonModel> getFeature() {
        if (feature == null){
            return new ArrayList<>();
        }
        return feature;
    }

    public void setFeature(List<AppSettingTitelCommonModel> feature) {
        this.feature = feature;
    }

    public List<MemberShipPackageModel> getMembershipPackage() {
        if (membershipPackage == null){
            return new ArrayList<>();
        }
        return membershipPackage;
    }

    public void setMembershipPackage(List<MemberShipPackageModel> membershipPackage) {
        this.membershipPackage = membershipPackage;
    }

    public List<AppSettingActivityTypeModel> getActivityType() {
        if (activityType == null){
            return new ArrayList<>();
        }
        return activityType;
    }

    public void setActivityType(List<AppSettingActivityTypeModel> activityType) {
        this.activityType = activityType;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public List<LoginRequestModel> getLoginRequests() {

        return loginRequests == null ? new ArrayList<>() : loginRequests;
    }

    public void setLoginRequests(List<LoginRequestModel> loginRequests) {
        this.loginRequests = loginRequests;
    }

    public List<AppSettingTitelCommonModel> getThemes() {
        return themes;
    }

    public void setThemes(List<AppSettingTitelCommonModel> themes) {
        this.themes = themes;
    }

    public List<AppSettingTitelCommonModel> getCategories() {
        return categories;
    }

    public void setCategories(List<AppSettingTitelCommonModel> categories) {
        this.categories = categories;
    }

    public boolean isAllowTabbyPayments() {
        return allowTabbyPayments;
    }

    public void setAllowTabbyPayments(boolean allowTabbyPayments) {
        this.allowTabbyPayments = allowTabbyPayments;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public boolean isAllowStripePayments() {
        return allowStripePayments;
    }

    public void setAllowStripePayments(boolean allowStripePayments) {
        this.allowStripePayments = allowStripePayments;
    }
}

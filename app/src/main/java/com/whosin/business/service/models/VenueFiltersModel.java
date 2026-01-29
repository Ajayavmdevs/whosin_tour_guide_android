package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class VenueFiltersModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("cuisines")
    @Expose
    private List<AppSettingTitelCommonModel> cuisine;
    @SerializedName("music")
    @Expose
    private List<AppSettingTitelCommonModel> music;
    @SerializedName("features")
    @Expose
    private List<AppSettingTitelCommonModel> feature;
    @SerializedName("themes")
    @Expose
    private List<AppSettingTitelCommonModel> themes;
    @SerializedName("categories")
    @Expose
    private List<AppSettingTitelCommonModel> categories;


    public List<AppSettingTitelCommonModel> getCuisine() {
        if (cuisine == null) {
            return new ArrayList<>();
        }
        return cuisine;
    }

    public void setCuisine(List<AppSettingTitelCommonModel> cuisine) {
        this.cuisine = cuisine;
    }

    public List<AppSettingTitelCommonModel> getMusic() {
        if (music == null) {
            return new ArrayList<>();
        }
        return music;
    }

    public void setMusic(List<AppSettingTitelCommonModel> music) {
        this.music = music;
    }

    public List<AppSettingTitelCommonModel> getFeature() {
        if (feature == null) {
            return new ArrayList<>();
        }
        return feature;
    }

    public void setFeature(List<AppSettingTitelCommonModel> feature) {
        this.feature = feature;
    }


    public List<AppSettingTitelCommonModel> getThemes() {
        if (themes == null) {
            return new ArrayList<>();
        }
        return themes;
    }

    public void setThemes(List<AppSettingTitelCommonModel> themes) {
        this.themes = themes;
    }

    public List<AppSettingTitelCommonModel> getCategories() {
        if (categories == null) {
            return new ArrayList<>();
        }
        return categories;
    }

    public void setCategories(List<AppSettingTitelCommonModel> categories) {
        this.categories = categories;
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

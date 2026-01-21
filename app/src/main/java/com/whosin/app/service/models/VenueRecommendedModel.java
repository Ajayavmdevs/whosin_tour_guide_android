package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class VenueRecommendedModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("category")
    @Expose
    private List<CategoriesModel> category;

    @SerializedName("deals")
    @Expose
    private List<ExclusiveDealModel> deals;

    @SerializedName("venues")
    @Expose
    private List<VenueObjectModel> venues;



    public AppConstants.SearchHomeType getBlockType() {
        switch (getType()) {
            case "category": return AppConstants.SearchHomeType.CATEGORIES;
            case "deals": return AppConstants.SearchHomeType.DEALS;
            case "venue": return AppConstants.SearchHomeType.VENUE;
            default:
                return AppConstants.SearchHomeType.NONE;
        }
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<CategoriesModel> getCategory() {
        return category;
    }

    public void setCategory(List<CategoriesModel> category) {
        this.category = category;
    }

    public List<ExclusiveDealModel> getDeals() {
        return deals;
    }

    public void setDeals(List<ExclusiveDealModel> deals) {
        this.deals = deals;
    }

    public List<VenueObjectModel> getVenues() {
        if (venues == null) {
            return new ArrayList<>();
        }
        return venues;    }

    public void setVenues(List<VenueObjectModel> venues) {
        this.venues = venues;
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

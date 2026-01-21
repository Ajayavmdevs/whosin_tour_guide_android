package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class VenueOfferDetailsModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("total_offers")
    @Expose
    private int totalOffers;
    @SerializedName("offers")
    @Expose
    private List<OffersModel> offers;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalOffers() {
        return totalOffers;
    }

    public void setTotalOffers(int totalOffers) {
        this.totalOffers = totalOffers;
    }

    public List<OffersModel> getOffers() {
        return offers;
    }

    public void setOffers(List<OffersModel> offers) {
        this.offers = offers;
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

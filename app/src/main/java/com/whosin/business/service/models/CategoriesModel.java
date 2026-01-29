package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import java.util.ArrayList;
import java.util.List;

public class CategoriesModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id="";
    @SerializedName("title")
    @Expose
    private String title="";
    @SerializedName("subTitle")
    @Expose
    private String subTitle="";
    @SerializedName("image")
    @Expose
    private String image="";
    @SerializedName("color")
    @Expose
    private ColorModel color;
    @SerializedName("createdAt")
    @Expose
    private String createdAt="";

    @SerializedName("banners")
    @Expose
    private List<BannerModel> banners;
    @SerializedName("deals")
    @Expose
    private List<VoucherModel> deals;

    @SerializedName("offers")
    @Expose
    private int offers;

    @SerializedName("name")
    @Expose
    private String name;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ColorModel getColor() {
        if (color == null){
            return new ColorModel();
        }
        return color;
    }

    public void setColor(ColorModel color) {
        this.color = color;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public List<BannerModel> getBanners() {
        if (banners == null){
            return new ArrayList<>();
        }
        return banners;
    }

    public void setBanners(List<BannerModel> banners) {
        this.banners = banners;
    }

    public List<VoucherModel> getDeals() {
        if (deals == null){
            return new ArrayList<>();
        }
        return deals;
    }


    public void setDeals(List<VoucherModel> deals) {
        this.deals = deals;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOffers() {
        return offers;
    }

    public void setOffers(int offers) {
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

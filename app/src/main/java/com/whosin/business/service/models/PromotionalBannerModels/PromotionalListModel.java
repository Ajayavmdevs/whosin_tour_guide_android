package com.whosin.business.service.models.PromotionalBannerModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.BannerModel;
import com.whosin.business.service.models.ModelProtocol;
import com.whosin.business.service.models.SizeModel;

import java.util.List;

public class PromotionalListModel  implements DiffIdentifier, ModelProtocol {

    @SerializedName("size")
    @Expose
    private SizeModel size;

    @SerializedName("startDate")
    @Expose
    private String startDate;

    @SerializedName("endDate")
    @Expose
    private String endDate;

    @SerializedName("banners")
    @Expose
    private List<BannerModel> banners;

    public SizeModel getSize() {
        return size;
    }

    public void setSize(SizeModel size) {
        this.size = size;
    }

    public String getStartDate() {
        return Utils.notNullString(startDate);
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return Utils.notNullString(endDate);
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<BannerModel> getBanners() {
        return Utils.notEmptyList(banners);
    }

    public void setBanners(List<BannerModel> banners) {
        this.banners = banners;
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

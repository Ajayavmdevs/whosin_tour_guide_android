package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import java.util.ArrayList;
import java.util.List;

public class YachtsOfferModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("yachtId")
    @Expose
    private String yachtId ="";
    @SerializedName("title")
    @Expose
    private String title ="";
    @SerializedName("description")
    @Expose
    private String description ="";
    @SerializedName("startDate")
    @Expose
    private String startDate ="";
    @SerializedName("endDate")
    @Expose
    private String endDate ="";
    @SerializedName("needToKnow")
    @Expose
    private String needToKnow;
    @SerializedName("importantNotice")
    @Expose
    private String importantNotice;
    @SerializedName("disclaimer")
    @Expose
    private String disclaimer;
    @SerializedName("images")
    @Expose
    private List<String> images;
    @SerializedName("startingAmount")
    @Expose
    private int startingAmount = 0;
    @SerializedName("addOns")
    @Expose
    private List<YachtAddOnModel> addOns;
    @SerializedName("isExpired")
    @Expose
    private String isExpired = "";
    @SerializedName("packageType")
    @Expose
    private String packageType;
    @SerializedName("packages")
    @Expose
    private List<YachtPackageModel> packages;
    @SerializedName("yacht")
    @Expose
    private YachtDetailModel yacht;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYachtId() {
        return yachtId;
    }

    public void setYachtId(String yachtId) {
        this.yachtId = yachtId;
    }

    public String getTitle() {
        return Utils.notNullString( title );
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return Utils.notNullString(description  ) ;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getNeedToKnow() {
        return Utils.notNullString(needToKnow);
    }

    public void setNeedToKnow(String needToKnow) {
        this.needToKnow = needToKnow;
    }

    public String getImportantNotice() {
        return importantNotice;
    }

    public void setImportantNotice(String importantNotice) {
        this.importantNotice = importantNotice;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public List<String> getImages() {
        return images == null ? new ArrayList<>() : images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getStartingAmount() {
        return startingAmount;
    }

    public void setStartingAmount(int startingAmount) {
        this.startingAmount = startingAmount;
    }

    public List<YachtAddOnModel> getAddOns() {
        return addOns == null ? new ArrayList<>() : addOns;
    }

    public void setAddOns(List<YachtAddOnModel> addOns) {
        this.addOns = addOns;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public YachtDetailModel getYacht() {
        return yacht == null ? new YachtDetailModel() : yacht;
    }

    public void setYacht(YachtDetailModel yacht) {
        this.yacht = yacht;
    }

    public String getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(String isExpired) {
        this.isExpired = isExpired;
    }

    public List<YachtPackageModel> getPackages() {
        return (packages == null) ? new ArrayList<>() : packages;

    }

    public void setPackages(List<YachtPackageModel> packages) {
        this.packages = packages;
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

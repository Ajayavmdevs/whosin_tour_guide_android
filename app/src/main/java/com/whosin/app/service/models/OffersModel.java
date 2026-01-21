package com.whosin.app.service.models;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OffersModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("categoryId")
    @Expose
    private String categoryId = "";
    @SerializedName("offerId")
    @Expose
    private String offerId = "";
    @SerializedName("title")
    @Expose
    private String title ;
    @SerializedName("dimension")
    @Expose
    private String dimension = "";
    @SerializedName("description")
    @Expose
    private String description ;
    @SerializedName("image")
    @Expose
    private String image = "";
    @SerializedName("days")
    @Expose
    public String days = "";
    @SerializedName("startTime")
    @Expose
    private String startTime = "";
    @SerializedName("endTime")
    @Expose
    private String endTime = "";
    @SerializedName("status")
    @Expose
    private String status = "";
    @SerializedName("discount")
    @Expose
    private String discount = "";
    @SerializedName("disclaimerTitle")
    @Expose
    private String disclaimerTitle = "";
    @SerializedName("disclaimerDescription")
    @Expose
    private String disclaimerDescription = "";
    @SerializedName("allowWhosIn")
    @Expose
    private boolean allowWhosIn = false;
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";
    @SerializedName("packages")
    @Expose
    private List<PackageModel> packages;

    @SerializedName("package")
    @Expose
    private PackageModel _package;

    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;
    @SerializedName("qty")
    @Expose
    private Integer qty = 0;

    @SerializedName("isRecommendation")
    @Expose
    private boolean isRecommendation ;

    @SerializedName("specialOffer")
    @Expose
    private SpecialOfferModel specialOfferModel ;

    @SerializedName("discountTag")
    @Expose
    private String discountTag ;

    private Boolean isAvailableForBuy;

    public String getId() {
        return Utils.notNullString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOfferId() {
        return Utils.notNullString(offerId);
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }
    public String getTitle() {
        return Utils.notNullString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return Utils.notNullString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        String coverImage = Utils.notNullString(image);
        return Utils.addResolutionSuffix(coverImage, "-600");
    }

    public String getImage300() {
        String coverImage = Utils.notNullString(image);
        String imageUrl = Utils.addResolutionSuffix(coverImage, "-300");
        Log.d("TAG", "getImage300: " + imageUrl + "  ID : " + id);
        return imageUrl;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public PackageModel get_package() {
        return _package;
    }

    public void set_package(PackageModel _package) {
        this._package = _package;
    }



    public void setImage(String image) {
        this.image = image;
    }

    public String getDays() {
        if (TextUtils.isEmpty(days)) { return ""; }
        String[] dayArray = days.split(",");
        if (dayArray.length == 1) {
            return dayArray[0].trim().substring(0, 1).toUpperCase() + dayArray[0].trim().substring(1).toLowerCase();
        } else if (dayArray.length == 7) {
            return "All days";
        }
        else {
            StringBuilder formattedDays = new StringBuilder();
            for (int i = 0; i < dayArray.length; i++) {
                String day = dayArray[i].trim();
                String capitalizedDay = day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase();
                if (i == 0) {
                    formattedDays.append(capitalizedDay);
                } else {
                    formattedDays.append(", ").append(capitalizedDay);
                }
            }
            return formattedDays.toString();
        }

    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getStartTime() {
        return Utils.notNullString(  startTime);
    }

    public boolean isShowTimeInfo() {
        return TextUtils.isEmpty(startTime);
    }

    public String getOfferTiming() {
        if (getStartTime().isEmpty()) {
            if (getVenue() == null) {
                return "any time";
            }
            else {
                List<String> dayArray = Arrays.asList(days.split(","));
                List<VenueTimingModel> timings = venue.getTiming();
                if (!dayArray.isEmpty()) {
                    timings = timings.stream().filter(p -> dayArray.contains(p.getDay().toLowerCase())).collect(Collectors.toList());
                }
                if (!timings.isEmpty()) {
                    VenueTimingModel smallObject = timings.stream().min(Comparator.comparing(VenueTimingModel::getOpeningTime)).orElse(null);
                    if (smallObject != null) {
                        return smallObject.getOpeningTime() + " - " + smallObject.getClosingTime();
                        //return Utils.convertDateFormat(smallObject.getOpeningTime(), "HH:mm", "hh:mm a") + " - " + Utils.convertDateFormat(smallObject.getClosingTime(), "HH:mm", "hh:mm a");
                    } else {
                        return " - ";
                    }
                }
                else {
                    return " - ";
                }
            }
        }
        return Utils.convertMainTimeFormat(getStartTime()) + " - " + Utils.convertMainTimeFormat(getEndTime());
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return Utils.notNullString(endTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isAllowWhosIn() {
        return allowWhosIn;
    }

    public void setAllowWhosIn(boolean allowWhosIn) {
        this.allowWhosIn = allowWhosIn;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<PackageModel> getPackages() {
        return packages == null ? new ArrayList<>() : packages;
    }

    public void setPackages(List<PackageModel> packages) {
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

    public VenueObjectModel getVenue() {
        return venue;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }


    public boolean isRecommendation() {
        return isRecommendation;
    }

    public void setRecommendation(boolean recommendation) {
        isRecommendation = recommendation;
    }

    public String getDisclaimerTitle() {
        return disclaimerTitle;
    }

    public void setDisclaimerTitle(String disclaimerTitle) {
        this.disclaimerTitle = disclaimerTitle;
    }

    public String getDisclaimerDescription() {
        return disclaimerDescription;
    }

    public void setDisclaimerDescription(String disclaimerDescription) {
        this.disclaimerDescription = disclaimerDescription;
    }

    public SpecialOfferModel getSpecialOfferModel() {
        return specialOfferModel;
    }

    public void setSpecialOfferModel(SpecialOfferModel specialOfferModel) {
        this.specialOfferModel = specialOfferModel;
    }

    public boolean isAvailableToBuy(){
        if (isAvailableForBuy == null) {
            if (isExpired() || getPackages().isEmpty()) {
                isAvailableForBuy = false;
            } else {
                List<PackageModel> list = getPackages().stream().filter(PackageModel::isAllowSale).collect(Collectors.toList());
                if (list.isEmpty()) {
                    isAvailableForBuy = false;
                } else {
                    List<PackageModel> notSoldList = list.stream().filter(p -> p.getRemainingQty() > 0).collect(Collectors.toList());
                    isAvailableForBuy = !notSoldList.isEmpty();
                }
            }
        }
        return isAvailableForBuy;
    }

    public boolean isSpecialOffer(){
        return specialOfferModel != null;
    }

    public boolean isExpired() {
        if (TextUtils.isEmpty(getEndTime())) { return false; }
        boolean isFutureDate = Utils.isFutureDate(getEndTime(), AppConstants.DATEFORMAT_LONG_TIME);
        return !isFutureDate;
    }

    public String getDiscountTag() {
        return Utils.notNullString(discountTag);
    }

    public void setDiscountTag(String discountTag) {
        this.discountTag = discountTag;
    }

    public List<VenueTimingModel> getTiming() {
//        return timing == null ? new ArrayList<>() : timing;
        if (getVenue() != null && !getVenue().getTiming().isEmpty()) {
            List<String> dayArray = Arrays.asList(days.split(","));
            if (!dayArray.isEmpty()) {
                List<VenueTimingModel> timings = venue.getTiming();
                return timings.stream().filter(p -> dayArray.contains(p.getDay().toLowerCase())).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
}

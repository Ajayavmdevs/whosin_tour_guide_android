package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

import java.util.List;

public class ExclusiveDealModel implements DiffIdentifier,ModelProtocol {


    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("description")
    @Expose
    private String description = "";
    @SerializedName("venueId")
    @Expose
    private String venueId ="";

    @SerializedName("image")
    @Expose
    private String image="";
    @SerializedName("vouchars")
    @Expose
    private List<VoucherModel> vouchars;
    @SerializedName("discountValue")
    @Expose
    private String discountValue ="";
    @SerializedName("originalPrice")
    @Expose
    private int  originalPrice;
    @SerializedName("startDate")
    @Expose
    private String startDate ="";
    @SerializedName("endDate")
    @Expose
    private String endDate ="";

    @SerializedName("startTime")
    @Expose
    private String startTime = "";

    @SerializedName("disclaimerTitle")
    @Expose
    private String disclaimerTitle = "";
    @SerializedName("disclaimerDescription")
    @Expose
    private String disclaimerDescription = "";

    @SerializedName("paxPerVoucher")
    @Expose
    private String paxPerVoucher="";

    @SerializedName("actualPrice")
    @Expose
    private int actualPrice;

    @SerializedName("endTime")
    @Expose
    private String endTime ="";
    @SerializedName("createdAt")
    @Expose
    private String createdAt ="";
    @SerializedName("status")
    @Expose
    private String status ="";
    @SerializedName("categoryId")
    @Expose
    private String categoryId = "";
    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;
    @SerializedName("discountedPrice")
    @Expose
    private int discountedPrice = 0;

    @SerializedName("features")
    @Expose
    private List<ActivityAvailableFeatureModel> features;

    @SerializedName("days")
    @Expose
    private String days;


    public int getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(int actualPrice) {
        this.actualPrice = actualPrice;
    }



    public String getDays() {
        if (days != null && !days.isEmpty()) {
            String[] dayArray = days.split(",");

            if (dayArray.length == 1) {
                // Only one day, return it without formatting
                return dayArray[0].trim().substring(0, 1).toUpperCase() + dayArray[0].trim().substring(1).toLowerCase();
            } else if (dayArray.length == 7) {
                // All days are selected
                return "All days";
            } else if (days.equalsIgnoreCase("sat,sun")) {
                return "Weekend";
            } else {
                StringBuilder formattedDays = new StringBuilder();

                for (int i = 0; i < dayArray.length; i++) {
                    String day = dayArray[i].trim();
                    String capitalizedDay = day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase();

                    if (i == 0) {
                        // First day, just append without a comma
                        formattedDays.append(capitalizedDay);
                    } else {
                        // Subsequent days, append with a comma
                        formattedDays.append(", ").append(capitalizedDay);
                    }
                }

                return formattedDays.toString();
            }
        } else {
            return "";
        }
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    public String getPaxPerVoucher() {
        return paxPerVoucher;
    }

    public void setPaxPerVoucher(String paxPerVoucher) {
        this.paxPerVoucher = paxPerVoucher;
    }
    public List<ActivityAvailableFeatureModel> getFeatures() {
        return features;
    }

    public void setFeatures(List<ActivityAvailableFeatureModel> features) {
        this.features = features;
    }


    public String getId() {
        return Utils.notNullString(id);
     }

    public void setId(String id) {
        this.id = id;
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

    public String getEndTime() {
        return Utils.notNullString(endTime);
     }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenueId() {
        return Utils.notNullString(venueId);

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

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public List<VoucherModel> getVouchars() {
        return Utils.notEmptyList(vouchars);
     }

    public void setVouchars(List<VoucherModel> vouchars) {
        this.vouchars = vouchars;
    }

    public String getDiscountValue() {
        return Utils.notNullString(discountValue);
     }

    public String getImage() {
        return Utils.notNullString(image);
     }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
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


    public String getCreatedAt() {
        return Utils.notNullString(createdAt);
     }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCategoryId() {
        return Utils.notNullString(categoryId);

    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public VenueObjectModel getVenue() {
        return venue;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
    }


    public int getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    public String getStatus() {
        return Utils.notNullString(status);
     }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}

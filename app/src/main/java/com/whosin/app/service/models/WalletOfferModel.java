package com.whosin.app.service.models;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import okio.Utf8;

public class WalletOfferModel implements DiffIdentifier, ModelProtocol {
    @SerializedName("_id")
    @Expose
    private String id="";
    @SerializedName("categoryId")
    @Expose
    private String categoryId="";
    @SerializedName("title")
    @Expose
    private String title="";
    @SerializedName("description")
    @Expose
    private String description="";
    @SerializedName("image")
    @Expose
    private String image="";
    @SerializedName("dimension")
    @Expose
    private String dimension="";
    @SerializedName("days")
    @Expose
    private String days="";
    @SerializedName("startTime")
    @Expose
    private String startTime="";
    @SerializedName("endTime")
    @Expose
    private String endTime="";
    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("discount")
    @Expose
    private String discount="";
    @SerializedName("allowWhosIn")
    @Expose
    private Boolean allowWhosIn;
    @SerializedName("createdAt")
    @Expose
    private String createdAt="";

    public boolean isShowTimeInfo() {
        return TextUtils.isEmpty(startTime);
    }
    @SerializedName("packages")
    @Expose
    private List<PackageModel> packages;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
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
//        return days;
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
                    return smallObject.getOpeningTime() + " - " + smallObject.getClosingTime();
                }
                else {
                    return " - ";
                }
            }
        }
        return Utils.convertMainTimeFormat(getStartTime()) + " - " + Utils.convertMainTimeFormat(getEndTime());
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

    public String getEndTime() {
        return Utils.notNullString(endTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public VenueObjectModel getVenue() {
        return venue;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Boolean getAllowWhosIn() {
        return allowWhosIn;
    }

    public void setAllowWhosIn(Boolean allowWhosIn) {
        this.allowWhosIn = allowWhosIn;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<PackageModel> getPackages() {
        return Utils.notEmptyList(packages);
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
}

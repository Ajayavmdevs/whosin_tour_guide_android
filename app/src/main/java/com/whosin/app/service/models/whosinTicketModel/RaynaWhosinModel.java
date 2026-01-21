package com.whosin.app.service.models.whosinTicketModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;
import com.whosin.app.service.models.rayna.RaynaTimeSlotModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RaynaWhosinModel  implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private int id;

    @SerializedName("customTicketId")
    @Expose
    private int customTicketId;

    @SerializedName("title")
    @Expose
    private String title = "";

    @SerializedName("description")
    @Expose
    private String description = "";

    @SerializedName("images")
    @Expose
    private List<String> images;

    @SerializedName("days")
    @Expose
    private List<String> days;

    @SerializedName("startDate")
    @Expose
    private String startDate = "";

    @SerializedName("endDate")
    @Expose
    private String endDate = "";

    @SerializedName("availabilityType")
    @Expose
    private String availabilityType = "";

    @SerializedName("availabilityTime")
    @Expose
    private String availabilityTime = "";

    @SerializedName("totalSeats")
    @Expose
    private int totalSeats;

    @SerializedName("availabilityTimeSlot")
    @Expose
    private List<String> availabilityTimeSlot;

    @SerializedName("amount")
    @Expose
    private Float amount = 0f;

    @SerializedName("amountForChild")
    @Expose
    private Float amountForChild = 0f;

    @SerializedName("amountForChildInfant")
    @Expose
    private Float amountForInfants = 0f;

    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt = "";

    @SerializedName("availableSeats")
    @Expose
    private int availableSeats = 0;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomTicketId() {
        return customTicketId;
    }

    public void setCustomTicketId(int customTicketId) {
        this.customTicketId = customTicketId;
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

    public List<String> getImages() {
        return Utils.notEmptyList(images);
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getDays() {
        return Utils.notEmptyList(days);
    }

    public void setDays(List<String> days) {
        this.days = days;
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

    public String getAvailabilityType() {
        return Utils.notNullString(availabilityType);
    }

    public void setAvailabilityType(String availabilityType) {
        this.availabilityType = availabilityType;
    }

    public String getAvailabilityTime() {
        return Utils.notNullString(availabilityTime);
    }

    public void setAvailabilityTime(String availabilityTime) {
        this.availabilityTime = availabilityTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public List<String> getAvailabilityTimeSlot() {
        return Utils.notEmptyList(availabilityTimeSlot);
    }

    public void setAvailabilityTimeSlot(List<String> availabilityTimeSlot) {
        this.availabilityTimeSlot = availabilityTimeSlot;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getAmountForChild() {
        return amountForChild;
    }

    public void setAmountForChild(Float amountForChild) {
        this.amountForChild = amountForChild;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Float getAmountForInfants() {
        return amountForInfants;
    }

    public void setAmountForInfants(Float amountForInfants) {
        this.amountForInfants = amountForInfants;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }


    public String getAvailableDaysLabel() {
        if (days == null) return "";
        if (days.isEmpty()) return "";
        List<String> allDays = Arrays.asList("mon", "tue", "wed", "thu", "fri", "sat", "sun");
        List<String> weekend = Arrays.asList("sat", "sun");

        Set<String> inputSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String day : days) {
            inputSet.add(day.toLowerCase());
        }

        if (inputSet.containsAll(allDays) && inputSet.size() == allDays.size()) {
            return "All Days";
        }

        if (inputSet.containsAll(weekend) && inputSet.size() == weekend.size()) {
            return "Weekend";
        }

        List<String> orderedOutput = new ArrayList<>();
        for (String day : allDays) {
            if (inputSet.contains(day)) {
                orderedOutput.add(day.substring(0, 1).toUpperCase() + day.substring(1));
            }
        }

        return String.join(", ", orderedOutput);
    }


    private int tmpAdultValue = 0;
    private int tmpChildValue = 0;
    private int tmpInfantValue = 0;
    private int selectedTransType = 0;
    private String pickUpLocation = "";
    private String message = "";
    private int selectedTransferId = 0;
    private RaynaTimeSlotModel raynaTimeSlotModel;
    private String tourOptionSelectDate = "";

    public int getTmpAdultValue() {
        return tmpAdultValue;
    }

    public void setTmpAdultValue(int tmpAdultValue) {
        this.tmpAdultValue = tmpAdultValue;
    }

    public int getTmpChildValue() {
        return tmpChildValue;
    }

    public void setTmpChildValue(int tmpChildValue) {
        this.tmpChildValue = tmpChildValue;
    }

    public int getTmpInfantValue() {
        return tmpInfantValue;
    }

    public void setTmpInfantValue(int tmpInfantValue) {
        this.tmpInfantValue = tmpInfantValue;
    }

    public int getSelectedTransType() {
        return selectedTransType;
    }

    public void setSelectedTransType(int selectedTransType) {
        this.selectedTransType = selectedTransType;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSelectedTransferId() {
        return selectedTransferId;
    }

    public void setSelectedTransferId(int selectedTransferId) {
        this.selectedTransferId = selectedTransferId;
    }

    public RaynaTimeSlotModel getRaynaTimeSlotModel() {
        return raynaTimeSlotModel;
    }

    public void setRaynaTimeSlotModel(RaynaTimeSlotModel raynaTimeSlotModel) {
        this.raynaTimeSlotModel = raynaTimeSlotModel;
    }

    public String getTourOptionSelectDate() {
        return tourOptionSelectDate;
    }

    public void setTourOptionSelectDate(String tourOptionSelectDate) {
        this.tourOptionSelectDate = tourOptionSelectDate;
    }

    public boolean hasAtLeastOneMember(){
        return tmpAdultValue + tmpChildValue + tmpInfantValue != 0;
    }

    public Float updateAdultPrices() {
        return tmpAdultValue * amount;
    }

    public Float updateChildPrices() {
        return tmpChildValue * amountForChild;
    }

    public Float updateInfantPrices() {
        return tmpInfantValue * amountForInfants;
    }
}

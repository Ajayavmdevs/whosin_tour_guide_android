package com.whosin.app.service.models.rayna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.HomeTicketsModel;
import com.whosin.app.service.models.ModelProtocol;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskOptionDataModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskTourDataModel;

import java.util.List;

public class RaynaTourDetailModel implements DiffIdentifier, ModelProtocol {


    @SerializedName("serviceUniqueId")
    @Expose
    private int serviceUniqueId;

    @SerializedName("tourId")
    @Expose
    private Object tourId;

    @SerializedName("tourOptionId")
    @Expose
    private String tourOptionId;

    @SerializedName("optionId")
    @Expose
    private Object optionId;

    @SerializedName("adult")
    @Expose
    private int adult;

    @SerializedName("child")
    @Expose
    private int child;

    @SerializedName("infant")
    @Expose
    private int infant;

    @SerializedName("tourDate")
    @Expose
    private String tourDate;

    @SerializedName("timeSlotId")
    @Expose
    private String timeSlotId;

    @SerializedName("timeSlot")
    @Expose
    private String timeSlot;

    @SerializedName("startTime")
    @Expose
    private String startTime;

    @SerializedName("transferId")
    @Expose
    private int transferId;

    @SerializedName("pickup")
    @Expose
    private String pickup;

    @SerializedName("adultRate")
    @Expose
    private double adultRate;

    @SerializedName("childRate")
    @Expose
    private double childRate;

    @SerializedName("serviceTotal")
    @Expose
    private String serviceTotal;

    @SerializedName("whosinTotal")
    @Expose
    private String whosinTotal;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("bookingId")
    @Expose
    private String bookingId;

    @SerializedName("startDate")
    @Expose
    private String startDate = "";

    @SerializedName("endDate")
    @Expose
    private String endDate = "";

    @SerializedName("tour")
    @Expose
    private TourOptionDetailModel tour;

    @SerializedName("tourData")
    @Expose
    private TravelDeskTourDataModel tourData;

    @SerializedName("tourOption")
    @Expose
    private TourOptionDetailModel tourOption;

    @SerializedName("optionData")
    @Expose
    private TravelDeskOptionDataModel optionData;

    @SerializedName("customData")
    @Expose
    private HomeTicketsModel customData;

    @SerializedName("customTicket")
    @Expose
    private HomeTicketsModel customTicket;

    @SerializedName("adult_title")
    @Expose
    public String adultTitle = "";

    @SerializedName("child_title")
    @Expose
    public String childTitle = "";

    @SerializedName("infant_title")
    @Expose
    public String infantTitle = "";

    @SerializedName("adult_description")
    @Expose
    public String adultDescription = "";

    @SerializedName("child_description")
    @Expose
    public String childDescription = "";

    @SerializedName("infant_description")
    @Expose
    public String infantDescription = "";

    @SerializedName("addOnTitle")
    @Expose
    public String addOnTitle = "";
    @SerializedName("addOnImage")
    @Expose
    public String addOnImage = "";
    @SerializedName("addOndesc")
    @Expose
    public String addOndesc = "";
    @SerializedName("Addons")
    @Expose
    private List<RaynaTourDetailModel> addons;
    @SerializedName("addonOption")
    @Expose
    private TourOptionsModel addonOption;

    public List<RaynaTourDetailModel> getAddons() {
        return addons;
    }

    public void setAddons(List<RaynaTourDetailModel> addons) {
        this.addons = addons;
    }

    public TourOptionsModel getAddonOption() {
        return addonOption;
    }

    public void setAddonOption(TourOptionsModel addonOption) {
        this.addonOption = addonOption;
    }

    public String getAdultTitle() {
        return Utils.isNullOrEmpty(adultTitle) ? "Adult" : adultTitle;
    }
    public String getChildTitle() {
        return Utils.isNullOrEmpty(childTitle) ? "Child" : childTitle;
    }
    public String getInfantTitle() {
        return Utils.isNullOrEmpty(infantTitle) ? "Infant" : infantTitle;
    }

    public String getAdultDescription() {
        return Utils.notNullString(adultDescription);
    }
    public String getChildDescription() {
        return Utils.notNullString(childDescription);
    }
    public String getInfantDescription() {
        return Utils.notNullString(infantDescription);
    }

    public String getTourOptionId() {
        return Utils.notNullString(tourOptionId);
    }

    public void setTourOptionId(String tourOptionId) {
        this.tourOptionId = tourOptionId;
    }

    public HomeTicketsModel getCustomTicket() {
        return customTicket;
    }

    public void setCustomTicket(HomeTicketsModel customTicket) {
        this.customTicket = customTicket;
    }

    public String getBookingId() {
        return Utils.notNullString(bookingId);
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getStatus() {
        return Utils.notNullString(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HomeTicketsModel getCustomData() {
        return customData;
    }

    public void setCustomData(HomeTicketsModel customData) {
        this.customData = customData;
    }

    public String getTimeSlot() {
        return Utils.notNullString(timeSlot);
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getServiceUniqueId() {
        return serviceUniqueId;
    }

    public void setServiceUniqueId(int serviceUniqueId) {
        this.serviceUniqueId = serviceUniqueId;
    }

    public Object getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public Object getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

    public int getInfant() {
        return infant;
    }

    public void setInfant(int infant) {
        this.infant = infant;
    }

    public String getTourDate() {
        return tourDate;
    }

    public void setTourDate(String tourDate) {
        this.tourDate = tourDate;
    }

    public String getTimeSlotId() {
        return Utils.notNullString(timeSlotId);
    }

    public void setTimeSlotId(String timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public String getPickup() {
        return Utils.notNullString(pickup);
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }
    public String getAddonTitle() {
        return Utils.notNullString(addOnTitle);
    }
    public String getAddonImage() {
        return Utils.notNullString(addOnImage);
    }
    public String getAddOndesc() {
        return Utils.notNullString(addOndesc);
    }

    public double getAdultRate() {
        return adultRate;
    }

    public void setAdultRate(double adultRate) {
        this.adultRate = adultRate;
    }

    public double getChildRate() {
        return childRate;
    }

    public void setChildRate(double childRate) {
        this.childRate = childRate;
    }

    public String getServiceTotal() {
        return serviceTotal;
    }

    public void setServiceTotal(String serviceTotal) {
        this.serviceTotal = serviceTotal;
    }

    public String getWhosinTotal() {
//        return Utils.notNullString(whosinTotal);
        return Utils.notNullString(Utils.convertIntoCurrentCurrency(whosinTotal));
    }

    public void setWhosinTotal(String whosinTotal) {
        this.whosinTotal = whosinTotal;
    }

    public TourOptionDetailModel getTour() {
        return tour;
    }

    public void setTour(TourOptionDetailModel tour) {
        this.tour = tour;
    }

    public TourOptionDetailModel getTourOption() {
        return tourOption;
    }

    public void setTourOption(TourOptionDetailModel tourOption) {
        this.tourOption = tourOption;
    }

    public void setOptionId(Object optionId) {
        this.optionId = optionId;
    }

    public TravelDeskTourDataModel getTourData() {
        return tourData;
    }

    public void setTourData(TravelDeskTourDataModel tourData) {
        this.tourData = tourData;
    }

    public TravelDeskOptionDataModel getOptionData() {
        return optionData;
    }

    public void setOptionData(TravelDeskOptionDataModel optionData) {
        this.optionData = optionData;
    }

    public void setTourId(Object tourId) {
        this.tourId = tourId;
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

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}

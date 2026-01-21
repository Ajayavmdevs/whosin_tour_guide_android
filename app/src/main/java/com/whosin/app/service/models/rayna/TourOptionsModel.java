package com.whosin.app.service.models.rayna;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.ModelProtocol;
import com.whosin.app.service.models.myCartModels.MyCartTourDetailsModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class TourOptionsModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String _id;
    @SerializedName("customTicketId")
    @Expose
    private String customTicketId;
    @SerializedName("tourId")
    @Expose
    private Integer tourId;
    @SerializedName("optionId")
    @Expose
    private Object optionId;
    @SerializedName("tourOptionId")
    @Expose
    private Integer tourOptionId;
    @SerializedName("transferId")
    @Expose
    private Integer transferId;
    @SerializedName("transferName")
    @Expose
    private String transferName;
    @SerializedName("ratioPerPax")
    @Expose
    private Integer ratioPerPax;

    @SerializedName("adultPrice")
    @Expose
    private Float adultPrice;
    @SerializedName("childPrice")
    @Expose
    private Float childPrice;
    @SerializedName("infantPrice")
    @Expose
    private Float infantPrice;
    @SerializedName("withoutDiscountAmount")
    @Expose
    private Float withoutDiscountAmount;
    @SerializedName("finalAmount")
    @Expose
    private Float finalAmount;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("departureTime")
    @Expose
    private String departureTime;
    @SerializedName("disableChild")
    @Expose
    private Boolean disableChild;
    @SerializedName("disableInfant")
    @Expose
    private Boolean disableInfant;
    @SerializedName("allowTodaysBooking")
    @Expose
    private Boolean allowTodaysBooking = false;
    @SerializedName("cutOff")
    @Expose
    private Integer cutOff;
    @SerializedName("isSlot")
    @Expose
    private Boolean isSlot = false;
    @SerializedName("isSeat")
    @Expose
    private Boolean isSeat;
    @SerializedName("isDefaultTransfer")
    @Expose
    private Integer isDefaultTransfer;
    @SerializedName("rateKey")
    @Expose
    private Object rateKey;
    @SerializedName("inventoryId")
    @Expose
    private Object inventoryId;
    @SerializedName("adultBuyingPrice")
    @Expose
    private Integer adultBuyingPrice;
    @SerializedName("childBuyingPrice")
    @Expose
    private Integer childBuyingPrice;
    @SerializedName("infantBuyingPrice")
    @Expose
    private Integer infantBuyingPrice;
    @SerializedName("adultSellingPrice")
    @Expose
    private Integer adultSellingPrice;
    @SerializedName("childSellingPrice")
    @Expose
    private Integer childSellingPrice;
    @SerializedName("infantSellingPrice")
    @Expose
    private Integer infantSellingPrice;
    @SerializedName("companyBuyingPrice")
    @Expose
    private Integer companyBuyingPrice;
    @SerializedName("companySellingPrice")
    @Expose
    private Integer companySellingPrice;
    @SerializedName("agentBuyingPrice")
    @Expose
    private Integer agentBuyingPrice;
    @SerializedName("agentSellingPrice")
    @Expose
    private Integer agentSellingPrice;
    @SerializedName("subAgentBuyingPrice")
    @Expose
    private Integer subAgentBuyingPrice;
    @SerializedName("subAgentSellingPrice")
    @Expose
    private Integer subAgentSellingPrice;
    @SerializedName("finalSellingPrice")
    @Expose
    private Integer finalSellingPrice;
    @SerializedName("vatbuying")
    @Expose
    private Integer vatbuying;
    @SerializedName("vatselling")
    @Expose
    private Integer vatselling;
    @SerializedName("currencyFactor")
    @Expose
    private Integer currencyFactor;
    @SerializedName("agentPercentage")
    @Expose
    private Integer agentPercentage;
    @SerializedName("transferBuyingPrice")
    @Expose
    private Integer transferBuyingPrice;
    @SerializedName("transferSellingPrice")
    @Expose
    private Integer transferSellingPrice;
    @SerializedName("serviceBuyingPrice")
    @Expose
    private Integer serviceBuyingPrice;
    @SerializedName("serviceSellingPrice")
    @Expose
    private Integer serviceSellingPrice;
    @SerializedName("rewardPoints")
    @Expose
    private Integer rewardPoints;
    @SerializedName("tourChildAge")
    @Expose
    private Integer tourChildAge;
    @SerializedName("maxChildAge")
    @Expose
    private Integer maxChildAge;
    @SerializedName("maxInfantAge")
    @Expose
    private Integer maxInfantAge;
    @SerializedName("minimumPax")
    @Expose
    private String minimumPax;
    @SerializedName("maximumPax")
    @Expose
    private String maximumPax;
    @SerializedName("pointRemark")
    @Expose
    private String pointRemark;
    @SerializedName("adultRetailPrice")
    @Expose
    private Integer adultRetailPrice;
    @SerializedName("childRetailPrice")
    @Expose
    private Integer childRetailPrice;
    @SerializedName("optionDetail")
    @Expose
    private TourOptionDetailModel optionDetail;
    @SerializedName("fromDate")
    @Expose
    private String fromDate;
    @SerializedName("toDate")
    @Expose
    private String toDate;
    @SerializedName("percentage")
    @Expose
    private Integer percentage;

    @SerializedName("withoutDiscountAdultPrice")
    @Expose
    private Float withoutDiscountAdultPrice;

    @SerializedName("withoutDiscountChildPrice")
    @Expose
    private Float withoutDiscountChildPrice;

    @SerializedName("withoutDiscountInfantPrice")
    @Expose
    private Float withoutDiscountInfantPrice;

    @SerializedName("images")
    @Expose
    private List<String> images;

    @SerializedName("optionName")
    @Expose
    private String optionName = "";

    @SerializedName("optionDescription")
    @Expose
    private String optionDescription = "";

    @SerializedName("duration")
    @Expose
    private String duration = "";

    @SerializedName("adultAge")
    @Expose
    private String adultAge = "";

    @SerializedName("childAge")
    @Expose
    private String childAge = "";

    @SerializedName("infantAge")
    @Expose
    private String infantAge = "";

    @SerializedName("cancellationPolicy")
    @Expose
    private String cancellationPolicy = "";

    @SerializedName("adultPriceRayna")
    @Expose
    private float adultPriceRayna;

    @SerializedName("childPriceRayna")
    @Expose
    private float childPriceRayna;

    @SerializedName("infantPriceRayna")
    @Expose
    private float infantPriceRayna ;

    @SerializedName("slotText")
    @Expose
    private String slotText ;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("operationdays")
    @Expose
    private RaynaOprationDaysModel operationdays;

    @SerializedName("exclusion")
    @Expose
    private String exclusion ;

    @SerializedName("tourExclusion")
    @Expose
    private String tourExclusion ;

    @SerializedName("inclusion")
    @Expose
    private String inclusion ;

    @SerializedName("termsAndConditions")
    @Expose
    private String termsAndConditions;
    @SerializedName("cancellationPolicyName")
    @Expose
    private String cancellationPolicyName;

    @SerializedName("cancellationPolicyDescription")
    @Expose
    private String cancellationPolicyDescription;

    @SerializedName("childPolicyDescription")
    @Expose
    private String childPolicyDescription;

    @SerializedName("title")
    @Expose
    private String title = "";

    @SerializedName("description")
    @Expose
    private String description = "";

    @SerializedName("totalSeats")
    @Expose
    private int totalSeats = 0;

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

    @SerializedName("notes")
    @Expose
    private String notes = "";

    @SerializedName("unit")
    @Expose
    private String unit = "";

    @SerializedName("availabilityTimeSlot")
    @Expose
    private List<RaynaTimeSlotModel> availabilityTimeSlot;

    @SerializedName("From")
    @Expose
    public String from = "";
    @SerializedName("DateFrom")
    @Expose
    public String dateFrom = "";
    @SerializedName("DateFromHour")
    @Expose
    public String dateFromHour = "";
    @SerializedName("DateTo")
    @Expose
    public String dateTo = "";
    @SerializedName("DateToHour")
    @Expose
    public String dateToHour = "";
    @SerializedName("Type")
    @Expose
    public String type = "";
    @SerializedName("FixedPrice")
    @Expose
    public String fixedPrice = "";
    @SerializedName("PercentPrice")
    @Expose
    public String percentPrice = "";
    @SerializedName("Nights")
    @Expose
    public String nights = "";
    @SerializedName("ApplicationTypeNights")
    @Expose
    public String applicationTypeNights = "";
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

    @SerializedName("sortDescription")
    @Expose
    private String sortDescription = "";

    @SerializedName("longDescription")
    @Expose
    private String longDescription = "";

    @SerializedName("addonOptionIds")
    @Expose
    private List<String> addonOptionIds;

    @SerializedName("isAddon")
    @Expose
    private Boolean isAddon;

    @SerializedName("isRestricted")
    @Expose
    private Boolean isRestricted;

    @SerializedName("Addons")
    @Expose
    private List<TourOptionsModel> addons;

    @SerializedName("order")
    @Expose
    private Integer order;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<TourOptionsModel> getAddons() {
        return addons;
    }

    public void setAddons(List<TourOptionsModel> addons) {
        this.addons = addons;
    }

    public Integer getRatioPerPax() {
        return ratioPerPax;
    }

    public void setRatioPerPax(Integer ratioPerPax) {
        this.ratioPerPax = ratioPerPax;
    }

    public String getSortDescription() {
        return sortDescription;
    }

    public void setSortDescription(String sortDescription) {
        this.sortDescription = sortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public List<String> getAddonOptionIds() {
        return addonOptionIds;
    }

    public void setAddonOptionIds(List<String> addonOptionIds) {
        this.addonOptionIds = addonOptionIds;
    }

    public Boolean getAddon() {
        return isAddon;
    }

    public void setAddon(Boolean addon) {
        isAddon = addon;
    }

    public Boolean getRestricted() {
        return isRestricted;
    }

    public void setRestricted(Boolean restricted) {
        isRestricted = restricted;
    }

    public String getFrom() {
        return Utils.notNullString(from);
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDateFrom() {
        return Utils.notNullString(dateFrom);
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateFromHour() {
        return Utils.notNullString(dateFromHour);
    }

    public void setDateFromHour(String dateFromHour) {
        this.dateFromHour = dateFromHour;
    }

    public String getType() {
        return Utils.notNullString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFixedPrice() {
        return Utils.notNullString(fixedPrice);
    }

    public void setFixedPrice(String fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public String getPercentPrice() {
        return Utils.notNullString(percentPrice);
    }

    public void setPercentPrice(String percentPrice) {
        this.percentPrice = percentPrice;
    }

    public String getNights() {
        return Utils.notNullString(nights);
    }

    public void setNights(String nights) {
        this.nights = nights;
    }

    public String getApplicationTypeNights() {
        return Utils.notNullString(applicationTypeNights);
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
    public void setApplicationTypeNights(String applicationTypeNights) {
        this.applicationTypeNights = applicationTypeNights;
    }

    public String getDateTo() {
        return Utils.notNullString(dateTo);
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getDateToHour() {
        return Utils.notNullString(dateToHour);
    }

    public void setDateToHour(String dateToHour) {
        this.dateToHour = dateToHour;
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
    public int whosinTypeTicketSlotPosition = -1;
    private boolean isFirestTimeUpdate = true;

    public boolean isFirestTimeUpdate() {
        return isFirestTimeUpdate;
    }

    public void setFirestTimeUpdate(boolean firestTimeUpdate) {
        isFirestTimeUpdate = firestTimeUpdate;
    }

    public ArrayList<String> transTypeList = new ArrayList<>();


    public String get_id() {
        return Utils.notNullString(_id);
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCustomTicketId() {
        return Utils.notNullString(customTicketId);
    }

    public void setCustomTicketId(String customTicketId) {
        this.customTicketId = customTicketId;
    }

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public Object getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public List<RaynaTimeSlotModel> getAvailabilityTimeSlot() {
        return Utils.notEmptyList(availabilityTimeSlot);
    }

    public void setAvailabilityTimeSlot(List<RaynaTimeSlotModel> availabilityTimeSlot) {
        this.availabilityTimeSlot = availabilityTimeSlot;
    }

    public Integer getTourOptionId() {
        return tourOptionId;
    }

    public void setTourOptionId(Integer tourOptionId) {
        this.tourOptionId = tourOptionId;
    }

    public Integer getTransferId() {
        return transferId;
    }

    public void setTransferId(Integer transferId) {
        this.transferId = transferId;
    }

    public String getTransferName() {
        return transferName;
    }

    public void setTransferName(String transferName) {
        this.transferName = transferName;
    }

    public Float getAdultPrice() {
        return Utils.roundFloatToFloat(adultPrice);
    }

    public void setAdultPrice(Float adultPrice) {
        this.adultPrice = adultPrice;
    }

    public Float getChildPrice() {
        return Utils.roundFloatToFloat(childPrice);
    }

    public void setChildPrice(Float childPrice) {
        this.childPrice = childPrice;
    }

    public Float getInfantPrice() {
        return Utils.roundFloatToFloat(infantPrice);
    }

    public void setInfantPrice(Float infantPrice) {
        this.infantPrice = infantPrice;
    }

    public Float getWithoutDiscountAmount() {
        return Utils.roundFloatToFloat(withoutDiscountAmount);
    }

    public void setWithoutDiscountAmount(Float withoutDiscountAmount) {
        this.withoutDiscountAmount = withoutDiscountAmount;
    }

    public Float getFinalAmount() {
        return Utils.roundFloatToFloat(finalAmount);
    }

    public void setFinalAmount(Float finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public Boolean getDisableChild() {
        return disableChild;
    }

    public void setDisableChild(Boolean disableChild) {
        this.disableChild = disableChild;
    }

    public Boolean getDisableInfant() {
        return disableInfant;
    }

    public void setDisableInfant(Boolean disableInfant) {
        this.disableInfant = disableInfant;
    }

    public Boolean getAllowTodaysBooking() {
        return allowTodaysBooking;
    }

    public void setAllowTodaysBooking(Boolean allowTodaysBooking) {
        this.allowTodaysBooking = allowTodaysBooking;
    }

    public Integer getCutOff() {
        return cutOff;
    }

    public void setCutOff(Integer cutOff) {
        this.cutOff = cutOff;
    }

    public Boolean getIsSlot() {
        return isSlot;
    }

    public void setIsSlot(Boolean isSlot) {
        this.isSlot = isSlot;
    }

    public Boolean getIsSeat() {
        return isSeat;
    }

    public void setIsSeat(Boolean isSeat) {
        this.isSeat = isSeat;
    }

    public Integer getIsDefaultTransfer() {
        return isDefaultTransfer;
    }

    public void setIsDefaultTransfer(Integer isDefaultTransfer) {
        this.isDefaultTransfer = isDefaultTransfer;
    }

    public Object getRateKey() {
        return rateKey;
    }

    public void setRateKey(Object rateKey) {
        this.rateKey = rateKey;
    }

    public Object getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Object inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getAdultBuyingPrice() {
        return adultBuyingPrice;
    }

    public void setAdultBuyingPrice(Integer adultBuyingPrice) {
        this.adultBuyingPrice = adultBuyingPrice;
    }

    public Integer getChildBuyingPrice() {
        return childBuyingPrice;
    }

    public void setChildBuyingPrice(Integer childBuyingPrice) {
        this.childBuyingPrice = childBuyingPrice;
    }

    public Integer getInfantBuyingPrice() {
        return infantBuyingPrice;
    }

    public void setInfantBuyingPrice(Integer infantBuyingPrice) {
        this.infantBuyingPrice = infantBuyingPrice;
    }

    public Integer getAdultSellingPrice() {
        return adultSellingPrice;
    }

    public void setAdultSellingPrice(Integer adultSellingPrice) {
        this.adultSellingPrice = adultSellingPrice;
    }

    public Integer getChildSellingPrice() {
        return childSellingPrice;
    }

    public void setChildSellingPrice(Integer childSellingPrice) {
        this.childSellingPrice = childSellingPrice;
    }

    public Integer getInfantSellingPrice() {
        return infantSellingPrice;
    }

    public void setInfantSellingPrice(Integer infantSellingPrice) {
        this.infantSellingPrice = infantSellingPrice;
    }

    public Integer getCompanyBuyingPrice() {
        return companyBuyingPrice;
    }

    public void setCompanyBuyingPrice(Integer companyBuyingPrice) {
        this.companyBuyingPrice = companyBuyingPrice;
    }

    public Integer getCompanySellingPrice() {
        return companySellingPrice;
    }

    public void setCompanySellingPrice(Integer companySellingPrice) {
        this.companySellingPrice = companySellingPrice;
    }

    public Integer getAgentBuyingPrice() {
        return agentBuyingPrice;
    }

    public void setAgentBuyingPrice(Integer agentBuyingPrice) {
        this.agentBuyingPrice = agentBuyingPrice;
    }

    public Integer getAgentSellingPrice() {
        return agentSellingPrice;
    }

    public void setAgentSellingPrice(Integer agentSellingPrice) {
        this.agentSellingPrice = agentSellingPrice;
    }

    public Integer getSubAgentBuyingPrice() {
        return subAgentBuyingPrice;
    }

    public void setSubAgentBuyingPrice(Integer subAgentBuyingPrice) {
        this.subAgentBuyingPrice = subAgentBuyingPrice;
    }

    public Integer getSubAgentSellingPrice() {
        return subAgentSellingPrice;
    }

    public void setSubAgentSellingPrice(Integer subAgentSellingPrice) {
        this.subAgentSellingPrice = subAgentSellingPrice;
    }

    public Integer getFinalSellingPrice() {
        return finalSellingPrice;
    }

    public void setFinalSellingPrice(Integer finalSellingPrice) {
        this.finalSellingPrice = finalSellingPrice;
    }

    public Integer getVatbuying() {
        return vatbuying;
    }

    public void setVatbuying(Integer vatbuying) {
        this.vatbuying = vatbuying;
    }

    public Integer getVatselling() {
        return vatselling;
    }

    public void setVatselling(Integer vatselling) {
        this.vatselling = vatselling;
    }

    public Integer getCurrencyFactor() {
        return currencyFactor;
    }

    public void setCurrencyFactor(Integer currencyFactor) {
        this.currencyFactor = currencyFactor;
    }

    public Integer getAgentPercentage() {
        return agentPercentage;
    }

    public void setAgentPercentage(Integer agentPercentage) {
        this.agentPercentage = agentPercentage;
    }

    public Integer getTransferBuyingPrice() {
        return transferBuyingPrice;
    }

    public void setTransferBuyingPrice(Integer transferBuyingPrice) {
        this.transferBuyingPrice = transferBuyingPrice;
    }

    public Integer getTransferSellingPrice() {
        return transferSellingPrice;
    }

    public void setTransferSellingPrice(Integer transferSellingPrice) {
        this.transferSellingPrice = transferSellingPrice;
    }

    public Integer getServiceBuyingPrice() {
        return serviceBuyingPrice;
    }

    public void setServiceBuyingPrice(Integer serviceBuyingPrice) {
        this.serviceBuyingPrice = serviceBuyingPrice;
    }

    public Integer getServiceSellingPrice() {
        return serviceSellingPrice;
    }

    public void setServiceSellingPrice(Integer serviceSellingPrice) {
        this.serviceSellingPrice = serviceSellingPrice;
    }

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public Integer getTourChildAge() {
        return tourChildAge;
    }

    public void setTourChildAge(Integer tourChildAge) {
        this.tourChildAge = tourChildAge;
    }

    public Integer getMaxChildAge() {
        return maxChildAge;
    }

    public void setMaxChildAge(Integer maxChildAge) {
        this.maxChildAge = maxChildAge;
    }

    public Integer getMaxInfantAge() {
        return maxInfantAge;
    }

    public void setMaxInfantAge(Integer maxInfantAge) {
        this.maxInfantAge = maxInfantAge;
    }

    public String getPointRemark() {
        return pointRemark;
    }

    public void setPointRemark(String pointRemark) {
        this.pointRemark = pointRemark;
    }

    public Integer getAdultRetailPrice() {
        return adultRetailPrice;
    }

    public void setAdultRetailPrice(Integer adultRetailPrice) {
        this.adultRetailPrice = adultRetailPrice;
    }

    public Integer getChildRetailPrice() {
        return childRetailPrice;
    }

    public void setChildRetailPrice(Integer childRetailPrice) {
        this.childRetailPrice = childRetailPrice;
    }

    public TourOptionDetailModel getOptionDetail() {
        return optionDetail;
    }

    public void setOptionDetail(TourOptionDetailModel optionDetail) {
        this.optionDetail = optionDetail;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public int getRefundPercentage() {
        return 100 - (percentage != null ? percentage : 0);
    }
    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public boolean shouldHideDiscount() {
        return finalAmount >= withoutDiscountAmount;
    }

    public Boolean getSlot() {
        return isSlot;
    }

    public void setSlot(Boolean slot) {
        isSlot = slot;
    }

    public Boolean getSeat() {
        return isSeat;
    }

    public void setSeat(Boolean seat) {
        isSeat = seat;
    }

    public Float getWithoutDiscountAdultPrice() {
        return Utils.roundFloatToFloat(withoutDiscountAdultPrice);
    }

    public void setWithoutDiscountAdultPrice(Float withoutDiscountAdultPrice) {
        this.withoutDiscountAdultPrice = withoutDiscountAdultPrice;
    }

    public Float getWithoutDiscountChildPrice() {
        return Utils.roundFloatToFloat(withoutDiscountChildPrice);
    }

    public void setWithoutDiscountChildPrice(Float withoutDiscountChildPrice) {
        this.withoutDiscountChildPrice = withoutDiscountChildPrice;
    }

    public Float getWithoutDiscountInfantPrice() {
        return Utils.roundFloatToFloat(withoutDiscountInfantPrice);
    }

    public void setWithoutDiscountInfantPrice(Float withoutDiscountInfantPrice) {
        this.withoutDiscountInfantPrice = withoutDiscountInfantPrice;
    }


    public List<String> getImages() {
        return Utils.notEmptyList(images);
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getOptionName() {
        return Utils.notNullString(optionName);
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getOptionDescription() {
        return Utils.notNullString(optionDescription);
    }

    public void setOptionDescription(String optionDescription) {
        this.optionDescription = optionDescription;
    }

    public String getDuration() {
        return Utils.notNullString(duration);
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAdultAge() {
        return Utils.notNullString(adultAge);
    }

    public void setAdultAge(String adultAge) {
        this.adultAge = adultAge;
    }

    public String getChildAge() {
        return Utils.notNullString(childAge);
    }

    public void setChildAge(String childAge) {
        this.childAge = childAge;
    }

    public String getInfantAge() {
        return Utils.notNullString(infantAge);
    }

    public void setInfantAge(String infantAge) {
        this.infantAge = infantAge;
    }

    public String getCancellationPolicy() {
        return Utils.notNullString(cancellationPolicy);
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public float getAdultPriceRayna() {
        return Utils.roundFloatToFloat(adultPriceRayna);
    }

    public void setAdultPriceRayna(float adultPriceRayna) {
        this.adultPriceRayna = adultPriceRayna;
    }

    public float getChildPriceRayna() {
        return Utils.roundFloatToFloat(childPriceRayna);
    }

    public void setChildPriceRayna(float childPriceRayna) {
        this.childPriceRayna = childPriceRayna;
    }

    public float getInfantPriceRayna() {
        return Utils.roundFloatToFloat(infantPriceRayna);
    }

    public void setInfantPriceRayna(float infantPriceRayna) {
        this.infantPriceRayna = infantPriceRayna;
    }

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

    public ArrayList<String> getTransTypeList() {
        return transTypeList;
    }

    public void setTransTypeList(ArrayList<String> transTypeList) {
        this.transTypeList = transTypeList;
    }

    public String getTourOptionSelectDate() {
        return tourOptionSelectDate;
    }

    public void setTourOptionSelectDate(String tourOptionSelectDate) {
        this.tourOptionSelectDate = tourOptionSelectDate;
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

    public String getTransType() {
        if (transTypeList == null || transTypeList.isEmpty()) return "";
        if (selectedTransType < 0 || selectedTransType >= transTypeList.size()) return "";
        return transTypeList.get(selectedTransType);
    }

    public Float updatePrice(){
        Float adult = tmpAdultValue * (adultPrice != null ? adultPrice : 0f);
        Float child = tmpChildValue * (childPrice != null ? childPrice : 0f);
        Float infant = tmpInfantValue * (infantPrice != null ? infantPrice : 0f);
        return Utils.roundFloatToFloat(adult + child + infant);
    }

    public Float updateWithoutDiscountPrice(){
        Float adult = tmpAdultValue * (withoutDiscountAdultPrice != null ? withoutDiscountAdultPrice : 0f);
        Float child = tmpChildValue * (withoutDiscountChildPrice != null ? withoutDiscountChildPrice : 0f);
        Float infant = tmpInfantValue * (withoutDiscountInfantPrice != null ? withoutDiscountInfantPrice : 0f);
        return Utils.roundFloatToFloat(adult + child + infant);
    }

    public void updateValueForCart(MyCartTourDetailsModel model, boolean isWhosinTicket) {
        this.tmpAdultValue = model.getAdult();
        this.tmpChildValue = model.getChild();
        this.tmpInfantValue = model.getInfant();
        this.tourOptionSelectDate = Utils.changeDateFormat(model.getTourDate(), AppConstants.DATEFORMAT_LONG_TIME, AppConstants.DATEFORMAT_SHORT);

        String timeSlotIdStr = model.getTimeSlotId() != null ? String.valueOf(model.getTimeSlotId()) : "";

        if (isWhosinTicket) {
            if (!TextUtils.isEmpty(availabilityType)) {
                if (availabilityType.equals("slot")) {
                    this.raynaTimeSlotModel = new RaynaTimeSlotModel(timeSlotIdStr, model.getTimeSlot());
                    this.whosinTypeTicketSlotPosition =
                            IntStream.range(0, availabilityTimeSlot.size())
                                    .filter(i ->
                                            timeSlotIdStr.equals(
                                                    availabilityTimeSlot.get(i).getId()))
                                    .findFirst()
                                    .orElse(-1);
                } else {
                    this.availabilityTime = model.getTimeSlot();
                }
            }
        } else {
            this.pickUpLocation = model.getPickup();
            if (isSlot) {
                this.raynaTimeSlotModel = new RaynaTimeSlotModel(timeSlotIdStr, model.getTimeSlot(),true);
            } else {
                this.slotText = model.getTimeSlot();
            }
        }

        if (model.getAddons() != null && !model.getAddons().isEmpty() && this.addons != null && !this.addons.isEmpty()) {
            for (MyCartTourDetailsModel cartAddon : model.getAddons()) {
                for (TourOptionsModel availableAddon : this.addons) {
                    if (cartAddon.getOptionId().equals(availableAddon.get_id())) {
                        availableAddon.setTmpAdultValue(cartAddon.getAdult());
                        availableAddon.setTmpChildValue(cartAddon.getChild());
                        if (availableAddon.getAvailabilityType().equals("slot")) {
                            availableAddon.setRaynaTimeSlotModel(new RaynaTimeSlotModel(String.valueOf(cartAddon.getTimeSlotId()), cartAddon.getTimeSlot(),true));
                        } else if (availableAddon.getAvailabilityType().equals("same_as_option")) {
                            if ("slot".equals(this.availabilityType)
                                    && this.whosinTypeTicketSlotPosition >= 0
                                    && this.getAvailabilityTimeSlot() != null
                                    && this.whosinTypeTicketSlotPosition < this.getAvailabilityTimeSlot().size()) {

                                RaynaTimeSlotModel slot = this.getAvailabilityTimeSlot().get(this.whosinTypeTicketSlotPosition);

                                availableAddon.setRaynaTimeSlotModel(new RaynaTimeSlotModel(
                                                slot.getId(),
                                                !TextUtils.isEmpty(slot.getAvailabilityTime())
                                                        ? slot.getAvailabilityTime()
                                                        : slot.getTimeSlot(),
                                                true
                                        )
                                );
                            } else {
                              availableAddon.setAvailabilityTime(this.availabilityTime);
                          }
                        } else {
                            availableAddon.setAvailabilityTime(cartAddon.getTimeSlot());
                        }
                        
                        boolean exists = false;
                        for (TourOptionsModel m : RaynaTicketManager.shared.selectedAddonModels) {
                            if (m.get_id().equals(availableAddon.get_id())) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            RaynaTicketManager.shared.selectedAddonModels.add(availableAddon);
                        }
                    }
                }
            }
        }
    }

    public void updateValueForTourOption(TourOptionsModel model, boolean isWhosinTicket) {
        this.tmpAdultValue = model.getTmpAdultValue();
        this.tmpChildValue = model.getTmpChildValue();
        this.tmpInfantValue = model.getTmpInfantValue();
        this.tourOptionSelectDate = model.getTourOptionSelectDate();

        if (isWhosinTicket) {
            if (!TextUtils.isEmpty(availabilityType)) {
                if (availabilityType.equals("slot")) {
                    this.whosinTypeTicketSlotPosition = model.whosinTypeTicketSlotPosition;
                } else {
                    this.availabilityTime = model.getAvailabilityTime();
                }
            }
        } else {
            this.selectedTransType = model.getSelectedTransType();
            this.selectedTransferId = model.getTransferId();
            if (isSlot && model.getRaynaTimeSlotModel() != null) {
                this.raynaTimeSlotModel = model.getRaynaTimeSlotModel();
            } else {
                this.slotText = model.getSlotText();
            }
        }
    }


    public void updateValueOnTransType(TourOptionsModel model){
        this.allowTodaysBooking = model.getAllowTodaysBooking();
        this.adultPriceRayna = model.getAdultPriceRayna();
        this.childPriceRayna = model.getChildPriceRayna();
        this.infantPriceRayna = model.getInfantPriceRayna();
        this.adultPrice = model.getAdultPrice();
        this.childPrice = model.getChildPrice();
        this.infantPrice = model.getInfantPrice();
        this.withoutDiscountAdultPrice = model.getWithoutDiscountAdultPrice();
        this.withoutDiscountChildPrice = model.getWithoutDiscountChildPrice();
        this.withoutDiscountInfantPrice = model.getWithoutDiscountInfantPrice();
        this.setOptionDetail(model.getOptionDetail());
        this.withoutDiscountAmount = model.getWithoutDiscountAmount();
        this.finalAmount = model.getFinalAmount();
        this.disableChild = model.disableChild;
        this.disableInfant = model.disableInfant;
        this.isSlot = model.isSlot;
        this.selectedTransferId = model.transferId;
        this.slotText = model.slotText;
    }


    public boolean isMinPax() {
        int total = tmpAdultValue + tmpChildValue + tmpInfantValue;
        return total < RaynaTicketManager.shared.raynaTicketDetailModel.getTmpMinPax();
    }

    public boolean isMaxPax() {

        int total = tmpAdultValue + tmpChildValue + tmpInfantValue;
        total = total + 1;
        return total > RaynaTicketManager.shared.raynaTicketDetailModel.getTmpMaxPax();
    }

    public boolean isWhosinMinPax() {
        int total = tmpAdultValue + tmpChildValue + tmpInfantValue;
        return total < getTmpMinPax();
    }

    public boolean isWhosinMaxPax() {
        int total = tmpAdultValue + tmpChildValue + tmpInfantValue;
        total = total + 1;
        return total > getTmpMaxPax();
    }

    public int getTmpMinPax() {
        if ("NA".equals(minimumPax)) {
            return 0;
        }
        try {
            return Integer.parseInt(String.valueOf(minimumPax));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getTmpMaxPax() {
        if (maximumPax == null || "NA".equals(maximumPax)) {
            return 1000;
        }
        try {
            return Integer.parseInt(String.valueOf(maximumPax));
        } catch (NumberFormatException e) {
            return 1000;
        }
    }

    public Float updateAdultPrices() {
        return tmpAdultValue * (adultPrice != null ? adultPrice : 0f);
    }

    public Float updateChildPrices() {
        return tmpChildValue * (childPrice != null ? childPrice : 0f);
    }

    public Float updateInfantPrices() {
        return tmpInfantValue * (infantPrice != null ? infantPrice : 0f);
    }

    public Float updateAddOnPrices() {
        if (addons == null || addons.isEmpty()) return 0f;
        float total = 0f;
        for (TourOptionsModel addon : addons) {
            if (addon == null) continue;
            try {
                total += addon.updatePrice();
            } catch (Exception ignored) {}
        }
        return Utils.roundFloatToFloat(total);
    }

    public Float updateWithoutAdultPrices() {
        return tmpAdultValue * (withoutDiscountAdultPrice != null ? withoutDiscountAdultPrice : 0f);
    }

    public Float updateWithoutChildPrices() {
        return tmpChildValue * (withoutDiscountChildPrice != null ? withoutDiscountChildPrice : 0f);
    }

    public Float updateWithoutInfantPrices() {
        return tmpInfantValue * (withoutDiscountInfantPrice != null ? withoutDiscountInfantPrice : 0f);
    }


    public boolean hasAtLeastOneMember(){
        return tmpAdultValue + tmpChildValue + tmpInfantValue != 0;
    }


    public String getSlotText() {
        return Utils.notNullString(slotText);
    }

    public void setSlotText(String slotText) {
        this.slotText = slotText;
    }

    public String getBookingDate() {
        Calendar calendar = Calendar.getInstance();
        if (!allowTodaysBooking) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        Date date = calendar.getTime();

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return outputFormat.format(date);
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RaynaOprationDaysModel getOperationdays() {
        return operationdays;
    }

    public void setOperationdays(RaynaOprationDaysModel operationdays) {
        this.operationdays = operationdays;
    }

    public String getExclusion() {
        return exclusion;
    }
    public String getTourExclusion() {
        return tourExclusion;
    }

    public void setExclusion(String exclusion) {
        this.exclusion = exclusion;
    }
    public void setTourExclusion(String tourExclusion) {
        this.tourExclusion = tourExclusion;
    }

    public String getInclusion() {
        return inclusion;
    }

    public void setInclusion(String inclusion) {
        this.inclusion = inclusion;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public String getCancellationPolicyName() {
        return cancellationPolicyName;
    }

    public void setCancellationPolicyName(String cancellationPolicyName) {
        this.cancellationPolicyName = cancellationPolicyName;
    }

    public String getCancellationPolicyDescription() {
        return cancellationPolicyDescription;
    }

    public void setCancellationPolicyDescription(String cancellationPolicyDescription) {
        this.cancellationPolicyDescription = cancellationPolicyDescription;
    }

    public String getChildPolicyDescription() {
        return childPolicyDescription;
    }

    public void setChildPolicyDescription(String childPolicyDescription) {
        this.childPolicyDescription = childPolicyDescription;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
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

    public RaynaTimeSlotModel getSlotModelForWhosinTicket() {
        if (availabilityTimeSlot != null
                && !availabilityTimeSlot.isEmpty()
                && whosinTypeTicketSlotPosition >= 0
                && whosinTypeTicketSlotPosition < availabilityTimeSlot.size()) {
            return availabilityTimeSlot.get(whosinTypeTicketSlotPosition);
        }
        return null;
    }

    public String getNotes() {
        return Utils.notNullString(notes);
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUnit() {
        return Utils.notNullString(unit);
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}

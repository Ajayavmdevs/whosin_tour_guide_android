package com.whosin.business.service.models.whosinTicketModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.manager.RaynaTicketManager;
import com.whosin.business.service.models.ModelProtocol;
import com.whosin.business.service.models.myCartModels.MyCartTourDetailsModel;
import com.whosin.business.service.models.rayna.RaynaOprationDaysModel;
import com.whosin.business.service.models.rayna.RaynaTimeSlotModel;
import com.whosin.business.service.models.rayna.TourOptionDetailModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WhosinTicketTourOptionModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String _id;
    @SerializedName("customTicketId")
    @Expose
    private String customTicketId;
    @SerializedName("tourId")
    @Expose
    private String tourId;
    @SerializedName("optionId")
    @Expose
    private Object optionId;
    @SerializedName("tourOptionId")
    @Expose
    private String tourOptionId;
    @SerializedName("transferId")
    @Expose
    private Integer transferId;
    @SerializedName("transferName")
    @Expose
    private String transferName;
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
    private Boolean disableChild = false;
    @SerializedName("disableInfant")
    @Expose
    private Boolean disableInfant = false;
    @SerializedName("allowTodaysBooking")
    @Expose
    private Boolean allowTodaysBooking = false;
    @SerializedName("isPickup")
    @Expose
    private Boolean isPickup = false;
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
    @SerializedName("minPax")
    @Expose
    private String minimumPax;
    @SerializedName("maxPax")
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

    @SerializedName("displayName")
    @Expose
    private String displayName = "";

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
    @SerializedName("adult_title")
    @Expose
    public String adultTitle = "Adult";
    @SerializedName("child_title")
    @Expose
    public String childTitle = "Child";
    @SerializedName("infant_title")
    @Expose
    public String infantTitle = "Infant";
    @SerializedName("adult_description")
    @Expose
    public String adultDescription = "";
    @SerializedName("child_description")
    @Expose
    public String childDescription = "";
    @SerializedName("infant_description")
    @Expose
    public String infantDescription = "";

    @SerializedName("discount")
    @Expose
    private Integer discount;

    @SerializedName("discountType")
    @Expose
    private String discountType = "";

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

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getDiscountText() {
        if (discount == null || discount <= 0) return "";

        return "flat".equalsIgnoreCase(discountType)
                ? discount + " OFF"
                : discount + "% OFF";
    }

    public String getUnit() {
        return Utils.notNullString(unit);
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNotes() {
        return Utils.notNullString(notes);
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isFirestTimeUpdate() {
        return isFirestTimeUpdate;
    }

    public void setFirestTimeUpdate(boolean firestTimeUpdate) {
        isFirestTimeUpdate = firestTimeUpdate;
    }


    public ArrayList<String> transTypeList = new ArrayList<>();

    public Boolean getPickup() {
        return isPickup;
    }

    public void setPickup(Boolean pickup) {
        isPickup = pickup;
    }

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

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
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

    public String getTourOptionId() {
        return tourOptionId;
    }

    public void setTourOptionId(String  tourOptionId) {
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

    public String getAdultTitle() {
        return Utils.notNullString(adultTitle);
    }
    public String getChildTitle() {
        return Utils.notNullString(childTitle);
    }
    public String getInfantTitle() {
        return Utils.notNullString(infantTitle);
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
        return Utils.notNullString(departureTime);
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

    public String getDisplayName() {
        return Utils.notNullString(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
        Float adult = tmpAdultValue * adultPrice;
        Float child = tmpChildValue * childPrice;
        Float infant = tmpInfantValue * infantPrice;
        return Utils.roundFloatToFloat(adult + child + infant);
    }

    public Float updateWithoutDiscountPrice(){
        Float adult = tmpAdultValue * withoutDiscountAdultPrice;
        Float child = tmpChildValue * withoutDiscountChildPrice;
        Float infant = tmpInfantValue * withoutDiscountInfantPrice;
        return Utils.roundFloatToFloat(adult + child + infant);
    }

    public void updateValueForCart(MyCartTourDetailsModel model) {
        this.tmpAdultValue = model.getAdult();
        this.tmpChildValue = model.getChild();
        this.tmpInfantValue = model.getInfant();
        this.tourOptionSelectDate = Utils.changeDateFormat(model.getTourDate(), AppConstants.DATEFORMAT_LONG_TIME, AppConstants.DATEFORMAT_SHORT);
        String timeSlotIdStr = model.getTimeSlotId() != null ? String.valueOf(model.getTimeSlotId()) : "";
        if (isSlot) {
            this.raynaTimeSlotModel = new RaynaTimeSlotModel(timeSlotIdStr, model.getTimeSlot(),true);
        } else {
            this.slotText = model.getTimeSlot();
        }

    }

    public boolean isMinPax() {
        int total = tmpAdultValue + tmpChildValue + tmpInfantValue;
        return total < RaynaTicketManager.shared.raynaTicketDetailModel.getTmpMinPax();
    }

    public boolean isMaxPax() {
        int total = tmpAdultValue + tmpChildValue + tmpInfantValue;

        return total > RaynaTicketManager.shared.raynaTicketDetailModel.getTmpMaxPax();
    }

    public boolean isWhosinMinPax() {
        int total = tmpAdultValue + tmpChildValue + tmpInfantValue;
        return total < getTmpMinPax();
    }

    public boolean isWhosinMaxPax() {
        int total = tmpAdultValue + tmpChildValue + tmpInfantValue;
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
        return tmpAdultValue * adultPrice;
    }

    public Float updateChildPrices() {
        return tmpChildValue * childPrice;
    }

    public Float updateInfantPrices() {
        return tmpInfantValue * infantPrice;
    }

    public Float updateWithoutAdultPrices() {
        return tmpAdultValue * withoutDiscountAdultPrice;
    }

    public Float updateWithoutChildPrices() {
        return tmpChildValue * withoutDiscountChildPrice;
    }

    public Float updateWithoutInfantPrices() {
        return tmpInfantValue * withoutDiscountInfantPrice;
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

    public void setExclusion(String exclusion) {
        this.exclusion = exclusion;
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

    private boolean isExpanded = true;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

}

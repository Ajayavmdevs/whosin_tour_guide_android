package com.whosin.app.service.models.TravelDeskModels;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.JuniperHotelModels.JPHotelBoardForWalletModel;
import com.whosin.app.service.models.JuniperHotelModels.JPHotelBoardModel;
import com.whosin.app.service.models.JuniperHotelModels.JPHotelInfoModel;
import com.whosin.app.service.models.JuniperHotelModels.JpHotelRoomModel;
import com.whosin.app.service.models.ModelProtocol;
import com.whosin.app.service.models.myCartModels.MyCartTourDetailsModel;
import com.whosin.app.service.models.rayna.RaynaTimeSlotModel;

import java.util.List;
import java.util.function.Function;

public class TravelDeskOptionDataModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String _id = "";

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("bookingType")
    @Expose
    private int bookingType;

    @SerializedName("childAge")
    @Expose
    private int childAge;

    @SerializedName("childrenAllowed")
    @Expose
    private boolean childrenAllowed;

    @SerializedName("code")
    @Expose
    private String code = "";

    @SerializedName("daysOffered")
    @Expose
    private int daysOffered;

    @SerializedName("description")
    @Expose
    private String description = "";

    @SerializedName("heroImage")
    @Expose
    private TravelDeskHeroImageModel heroImage ;

    @SerializedName("infantAge")
    @Expose
    private int infantAge;

    @SerializedName("infantsAllowed")
    @Expose
    private boolean infantsAllowed;

    @SerializedName("isAppendTransactionFeeB2b")
    @Expose
    private boolean isAppendTransactionFeeB2b;

    @SerializedName("isDirectCollection")
    @Expose
    private boolean isDirectCollection;

    @SerializedName("isDirectReporting")
    @Expose
    private boolean isDirectReporting = true;

    @SerializedName("isExternal")
    @Expose
    private boolean isExternal;

    @SerializedName("maxNumOfPeople")
    @Expose
    private int maxNumOfPeople = 0;

    @SerializedName("minNumOfPeople")
    @Expose
    private int minNumOfPeople = 0;

    @SerializedName("minimumAdvancedPayment")
    @Expose
    private int minimumAdvancedPayment = 0;

    @SerializedName("name")
    @Expose
    private String name = "";

    @SerializedName("displayName")
    @Expose
    private String displayName = "";

    @SerializedName("cancellationPolicy")
    @Expose
    private String cancellationPolicy = "";

    @SerializedName("optionDescription")
    @Expose
    private String optionDescription = "";

    @SerializedName("numberOfHours")
    @Expose
    private int numberOfHours;

    @SerializedName("privateType")
    @Expose
    private int privateType;

    @SerializedName("tourId")
    @Expose
    private int tourId;

    @SerializedName("shortDescription")
    @Expose
    private String shortDescription = "";

    @SerializedName("title")
    @Expose
    private String title = "";

    @SerializedName("notes")
    @Expose
    private String notes = "";

    private boolean isDescriptionProcessed = false;

    @SerializedName("unit")
    @Expose
    private String unit = "";

    @SerializedName("hotelInfo")
    @Expose
    private JPHotelInfoModel hotelInfo ;

    @SerializedName("board")
    @Expose
    private JPHotelBoardForWalletModel board ;

    @SerializedName("rooms")
    @Expose
    private List<JpHotelRoomModel> rooms ;


    @SerializedName("pricingPeriods")
    @Expose
    private List<TravelDeskPriceModel> pricingPeriods;

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

    public boolean isDescriptionProcessed() {
        return isDescriptionProcessed;
    }

    public void setDescriptionProcessed(boolean descriptionProcessed) {
        isDescriptionProcessed = descriptionProcessed;
    }

    public String getCancellationPolicy() {
        return Utils.notNullString(cancellationPolicy);
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public String getTitle() {
        return Utils.notNullString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return Utils.notNullString(shortDescription);
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String get_id() {
        return Utils.notNullString(_id);
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingType() {
        return bookingType;
    }

    public void setBookingType(int bookingType) {
        this.bookingType = bookingType;
    }

    public int getChildAge() {
        return childAge;
    }

    public void setChildAge(int childAge) {
        this.childAge = childAge;
    }

    public boolean isChildrenAllowed() {
        return childrenAllowed;
    }

    public void setChildrenAllowed(boolean childrenAllowed) {
        this.childrenAllowed = childrenAllowed;
    }

    public String getCode() {
        return Utils.notNullString(code);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDaysOffered() {
        return daysOffered;
    }

    public void setDaysOffered(int daysOffered) {
        this.daysOffered = daysOffered;
    }

    public String getDescription() {
        return Utils.notNullString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getInfantAge() {
        return infantAge;
    }

    public void setInfantAge(int infantAge) {
        this.infantAge = infantAge;
    }

    public boolean isInfantsAllowed() {
        return infantsAllowed;
    }

    public void setInfantsAllowed(boolean infantsAllowed) {
        this.infantsAllowed = infantsAllowed;
    }

    public boolean isAppendTransactionFeeB2b() {
        return isAppendTransactionFeeB2b;
    }

    public void setAppendTransactionFeeB2b(boolean appendTransactionFeeB2b) {
        isAppendTransactionFeeB2b = appendTransactionFeeB2b;
    }

    public boolean isDirectCollection() {
        return isDirectCollection;
    }

    public void setDirectCollection(boolean directCollection) {
        isDirectCollection = directCollection;
    }

    public boolean isDirectReporting() {
        return isDirectReporting;
    }

    public void setDirectReporting(boolean directReporting) {
        isDirectReporting = directReporting;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    public int getMaxNumOfPeople() {
        return maxNumOfPeople;
    }

    public void setMaxNumOfPeople(int maxNumOfPeople) {
        this.maxNumOfPeople = maxNumOfPeople;
    }

    public int getMinNumOfPeople() {
        return minNumOfPeople;
    }

    public void setMinNumOfPeople(int minNumOfPeople) {
        this.minNumOfPeople = minNumOfPeople;
    }

    public int getMinimumAdvancedPayment() {
        return minimumAdvancedPayment;
    }

    public void setMinimumAdvancedPayment(int minimumAdvancedPayment) {
        this.minimumAdvancedPayment = minimumAdvancedPayment;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfHours() {
        return numberOfHours;
    }

    public void setNumberOfHours(int numberOfHours) {
        this.numberOfHours = numberOfHours;
    }

    public int getPrivateType() {
        return privateType;
    }

    public void setPrivateType(int privateType) {
        this.privateType = privateType;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public List<TravelDeskPriceModel> getPricingPeriods() {
        return Utils.notEmptyList(pricingPeriods);
    }

    public void setPricingPeriods(List<TravelDeskPriceModel> pricingPeriods) {
        this.pricingPeriods = pricingPeriods;
    }

    public TravelDeskHeroImageModel getHeroImage() {
        return heroImage;
    }

    public void setHeroImage(TravelDeskHeroImageModel heroImage) {
        this.heroImage = heroImage;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public String getDisplayName() {
        return Utils.notNullString(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getOptionDescription() {
        return Utils.notNullString(optionDescription);
    }

    public void setOptionDescription(String optionDescription) {
        this.optionDescription = optionDescription;
    }

    private int tmpAdultValue = 0;
    private int tmpChildValue = 0;
    private int tmpInfantValue = 0;
    private String tourOptionSelectDate = "";
    private String message = "";
    private TravelDeskTourAvailabilityModel travelDeskAvailabilityModel = null;
    private TravelDeskPickUpListModel travelDeskPickUpListModel = null;
    private boolean isFirestTimeUpdate = true;

    public boolean isFirestTimeUpdate() {
        return isFirestTimeUpdate;
    }

    public void setFirestTimeUpdate(boolean firestTimeUpdate) {
        isFirestTimeUpdate = firestTimeUpdate;
    }

    public TravelDeskPickUpListModel getTravelDeskPickUpListModel() {
        return travelDeskPickUpListModel;
    }

    public void setTravelDeskPickUpListModel(TravelDeskPickUpListModel travelDeskPickUpListModel) {
        this.travelDeskPickUpListModel = travelDeskPickUpListModel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TravelDeskTourAvailabilityModel getTravelDeskAvailabilityModel() {
        return travelDeskAvailabilityModel;
    }

    public void setTravelDeskAvailabilityModel(TravelDeskTourAvailabilityModel travelDeskAvailabilityModel) {
        this.travelDeskAvailabilityModel = travelDeskAvailabilityModel;
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

    private Float getPriceSafe(Function<TravelDeskPriceModel, Float> priceExtractor) {
        if (pricingPeriods == null || pricingPeriods.isEmpty() || pricingPeriods.get(0) == null) {
            return 0f;
        }
        return Utils.roundFloatToFloat(priceExtractor.apply(pricingPeriods.get(0)));
    }

    public Float getAdultPrice() {
        return getPriceSafe(TravelDeskPriceModel::getPricePerAdult);
    }

    public Float getChildPrice() {
        return getPriceSafe(TravelDeskPriceModel::getPricePerChild);
    }

    public Float getInfantPrice() {
        return getPriceSafe(TravelDeskPriceModel::getPricePerInfant);
    }

    public Float getWithoutDiscountAdultPrice() {
        return Utils.roundFloatToFloat(getPriceSafe(TravelDeskPriceModel::getPricePerAdultBeforeDiscount));
    }

    public Float getPricePerTrip() {
        return getPriceSafe(TravelDeskPriceModel::getPricePerTrip);
    }

    public Float getWithoutDiscountChildPrice() {
        return Utils.roundFloatToFloat(getPriceSafe(TravelDeskPriceModel::getPricePerChildBeforeDiscount));
    }

    public Float getWithoutDiscountInfantPrice() {
        return Utils.roundFloatToFloat(getPriceSafe(TravelDeskPriceModel::getPricePerInfantBeforeDiscount));
    }

    public Float getPricePerAdultTravelDesk() {
        return Utils.roundFloatToFloat(getPriceSafe(TravelDeskPriceModel::getPricePerAdultTravelDesk));
    }

    public Float getPricePerChildTravelDesk() {
        return Utils.roundFloatToFloat(getPriceSafe(TravelDeskPriceModel::getPricePerChildTravelDesk));
    }

    public Float getPricePerTripTravelDesk() {
        return Utils.roundFloatToFloat(getPriceSafe(TravelDeskPriceModel::getPricePerTripTravelDesk));
    }


    public Float updateAdultPrices() {
        return tmpAdultValue * getAdultPrice();
    }

    public Float updateChildPrices() {
        return tmpChildValue * getChildPrice();
    }

    public Float updateInfantPrices() {
        return tmpInfantValue * getInfantPrice();
    }

    public boolean hasAtLeastOneMember(){
        return tmpAdultValue + tmpChildValue + tmpInfantValue != 0;
    }

    public String getTourOptionSelectDate() {
        return tourOptionSelectDate;
    }

    public void setTourOptionSelectDate(String tourOptionSelectDate) {
        this.tourOptionSelectDate = tourOptionSelectDate;
    }

    private String getStringSafe(Function<TravelDeskPriceModel, String> stringExtractor) {
        if (pricingPeriods == null || pricingPeriods.isEmpty() || pricingPeriods.get(0) == null) {
            return "";
        }
        String value = stringExtractor.apply(pricingPeriods.get(0));
        return value != null ? value : "";
    }


    public String getStartDate(){
      return getStringSafe(TravelDeskPriceModel::getDateStart);
    }

    public String getEndDate(){
        return getStringSafe(TravelDeskPriceModel::getDateEnd);
    }

    public void updateValueForCart(MyCartTourDetailsModel model) {
        if (model == null) return;

        this.tmpAdultValue = model.getAdult();
        this.tmpChildValue = model.getChild();
        this.tmpInfantValue = model.getInfant();


        if (!TextUtils.isEmpty(model.getTourDate())) {
            this.tourOptionSelectDate = Utils.changeDateFormat(
                    model.getTourDate(),
                    AppConstants.DATEFORMAT_LONG_TIME,
                    AppConstants.DATEFORMAT_SHORT
            );
        }


        int startTime = 0;
        int endTime = 0;
        try {
            if (!TextUtils.isEmpty(model.getStartTime())) {
                startTime = Integer.parseInt(model.getStartTime());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            if (!TextUtils.isEmpty(model.getEndTime())) {
                endTime = Integer.parseInt(model.getEndTime());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // Safely create and assign availability model

        int timeSlotId = 0;
        try {
            timeSlotId = Integer.parseInt(model.getTimeSlotId().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        TravelDeskAvailabilityModel availabilityModel = new TravelDeskAvailabilityModel(
                startTime,
                endTime,
                timeSlotId
        );


        TravelDeskTourAvailabilityModel tourAvailabilityModel = new TravelDeskTourAvailabilityModel();
        tourAvailabilityModel.setAvailability(availabilityModel);
        this.travelDeskAvailabilityModel = tourAvailabilityModel;
        this.travelDeskPickUpListModel = new TravelDeskPickUpListModel(model.getHotelId(),model.getPickup());
    }


    public JPHotelInfoModel getHotelInfo() {
        return hotelInfo;
    }

    public void setHotelInfo(JPHotelInfoModel hotelInfo) {
        this.hotelInfo = hotelInfo;
    }

    public JPHotelBoardForWalletModel getBoard() {
        return board;
    }

    public void setBoard(JPHotelBoardForWalletModel board) {
        this.board = board;
    }

    public List<JpHotelRoomModel> getRooms() {
        return Utils.notEmptyList(rooms);
    }

    public void setRooms(List<JpHotelRoomModel> rooms) {
        this.rooms = rooms;
    }
}

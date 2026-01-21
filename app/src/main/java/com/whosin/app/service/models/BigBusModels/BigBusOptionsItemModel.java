package com.whosin.app.service.models.BigBusModels;

import android.text.Html;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.ModelProtocol;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskAvailabilityModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskPickUpListModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskTourAvailabilityModel;
import com.whosin.app.service.models.myCartModels.MyCartTourDetailsModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;

public class BigBusOptionsItemModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("cancellationCutoffUnit")
	@Expose
	private String cancellationCutoffUnit = "";

	@SerializedName("cancellationCutoff")
	@Expose
	private String cancellationCutoff = "";

	@SerializedName("pickupPoints")
	@Expose
	private List<BigBusPickupPointsModel> pickupPoints;

	@SerializedName("coverImageUrl")
	@Expose
	private String coverImageUrl = "";

	@SerializedName("pickupRequired")
	@Expose
	private boolean pickupRequired = false;

	@SerializedName("language")
	@Expose
	private String language = "";

	@SerializedName("itinerary")
	@Expose
	private List<BigBusItineraryItemModel> itinerary;

	@SerializedName("units")
	@Expose
	private List<BigBusUnitsItemModel> units;

	@SerializedName("title")
	@Expose
	private String title = "";

	@SerializedName("toPoint")
	@Expose
	private String toPoint = "";

	@SerializedName("reference")
	@Expose
	private String reference = "";

	@SerializedName("requiredContactFields")
	@Expose
	private List<Object> requiredContactFields;

	@SerializedName("duration")
	@Expose
	private String duration = "";

	@SerializedName("internalName")
	@Expose
	private String internalName = "";

	@SerializedName("pickupAvailable")
	@Expose
	private boolean pickupAvailable = false;

	@SerializedName("default")
	@Expose
	private boolean jsonMemberDefault = false;

	@SerializedName("availabilityLocalDateStart")
	@Expose
	private String availabilityLocalDateStart = "";

	@SerializedName("visibleContactFields")
	@Expose
	private List<Object> visibleContactFields;

	@SerializedName("availabilityCutoffAmount")
	@Expose
	private int availabilityCutoffAmount = 0;

	@SerializedName("availabilityCutoff")
	@Expose
	private String availabilityCutoff = "";

	@SerializedName("id")
	@Expose
	private String id = "";

	@SerializedName("availabilityLocalStartTimes")
	@Expose
	private List<Object> availabilityLocalStartTimes;

	@SerializedName("durationUnit")
	@Expose
	private String durationUnit = "";

	@SerializedName("durationAmount")
	@Expose
	private int durationAmount;

	@SerializedName("restrictions")
	@Expose
	private BigBusRestrictionsModel restrictions;

	@SerializedName("shortDescription")
	@Expose
	private String shortDescription = "";

	@SerializedName("tags")
	@Expose
	private List<String> tags;

	@SerializedName("cancellationCutoffAmount")
	@Expose
	private int cancellationCutoffAmount;

	@SerializedName("subtitle")
	@Expose
	private String subtitle = "";

	@SerializedName("availabilityLocalDateEnd")
	@Expose
	private String availabilityLocalDateEnd = "";

	@SerializedName("availabilityCutoffUnit")
	@Expose
	private String availabilityCutoffUnit = "";

	@SerializedName("fromPoint")
	@Expose
	private String fromPoint = "";

	@SerializedName("notes")
	@Expose
	private String notes = "";

	@SerializedName("unit")
	@Expose
	private String unit = "";

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

	private boolean isDescriptionProcessed = false;

	public boolean isDescriptionProcessed() {
		return isDescriptionProcessed;
	}

	public void setDescriptionProcessed(boolean descriptionProcessed) {
		isDescriptionProcessed = descriptionProcessed;
	}

	public String getCancellationCutoffUnit() {
		return Utils.notNullString(cancellationCutoffUnit);
	}

	public void setCancellationCutoffUnit(String cancellationCutoffUnit) {
		this.cancellationCutoffUnit = cancellationCutoffUnit;
	}

	public String getCancellationCutoff() {
		return Utils.notNullString(cancellationCutoff);
	}

	public void setCancellationCutoff(String cancellationCutoff) {
		this.cancellationCutoff = cancellationCutoff;
	}

	public List<BigBusPickupPointsModel> getPickupPoints() {
		return Utils.notEmptyList(pickupPoints);
	}

	public void setPickupPoints(List<BigBusPickupPointsModel> pickupPoints) {
		this.pickupPoints = pickupPoints;
	}

	public String getCoverImageUrl() {
		return Utils.notNullString(coverImageUrl);
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public boolean isPickupRequired() {
		return pickupRequired;
	}

	public void setPickupRequired(boolean pickupRequired) {
		this.pickupRequired = pickupRequired;
	}

	public String getLanguage() {
		return Utils.notNullString(language);
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<BigBusItineraryItemModel> getItinerary() {
		return Utils.notEmptyList(itinerary);
	}

	public void setItinerary(List<BigBusItineraryItemModel> itinerary) {
		this.itinerary = itinerary;
	}

	public List<BigBusUnitsItemModel> getUnits() {
		return Utils.notEmptyList(units);
	}

	public void setUnits(List<BigBusUnitsItemModel> units) {
		this.units = units;
	}

	public String getTitle() {
		return Utils.notNullString(title);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getToPoint() {
		return Utils.notNullString(toPoint);
	}

	public void setToPoint(String toPoint) {
		this.toPoint = toPoint;
	}

	public String getReference() {
		return Utils.notNullString(reference);
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public List<Object> getRequiredContactFields() {
		return Utils.notEmptyList(requiredContactFields);
	}

	public void setRequiredContactFields(List<Object> requiredContactFields) {
		this.requiredContactFields = requiredContactFields;
	}

	public String getDuration() {
		return Utils.notNullString(duration);
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getInternalName() {
		return Utils.notNullString(internalName);
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	public boolean isPickupAvailable() {
		return pickupAvailable;
	}

	public void setPickupAvailable(boolean pickupAvailable) {
		this.pickupAvailable = pickupAvailable;
	}

	public boolean isJsonMemberDefault() {
		return jsonMemberDefault;
	}

	public void setJsonMemberDefault(boolean jsonMemberDefault) {
		this.jsonMemberDefault = jsonMemberDefault;
	}

	public String getAvailabilityLocalDateStart() {
		return Utils.notNullString(availabilityLocalDateStart);
	}

	public void setAvailabilityLocalDateStart(String availabilityLocalDateStart) {
		this.availabilityLocalDateStart = availabilityLocalDateStart;
	}

	public List<Object> getVisibleContactFields() {
		return visibleContactFields;
	}

	public void setVisibleContactFields(List<Object> visibleContactFields) {
		this.visibleContactFields = visibleContactFields;
	}

	public int getAvailabilityCutoffAmount() {
		return availabilityCutoffAmount;
	}

	public void setAvailabilityCutoffAmount(int availabilityCutoffAmount) {
		this.availabilityCutoffAmount = availabilityCutoffAmount;
	}

	public String getAvailabilityCutoff() {
		return Utils.notNullString(availabilityCutoff);
	}

	public void setAvailabilityCutoff(String availabilityCutoff) {
		this.availabilityCutoff = availabilityCutoff;
	}

	public String getId() {
		return Utils.notNullString(id);
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Object> getAvailabilityLocalStartTimes() {
		return availabilityLocalStartTimes;
	}

	public void setAvailabilityLocalStartTimes(List<Object> availabilityLocalStartTimes) {
		this.availabilityLocalStartTimes = availabilityLocalStartTimes;
	}

	public String getDurationUnit() {
		return Utils.notNullString(durationUnit);
	}

	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}

	public int getDurationAmount() {
		return durationAmount;
	}

	public void setDurationAmount(int durationAmount) {
		this.durationAmount = durationAmount;
	}

	public BigBusRestrictionsModel getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(BigBusRestrictionsModel restrictions) {
		this.restrictions = restrictions;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public int getCancellationCutoffAmount() {
		return cancellationCutoffAmount;
	}

	public void setCancellationCutoffAmount(int cancellationCutoffAmount) {
		this.cancellationCutoffAmount = cancellationCutoffAmount;
	}

	public String getSubtitle() {
		return Utils.notNullString(subtitle);
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getAvailabilityLocalDateEnd() {
		return Utils.notNullString(availabilityLocalDateEnd);
	}

	public void setAvailabilityLocalDateEnd(String availabilityLocalDateEnd) {
		this.availabilityLocalDateEnd = availabilityLocalDateEnd;
	}

	public String getAvailabilityCutoffUnit() {
		return Utils.notNullString(availabilityCutoffUnit);
	}

	public void setAvailabilityCutoffUnit(String availabilityCutoffUnit) {
		this.availabilityCutoffUnit = availabilityCutoffUnit;
	}

	public String getFromPoint() {
		return Utils.notNullString(fromPoint);
	}

	public void setFromPoint(String fromPoint) {
		this.fromPoint = fromPoint;
	}

	@Override
	public int getIdentifier() {
		return 0;
	}

	@Override
	public boolean isValidModel() {
		return false;
	}


 	// --------------------------------------
	// region Pax ( age and allow ) Methods
	// --------------------------------------

	public BigBusUnitsItemModel getUnitByType(String type) {
		if (units == null || units.isEmpty()) return null;
		return units.stream()
				.filter(p -> type.equalsIgnoreCase(p.getType()))
				.findFirst()
				.orElse(null);
	}

	public boolean isAdultAllowed() {
		return containsType(AppConstants.ADULTS);
	}

	public boolean isChildrenAllowed() {
		return containsType(AppConstants.CHILD);
	}

	public boolean isInfantsAllowed() {
		return containsType(AppConstants.INFANT);
	}

	private boolean containsType(String type) {
		return units != null && !units.isEmpty() &&
				units.stream().anyMatch(p -> type.equalsIgnoreCase(p.getType()));
	}

	private String aduJustString(String value){
		return "(" + value + ")";
	}

	public String getAdultAge() {
		BigBusUnitsItemModel unit = getUnitByType(AppConstants.ADULTS);
		return unit != null ? aduJustString(unit.getSubtitle()) : "";
	}

	public String getChildAge() {
		BigBusUnitsItemModel unit = getUnitByType(AppConstants.CHILD);
		return unit != null ? aduJustString(unit.getSubtitle()) : "";
	}

	public String getInfantAge() {
		BigBusUnitsItemModel unit = getUnitByType(AppConstants.INFANT);
		return unit != null ? aduJustString(unit.getSubtitle()) : "";
	}


	// endregion
	// --------------------------------------
	// region Precising Methods
	// --------------------------------------

	private Float getPrice(String unitType, boolean isWithoutDiscount) {
		BigBusUnitsItemModel unit = getUnitByType(unitType);
		if (unit == null) return 0f;

		List<BigBusPricingFromItemModel> pricingList = unit.getPricingFrom();
		if (pricingList == null || pricingList.isEmpty()) return 0f;

		BigBusPricingFromItemModel tmpModel = pricingList.get(0);
		int amount = isWithoutDiscount ? tmpModel.getNet() : tmpModel.getWithoutDiscountNet();
		return  Utils.roundFloatToFloat((float) amount);

	}

	public Float getWithoutDiscountAdultPrice() {
		return getPrice(AppConstants.ADULTS, true);
	}

	public Float getWithoutDiscountChildPrice() {
		return getPrice(AppConstants.CHILD, true);
	}

	public Float getWithoutDiscountInfantPrice() {
		return getPrice(AppConstants.INFANT, true);
	}

	public Float getAdultPrice() {
		return getPrice(AppConstants.ADULTS, false);
	}

	public Float getChildPrice() {
		return getPrice(AppConstants.CHILD, false);
	}

	public Float getInfantPrice() {
		return getPrice(AppConstants.INFANT, false);
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

	// endregion
	// --------------------------------------
	// region Pax Selection Store and Date - Time Methods
	// --------------------------------------

	private int tmpAdultValue = 0;
	private int tmpChildValue = 0;
	private int tmpInfantValue = 0;
	private String tourOptionSelectDate = "";
	private OctaTourAvailabilityModel timeModel = null;
	private BigBusPickupPointsModel pickupPointsModel = null;
	private boolean isFirestTimeUpdate = true;

	public boolean isFirestTimeUpdate() {
		return isFirestTimeUpdate;
	}

	public void setFirestTimeUpdate(boolean firestTimeUpdate) {
		isFirestTimeUpdate = firestTimeUpdate;
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

	public boolean hasAtLeastOneMember(){
		return tmpAdultValue + tmpChildValue + tmpInfantValue != 0;
	}

	public String getTourOptionSelectDate() {
		return tourOptionSelectDate;
	}

	public void setTourOptionSelectDate(String tourOptionSelectDate) {
		this.tourOptionSelectDate = tourOptionSelectDate;
	}

	public Float updateAdultPrices() {return tmpAdultValue * getAdultPrice();}

	public Float updateChildPrices() {
		return tmpChildValue * getChildPrice();
	}

	public Float updateInfantPrices() {
		return tmpInfantValue * getInfantPrice();
	}

	public OctaTourAvailabilityModel getTimeModel() {
		return timeModel;
	}

	public void setTimeModel(OctaTourAvailabilityModel timeModel) {
		this.timeModel = timeModel;
	}

	public BigBusPickupPointsModel getPickupPointsModel() {
		return pickupPointsModel;
	}

	public void setPickupPointsModel(BigBusPickupPointsModel pickupPointsModel) {
		this.pickupPointsModel = pickupPointsModel;
	}

	public String getSlotText() {
		if (timeModel == null || timeModel.getOpeningHours() == null || timeModel.getOpeningHours().isEmpty()) {
			return "";
		}

		BigBusOpeningHoursModel slotModel = timeModel.getOpeningHours().get(0);
		return (slotModel != null && !TextUtils.isEmpty(slotModel.getFrom()) && !TextUtils.isEmpty(slotModel.getTo()))
				? slotModel.getFrom() + " - " + slotModel.getTo()
				: "";
	}

	public String getPickUpPoint(){
		if (pickupPointsModel == null){
			return "";
		}
		return pickupPointsModel.getName();
	}


	// endregion
	// --------------------------------------
	// region Other helper method
	// --------------------------------------


	public String getTourId() {
		RaynaTicketDetailModel model = RaynaTicketManager.shared.raynaTicketDetailModel;
		if (model == null || model.getBigBusTourDataModels().isEmpty()) return "";

		String id = model.getBigBusTourDataModels().get(0).getId();
		return TextUtils.isEmpty(id) ? "" : id;
	}

	public int getMaxNumOfPeople() {
		final int DEFAULT_VALUE = 1000;

		if (restrictions != null) {
			Object maxCount = restrictions.getMaxPaxCount();
			if (maxCount == null) {
				return DEFAULT_VALUE;
			}

			try {
				int value;
				if (maxCount instanceof Number) {
					value = ((Number) maxCount).intValue();
				} else if (maxCount instanceof String) {
					String str = ((String) maxCount).trim();
					if (str.isEmpty()) {
						return DEFAULT_VALUE;
					}
					value = (int) Double.parseDouble(str);
				} else {
					return DEFAULT_VALUE;
				}

				return (value <= 0) ? DEFAULT_VALUE : value;

			} catch (NumberFormatException e) {
				return DEFAULT_VALUE;
			}
		}

		return DEFAULT_VALUE;
	}

	public int getMinNumOfPeople() {
		final int DEFAULT_VALUE = 0;

		if (restrictions != null) {
			Object maxCount = restrictions.getMinPaxCount();
			if (maxCount == null) {
				return DEFAULT_VALUE;
			}

			try {
				int value;
				if (maxCount instanceof Number) {
					value = ((Number) maxCount).intValue();
				} else if (maxCount instanceof String) {
					String str = ((String) maxCount).trim();
					if (str.isEmpty() || str.equalsIgnoreCase("N/A") || str.equalsIgnoreCase("NA")) {
						return DEFAULT_VALUE;
					}
					value = (int) Double.parseDouble(str);
				} else {
					return DEFAULT_VALUE;
				}

				return (value <= 0) ? DEFAULT_VALUE : value;

			} catch (NumberFormatException e) {
				return DEFAULT_VALUE;
			}
		}

		return DEFAULT_VALUE;
	}


	public boolean isNonRefundable() {

		RaynaTicketDetailModel model = RaynaTicketManager.shared.raynaTicketDetailModel;

		if (model == null || TextUtils.isEmpty(model.getCancellationPolicy())) {
			return true;
		}

		return !model.getFreeCancellation();

//		String policy = Html.fromHtml(model.getCancellationPolicy(), Html.FROM_HTML_MODE_LEGACY)
//				.toString()
//				.trim();
//
//		return policy.equalsIgnoreCase("Non Refundable");
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

		// Pick Up
		if (!TextUtils.isEmpty(model.getPickup())){
			Optional<BigBusPickupPointsModel> pickUp = pickupPoints.stream().filter(p -> p.getName().equalsIgnoreCase(model.getPickup())).findFirst();
            pickUp.ifPresent(bigBusPickupPointsModel -> this.pickupPointsModel = bigBusPickupPointsModel);
		}

		// Time
		OctaTourAvailabilityModel octaTimeModel = new OctaTourAvailabilityModel();
		octaTimeModel.setId(model.getTourDate());

		String timeRange = model.getTimeSlot();
		String startTime = "";
		String endTime = "";

		if (!TextUtils.isEmpty(timeRange) && timeRange.contains("-")) {
			String[] parts = timeRange.split("-");
			if (parts.length == 2) {
				startTime = parts[0].trim();
				endTime = parts[1].trim();
			}
		}

		List<BigBusOpeningHoursModel> tmpTimeList = new ArrayList<>();
		BigBusOpeningHoursModel bigBusOpeningHoursModel = new BigBusOpeningHoursModel();
		bigBusOpeningHoursModel.setFrom(startTime);
		bigBusOpeningHoursModel.setTo(endTime);
		tmpTimeList.add(bigBusOpeningHoursModel);

		octaTimeModel.setOpeningHours(tmpTimeList);
		this.timeModel = octaTimeModel;
	}



	// endregion
	// --------------------------------------


}
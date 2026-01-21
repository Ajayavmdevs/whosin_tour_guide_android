package com.whosin.app.service.models.BigBusModels;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class BigBusTourDataModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("privacyTerms")
	@Expose
	private String privacyTerms = "";

	@SerializedName("country")
	@Expose
	private String country = "";

	@SerializedName("keywords")
	@Expose
	private List<String> keywords;

	@SerializedName("instantConfirmation")
	@Expose
	private boolean instantConfirmation = false;

	@SerializedName("exclusions")
	@Expose
	private List<String> exclusions;

	@SerializedName("reference")
	@Expose
	private String reference = "";

	@SerializedName("freesaleDurationAmount")
	@Expose
	private int freesaleDurationAmount;

	@SerializedName("availabilityType")
	@Expose
	private String availabilityType = "";

	@SerializedName("defaultCurrency")
	@Expose
	private String defaultCurrency = "";

	@SerializedName("supplier")
	@Expose
	private String supplier = "";

	@SerializedName("options")
	@Expose
	private List<BigBusOptionsItemModel> options;

	@SerializedName("id")
	@Expose
	private String id = "";

	@SerializedName("pricingPer")
	@Expose
	private String pricingPer = "";

	@SerializedName("pointToPoint")
	@Expose
	private boolean pointToPoint = false;

	@SerializedName("timeZone")
	@Expose
	private String timeZone = "";

	@SerializedName("shortDescription")
	@Expose
	private String shortDescription = "";

	@SerializedName("tags")
	@Expose
	private List<String> tags;

	@SerializedName("redemptionInstructions")
	@Expose
	private String redemptionInstructions = "";

	@SerializedName("faqs")
	@Expose
	private List<BigBusFaqsItemModel> faqs;

	@SerializedName("deliveryFormats")
	@Expose
	private List<String> deliveryFormats;

	@SerializedName("subtitle")
	@Expose
	private String subtitle = "";

	@SerializedName("tagline")
	@Expose
	private String tagline = "";

	@SerializedName("_id")
	@Expose
	private String _id = "";

	@SerializedName("instantDelivery")
	@Expose
	private boolean instantDelivery = false;

	@SerializedName("inclusions")
	@Expose
	private List<String> inclusions;

	@SerializedName("galleryImages")
	@Expose
	private List<BigBusGalleryImagesItemModel> galleryImages;

	@SerializedName("status")
	@Expose
	private boolean status;

	@SerializedName("freesaleDurationUnit")
	@Expose
	private String freesaleDurationUnit = "";

	@SerializedName("coverImageUrl")
	@Expose
	private String coverImageUrl = "";

	@SerializedName("destination")
	@Expose
	private BigBusDestinationModel destination;

	@SerializedName("description")
	@Expose
	private String description = "";

	@SerializedName("locale")
	@Expose
	private String locale = "";

	@SerializedName("deliveryMethods")
	@Expose
	private List<String> deliveryMethods;

	@SerializedName("title")
	@Expose
	private String title = "";

	@SerializedName("internalName")
	@Expose
	private String internalName = "";

	@SerializedName("settlementMethods")
	@Expose
	private List<String> settlementMethods;

	@SerializedName("videoUrl")
	@Expose
	private String  videoUrl = "";

	@SerializedName("alert")
	@Expose
	private String alert = "";

	@SerializedName("bookingTerms")
	@Expose
	private String bookingTerms = "";

	@SerializedName("bannerImageUrl")
	@Expose
	private String bannerImageUrl = "";

	@SerializedName("availableCurrencies")
	@Expose
	private List<String> availableCurrencies;

	@SerializedName("categories")
	@Expose
	private List<BigBusCategoriesItemModel> categories;

	@SerializedName("allowFreesale")
	@Expose
	private boolean allowFreesale = false;

	@SerializedName("availabilityRequired")
	@Expose
	private boolean availabilityRequired = false;

	@SerializedName("includeTax")
	@Expose
	private boolean includeTax = false;

	@SerializedName("redemptionMethod")
	@Expose
	private String redemptionMethod = "";

	@SerializedName("highlights")
	@Expose
	private List<String> highlights;

	@SerializedName("location")
	@Expose
	private String location = "";

	@SerializedName("cancellationPolicy")
	@Expose
	private String cancellationPolicy = "";

	@SerializedName("bannerImages")
	@Expose
	private List<String> bannerImages;

	public String getPrivacyTerms() {
		return Utils.notNullString(privacyTerms);
	}

	public void setPrivacyTerms(String privacyTerms) {
		this.privacyTerms = privacyTerms;
	}

	public String getCountry() {
		return Utils.notNullString(country);
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<String> getKeywords() {
		return Utils.notEmptyList(keywords);
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public boolean isInstantConfirmation() {
		return instantConfirmation;
	}

	public void setInstantConfirmation(boolean instantConfirmation) {
		this.instantConfirmation = instantConfirmation;
	}

	public List<String> getExclusions() {
		return Utils.notEmptyList(exclusions);
	}

	public void setExclusions(List<String> exclusions) {
		this.exclusions = exclusions;
	}

	public String getReference() {
		return Utils.notNullString(reference);
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public int getFreesaleDurationAmount() {
		return freesaleDurationAmount;
	}

	public void setFreesaleDurationAmount(int freesaleDurationAmount) {
		this.freesaleDurationAmount = freesaleDurationAmount;
	}

	public String getAvailabilityType() {
		return Utils.notNullString(availabilityType);
	}

	public void setAvailabilityType(String availabilityType) {
		this.availabilityType = availabilityType;
	}

	public String getDefaultCurrency() {
		return Utils.notNullString(defaultCurrency);
	}

	public void setDefaultCurrency(String defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public String getSupplier() {
		return Utils.notNullString(supplier);
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public List<BigBusOptionsItemModel> getOptions() {
		return Utils.notEmptyList(options);
	}

	public void setOptions(List<BigBusOptionsItemModel> options) {
		this.options = options;
	}

	public String getId() {
		return Utils.notNullString(id);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPricingPer() {
		return Utils.notNullString(pricingPer);
	}

	public void setPricingPer(String pricingPer) {
		this.pricingPer = pricingPer;
	}

	public boolean isPointToPoint() {
		return pointToPoint;
	}

	public void setPointToPoint(boolean pointToPoint) {
		this.pointToPoint = pointToPoint;
	}

	public String getTimeZone() {
		return Utils.notNullString(timeZone);
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getShortDescription() {
		return Utils.notNullString(shortDescription);
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public List<String> getTags() {
		return Utils.notEmptyList(tags);
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getRedemptionInstructions() {
		return Utils.notNullString(redemptionInstructions);
	}

	public void setRedemptionInstructions(String redemptionInstructions) {
		this.redemptionInstructions = redemptionInstructions;
	}

	public List<BigBusFaqsItemModel> getFaqs() {
		return Utils.notEmptyList(faqs);
	}

	public void setFaqs(List<BigBusFaqsItemModel> faqs) {
		this.faqs = faqs;
	}

	public List<String> getDeliveryFormats() {
		return Utils.notEmptyList(deliveryFormats);
	}

	public void setDeliveryFormats(List<String> deliveryFormats) {
		this.deliveryFormats = deliveryFormats;
	}

	public String getSubtitle() {
		return Utils.notNullString(subtitle);
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getTagline() {
		return Utils.notNullString(tagline);
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public String get_id() {
		return Utils.notNullString(_id);
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public boolean isInstantDelivery() {
		return instantDelivery;
	}

	public void setInstantDelivery(boolean instantDelivery) {
		this.instantDelivery = instantDelivery;
	}

	public List<String> getInclusions() {
		return Utils.notEmptyList(inclusions);
	}

	public void setInclusions(List<String> inclusions) {
		this.inclusions = inclusions;
	}

	public List<BigBusGalleryImagesItemModel> getGalleryImages() {
		return Utils.notEmptyList(galleryImages);
	}

	public void setGalleryImages(List<BigBusGalleryImagesItemModel> galleryImages) {
		this.galleryImages = galleryImages;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getFreesaleDurationUnit() {
		return Utils.notNullString(freesaleDurationUnit);
	}

	public void setFreesaleDurationUnit(String freesaleDurationUnit) {
		this.freesaleDurationUnit = freesaleDurationUnit;
	}

	public String getCoverImageUrl() {
		return Utils.notNullString(coverImageUrl);
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public BigBusDestinationModel getDestination() {
		return destination;
	}

	public void setDestination(BigBusDestinationModel destination) {
		this.destination = destination;
	}

	public String getDescription() {
		return Utils.notNullString(description);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocale() {
		return Utils.notNullString(locale);
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public List<String> getDeliveryMethods() {
		return Utils.notEmptyList(deliveryMethods);
	}

	public void setDeliveryMethods(List<String> deliveryMethods) {
		this.deliveryMethods = deliveryMethods;
	}

	public String getTitle() {
		return Utils.notNullString(title);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInternalName() {
		return Utils.notNullString(internalName);
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	public List<String> getSettlementMethods() {
		return Utils.notEmptyList(settlementMethods);
	}

	public void setSettlementMethods(List<String> settlementMethods) {
		this.settlementMethods = settlementMethods;
	}

	public String getVideoUrl() {
		return Utils.notNullString(videoUrl);
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getAlert() {
		return Utils.notNullString(alert);
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public String getBookingTerms() {
		return Utils.notNullString(bookingTerms);
	}

	public void setBookingTerms(String bookingTerms) {
		this.bookingTerms = bookingTerms;
	}

	public String getBannerImageUrl() {
		return Utils.notNullString(bannerImageUrl);
	}

	public void setBannerImageUrl(String bannerImageUrl) {
		this.bannerImageUrl = bannerImageUrl;
	}

	public List<String> getAvailableCurrencies() {
		return Utils.notEmptyList(availableCurrencies);
	}

	public void setAvailableCurrencies(List<String> availableCurrencies) {
		this.availableCurrencies = availableCurrencies;
	}

	public List<BigBusCategoriesItemModel> getCategories() {
		return Utils.notEmptyList(categories);
	}

	public void setCategories(List<BigBusCategoriesItemModel> categories) {
		this.categories = categories;
	}

	public boolean isAllowFreesale() {
		return allowFreesale;
	}

	public void setAllowFreesale(boolean allowFreesale) {
		this.allowFreesale = allowFreesale;
	}

	public boolean isAvailabilityRequired() {
		return availabilityRequired;
	}

	public void setAvailabilityRequired(boolean availabilityRequired) {
		this.availabilityRequired = availabilityRequired;
	}

	public boolean isIncludeTax() {
		return includeTax;
	}

	public void setIncludeTax(boolean includeTax) {
		this.includeTax = includeTax;
	}

	public String getRedemptionMethod() {
		return Utils.notNullString(redemptionMethod);
	}

	public void setRedemptionMethod(String redemptionMethod) {
		this.redemptionMethod = redemptionMethod;
	}

	public List<String> getHighlights() {
		return Utils.notEmptyList(highlights);
	}

	public void setHighlights(List<String> highlights) {
		this.highlights = highlights;
	}

	public String getLocation() {
		return Utils.notNullString(location);
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCancellationPolicy() {
		return Utils.notNullString(cancellationPolicy);
	}

	public void setCancellationPolicy(String cancellationPolicy) {
		this.cancellationPolicy = cancellationPolicy;
	}

	public List<String> getBannerImages() {
		return Utils.notEmptyList(bannerImages);
	}

	public void setBannerImages(List<String> bannerImages) {
		this.bannerImages = bannerImages;
	}

	@Override
	public int getIdentifier() {
		return 0;
	}

	@Override
	public boolean isValidModel() {
		return false;
	}
}
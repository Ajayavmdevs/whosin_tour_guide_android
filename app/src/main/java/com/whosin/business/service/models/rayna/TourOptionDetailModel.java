package com.whosin.business.service.models.rayna;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.HomeTicketsModel;
import com.whosin.business.service.models.ModelProtocol;
import com.whosin.business.service.models.whosinTicketModel.WhosinTicketTourOptionModel;

import java.util.List;

public class TourOptionDetailModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("id")
    @Expose
    private int myCartId;

    @SerializedName("customTicketId")
    @Expose
    private String customTicketId;

    @SerializedName("transferId")
    @Expose
    private String transferId;
    @SerializedName("xmlcode")
    @Expose
    private String xmlcode;
    @SerializedName("xmloptioncode")
    @Expose
    private String xmloptioncode;
    @SerializedName("minPax")
    @Expose
    private String minPax;
    @SerializedName("maxPax")
    @Expose
    private String maxPax;
    @SerializedName("isWithoutAdult")
    @Expose
    private Boolean isWithoutAdult;
    @SerializedName("isTourGuide")
    @Expose
    private String isTourGuide;
    @SerializedName("compulsoryOptions")
    @Expose
    private Boolean compulsoryOptions;
    @SerializedName("isHideRateBreakup")
    @Expose
    private Boolean isHideRateBreakup;
    @SerializedName("isHourly")
    @Expose
    private Boolean isHourly;
    @SerializedName("tourId")
    @Expose
    private String tourId;
    @SerializedName("tourOptionId")
    @Expose
    private String tourOptionId;
    @SerializedName("optionId")
    @Expose
    private String optionId;
    @SerializedName("optionName")
    @Expose
    private String optionName;
    @SerializedName("childAge")
    @Expose
    private String childAge;
    @SerializedName("infantAge")
    @Expose
    private String infantAge;
    @SerializedName("adultAge")
    @Expose
    private String adultAge;
    @SerializedName("optionDescription")
    @Expose
    private String optionDescription;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cancellationPolicy")
    @Expose
    private String cancellationPolicy;
    @SerializedName("cancellationPolicyDescription")
    @Expose
    private String cancellationPolicyDescription;
    @SerializedName("childPolicyDescription")
    @Expose
    private String childPolicyDescription;
    @SerializedName("countryId")
    @Expose
    private String countryId;
    @SerializedName("cityId")
    @Expose
    private String cityId;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("timeZone")
    @Expose
    private String timeZone;
    @SerializedName("countryName")
    @Expose
    private String countryName;
    @SerializedName("cityName")
    @Expose
    private String cityName;
    @SerializedName("tourName")
    @Expose
    private String tourName;
    @SerializedName("reviewCount")
    @Expose
    private Integer reviewCount;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("departurePoint")
    @Expose
    private String departurePoint;
    @SerializedName("reportingTime")
    @Expose
    private String reportingTime;
    @SerializedName("tourLanguage")
    @Expose
    private String tourLanguage;
    @SerializedName("imagePath")
    @Expose
    private String imagePath;
    @SerializedName("imageCaptionName")
    @Expose
    private String imageCaptionName;
    @SerializedName("cityTourTypeId")
    @Expose
    private String cityTourTypeId;
    @SerializedName("cityTourType")
    @Expose
    private String cityTourType;
    @SerializedName("tourDescription")
    @Expose
    private String tourDescription;
    @SerializedName("tourInclusion")
    @Expose
    private String tourInclusion;
    @SerializedName("tourShortDescription")
    @Expose
    private String tourShortDescription;
    @SerializedName("raynaToursAdvantage")
    @Expose
    private String raynaToursAdvantage;
    @SerializedName("whatsInThisTour")
    @Expose
    private String whatsInThisTour;
    @SerializedName("importantInformation")
    @Expose
    private String importantInformation;
    @SerializedName("itenararyDescription")
    @Expose
    private String itenararyDescription;
    @SerializedName("usefulInformation")
    @Expose
    private String usefulInformation;
    @SerializedName("faqDetails")
    @Expose
    private String faqDetails;
    @SerializedName("termsAndConditions")
    @Expose
    private String termsAndConditions;
    @SerializedName("cancellationPolicyName")
    @Expose
    private String cancellationPolicyName;
    @SerializedName("childCancellationPolicyName")
    @Expose
    private String childCancellationPolicyName;
    @SerializedName("childCancellationPolicyDescription")
    @Expose
    private String childCancellationPolicyDescription;
    @SerializedName("infantCount")
    @Expose
    private Integer infantCount;
    @SerializedName("isSlot")
    @Expose
    private Boolean isSlot;
    @SerializedName("isSeat")
    @Expose
    private Boolean isSeat;
    @SerializedName("onlyChild")
    @Expose
    private Boolean onlyChild;
    @SerializedName("recommended")
    @Expose
    private Boolean recommended;
    @SerializedName("contractId")
    @Expose
    private Object contractId;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("tourDate")
    @Expose
    private String tourDate;
    @SerializedName("meal")
    @Expose
    private Object meal;
    @SerializedName("videoUrl")
    @Expose
    private String videoUrl;
    @SerializedName("googleMapUrl")
    @Expose
    private String googleMapUrl;
    @SerializedName("tourExclusion")
    @Expose
    private String tourExclusion;
    @SerializedName("howToRedeem")
    @Expose
    private String howToRedeem;
    @SerializedName("tourImages")
    @Expose
    private List<TourImageModel> tourImages;

    @SerializedName("images")
    @Expose
    private List<String> images;
    @SerializedName("tourReview")
    @Expose
    private List<TourReviewModel> tourReview;
    @SerializedName("questions")
    @Expose
    private Object questions;

    @SerializedName("customData")
    @Expose
    private HomeTicketsModel customData;

    @SerializedName("withoutDiscountAmount")
    @Expose
    private Float withoutDiscountAmount;

    @SerializedName("finalAmount")
    @Expose
    private Float finalAmount;

    @SerializedName("departureTime")
    @Expose
    private String departureTime;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("bookingDates")
    @Expose
    private List<RaynaBookingDateModel> bookingDates;


    @SerializedName("operationdays")
    @Expose
    private RaynaOprationDaysModel operationdays;


    @SerializedName("exclusion")
    @Expose
    private String exclusion ;

    @SerializedName("inclusion")
    @Expose
    private String inclusion ;

    @SerializedName("availabilityType")
    @Expose
    private String availabilityType ;

    @SerializedName("availabilityTime")
    @Expose
    private String availabilityTime ;

    @SerializedName("name")
    @Expose
    private String name = "" ;

    @SerializedName("transferName")
    @Expose
    private String transferName = "" ;

    @SerializedName("title")
    @Expose
    private String title = "" ;

    @SerializedName("optionData")
    @Expose
    private List<WhosinTicketTourOptionModel> optionData  ;

    @SerializedName("displayName")
    @Expose
    private String displayName ;

    public String getDisplayName() {
        return Utils.notNullString(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<WhosinTicketTourOptionModel> getOptionData() {
        return Utils.notEmptyList(optionData);
    }

    public void setOptionData(List<WhosinTicketTourOptionModel> optionData) {
        this.optionData = optionData;
    }

    public String getTitle() {
        return Utils.notNullString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMyCartId() {
        return myCartId;
    }

    public void setMyCartId(int myCartId) {
        this.myCartId = myCartId;
    }

    public String getTransferName() {
        return Utils.notNullString(transferName);
    }

    public void setTransferName(String transferName) {
        this.transferName = transferName;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOptionId() {
        return Utils.notNullString(optionId);
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getCustomTicketId() {
        return Utils.notNullString(customTicketId);
    }

    public void setCustomTicketId(String customTicketId) {
        this.customTicketId = customTicketId;
    }

    public String getAvailabilityTime() {
        return Utils.notNullString(availabilityTime);
    }

    public void setAvailabilityTime(String availabilityTime) {
        this.availabilityTime = availabilityTime;
    }

    public String getAvailabilityType() {
        return Utils.notNullString(availabilityType);
    }

    public void setAvailabilityType(String availabilityType) {
        this.availabilityType = availabilityType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getXmlcode() {
        return xmlcode;
    }

    public void setXmlcode(String xmlcode) {
        this.xmlcode = xmlcode;
    }

    public String getXmloptioncode() {
        return xmloptioncode;
    }

    public void setXmloptioncode(String xmloptioncode) {
        this.xmloptioncode = xmloptioncode;
    }

    public String getMinPax() {
        return minPax;
    }

    public void setMinPax(String minPax) {
        this.minPax = minPax;
    }

    public String getMaxPax() {
        return maxPax;
    }

    public void setMaxPax(String maxPax) {
        this.maxPax = maxPax;
    }

    public Boolean getIsWithoutAdult() {
        return isWithoutAdult;
    }

    public void setIsWithoutAdult(Boolean isWithoutAdult) {
        this.isWithoutAdult = isWithoutAdult;
    }

    public String getIsTourGuide() {
        return isTourGuide;
    }

    public void setIsTourGuide(String isTourGuide) {
        this.isTourGuide = isTourGuide;
    }

    public Boolean getCompulsoryOptions() {
        return compulsoryOptions;
    }

    public void setCompulsoryOptions(Boolean compulsoryOptions) {
        this.compulsoryOptions = compulsoryOptions;
    }

    public Boolean getIsHideRateBreakup() {
        return isHideRateBreakup;
    }

    public void setIsHideRateBreakup(Boolean isHideRateBreakup) {
        this.isHideRateBreakup = isHideRateBreakup;
    }

    public Boolean getIsHourly() {
        return isHourly;
    }

    public void setIsHourly(Boolean isHourly) {
        this.isHourly = isHourly;
    }

    public String getTourId() {
        return Utils.notNullString(tourId);
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getTourOptionId() {
        return tourOptionId;
    }

    public void setTourOptionId(String tourOptionId) {
        this.tourOptionId = tourOptionId;
    }

    public String getOptionName() {
        return Utils.notNullString(optionName);
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getChildAge() {
        return childAge;
    }

    public void setChildAge(String childAge) {
        this.childAge = childAge;
    }

    public String getInfantAge() {
        return infantAge;
    }

    public void setInfantAge(String infantAge) {
        this.infantAge = infantAge;
    }

    public String getOptionDescription() {
        return Utils.notNullString(optionDescription);
    }

    public void setOptionDescription(String optionDescription) {
        this.optionDescription = optionDescription;
    }


    public String getDescription() {
        return Utils.notNullString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCancellationPolicy() {
        return cancellationPolicy.trim();
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public String getCancellationPolicyDescription() {
        return Utils.notNullString(cancellationPolicyDescription);
    }

    public void setCancellationPolicyDescription(String cancellationPolicyDescription) {
        this.cancellationPolicyDescription = cancellationPolicyDescription;
    }

//    public  String getAdultAge() {
//        List<Integer> numbers = new ArrayList<>();
//        Matcher matcher = Pattern.compile("\\d+").matcher(childAge);
//
//        while (matcher.find()) {
//            numbers.add(Integer.parseInt(matcher.group()));
//        }
//
//        if (!numbers.isEmpty()) {
//            int maxChildAge = Collections.max(numbers);
//            return (maxChildAge) + "+ yrs";
//        }
//
//        return "";
//
//
//    }

    public String getAdultAge() {
        // Check if adultPaxAge is not null or empty
        if (!TextUtils.isEmpty(adultAge)) {
            // Check if adultPaxAge contains "yrs" (case-insensitive)
            if (adultAge.toLowerCase().contains("yrs")) {
                return adultAge;
            } else {
                return adultAge + "+ yrs";
            }
        }

        // Process childAge to extract numbers
        String[] numbers = childAge.split("[^0-9]");
        int maxChildAge = 0;
        boolean hasValidNumber = false;

        for (String num : numbers) {
            if (!num.isEmpty()) {
                try {
                    int value = Integer.parseInt(num);
                    if (value > maxChildAge) {
                        maxChildAge = value;
                        hasValidNumber = true;
                    }
                } catch (NumberFormatException e) {
                    // Ignore non-numeric parts
                }
            }
        }

        // Return maxChildAge + "+ yrs" if valid and positive, else empty string
        if (hasValidNumber && maxChildAge > 0) {
            return maxChildAge + "+ yrs";
        }
        return "";
    }

    public String getChildPolicyDescription() {
        return childPolicyDescription;
    }

    public void setChildPolicyDescription(String childPolicyDescription) {
        this.childPolicyDescription = childPolicyDescription;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getDuration() {
        return Utils.notNullString(duration);
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getDeparturePoint() {
        return departurePoint;
    }

    public void setDeparturePoint(String departurePoint) {
        this.departurePoint = departurePoint;
    }

    public String getReportingTime() {
        return reportingTime;
    }

    public void setReportingTime(String reportingTime) {
        this.reportingTime = reportingTime;
    }

    public String getTourLanguage() {
        return tourLanguage;
    }

    public void setTourLanguage(String tourLanguage) {
        this.tourLanguage = tourLanguage;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageCaptionName() {
        return imageCaptionName;
    }

    public void setImageCaptionName(String imageCaptionName) {
        this.imageCaptionName = imageCaptionName;
    }

    public String getCityTourTypeId() {
        return cityTourTypeId;
    }

    public void setCityTourTypeId(String cityTourTypeId) {
        this.cityTourTypeId = cityTourTypeId;
    }

    public String getCityTourType() {
        return cityTourType;
    }

    public void setCityTourType(String cityTourType) {
        this.cityTourType = cityTourType;
    }

    public String getTourDescription() {
        return tourDescription;
    }

    public void setTourDescription(String tourDescription) {
        this.tourDescription = tourDescription;
    }

    public String getTourInclusion() {
        return tourInclusion;
    }

    public void setTourInclusion(String tourInclusion) {
        this.tourInclusion = tourInclusion;
    }

    public String getTourShortDescription() {
        return tourShortDescription;
    }

    public void setTourShortDescription(String tourShortDescription) {
        this.tourShortDescription = tourShortDescription;
    }

    public String getRaynaToursAdvantage() {
        return raynaToursAdvantage;
    }

    public void setRaynaToursAdvantage(String raynaToursAdvantage) {
        this.raynaToursAdvantage = raynaToursAdvantage;
    }

    public String getWhatsInThisTour() {
        return whatsInThisTour;
    }

    public void setWhatsInThisTour(String whatsInThisTour) {
        this.whatsInThisTour = whatsInThisTour;
    }

    public String getImportantInformation() {
        return importantInformation;
    }

    public void setImportantInformation(String importantInformation) {
        this.importantInformation = importantInformation;
    }

    public String getItenararyDescription() {
        return itenararyDescription;
    }

    public void setItenararyDescription(String itenararyDescription) {
        this.itenararyDescription = itenararyDescription;
    }

    public String getUsefulInformation() {
        return usefulInformation;
    }

    public void setUsefulInformation(String usefulInformation) {
        this.usefulInformation = usefulInformation;
    }

    public String getFaqDetails() {
        return faqDetails;
    }

    public void setFaqDetails(String faqDetails) {
        this.faqDetails = faqDetails;
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

    public String getChildCancellationPolicyName() {
        return childCancellationPolicyName;
    }

    public void setChildCancellationPolicyName(String childCancellationPolicyName) {
        this.childCancellationPolicyName = childCancellationPolicyName;
    }

    public String getChildCancellationPolicyDescription() {
        return childCancellationPolicyDescription;
    }

    public void setChildCancellationPolicyDescription(String childCancellationPolicyDescription) {
        this.childCancellationPolicyDescription = childCancellationPolicyDescription;
    }

    public Integer getInfantCount() {
        return infantCount;
    }

    public void setInfantCount(Integer infantCount) {
        this.infantCount = infantCount;
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

    public Boolean getOnlyChild() {
        return onlyChild;
    }

    public void setOnlyChild(Boolean onlyChild) {
        this.onlyChild = onlyChild;
    }

    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
    }

    public Object getContractId() {
        return contractId;
    }

    public void setContractId(Object contractId) {
        this.contractId = contractId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Object getMeal() {
        return meal;
    }

    public void setMeal(Object meal) {
        this.meal = meal;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getGoogleMapUrl() {
        return googleMapUrl;
    }

    public void setGoogleMapUrl(String googleMapUrl) {
        this.googleMapUrl = googleMapUrl;
    }

    public String getTourExclusion() {
        return tourExclusion;
    }

    public void setTourExclusion(String tourExclusion) {
        this.tourExclusion = tourExclusion;
    }

    public String getHowToRedeem() {
        return howToRedeem;
    }

    public void setHowToRedeem(String howToRedeem) {
        this.howToRedeem = howToRedeem;
    }

    public List<TourImageModel> getTourImages() {
        return tourImages;
    }

    public void setTourImages(List<TourImageModel> tourImages) {
        this.tourImages = tourImages;
    }

    public List<TourReviewModel> getTourReview() {
        return tourReview;
    }

    public void setTourReview(List<TourReviewModel> tourReview) {
        this.tourReview = tourReview;
    }

    public Object getQuestions() {
        return questions;
    }

    public void setQuestions(Object questions) {
        this.questions = questions;
    }

    public Boolean getWithoutAdult() {
        return isWithoutAdult;
    }

    public void setWithoutAdult(Boolean withoutAdult) {
        isWithoutAdult = withoutAdult;
    }

    public Boolean getHideRateBreakup() {
        return isHideRateBreakup;
    }

    public void setHideRateBreakup(Boolean hideRateBreakup) {
        isHideRateBreakup = hideRateBreakup;
    }

    public Boolean getHourly() {
        return isHourly;
    }

    public void setHourly(Boolean hourly) {
        isHourly = hourly;
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

    public HomeTicketsModel getCustomData() {
        if (customData == null) return new HomeTicketsModel();
        return customData;
    }

    public void setCustomData(HomeTicketsModel customData) {
        this.customData = customData;
    }

    public String getTourDate() {
        return tourDate;
    }

    public void setTourDate(String tourDate) {
        this.tourDate = tourDate;
    }

    public List<String> getImages() {
        return Utils.notEmptyList(images);
    }

    public void setImages(List<String> images) {
        this.images = images;
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


    public Float getWithoutDiscountAmount() {
        return withoutDiscountAmount;
    }

    public void setWithoutDiscountAmount(Float withoutDiscountAmount) {
        this.withoutDiscountAmount = withoutDiscountAmount;
    }

    public Float getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Float finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getDepartureTime() {
        return Utils.notNullString(departureTime);
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
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

    public List<RaynaBookingDateModel> getBookingDates() {
        return Utils.notEmptyList(bookingDates);
    }

    public void setBookingDates(List<RaynaBookingDateModel> bookingDates) {
        this.bookingDates = bookingDates;
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
}

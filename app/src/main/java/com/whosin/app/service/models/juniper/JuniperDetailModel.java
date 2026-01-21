package com.whosin.app.service.models.juniper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.R;
import com.whosin.app.comman.CustomTypefaceSpan;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.CurrentUserRatingModel;
import com.whosin.app.service.models.LocationModel;
import com.whosin.app.service.models.ModelProtocol;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.YachtFeatureModel;
import com.whosin.app.service.models.rayna.RaynaBookingDateModel;
import com.whosin.app.service.models.rayna.TourOptionDetailModel;
import com.whosin.app.service.models.rayna.TourOptionsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class JuniperDetailModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("badge")
    @Expose
    private String badge;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("images")
    @Expose
    private List<String> images;
    @SerializedName("categoryIds")
    @Expose
    private List<Object> categoryIds;
    @SerializedName("subCategoryIds")
    @Expose
    private List<Object> subCategoryIds;
    @SerializedName("bookingType")
    @Expose
    private String bookingType;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("startingAmount")
    @Expose
    private Object startingAmount;
    @SerializedName("startingAmountWithoutDiscount")
    @Expose
    private Object startingAmountWithoutDiscount;
    @SerializedName("isFreeCancellation")
    @Expose
    private Boolean isFreeCancellation;
    @SerializedName("cancellationPolicy")
    @Expose
    private String cancellationPolicy;
    @SerializedName("cancellationPolicyRules")
    @Expose
    private List<Object> cancellationPolicyRules;
    @SerializedName("features")
    @Expose
    private List<YachtFeatureModel> features;
    @SerializedName("whatsInclude")
    @Expose
    private List<YachtFeatureModel> whatsInclude;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("tourData")
    @Expose
    private List<JuniperTourDataModel> tourData;

    @SerializedName("discount")
    @Expose
    private int discount = 0;

    @SerializedName("tourId")
    @Expose
    private String tourId ;

    @SerializedName("contractId")
    @Expose
    private String contractId ;

    @SerializedName("adultAge")
    private String adultAge = "";

    @SerializedName("allowAdult")
    private boolean allowAdult = false;

    @SerializedName("allowChild")
    private boolean allowChild = false;

    @SerializedName("allowInfant")
    private boolean allowInfant = false;

    @SerializedName("cancellationPolicyDescription")
    private String cancellationPolicyDescription = "";

    @SerializedName("childAge")
    private String childAge = "";

    @SerializedName("childPolicy")
    private String childPolicy = "";

    @SerializedName("inclusion")
    private String inclusion = "";

    @SerializedName("infantAge")
    private String infantAge = "";

    @SerializedName("isEnableRating")
    private boolean isEnableRating = false;

    @SerializedName("isEnableReview")
    private boolean isEnableReview = false;

    @SerializedName("isReviewVisible")
    private boolean isReviewVisible = false;

    @SerializedName("markup")
    private int markup = 0;

    @SerializedName("maximumPax")
    private String maximumPax = "";

    @SerializedName("minimumPax")
    private String minimumPax = "";

    @SerializedName("overview")
    private String overview = "";

    @SerializedName("vat")
    private boolean vat = false;

    @SerializedName("vatPercentage")
    private int vatPercentage = 0;

    @SerializedName("reviews")
    private List<com.whosin.app.service.models.CurrentUserRatingModel> reviews ;

    @SerializedName("users")
    private List<UserDetailModel> users ;

    @SerializedName("avg_ratings")
    private double avg_ratings ;

    @SerializedName("currentUserReview")
    private CurrentUserRatingModel CurrentUserRatingModel ;

    @SerializedName("tourOptionData")
    @Expose
    private List<TourOptionsModel> tourOptionData;

    @SerializedName("tourExclusion")
    @Expose
    private String tourExclusion = "";

    @SerializedName("importantInformation")
    @Expose
    private String importantInformation = "";

    @SerializedName("usefulInformation")
    @Expose
    private String usefulInformation = "";

    @SerializedName("faqDetails")
    @Expose
    private String faqDetails = "";

    @SerializedName("howToRedeem")
    @Expose
    private String howToRedeem = "";

    @SerializedName("location")
    @Expose
    private LocationModel location;

    @SerializedName("departurePoint")
    @Expose
    private String departurePoint;

    @SerializedName("bookingStartDate")
    @Expose
    private String bookingStartDate;

    @SerializedName("bookingEndDate")
    @Expose
    private String bookingEndDate;

    @SerializedName("is_favorite")
    @Expose
    private boolean is_favorite = false;

    @SerializedName("bookingDates")
    @Expose
    private List<RaynaBookingDateModel> bookingDates;

    @SerializedName("tags")
    @Expose
    private List<String> tags;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getImages() {
        return Utils.notEmptyList(images);
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Object> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Object> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<Object> getSubCategoryIds() {
        return subCategoryIds;
    }

    public void setSubCategoryIds(List<Object> subCategoryIds) {
        this.subCategoryIds = subCategoryIds;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getStartingAmount() {
        return startingAmount;
    }

    public void setStartingAmount(Object startingAmount) {
        this.startingAmount = startingAmount;
    }

    public Object getStartingAmountWithoutDiscount() {
        return startingAmountWithoutDiscount;
    }

    public void setStartingAmountWithoutDiscount(Object startingAmountWithoutDiscount) {
        this.startingAmountWithoutDiscount = startingAmountWithoutDiscount;
    }

    public Boolean getIsFreeCancellation() {
        return isFreeCancellation;
    }

    public void setIsFreeCancellation(Boolean isFreeCancellation) {
        this.isFreeCancellation = isFreeCancellation;
    }

    public String getCancellationPolicy() {
        return cancellationPolicy;
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public List<Object> getCancellationPolicyRules() {
        return cancellationPolicyRules;
    }

    public void setCancellationPolicyRules(List<Object> cancellationPolicyRules) {
        this.cancellationPolicyRules = cancellationPolicyRules;
    }

    public List<YachtFeatureModel> getFeatures() {
        return Utils.notEmptyList(features);
    }

    public void setFeatures(List<YachtFeatureModel> features) {
        this.features = features;
    }

    public List<YachtFeatureModel> getWhatsInclude() {
        return Utils.notEmptyList(whatsInclude);
    }

    public void setWhatsInclude(List<YachtFeatureModel> whatsInclude) {
        this.whatsInclude = whatsInclude;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<JuniperTourDataModel> getTourData() {
        return Utils.notEmptyList(tourData);
    }

    public void setTourData(List<JuniperTourDataModel> tourData) {
        this.tourData = tourData;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public Boolean getFreeCancellation() {
        return isFreeCancellation;
    }

    public void setFreeCancellation(Boolean freeCancellation) {
        isFreeCancellation = freeCancellation;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getAdultAge() {
        return Utils.notNullString(adultAge);
    }

    public void setAdultAge(String adultAge) {
        this.adultAge = adultAge;
    }

    public boolean isAllowAdult() {
        return allowAdult;
    }

    public void setAllowAdult(boolean allowAdult) {
        this.allowAdult = allowAdult;
    }

    public boolean isAllowChild() {
        return allowChild;
    }

    public void setAllowChild(boolean allowChild) {
        this.allowChild = allowChild;
    }

    public boolean isAllowInfant() {
        return allowInfant;
    }

    public void setAllowInfant(boolean allowInfant) {
        this.allowInfant = allowInfant;
    }

    public String getCancellationPolicyDescription() {
        return Utils.notNullString(cancellationPolicyDescription);
    }

    public void setCancellationPolicyDescription(String cancellationPolicyDescription) {
        this.cancellationPolicyDescription = cancellationPolicyDescription;
    }

    public String getChildAge() {
        return Utils.notNullString(childAge);
    }

    public void setChildAge(String childAge) {
        this.childAge = childAge;
    }

    public String getChildPolicy() {
        return Utils.notNullString(childPolicy);
    }

    public void setChildPolicy(String childPolicy) {
        this.childPolicy = childPolicy;
    }

    public String getInclusion() {
        return Utils.notNullString(inclusion);
    }

    public void setInclusion(String inclusion) {
        this.inclusion = inclusion;
    }

    public String getInfantAge() {
        return Utils.notNullString(infantAge);
    }

    public void setInfantAge(String infantAge) {
        this.infantAge = infantAge;
    }

    public boolean isEnableRating() {
        return isEnableRating;
    }

    public void setEnableRating(boolean enableRating) {
        isEnableRating = enableRating;
    }

    public boolean isEnableReview() {
        return isEnableReview;
    }

    public void setEnableReview(boolean enableReview) {
        isEnableReview = enableReview;
    }

    public int getMarkup() {
        return markup;
    }

    public void setMarkup(int markup) {
        this.markup = markup;
    }

    public String getMaximumPax() {
        return Utils.notNullString(maximumPax);
    }

    public void setMaximumPax(String maximumPax) {
        this.maximumPax = maximumPax;
    }

    public String getMinimumPax() {
        return Utils.notNullString(minimumPax);
    }

    public void setMinimumPax(String minimumPax) {
        this.minimumPax = minimumPax;
    }

    public String getOverview() {
        return Utils.notNullString(overview);
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public boolean isVat() {
        return vat;
    }

    public void setVat(boolean vat) {
        this.vat = vat;
    }

    public int getVatPercentage() {
        return vatPercentage;
    }

    public void setVatPercentage(int vatPercentage) {
        this.vatPercentage = vatPercentage;
    }

    public List<CurrentUserRatingModel> getReviews() {
        return Utils.notEmptyList(reviews);
    }

    public void setReviews(List<CurrentUserRatingModel> reviews) {
        this.reviews = reviews;
    }

    public List<UserDetailModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserDetailModel> users) {
        this.users = users;
    }

    public double getAvg_ratings() {
        return avg_ratings;
    }

    public void setAvg_ratings(double avg_ratings) {
        this.avg_ratings = avg_ratings;
    }

    public CurrentUserRatingModel getCurrentUserRatingModel() {
        if (CurrentUserRatingModel == null){
            return new CurrentUserRatingModel();
        }
        return CurrentUserRatingModel;
    }

    public void setCurrentUserRatingModel(CurrentUserRatingModel currentUserRatingModel) {
        CurrentUserRatingModel = currentUserRatingModel;
    }

    public boolean isReviewVisible() {
        return isReviewVisible;
    }

    public void setReviewVisible(boolean reviewVisible) {
        isReviewVisible = reviewVisible;
    }

    public List<TourOptionsModel> getTourOptionData() {
        return Utils.notEmptyList(tourOptionData);
    }

    public void setTourOptionData(List<TourOptionsModel> tourOptionData) {
        this.tourOptionData = tourOptionData;
    }

    public String getTourExclusion() {
        return Utils.notNullString(tourExclusion);
    }

    public void setTourExclusion(String tourExclusion) {
        this.tourExclusion = tourExclusion;
    }

    public String getImportantInformation() {
        return Utils.notNullString(importantInformation);
    }

    public void setImportantInformation(String importantInformation) {
        this.importantInformation = importantInformation;
    }

    public String getUsefulInformation() {
        return Utils.notNullString(usefulInformation);
    }

    public void setUsefulInformation(String usefulInformation) {
        this.usefulInformation = usefulInformation;
    }

    public String getFaqDetails() {
        return Utils.notNullString(faqDetails);
    }

    public void setFaqDetails(String faqDetails) {
        this.faqDetails = faqDetails;
    }

    public String getHowToRedeem() {
        return Utils.notNullString(howToRedeem);
    }

    public void setHowToRedeem(String howToRedeem) {
        this.howToRedeem = howToRedeem;
    }

    public LocationModel getLocation() {
        return location;
    }

    public void setLocation(LocationModel location) {
        this.location = location;
    }

    public String getLatLng() {
        if (location == null || location.getCoordinates() == null) {
            return "";
        }
        return location.getCoordinates().size() < 2 ? "" : location.getCoordinates().get(1) + "," +
                location.getCoordinates().get(0);
    }

    public String getLongitude() {
        if (location == null || location.getCoordinates() == null) {
            return "";
        }
        return location.getCoordinates().size() < 2 ? "" : "" + location.getCoordinates().get(1);
    }

    public String getLatitude() {
        if (location == null || location.getCoordinates() == null) {
            return "";
        }
        return location.getCoordinates().size() < 2 ? "" : "" + location.getCoordinates().get(0);
    }

    public String getDeparturePoint() {
        return Utils.notNullString(departurePoint);
    }

    public void setDeparturePoint(String departurePoint) {
        this.departurePoint = departurePoint;
    }

    public String getBookingStartDate() {
        return bookingStartDate;
    }

    public void setBookingStartDate(String bookingStartDate) {
        this.bookingStartDate = bookingStartDate;
    }

    public String getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(String bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public int getTmpMinPax() {
        if ("NA".equals(minimumPax)) {
            return 0;
        }
        try {
            return Integer.parseInt(minimumPax);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getTmpMaxPax() {
        if (maximumPax == null || "NA".equals(maximumPax)) {
            return 1000;
        }
        try {
            return Integer.parseInt(maximumPax);
        } catch (NumberFormatException e) {
            return 1000;
        }
    }

    public List<RaynaBookingDateModel> getBookingDates() {
        return bookingDates;
    }

    public void setBookingDates(List<RaynaBookingDateModel> bookingDates) {
        this.bookingDates = bookingDates;
    }

    public boolean isIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    public List<String> getAllDatesFromBookingDates(boolean isAllowTodayBooking, List<String> allowDays, boolean isPickDateInTour, TourOptionDetailModel tourOptionDetailModel) {
        List<String> allDates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        if (isPickDateInTour && tourOptionDetailModel != null && tourOptionDetailModel.getBookingDates() != null && !tourOptionDetailModel.getBookingDates().isEmpty()){
            for (RaynaBookingDateModel bookingDate : tourOptionDetailModel.getBookingDates()) {
                Date startDate = parseFlexibleDate(bookingDate.getFromDate());
                Date endDate = parseFlexibleDate(bookingDate.getToDate());

                Calendar startCal = Calendar.getInstance();
                startCal.setTime(startDate);

                Calendar endCal = Calendar.getInstance();
                endCal.setTime(endDate);

                while (!startCal.after(endCal)) {
                    allDates.add(dateFormat.format(startCal.getTime()));
                    startCal.add(Calendar.DAY_OF_MONTH, 1);
                }

            }
        }
        else if (bookingDates != null && !bookingDates.isEmpty()){
            for (RaynaBookingDateModel bookingDate : bookingDates) {
                Date startDate = parseFlexibleDate(bookingDate.getStartDate());
                Date endDate = parseFlexibleDate(bookingDate.getEndDate());

                Calendar startCal = Calendar.getInstance();
                startCal.setTime(startDate);

                Calendar endCal = Calendar.getInstance();
                endCal.setTime(endDate);

                while (!startCal.after(endCal)) {
                    allDates.add(dateFormat.format(startCal.getTime()));
                    startCal.add(Calendar.DAY_OF_MONTH, 1);
                }

            }
        } else {
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.add(Calendar.YEAR, 1);
            while (!startCalendar.after(endCalendar)) {
                allDates.add(dateFormat.format(startCalendar.getTime()));
                startCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        if (!allDates.isEmpty() && !isAllowTodayBooking){
            String todayStr = dateFormat.format(new Date());
            allDates.removeIf(date -> date.equals(todayStr));
        }

        if (!allDates.isEmpty()) {
            String todayStr = dateFormat.format(new Date());

            allDates.removeIf(dateStr -> {
                try {
                    Date date = dateFormat.parse(dateStr);
                    Date today = dateFormat.parse(todayStr);
                    return date.before(today);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return false;
                }
            });
        }


        if (!allDates.isEmpty()){
            String[] allWeekdays = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};

            List<String> filteredDates = new ArrayList<>();
            for (String dateStr : allDates) {
                try {
                    Date date = dateFormat.parse(dateStr);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);

                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    String dayName = allWeekdays[dayOfWeek - 1];


                    if (allowDays.contains(dayName)) {
                        filteredDates.add(dateStr);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            allDates = filteredDates;


        }

        return allDates;
    }

    public List<String> getTags() {
        return Utils.notEmptyList(tags);
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isTicketRecentlyAdded(){
        if (tags == null) return false;
        if (tags.isEmpty()) return false;
        for (String data : tags){
            if (data.equals("Recently added")){
                return true;
            }
        }
        return false;
    }


    private static Date parseFlexibleDate(String dateStr) {
        List<String> patterns = Arrays.asList(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd"
        );

        for (String pattern : patterns) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
                format.setTimeZone(TimeZone.getDefault()); // Treat all input as local

                Date parsed = format.parse(dateStr);

                SimpleDateFormat dateOnly = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                dateOnly.setTimeZone(TimeZone.getDefault());

                String dateStrOnly = dateOnly.format(parsed);
                return dateOnly.parse(dateStrOnly);
            } catch (ParseException ignored) {}
        }

        System.err.println("Could not parse date: " + dateStr);
        return null;
    }

    public SpannableStringBuilder getDiscountAndStartingAmount(Activity activity, TextView textView){
        String startingAmount = getStartingAmount() != null ? String.valueOf(getStartingAmount()) : "N/A";
        String discountAmount = getStartingAmountWithoutDiscount() != null ? String.valueOf(getStartingAmountWithoutDiscount()) : "N/A";

        String tmpDiscountAmount = "";
        String amount = Utils.roundFloatValue(Float.valueOf(startingAmount));
        SpannableStringBuilder fullText = new SpannableStringBuilder();


        if (!"N/A".equals(discountAmount) && !discountAmount.equals("0") && !startingAmount.equals(discountAmount)) {
            tmpDiscountAmount = Utils.removePoint(discountAmount);
            SpannableString strikeAmount = Utils.getStyledText(activity, tmpDiscountAmount);
            strikeAmount.setSpan(new StrikethroughSpan(), 0, tmpDiscountAmount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            textView.setVisibility(View.VISIBLE);
            textView.setText(String.format("%s ", strikeAmount));

            textView.setText(strikeAmount);
        }else {
            textView.setVisibility(View.GONE);
        }


        SpannableString styledPrice = Utils.getStyledText(activity, amount);
        fullText.append(styledPrice);
        return fullText;
    }

    public static SpannableString getStyledText(Context context, String originalText) {
        SpannableString spannable = new SpannableString("D" + originalText);

        Typeface dTypeface = ResourcesCompat.getFont(context, R.font.aed_regular);
        if (dTypeface != null) {
            spannable.setSpan(new CustomTypefaceSpan(dTypeface), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannable;
    }
}

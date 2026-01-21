package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class PromoterNewEventDetailModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("customVenue")
    @Expose
    private CustomVenueModel customVenue;
    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("userId")
    @Expose
    private String userId = "";
    @SerializedName("cloneId")
    @Expose
    private String cloneId = "";
    @SerializedName("image")
    @Expose
    private String image = "";
    @SerializedName("description")
    @Expose
    private String description = "";
    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("venueType")
    @Expose
    private String venueType = "";
    @SerializedName("venueId")
    @Expose
    private String venueId = "";
    @SerializedName("offerId")
    @Expose
    private String offerId;
    @SerializedName("date")
    @Expose
    private String date = "";
    @SerializedName("startTime")
    @Expose
    private String startTime = "";
    @SerializedName("endTime")
    @Expose
    private String endTime = "";
    @SerializedName("dressCode")
    @Expose
    private String dressCode = "";
    @SerializedName("maxInvitee")
    @Expose
    private int maxInvitee = 0;
    @SerializedName("requirementsAllowed")
    @Expose
    private List<String> requirementsAllowed;
    @SerializedName("requirementsNotAllowed")
    @Expose
    private List<String> requirementsNotAllowed;
    @SerializedName("benefitsIncluded")
    @Expose
    private List<String> benefitsIncluded;
    @SerializedName("benefitsNotIncluded")
    @Expose
    private List<String> benefitsNotIncluded;
    @SerializedName("socialAccountsToMention")
    @Expose
    private List<SocialAccountsToMentionModel> socialAccountsToMention;
    @SerializedName("status")
    @Expose
    private String status = "";
    @SerializedName("isDeleted")
    @Expose
    private boolean isDeleted;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;
    @SerializedName("user")
    @Expose
    private UserDetailModel user;
    @SerializedName("invitedUsers")
    @Expose
    private List<InvitedUserModel> invitedUsers;
    @SerializedName("invitedCircles")
    @Expose
    private List<String> invitedCircles;
    @SerializedName("inMembers")
    @Expose
    private List<InvitedUserModel> inMembers;

    @SerializedName("inviteCancelList")
    @Expose
    private List<InvitedUserModel> inviteCancelList;

    @SerializedName("invite")
    @Expose
    private InvitedUserModel invite;

    @SerializedName("isEventFull")
    @Expose
    private boolean isEventFull = false;
    @SerializedName("isWishlisted")
    @Expose
    private boolean isWishlisted = false;

    @SerializedName("totalInMembers")
    @Expose
    private int totalInMembers = 0;

    @SerializedName("isConfirmationRequired")
    @Expose
    private boolean isConfirmationRequired = false;

    @SerializedName("invitedGender")
    @Expose
    private String invitedGender = "";

    @SerializedName("repeat")
    @Expose
    private String repeat = "";

    @SerializedName("repeatDate")
    @Expose
    private String repeatDate = "";

    @SerializedName("repeatCount")
    @Expose
    private int repeatCount;

    @SerializedName("interestedMembers")
    @Expose
    private List<InvitedUserModel> interestedMembers;

    @SerializedName("offer")
    @Expose
    private OffersModel offer;

    @SerializedName("latitude")
    @Expose
    private double latitude;

    @SerializedName("longitude")
    @Expose
    private double longitude;

    @SerializedName("distance")
    @Expose
    private double distance;

    @SerializedName("isHidden")
    @Expose
    private boolean isHidden = false;

    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("maleSeats")
    @Expose
    private int maleSeats;
    @SerializedName("femaleSeats")
    @Expose
    private int femaleSeats;
    @SerializedName("plusOneAccepted")
    @Expose
    private boolean plusOneAccepted;

    @SerializedName("plusOneQty")
    @Expose
    private int plusOneQty;

    @SerializedName("extraGuestType")
    @Expose
    private String  extraGuestType;

    @SerializedName("extraGuestAge")
    @Expose
    private String  extraGuestAge;

    @SerializedName("extraGuestDressCode")
    @Expose
    private String  extraGuestDressCode;

    @SerializedName("extraGuestGender")
    @Expose
    private String  extraGuestGender;

    @SerializedName("extraGuestNationality")
    @Expose
    private String  extraGuestNationality;

    @SerializedName("extraSeatPreference")
    @Expose
    private String  extraSeatPreference ;

    @SerializedName("extraGuestMaleSeats")
    @Expose
    private int  extraGuestMaleSeats = 0;

    @SerializedName("extraGuestFemaleSeats")
    @Expose
    private int  extraGuestFemaleSeats = 0;

    @SerializedName("remainingSeats")
    @Expose
    private int remainingSeats = 0;

    @SerializedName("totalInvitedUsers")
    @Expose
    private int totalInvitedUsers = 0;

    @SerializedName("totalInvitedCircles")
    @Expose
    private int totalInvitedCircles = 0;

    @SerializedName("totalInterestedMembers")
    @Expose
    private int totalInterestedMembers = 0;

    @SerializedName("plusOneMembers")
    @Expose
    private List<InvitedUserModel>  plusOneMembers ;

    @SerializedName("selectAllUsers")
    @Expose
    private boolean selectAllUsers = false;

    @SerializedName("selectAllCircles")
    @Expose
    private boolean selectAllCircles = false;

    @SerializedName("spotCloseType")
    @Expose
    private String spotCloseType = "";

    @SerializedName("spotCloseAt")
    @Expose
    private String spotCloseAt = "";

    @SerializedName("isSpotClosed")
    @Expose
    private boolean isSpotClosed = false;

    @SerializedName("eventId")
    @Expose
    private String eventId = "";

    @SerializedName("repeatStartDate")
    @Expose
    private String repeatStartDate = "";

    @SerializedName("repeatEndDate")
    @Expose
    private String repeatEndDate = "";

    @SerializedName("repeatDays")
    @Expose
    private List<String> repeatDays ;

    @SerializedName("repeatDatesAndTime")
    @Expose
    private List<PromoterSpecificDateModel> repeatDatesAndTime ;

    @SerializedName("plusOneMandatory")
    @Expose
    private boolean plusOneMandatory = false ;

    @SerializedName("saveToDraftId")
    @Expose
    private String saveToDraftId = "";

    @SerializedName("faq")
    @Expose
    private String faq = "";

    @SerializedName("paidPassType")
    @Expose
    private String paidPassType = "";

    @SerializedName("paidPassId")
    @Expose
    private String paidPassId = "";


    @SerializedName("eventGallery")
    @Expose
    private List<String> eventGallery ;


    public CustomVenueModel getCustomVenue() {
        return customVenue;
    }

    public void setCustomVenue(CustomVenueModel customVenue) {
        this.customVenue = customVenue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCloneId() {
        return cloneId;
    }

    public void setCloneId(String cloneId) {
        this.cloneId = cloneId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDressCode() {
        return dressCode;
    }

    public void setDressCode(String dressCode) {
        this.dressCode = dressCode;
    }

    public int getMaxInvitee() {
        return maxInvitee;
    }

    public void setMaxInvitee(int maxInvitee) {
        this.maxInvitee = maxInvitee;
    }

    public List<String> getRequirementsAllowed() {
        return requirementsAllowed;
    }

    public void setRequirementsAllowed(List<String> requirementsAllowed) {
        this.requirementsAllowed = requirementsAllowed;
    }

    public List<String> getRequirementsNotAllowed() {
        return requirementsNotAllowed;
    }

    public void setRequirementsNotAllowed(List<String> requirementsNotAllowed) {
        this.requirementsNotAllowed = requirementsNotAllowed;
    }

    public List<String> getBenefitsIncluded() {
        return benefitsIncluded;
    }

    public void setBenefitsIncluded(List<String> benefitsIncluded) {
        this.benefitsIncluded = benefitsIncluded;
    }

    public List<String> getBenefitsNotIncluded() {
        return benefitsNotIncluded;
    }

    public void setBenefitsNotIncluded(List<String> benefitsNotIncluded) {
        this.benefitsNotIncluded = benefitsNotIncluded;
    }

    public List<SocialAccountsToMentionModel> getSocialAccountsToMention() {
        return socialAccountsToMention;
    }

    public void setSocialAccountsToMention(List<SocialAccountsToMentionModel> socialAccountsToMention) {
        this.socialAccountsToMention = socialAccountsToMention;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public VenueObjectModel getVenue() {
        return venue;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
    }

    public UserDetailModel getUser() {
        return user;
    }

    public void setUser(UserDetailModel user) {
        this.user = user;
    }

    public List<InvitedUserModel> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(List<InvitedUserModel> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public List<String> getInvitedCircles() {
        return invitedCircles;
    }

    public void setInvitedCircles(List<String> invitedCircles) {
        this.invitedCircles = invitedCircles;
    }

    public List<InvitedUserModel> getInMembers() {
        return inMembers;
    }

    public void setInMembers(List<InvitedUserModel> inMembers) {
        this.inMembers = inMembers;
    }

    public List<InvitedUserModel> getInviteCancelList() {
        return inviteCancelList;
    }

    public void setInviteCancelList(List<InvitedUserModel> inviteCancelList) {
        this.inviteCancelList = inviteCancelList;
    }

    public InvitedUserModel getInvite() {
        return invite;
    }

    public void setInvite(InvitedUserModel invite) {
        this.invite = invite;
    }

    public boolean isEventFull() {
        return isEventFull;
    }

    public void setEventFull(boolean eventFull) {
        isEventFull = eventFull;
    }

    public boolean isWishlisted() {
        return isWishlisted;
    }

    public void setWishlisted(boolean wishlisted) {
        isWishlisted = wishlisted;
    }

    public int getTotalInMembers() {
        return totalInMembers;
    }

    public void setTotalInMembers(int totalInMembers) {
        this.totalInMembers = totalInMembers;
    }

    public boolean isConfirmationRequired() {
        return isConfirmationRequired;
    }

    public void setConfirmationRequired(boolean confirmationRequired) {
        isConfirmationRequired = confirmationRequired;
    }

    public String getInvitedGender() {
        return invitedGender;
    }

    public void setInvitedGender(String invitedGender) {
        this.invitedGender = invitedGender;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getRepeatDate() {
        return repeatDate;
    }

    public void setRepeatDate(String repeatDate) {
        this.repeatDate = repeatDate;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public List<InvitedUserModel> getInterestedMembers() {
        return interestedMembers;
    }

    public void setInterestedMembers(List<InvitedUserModel> interestedMembers) {
        this.interestedMembers = interestedMembers;
    }

    public OffersModel getOffer() {
        return offer;
    }

    public void setOffer(OffersModel offer) {
        this.offer = offer;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMaleSeats() {
        return maleSeats;
    }

    public void setMaleSeats(int maleSeats) {
        this.maleSeats = maleSeats;
    }

    public int getFemaleSeats() {
        return femaleSeats;
    }

    public void setFemaleSeats(int femaleSeats) {
        this.femaleSeats = femaleSeats;
    }

    public boolean isPlusOneAccepted() {
        return plusOneAccepted;
    }

    public void setPlusOneAccepted(boolean plusOneAccepted) {
        this.plusOneAccepted = plusOneAccepted;
    }

    public int getPlusOneQty() {
        return plusOneQty;
    }

    public void setPlusOneQty(int plusOneQty) {
        this.plusOneQty = plusOneQty;
    }

    public String getExtraGuestType() {
        return extraGuestType;
    }

    public void setExtraGuestType(String extraGuestType) {
        this.extraGuestType = extraGuestType;
    }

    public String getExtraGuestAge() {
        return extraGuestAge;
    }

    public void setExtraGuestAge(String extraGuestAge) {
        this.extraGuestAge = extraGuestAge;
    }

    public String getExtraGuestDressCode() {
        return extraGuestDressCode;
    }

    public void setExtraGuestDressCode(String extraGuestDressCode) {
        this.extraGuestDressCode = extraGuestDressCode;
    }

    public String getExtraGuestGender() {
        return extraGuestGender;
    }

    public void setExtraGuestGender(String extraGuestGender) {
        this.extraGuestGender = extraGuestGender;
    }

    public String getExtraGuestNationality() {
        return extraGuestNationality;
    }

    public void setExtraGuestNationality(String extraGuestNationality) {
        this.extraGuestNationality = extraGuestNationality;
    }

    public String getExtraSeatPreference() {
        return extraSeatPreference;
    }

    public void setExtraSeatPreference(String extraSeatPreference) {
        this.extraSeatPreference = extraSeatPreference;
    }

    public int getExtraGuestMaleSeats() {
        return extraGuestMaleSeats;
    }

    public void setExtraGuestMaleSeats(int extraGuestMaleSeats) {
        this.extraGuestMaleSeats = extraGuestMaleSeats;
    }

    public int getExtraGuestFemaleSeats() {
        return extraGuestFemaleSeats;
    }

    public void setExtraGuestFemaleSeats(int extraGuestFemaleSeats) {
        this.extraGuestFemaleSeats = extraGuestFemaleSeats;
    }

    public int getRemainingSeats() {
        return remainingSeats;
    }

    public void setRemainingSeats(int remainingSeats) {
        this.remainingSeats = remainingSeats;
    }

    public int getTotalInvitedUsers() {
        return totalInvitedUsers;
    }

    public void setTotalInvitedUsers(int totalInvitedUsers) {
        this.totalInvitedUsers = totalInvitedUsers;
    }

    public int getTotalInvitedCircles() {
        return totalInvitedCircles;
    }

    public void setTotalInvitedCircles(int totalInvitedCircles) {
        this.totalInvitedCircles = totalInvitedCircles;
    }

    public int getTotalInterestedMembers() {
        return totalInterestedMembers;
    }

    public void setTotalInterestedMembers(int totalInterestedMembers) {
        this.totalInterestedMembers = totalInterestedMembers;
    }

    public List<InvitedUserModel> getPlusOneMembers() {
        return plusOneMembers;
    }

    public void setPlusOneMembers(List<InvitedUserModel> plusOneMembers) {
        this.plusOneMembers = plusOneMembers;
    }

    public boolean isSelectAllUsers() {
        return selectAllUsers;
    }

    public void setSelectAllUsers(boolean selectAllUsers) {
        this.selectAllUsers = selectAllUsers;
    }

    public boolean isSelectAllCircles() {
        return selectAllCircles;
    }

    public void setSelectAllCircles(boolean selectAllCircles) {
        this.selectAllCircles = selectAllCircles;
    }

    public String getSpotCloseType() {
        return spotCloseType;
    }

    public void setSpotCloseType(String spotCloseType) {
        this.spotCloseType = spotCloseType;
    }

    public String getSpotCloseAt() {
        return spotCloseAt;
    }

    public void setSpotCloseAt(String spotCloseAt) {
        this.spotCloseAt = spotCloseAt;
    }

    public boolean isSpotClosed() {
        return isSpotClosed;
    }

    public void setSpotClosed(boolean spotClosed) {
        isSpotClosed = spotClosed;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getRepeatStartDate() {
        return repeatStartDate;
    }

    public void setRepeatStartDate(String repeatStartDate) {
        this.repeatStartDate = repeatStartDate;
    }

    public String getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(String repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }

    public List<String> getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(List<String> repeatDays) {
        this.repeatDays = repeatDays;
    }

    public List<PromoterSpecificDateModel> getRepeatDatesAndTime() {
        return repeatDatesAndTime;
    }

    public void setRepeatDatesAndTime(List<PromoterSpecificDateModel> repeatDatesAndTime) {
        this.repeatDatesAndTime = repeatDatesAndTime;
    }

    public boolean isPlusOneMandatory() {
        return plusOneMandatory;
    }

    public void setPlusOneMandatory(boolean plusOneMandatory) {
        this.plusOneMandatory = plusOneMandatory;
    }

    public String getSaveToDraftId() {
        return saveToDraftId;
    }

    public void setSaveToDraftId(String saveToDraftId) {
        this.saveToDraftId = saveToDraftId;
    }

    public String getFaq() {
        return faq;
    }

    public void setFaq(String faq) {
        this.faq = faq;
    }

    public String getPaidPassType() {
        return paidPassType;
    }

    public void setPaidPassType(String paidPassType) {
        this.paidPassType = paidPassType;
    }

    public String getPaidPassId() {
        return paidPassId;
    }

    public void setPaidPassId(String paidPassId) {
        this.paidPassId = paidPassId;
    }

    public List<String> getEventGallery() {
        return eventGallery;
    }

    public void setEventGallery(List<String> eventGallery) {
        this.eventGallery = eventGallery;
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

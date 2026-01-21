package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.rayna.RaynaTicketBookingModel;

import java.util.ArrayList;
import java.util.List;

public class MyWalletModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("orderId")
    @Expose
    private String orderId = "";
    @SerializedName("type")
    @Expose
    private String type="";

    @SerializedName("items")
    @Expose
    private List<ItemModel> items;
    @SerializedName("venueId")
    @Expose
    private String venueId="";
    @SerializedName("ticketId")
    @Expose
    private String ticketId="";
    @SerializedName("eventId")
    @Expose
    private String eventId="";
    @SerializedName("offerId")
    @Expose
    private String offerId="";
    @SerializedName("packageId")
    @Expose
    private String packageId="";
    @SerializedName("dealId")
    @Expose
    private String dealId="";
    @SerializedName("voucherId")
    @Expose
    private String voucherId="";
    @SerializedName("activityId")
    @Expose
    private String activityId="";
    @SerializedName("date")
    @Expose
    private String date="";
    @SerializedName("time")
    @Expose
    private String time="";
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("usedQty")
    @Expose
    private String usedQty="";
    @SerializedName("remainingQty")
    @Expose
    private Integer remainingQty;
    @SerializedName("offer")
    @Expose
    private WalletOfferModel offer;
    @SerializedName("deal")
    @Expose
    private ExclusiveDealModel deal;
    @SerializedName("activity")
    @Expose
    private ActivityDetailModel activity;

    @SerializedName("giftBy")
    @Expose
    private UserDetailModel giftBy;

    @SerializedName("giftTo")
    @Expose
    private UserDetailModel giftTo;

    @SerializedName("event")
    @Expose
    private WalletEventModel event;

    @SerializedName("ticket")
    @Expose
    private RaynaTicketBookingModel ticket;

    @SerializedName("whosinTicket")
    @Expose
    private RaynaTicketBookingModel whosinTicket;

    @SerializedName("traveldeskTicket")
    @Expose
    private RaynaTicketBookingModel traveldeskTicket;

    @SerializedName("octoTicket")
    @Expose
    private RaynaTicketBookingModel octoTicket;

    @SerializedName("juniperHotel")
    @Expose
    private RaynaTicketBookingModel juniperHotel;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getPackageId() {
        return packageId;
    }

    public String getUsedQty() {
        return usedQty;
    }

    public void setUsedQty(String usedQty) {
        this.usedQty = usedQty;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public WalletEventModel getEvent() {
        return event;
    }

    public void setEvent(WalletEventModel event) {
        this.event = event;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getRemainingQty() {
        return remainingQty;
    }

    public void setRemainingQty(Integer remainingQty) {
        this.remainingQty = remainingQty;
    }

    public WalletOfferModel getOffer() {
        return offer;
    }

    public void setOffer(WalletOfferModel offer) {
        this.offer = offer;
    }

    public ExclusiveDealModel getDeal() {
        return deal;
    }

    public void setDeal(ExclusiveDealModel deal) {
        this.deal = deal;
    }

    public ActivityDetailModel getActivity() {
        return activity;
    }

    public void setActivity(ActivityDetailModel activity) {
        this.activity = activity;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public List<ItemModel> getItems() {
        if (items == null) {
            return new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public UserDetailModel getGiftBy() {
        return giftBy;
    }

    public void setGiftBy(UserDetailModel giftBy) {
        this.giftBy = giftBy;
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserDetailModel getGiftTo() {
        return giftTo;
    }

    public void setGiftTo(UserDetailModel giftTo) {
        this.giftTo = giftTo;
    }

    public RaynaTicketBookingModel getTicket() {
        return ticket;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public void setTicket(RaynaTicketBookingModel ticket) {
        this.ticket = ticket;
    }

    public AppConstants.OrderListType getOrderListType() {
        switch (getType()) {
            case "offer": return AppConstants.OrderListType.OFFER;
            case "activity": return AppConstants.OrderListType.ACTIVITY;
            case "deal": return AppConstants.OrderListType.DEAL;
            case "event": return AppConstants.OrderListType.EVENT;
            case "ticket": return AppConstants.OrderListType.TICKET;
            case "whosin-ticket": return AppConstants.OrderListType.WHOSIN_TICKET;
            case "travel-desk": return AppConstants.OrderListType.TRAVEL_DESK;
            case "big-bus": return AppConstants.OrderListType.BIG_BUS;
            case "hero-balloon": return AppConstants.OrderListType.HERO_BALLOON;
            case "juniper-hotel": return AppConstants.OrderListType.JUNIPER_HOTEL;
            default:
                return AppConstants.OrderListType.NONE;
        }
    }

    public String getOrderId() {
        return Utils.notNullString(orderId);
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public RaynaTicketBookingModel getWhosinTicket() {
        return whosinTicket;
    }

    public void setWhosinTicket(RaynaTicketBookingModel whosinTicket) {
        this.whosinTicket = whosinTicket;
    }

    public RaynaTicketBookingModel getTraveldeskTicket() {
        return traveldeskTicket;
    }

    public void setTraveldeskTicket(RaynaTicketBookingModel traveldeskTicket) {
        this.traveldeskTicket = traveldeskTicket;
    }

    public RaynaTicketBookingModel getOctoTicket() {
        return octoTicket;
    }

    public void setOctoTicket(RaynaTicketBookingModel octoTicket) {
        this.octoTicket = octoTicket;
    }

    public RaynaTicketBookingModel getJuniperHotel() {
        return juniperHotel;
    }

    public void setJuniperHotel(RaynaTicketBookingModel juniperHotel) {
        this.juniperHotel = juniperHotel;
    }
}

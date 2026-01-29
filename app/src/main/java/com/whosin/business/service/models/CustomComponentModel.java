package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;

public class CustomComponentModel implements DiffIdentifier,ModelProtocol {


    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("subTitle")
    @Expose
    private String subTitle;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("borderColor")
    @Expose
    private String borderColor;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("badge")
    @Expose
    private String badge;
    @SerializedName("venueId")
    @Expose
    private String venueId;
    @SerializedName("offerId")
    @Expose
    private String offerId;

    @SerializedName("media")
    @Expose
    private String media;

    @SerializedName("mediaType")
    @Expose
    private String mediaType;

    @SerializedName("typeId")
    @Expose
    private String typeId;

    @SerializedName("buttonText")
    @Expose
    private String buttonText;

    @SerializedName("buttonColor")
    @Expose
    private String buttonColor;

    @SerializedName("ticketId")
    @Expose
    private String ticketId = "";

    private VenueObjectModel venueObjectModel;

    private RaynaTicketDetailModel raynaTicketDetailModel;

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

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getVenue() {
        return venueId;
    }

    public void setVenue(String venue) {
        this.venueId = venue;
    }

    public String getOffer() {
        return offerId;
    }

    public void setOffer(String offer) {
        this.offerId = offer;
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

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }


    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }


    public boolean isVideoUrl(){
        return Utils.isVideo(media);
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(String buttonColor) {
        this.buttonColor = buttonColor;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public VenueObjectModel getVenueObjectModel() {
        return venueObjectModel;
    }

    public void setVenueObjectModel(VenueObjectModel venueObjectModel) {
        this.venueObjectModel = venueObjectModel;
    }

    public RaynaTicketDetailModel getRaynaTicketDetailModel() {
        return raynaTicketDetailModel;
    }

    public void setRaynaTicketDetailModel(RaynaTicketDetailModel raynaTicketDetailModel) {
        this.raynaTicketDetailModel = raynaTicketDetailModel;
    }
}

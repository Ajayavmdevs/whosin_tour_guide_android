package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

import java.util.List;

public class BannerModel implements DiffIdentifier, ModelProtocol {


    @SerializedName("_id")
    @Expose
    private String id="";
    @SerializedName("title")
    @Expose
    private String title="";
    @SerializedName("type")
    @Expose
    private String type="";
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("image")
    @Expose
    private String image="";
    @SerializedName("venueId")
    @Expose
    private String venueId = "";

    @SerializedName("offerId")
    @Expose
    private String offerId = "";

    @SerializedName("ticketId")
    @Expose
    private String ticketId = "";

    @SerializedName("typeId")
    @Expose
    private String typeId = "";

    @SerializedName("media")
    @Expose
    private String media = "";

    @SerializedName("thumbnail")
    @Expose
    private String thumbnail = "";

    @SerializedName("description")
    @Expose
    private String description = "";

    @SerializedName("mediaUrls")
    @Expose
    private List<String > mediaUrls ;

    @SerializedName("buttonTint")
    @Expose
    private String buttonTint ;

    @SerializedName("buttonText")
    @Expose
    private String buttonText ;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return Utils.notNullString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getTypeId() {
        return Utils.notNullString(typeId);
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getMedia() {
        return Utils.notNullString(media);
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return Utils.notNullString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public List<String> getMediaUrls() {
        return Utils.notEmptyList(mediaUrls);
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public String getButtonTint() {
        return buttonTint;
    }

    public void setButtonTint(String buttonTint) {
        this.buttonTint = buttonTint;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
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

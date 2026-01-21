package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;

public class VideoComponentModel implements DiffIdentifier,ModelProtocol {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("venueId")
    @Expose
    private String venueId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("videoUrl")
    @Expose
    private String videoUrl;
    @SerializedName("thumb")
    @Expose
    private String thumb;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("venue")
    @Expose
    private VenueObjectModel venue;
    @SerializedName("ticket")
    @Expose
    private RaynaTicketDetailModel ticketDetailModel;
    @SerializedName("ticketId")
    @Expose
    private String ticketId = "";

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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumb() {
        return Utils.notNullString(thumb);
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public VenueObjectModel getVenue() {
        return venue;
    }

    public void setVenue(VenueObjectModel venue) {
        this.venue = venue;
    }
    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public String getVenueId() {
        return Utils.notNullString(venueId);
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public long getDuration() { return Long.parseLong(duration); }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public RaynaTicketDetailModel getTicketDetailModel() {
        return ticketDetailModel;
    }

    public void setTicketDetailModel(RaynaTicketDetailModel ticketDetailModel) {
        this.ticketDetailModel = ticketDetailModel;
    }

    public VideoComponentModel() {
    }

    public VideoComponentModel(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}

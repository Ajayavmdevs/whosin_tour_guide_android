package com.whosin.app.service.models.whosinTicketModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class RaynaWhosinBookingRulesModel  implements DiffIdentifier, ModelProtocol {

    @SerializedName("ticketId")
    @Expose
    private String ticketId;

    @SerializedName("optionId")
    @Expose
    private String optionId;

    @SerializedName("tourId")
    @Expose
    private String tourId;

    @SerializedName("tourOptionId")
    @Expose
    private String tourOptionId;

    @SerializedName("fromDate")
    @Expose
    private String fromDate;

    @SerializedName("toDate")
    @Expose
    private String toDate;

    @SerializedName("percentage")
    @Expose
    private int percentage;

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getTourId() {
        return Utils.notNullString(tourId);
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getTourOptionId() {
        return Utils.notNullString(tourOptionId);
    }

    public void setTourOptionId(String tourOptionId) {
        this.tourOptionId = tourOptionId;
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

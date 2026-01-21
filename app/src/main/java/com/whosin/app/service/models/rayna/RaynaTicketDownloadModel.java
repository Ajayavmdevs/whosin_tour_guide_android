package com.whosin.app.service.models.rayna;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class RaynaTicketDownloadModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("bookingId")
    @Expose
    private int bookingId = 0;

    @SerializedName("referenceNo")
    @Expose
    private String referenceNo = "";

    @SerializedName("ticketURL")
    @Expose
    private String ticketURL = "";

    @SerializedName("optionName")
    @Expose
    private String optionName = "";

    @SerializedName("validity")
    @Expose
    private String validity = "";

    @SerializedName("validityExtraDetails")
    @Expose
    private String validityExtraDetails ="";

    @SerializedName("printType")
    @Expose
    private String printType = "";

    @SerializedName("slot")
    @Expose
    private String slot = "";

    @SerializedName("pnrNumber")
    @Expose
    private String pnrNumber = "";

    @SerializedName("status")
    @Expose
    private String status = "";

    @SerializedName("downloadRequired")
    @Expose
    private boolean downloadRequired = false;

    @SerializedName("serviceUniqueId")
    @Expose
    private String serviceUniqueId = "";

    @SerializedName("servicetype")
    @Expose
    private String servicetype = "";

    @SerializedName("confirmationNo")
    @Expose
    private String confirmationNo = "";

    @SerializedName("optionId")
    @Expose
    private String optionId = "";

    @SerializedName("cancellable")
    @Expose
    private boolean cancellable = false;

    public String getOptionId() {
        return Utils.notNullString(optionId);
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public boolean isCancellable() {
        return cancellable;
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getReferenceNo() {
        return Utils.notNullString(referenceNo);
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getTicketURL() {
        return Utils.notNullString(ticketURL);
    }

    public void setTicketURL(String ticketURL) {
        this.ticketURL = ticketURL;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getValidityExtraDetails() {
        return validityExtraDetails;
    }

    public void setValidityExtraDetails(String validityExtraDetails) {
        this.validityExtraDetails = validityExtraDetails;
    }

    public String getPrintType() {
        return printType;
    }

    public void setPrintType(String printType) {
        this.printType = printType;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDownloadRequired() {
        return downloadRequired;
    }

    public void setDownloadRequired(boolean downloadRequired) {
        this.downloadRequired = downloadRequired;
    }

    public String getServiceUniqueId() {
        return serviceUniqueId;
    }

    public void setServiceUniqueId(String serviceUniqueId) {
        this.serviceUniqueId = serviceUniqueId;
    }

    public String getServicetype() {
        return servicetype;
    }

    public void setServicetype(String servicetype) {
        this.servicetype = servicetype;
    }

    public String getConfirmationNo() {
        return confirmationNo;
    }

    public void setConfirmationNo(String confirmationNo) {
        this.confirmationNo = confirmationNo;
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

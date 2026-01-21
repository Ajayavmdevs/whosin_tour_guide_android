package com.whosin.app.service.models.JuniperHotelModels;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

import java.util.List;

public class JpHotelOptionsModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("RatePlanCode")
    @Expose
    public String ratePlanCode = "";

    @SerializedName("Status")
    @Expose
    public String status = "";

    @SerializedName("NonRefundable")
    @Expose
    public String nonRefundable = "";

    @SerializedName("PackageContract")
    @Expose
    public String packageContract = "";

    @SerializedName("board")
    @Expose
    public JPHotelBoardModel board;

    @SerializedName("price")
    @Expose
    public JpHotelPriceModel price;

    @SerializedName("hotelRooms")
    @Expose
    public List<JpHotelRoomModel> hotelRooms;

    @SerializedName("hotelSupplements")
    @Expose
    public List<Object> hotelSupplements;

    @SerializedName("hotelOffers")
    @Expose
    public List<JPHotelOffer> hotelOffers;


    private boolean isDescriptionProcessed = false;

    public boolean isDescriptionProcessed() {
        return isDescriptionProcessed;
    }

    public void setDescriptionProcessed(boolean descriptionProcessed) {
        isDescriptionProcessed = descriptionProcessed;
    }

    public String getRatePlanCode() {
        return Utils.notNullString(ratePlanCode);
    }

    public void setRatePlanCode(String ratePlanCode) {
        this.ratePlanCode = ratePlanCode;
    }

    public String getStatus() {
        return Utils.notNullString(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNonRefundable() {
        return Utils.notNullString(nonRefundable);
    }

    public void setNonRefundable(String nonRefundable) {
        this.nonRefundable = nonRefundable;
    }

    public String getPackageContract() {
        return Utils.notNullString(packageContract);
    }

    public void setPackageContract(String packageContract) {
        this.packageContract = packageContract;
    }

    public JPHotelBoardModel getBoard() {
        return board;
    }

    public void setBoard(JPHotelBoardModel board) {
        this.board = board;
    }

    public JpHotelPriceModel getPrice() {
        return price;
    }

    public void setPrice(JpHotelPriceModel price) {
        this.price = price;
    }

    public List<JpHotelRoomModel> getHotelRooms() {
        return Utils.notEmptyList(hotelRooms);
    }

    public void setHotelRooms(List<JpHotelRoomModel> hotelRooms) {
        this.hotelRooms = hotelRooms;
    }

    public List<Object> getHotelSupplements() {
        return hotelSupplements;
    }

    public void setHotelSupplements(List<Object> hotelSupplements) {
        this.hotelSupplements = hotelSupplements;
    }

    public List<JPHotelOffer> getHotelOffers() {
        return Utils.notEmptyList(hotelOffers);
    }

    public void setHotelOffers(List<JPHotelOffer> hotelOffers) {
        this.hotelOffers = hotelOffers;
    }


    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }


    public String getFullBoardName() {
        if (board == null) return "";

        StringBuilder boardName = new StringBuilder();

        if (!TextUtils.isEmpty(board.getBoard())) {
            boardName.append(board.getBoard()).append(" | ");
        }

        if (!TextUtils.isEmpty(board.getType())) {
            boardName.append(board.getType());
        }

        return boardName.toString();
    }

}



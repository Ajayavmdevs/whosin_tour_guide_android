package com.whosin.business.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

import java.util.List;

public class JpHotelPriceInformationModel implements DiffIdentifier, ModelProtocol {

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
    public List<JpHotelHotelSupplementModel> hotelSupplements;

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

    public List<JpHotelHotelSupplementModel> getHotelSupplements() {
        return Utils.notEmptyList(hotelSupplements);
    }

    public void setHotelSupplements(List<JpHotelHotelSupplementModel> hotelSupplements) {
        this.hotelSupplements = hotelSupplements;
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

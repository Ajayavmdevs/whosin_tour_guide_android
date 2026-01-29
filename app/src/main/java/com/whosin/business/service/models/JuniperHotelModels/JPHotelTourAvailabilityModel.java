package com.whosin.business.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

import java.util.List;

public class JPHotelTourAvailabilityModel implements DiffIdentifier, ModelProtocol {


    @SerializedName("hotelInfo")
    @Expose
    public JPHotelInfoModel hotelInfo;

    @SerializedName("hotelOptions")
    @Expose
    public List<JpHotelOptionsModel> hotelOptions;

    public JPHotelInfoModel getHotelInfo() {
        return hotelInfo;
    }

    public void setHotelInfo(JPHotelInfoModel hotelInfo) {
        this.hotelInfo = hotelInfo;
    }

    public List<JpHotelOptionsModel> getHotelOptions() {
        return Utils.notEmptyList(hotelOptions);
    }

    public void setHotelOptions(List<JpHotelOptionsModel> hotelOptions) {
        this.hotelOptions = hotelOptions;
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

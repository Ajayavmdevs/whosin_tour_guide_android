package com.whosin.app.service.models.JuniperHotelModels;

import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

import java.util.List;

public class HotelRequestModel implements DiffIdentifier, ModelProtocol {

    private String hotelCode = "";

    private String startDate = "";

    private String endDate = "";

    private List<PaxesItemModel> paxes;

    public String getHotelCode() {
        return Utils.notNullString(hotelCode);
    }

    public void setHotelCode(String hotelCode) {
        this.hotelCode = hotelCode;
    }

    public String getStartDate() {
        return Utils.notNullString(startDate);
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return Utils.notNullString(endDate);
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<PaxesItemModel> getPaxes() {
        return Utils.notEmptyList(paxes);
    }

    public void setPaxes(List<PaxesItemModel> paxes) {
        this.paxes = paxes;
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

package com.whosin.app.service.models.whosinTicketModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

import java.util.List;

public class RaynaWhosinMoreInfoModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("tourOptionId")
    @Expose
    private String tourOptionId = "";

    @SerializedName("info")
    @Expose
    private List<RaynaWhosinInfoModel> infoList ;

    public String getTourOptionId() {
        return Utils.notNullString(tourOptionId);
    }

    public void setTourOptionId(String tourOptionId) {
        this.tourOptionId = tourOptionId;
    }

    public List<RaynaWhosinInfoModel> getInfoList() {
        return Utils.notEmptyList(infoList);
    }

    public void setInfoList(List<RaynaWhosinInfoModel> infoList) {
        this.infoList = infoList;
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

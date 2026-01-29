package com.whosin.business.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

import java.util.List;

public class JpHotelRelPaxesDistModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("relPaxes")
    @Expose
    public List<JpHotelRelPaxisModel> relPaxes;


    public List<JpHotelRelPaxisModel> getRelPaxes() {
        return Utils.notEmptyList(relPaxes);
    }

    public void setRelPaxes(List<JpHotelRelPaxisModel> relPaxes) {
        this.relPaxes = relPaxes;
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

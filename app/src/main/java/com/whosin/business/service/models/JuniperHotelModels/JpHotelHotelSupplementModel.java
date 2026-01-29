package com.whosin.business.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class JpHotelHotelSupplementModel  implements DiffIdentifier, ModelProtocol {

    @SerializedName("Code")
    @Expose
    public String code = "";
    @SerializedName("Type")
    @Expose
    public String type = "";
    @SerializedName("RatePlanCode")
    @Expose
    public String ratePlanCode = "";
    @SerializedName("Name")
    @Expose
    public String name = "";
    @SerializedName("price")
    @Expose
    public JpHotelPriceModel price;

    public String getCode() {
        return Utils.notNullString(code);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return Utils.notNullString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRatePlanCode() {
        return Utils.notNullString(ratePlanCode);
    }

    public void setRatePlanCode(String ratePlanCode) {
        this.ratePlanCode = ratePlanCode;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public JpHotelPriceModel getPrice() {
        return price;
    }

    public void setPrice(JpHotelPriceModel price) {
        this.price = price;
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

package com.whosin.app.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class JPHotelOffer implements DiffIdentifier, ModelProtocol {

    @SerializedName("Code")
    @Expose
    public String code = "";

    @SerializedName("Category")
    @Expose
    public String category = "";

    @SerializedName("OnlyResidents")
    @Expose
    public String onlyResidents = "";

    @SerializedName("Name")
    @Expose
    public String name = "";

    @SerializedName("Description")
    @Expose
    public String description = "";

    public String getCode() {
        return Utils.notNullString(code);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategory() {
        return Utils.notNullString(category);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOnlyResidents() {
        return Utils.notNullString(onlyResidents);
    }

    public void setOnlyResidents(String onlyResidents) {
        this.onlyResidents = onlyResidents;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return Utils.notNullString(description);
    }

    public void setDescription(String description) {
        this.description = description;
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

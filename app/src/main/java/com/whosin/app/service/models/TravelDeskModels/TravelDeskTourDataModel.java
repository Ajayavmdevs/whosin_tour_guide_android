package com.whosin.app.service.models.TravelDeskModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

import java.util.List;

public class TravelDeskTourDataModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String _id = "";

    @SerializedName("id")
    @Expose
    private int id ;

    @SerializedName("cancellationPolicy")
    @Expose
    private List<TravelDeskCancellationPolicyModel> cancellationPolicy ;

    @SerializedName("heroImage")
    @Expose
    private TravelDeskHeroImageModel heroImage ;

    @SerializedName("isExternal")
    @Expose
    private boolean isExternal = false;

    @SerializedName("language")
    @Expose
    private String language = "";

    @SerializedName("tourName")
    @Expose
    private String tourName = "";

    @SerializedName("name")
    @Expose
    private String name = "";

    @SerializedName("salesDescription")
    @Expose
    private String salesDescription = "";

    @SerializedName("termsAndConditions")
    @Expose
    private String termsAndConditions = "";

    @SerializedName("checkIn")
    @Expose
    private String checkIn = "";

    @SerializedName("checkOut")
    @Expose
    private String checkOut = "";

    @SerializedName("optionData")
    @Expose
    private List<TravelDeskOptionDataModel> optionDataModel;


    public String getCheckIn() {
        return Utils.notNullString(checkIn);
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return Utils.notNullString(checkOut);
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public List<TravelDeskCancellationPolicyModel> getCancellationPolicy() {
        return Utils.notEmptyList(cancellationPolicy);
    }

    public void setCancellationPolicy(List<TravelDeskCancellationPolicyModel> cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public String get_id() {
        return Utils.notNullString(_id);
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TravelDeskHeroImageModel getHeroImage() {
        return heroImage;
    }

    public void setHeroImage(TravelDeskHeroImageModel heroImage) {
        this.heroImage = heroImage;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    public String getLanguage() {
        return Utils.notNullString(language);
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalesDescription() {
        return Utils.notNullString(salesDescription);
    }

    public void setSalesDescription(String salesDescription) {
        this.salesDescription = salesDescription;
    }

    public String getTermsAndConditions() {
        return Utils.notNullString(termsAndConditions);
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public List<TravelDeskOptionDataModel> getOptionDataModel() {
        return Utils.notEmptyList(optionDataModel);
    }

    public void setOptionDataModel(List<TravelDeskOptionDataModel> optionDataModel) {
        this.optionDataModel = optionDataModel;
    }

    public String getTourName() {
        return Utils.notNullString(tourName);
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
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

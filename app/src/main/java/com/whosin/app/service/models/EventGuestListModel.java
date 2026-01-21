package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class EventGuestListModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("invitations")
    @Expose
    private List<InviationsModel> inviationsModels;

    @SerializedName("users")
    @Expose
    private List<ContactListModel> userModel;


    public List<InviationsModel> getInviationsModels() {
        return inviationsModels != null ? inviationsModels : new ArrayList<>();
    }

    public void setInviationsModels(List<InviationsModel> inviationsModels) {
        this.inviationsModels = inviationsModels;
    }

    public List<ContactListModel> getUserModel() {
        return userModel != null ? userModel : new ArrayList<>();
    }

    public void setUserModel(List<ContactListModel> userModel) {
        this.userModel = userModel;
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

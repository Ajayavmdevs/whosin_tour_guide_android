package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class PromoterInvitedUserModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("selectAllUsers")
    @Expose
    private boolean selectAllUsers;

    @SerializedName("selectAllCircles")
    @Expose
    private boolean selectAllCircles;

    @SerializedName("invitedUsers")
    @Expose
    private List<String> invitedUsers;

    public boolean isSelectAllUsers() {
        return selectAllUsers;
    }

    public void setSelectAllUsers(boolean selectAllUsers) {
        this.selectAllUsers = selectAllUsers;
    }

    public boolean isSelectAllCircles() {
        return selectAllCircles;
    }

    public void setSelectAllCircles(boolean selectAllCircles) {
        this.selectAllCircles = selectAllCircles;
    }

    public List<String> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(List<String> invitedUsers) {
        this.invitedUsers = invitedUsers;
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

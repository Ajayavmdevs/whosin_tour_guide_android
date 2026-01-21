package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.List;

public class PromoterEventInviteModel  implements DiffIdentifier,ModelProtocol{

    @SerializedName("invitedUsers")
    @Expose
    private List<InvitedUserModel> invitedUsers;

    @SerializedName("inMembers")
    @Expose
    private List<InvitedUserModel> inMembers;

    @SerializedName("interestedMembers")
    @Expose
    private List<InvitedUserModel> interestedMembers;

    @SerializedName("plusOneInvites")
    @Expose
    private List<InvitedUserModel> plusOneInvites;

    @SerializedName("users")
    @Expose
    private List<UserDetailModel> users;


    public List<InvitedUserModel> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(List<InvitedUserModel> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public List<InvitedUserModel> getInMembers() {
        return inMembers;
    }

    public void setInMembers(List<InvitedUserModel> inMembers) {
        this.inMembers = inMembers;
    }

    public List<InvitedUserModel> getInterestedMembers() {
        return interestedMembers;
    }

    public void setInterestedMembers(List<InvitedUserModel> interestedMembers) {
        this.interestedMembers = interestedMembers;
    }

    public List<InvitedUserModel> getPlusOneInvites() {
        return plusOneInvites;
    }

    public void setPlusOneInvites(List<InvitedUserModel> plusOneInvites) {
        this.plusOneInvites = plusOneInvites;
    }

    public List<UserDetailModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserDetailModel> users) {
        this.users = users;
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

package com.whosin.app.service.models;

import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class FollowAndContactListModel implements DiffIdentifier,ModelProtocol{

    private List<ContactListModel> followingList;

    private List<ContactListModel> contactList;

    public List<ContactListModel> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(List<ContactListModel> followingList) {
        this.followingList = followingList;
    }

    public List<ContactListModel> getContactList() {
        return contactList;
    }

    public void setContactList(List<ContactListModel> contactList) {
        this.contactList = contactList;
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

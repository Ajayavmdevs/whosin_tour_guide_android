package com.whosin.app.service.models;

import com.whosin.app.comman.DiffIdentifier;

public class GroupChatModel implements DiffIdentifier, ModelProtocol{

    private String title;
    private boolean isTitle;
    private BucketListModel bucketListModel;

    public GroupChatModel(String title, boolean isTitle, BucketListModel bucketListModel) {
        this.title = title;
        this.isTitle = isTitle;
        this.bucketListModel = bucketListModel;
    }

    public GroupChatModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setTitle(boolean title) {
        isTitle = title;
    }

    public BucketListModel getBucketListModel() {
        return bucketListModel;
    }

    public void setBucketListModel(BucketListModel bucketListModel) {
        this.bucketListModel = bucketListModel;
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

package com.whosin.business.service.models;

public class FollowUpdateEventModel {

    public String id;
    public String status;

    public FollowUpdateEventModel() {

    }
    public FollowUpdateEventModel(String id, String status) {
        this.id = id;
        this.status = status;
    }
}

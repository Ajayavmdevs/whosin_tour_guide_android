package com.whosin.app.service.models;

import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class MyPlanContainerModel implements DiffIdentifier {

    private String id = "-1";
    private String title = "";
    private CreateBucketListModel bucketModel;
    private List<InviteFriendModel> outings = new ArrayList<>();


    public MyPlanContainerModel() {

    }

    public MyPlanContainerModel(String id, String title, CreateBucketListModel bucketModel, List<InviteFriendModel> outings) {
        this.id = id;
        this.title = title;
        this.bucketModel = bucketModel;
        this.outings = outings;
    }

    public MyPlanContainerModel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CreateBucketListModel getBucketModel() {
        return bucketModel;
    }

    public void setBucketModel(CreateBucketListModel bucketModel) {
        this.bucketModel = bucketModel;
    }

    public List<InviteFriendModel> getOutings() {
        return outings;
    }

    public void setOutings(List<InviteFriendModel> outings) {
        this.outings = outings;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }
}

package com.whosin.business.service.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class EventChatModel extends RealmObject implements DiffIdentifier,ModelProtocol{

    @SerializedName("_id")
    @Expose
    private String id = "";

    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("image")
    @Expose
    private String image = "";

    @SerializedName("members")
    @Expose
    private RealmList<String> members;



    public static List<EventChatModel> getEventList(@NonNull Realm realm) {
        RealmResults<EventChatModel> results = realm.where( EventChatModel.class ).findAll();
        return realm.copyFromRealm( results );
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public RealmList<String> getMembers() {
        if (members == null){
            return new RealmList<>();
        }
        return members;
    }

    public void setMembers(RealmList<String> members) {
        this.members = members;
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

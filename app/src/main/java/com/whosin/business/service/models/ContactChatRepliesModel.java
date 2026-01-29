package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;

import io.realm.RealmObject;

public class ContactChatRepliesModel extends RealmObject implements DiffIdentifier,ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String id ="";
    @SerializedName("conctactUsId")
    @Expose
    private String conctactUsId ="";
    @SerializedName("reply")
    @Expose
    private String reply ="";
    @SerializedName("replyBy")
    @Expose
    private String replyBy = "";
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";
    @SerializedName("isRead")
    @Expose
    private boolean isRead = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConctactUsId() {
        return conctactUsId;
    }

    public void setConctactUsId(String conctactUsId) {
        this.conctactUsId = conctactUsId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReplyBy() {
        return replyBy;
    }

    public void setReplyBy(String replyBy) {
        this.replyBy = replyBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    @Override
    public int getIdentifier() {
        return 0;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}

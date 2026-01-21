package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class TypingEventModel {

    @SerializedName("chatId")
    @Expose
    public String chatId;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("userId")
    @Expose
    public String userId;

    @SerializedName("userName")
    @Expose
    public String userName;

    @SerializedName("isForStartTyping")
    @Expose
    public boolean isForStartTyping = false;

    @SerializedName("receivers")
    @Expose
    public List<String> receivers = new ArrayList<>();

}

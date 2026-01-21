package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class ChatResponseModel implements DiffIdentifier, ModelProtocol {


    @SerializedName("chat")
    @Expose
    private List<ChatModel> chat;
    @SerializedName("users")
    @Expose
    private List<UserDetailModel> users;

    public List<ChatModel> getChat() {
        if (chat == null) {
            return new ArrayList<>();
        }
        return chat;    }

    public void setChat(List<ChatModel> chat) {
        this.chat = chat;
    }

    public List<UserDetailModel> getUsers() {
        if (users == null) {
            return new ArrayList<>();
        }
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

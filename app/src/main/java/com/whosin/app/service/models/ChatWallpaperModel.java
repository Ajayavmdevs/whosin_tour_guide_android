package com.whosin.app.service.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.whosin.app.comman.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatWallpaperModel {

    private String  chatID;
    private String imagePath;


    public ChatWallpaperModel(String chatID, String imagePath) {
        this.chatID = chatID;
        this.imagePath = imagePath;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public static void addRecord(String chatID, String imagePath) {
        List<ChatWallpaperModel> history = getHistory();
        if (!history.isEmpty()) {
            Optional<ChatWallpaperModel> isExist = history.stream().filter(p -> p.chatID.equals(chatID)).findFirst();
            if (isExist.isPresent()) {
                isExist.get().setImagePath(imagePath);
                Preferences.shared.setString("wallpaper_list", new Gson().toJson(history));
            }
            else {
                history.add(new ChatWallpaperModel(chatID , imagePath));
                Preferences.shared.setString("wallpaper_list", new Gson().toJson(history));
            }
        }
        else {
            List<ChatWallpaperModel> newHistories = new ArrayList<>();
            newHistories.add(new ChatWallpaperModel(chatID, imagePath));
            Preferences.shared.setString("wallpaper_list", new Gson().toJson(newHistories));
        }
    }

    public static List<ChatWallpaperModel> getHistory() {
        String json = Preferences.shared.getString("wallpaper_list");
        Type type = new TypeToken<List<ChatWallpaperModel>>() {}.getType();
        List<ChatWallpaperModel> list = new Gson().fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public static String setWallPaper(String chatID) {
        List<ChatWallpaperModel> list = getHistory();
        Optional<ChatWallpaperModel> wallPaper = list.stream().filter(p -> p.getChatID().equals(chatID)).findFirst();
        if (wallPaper.isPresent()) {
            return wallPaper.get().getImagePath();
        }
        return "";
    }


    public static void clearCart() {
        List<CartModel> emptyList = new ArrayList<>();
        Preferences.shared.setString("wallpaper_list", new Gson().toJson(emptyList));
    }


}

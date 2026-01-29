package com.whosin.business.service.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Preferences;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SearchHistoryModel implements DiffIdentifier, ModelProtocol {


    private String id;
    private String type;
    private String title;
    private String subTitle;
    private String image;
    private String venueId = "";
    private OffersModel offersModel;
    private VenueObjectModel venueObjectModel;
    private ContactListModel contactListModel;
    private RaynaTicketDetailModel raynaTicketDetailModel;



    public String getId() {
        return id;
    }

    public SearchHistoryModel(String id, String type, String title, String subTitle, String imageUrl, String venueId) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
        this.image = imageUrl;
        this.venueId = venueId;
    }

    public SearchHistoryModel(String id, String type, String title, String subTitle, String imageUrl, String venueId,VenueObjectModel model,ContactListModel contactListModel,RaynaTicketDetailModel raynaTicketDetailModel) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
        this.image = imageUrl;
        this.venueId = venueId;
        this.venueObjectModel = model;
        this.contactListModel = contactListModel;
        this.raynaTicketDetailModel = raynaTicketDetailModel;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public OffersModel getOffersModel() {
        return offersModel;
    }

    public void setOffersModel(OffersModel offersModel) {
        this.offersModel = offersModel;
    }

    public VenueObjectModel getVenueObjectModel() {
        return venueObjectModel;
    }

    public void setVenueObjectModel(VenueObjectModel venueObjectModel) {
        this.venueObjectModel = venueObjectModel;
    }

    public ContactListModel getContactListModel() {
        return contactListModel;
    }

    public void setContactListModel(ContactListModel contactListModel) {
        this.contactListModel = contactListModel;
    }

    public static void addRecord(String id, String type, String title, String subTitle, String imageUrl, String venueId, VenueObjectModel model,ContactListModel contactListModel) {
        List<SearchHistoryModel> history = getHistory();
        if (history != null && !history.isEmpty()) {
            Optional<SearchHistoryModel> isExist = history.stream().filter(p -> p.id.equals(id)).findFirst();
            if (isExist.isPresent()) {
                return;
            }
        }

        List<SearchHistoryModel> newHistories = new ArrayList<>();
        newHistories.add(new SearchHistoryModel(id, type, title, subTitle, imageUrl, venueId,model,contactListModel,null));
        if (history != null) {
            newHistories.addAll(history);
        }
        Preferences.shared.setString("search_history", new Gson().toJson(newHistories));
    }

    public static void addRecord(String id, String type, String title, String subTitle, String imageUrl, String venueId, VenueObjectModel model,ContactListModel contactListModel,RaynaTicketDetailModel raynaTicketDetailModel) {
        List<SearchHistoryModel> history = getHistory();
        if (history != null && !history.isEmpty()) {
            Optional<SearchHistoryModel> isExist = history.stream().filter(p -> p.id.equals(id)).findFirst();
            if (isExist.isPresent()) {
                return;
            }
        }

        List<SearchHistoryModel> newHistories = new ArrayList<>();
        newHistories.add(new SearchHistoryModel(id, type, title, subTitle, imageUrl, venueId,model,contactListModel,raynaTicketDetailModel));
        if (history != null) {
            newHistories.addAll(history);
        }
        Preferences.shared.setString("search_history", new Gson().toJson(newHistories));
    }


    public static List<SearchHistoryModel> getHistory() {
        String json = Preferences.shared.getString("search_history");
        Type type = new TypeToken<List<SearchHistoryModel>>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }


    public static void removeRecord(String id) {
        List<SearchHistoryModel> history = getHistory();
        if (history != null && !history.isEmpty()) {
            history.removeIf(item -> item.getId().equals(id));
            Preferences.shared.setString("search_history", new Gson().toJson(history));
        }
    }

    public static void clearHistory() {
        Preferences.shared.setString("search_history", "");
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

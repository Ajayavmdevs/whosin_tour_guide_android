package com.whosin.app.service.models.TravelDeskModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

import java.util.List;

public class TravelDeskHeroImageModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("caption")
    @Expose
    private String caption = "";

    @SerializedName("relatedId")
    @Expose
    private String relatedId = "";

    @SerializedName("isSharedStorage")
    @Expose
    private boolean isSharedStorage = true;

    @SerializedName("srcSet")
    @Expose
    private List<TravelDeskSrcSetModel> srcSet ;

    @SerializedName("id")
    @Expose
    private String id = "" ;

    public String getCaption() {
        return Utils.notNullString(caption);
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getRelatedId() {
        return Utils.notNullString(relatedId);
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    public boolean isSharedStorage() {
        return isSharedStorage;
    }

    public void setSharedStorage(boolean sharedStorage) {
        isSharedStorage = sharedStorage;
    }

    public List<TravelDeskSrcSetModel> getSrcSet() {
        return Utils.notEmptyList(srcSet);
    }

    public void setSrcSet(List<TravelDeskSrcSetModel> srcSet) {
        this.srcSet = srcSet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

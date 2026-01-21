package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class ImageListUploadModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("url")
    @Expose
    private List<String > url;


    public List<String> getUrl() {
        if (url == null){return  new ArrayList<>();}
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
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

package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

public class YachtAddOnModel implements DiffIdentifier, ModelProtocol {


    @SerializedName("_id")
    @Expose
    private String id = "";

    @SerializedName("title")
    @Expose
    private String title = "";

    @SerializedName("image")
    @Expose
    private String image = "";

    @SerializedName("price")
    @Expose
    private String price = "";


    public String getId() {
        return Utils.notNullString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return Utils.notNullString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return Utils.notNullString(image);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return Utils.notNullString(price);
    }

    public void setPrice(String price) {
        this.price = price;
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

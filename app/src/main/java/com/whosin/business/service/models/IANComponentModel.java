package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

public class IANComponentModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("color")
    @Expose
    private String color;

    @SerializedName("alignment")
    @Expose
    private String alignment;

    @SerializedName("bgColor")
    @Expose
    private String bgColor;

    @SerializedName("action")
    @Expose
    private String action;

    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("image")
    @Expose
    private String image;

    public String getText() {
        return Utils.notNullString(text);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return Utils.notNullString(color);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAlignment() {
        return Utils.notNullString(alignment);
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getBgColor() {
        return Utils.notNullString(bgColor);
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getAction() {
        return Utils.notNullString(action);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getData() {
        return Utils.notNullString(data);
    }

    public void setData(String data) {
        this.data = data;
    }


    public String getImage() {
        return Utils.notNullString(image);
    }

    public void setImage(String image) {
        this.image = image;
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

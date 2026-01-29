package com.whosin.business.service.models.BigBusModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class BigBusBrandModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor = "";

    @SerializedName("color")
    @Expose
    private String color = "";

    @SerializedName("headerFont")
    @Expose
    private BigBusAccentFont headerFont;

    @SerializedName("checkoutLogoUrl")
    @Expose
    private String checkoutLogoUrl = "";

    @SerializedName("bodyFont")
    @Expose
    private BigBusAccentFont bodyFont;

    @SerializedName("logoUrl")
    @Expose
    private String logoUrl = "";

    @SerializedName("logoWhiteUrl")
    @Expose
    private String logoWhiteUrl = "";

    @SerializedName("faviconUrl")
    @Expose
    private String faviconUrl = "";

    @SerializedName("accentFont")
    @Expose
    private BigBusAccentFont accentFont;

    @SerializedName("contact")
    @Expose
    private BigBusContactModel contact;

    @SerializedName("name")
    @Expose
    private String name = "";

    @SerializedName("id")
    @Expose
    private String id = "";

    @SerializedName("secondaryColor")
    @Expose
    private String secondaryColor = "";


    public String getBackgroundColor() {
        return Utils.notNullString(backgroundColor);
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getColor() {
        return Utils.notNullString(color);
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigBusAccentFont getHeaderFont() {
        return headerFont;
    }

    public void setHeaderFont(BigBusAccentFont headerFont) {
        this.headerFont = headerFont;
    }

    public String getCheckoutLogoUrl() {
        return Utils.notNullString(checkoutLogoUrl);
    }

    public void setCheckoutLogoUrl(String checkoutLogoUrl) {
        this.checkoutLogoUrl = checkoutLogoUrl;
    }

    public BigBusAccentFont getBodyFont() {
        return bodyFont;
    }

    public void setBodyFont(BigBusAccentFont bodyFont) {
        this.bodyFont = bodyFont;
    }

    public String getLogoUrl() {
        return Utils.notNullString(logoUrl);
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLogoWhiteUrl() {
        return Utils.notNullString(logoWhiteUrl);
    }

    public void setLogoWhiteUrl(String logoWhiteUrl) {
        this.logoWhiteUrl = logoWhiteUrl;
    }

    public String getFaviconUrl() {
        return Utils.notNullString(faviconUrl);
    }

    public void setFaviconUrl(String faviconUrl) {
        this.faviconUrl = faviconUrl;
    }

    public BigBusAccentFont getAccentFont() {
        return accentFont;
    }

    public void setAccentFont(BigBusAccentFont accentFont) {
        this.accentFont = accentFont;
    }

    public BigBusContactModel getContact() {
        return contact;
    }

    public void setContact(BigBusContactModel contact) {
        this.contact = contact;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return Utils.notNullString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecondaryColor() {
        return Utils.notNullString(secondaryColor);
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
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
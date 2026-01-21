package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

public class YachtsSpecificationModel implements DiffIdentifier,ModelProtocol {

    @SerializedName("title")
    @Expose
    private String title  ="";
    @SerializedName("showTitle")
    @Expose
    private boolean showTitle = false;
    @SerializedName("value")
    @Expose
    private String value ="";
    @SerializedName("_id")
    @Expose
    private String id ="";

    public String getTitle() {
        return Utils.notNullString(title  );
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class ContainerBaseModel {

    @SerializedName("status")
    @Expose
    public int status = 0;

    @SerializedName("message")
    @Expose
    public String message = "";
}

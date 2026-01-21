package com.whosin.app.service.models;


import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContainerModel<T> extends ContainerBaseModel {

    @Nullable
    @SerializedName("data")
    @Expose
    public T data = null;

    @Nullable
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
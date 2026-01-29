package com.whosin.business.service.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContainerListModel<T> extends ContainerBaseModel {

    @Nullable
    @SerializedName("data")
    @Expose
    public List<T> data = null;
}


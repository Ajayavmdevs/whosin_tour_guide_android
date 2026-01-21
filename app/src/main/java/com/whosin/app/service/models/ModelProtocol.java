package com.whosin.app.service.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ModelProtocol {

    @NonNull
    default String searchCriteria() {
        return "";
    }

    @Nullable
    default String statusMessage() {
        return "";
    }

    boolean isValidModel();
}

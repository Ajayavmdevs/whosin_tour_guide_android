package com.whosin.business.service.rest;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import java.io.IOException;
import java.net.SocketTimeoutException;

public abstract class RestCallback<T> {

    @Nullable
    // LifecycleOwner for handling if it should call delegate only in RESUMED state
    private final LifecycleOwner lifecycleOwner;
    private Integer errorCode = 0;
    private Throwable throwable;

    public RestCallback() {
        lifecycleOwner = null;
    }

    public abstract void result(T model, String error);


    // null to indicate that the operation is in the background
    // otherwise you need to specify correct LifecycleOwner
    public RestCallback(@Nullable LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public boolean isTimeoutRequest() {
        return throwable != null && ((throwable instanceof SocketTimeoutException) || (throwable instanceof IOException));
    }

    @Nullable
    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }
}

package com.whosin.business.service.rest;

import androidx.annotation.NonNull;

import com.whosin.business.comman.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpHeaderInterceptor implements Interceptor {

    @NonNull
    private final Map<String, String> headers;

    public HttpHeaderInterceptor() {
        super();
        headers = new HashMap<>();
    }

    protected HttpHeaderInterceptor addHeader() {
        headers.put(HttpCommon.HTTPRequestHeaderNameAccept, "*/*");
        headers.put(HttpCommon.HTTPRequestHeaderNameContentType, "*/*");
        headers.put("Accept-Language", Utils.getLang());
        return this;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        for (String key : headers.keySet()) {
            builder.addHeader(key, Objects.requireNonNull(headers.get(key)));
        }
        Response response;
        try {
            response = chain.proceed(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return response;
    }
}

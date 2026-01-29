package com.whosin.business.service.rest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.whosin.business.BuildConfig;
import com.whosin.business.service.manager.UrlManager;

import io.nerdythings.okhttp.profiler.OkHttpProfilerInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RestClient {

    @NonNull
    public static Retrofit getService() {
        Gson gson = new GsonBuilder().setLenient().registerTypeAdapterFactory(new IgnoreFailureTypeAdapterFactory()).create();
        return new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(getUnsafeHttpClient(new HttpHeaderInterceptor().addHeader()).build())
                .baseUrl(UrlManager.shared.getBaseUrl())
                .build();
    }

    @NonNull
    public static OkHttpClient.Builder getUnsafeHttpClient() {
        return getUnsafeHttpClient(null);
    }

    @NonNull
    public static OkHttpClient.Builder getUnsafeHttpClient(@Nullable HttpHeaderInterceptor headers) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @NonNull
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            // DO NOT SET LOG LEVEL TO Level.BODY SINCE IT WILL NOT WORK WITH SSE
            if (BuildConfig.DEBUG) {
                interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            }

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(new OkHttpProfilerInterceptor());
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.callTimeout(2, TimeUnit.MINUTES);
            builder.connectTimeout(20, TimeUnit.SECONDS);
            builder.readTimeout(60, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);
            if (headers != null) {
                builder.addInterceptor(headers);
            }
            builder.addInterceptor(interceptor);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // endregion

    public static class IgnoreFailureTypeAdapterFactory implements TypeAdapterFactory {

        public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            return createCustomTypeAdapter(delegate);
        }

        private <T> TypeAdapter<T> createCustomTypeAdapter(TypeAdapter<T> delegate) {
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    try {
                        return delegate.read(in);
                    } catch (Exception e) {
                        in.skipValue();
                        return null;
                    }
                }
            };
        }
    }
}



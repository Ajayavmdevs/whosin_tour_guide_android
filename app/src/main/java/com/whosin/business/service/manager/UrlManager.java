package com.whosin.business.service.manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.whosin.business.BuildConfig;


public class UrlManager {

    @Nullable
    private static final String TAG = UrlManager.class.getCanonicalName();

    @NonNull
    public static UrlManager shared = UrlManager.shared();
    @Nullable
    private static volatile UrlManager _instance = null;

    public static final String devBaseUrl = "http://40.172.74.243:8443";
    public static final String devSocketHost = "http://64.227.131.3:2096";


    public static final String liveBaseUrl = "https://api.whosin.me";
    public static final String liveSocketHost = "https://websocket.whosin.me";


//    public static final String liveBaseUrl = "https://apiv2.whosin.me";
////    public static final String liveSocketHost = "http://40.172.247.198:2096";
//    public static final String liveSocketHost = "https://websocket.whosin.me";


//    public static final String liveSocketHost = "http://51.112.153.1:2096";


    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @NonNull
    private static synchronized UrlManager shared() {
        if (_instance == null) {
            synchronized (UrlManager.class) {
                _instance = new UrlManager();
            }
        }
        return _instance;
    }

    private UrlManager() {
    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public String getServiceUrl(String endPoint) {
         return String.format("%s/v1/%s", getBaseUrl(), endPoint);
    }

    public String getServiceUrlV2(String endPoint) {
        String version = BuildConfig.isLive ? "v2" : "v2";
        return String.format("%s/%s/%s", getBaseUrl(), version, endPoint);
    }


    public String getBaseUrl(){
        return BuildConfig.isLive ? liveBaseUrl  : devBaseUrl;
    }

    public String getSocketHost(){
        return BuildConfig.isLive ? liveSocketHost  : devSocketHost;
    }

    // endregion
    // --------------------------------------
}

package com.whosin.app.ui.fragment.home;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheWriter;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.VideoCache;
import com.whosin.app.service.rest.NetworkConnectivity;

import java.util.ArrayList;
import java.util.List;

public class VideoPreCaching {
    private final Context context;
    private final List<String> videoList = new ArrayList<>();
    public boolean isCaching = false;

    private static VideoPreCaching videoPreCaching;

    public static synchronized VideoPreCaching shared(Context context) {
        if (videoPreCaching == null){
            videoPreCaching = new VideoPreCaching(context);
        }
        return videoPreCaching;
    }

    public VideoPreCaching(Context context) {
        this.context = context;
    }

    public void startCaching() {
        isCaching = true;
        preCacheVideo();
    }

    public void clearItems(){
        isCaching = false;
        this.videoList.clear();
    }

    public void addItems(List<String> videoList){
        this.videoList.addAll(videoList);
        if (!isCaching) {
            isCaching = true;
            preCacheVideo();
        }
    }

    public void addItem(String videoItem){
        this.videoList.add(videoItem);
        if (!isCaching) {
            isCaching = true;
            preCacheVideo();
        }
    }

    public void removeItem(String url){
        if (!videoList.isEmpty()) {
            synchronized (this.videoList) {
                this.videoList.removeIf(videoItem -> videoItem.equals(url));
            }
        }
    }

    private void preCacheVideo() {
        if (!NetworkConnectivity.isConnected(context)) {
            isCaching = false;
            return;
        }

        if (videoList.isEmpty()) {
            isCaching = false;
            return;
        }
        String videoUrl = this.videoList.get(0);
        this.videoList.remove(0);
        if (!Utils.isNullOrEmpty(videoUrl)) {
            cacheVideo(videoUrl);
        } else {
            preCacheVideo();
        }
    }

    private void cacheVideo(String videoUrl) {
        try {
            Uri videoUri = Uri.parse(videoUrl);
            DataSpec dataSpec = new DataSpec(videoUri,0,1024*1024);
            HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true);
            DefaultDataSource.Factory defaultDataSourceFactory = new DefaultDataSource.Factory(context, httpDataSourceFactory);
            CacheDataSource cacheDataSourceFactory = new CacheDataSource.Factory()
                    .setCache(VideoCache.shared(context))
                    .setUpstreamDataSourceFactory(defaultDataSourceFactory)
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                    .createDataSource();
            CacheWriter cacheWriter = new CacheWriter(cacheDataSourceFactory, dataSpec, null, (requestLength, bytesCached, newBytesCached) -> {
                double downloadPercentage = (bytesCached * 100.0 / requestLength);
                if (downloadPercentage >= 100) {
                    preCacheVideo();
                }
            });
            cacheWriter.cache();
        } catch (Exception e){
            e.printStackTrace();
            preCacheVideo();
        }
    }
}


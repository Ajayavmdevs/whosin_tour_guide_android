package com.whosin.app.comman;

import android.content.Context;

import com.google.android.exoplayer2.database.StandaloneDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

public class VideoCache {
    private static final long CACHE_SIZE = 5000L *1024*1024;
    private static SimpleCache simpleCache;

    public static synchronized SimpleCache shared(Context context) {
        if (simpleCache == null){
            File cacheFolder = new File(context.getCacheDir(),"media");
            LeastRecentlyUsedCacheEvictor cacheEvictor = new LeastRecentlyUsedCacheEvictor(CACHE_SIZE);
            simpleCache = new SimpleCache(cacheFolder,cacheEvictor, new StandaloneDatabaseProvider(context));
        }
        return simpleCache;
    }
}
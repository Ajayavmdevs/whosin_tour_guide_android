package com.whosin.app.comman.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.LruCache;
import android.widget.LinearLayout;

import com.whosin.app.R;

import java.util.HashMap;
import java.util.Map;

public class BitmapManager {
    private static BitmapManager instance;
    private Context mContext;
    private LruCache<String, Bitmap> bitmapCache;
    private Map<String, Bitmap> inProgressTasks;

    private BitmapManager(Context context) {
        mContext = context.getApplicationContext();
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8; // Use 1/8th of the available memory for this memory cache.
        bitmapCache = new LruCache<>(cacheSize);
        inProgressTasks = new HashMap<>();
    }

    public static synchronized BitmapManager getInstance(Context context) {
        if (instance == null) {
            instance = new BitmapManager(context);
        }
        return instance;
    }

    public void clearCache() {
        bitmapCache.evictAll();
    }

    public void setBlurredBackground(LinearLayout linearLayout, int width, int height, float blurRadius) {
        String key = getKey(width, height, blurRadius);
        Bitmap cachedBitmap = bitmapCache.get(key);

        if (cachedBitmap != null) {
            linearLayout.setBackground(new BitmapDrawable(mContext.getResources(), cachedBitmap));
        } else {
            if (!inProgressTasks.containsKey(key)) {
                new BlurAsyncTask(linearLayout, width, height, blurRadius).execute();
            }
        }
    }

    private String getKey(int width, int height, float blurRadius) {
        return width + "x" + height + "@" + blurRadius;
    }

    private Bitmap createColoredBitmap(int color, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        return bitmap;
    }

    private Bitmap blurBitmap(Bitmap inputBitmap, float blurRadius) {
        if (inputBitmap == null) {
            return null;
        }

        Bitmap outputBitmap = inputBitmap.copy(inputBitmap.getConfig(), true);

        // Create a canvas from the output bitmap
        Canvas canvas = new Canvas(outputBitmap);

        // Create a paint object for applying blur effect
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.TRANSPARENT);
        paint.setMaskFilter(new android.graphics.BlurMaskFilter(blurRadius, android.graphics.BlurMaskFilter.Blur.NORMAL));

        // Draw the blurred bitmap onto the canvas
        canvas.drawBitmap(inputBitmap, 0, 0, paint);

        return outputBitmap;
    }

    private class BlurAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        private LinearLayout linearLayout;
        private int width, height;
        private float blurRadius;

        public BlurAsyncTask(LinearLayout linearLayout, int width, int height, float blurRadius) {
            this.linearLayout = linearLayout;
            this.width = width;
            this.height = height;
            this.blurRadius = blurRadius;
        }

        @Override
        protected void onPreExecute() {
            inProgressTasks.put(getKey(width, height, blurRadius), null);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap coloredBitmap = createColoredBitmap(mContext.getColor(R.color.black_50), width, height); // Adjust color as needed
            return blurBitmap(coloredBitmap, blurRadius);
        }

        @Override
        protected void onPostExecute(Bitmap blurredBitmap) {
            if (blurredBitmap != null) {
                String key = getKey(width, height, blurRadius);
                bitmapCache.put(key, blurredBitmap);
                inProgressTasks.remove(key);
                linearLayout.setBackground(new BitmapDrawable(mContext.getResources(), blurredBitmap));
            }
        }
    }
}



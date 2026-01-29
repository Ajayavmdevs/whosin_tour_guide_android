package com.whosin.business.comman.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BlurAsyncTask extends AsyncTask<Void, Void, Bitmap> {
    private Context context;
    private Bitmap inputBitmap;
    private float blurRadius;
    private BlurTaskListener listener;

    public interface BlurTaskListener {
        void onTaskCompleted(Bitmap blurredBitmap);
    }

    public BlurAsyncTask(Context context, Bitmap inputBitmap, float blurRadius, BlurTaskListener listener) {
        this.context = context;
        this.inputBitmap = inputBitmap;
        this.blurRadius = blurRadius;
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        return applyBlur(inputBitmap, blurRadius);
    }

    @Override
    protected void onPostExecute(Bitmap blurredBitmap) {
        super.onPostExecute(blurredBitmap);
        if (listener != null) {
            listener.onTaskCompleted(blurredBitmap);
        }
    }

    private Bitmap applyBlur(Bitmap inputBitmap, float blurRadius) {
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript renderScript = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(renderScript, inputBitmap);
        Allocation output = Allocation.createFromBitmap(renderScript, outputBitmap);

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        blurScript.setRadius(blurRadius);

        blurScript.setInput(input);
        blurScript.forEach(output);

        output.copyTo(outputBitmap);

        // Destroy resources
        input.destroy();
        output.destroy();
        blurScript.destroy();
        renderScript.destroy();

        return outputBitmap;
    }
}


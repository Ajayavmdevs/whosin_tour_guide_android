package com.whosin.app.ui.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.whosin.app.R;

public class RoundProgressBar extends View {
    private int progress = 0;
    private final Paint paint;
    private final RectF oval;

    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);

        oval = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float radius = Math.min(centerX, centerY) - (paint.getStrokeWidth() / 2);

        // Draw the progress arc
        oval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        float angle = 360 * ((float) progress / 100);
        canvas.drawArc(oval, -90, angle, false, paint);

        // Draw the percentage text
        paint.setColor(getContext().getColor(R.color.brand_pink));
        paint.setTextSize(50);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextAlign(Paint.Align.CENTER);
//        canvas.drawText(progress + "%", centerX, centerY + paint.getTextSize() / 4, paint);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }
}


package com.whosin.app.comman.ui.roundcornerlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.whosin.app.R;

@SuppressLint("AppCompatCustomView")
public class CustomSeekBar extends SeekBar {

    private Paint textPaint;
    private String valueText;
    private int thumbRadius;

    public CustomSeekBar(Context context) {
        super(context);
        initialize();
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._45ssp));
        textPaint.setColor(getResources().getColor(R.color.brand_pink));
        textPaint.setTextAlign(Paint.Align.CENTER);
        thumbRadius = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._33sdp);
        valueText = "";
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate the position to draw the value on top of the thumb
        int thumbX = (int) (getPaddingLeft() + (getWidth() - getPaddingLeft() - getPaddingRight()) * getProgress() / getMax());

        // Calculate the vertical position for the text
        int textY = (int) (getThumb().getBounds().top - thumbRadius - textPaint.getFontMetrics().top);

        // Draw the value text on top of the thumb
        canvas.drawText(valueText, thumbX, textY, textPaint);
    }

    public void setValueText(String text) {
        valueText = text;
        invalidate(); // Redraw the SeekBar with the new value text
    }
}
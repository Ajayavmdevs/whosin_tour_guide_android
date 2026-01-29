package com.whosin.business.comman;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class CustomTypefaceSpan extends TypefaceSpan {

    private final Typeface newType;

    public CustomTypefaceSpan(Typeface type) {
        super(""); // Use default family
        this.newType = type;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, newType);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }

    private static void applyCustomTypeFace(Paint paint, Typeface tf) {
        int oldStyle = paint.getTypeface() != null ? paint.getTypeface().getStyle() : 0;
        @SuppressLint("WrongConstant") Typeface tfWithStyle = Typeface.create(tf, oldStyle);
        paint.setTypeface(tfWithStyle);
    }
}


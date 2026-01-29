package com.whosin.business.ui.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class OuterRecyclerView extends RecyclerView implements NestedScrollingParent {

    private NestedScrollingParentHelper parentHelper;

    public OuterRecyclerView(Context context) {
        super(context);
        init();
    }

    public OuterRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OuterRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        parentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
        parentHelper.onStopNestedScroll(child);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
        parentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public boolean onNestedFling( View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }
}



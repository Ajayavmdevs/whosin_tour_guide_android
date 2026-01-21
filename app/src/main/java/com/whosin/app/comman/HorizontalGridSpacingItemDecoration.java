package com.whosin.app.comman;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalGridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int marginDp;

    public HorizontalGridSpacingItemDecoration(int marginDp) {
        this.marginDp = marginDp;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);
        int column = position % 2; // 2 columns
        int marginPx = dpToPx(view.getContext(), marginDp);

        // Check layout direction (LTR or RTL)
        boolean isRtl = ViewCompat.getLayoutDirection(parent) == ViewCompat.LAYOUT_DIRECTION_RTL;

        if (!isRtl) {
            // LTR (default)
            if (column == 0) {
                outRect.left = 0;
                outRect.right = marginPx;
            } else {
                outRect.left = marginPx;
                outRect.right = 0;
            }
        } else {
            // RTL → flip left/right
            if (column == 0) {
                outRect.right = 0;
                outRect.left = marginPx;
            } else {
                outRect.right = marginPx;
                outRect.left = 0;
            }
        }

        // Vertical spacing
        outRect.top = marginPx;
    }

    private int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}

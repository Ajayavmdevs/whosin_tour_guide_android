package com.whosin.business.comman;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.whosin.business.R;

public class StoryProgressBar extends LinearLayout {

    private int numProgressBars = 5; // Default number of ProgressBars
    private ProgressBar[] progressBars;
    private int marginBetweenBars = 8; // Set your desired margin value in pixels

    public StoryProgressBar(Context context) {
        super(context);
        init();
    }

    public StoryProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        createProgressBars();
    }

    private void createProgressBars() {
        removeAllViews(); // Remove existing ProgressBars

        progressBars = new ProgressBar[numProgressBars];

        for (int i = 0; i < numProgressBars; i++) {
            progressBars[i] = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
            progressBars[i].setMax(100); // Set max value as needed

            progressBars[i].setProgressTintList(ColorStateList.valueOf(Color.WHITE));

            // Use LayoutParams to set width and height
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    0, // Width will be calculated by weight
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f // Weight to distribute width equally among ProgressBars
            );

            // Set left margin for all bars except the first one
            if (i > 0) {
                layoutParams.setMarginStart(marginBetweenBars);
            }

            progressBars[i].setLayoutParams(layoutParams);

            addView(progressBars[i]);
        }
    }

    // Set progress for a specific ProgressBar
    public void setProgressBarProgress(int index, int progress) {
        for(int i = 0; i < index; i++) {
            progressBars[i].setProgress(100);
        }
        if (index >= 0 && index < numProgressBars) {
            progressBars[index].setProgress(progress);
        }
    }

    // Set progress for all ProgressBars
    public void setAllProgressBarsProgress(int progress) {
        for (ProgressBar progressBar : progressBars) {
            progressBar.setProgress(progress);
        }
    }

    // Set the number of ProgressBars
    public void setNumProgressBars(int num) {
        numProgressBars = num;
        createProgressBars(); // Recreate ProgressBars with the new count
        requestLayout();  // Request a new layout pass
    }
}

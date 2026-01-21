package com.whosin.app.comman.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.whosin.app.ui.adapter.raynaTicketAdapter.RaynaGalleryAdapter;

import java.util.Arrays;
import java.util.List;

public class UiUtils {

    public static void showFullScreen(Activity activity) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public static boolean isAppOnForeground(Context context, String appPackageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = appPackageName;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if ((appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static void replaceFragment(LifecycleOwner lo, @IdRes int containerId, Fragment fragment) {
        // UI not in visible state, will not perform replace fragment
        if (lo == null) {
            return;
        }
        if (!lo.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
            return;
        }
        FragmentManager fragmentManager = null;
        if (lo instanceof AppCompatActivity) {
            fragmentManager = ((AppCompatActivity) lo).getSupportFragmentManager();
        } else if (lo instanceof Fragment) {
            fragmentManager = ((Fragment) lo).getChildFragmentManager();
        }
        if (fragmentManager != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(containerId, fragment);
            transaction.commit();
        }
    }

    public static Fragment getVisibleFragment(LifecycleOwner lo, @IdRes int containerId) {
        // UI not in visible state, will not perform replace fragment
        if (lo == null) {
            return null;
        }
        if (!lo.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
            return null;
        }
        FragmentManager fragmentManager = null;
        if (lo instanceof AppCompatActivity) {
            fragmentManager = ((AppCompatActivity) lo).getSupportFragmentManager();
        } else if (lo instanceof Fragment) {
            fragmentManager = ((Fragment) lo).getChildFragmentManager();
        }
        if (fragmentManager != null) {
            Fragment currentFragment = fragmentManager.findFragmentById(containerId);
            return currentFragment;
        }
        return null;
    }

    public static void reloadFragment(LifecycleOwner lo, Fragment fragment) {
        // UI not in visible state, will not perform replace fragment
        if (lo == null) {
            return;
        }
        if (!lo.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
            return;
        }
        FragmentManager fragmentManager = null;
        if (lo instanceof AppCompatActivity) {
            fragmentManager = ((AppCompatActivity) lo).getSupportFragmentManager();
        } else if (lo instanceof Fragment) {
            fragmentManager = ((Fragment) lo).getChildFragmentManager();
        }
        if (fragmentManager != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.detach(fragment);
            transaction.attach(fragment);
            transaction.commit();
        }
    }

    public static void addFragment(LifecycleOwner lo, @IdRes int containerId, Fragment fragment) {
        // UI not in visible state, will not perform replace fragment
        if (lo == null) {
            return;
        }
        if (!lo.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
            return;
        }
        FragmentManager fragmentManager = null;
        if (lo instanceof AppCompatActivity) {
            fragmentManager = ((AppCompatActivity) lo).getSupportFragmentManager();
        } else if (lo instanceof Fragment) {
            fragmentManager = ((Fragment) lo).getChildFragmentManager();
        }
        if (fragmentManager != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(containerId, fragment);
            transaction.commit();
        }
    }

    public static void removeFragment(LifecycleOwner lo, Fragment fragment) {
        if (lo == null) {
            return;
        }
        if (!lo.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
            return;
        }
        FragmentManager fragmentManager = null;
        if (lo instanceof AppCompatActivity) {
            fragmentManager = ((AppCompatActivity) lo).getSupportFragmentManager();
        } else if (lo instanceof Fragment) {
            fragmentManager = ((Fragment) lo).getChildFragmentManager();
        }
        if (fragmentManager != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
        }

    }

    public static View getViewBy(@NonNull ViewGroup parent,@LayoutRes int layout) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return itemView;
    }

    public static int getLastCompletelyVisiblePosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm instanceof LinearLayoutManager) {
            LinearLayoutManager llm = (LinearLayoutManager) lm;
            return llm.findLastCompletelyVisibleItemPosition();
        } else {
            StaggeredGridLayoutManager sglm = (StaggeredGridLayoutManager) lm;
            if (sglm == null) {
                return 0;
            }
            int[] lastVisibleItemPositions = sglm.findLastCompletelyVisibleItemPositions(null);
            Arrays.sort(lastVisibleItemPositions);
            return lastVisibleItemPositions[lastVisibleItemPositions.length - 1];
        }
    }


    public static boolean is90PercentVisible(View parent, View child) {
        // Parent ka global visible rect
        Rect parentRect = new Rect();
        boolean parentVisible = parent.getGlobalVisibleRect(parentRect);

        // Child ka global visible rect
        Rect childRect = new Rect();
        boolean childVisible = child.getGlobalVisibleRect(childRect);

        if (!parentVisible || !childVisible) {
            return false;
        }

        // Intersect karte hain child aur parent rect ko
        Rect intersectRect = new Rect(childRect);
        boolean intersects = intersectRect.intersect(parentRect);
        if (!intersects) {
            return false;
        }

        int visibleWidth = intersectRect.width();
        int childWidth = child.getWidth();
        if (childWidth == 0) return false;

        float ratio = (float) visibleWidth / childWidth;
        // Debug log: aap ratio ko dekh sakte hain for verification
        Log.d("VideoVisibility", "Visible ratio: " + ratio);
        return ratio >= 0.9f;
    }


    public static boolean isView90PercentVisibleHorizontally(RecyclerView recyclerView, View view) {
        int[] viewLocation = new int[2];
        int[] recyclerViewLocation = new int[2];

        view.getLocationOnScreen(viewLocation);
        recyclerView.getLocationOnScreen(recyclerViewLocation);

        int viewStart = viewLocation[0];
        int viewEnd = viewStart + view.getWidth();

        int recyclerViewStart = recyclerViewLocation[0];
        int recyclerViewEnd = recyclerViewStart + recyclerView.getWidth();

        int visibleStart = Math.max(viewStart, recyclerViewStart);
        int visibleEnd = Math.min(viewEnd, recyclerViewEnd);

        int visibleWidth = Math.max(0, visibleEnd - visibleStart);
        float visibilityPercentage = (visibleWidth / (float) view.getWidth()) * 100;

        return visibilityPercentage >= 90;
    }


}

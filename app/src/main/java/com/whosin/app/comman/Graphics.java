package com.whosin.app.comman;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.gson.Gson;
import com.khoiron.actionsheets.ActionSheet;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.whosin.app.R;
import com.whosin.app.comman.interfaces.ActionDoneCallback;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.roundcornerlayout.RoundCornerLinearLayout;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.ui.activites.venue.VenueActivity;
import com.whosin.app.ui.activites.venue.VenueShareActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class Graphics {

    private static final Map<String, DialogInterface> dialogMap = new ConcurrentHashMap<>();
    public static Context context;
    private static AlertDialog dialog = null;
    public static Activity activity;
    public static FragmentManager fragmentManager = null;


    // --------------------------------------
    // region Common
    // --------------------------------------

    public static AnimationDrawable getAnimatedDrawable() {
        AnimationDrawable animationDrawable = (AnimationDrawable) ContextCompat.getDrawable(context, R.drawable.blinking_animation);
        animationDrawable.start();
        return animationDrawable;
    }

    public static AnimationDrawable getRoundAnimatedDrawable() {
        AnimationDrawable animationDrawable = (AnimationDrawable) ContextCompat.getDrawable(context, R.drawable.blinking_animation_round);
        animationDrawable.start();
        return animationDrawable;
    }

    public static void applyBlurEffect(Activity activity, BlurView blurView) {
        if (activity == null) {
            return;
        }
        if (blurView == null) {
            return;
        }
        activity.runOnUiThread(() -> {
            float radius = 25f;
            View decorView = activity.getWindow().getDecorView();
            ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
            Drawable windowBackground = decorView.getBackground();
            blurView.setupWith(rootView, new RenderScriptBlur(context)) // or RenderEffectBlur
                    .setFrameClearDrawable(windowBackground) // Optional
                    .setBlurRadius(radius);
        });

    }


    public static void applyBlurEffectOnClaimScreen(Activity activity, BlurView blurView) {
        float radius = 20f;
        View decorView = activity.getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        blurView.setupWith(rootView, new RenderScriptBlur(context)) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);
    }

    public static Drawable getShimmer() {
        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder()
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setShape(Shimmer.Shape.RADIAL)
                .setDuration(1000)
                .setBaseAlpha(0.2f)
                .setHighlightAlpha(1.0f)
                .setAutoStart(true)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        return shimmerDrawable;
    }

    public static void loadCoilImage(Context context, ImageView imageView, String imageData) {
        ImageRequest request = new ImageRequest.Builder(context)
                .data(imageData)
                .target(imageView)
                .placeholder(Graphics.getAnimatedDrawable())
                .error(R.drawable.gery_image)
                .build();

        ImageLoader imageLoader = Coil.imageLoader(context);
        imageLoader.enqueue(request);
    }

    public static void loadImage(String url, ImageView imageView) {
        Glide.with(context).load(url).placeholder(getAnimatedDrawable()).into(imageView);
    }

    public static void loadVideoThumbnail(String videoUrl, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions().frame(1000000).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop();
        Glide.with(context)
                .load(Uri.parse(videoUrl))
                .apply(requestOptions)
                .into(imageView);
    }

    public static void loadRoundImage(String url, ImageView imageView) {
        Glide.with(context).load(url).circleCrop().placeholder(getRoundAnimatedDrawable()).into(imageView);
    }

    public static void loadImageAsBitMap(String url, ImageView imageView, CommanCallback<Bitmap> callback) {
        Glide.with(context).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                imageView.setImageBitmap(resource);
                if (callback != null) {
                    callback.onReceive(resource);
                }
            }
        });

    }

    public static void loadImageWithFirstLetter(String url, ImageView imageView, String name) {

        if (TextUtils.isEmpty(name)) {
            name = "WHOSIN";
        }
        name = name.trim();
        String[] names = name.split(" ");
        StringBuilder firstLetters = new StringBuilder();
        for (String n : names) {
            if (!TextUtils.isEmpty(n) && n.length() > 0) {
                firstLetters.append(n.charAt(0));
            }
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (!TextUtils.isEmpty(firstLetters.toString())) {
                Typeface typeface = context.getResources().getFont(R.font.montserrat_semibold);
                com.whosin.app.comman.TextDrawable drawable = new com.whosin.app.comman.TextDrawable.Builder()
                        .setShape(TextDrawable.SHAPE_ROUND_RECT)
                        .setText(firstLetters.toString().toUpperCase())
                        .setTextColor(Color.BLACK)
                        .setFont(typeface)
                        .setHeight(100)
                        .setWidth(100)
                        .build();

                imageView.setImageDrawable(drawable);
                if (url != null && !url.isEmpty()) {
                    Glide.with(context).load(url).error(drawable).placeholder(getRoundAnimatedDrawable()).into(imageView);
                }
            } else {
                if (url != null && !url.isEmpty()) {
                    Glide.with(context).load(url).placeholder(getRoundAnimatedDrawable()).into(imageView);
                }
            }
        }
    }

    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        if (TextUtils.isEmpty(imageUrl)) {
            Glide.with(context)
                    .load(R.drawable.gery_image)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(Graphics.getAnimatedDrawable())
                    .error(R.drawable.gery_image)
                    .into(imageView);
        }
    }

    public static void loadImageWithFirstLetterWithPink(String url, ImageView imageView, String name) {

        if (TextUtils.isEmpty(name)) {
            name = "WHOSIN";
        }
        name = name.trim();
        String[] names = name.split(" ");
        StringBuilder firstLetters = new StringBuilder();
        for (String n : names) {
            if (!TextUtils.isEmpty(n) && n.length() > 0) {
                firstLetters.append(n.charAt(0));
            }
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (!TextUtils.isEmpty(firstLetters.toString())) {
                Typeface typeface = context.getResources().getFont(R.font.montserrat_semibold);
                com.whosin.app.comman.TextDrawable drawable = new com.whosin.app.comman.TextDrawable.Builder()
                        .setShape(TextDrawable.SHAPE_ROUND_RECT)
                        .setText(firstLetters.toString().toUpperCase())
                        .setTextColor(Color.BLACK)
                        .setColor(context.getColor(R.color.brand_pink))
                        .setFont(typeface)
                        .setHeight(100)
                        .setWidth(100)
                        .build();

                imageView.setImageDrawable(drawable);
                if (url != null && !url.isEmpty()) {
                    Glide.with(context).load(url).error(drawable).placeholder(getRoundAnimatedDrawable()).into(imageView);
                }
            } else {
                if (url != null && !url.isEmpty()) {
                    Glide.with(context).load(url).placeholder(getRoundAnimatedDrawable()).into(imageView);
                }
            }
        }
    }


    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) Graphics.context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static void setStoryRing(String id, RoundCornerLinearLayout view) {
        if (!Preferences.shared.containValueInList("story_seen", id)) {
            view.setBackground(context.getResources().getDrawable(R.drawable.story_border));
        } else {
            view.setBackground(context.getResources().getDrawable(R.drawable.gray_circal_bg));
        }
    }

    public static boolean isStoryView(String id) {
        return Preferences.shared.containValueInList("story_seen", id);
    }

    public static float dpToPx(@NonNull Context context, float dp) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float pxToDp(@NonNull Context context, float px) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void replaceFragment(@Nullable LifecycleOwner lo, @IdRes int containerId, @NonNull Fragment fragment) {
        try {
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
                if (fragment.isAdded()) {
                    transaction.show(fragment);
                } else {
                    transaction.replace(containerId, fragment);
                }
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applyGradientBackground(ImageView imageView, String hexColor) {
        int startColor = Color.parseColor(hexColor);
        int endColor = darkenColor(startColor, 0.3f);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{startColor, endColor}
        );
        gradientDrawable.setCornerRadius(30f);

        imageView.setBackground(gradientDrawable);
    }

    public static int darkenColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = (int) (Color.red(color) * factor);
        int g = (int) (Color.green(color) * factor);
        int b = (int) (Color.blue(color) * factor);
        return Color.argb(a, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    // endregion
    // --------------------------------------
    // region Progress Dialog
    // --------------------------------------

    public static void hideDialog(Context context, String key) {
        Optional.ofNullable(dialogMap.get(key)).ifPresent(d -> {
            try {
                d.dismiss();
            } catch (Exception ex) {

            }
            dialogMap.remove(key);
        });
    }

    public static void hideDialog(Context context, @NonNull Class c) {
        Optional.ofNullable(dialogMap.get(c.getName())).ifPresent(d -> {
            try {
                d.dismiss();
            } catch (Exception ex) {

            }
            dialogMap.remove(c.getName());
        });
    }

    public static void showProgressBarDialog(Context context, @NonNull Class c, String message) {
        if (!dialogMap.containsKey(c.getName())) {
            ProgressDialog m_pd = new ProgressDialog(context);
            if (m_pd.getWindow() != null) {
                m_pd.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
            m_pd.setCancelable(false);
            m_pd.setMessage(message);
            m_pd.setIndeterminate(true);
            m_pd.show();
            m_pd.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            m_pd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialogMap.put(c.getName(), m_pd);
        }
    }

    public static void showProgressBarDialog(Context context, String key, String message) {
        if (!dialogMap.containsKey(key)) {
            ProgressDialog m_pd = new ProgressDialog(context);

            if (m_pd.getWindow() != null) {
                m_pd.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
            m_pd.setCancelable(false);
            m_pd.setMessage(message);
            m_pd.setIndeterminate(true);
            m_pd.show();
            m_pd.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            m_pd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialogMap.put(key, m_pd);
        }
    }

    public static void showProgress(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (dialog == null || !dialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setView(R.layout.layout_loading_dialog);
            dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (!activity.isFinishing()) {
            dialog.show();
        }

    }

    public static void hideProgress(Activity activity) {
        if (dialog != null && activity != null && !activity.isFinishing()) {
            dialog.dismiss();
        }
    }

    // endregion
    // --------------------------------------
    // region Alert Dialog
    // --------------------------------------

    public static void showAlertDialogWithOkButton(Context context, String title, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.common_alert_with_ok, null);

        TextView tvTitle = dialogView.findViewById(R.id.Title);
        ImageView ivLogo = dialogView.findViewById(R.id.ivLogo);
        TextView tvMessage = dialogView.findViewById(R.id.Message);
        TextView btnTitle = dialogView.findViewById(R.id.btnTitle);

        btnTitle.setText(Utils.getLangValue("ok"));

        tvTitle.setText(title);
        tvMessage.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        alertDialog.show();

        if (alertDialog.getWindow() != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        }

        btnTitle.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }



    public static void showAlertDialogWithOkButton(Context context, String title, String message, @NonNull Consumer<Boolean> delegate) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.common_alert_with_ok, null);

        TextView titleTextView = customView.findViewById(R.id.Title);
        TextView messageTextView = customView.findViewById(R.id.Message);
        View okButton = customView.findViewById(R.id.okButton);

        titleTextView.setText(title);
        messageTextView.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);
        AlertDialog alert = builder.create();
        alert.setCancelable(false);

        if (alert.getWindow() != null) {
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (alert.getWindow() != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alert.getWindow().getAttributes());
            layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8); // Set width to 80% of screen width
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Keep height dynamic
            alert.getWindow().setAttributes(layoutParams);
        }

        okButton.setOnClickListener(v -> {
            delegate.accept(true);
            alert.dismiss();
        });

        alert.show();
    }

    public static void alertDialogOkBtnWithUIFlag(Context context, String title, String message, boolean isCancel, String positiveTitle) {
        try {
            int ui_flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            if (message.contains("Unable to resolve host") || message.contains("Failed to connect")) {
                message = context.getString(R.string.service_message_connection_error);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title).setMessage(message).setCancelable(isCancel).setPositiveButton(positiveTitle, (dialog, id) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            alert.show();
            alert.getWindow().getDecorView().setSystemUiVisibility(ui_flags);
            alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } catch (Throwable t) {

        }
    }

    public static void alertDialogOkBtnWithUIFlag(Context context, String title, String message, boolean isCancel, String positiveTitle, @NonNull Consumer<Boolean> delegate) {
        int ui_flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (message.contains("Unable to resolve host") || message.contains("Failed to connect")) {
            message = context.getString(R.string.service_message_connection_error);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setCancelable(isCancel).setPositiveButton(positiveTitle, (dialog, id) -> {
            delegate.accept(true);
            dialog.dismiss();
        });
        AlertDialog alert = builder.create();
        alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        alert.show();
        alert.getWindow().getDecorView().setSystemUiVisibility(ui_flags);
        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void alertDialogYesNoBtnWithUIFlag(Context context, String title, String message, boolean isCancel, String negativeTitle, String positiveTitle, @NonNull Consumer<Boolean> delegate) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.common_alert_with_yes_no, null);

        TextView titleTextView = customView.findViewById(R.id.txtTitle);
        TextView messageTextView = customView.findViewById(R.id.tvMessage);
        TextView yesButtonTextView = customView.findViewById(R.id.yesBtnTitle);
        TextView noButtonTextView = customView.findViewById(R.id.txtNoBtnTitle);
        View yesButton = customView.findViewById(R.id.yesButton);
        View noButton = customView.findViewById(R.id.noButton);

        titleTextView.setText(title);
        messageTextView.setText(message);
        yesButtonTextView.setText(positiveTitle);
        noButtonTextView.setText(negativeTitle);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (alertDialog.getWindow() != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        }

        alertDialog.show();

        yesButton.setOnClickListener(v -> {
            delegate.accept(true);
            alertDialog.dismiss();
        });

        noButton.setOnClickListener(v -> {
            delegate.accept(false);
            alertDialog.dismiss();
        });
    }

    public static void alertDialogYesNoBtnWithUIFlag(Context context, String title, CharSequence message, boolean isCancel, String negativeTitle, String positiveTitle, @NonNull Consumer<Boolean> delegate) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.common_alert_with_yes_no, null);

        TextView titleTextView = customView.findViewById(R.id.txtTitle);
        TextView messageTextView = customView.findViewById(R.id.tvMessage);
        TextView yesButtonTextView = customView.findViewById(R.id.yesBtnTitle);
        TextView noButtonTextView = customView.findViewById(R.id.txtNoBtnTitle);
        View yesButton = customView.findViewById(R.id.yesButton);
        View noButton = customView.findViewById(R.id.noButton);

        titleTextView.setText(title);
        messageTextView.setText(message);
        yesButtonTextView.setText(positiveTitle);
        noButtonTextView.setText(negativeTitle);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (alertDialog.getWindow() != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        }

        alertDialog.show();

        yesButton.setOnClickListener(v -> {
            delegate.accept(true);
            alertDialog.dismiss();
        });

        noButton.setOnClickListener(v -> {
            delegate.accept(false);
            alertDialog.dismiss();
        });
    }

    public static void showAlertDialogWithOkCancel(Context context, String title, String message, @NonNull Consumer<Boolean> delegate) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.common_alert_with_yes_no, null);

        TextView titleTextView = customView.findViewById(R.id.txtTitle);
        TextView messageTextView = customView.findViewById(R.id.tvMessage);
        TextView yesButtonTextView = customView.findViewById(R.id.yesBtnTitle);
        TextView noButtonTextView = customView.findViewById(R.id.txtNoBtnTitle);
        View yesButton = customView.findViewById(R.id.yesButton);
        View noButton = customView.findViewById(R.id.noButton);

        if (titleTextView == null || messageTextView == null || yesButtonTextView == null || noButtonTextView == null) {
            throw new IllegalStateException("One or more required views are missing from the layout!");
        }

        titleTextView.setText(title);
        messageTextView.setText(message);

        if (message.equals("You must have to verify your phone number & email to access who is in.")) {
            yesButtonTextView.setText(Utils.getLangValue("edit"));
        } else if (message.equals(Utils.getLangValue("delete_chat_confirmation"))) {
            yesButtonTextView.setText(Utils.getLangValue("yes"));
        } else if (message.equals("Are you sure want to exit from group?")) {
            yesButtonTextView.setText(Utils.getLangValue("yes"));
        } else {
            yesButtonTextView.setText(Utils.getLangValue("ok"));
        }

        noButtonTextView.setText(Utils.getLangValue("cancel"));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (alertDialog.getWindow() != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        }
        yesButton.setOnClickListener(v -> {
            delegate.accept(true);
            alertDialog.dismiss();
        });

        noButton.setOnClickListener(v -> {
            delegate.accept(false);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }



    public static void showAlertDialogWithOkCancel(Context context, String title, String message, String positiveButtonText, String negativeButtonText, @NonNull Consumer<Boolean> delegate) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.common_alert_with_yes_no, null);

        TextView titleTextView = customView.findViewById(R.id.txtTitle);
        TextView messageTextView = customView.findViewById(R.id.tvMessage);
        TextView yesButtonTextView = customView.findViewById(R.id.yesBtnTitle);
        TextView noButtonTextView = customView.findViewById(R.id.txtNoBtnTitle);
        View yesButton = customView.findViewById(R.id.yesButton);
        View noButton = customView.findViewById(R.id.noButton);

        titleTextView.setText(title);
        messageTextView.setText(message);
        yesButtonTextView.setText(positiveButtonText);
        noButtonTextView.setText(negativeButtonText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if (alertDialog.getWindow() != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        }

        yesButton.setOnClickListener(v -> {
            delegate.accept(true);
            alertDialog.dismiss();
        });

        noButton.setOnClickListener(v -> {
            delegate.accept(false);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }



    public static void showAlertDialogWithOkCancel(Context context, String title, String message, String temporaryButtonText, String permanentlyButtonText, String cancelButtonText, @NonNull Consumer<String> delegate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(cancelButtonText, (dialog, id) -> {
                    dialog.dismiss();
                    delegate.accept("cancel");
                });

        // Set the positive button text based on the provided parameter
        builder.setPositiveButton(temporaryButtonText, (dialog, i) -> {
            dialog.dismiss();
            delegate.accept("temporary");
        });

        builder.setNeutralButton(permanentlyButtonText, (dialog, i) -> {
            dialog.dismiss();
            delegate.accept("permanently");
        });

        AlertDialog alert = builder.create();
        alert.show();
        Button nBtn = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nBtn.setTextColor(Color.WHITE);
        Button pBtn = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pBtn.setTextColor(Color.WHITE);
        Button neutralBtn = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
        neutralBtn.setTextColor(Color.WHITE);
    }

    public static void showAlertDialogForDeleteEvent(Context context, String title, String message, String temporaryButtonText, String permanentlyButtonText, String cancelButtonText, @NonNull Consumer<String> delegate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(cancelButtonText, (dialog, id) -> {
                    dialog.dismiss();
                    delegate.accept("Delete All Events");
                });

        // Set the positive button text based on the provided parameter
        builder.setPositiveButton(temporaryButtonText, (dialog, i) -> {
            dialog.dismiss();
            delegate.accept("Delete Current Event");
        });

        builder.setNeutralButton(permanentlyButtonText, (dialog, i) -> {
            dialog.dismiss();
            delegate.accept("cancel");
        });

        AlertDialog alert = builder.create();
        alert.show();
        Button nBtn = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nBtn.setTextColor(Color.RED);
        Button pBtn = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pBtn.setTextColor(Color.WHITE);
        Button neutralBtn = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
        neutralBtn.setTextColor(Color.WHITE);
    }

    public static void showAlertDialog(Context context, String title, String message,
                                       String positiveButtonText, String neutralButtonText,
                                       String negativeButtonText, @NonNull Consumer<String> delegate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(negativeButtonText, (dialog, id) -> {
                    dialog.dismiss();
                    delegate.accept("Approve only");
                });

        // Set "Add to circle" as the primary action (positive button)
        builder.setPositiveButton(positiveButtonText, (dialog, i) -> {
            dialog.dismiss();
            delegate.accept("Approve and add to circle");
        });

        // Set "Approve Only" as the neutral action
        builder.setNeutralButton(neutralButtonText, (dialog, i) -> {
            dialog.dismiss();
            delegate.accept("Cancel");
        });

        AlertDialog alert = builder.create();
        alert.show();

        Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.WHITE);
        negativeButton.setAllCaps(false);

        Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.WHITE);
        positiveButton.setAllCaps(false);

        Button neutralButton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
        neutralButton.setTextColor(Color.WHITE);
        neutralButton.setAllCaps(false);
    }

    public static void showAlertDialogForEventUdapte(Context context, String title, String message,
                                                     String positiveButtonText, String neutralButtonText,
                                                     String negativeButtonText, @NonNull Consumer<String> delegate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(negativeButtonText, (dialog, id) -> {
                    dialog.dismiss();
                    delegate.accept("Update All Events");
                });

        // Set "Add to circle" as the primary action (positive button)
        builder.setPositiveButton(positiveButtonText, (dialog, i) -> {
            dialog.dismiss();
            delegate.accept("Update Current Event");
        });

        // Set "Approve Only" as the neutral action
        builder.setNeutralButton(neutralButtonText, (dialog, i) -> {
            dialog.dismiss();
            delegate.accept("Close");
        });

        AlertDialog alert = builder.create();
        alert.show();

        Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.WHITE);
        negativeButton.setAllCaps(false);

        Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.WHITE);
        positiveButton.setAllCaps(false);

        Button neutralButton = alert.getButton(DialogInterface.BUTTON_NEUTRAL);
        neutralButton.setTextColor(Color.WHITE);
        neutralButton.setAllCaps(false);
    }

    // endregion
    // --------------------------------------
    // region Actionsheet
    // --------------------------------------

    public static void showActionSheet(Context context, String title, ArrayList<String> options, ActionDoneCallback<String> callback) {
        String cancelBtnTitle = Utils.getLangValue("cancel");
        ActionSheet actionSheet = new ActionSheet(context, options).setTitle(title);
        if (title.equals("Remove event or assign it to admin")) {
            cancelBtnTitle = "X";
            actionSheet.setColorTitleCancel(context.getColor(R.color.redColor));
        }

        actionSheet.setCancelTitle(cancelBtnTitle);
//                .setColorTitle(context.getColor(R.color.neon_black_300))
//                .setColorTitleCancel(context.getColor(R.color.neon_black_500))
//                .setColorData(context.getColor(R.color.neon_black_500))
//                .setFontTitle(R.font.proxima_nova_regular)
//                .setFontData(R.font.proxima_nova_regular)
//                .setFontCancelTitle(R.font.proxima_nova_regular)

        actionSheet.setSizeTextCancel(20).setSizeTextData(20).setSizeTextTitle(17).create(callback::onDone);
    }

    public static void showActionSheet(Context context, String title, String cancelText, ArrayList<String> options, ActionDoneCallback<String> callback) {
        String cancelBtnTitle = cancelText;
        ActionSheet actionSheet = new ActionSheet(context, options).setTitle(title);
        if (title.equals("Remove event or assign it to admin")) {
            cancelBtnTitle = "X";
            actionSheet.setColorTitleCancel(context.getColor(R.color.redColor));
        }

        actionSheet.setCancelTitle(cancelBtnTitle);
        actionSheet.setSizeTextCancel(20).setSizeTextData(20).setSizeTextTitle(17).create(callback::onDone);
    }

    public static void showActionSheetRedTitle(Context context, String title, ArrayList<String> options, ActionDoneCallback<String> callback) {
        String cancelBtnTitle = "Cancel";
        ActionSheet actionSheet = new ActionSheet(context, options).setTitle(title);

        if (title.equals("Remove event or assign it to admin")) {
            cancelBtnTitle = "X";
        }

        actionSheet.setCancelTitle(cancelBtnTitle).setColorData(context.getColor(R.color.redColor)).setColorTitleCancel(context.getColor(R.color.redColor)).setSizeTextCancel(20).setSizeTextData(20).setSizeTextTitle(17).create(callback::onDone);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void showMenu(Context context, String[] options, View view, OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener) {
        List<PowerMenuItem> list = new ArrayList<>();
        for (String title : options) {
            list.add(new PowerMenuItem(title));
        }

        PowerMenu powerMenu = new PowerMenu.Builder(context).addItemList(list).setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextColor(context.getColor(R.color.black)).setTextGravity(Gravity.CENTER).setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD)).setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE)
                .setOnMenuItemClickListener(onMenuItemClickListener).setAutoDismiss(true).build();
        powerMenu.showAsDropDown(view);
    }


    public static void openVenueDetail(Activity activity, String venueId) {
        Intent intent = new Intent(Graphics.context, VenueActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("venueId", venueId);
        activity.startActivity(intent);
//        activity.overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
    }

    public static void openShareDialog(Activity activity, VenueObjectModel venueObjectModel, UserDetailModel userDetailModel, OffersModel offersModel, String type) {
        Intent intent = new Intent(Graphics.context, VenueShareActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (venueObjectModel != null) {
            intent.putExtra("venue", new Gson().toJson(venueObjectModel));
        }
        if (userDetailModel != null) {
            intent.putExtra("userModel", new Gson().toJson(userDetailModel));
        }
        if (offersModel != null) {
            intent.putExtra("offer", new Gson().toJson(offersModel));
        }
        intent.putExtra("type", type);
        activity.startActivity(intent);

    }

    public static void openInstagramProfile(Context context, String input) {
        Uri uri;
        Intent intent;

        if (input.contains("instagram.com")) {
            uri = Uri.parse(input);
        } else {
            uri = Uri.parse("http://instagram.com/_u/" + input);
            intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.instagram.android");

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                return;
            } else {
                uri = Uri.parse("https://www.instagram.com/" + input);
            }
        }

        intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }


    public static void openFacebookProfile(Context context, String input) {
        Uri uri;
        Intent intent;

        // Check if the input is a URL or a username
        if (input.contains("facebook.com")) {
            uri = Uri.parse(input);
        } else {
            uri = Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/" + input);
            intent = new Intent(Intent.ACTION_VIEW, uri);

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                return; // Exit the method after starting the intent
            } else {
                uri = Uri.parse("https://www.facebook.com/" + input);
            }
        }

        // If the app is not installed or the input was a URL
        intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }


    public static void openSnapchatProfile(Context context, String input) {
        Uri uri;
        Intent intent;

        // Check if the input is a URL or a username
        if (input.contains("snapchat.com")) {
            uri = Uri.parse(input);
        } else {
            uri = Uri.parse("snapchat://add/" + input);
            intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.snapchat.android");

            try {
                context.startActivity(intent);
                return; // Exit the method after starting the intent
            } catch (ActivityNotFoundException e) {
                uri = Uri.parse("https://www.snapchat.com/add/" + input);
            }
        }

        // If the app is not installed or the input was a URL
        intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }


    public static void openYouTubeChannel(Context context, String input) {
        Uri uri;
        Intent intent;

        // Check if the input is a URL or a channel name
        if (input.contains("youtube.com") || input.contains("youtu.be")) {
            uri = Uri.parse(input);
        } else {
            uri = Uri.parse("https://www.youtube.com/channel/" + input);
        }

        intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.youtube");

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            // Fallback to using a browser if the YouTube app is not installed
            intent.setPackage(null);
            context.startActivity(intent);
        }
    }

    public static void openWhatsAppContact(Context context, String phoneNumber) {
        String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);

    }

    public static void openEmailApp(Context context, String emailAddress) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + emailAddress));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }


    public static void openUrlInBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        // Get a list of all activities that can handle the intent
        List<ResolveInfo> resolvedActivities = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (!resolvedActivities.isEmpty()) {
            // There is at least one activity (browser) that can handle the intent
            context.startActivity(intent);
        } else {
            // Handle the case when no browser is installed
            Toast.makeText(context, "No browser found to open the link", Toast.LENGTH_SHORT).show();
        }
    }


    public static void openTikTokLink(Context context, String input) {
        Uri uri;
        Intent intent;

        // Check if the input is a URL or a username
        if (input.contains("tiktok.com")) {
            uri = Uri.parse(input);
        } else {
            uri = Uri.parse("https://www.tiktok.com/@" + input);
        }

        intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.zhiliaoapp.musically");

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            // Fallback to using a browser if the TikTok app is not installed
            intent.setPackage(null);
            context.startActivity(intent);
        }
    }


    public static void openSoicalAccount(Context context, String type, String target) {
        switch (type) {
            case "instagram":
                openInstagramProfile(context, target.trim());
                break;
            case "tiktok":
                openTikTokLink(context, target);
                break;
            case "facebook":
                openFacebookProfile(context, target);
                break;
            case "google":
                openUrlInBrowser(context, target);
                break;
            case "youtube":
                openYouTubeChannel(context, target);
                break;
            case "snapchat":
                openSnapchatProfile(context, target);
                break;
            case "website":
                openUrlInBrowser(context, target);
                break;
            case "whatsapp":
                openWhatsAppContact(context, target);
                break;
            case "email":
                openEmailApp(context, target);
                break;
            case "whosin":
                openUrlInBrowser(context, target);
                break;
            default:
                break;
        }
    }

    public static void setFavoriteIcon(Context context, boolean isFavorite, ImageView imageView) {
        int drawableId = isFavorite ? R.drawable.icon_heart_withfill : R.drawable.icon_heart_withoutfil;
        int tintColor = ContextCompat.getColor(context, isFavorite ? R.color.brand_pink : R.color.gray_medium);

        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, tintColor);
            imageView.setImageDrawable(drawable);
        }
    }


// endregion
// --------------------------------------

}
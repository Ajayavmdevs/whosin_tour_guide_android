package com.whosin.app.comman;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;

public class NotificationUtils {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "channel_id";

    public static void showNotificationWithImage(Context context, String imageUrl,RemoteMessage message) {
        Glide.with(context).asBitmap().load(imageUrl).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                AppExecutors.get().mainThread().execute(() -> {
                    showNotification(context, resource, message);
                });
            }
            @Override
            public void onLoadCleared(Drawable placeholder) {
            }
        });
    }

    public static void showNotification(Context context, String imageUrl, RemoteMessage message) {
        RemoteViews customNotificationLayout = new RemoteViews(context.getPackageName(), R.layout.custom_notification_layout);

        // Set the text for the custom layout
        customNotificationLayout.setTextViewText(R.id.tvNTitle, message.getNotification().getTitle());
        customNotificationLayout.setTextViewText(R.id.tvMessage, message.getNotification().getBody());

        AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.imageView, customNotificationLayout, new int[]{ NOTIFICATION_ID });

        Glide.with(context).asBitmap().load(imageUrl).into(appWidgetTarget);

        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager != null && uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            int tvNTitle = ContextCompat.getColor(context, R.color.white);
            int tvMessage = ContextCompat.getColor(context, R.color.white_2);
            customNotificationLayout.setTextColor(R.id.tvNTitle, tvNTitle);
            customNotificationLayout.setTextColor(R.id.tvMessage, tvMessage);
        } else {
            int tvNTitle = ContextCompat.getColor(context, R.color.black);
            int tvMessage = ContextCompat.getColor(context, R.color.black_2);
            customNotificationLayout.setTextColor(R.id.tvNTitle, tvNTitle);
            customNotificationLayout.setTextColor(R.id.tvMessage, tvMessage);
        }

        NotificationCompat.Builder builder = new  NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.appicon)
                .setCustomContentView(customNotificationLayout)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        PendingIntent pendingIntent = getPendingIntent(context,message);
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Default channel", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void showNotification(Context context, Bitmap imageBitmap, RemoteMessage message) {
        RemoteViews customNotificationLayout = new RemoteViews(context.getPackageName(), R.layout.custom_notification_layout);

        // Set the text for the custom layout
        customNotificationLayout.setTextViewText(R.id.tvNTitle, message.getNotification().getTitle());
        customNotificationLayout.setTextViewText(R.id.tvMessage, message.getNotification().getBody());
        customNotificationLayout.setImageViewBitmap(R.id.imageView, imageBitmap);
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager != null && uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            int tvNTitle = ContextCompat.getColor(context, R.color.white);
            int tvMessage = ContextCompat.getColor(context, R.color.white_2);
            customNotificationLayout.setTextColor(R.id.tvNTitle, tvNTitle);
            customNotificationLayout.setTextColor(R.id.tvMessage, tvMessage);
        } else {
            int tvNTitle = ContextCompat.getColor(context, R.color.black);
            int tvMessage = ContextCompat.getColor(context, R.color.black_2);
            customNotificationLayout.setTextColor(R.id.tvNTitle, tvNTitle);
            customNotificationLayout.setTextColor(R.id.tvMessage, tvMessage);
        }

        NotificationCompat.Builder builder = new  NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.appicon)
                .setCustomContentView(customNotificationLayout)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        PendingIntent pendingIntent = getPendingIntent(context,message);
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Default channel", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    private static PendingIntent getPendingIntent(Context context, RemoteMessage message) {
        if (message.getData().isEmpty()) { return null; }
        if (message.getData().containsKey("msg")) {
            String msgData = message.getData().get("msg");
            ChatMessageModel model = new Gson().fromJson(msgData, ChatMessageModel.class);
            ChatModel chatModel = new ChatModel();
            chatModel.setChatId(model.getChatId());
            chatModel.setChatType(model.getChatType());
            if (model.getChatType().equals("friend")) {
                chatModel.setImage(model.getAuthorImage());
                chatModel.setTitle(model.getAuthorName());
                chatModel.setMembers(model.getMembers());
            } else if (model.getChatType().equals("bucket")) {
                chatModel.setMembers(model.getMembers());
            }

            Intent intent = new Intent(context, ChatMessageActivity.class);
            intent.putExtra("chatModel", new Gson().toJson(chatModel));
            intent.putExtra( "type",chatModel.getChatType() );
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        }
        return null;
    }
}


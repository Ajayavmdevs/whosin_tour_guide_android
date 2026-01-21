package com.whosin.app.service;

import static com.whosin.app.comman.Graphics.context;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.manager.ActivityTrackerManager;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.GetNotificationManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.BucketListModel;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.MyWalletModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.manager.DialogManager;
import com.whosin.app.ui.activites.CmProfile.CmProfileActivity;
import com.whosin.app.ui.activites.Notification.NotificaionActivity;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;
import com.whosin.app.ui.activites.Promoter.PromoterActivity;
import com.whosin.app.ui.activites.auth.TwoFactorAuthActivity;
import com.whosin.app.ui.activites.bucket.MyInvitationActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.activity.WriteReviewActivity;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.offers.OfferDetailActivity;
import com.whosin.app.ui.activites.offers.VoucherDetailScreenActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.app.ui.activites.venue.Bucket.BucketListDetailActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final Context mContext = MyFirebaseMessagingService.this;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
//        ShortcutBadger.applyCount(context, 10);
        showNotification(message);
    }

    private void showNotification(RemoteMessage message) {
//        if (message.getNotification() == null) {
//            return;
//        }
//        String channelId = "Default";

//        Log.d("Notification", "showNotification: " + message.getNotification().getImageUrl());

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(message.getNotification().getTitle())
//                .setContentText(message.getNotification().getBody())
//                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//
//        if (message.getNotification().getImageUrl() != null) {
//            Bitmap largeIconBitmap = getBitmapFromUrl(message.getNotification().getImageUrl());
//            if (largeIconBitmap != null) {
//                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(largeIconBitmap));
//            }
//        }
//        PendingIntent pendingIntent = getPendingIntent(message);
//        if (pendingIntent != null) {
//            builder.setContentIntent(pendingIntent);
//        }
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_HIGH);
//            channel.setShowBadge(false);
//            manager.createNotificationChannel(channel);
//        }
//        manager.notify(0, builder.build());
    }

    private PendingIntent getPendingIntent(RemoteMessage message) {
        if (message.getData().isEmpty()) {
            return null;
        }
        if (message.getData().containsKey("msg")) {
            String msgData = message.getData().get("msg");
            Log.d("ChatMessage", "getPendingIntent: " + msgData);
            if (!TextUtils.isEmpty(msgData)) {
                ChatMessageModel model = new Gson().fromJson(msgData, ChatMessageModel.class);
                ChatModel chatModel = new ChatModel();
                chatModel.setChatId(model.getChatId());
                chatModel.setChatType(model.getChatType());
                chatModel.setMembers(model.getMembers());
                if (model.getChatType().equals("friend")) {
                    chatModel.setImage(model.getAuthorImage());
                    chatModel.setTitle(model.getAuthorName());
                } else if (model.getChatType().equals("bucket")) {
                    EventBus.getDefault().post(new BucketListModel());
                    BucketListModel bucketListModel = ChatRepository.shared(context).getGroupChatFromCache();
                    Optional<CreateBucketListModel> result = bucketListModel.getBucketsModels().stream().filter(p -> p.getId().equals(model.getChatId())).findAny();
                    if (result.isPresent()) {
                        chatModel.setImage(result.get().getCoverImage());
                        chatModel.setTitle(result.get().getName());
                    }
                } else if (model.getChatType().equals("event")) {

                } else if (model.getChatType().equals("outing")) {

                } else if (model.getChatType().equals("promoter_event")) {
                    EventBus.getDefault().post(new BucketListModel());
                    chatModel.setImage(model.getAuthorImage());
                    chatModel.setTitle(model.getAuthorName());
                    chatModel.setComplementry(true);
                }
                Intent intent = new Intent(this, ChatMessageActivity.class);
                intent.putExtra("chatModel", new Gson().toJson(chatModel));
                intent.putExtra("type", chatModel.getChatType());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            }
        } else if (message.getData().containsKey("data")) {
            String msgData = message.getData().get("data");
            if (!TextUtils.isEmpty(msgData)) {
                JsonObject jsonObject = new Gson().fromJson(msgData, JsonObject.class);
                Log.d("TAG", "getPendingIntent: " + jsonObject);
                if (jsonObject != null && jsonObject.has("type") && jsonObject.has("id")) {
                    String type = jsonObject.get("type").getAsString();
                    String id = jsonObject.get("id").getAsString();
                    Log.d("TAG", "getPendingIntent: " + type);
                    if (Objects.equals(type, "bucket")) {
                        Intent intent = new Intent(this, BucketListDetailActivity.class);
                        intent.putExtra("bucketId", id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    } else if (Objects.equals(type, "event")) {
                        Intent intent = new Intent(this, EventDetailsActivity.class);
                        intent.putExtra("eventId", id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    } else if (Objects.equals(type, "outing")) {
                        Intent intent = new Intent(this, MyInvitationActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("notificationType", "notification");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    } else if (Objects.equals(type, "activity")) {
                        Intent intent = new Intent(this, ActivityListDetail.class);
                        intent.putExtra("activityId", id);
                        intent.putExtra("notificationType", "notification");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    } else if (Objects.equals(type, "deal")) {
                        Intent intent = new Intent(this, VoucherDetailScreenActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("notificationType", "notification");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    } else if (Objects.equals(type, "offer")) {
                        Intent intent = new Intent(this, OfferDetailActivity.class);
                        intent.putExtra("offerId", id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    } else if (Objects.equals(type, "follow")) {
                        Intent intent = new Intent(this, OtherUserProfileActivity.class);
                        intent.putExtra("friendId", id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    } else if (Objects.equals(type, "authentication")) {
                        Intent intent = new Intent(this, TwoFactorAuthActivity.class);
                        intent.putExtra("metadata", new Gson().toJson(jsonObject));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    }else if (Objects.equals(type, "invite")) {
                        if (SessionManager.shared.getUser().isPromoter()){
                            EventBus.getDefault().post(new UserDetailModel());
                        }
                        Intent intent = new Intent(this, ComplementaryEventDetailActivity.class);
                        intent.putExtra("eventId", id);
                        intent.putExtra("type","Promoter");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    }else if (Objects.equals(type, "promoter-event")) {
                        if (SessionManager.shared.getUser().isRingMember()){
                            EventBus.getDefault().post(new ComplimentaryProfileModel());
                            EventBus.getDefault().post(new NotificationModel());
                            EventBus.getDefault().post(new MessageEvent());
                        }
                        Intent intent = new Intent(this, ComplementaryEventDetailActivity.class);
                        intent.putExtra("eventId", id);
                        intent.putExtra("type","complementary");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    }else if (Objects.equals(type, "add-to-ring")) {
                        EventBus.getDefault().post(new NotificationModel());
                        EventBus.getDefault().post(new ComplimentaryProfileModel());
                        EventBus.getDefault().post(new PromoterCirclesModel());
                        if (SessionManager.shared.getUser().isRingMember()) {
                            Intent intent = new Intent(this, NotificaionActivity.class);
                            intent.putExtra("id", id);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                        } else {
                            Intent intent = new Intent(this, PromoterActivity.class);
                            intent.putExtra("isPromoter", false);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                        }

                    }else if (Objects.equals(type, "join-my-ring")) {
                        EventBus.getDefault().post(new NotificationModel());
                        EventBus.getDefault().post(new ComplimentaryProfileModel());
                        EventBus.getDefault().post(new PromoterCirclesModel());
                        if (SessionManager.shared.getUser().isPromoter()){
//                            EventBus.getDefault().post(new PromoterCirclesModel());
//                            Intent intent = new Intent(this, PromoterMyProfile.class);
//                            intent.putExtra("promoterUserId", id);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                        }
                    } else if (Objects.equals(type,"promoter-event-cancel") || Objects.equals(type,"invite_rejected")) {
                        if (SessionManager.shared.getUser().isRingMember()){
                            EventBus.getDefault().post(new ComplimentaryProfileModel());
                            EventBus.getDefault().post(new NotificationModel());
                            EventBus.getDefault().post(new MessageEvent());
                        }
                    } else if (Objects.equals(type, "ring-accepted") || Objects.equals(type, "ring-declined")) {
                        EventBus.getDefault().post(new NotificationModel());
                        EventBus.getDefault().post(new ComplimentaryProfileModel());
                        EventBus.getDefault().post(new PromoterCirclesModel());
                    } else if (Objects.equals(type, "ring-request-accepted")) {
                        EventBus.getDefault().post(new ComplimentaryProfileModel());
                        DialogManager.getInstance(context).showRestartAppDialog("complimentary");
                    }else if (Objects.equals(type, "plusone-accepted")) {
                        if (SessionManager.shared.getUser().isRingMember()){
                            EventBus.getDefault().post(new ComplimentaryProfileModel());
                        }else {
                            EventBus.getDefault().post(new PromoterEventModel());
                        }
                    }else if (Objects.equals(type, "promoter-request") || Objects.equals(type, "promoter-request-accepted")) {
                        DialogManager.getInstance(context).showRestartAppDialog("promoter");
                    }else if (Objects.equals(type, "promoter-subadmin-remove")) {
                        DialogManager.getInstance(context).showRestartAppDialog("subadmin-remove");
                    }else if (Objects.equals(type, "add-to-plusone")) {
                        if (SessionManager.shared.getUser().isRingMember()){
                            EventBus.getDefault().post(new ComplimentaryProfileModel());
                        }else {
                            EventBus.getDefault().post(new PromoterEventModel());
                        }
                    }else if (Objects.equals(type, "plusone-remove") || Objects.equals(type, "circle-remove") || Objects.equals(type, "ring-remove")) {
                        if (!SessionManager.shared.getUser().isPromoter() && !SessionManager.shared.isSubAdmin()){
                            DialogManager.getInstance(context).showRestartAppDialog("remove");
                        }

                    } else if (Objects.equals(type, "plusone-leave") || Objects.equals(type, "cm-leave-ring")) {
                        if (!SessionManager.shared.getUser().isPromoter() && !SessionManager.shared.isSubAdmin()){
                            DialogManager.getInstance(context).showRestartAppDialog("remove");
                        }
                    } else if (Objects.equals(type, "ticket")) {
                        EventBus.getDefault().post(new MyWalletModel());
                        if (!TextUtils.isEmpty(id)){
                            Intent intent = new Intent(this, RaynaTicketDetailActivity.class);
                            intent.putExtra("ticketId", id);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                        }
                    }
                    else if (Objects.equals(type, "review-ticket")) {
                        if (!TextUtils.isEmpty(id)) {
                            Activity activity = ActivityTrackerManager.getInstance().getCurrentActivity();
                            if (activity != null && !activity.isFinishing() && !AppSettingManager.shared.isAlreadyOpenReviewSheet) {
                                FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
                                WriteReviewActivity bottomSheet = new WriteReviewActivity(id,null,"ticket");
                                bottomSheet.ticketID = id;
                                bottomSheet.show(fragmentManager, "");
                                AppSettingManager.shared.isAlreadyOpenReviewSheet = true;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


    private Bitmap getBitmapFromUrl(Uri imageUri) {
        try {
            URL url = new URL(imageUri.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            input.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}

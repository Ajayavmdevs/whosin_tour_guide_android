package com.whosin.app.service.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.InAppNotificationModel;
import com.whosin.app.service.models.TypingEventModel;
import com.whosin.app.ui.activites.auth.TwoFactorAuthActivity;
import com.whosin.app.ui.activites.home.activity.WriteReviewActivity;
import com.whosin.app.ui.fragment.InAppNotification.InAppNotificationDialog;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatManager {

    @NonNull
    public static ChatManager shared = ChatManager.getInstance();
    private static volatile ChatManager instance;

    private Socket mSocket;

    // --------------------------------------
    // region Singleton
    // --------------------------------------

    private ChatManager() {

    }

    @NonNull
    private static synchronized ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public void connect() {
        try {
            if (mSocket == null) {
                mSocket = IO.socket(UrlManager.shared.getSocketHost());
                setOnListener();
            }
            if (!mSocket.connected()) {
                mSocket.connect();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setOnListener() {

        mSocket.on("disconnect", args -> { });

        mSocket.on("connect", args -> {
//            syncChatMessages();
            sendPendingMessages();
            Log.d("TAG", "setOnListener: Socket Connected");
        });

        mSocket.on("connect_error", args -> Log.d("TAG", "setOnListener: Socket Error"));

        mSocket.on("update_data", args -> {
//            Utils.saveLastSyncDate();
            if (SessionManager.shared.getUser() != null) {
                JsonObject jsonObject = new Gson().fromJson(args[0].toString(), JsonObject.class);
                Log.d("update_data", "setOnListener: " + args[0].toString());
                if (jsonObject == null) {
                    return;
                }

                if (jsonObject.has("type") && "cart-sync".equals(jsonObject.get("type").getAsString())) {
                    if (jsonObject.has("userIds")) {
                        JsonArray userIdsArray = jsonObject.getAsJsonArray("userIds");
                        String currentUserId = SessionManager.shared.getUser().getId();
                        for (JsonElement userIdElement : userIdsArray) {
                            if (userIdElement.getAsString().equals(currentUserId)) {
                               if (RaynaTicketManager.shared.cartReloadCallBack != null){
                                   RaynaTicketManager.shared.cartReloadCallBack.onReceive(true);
                               }
                                break;
                            }
                        }
                    }
                    return;
                }
                if (jsonObject.get("metadata") == null) {
                    return;
                }
                JsonObject metaData = jsonObject.get("metadata").getAsJsonObject();
                if (!metaData.has("userId")) { return; }
                if (metaData.get("userId").getAsString().equals(SessionManager.shared.getUser().getId()) ) {
                    if (!metaData.has("device_id")) { return; }
                    if (!metaData.has("status")) { return; }
                    if (metaData.get("status").getAsString().equals("pending") && !metaData.get("device_id").getAsString().equals(Utils.getDeviceUniqueId(Graphics.context))) {
                        Intent intent = new Intent(Graphics.context, TwoFactorAuthActivity.class);
                        intent.putExtra("metadata", new Gson().toJson(jsonObject));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Graphics.context.startActivity(intent);
                    }
                    else if (metaData.get("device_id").getAsString().equals(Utils.getDeviceUniqueId(Graphics.context))) {
                        if (metaData.has("token") && metaData.get("status").getAsString().equals("approved")) {
                            String token = metaData.get("token").getAsString();
                            if (!TextUtils.isEmpty(token)) {
                                Preferences.shared.setString("token", token);
                            }
                        }
                        EventBus.getDefault().post(metaData);
                    }
                }
            }
        });


        mSocket.on("in_app_notification", args -> {
            if (SessionManager.shared.getUser() == null) {
                return;
            }
            if (args.length > 0 && args[0] != null) {
                try {
                    // Log received event
                    Log.d("SocketEvent", "in_app_notification: " + args[0]);

                    // Parse JSON into model
                    Gson gson = new Gson();
                    InAppNotificationModel eventModel = gson.fromJson(args[0].toString(), InAppNotificationModel.class);

                    if (eventModel != null) {
                        String eventJson = gson.toJson(eventModel);
                        String userType = eventModel.getUserType();
                        String userId = eventModel.getUserId();
                        String currentUserId = SessionManager.shared.getUser() != null ? SessionManager.shared.getUser().getId() : null;

                        boolean shouldShowDialog =
                                ("individual".equals(userType) && userId != null && userId.equals(currentUserId)) ||
                                        "all".equals(userType) || "only-live".equals(userType);

                        if (shouldShowDialog) {
                            Activity activity = ActivityTrackerManager.getInstance().getCurrentActivity();
                            Context context = (activity != null && !activity.isFinishing()) ? activity : Graphics.context;

                            if (context != null) {
                                Intent intent = new Intent(context, InAppNotificationDialog.class);
                                intent.putExtra("eventModelJson", eventJson);

                                // Only add FLAG_ACTIVITY_NEW_TASK if context is NOT an Activity
                                if (!(context instanceof Activity)) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                }

                                context.startActivity(intent);
                            } else {
                                Log.w("InAppNotification", "No valid context found to show dialog");
                            }
                        }
                    }

//                    if (eventModel != null) {
//                        String eventJson = gson.toJson(eventModel);
//                        if (eventModel.getUserType().equals("individual") && eventModel.getUserId().equals(SessionManager.shared.getUser().getId())) {
//                            Activity activity = ActivityTrackerManager.getInstance().getCurrentActivity();
//                            if (activity != null && !activity.isFinishing()) {
//                                Intent intent = new Intent(activity, InAppNotificationDialog.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.putExtra("eventModelJson", eventJson);
//                                activity.startActivity(intent);
//                            } else {
//                                Intent intent = new Intent(Graphics.context, InAppNotificationDialog.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.putExtra("eventModelJson", eventJson);
//                                Graphics.context.startActivity(intent);
//                            }
//                        } else if (eventModel.getUserType().equals("all") || eventModel.getUserType().equals("only-live")) {
//                            Activity activity = ActivityTrackerManager.getInstance().getCurrentActivity();
//                            if (activity != null && !activity.isFinishing()) {
//                                Intent intent = new Intent(activity, InAppNotificationDialog.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.putExtra("eventModelJson", eventJson);
//                                activity.startActivity(intent);
//                            } else {
//                                Intent intent = new Intent(Graphics.context, InAppNotificationDialog.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.putExtra("eventModelJson", eventJson);
//                                Graphics.context.startActivity(intent);
//                            }
//                        }
//
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        mSocket.on("typing", args -> {
            Utils.saveLastSyncDate();
            TypingEventModel model = new Gson().fromJson(args[0].toString(), TypingEventModel.class);
            String tmpId = SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId();
            if (Objects.equals(model.userId, tmpId)) { return; }
//            if (Objects.equals(model.userId, SessionManager.shared.getUser().getId())) { return; }
            if (model.receivers.contains(SessionManager.shared.getUser().getId())) {
                EventBus.getDefault().post(model);
            }
        });

//        mSocket.on("ticket_review", args -> {
//            TypingEventModel model = new Gson().fromJson(args[0].toString(), TypingEventModel.class);
//            if (!TextUtils.isEmpty(id)) {
//                Activity activity = ActivityTrackerManager.getInstance().getCurrentActivity();
//                if (activity != null && !activity.isFinishing() && !AppSettingManager.shared.isAlreadyOpenReviewSheet) {
//                    FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
//                    WriteReviewActivity bottomSheet = new WriteReviewActivity(id, null, "ticket");
//                    bottomSheet.ticketID = id;
//                    bottomSheet.show(fragmentManager, "");
//                    AppSettingManager.shared.isAlreadyOpenReviewSheet = true;
//                }
//            }
//
//        });

        mSocket.on("new_message", args -> {
            Utils.saveLastSyncDate();
            ChatMessageModel model = new Gson().fromJson(args[0].toString(), ChatMessageModel.class);
            if (model == null) { return; }
            if (model.getMembers().isEmpty()) { return; }
            String tmpId = SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId();
            if (!model.getMembers().contains(tmpId)) {
                return;
            }
//            if (!model.getMembers().contains(SessionManager.shared.getUser().getId())) {
//                return;
//            }
            Log.d("TAG", "new_message: " + model.getChatId());
            AppExecutors.get().mainThread().execute(() -> {
                ChatRepository.shared(Graphics.context).addMessage(model, data -> {
                    Log.d("TAG", "new_message: added " + model.getChatId());
                    EventBus.getDefault().post(model.getChatId());
                    if (!model.isSent()) {
                        sendDeliveryEvent(Collections.singletonList(model));
                    }
                });
            });
        });

        mSocket.on("seen_event", args -> {
            Utils.saveLastSyncDate();
            Type typeOfT = TypeToken.getParameterized(List.class, ChatMessageModel.class).getType();
            List<ChatMessageModel> chatMessageList = new Gson().fromJson(args[0].toString(), typeOfT);
            if (chatMessageList.isEmpty()) { return; }
            String tmpId = SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId();
            List<ChatMessageModel> filterList = chatMessageList.stream().filter(p -> !p.isSent() && p.getMembers().contains(tmpId)).collect(Collectors.toList());
//            List<ChatMessageModel> filterList = chatMessageList.stream().filter(p -> !p.isSent() && p.getMembers().contains(SessionManager.shared.getUser().getId())).collect(Collectors.toList());
            if(filterList.isEmpty()) { return; }
            List<String> ids = filterList.stream().map(ChatMessageModel::getId).collect(Collectors.toList());
            String author = filterList.get(0).getAuthor();
            if (TextUtils.isEmpty(author)) { return; }
            updateSeenBy(ids, author);
        });

        mSocket.on("delivered_event", args -> {
            Utils.saveLastSyncDate();
            Type typeOfT = TypeToken.getParameterized(List.class, ChatMessageModel.class).getType();
            List<ChatMessageModel> chatMessageList = new Gson().fromJson(args[0].toString(), typeOfT);
            if (chatMessageList.isEmpty()) { return; }
            String tmpId = SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId();
            List<ChatMessageModel> filterList = chatMessageList.stream().filter(p -> !p.isSent() && p.getMembers().contains(tmpId)).collect(Collectors.toList());
//            List<ChatMessageModel> filterList = chatMessageList.stream().filter(p -> !p.isSent() && p.getMembers().contains(SessionManager.shared.getUser().getId())).collect(Collectors.toList());
            if(filterList.isEmpty()) { return; }
            List<String> ids = filterList.stream().map(ChatMessageModel::getId).collect(Collectors.toList());
            String author = filterList.get(0).getAuthor();
            if (TextUtils.isEmpty(author)) { return; }

            updateReceivers(ids, author);
        });
    }

    public void  sendTypingEvent(String chatId, List<String> members, String chatType, Boolean status) {
        if (mSocket == null) { return; }
        if (!mSocket.connected()) { return; }
        if (members == null) { return; }
        if (members.isEmpty()) { return; }
        JsonObject object = new JsonObject();
        object.addProperty("chatId", chatId);
        String tmpId = SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId();
        object.addProperty("userId", tmpId);
//        object.addProperty("userId", SessionManager.shared.getUser().getId());
        object.addProperty("userName", SessionManager.shared.getUser().getFullName());

//        object.addProperty("userName", SessionManager.shared.getUser().getFullName());
        object.addProperty("chatType", chatType);
        object.addProperty("isForStartTyping", status);
        JsonArray jsonArray = new Gson().toJsonTree(members).getAsJsonArray();
        object.add("receivers", jsonArray);
        if (!object.isJsonNull()) {
            mSocket.emit("typing", object);
        }
    }

    public void sendMessage(ChatMessageModel model) {
        if (mSocket == null) { return; }
        if (!mSocket.connected()) { return; }
        if (model == null) { return; }
        JsonObject jsonObject = new Gson().toJsonTree(model).getAsJsonObject();
        mSocket.emit("new_message", jsonObject, (Emitter.Listener) args -> {
            Log.d("TAG", "sendMessage: " + model.getChatId());
        });
    }

    public void sendDeliveryEvent(List<ChatMessageModel> messageModelList) {
        if (mSocket == null) { return; }
        if (!mSocket.connected()) { return; }
        if (messageModelList == null) { return;}
        if (messageModelList.isEmpty()) { return;}
        JsonArray jsonString = msgListToJson(messageModelList);
        mSocket.emit("delivered_event", jsonString);
        List<String> ids = messageModelList.stream().map(ChatMessageModel::getId).collect(Collectors.toList());
        String tmpId = SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId();
        updateReceivers(ids, tmpId);
//        updateReceivers(ids, SessionManager.shared.getUser().getId());
    }

    public void sendSeenEvent(List<ChatMessageModel> messageModelList) {
        if (mSocket == null) { return; }
        if (!mSocket.connected()) { return; }
        if (messageModelList == null) { return;}
        if (messageModelList.isEmpty()) { return;}
        JsonArray jsonString = msgListToJson(messageModelList);
//        mSocket.emit("seen_event", jsonString, (Ack) args -> {
//            List<String> ids = messageModelList.stream().map(ChatMessageModel::getId).collect(Collectors.toList());
//            updateSeenBy(ids, SessionManager.shared.getUser().getId());
//        });
//
        mSocket.emit("seen_event", jsonString);
        List<String> ids = messageModelList.stream().map(ChatMessageModel::getId).collect(Collectors.toList());
        String tmpId = SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId();
        updateSeenBy(ids, tmpId);
//        updateSeenBy(ids, SessionManager.shared.getUser().getId());
    }

    public void updateSeenBy(List<String> ids, String seebBy) {
        AppExecutors.get().mainThread().execute(() -> {
            ChatRepository.shared(Graphics.context).updateSeenBy(ids, seebBy, data -> {
                List<String> chatIds = data.stream().map(ChatMessageModel::getChatId).distinct().collect(Collectors.toList());
                chatIds.forEach(p -> EventBus.getDefault().post(p));
            });
        });
    }

    public void updateReceivers(List<String> ids, String receiver) {
        AppExecutors.get().mainThread().execute(() -> {
            ChatRepository.shared(Graphics.context).updateReceives(ids, receiver, data -> {
                List<String> chatIds = data.stream().map(ChatMessageModel::getChatId).distinct().collect(Collectors.toList());
                chatIds.forEach( p -> {
                    EventBus.getDefault().post(p);
                });
            });
        });
    }

    private JsonArray msgListToJson(List<ChatMessageModel> messageModelList) {
        JsonArray jsonArray = new JsonArray();
        messageModelList.forEach( messageModel -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", messageModel.getId());
            jsonObject.addProperty("chatId", messageModel.getChatId());
            jsonObject.addProperty("author", SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId());
//            jsonObject.addProperty("author", SessionManager.shared.getUser().getId());
            JsonArray members = new JsonArray();
            messageModel.getMembers().forEach(members::add);
            jsonObject.add("members", members);
            jsonArray.add(jsonObject);
        });

        return jsonArray;
    }

    public void syncChatMessages(boolean isPromoter) {
        ChatRepository.shared(Graphics.context).syncChatMessages(data -> {
            if (data.isEmpty()) { return; }
            String userId = SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId();
            List<ChatMessageModel> filteredList = data.stream().filter(p -> !p.getReceivers().contains(userId)).collect(Collectors.toList());
            sendDeliveryEvent(filteredList);
        },isPromoter);
    }

    private void sendPendingMessages() {
        AppExecutors.get().mainThread().execute(() -> {
            List<ChatMessageModel> pendingMessages = ChatRepository.shared(Graphics.context).getPendingChatMessages();
            if (pendingMessages != null) {
                pendingMessages.forEach(p -> {
                    sendMessage(p);
                });
            }
        });

    }





}

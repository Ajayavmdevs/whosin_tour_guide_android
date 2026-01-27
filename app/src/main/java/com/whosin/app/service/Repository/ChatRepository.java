package com.whosin.app.service.Repository;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.whosin.app.comman.JsonUtils;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.BucketListModel;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.EventChatListModel;
import com.whosin.app.service.models.EventChatModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatRepository extends RealmRepository {

    private Context context;

    private static volatile ChatRepository _instance = null;

    public static ChatRepository shared(Context ctx) {
        if (_instance == null) {
            synchronized (ChatRepository.class) {
                _instance = new ChatRepository();
            }
        }
        _instance.context = ctx;
        return _instance;
    }

    public void clearDb() {
        getRealm().executeTransactionAsync(bgRealm -> bgRealm.deleteAll(), () -> getRealm().refresh());
    }

    public List<ChatMessageModel> getChatMessages(String chatId) {
        RealmResults<ChatMessageModel> results = ChatMessageModel.getMessageByChatId(getRealm(), chatId).sort("date", Sort.ASCENDING);
        if(!results.isEmpty()) {
            return getRealm().copyFromRealm(results);
        }
        return Collections.emptyList();
    }

    public long getUnrealMessageCount(String chatId) {
        return ChatMessageModel.getUnrealMessageCount(getRealm(),chatId);
    }
    public long getAllUnrealMessageCount() {
        return ChatMessageModel.getAllUnrealMessageCount(getRealm());
    }

    public List<ChatMessageModel> getMediaMessages(String chatId) {
        RealmResults<ChatMessageModel> results = ChatMessageModel.getMessageByIdAndType(getRealm(), chatId, "image");
        if(!results.isEmpty()) {
            return getRealm().copyFromRealm(results);
        }
        return null;
    }

    public ChatMessageModel getLastMessages(String chatId) {
        ChatMessageModel results = ChatMessageModel.getLastMessage(getRealm(), chatId);
        if(results != null) {
            return getRealm().copyFromRealm(results);
        }
        return null;
    }

    public void clearChat(String chatId, CommanCallback<Boolean> delegate) {
        getRealm().executeTransactionAsync(bgRealm -> {
            RealmResults<ChatMessageModel> results = ChatMessageModel.getMessageByChatId(bgRealm, chatId);
            if (!results.isEmpty()) {
                results.deleteAllFromRealm();
            }
        }, () -> {
            getRealm().refresh();
            delegate.onReceive(true);
        });
    }

    public void addMessage(ChatMessageModel msgObj, CommanCallback<Boolean> delegate) {

        getRealm().executeTransactionAsync( bgRealm -> {
            bgRealm.insertOrUpdate( msgObj );
            if (msgObj.getChatType().equals("friend")) {
                ChatModel chatModel = ChatModel.getChatById(bgRealm, msgObj.getChatId());
                if (chatModel == null) {
                    ChatModel newModel = new ChatModel(msgObj);
                    bgRealm.insertOrUpdate(newModel);
                }
            }
        }, () -> {
            getRealm().refresh();
            delegate.onReceive( true );
        } );
    }

    public void updateReceives(List<String> msgIds, String receiver, CommanCallback<List<ChatMessageModel>> delegate) {
        getRealm().executeTransactionAsync( bgRealm -> {
            RealmResults<ChatMessageModel> results = ChatMessageModel.getMessageByIds(bgRealm, msgIds);
            results.forEach(messageModel -> {
                if (!messageModel.getReceivers().contains(receiver)) {
                    messageModel.getReceivers().add(receiver);
                }
            });
        }, () -> {
            getRealm().refresh();
            RealmResults<ChatMessageModel> results = ChatMessageModel.getMessageByIds(getRealm(), msgIds);
            List<ChatMessageModel> copiedObjects = getRealm().copyFromRealm(results);
            delegate.onReceive( copiedObjects );
        } );
    }

    public void updateSeenBy(List<String> msgIds, String seenBy, CommanCallback<List<ChatMessageModel>> delegate) {
        getRealm().executeTransactionAsync( bgRealm -> {
            RealmResults<ChatMessageModel> results = ChatMessageModel.getMessageByIds(bgRealm, msgIds);
            results.forEach(messageModel -> {
                if (!messageModel.getSeenBy().contains(seenBy)) {
                    messageModel.getSeenBy().add(seenBy);
                }
            });
        }, () -> {
            getRealm().refresh();
            RealmResults<ChatMessageModel> results = ChatMessageModel.getMessageByIds(getRealm(), msgIds);
            List<ChatMessageModel> copiedObjects = getRealm().copyFromRealm(results);
            delegate.onReceive( copiedObjects );
        } );
    }

    public void updateSeenByForBuckets(List<String> msgIds, String seenBy) {
        getRealm().executeTransactionAsync(bgRealm -> {
            RealmResults<ChatMessageModel> results = bgRealm.where(ChatMessageModel.class)
                    .in("chatId", msgIds.toArray(new String[0]))
                    .findAll();

            results.forEach(messageModel -> {
                if (!messageModel.getSeenBy().contains(seenBy)) {
                    messageModel.getSeenBy().add(seenBy);
                }
            });
        }, getRealm()::refresh);
    }


    public void syncChatMessages(CommanCallback<List<ChatMessageModel>> delegate,boolean isPromoter) {
        DataService.shared(context).requestSyncChatMsg(new RestCallback<ContainerListModel<ChatMessageModel>>(null) {
            @Override
            public void result(ContainerListModel<ChatMessageModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    delegate.onReceive(new ArrayList<>());
                    return;
                }
                Utils.saveLastSyncDate();
                if (model.data != null) {
                    getRealm().executeTransactionAsync(bgRealm -> {
                        bgRealm.insertOrUpdate(model.data);
                        model.data.forEach( p -> {
                            if (p.getChatType().equals("friend")) {
                                ChatModel chatModel = ChatModel.getChatById(bgRealm, p.getChatId());
                                if (chatModel == null) {
                                    ChatModel newModel = new ChatModel(p);
                                    bgRealm.insertOrUpdate(newModel);
                                }
                            }
                            else if (p.getChatType().equals("promoter_event")) {
                                ChatModel chatModel = ChatModel.getChatById(bgRealm, p.getChatId());
                                if (chatModel == null) {
                                    ChatModel newModel = new ChatModel(p,isPromoter);
                                    bgRealm.insertOrUpdate(newModel);
                                }
                            }
                        });
                    }, () -> {
                        delegate.onReceive(model.data);
                    });
                }
            }
        });
    }


    public List<ChatMessageModel> getPendingChatMessages() {
        String userId = SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId();
        RealmResults<ChatMessageModel> results = ChatMessageModel.getPendingMessageByUserId(getRealm(), userId);
        if(!results.isEmpty()) {
            return getRealm().copyFromRealm(results);
        }
        return null;
    }


    public BucketListModel getGroupChatFromCache() {
        String json = Preferences.shared.getString("groupChatList");
        if (!TextUtils.isEmpty(json)) {
            return new Gson().fromJson(json, BucketListModel.class);
        }
        return null;
    }
}


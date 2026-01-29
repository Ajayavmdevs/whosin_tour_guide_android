package com.whosin.business.service.models;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.R;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.CryptLib;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.Repository.ChatRepository;
import com.whosin.business.service.manager.SessionManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class ChatMessageModel extends RealmObject implements DiffIdentifier,ModelProtocol {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("chatId")
    @Expose
    private String chatId;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("audioDuration")
    @Expose
    private String audioDuration;

    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("authorName")
    @Expose
    private String authorName;
    @SerializedName("authorImage")
    @Expose
    private String authorImage;

    @SerializedName("sending")
    @Expose
    private String sending;

    @SerializedName("members")
    @Expose
    private RealmList<String> members = new RealmList<>();
    @SerializedName("receivers")
    @Expose
    private RealmList<String> receivers = new RealmList<>();
    @SerializedName("seenBy")
    @Expose
    private RealmList<String> seenBy = new RealmList<>();

    @SerializedName("chatType")
    @Expose
    private String chatType;

    @SerializedName("replyBy")
    @Expose
    private String replyBy;

    @SerializedName("replyTo")
    @Expose
    private ReplyToModel replyToModel;

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }


    public ChatMessageModel(){}

    public ChatMessageModel(String msg, String type, ChatModel chatModel, List<String> members) {
        long timestamp = System.currentTimeMillis() / 1000;
        if (SessionManager.shared.isSubAdmin()){
            this.id = SessionManager.shared.getSubAdminUser().getPromoterId() + "_" + timestamp;
        } else if (SessionManager.shared.isPromoterSubAdmin()) {
            this.id = SessionManager.shared.getPromoterId() + "_" + timestamp;
        } else {
            this.id =   SessionManager.shared.getUser().getId() +"_" + timestamp;
        }
        try {
            CryptLib cryptLib = new CryptLib();
            this.msg = cryptLib.encryptPlainTextWithRandomIV(msg, chatModel.getChatId());
        } catch (Exception e) {
            this.msg = msg;
        }
        this.type = type;
        this.chatId = chatModel.getChatId();
        this.members = (RealmList<String>) members;
        this.author = SessionManager.shared.getUser().getId();
        this.authorName = SessionManager.shared.getUser().getFullName();
        this.authorImage = SessionManager.shared.getUser().getImage();

        this.date = String.valueOf(timestamp);
        this.chatType = chatModel.getChatType();
        if (SessionManager.shared.getSubAdminUser() != null && SessionManager.shared.getSubAdminUser().getLoginType().equals("sub-admin")) {
            this.replyBy = SessionManager.shared.getSubAdminUser().getId();
        } else if (SessionManager.shared.isPromoterSubAdmin()) {
            this.replyBy = SessionManager.shared.getUser().getId();
        }
    }

    public ChatMessageModel(String msg, String type, ChatModel chatModel, List<String> members,boolean isFromSubAdmin , PromoterEventModel promoterEventModel) {
        long timestamp = System.currentTimeMillis() / 1000;
        if (SessionManager.shared.isSubAdmin()){
            this.id = SessionManager.shared.getSubAdminUser().getPromoterId() + "_" + timestamp;
        } else if (SessionManager.shared.isPromoterSubAdmin()) {
            this.id = SessionManager.shared.getPromoterId() + "_" + timestamp;
        } else {
            this.id =   SessionManager.shared.getUser().getId() +"_" + timestamp;
        }
        try {
            CryptLib cryptLib = new CryptLib();
            this.msg = cryptLib.encryptPlainTextWithRandomIV(msg, chatModel.getChatId());
        } catch (Exception e) {
            this.msg = msg;
        }
        this.type = type;
        this.chatId = chatModel.getChatId();
        this.members = (RealmList<String>) members;
        this.author = SessionManager.shared.getUser().getId();
        this.authorName = SessionManager.shared.getUser().getFullName();
        this.authorImage = SessionManager.shared.getUser().getImage();

        this.date = String.valueOf(timestamp);
        this.chatType = chatModel.getChatType();
        if (SessionManager.shared.getSubAdminUser() != null && SessionManager.shared.getSubAdminUser().getLoginType().equals("sub-admin")){
            this.replyBy = SessionManager.shared.getSubAdminUser().getId();
        } else if (SessionManager.shared.isPromoterSubAdmin()) {
            this.replyBy = SessionManager.shared.getUser().getId();
        }

        if (isFromSubAdmin && promoterEventModel != null){
            this.replyToModel = new ReplyToModel("Prmoter-event",getEventJson(promoterEventModel),id);
        }
    }


    public ChatMessageModel(String msg, String type, ChatModel chatModel, List<String> members,String raynaTicketJSon) {
        long timestamp = System.currentTimeMillis() / 1000;
        if (SessionManager.shared.isSubAdmin()){
            this.id = SessionManager.shared.getSubAdminUser().getPromoterId() + "_" + timestamp;
        } else if (SessionManager.shared.isPromoterSubAdmin()) {
            this.id = SessionManager.shared.getPromoterId() + "_" + timestamp;
        } else {
            this.id =   SessionManager.shared.getUser().getId() +"_" + timestamp;
        }
        try {
            CryptLib cryptLib = new CryptLib();
            this.msg = cryptLib.encryptPlainTextWithRandomIV(msg, chatModel.getChatId());
        } catch (Exception e) {
            this.msg = msg;
        }
        this.type = type;
        this.chatId = chatModel.getChatId();
        this.members = (RealmList<String>) members;
        this.author = SessionManager.shared.getUser().getId();
        this.authorName = SessionManager.shared.getUser().getFullName();
        this.authorImage = SessionManager.shared.getUser().getImage();

        this.date = String.valueOf(timestamp);
        this.chatType = chatModel.getChatType();
        if (SessionManager.shared.getSubAdminUser() != null && SessionManager.shared.getSubAdminUser().getLoginType().equals("sub-admin")){
            this.replyBy = SessionManager.shared.getSubAdminUser().getId();
        } else if (SessionManager.shared.isPromoterSubAdmin()) {
            this.replyBy = SessionManager.shared.getUser().getId();
        }

        if (!TextUtils.isEmpty(raynaTicketJSon)){
            this.replyToModel = new ReplyToModel("ticket",raynaTicketJSon,id);
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public ReplyToModel getReplyToModel() {
        return replyToModel;
    }

    public void setReplyToModel(ReplyToModel replyToModel) {
        this.replyToModel = replyToModel;
    }

    public String getMsg() {
        try {
            CryptLib cryptLib = new CryptLib();
            return cryptLib.decryptCipherTextWithRandomIV(msg, getChatId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void setMsg(String msg) {
        try {
            CryptLib cryptLib = new CryptLib();
            String encryptedMsg =cryptLib.encryptPlainTextWithRandomIV(msg, getChatId());
            this.msg = encryptedMsg;
        } catch (Exception e) {
            this.msg = msg;
            e.printStackTrace();
        }

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RealmList<String> getMembers() {
        if (members == null) {
            return new RealmList<>();
        }
        return members;
    }

    public void setMembers(RealmList<String> members) {
        this.members = members;
    }

    public RealmList<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(RealmList<String> receivers) {
        this.receivers = receivers;
    }

    public String getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(String audioDuration) {
        this.audioDuration = audioDuration;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDate() {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        return date;
    }

    public String getDate(String format) {
        if (TextUtils.isEmpty( date )) {
            return "";
        }
        return Utils.timestampToString( Long.parseLong( date ), format );
    }

    public String getSending() {
        return sending;
    }

    public void setSending(String sending) {
        this.sending = sending;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSent() {
        if (SessionManager.shared.isSubAdmin()){
            return Objects.equals( getAuthor(), SessionManager.shared.getSubAdminUser().getPromoterId());
        } else if (SessionManager.shared.isPromoterSubAdmin()) {
            return Objects.equals( getAuthor(), SessionManager.shared.getPromoterId() );
        } else {
            return Objects.equals( getAuthor(), SessionManager.shared.getUser().getId() );
        }
    }

    public boolean isCmAdminMessage() {
        return replyToModel != null && !TextUtils.isEmpty(replyToModel.getType()) && replyToModel.getType().equals("Prmoter-event");
    }

    public boolean isRaynaTicketMessage() {
        return replyToModel != null && !TextUtils.isEmpty(replyToModel.getType()) && replyToModel.getType().equals("ticket");
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public String getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
    }

    public RealmList<String> getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(RealmList<String> seenBy) {
        this.seenBy = seenBy;
    }

    public String getReply_by() {
        return replyBy;
    }

    public void setReply_by(String replyBy) {
        this.replyBy = replyBy;
    }

    public static RealmResults<ChatMessageModel> getMessageByIds(@NonNull Realm realm, List<String> ids) {
        String[] stringIds = ids.toArray(new String[0]);
        return realm.where( ChatMessageModel.class ).in("id", stringIds).findAll();
    }

    public static long getUnrealMessageCount(@NonNull Realm realm, String chatId) {
        String authorId = SessionManager.shared.getSubAdminUser() != null ? SessionManager.shared.getSubAdminUser().getPromoterId() :SessionManager.shared.getUser().getId();
        String seenByID = SessionManager.shared.getSubAdminUser() != null ? SessionManager.shared.getSubAdminUser().getPromoterId() :SessionManager.shared.getUser().getId();
        if (SessionManager.shared.isPromoterSubAdmin()){
            authorId = SessionManager.shared.getPromoterId();
            seenByID = SessionManager.shared.getPromoterId();
        }
        return realm.where( ChatMessageModel.class )
                .equalTo("chatId", chatId)
                .notEqualTo("author", authorId)
                .not().contains("seenBy", seenByID)
                .count();
//        return realm.where( ChatMessageModel.class )
//                .equalTo("chatId", chatId)
//                .notEqualTo("author", SessionManager.shared.getUser().getId())
//                .not().contains("seenBy", SessionManager.shared.getUser().getId())
//                .count();
    }
    
    public static long getAllUnrealMessageCount(@NonNull Realm realm) {
        BucketListModel chatList = ChatRepository.shared(Graphics.context).getGroupChatFromCache();
        if (chatList != null && !chatList.getBucketsModels().isEmpty()) {
            List<String> bucketlistid = chatList.getBucketsModels().stream().map(CreateBucketListModel::getId).collect(Collectors.toList());
            RealmResults<ChatMessageModel> results = realm.where(ChatMessageModel.class).not().contains("seenBy", SessionManager.shared.getUser().getId()).equalTo("chatType", "bucket").findAll();
            List<String> chatIds = results.stream().map(ChatMessageModel::getChatId).distinct().filter(id -> !bucketlistid.contains(id)).collect(Collectors.toList());
            if (!chatIds.isEmpty()) {
                ChatRepository.shared(Graphics.context).updateSeenByForBuckets(chatIds, SessionManager.shared.getUser().getId());
            }
        }

        String authorId = SessionManager.shared.getSubAdminUser() != null ? SessionManager.shared.getSubAdminUser().getPromoterId() :SessionManager.shared.getUser().getId();
        String seenByID = SessionManager.shared.getSubAdminUser() != null ? SessionManager.shared.getSubAdminUser().getPromoterId() :SessionManager.shared.getUser().getId();
        if (SessionManager.shared.isPromoterSubAdmin()){
            authorId = SessionManager.shared.getPromoterId();
            seenByID = SessionManager.shared.getPromoterId();
        }
        return realm.where(ChatMessageModel.class)
                .notEqualTo("author", authorId)
                .not().contains("seenBy",seenByID)
                .count();
//        return realm.where(ChatMessageModel.class)
//                .notEqualTo("author", SessionManager.shared.getUser().getId())
//                .not().contains("seenBy", SessionManager.shared.getUser().getId())
//                .count();
    }


    public static long getAllUnrealMessageCountForSubAdmin(@NonNull Realm realm) {
        String authorId = SessionManager.shared.getSubAdminUser() != null ? SessionManager.shared.getSubAdminUser().getPromoterId() : SessionManager.shared.getUser().getId();
        String seenByID = SessionManager.shared.getSubAdminUser() != null ? SessionManager.shared.getSubAdminUser().getPromoterId() : SessionManager.shared.getUser().getId();
        return realm.where(ChatMessageModel.class)
                .notEqualTo("author", authorId)
                .not().contains("seenBy",seenByID)
                .and() // Add AND condition
                .beginGroup() // Begin group for OR condition on chatType
                .equalTo("chatType", "friend") // chatType is "friend"
                .or()
                .equalTo("chatType", "promoter_event") // chatType is "promoterEvent"
                .endGroup()
                .count();
    }

    public static RealmResults<ChatMessageModel> getMessageByChatId(@NonNull Realm realm, String chatId) {
        return realm.where( ChatMessageModel.class ).equalTo("chatId", chatId).findAll();
    }

    public static RealmResults<ChatMessageModel> getPendingMessageByUserId(@NonNull Realm realm, String userId) {
        return realm.where( ChatMessageModel.class )
                .equalTo("author", userId)
                .not().contains("receivers",userId)
                .findAll();
    }

    public static RealmResults<ChatMessageModel> getMessageByIdAndType(@NonNull Realm realm, String chatId, String type) {
        return realm.where( ChatMessageModel.class ).equalTo("chatId", chatId).equalTo("type", type).findAll();
    }

    public static ChatMessageModel getLastMessage(@NonNull Realm realm, String chatId) {
        return realm.where( ChatMessageModel.class ).equalTo("chatId", chatId).sort("date", Sort.DESCENDING).findFirst();
    }

    public Drawable getStatusIcon() {
        int your_drawable = R.drawable.icon_msg_pending;
        int color = ContextCompat.getColor(Graphics.context, R.color.white);
        if (members != null && seenBy != null && receivers != null) {
            if (seenBy.size() >= members.size() - 1) {
                your_drawable = R.drawable.icon_msg_delivered;
                color = ContextCompat.getColor(Graphics.context, R.color.light_green);
            } else if (receivers.size() >= members.size()) {
                your_drawable = R.drawable.icon_msg_delivered;
            } else if (receivers.contains(SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId())) {
                your_drawable = R.drawable.icon_msg_sent;
            } else {
                your_drawable = R.drawable.icon_msg_pending;
            }
        }

        Drawable drawable = ContextCompat.getDrawable(Graphics.context, your_drawable);
        if (drawable != null) {
            drawable.setTint(color);
            DrawableCompat.setTint(drawable, color);
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        }
        return drawable;
    }

    public String getSendTime() {
        String time = "";
        if (seenBy.size() >= members.size() -1) {
            time = getDate(AppConstants.DATEFORMAT_24HOUR);

        }
        else if (receivers.size() >= members.size()) {
            time = getDate(AppConstants.DATEFORMAT_24HOUR);
        }
        else if (receivers.contains(SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId())) {
            time = getDate(AppConstants.DATEFORMAT_24HOUR);
        }
        else {
            time = "sending";
        }
        return time;
    }

    private String getEventJson(PromoterEventModel promoterEventModel){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("eventId",promoterEventModel.getId());
        jsonObject.addProperty("type",promoterEventModel.getType());
        jsonObject.addProperty("venueType",promoterEventModel.getVenueType());
        jsonObject.addProperty("date",promoterEventModel.getDate());
        jsonObject.addProperty("startTime",promoterEventModel.getStartTime());
        jsonObject.addProperty("endTime",promoterEventModel.getEndTime());
        jsonObject.addProperty("status",promoterEventModel.getStatus());
        if (promoterEventModel.getVenueType().equals("venue")){
            JsonObject venueJson = new JsonObject();
            venueJson.addProperty("name", promoterEventModel.getVenue() != null ? promoterEventModel.getVenue().getName() : "");
            venueJson.addProperty("address", promoterEventModel.getVenue() != null ? promoterEventModel.getVenue().getAddress() : "");
            venueJson.addProperty("image", promoterEventModel.getVenue() != null ? promoterEventModel.getVenue().getCover() : "");
            venueJson.addProperty("image", promoterEventModel.getVenue() != null ? promoterEventModel.getVenue().getCover() : "");
            venueJson.addProperty("logo", promoterEventModel.getVenue() != null ? promoterEventModel.getVenue().getLogo() : "");
            jsonObject.add("customVenue",venueJson);
        }else {
            JsonObject customVenueJson = new JsonObject();
            customVenueJson.addProperty("name", promoterEventModel.getCustomVenue() != null ? promoterEventModel.getCustomVenue().getName() : "");
            customVenueJson.addProperty("address", promoterEventModel.getCustomVenue() != null ? promoterEventModel.getCustomVenue().getAddress() : "");
            customVenueJson.addProperty("image", promoterEventModel.getCustomVenue() != null ? promoterEventModel.getCustomVenue().getImage() : "");
            customVenueJson.addProperty("description", promoterEventModel.getCustomVenue() != null ? promoterEventModel.getCustomVenue().getDescription() : "");
            customVenueJson.addProperty("logo",  promoterEventModel.getCustomVenue() != null ? promoterEventModel.getCustomVenue().getImage() : "");

            jsonObject.add("customVenue",customVenueJson);
        }

        return jsonObject.toString();

    }


}

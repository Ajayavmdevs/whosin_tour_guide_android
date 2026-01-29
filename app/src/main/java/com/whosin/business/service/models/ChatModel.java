package com.whosin.business.service.models;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.Repository.ChatRepository;
import com.whosin.business.service.Repository.UserRepository;
import com.whosin.business.service.manager.SessionManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class ChatModel extends RealmObject implements DiffIdentifier, ModelProtocol {

    @PrimaryKey
    @SerializedName("chatId")
    @Expose
    private String chatId = "";

    @SerializedName("chatType")
    @Expose
    private String chatType;

    @SerializedName("members")
    @Expose
    private RealmList<String> members;

    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";

    private String title = "";
    private String image = "";

    private String eventCoverImage = "";

    private String eventOrgName = "";
    private String outingCrateUserName = "";

    private boolean isComplementry = false;
    private boolean isPromoter = false;

    private String  eventOwnerIdForCm = "";

    public  ChatModel() {}

    public  ChatModel(ChatMessageModel model) {
        this.chatId = model.getChatId();
        this.chatType = model.getChatType();
        this.members = model.getMembers();
        this.createdAt = model.getDate();
    }

    public  ChatModel(UserDetailModel model) {
        this.chatType = "friend";
        this.title = model.getFullName();
        this.image = model.getImage();
        RealmList<String> idList = new RealmList<>();
        idList.add(SessionManager.shared.isUserOrSubAdmin());
        idList.add( model.getId() );
        this.members =idList;
        Collections.sort( idList );
        String joinedString = String.join( ",", idList );
        this.chatId = joinedString;
    }

    public  ChatModel(ContactListModel model) {
        this.chatType = "friend";
        this.title = model.getFullName();
        this.image = model.getImage();
        RealmList<String> idList = new RealmList<>();
        idList.add(SessionManager.shared.isUserOrSubAdmin());
        idList.add( model.getId() );
        this.members = idList;
        Collections.sort( idList );
        String joinedString = String.join( ",", idList );
        this.chatId = joinedString;
    }


    public ChatModel(BucketEventListModel model) {
        RealmList<String> idList = new RealmList<>();
        List<String> users = model.getInvitedUsers().stream().map(InviteFriendModel::getId).collect(Collectors.toList());
        idList.addAll(users);
        List<String> admins = model.getAdmins().stream().map(p -> p.getUserId()).collect(Collectors.toList());
        idList.addAll(admins);
        String idToAdd = SessionManager.shared.isPromoterSubAdmin()
                ? SessionManager.shared.getPromoterId()
                : SessionManager.shared.getUser().getId();

        if (!idList.contains(idToAdd)) {
            idList.add(idToAdd);
        }



        this.chatId = model.getId();
        this.chatType = "event";
        this.image = model.getImage();
        this.title = model.getTitle();
        this.members = idList;
        if (model.getOrg() != null){
            this.eventOrgName = model.getOrg().getName();
            this.eventCoverImage = model.getOrg().getCover();
        }
    }

    public ChatModel(BucketEventListModel model,String chatType) {
        RealmList<String> idList = new RealmList<>();
        List<String> users = model.getInvitedUsers().stream().map(InviteFriendModel::getId).collect(Collectors.toList());
        idList.addAll(users);
        List<String> admins = model.getAdmins().stream().map(p -> p.getUserId()).collect(Collectors.toList());
        idList.addAll(admins);
        String idToAdd = SessionManager.shared.isPromoterSubAdmin()
                ? SessionManager.shared.getPromoterId()
                : SessionManager.shared.getUser().getId();

        if (!idList.contains(idToAdd)) {
            idList.add(idToAdd);
        }
        this.chatId = model.getId();
        this.chatType = chatType;
        this.image = model.getImage();
        this.title = model.getTitle();
        this.isComplementry = model.isComplementry();
        this.isPromoter = model.isPromoter();
        this.members = idList;
        if (model.getAdmins() != null && !model.getAdmins().isEmpty()){
            this.eventOwnerIdForCm = model.getAdmins().get(0).getUserId();
        }
        if (model.getOrg() != null){
            this.eventOrgName = model.getOrg().getName();
            this.eventCoverImage = model.getOrg().getCover();
        }
    }

    public ChatModel(ChatMessageModel model,boolean isPromoter) {
        this.chatId = model.getId();
        this.chatType = model.getChatType();
        this.isComplementry = !isPromoter;
        this.isPromoter = isPromoter;
        this.members = model.getMembers();
        this.eventOrgName = model.getAuthorName();
        this.eventCoverImage = model.getAuthorImage();
        this.eventOwnerIdForCm = model.getAuthor();
    }


    public ChatModel(EventDetailModel model) {
        RealmList<String> idList = new RealmList<>();
        List<String> users = model.getEventModel().getInvitedGuests().stream().map(EventInviteGuestModel::getId).collect(Collectors.toList());
        idList.addAll(users);
        List<ContactListModel> adminList = model.getUsers().stream().filter(p -> model.getEventModel().getAdmins().contains(p.getId())).collect(Collectors.toList());
        List<String> admins = adminList.stream().map(p -> p.getUserId()).collect(Collectors.toList());
        idList.addAll(admins);
        String idToAdd = SessionManager.shared.isPromoterSubAdmin()
                ? SessionManager.shared.getPromoterId()
                : SessionManager.shared.getUser().getId();

        if (!idList.contains(idToAdd)) {
            idList.add(idToAdd);
        }

//        if (!idList.contains(SessionManager.shared.getUser().getId())) {
//            idList.add(SessionManager.shared.getUser().getId());
//        }

        this.chatId = model.getEventModel().getId();
        this.chatType = "event";
        this.image = model.getEventModel().getImage();
        this.title = model.getEventModel().getTitle();
        this.members = idList;
        if (model.getEventModel() != null && model.getEventModel().getOrgData() != null){
            this.eventOrgName = model.getEventModel().getOrgData().getName();
        }
    }


    public ChatModel(InviteFriendModel model) {
        List<String> userIds = model.getInvitedUser().stream().map(ContactListModel::getId).collect(Collectors.toList());
        RealmList<String> idList = new RealmList<>();
        idList.addAll(userIds);
        if (!idList.contains(model.getUserId())) {
            idList.add(model.getUserId());
        }

        String idToAdd = SessionManager.shared.isPromoterSubAdmin()
                ? SessionManager.shared.getPromoterId()
                : SessionManager.shared.getUser().getId();

        if (!idList.contains(idToAdd)) {
            idList.add(idToAdd);
        }

//        if (!idList.contains(SessionManager.shared.getUser().getId())) {
//            idList.add( SessionManager.shared.getUser().getId() );
//        }

        this.chatId = model.getId();
        this.chatType = "outing";
        if (model.getVenue() != null) {
            this.image = model.getVenue().getCover();
        }
        if (model.getUser() != null){
            this.outingCrateUserName = model.getUser().getFullName();
        }
        this.title = model.getTitle();
        this.members = idList;
    }

    public ChatModel(CreateBucketListModel model) {
        List<String> userIds = model.getSharedWith().stream().map(ContactListModel::getId).collect(Collectors.toList());
        RealmList<String> idList = new RealmList<>();
        idList.addAll(userIds);
        if (!idList.contains(model.getUserId())) {
            idList.add(model.getUserId());
        }

        String idToAdd = SessionManager.shared.isPromoterSubAdmin()
                ? SessionManager.shared.getPromoterId()
                : SessionManager.shared.getUser().getId();

        if (!idList.contains(idToAdd)) {
            idList.add(idToAdd);
        }
//
//        if (!idList.contains(SessionManager.shared.getUser().getId())) {
//            idList.add( SessionManager.shared.getUser().getId() );
//        }

        this.chatId = model.getId();
        this.chatType = "bucket";
        this.image = model.getCoverImage();
        this.title = model.getName();
        this.members = idList;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatType() { return chatType; }
    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public RealmList<String> getMembers() {
        return members;
    }

    public void setMembers(RealmList<String> members) {
        this.members = members;
    }

    public ChatMessageModel getLastMsg() {
        return ChatRepository.shared(Graphics.context).getLastMessages(chatId);
    }


    public long getUnrealMessageCount() {
        return ChatRepository.shared(Graphics.context).getUnrealMessageCount(chatId);
    }
    public long getAllUnrealMessageCount(@NonNull Realm realm) {
        return realm.where( ChatMessageModel.class )
                .notEqualTo("author", SessionManager.shared.getUser().getId())
                .not().contains("seenBy", SessionManager.shared.getUser().getId())
                .count();
    }

    public static ChatModel getChatById(@NonNull Realm realm, String id) {
        return realm.where( ChatModel.class ).equalTo( "chatId", id ).findFirst();
    }

    public static List<ChatModel> getChatList(@NonNull Realm realm) {
        RealmResults<ChatModel> results = realm.where( ChatModel.class ).findAll();
        return realm.copyFromRealm( results );
    }

    public UserDetailModel getUser() {
        String id;
        if (SessionManager.shared.getSubAdminUser() != null && SessionManager.shared.getSubAdminUser().getLoginType().equals("sub-admin")){
            id = SessionManager.shared.getSubAdminUser().getPromoterId();
        } else if (SessionManager.shared.isPromoterSubAdmin()) {
            id = SessionManager.shared.getPromoterId();
        } else {
            id = SessionManager.shared.getUser().getId();
        }
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        String finalId = id;
        Optional<String> userId = getMembers().stream().filter(u -> !u.equals(finalId)).findAny();
        if (userId.isPresent()) {
            return UserRepository.shared(Graphics.context).getUserById(userId.get());
        }
        return null;
    }

    public String getNotExistUserId() {
        if (SessionManager.shared.getUser() == null) { return null; }
        Optional<String> userId = getMembers().stream().filter(u -> !u.equals(SessionManager.shared.getUser().getId())).findAny();
        if (userId.isPresent()) {
            UserDetailModel user = UserRepository.shared(Graphics.context).getUserById(userId.get());
            if(user == null) {
                return userId.get();
            }
//            if (!user.getFullName().equals(getLastMsg().getAuthor()) || !user.getImage().equals(getLastMsg().getAuthorImage())) {
//                return userId.get();
//            }
        }
        return null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public String getEventOrgName() {
        return Utils.notNullString(eventOrgName);
    }

    public void setEventOrgName(String eventOrgName) {
        this.eventOrgName = eventOrgName;
    }

    public String getOutingCrateUserName() {
        return outingCrateUserName;
    }

    public void setOutingCrateUserName(String outingCrateUserName) {
        this.outingCrateUserName = outingCrateUserName;
    }

    public boolean isComplementry() {
        return isComplementry;
    }

    public void setComplementry(boolean complementry) {
        isComplementry = complementry;
    }

    public String getEventCoverImage() {
        return eventCoverImage;
    }

    public void setEventCoverImage(String eventCoverImage) {
        this.eventCoverImage = eventCoverImage;
    }

    public String getEventOwnerIdForCm() {
        return eventOwnerIdForCm;
    }

    public void setEventOwnerIdForCm(String eventOwnerIdForCm) {
        this.eventOwnerIdForCm = eventOwnerIdForCm;
    }

    public boolean isPromoter() {
        return isPromoter;
    }

    public void setPromoter(boolean promoter) {
        isPromoter = promoter;
    }

    @Override
    public int getIdentifier() {
        return chatId.hashCode();
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}

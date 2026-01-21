package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

public class ReportUseListModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("_id")
    @Expose
    private String _id = "" ;

    @SerializedName("title")
    @Expose
    private String title = "";

    @SerializedName("reporterId")
    @Expose
    private String reporterId = "";

    @SerializedName("userId")
    @Expose
    private String userId = "";

    @SerializedName("reason")
    @Expose
    private String reason = "";

    @SerializedName("type")
    @Expose
    private String type = "";

    @SerializedName("typeId")
    @Expose
    private String typeId = "";

    @SerializedName("message")
    @Expose
    private String message = "";

    @SerializedName("status")
    @Expose
    private String status = "";

    @SerializedName("action")
    @Expose
    private String action = "";

    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";

    @SerializedName("chat")
    @Expose
    private ChatMessageModel chatMessageModel;

    @SerializedName("review")
    @Expose
    private CurrentUserRatingModel reviewModel;

    @SerializedName("reporUser")
    @Expose
    private UserDetailModel reportUserModel;

    @SerializedName("user")
    @Expose
    private UserDetailModel userDetailModel;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return Utils.notNullString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReporterId() {
        return Utils.notNullString(reporterId);
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getUserId() {
        return Utils.notNullString(userId);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReason() {
        return Utils.notNullString(reason);
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getType() {
        return Utils.notNullString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeId() {
        return Utils.notNullString(typeId);
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getMessage() {
        return Utils.notNullString(message);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return Utils.notNullString(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return Utils.notNullString(action);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCreatedAt() {
        return Utils.notNullString(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ChatMessageModel getChatMessageModel() {
        return chatMessageModel;
    }

    public void setChatMessageModel(ChatMessageModel chatMessageModel) {
        this.chatMessageModel = chatMessageModel;
    }

    public CurrentUserRatingModel getReviewModel() {
        return reviewModel;
    }

    public void setReviewModel(CurrentUserRatingModel reviewModel) {
        this.reviewModel = reviewModel;
    }

    public UserDetailModel getReportUserModel() {
        return reportUserModel;
    }

    public void setReportUserModel(UserDetailModel reportUserModel) {
        this.reportUserModel = reportUserModel;
    }

    public UserDetailModel getUserDetailModel() {
        return userDetailModel;
    }

    public void setUserDetailModel(UserDetailModel userDetailModel) {
        this.userDetailModel = userDetailModel;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}

package com.whosin.app.service.models;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.manager.SessionManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserDetailModel extends RealmObject implements DiffIdentifier, ModelProtocol {

    @PrimaryKey
    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("userId")
    @Expose
    private String userId = "";
    @SerializedName("phone")
    @Expose
    private String phone = "";
    @SerializedName("country_code")
    @Expose
    private String countryCode = "";
    @SerializedName("first_name")
    @Expose
    private String firstName = "";
    @SerializedName("last_name")
    @Expose
    private String lastName = "";
    @SerializedName("gender")
    @Expose
    private String gender = "";
    @SerializedName("image")
    @Expose
    private String image = "";
    @SerializedName("platform")
    @Expose
    private String platform = "";
    @SerializedName("email")
    @Expose
    private String email = "";
    @SerializedName("social_platform")
    @Expose
    private String socialPlatform = "";
    @SerializedName("social_id")
    @Expose
    private String socialId = "";
    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("__v")
    @Expose
    private String __v = "";
    @SerializedName("itemId")
    @Expose
    private String itemId = "";
    @SerializedName("dateOfBirth")
    @Expose
    private String dateOfBirth = "";

    @SerializedName("nationality")
    @Expose
    private String nationality = "";

    @SerializedName("isPhoneVerified")
    @Expose
    private int isPhoneVerified = 0;
    @SerializedName("isEmailVerified")
    @Expose
    private int isEmailVerified;

    @SerializedName("follower")
    @Expose
    private int follower;
    @SerializedName("referralCode")
    @Expose
    private String referralCode;

    @SerializedName("following")
    private int following;

    @SerializedName("isSynced")
    @Expose
    private boolean isSynced = true;

    @SerializedName("isVip")
    @Expose
    private boolean isVip = false;
    @SerializedName("isMembershipActive")
    @Expose
    private boolean isMembershipActive = false;

    @SerializedName("follow")
    @Expose
    private String follow = "";

    @SerializedName("bio")
    @Expose
    private String bio = "";
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";
    @SerializedName("inviteStatus")
    @Expose
    private String inviteStatus = "";
    @SerializedName("promoterStatus")
    @Expose
    private String promoterStatus = "";
    @SerializedName("mutualFriends")
    @Expose
    private RealmList<ContactListModel> mutualFriends;
    @SerializedName("replies")
    @Expose
    private RealmList<ContactChatRepliesModel> replies;
    @SerializedName("isSignUp")
    @Expose
    private boolean isSignUp = true;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("subject")
    @Expose
    private String subject = "";
    @SerializedName("isProfilePrivate")
    @Expose
    private boolean isProfilePrivate = false;
    @SerializedName("isTwoFactorActive")
    @Expose
    private boolean isTwoFactorActive = false;

    @SerializedName("isGuest")
    @Expose
    private boolean isGuest = false;

    @SerializedName("facebook")
    @Expose
    private String facebook = "";
    @SerializedName("instagram")
    @Expose
    private String instagram = "";
    @SerializedName("youtube")
    @Expose
    private String youtube = "";
    @SerializedName("tiktok")
    @Expose
    private String tiktok= "";

    @SerializedName("address")
    @Expose
    private String address ="";

    @SerializedName("ringPromoterStatus")
    @Expose
    private String ringPromoterStatus ="";

    @SerializedName("accountStatus")
    @Expose
    private String accountStatus ="";

    @Expose
    private boolean isAlwaysAvailable = false;
    @SerializedName("circles")
    @Expose
    private RealmList<InvitedUserModel> circles;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("name")
    @Expose
    private String name = "";

    @SerializedName("myRingStatus")
    @Expose
    private String myRingStatus = "";

    private boolean isRingUserSelect = false;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("message")
    @Expose
    private String message = "";

    @SerializedName("isRequestPending")
    @Expose
    private boolean isRequestPending = false;

    @SerializedName("isPromoter")
    @Expose
    private boolean isPromoter = false;

    @SerializedName("isRingMember")
    @Expose
    private boolean isRingMember = false;

    @SerializedName("avatar")
    @Expose
    private String avatar = "";

    @SerializedName("images")
    @Expose
    private RealmList<String> images;
    @SerializedName("ringMember")
    @Expose
    private String ringMember = "";

    @SerializedName("banStatus")
    @Expose
    private String banStatus = "";

    @SerializedName("promoterId")
    @Expose
    private String promoterId = "";

    @SerializedName("loginType")
    @Expose
    private String loginType = "";

    @SerializedName("isManagePromoter")
    @Expose
    private boolean isManagePromoter = false;

    @SerializedName("status")
    @Expose
    private String status = "";

    @SerializedName("plusOneStatus")
    @Expose
    private String plusOneStatus = "";

    @SerializedName("plusOneId")
    @Expose
    private String plusOneId = "";

    @SerializedName("adminStatusOnPlusOne")
    @Expose
    private String adminStatusOnPlusOne = "";

    @SerializedName("currency")
    @Expose
    private String currency = "";

    @SerializedName("lang")
    @Expose
    private String lang = "";

    @SerializedName("roleAcess")
    @Expose
    private RealmList<RoleAcessModel> roleAcessModelList ;

    public boolean isGuest() {
        return isGuest;
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public String getId() {return Utils.notNullString(id);}

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return Utils.notNullString(phone);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreatedAt() {
        return Utils.notNullString(createdAt);

    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getInviteStatus() {
        return Utils.notNullString(inviteStatus);
    }

    public void setInviteStatus(String inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public String getCountryCode() {
        return Utils.notNullString(countryCode);
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFirstName() {
        return Utils.notNullString(firstName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return Utils.notNullString(lastName);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return Utils.notNullString(gender);
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return Utils.notNullString(image);
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }
    public String getPlatform() {
        return Utils.notNullString(platform);
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getEmail() {
        return Utils.notNullString(email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSocialPlatform() {
        return socialPlatform;
    }

    public void setSocialPlatform(String socialPlatform) {
        this.socialPlatform = socialPlatform;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getType() {
        return Utils.notNullString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateOfBirth() {
        return Utils.notNullString(dateOfBirth);
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNationality() {
        return Utils.notNullString(nationality);
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getFullName() {
        return getFirstName().trim() + " " + getLastName().trim();
    }


    public int getIsPhoneVerified() {
        return isPhoneVerified;
    }

    public void setIsPhoneVerified(int isPhoneVerified) {
        this.isPhoneVerified = isPhoneVerified;
    }

    public int getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(int isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public int getFollower() {
        return follower;
    }

    public void setFollower(int follower) {
        this.follower = follower;
    }


    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    @Override
    public int getIdentifier() {
        return id.hashCode();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public boolean isMembershipActive() {
        return isMembershipActive;
    }

    public void setMembershipActive(boolean membershipActive) {
        isMembershipActive = membershipActive;
    }

    public String getFollow() {
        return Utils.notNullString(follow);
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getBio() {
        return Utils.notNullString(bio);
    }

    public RealmList<ContactListModel> getMutualFriends() {
        if (mutualFriends == null) {
            return new RealmList<>();
        } else {
            return mutualFriends;
        }
    }

    public void setMutualFriends(RealmList<ContactListModel> mutualFriends) {
        this.mutualFriends = mutualFriends;
    }

    public RealmList<ContactChatRepliesModel> getReplies() {
        if (replies == null) {
            return new RealmList<>();
        } else {
            return replies;
        }
    }

    public void setReplies(RealmList<ContactChatRepliesModel> replies) {
        this.replies = replies;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public static UserDetailModel getUserById(@NonNull Realm realm, String id) {
        UserDetailModel result = realm.where(UserDetailModel.class).equalTo("id", id).findFirst();
        if (result != null) {
            return realm.copyFromRealm(result);
        }
        return null;
    }


    public boolean getSignUp() {
        return isSignUp;
    }

    public void setSignUp(boolean signUp) {
        isSignUp = signUp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isProfilePrivate() {
        return isProfilePrivate;
    }

    public void setProfilePrivate(boolean profilePrivate) {
        isProfilePrivate = profilePrivate;
    }


    public boolean isRequestPending() {
        return isRequestPending;
    }

    public void setRequestPending(boolean requestPending) {
        isRequestPending = requestPending;
    }

    public boolean isTwoFactorActive() {
        return isTwoFactorActive;
    }

    public void setTwoFactorActive(boolean twoFactorActive) {
        isTwoFactorActive = twoFactorActive;
    }

    public boolean isPromoter() {
        return SessionManager.shared.isPromoterSubAdmin() || isPromoter;
    }


    public void setPromoter(boolean promoter) {
        isPromoter = promoter;
    }

    public boolean isRingMember() {
        return isRingMember;
    }

    public void setRingMember(boolean ringMember) {
        isRingMember = ringMember;
    }

    public String getPlusOneStatus() {
        return plusOneStatus;
    }

    public boolean showPlusOneStatus() {
        if (isSynced){
            if (plusOneStatus.equalsIgnoreCase("none") || plusOneStatus.equalsIgnoreCase("accepted")) {
                return false;
            } else {
                return true;
            }
        }else {
            return false;
        }

    }

    public String getPlusOneId() {
        return plusOneId;
    }

    public void setPlusOneId(String plusOneId) {
        this.plusOneId = userId;
    }

    public void setPlusOneStatus(String plusOneStatus) {
        this.plusOneStatus = plusOneStatus;
    }

    public String getAvatar() {
        return Utils.notNullString(avatar);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isRingUserSelect() {
        return isRingUserSelect;
    }

    public void setRingUserSelect(boolean ringUserSelect) {
        isRingUserSelect = ringUserSelect;
    }

    public List<String> getImages() {
        if (images == null) {
            return new RealmList<>();
        }
        return images;
    }

    public boolean isSignUp() {
        return isSignUp;
    }

    public String getFacebook() {
        return Utils.notNullString(facebook);
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return Utils.notNullString(instagram);
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getYoutube() {
        return Utils.notNullString(youtube);
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getTiktok() {
        return Utils.notNullString(tiktok);
    }

    public void setTiktok(String tiktok) {
        this.tiktok = tiktok;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public boolean isAlwaysAvailable() {
        return isAlwaysAvailable;
    }

    public void setAlwaysAvailable(boolean alwaysAvailable) {
        isAlwaysAvailable = alwaysAvailable;
    }

    public void setImages(RealmList<String> images) {
        this.images = images;
    }

    public String getRingMember() {
        return ringMember;
    }

    public void setRingMember(String ringMember) {
        this.ringMember = ringMember;
    }

    public String getPromoterStatus() {
        return promoterStatus;
    }

    public void setPromoterStatus(String promoterStatus) {
        this.promoterStatus = promoterStatus;
    }

    public String getBanStatus() {
        return Utils.notNullString(banStatus);
    }

    public void setBanStatus(String banStatus) {
        this.banStatus = banStatus;
    }

    public String getMyRingStatus() {
        return myRingStatus;
    }

    public void setMyRingStatus(String myRingStatus) {
        this.myRingStatus = myRingStatus;
    }

    public String getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(String promoterId) {
        this.promoterId = promoterId;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public boolean isManagePromoter() {
        return isManagePromoter;
    }

    public void setManagePromoter(boolean managePromoter) {
        isManagePromoter = managePromoter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RealmList<InvitedUserModel> getCircles() {
        if (circles == null) {
            return new RealmList<>();
        } else {
            return circles;
        }
    }public void setCircles(RealmList<InvitedUserModel> circles) {
        this.circles = circles;
    }

    public String getRingPromoterStatus() {
        return ringPromoterStatus;
    }

    public void setRingPromoterStatus(String ringPromoterStatus) {
        this.ringPromoterStatus = ringPromoterStatus;
    }

    public String getAdminStatusOnPlusOne() {
        return adminStatusOnPlusOne;
    }

    public void setAdminStatusOnPlusOne(String adminStatusOnPlusOne) {
        this.adminStatusOnPlusOne = adminStatusOnPlusOne;
    }

    public RealmList<RoleAcessModel> getRoleAcessModelList() {
        if (roleAcessModelList == null || roleAcessModelList.isEmpty()) {
            return new RealmList<>();
        }
        return roleAcessModelList;
    }


    public void setRoleAcessModelList(RealmList<RoleAcessModel> roleAcessModelList) {
        this.roleAcessModelList = roleAcessModelList;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getCurrency() {
        return Utils.notNullString(currency);
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLang() {
        if (TextUtils.isEmpty(lang)) {
            return "en";
        }else {
            return lang;
        }
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}

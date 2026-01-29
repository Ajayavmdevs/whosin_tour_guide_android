package com.whosin.business.service.models;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.R;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class ContactListModel extends RealmObject implements DiffIdentifier,ModelProtocol {

    @PrimaryKey
    @SerializedName("_id")
    @Expose
    private String id = "";
    @SerializedName("first_name")
    @Expose
    private String firstName = "";
    @SerializedName("last_name")
    @Expose
    private String lastName = "";
    @SerializedName("phone")
    @Expose
    private String phone = "";
    @SerializedName("country_code")
    @Expose
    private String countryCode = "";
    @SerializedName("email")
    @Expose
    private String email = "";
    @SerializedName("image")
    @Expose
    private String image = "";
    @SerializedName("userId")
    @Expose
    private String userId = "";
    @SerializedName("inviteStatus")
    @Expose
    private String inviteStatus = "";
    @SerializedName("createdAt")
    @Expose
    private String createdAt = "";
    @SerializedName("inviteId")
    @Expose
    private String inviteId = "";
    @SerializedName("plusOneStatus")
    @Expose
    private String plusOneStatus = "";
    @SerializedName("adminStatusOnPlusOne")
    @Expose
    private String adminStatusOnPlusOne = "";
    @SerializedName("isSynced")
    @Expose
    private boolean isSynced = true;
    @SerializedName("isVip")
    @Expose
    private boolean isVip = true;

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    @SerializedName("follow")
    @Expose
    private String follow = "";
    private String nameOnContactBook = "";

    @SerializedName("isPromoter")
    @Expose
    private boolean isPromoter = false;

    @SerializedName("isRingMember")
    @Expose
    private boolean isRingMember = false;

    private boolean isUserSelect = false;


    public ContactListModel() {}
    public ContactListModel(String id,String name, String email, String  phone) {
        this.id = id;
        String[] names = name.split(" ");
        this.firstName = names.length > 0 ? names[0] : "";
        this.lastName = names.length > 1 ? names[1] : "";
        this.email = email;
        this.phone = phone;
        this.image = "";
        this.isSynced = false;
    }


    public ContactListModel(String id,String firstName) {
        this.id = id;
        this.firstName = firstName;
    }
    public ContactListModel(String id,String firstName, boolean isSynced) {
        this.id = id;
        this.firstName = firstName;
        this.isSynced = isSynced;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    private boolean selected = false;

    private boolean isDisable = false;

    public boolean isDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        isDisable = disable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return getFirstName()+" "+getLastName();
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static List<ContactListModel> getSyncedContacts(@NonNull Realm realm) {
        RealmResults<ContactListModel> results = realm.where(ContactListModel.class).equalTo("isSynced", true).findAll();
        return realm.copyFromRealm(results);
    }

    public static List<ContactListModel> getContacts(@NonNull Realm realm) {
        RealmResults<ContactListModel> results = realm.where( ContactListModel.class ).equalTo("isSynced", false).findAll();
        return realm.copyFromRealm(results);
    }

    public static List<ContactListModel> searchContacts(@NonNull Realm realm, String query, boolean isSynced) {

        RealmQuery<ContactListModel> userQuery = realm.where(ContactListModel.class).equalTo("isSynced", isSynced).findAll().where();
        if (TextUtils.isEmpty(query)) {
            return realm.copyFromRealm(userQuery.findAll());
        }
        String[] params = query.split(" ");
        if (query.split(" ").length == 1) {
            userQuery.beginGroup()
            .contains("firstName", query.trim(), Case.INSENSITIVE).or()
            .contains("lastName", query.trim(), Case.INSENSITIVE).or()
            .contains("phone", query.trim(),Case.INSENSITIVE)
            .endGroup();
        } else {
            userQuery.beginGroup()
                    .contains("firstName", params[0], Case.INSENSITIVE).and()
                    .contains("lastName", params[1], Case.INSENSITIVE)
                    .endGroup()
                    .or()
                    .contains("phone", query,Case.INSENSITIVE);
        }
        return realm.copyFromRealm(userQuery.findAll());
    }
    public String getNameOnContactBook() {
        return nameOnContactBook;
    }

    public void setNameOnContactBook(String nameOnContactBook) {
        this.nameOnContactBook = nameOnContactBook;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInviteStatus() {
        return inviteStatus;
    }

    public void setInviteStatus(String inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public boolean isPromoter() {
        return isPromoter;
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

    public void setPlusOneStatus(String plusOneStatus) {
        this.plusOneStatus = plusOneStatus;
    }

    public int getStatusColor() {
        if (getInviteStatus().equals("in")) {
            return R.color.in_green;
        } else if (getInviteStatus().equals("pending")) {
            return R.color.pending_yellow;
        }
        else {
            return R.color.out_red;
        }
    }
    public int getStatusBorder() {
        if (getInviteStatus().equals("in")) {
            return R.drawable.stroke_gradiant_line_in;
        } else if (getInviteStatus().equals("pending")) {
            return R.drawable.stroke_gradiant_line_pending;
        } else {
            return R.drawable.stroke_gradiant_line_out;
        }
    }

    public boolean showPlusOneStatus() {
        if (isSynced){
            if (!TextUtils.isEmpty(adminStatusOnPlusOne) && !TextUtils.isEmpty(plusOneStatus) && plusOneStatus.equals("accepted") && adminStatusOnPlusOne.equals("pending")){
                return true;
            } else if (!TextUtils.isEmpty(adminStatusOnPlusOne) && adminStatusOnPlusOne.equals("pending")) {
                return true;
            } else if (plusOneStatus.equalsIgnoreCase("none") || plusOneStatus.equalsIgnoreCase("accepted")) {
                return false;
            } else {
                return true;
            }
        }else {
            return false;
        }

    }

    public String getAdminStatusOnPlusOne() {
        return adminStatusOnPlusOne;
    }

    public void setAdminStatusOnPlusOne(String adminStatusOnPlusOne) {
        this.adminStatusOnPlusOne = adminStatusOnPlusOne;
    }

    public boolean isUserSelect() {
        return isUserSelect;
    }

    public void setUserSelect(boolean userSelect) {
        isUserSelect = userSelect;
    }
}

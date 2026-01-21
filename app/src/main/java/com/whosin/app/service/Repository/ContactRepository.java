package com.whosin.app.service.Repository;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.rest.RestCallback;

import io.realm.RealmResults;

public class ContactRepository extends RealmRepository {
    private Context context;

    private static volatile ContactRepository _instance = null;

    public static ContactRepository shared(Context ctx) {
        if (_instance == null) {
            synchronized (ContactRepository.class) {
                _instance = new ContactRepository();
            }
        }
        _instance.context = ctx;
        return _instance;
    }

    public List<ContactListModel> getSyncedContacts() {
        return ContactListModel.getSyncedContacts(getRealm());
    }

    public List<ContactListModel> getContacts() {
        return ContactListModel.getContacts(getRealm());
    }

    public List<ContactListModel> getAllContacts() {
        List<ContactListModel> finalList = new ArrayList<>();
        if (ContactListModel.getSyncedContacts(getRealm()) != null && !ContactListModel.getSyncedContacts(getRealm()).isEmpty()){
            finalList.add(new ContactListModel("-1","Friends on WHOS’IN"));
            finalList.addAll(ContactListModel.getSyncedContacts(getRealm()));
        }
        if (ContactListModel.getContacts(getRealm()) != null && !ContactListModel.getContacts(getRealm()).isEmpty()){
            finalList.add(new ContactListModel("-1","Invite your friends", false));
            finalList.addAll(ContactListModel.getContacts(getRealm()));
        }
        return finalList;
    }

    public List<ContactListModel> getAllInviteFriendContacts() {
        List<ContactListModel> finalList = new ArrayList<>();
        if (ContactListModel.getContacts(getRealm()) != null && !ContactListModel.getContacts(getRealm()).isEmpty()){
            finalList.add(new ContactListModel("-1","Invite your friends", false));
            finalList.addAll(ContactListModel.getContacts(getRealm()));
        }
        return finalList;
    }

    public List<ContactListModel> searchContacts(String query) {
        List<ContactListModel> finalList = new ArrayList<>();
        if (ContactListModel.searchContacts(getRealm(), query, true) != null && !ContactListModel.searchContacts(getRealm(), query, true).isEmpty()){
            finalList.add(new ContactListModel("-1","Friends on WHOS’IN"));
            finalList.addAll(ContactListModel.searchContacts(getRealm(), query, true));
        }
        if (ContactListModel.searchContacts(getRealm(), query, false) != null && !ContactListModel.searchContacts(getRealm(), query, false).isEmpty()){
            finalList.add(new ContactListModel("-1","Invite your friends"));
            finalList.addAll(ContactListModel.searchContacts(getRealm(), query, false));
        }
        return finalList;
    }

    public void syncContacts(List<ContactListModel> contactList, CommanCallback<Boolean> delegate) {

        List<String> phoneNumbers = new ArrayList<>();
        contactList.forEach(p -> {
            if (!TextUtils.isEmpty(p.getPhone())  && !phoneNumbers.contains(p.getPhone())) {
                phoneNumbers.add(p.getPhone());
            }
        });
        List<String> emails = contactList.stream().map(ContactListModel::getEmail).filter(p-> !TextUtils.isEmpty(p)).collect(Collectors.toList());

        DataService.shared( context).requestGetContact( phoneNumbers, emails, new RestCallback<ContainerListModel<ContactListModel>>() {
            @Override
            public void result(ContainerListModel<ContactListModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    delegate.onReceive(false);
                    return;
                }
                if (model.data != null) {
                    List<ContactListModel> syncedContacts = model.data;
                    syncedContacts.removeIf(p -> TextUtils.isEmpty(p.getFirstName()) && TextUtils.isEmpty(p.getLastName()));
                    syncedContacts.forEach(p-> {
                        p.setSynced(true);
                        Optional<ContactListModel> contact = contactList.stream().filter( o ->
                                o.getPhone().contains(p.getPhone()) || p.getPhone().contains(o.getPhone())).findFirst();
                        contact.ifPresent(contactListModel -> {
                            p.setNameOnContactBook(contactListModel.getFirstName());
                            contactList.removeIf( q -> p.getPhone().contains(q.getPhone()) || q.getPhone().contains(p.getPhone()));
                        });
                    });

                    syncedContacts.addAll(contactList);
                    getRealm().executeTransactionAsync(bgRealm -> {
                        RealmResults<ContactListModel> realmResults = bgRealm.where(ContactListModel.class).findAll();
                        realmResults.forEach(c -> c.deleteFromRealm());
                        bgRealm.insertOrUpdate(syncedContacts);
                    }, () -> {
                        getRealm().refresh();
                        delegate.onReceive(true);
                    });

                } else {
                    delegate.onReceive(false);
                }
            }
        } );

    }

    public void updateUserFollowStatus(ContactListModel model) {
        getRealm().executeTransactionAsync(bgRealm -> {
            bgRealm.insertOrUpdate(model);
        }, () -> {
            getRealm().refresh();
        });
    }

    public void updateUserFollowStatus(String userId, String status) {
        getRealm().executeTransactionAsync(bgRealm -> {
            ContactListModel model = bgRealm.where(ContactListModel.class).equalTo("id", userId).findFirst();
            if (model != null) {
                model.setFollow(status);
                bgRealm.insertOrUpdate(model);
            }
        }, () -> {
            getRealm().refresh();
        });
    }

}

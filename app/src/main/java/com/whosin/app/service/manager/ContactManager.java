package com.whosin.app.service.manager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.service.Repository.ContactRepository;
import com.whosin.app.service.models.ContactListModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactManager {
    @NonNull
    public static ContactManager shared = ContactManager.getInstance();

    @Nullable
    private static volatile ContactManager instance = null;
    private boolean permissionInProgress = false;


    public Activity context;
    private List<ContactListModel> contactList = new ArrayList<>();

    // --------------------------------------
    // region Singleton
    // --------------------------------------

    private ContactManager() {
    }

    private static synchronized ContactManager getInstance() {
        if (instance == null) {
            instance = new ContactManager();
        }
        return instance;
    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    public void requestContact(Activity context) {
        this.context = context;
        requestPermission(false, null);
    }

    public void requestPermission(boolean showAlert, CommanCallback<Boolean> callback) {
        if (permissionInProgress) {
            return; // Permission request already in progress
        }

        String[] permissions = {Manifest.permission.READ_CONTACTS};
        String rationale = "Please provide read contact permission for find friends";
        if (context == null) {
            return;
        }
        permissionInProgress = true; // Mark permission request in progress
//        Permissions.check(context, permissions, rationale, null, new PermissionHandler() {
//            @Override
//            public void onGranted() {
//                getContact(callback);
//                permissionInProgress = false; // Reset flag after permission granted
//            }
//
//            @Override
//            public boolean onBlocked(Context context, ArrayList<String> blockedList) {
//                permissionInProgress = false; // Reset flag
//                if (showAlert) {
//                    Graphics.alertDialogYesNoBtnWithUIFlag(context, "Permissions Required", "Contact Permission have been set \"not to ask again!\" Please provide them from settings.", false, "Cancel", "Go to setting", aBoolean -> {
//                        if (aBoolean) {
//                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            intent.setData(Uri.fromParts("package", "com.whosin.me", null));
//                            context.startActivity(intent);
//                        } else {
//                            // Dismiss the dialog when cancel button is clicked
//                            permissionInProgress = false; // Reset flag
//                        }
//                    });
//                }
//                return super.onBlocked(context, blockedList);
//            }
//
//            @Override
//            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
//                super.onDenied(context, deniedPermissions);
//                permissionInProgress = false; // Reset flag
//                // Permission denied, do not fetch contacts
//                if (callback != null) {
//                    callback.onReceive(false);
//                }
//            }
//        });
    }

    private void getContact(CommanCallback<Boolean> callback) {
        contactList.clear();
        new FetchContacts(context, contacts -> {
            contactList = contacts;
            syncContacts( callback);
        }).execute();
    }

    public void syncContacts(CommanCallback<Boolean> delegate) {
        if (contactList == null || contactList.isEmpty()) {
            if (delegate != null) {
                delegate.onReceive(false);
            }
            return;
        }
        if (TextUtils.isEmpty(SessionManager.shared.getToken())) {
            return;
        }
        ContactRepository.shared(context).syncContacts( contactList, data -> {
            if (delegate != null) {
                delegate.onReceive(data);
            }
            EventBus.getDefault().post(new ContactListModel());
        });
    }

    public interface OnContactFetchListener {
        void onContactFetch(List<ContactListModel>  list);
    }

    public static class FetchContacts extends AsyncTask<Void, Void, List<ContactListModel>> {

        private final Context activity;
        private OnContactFetchListener listener;

        public FetchContacts(Context context, OnContactFetchListener listener) {
            activity = context;
            this.listener = listener;
        }
        @Override
        protected List<ContactListModel> doInBackground(Void... params) {
            List<ContactListModel> contactList = new ArrayList<>();
            try {
                ContentResolver cr = activity.getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                if ((cur != null ? cur.getCount() : 0) > 0) {
                    while (cur.moveToNext()) {
                        if (cur.getInt(cur.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                            String id = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                            String name = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                            while (pCur.moveToNext()) {
                                String phoneNo = pCur.getString(pCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                if (!TextUtils.isEmpty(phoneNo)) {
                                    String phoneTemp = phoneNo.replaceAll("[^0-9]","");
                                    if (contactList.stream().noneMatch(p -> Objects.equals(p.getPhone(), phoneTemp))) {
                                        contactList.add(new ContactListModel(id, name, "", phoneTemp));
                                    }
                                }
                            }
                            pCur.close();
                        }
                    }
                }
                if (cur != null) {
                    cur.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return contactList;
        }

        @Override
        protected void onPostExecute(List<ContactListModel> list) {
            super.onPostExecute(list);
            if(listener!=null){
                listener.onContactFetch(list);
            }
        }
    }

}

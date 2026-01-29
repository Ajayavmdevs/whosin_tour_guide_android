package com.whosin.business.service.Repository;

import android.content.Context;

import androidx.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.annotations.RealmModule;

public class RealmRepository {
    protected Context mContext;
    private Realm realm;

    @NonNull
    public Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    public static void configRealm(@NonNull Context context) {
        // Config Realm
        Realm.init(context);
//        Realm.removeDefaultConfiguration();
        RealmConfiguration config = new RealmConfiguration.Builder()
                .modules(new LibraryModule(), Realm.getDefaultModule())
                .schemaVersion(100)
                .migration((realm, oldVersion, newVersion) -> {
                })
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .build();

        Realm.setDefaultConfiguration(config);
    }
}

@RealmModule(library = true, allClasses = true)
class LibraryModule {
}

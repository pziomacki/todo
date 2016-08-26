package com.ziomacki.todo.component;

import javax.inject.Inject;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmWrapper {

    @Inject
    RealmConfiguration realmConfiguration;

    @Inject
    RealmWrapper() {}

    public Realm getRealmInstance() {
        return Realm.getInstance(realmConfiguration);
    }
}

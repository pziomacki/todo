package com.ziomacki.todo.taskslist.model;

import com.ziomacki.todo.component.RealmWrapper;
import com.ziomacki.todo.taskdetails.model.Task;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;

public class TaskListRepository {

    private RealmWrapper realmWrapper;

    @Inject
    TaskListRepository(RealmWrapper realmWrapper) {
        this.realmWrapper = realmWrapper;
    }

    public void updateTaskList(List<Task> tasks) {
        Realm realm = realmWrapper.getRealmInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(tasks);
        realm.commitTransaction();
        realm.close();
    }

    public Observable<List<Task>> getManagedTaskList(final boolean onlyModified) {
        final Realm realm = realmWrapper.getRealmInstance();
        RealmQuery<Task> query = realm.where(Task.class);
        if (onlyModified) {
            query = query.equalTo(Task.KEY_MODIFIED, true);
        }
        Observable<List<Task>> taskObservable = query.findAllAsync().asObservable()
                .map(tasks -> realm.copyFromRealm(tasks))
                .doOnUnsubscribe(() -> realm.close());
        return taskObservable;
    }

    public Observable<List<Task>> getUnmanagedModifiedTaskList() {
        return Observable.create(new Observable.OnSubscribe<List<Task>>() {
            @Override
            public void call(Subscriber<? super List<Task>> subscriber) {
                Realm realm = realmWrapper.getRealmInstance();
                List<Task> result = new ArrayList<>();
                RealmResults<Task> tasks = realm.where(Task.class).equalTo(Task.KEY_MODIFIED, true).findAll();
                result.addAll(realm.copyFromRealm(tasks));
                realm.close();
                subscriber.onNext(result);
            }
        });
    }
}

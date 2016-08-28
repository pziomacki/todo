package com.ziomacki.todo.task.model;

import com.ziomacki.todo.component.RealmWrapper;
import javax.inject.Inject;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class TodoRepository {

    private RealmWrapper realmWrapper;

    @Inject
    TodoRepository(RealmWrapper realmWrapper) {
        this.realmWrapper = realmWrapper;
    }

    public void updateTaskContainer(TaskContainer taskContainer) {
        Realm realm = realmWrapper.getRealmInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(taskContainer.taskRealmList);
        realm.commitTransaction();
        RealmResults<Task> realmResults = realm.where(Task.class).findAll();
        RealmList<Task> realmTaskList = new RealmList<>();
        realmTaskList.addAll(realmResults);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(realmTaskList);
        TaskContainer realmTaskContainer = realm.where(TaskContainer.class).findFirst();
        if (realmTaskContainer != null) {
            realmTaskContainer.totalCount = taskContainer.totalCount;
            realmTaskContainer.taskRealmList = realmTaskList;
        }
        realm.commitTransaction();
        realm.close();
    }

    public Observable<RealmResults<Task>> getTasks(final boolean onlyModified) {
        final Realm realm = realmWrapper.getRealmInstance();
        return Observable.create(new Observable.OnSubscribe<RealmResults<Task>>() {
            @Override
            public void call(Subscriber<? super RealmResults<Task>> subscriber) {
                RealmResults<Task> results;
                RealmQuery<Task> query = realm.where(Task.class);
                if (onlyModified) {
                    results = query.equalTo(Task.KEY_MODIFIED, true).findAll();
                } else {
                    results = query.findAll();
                }
                subscriber.onNext(results);
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                realm.close();
            }
        });
    }

    public Observable<Task> getTask(final int taskId) {
        return Observable.create(new Observable.OnSubscribe<Task>() {
            @Override
            public void call(Subscriber<? super Task> subscriber) {
                Realm realm = realmWrapper.getRealmInstance();
                Task taskManaged = realm.where(Task.class).equalTo(Task.KEY_ID, taskId).findFirst();
                Task taskUnmanaged;
                if (taskManaged != null) {
                    taskUnmanaged = realm.copyFromRealm(taskManaged);
                } else {
                    taskUnmanaged = new Task();
                }
                realm.close();
                subscriber.onNext(taskUnmanaged);
            }
        });
    }

}

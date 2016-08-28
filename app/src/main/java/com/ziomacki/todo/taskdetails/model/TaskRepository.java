package com.ziomacki.todo.taskdetails.model;

import com.ziomacki.todo.component.RealmWrapper;
import javax.inject.Inject;
import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;

public class TaskRepository {

    private RealmWrapper realmWrapper;

    @Inject
    TaskRepository(RealmWrapper realmWrapper) {
        this.realmWrapper = realmWrapper;
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

    public Observable<Task> saveTask(final Task task) {
        return Observable.create(new Observable.OnSubscribe<Task>() {
            @Override
            public void call(Subscriber<? super Task> subscriber) {
                Realm realm = realmWrapper.getRealmInstance();
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(task);
                realm.commitTransaction();
                realm.close();
                subscriber.onNext(task);
            }
        });
    }
}

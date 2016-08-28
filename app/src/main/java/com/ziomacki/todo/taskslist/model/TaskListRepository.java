package com.ziomacki.todo.taskslist.model;

import com.ziomacki.todo.component.RealmWrapper;
import com.ziomacki.todo.taskdetails.model.Task;
import javax.inject.Inject;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class TaskListRepository {

    private RealmWrapper realmWrapper;

    @Inject
    TaskListRepository(RealmWrapper realmWrapper) {
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


}

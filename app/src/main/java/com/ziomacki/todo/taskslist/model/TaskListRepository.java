package com.ziomacki.todo.taskslist.model;

import com.ziomacki.todo.component.RealmWrapper;
import com.ziomacki.todo.taskdetails.model.Task;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import io.realm.Realm;
import io.realm.RealmList;
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

    public void updateTaskList(List<Task> tasks) {
        Realm realm = realmWrapper.getRealmInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(tasks);
        realm.commitTransaction();
        realm.close();
    }

    public Observable<RealmResults<Task>> getTasks(final boolean onlyModified) {
        final Realm realm = realmWrapper.getRealmInstance();
        RealmQuery<Task> query = realm.where(Task.class);
        if (onlyModified) {
            query = query.equalTo(Task.KEY_MODIFIED, true);
        }
        Observable<RealmResults<Task>> taskObservable = query.findAllAsync().asObservable()
                .doOnUnsubscribe(() -> realm.close());
        return taskObservable;
    }

    public Observable<List<Task>> getUnmanagedModifiedTasks() {
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

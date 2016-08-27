package com.ziomacki.todo.task.model;

import com.ziomacki.todo.component.RealmWrapper;
import javax.inject.Inject;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

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

}

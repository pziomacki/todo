package com.ziomacki.todo.task.model;

import io.realm.RealmList;
import io.realm.RealmObject;

public class TaskContainer extends RealmObject{

    public RealmList<Task> taskRealmList;
    public int totalCount;


}

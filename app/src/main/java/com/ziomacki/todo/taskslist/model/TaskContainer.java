package com.ziomacki.todo.taskslist.model;

import com.ziomacki.todo.taskdetails.model.Task;
import io.realm.RealmList;
import io.realm.RealmObject;

public class TaskContainer extends RealmObject{

    public RealmList<Task> taskRealmList;
    public int totalCount;

    public boolean isAllFetched() {
        return taskRealmList.size() == totalCount;
    }

}

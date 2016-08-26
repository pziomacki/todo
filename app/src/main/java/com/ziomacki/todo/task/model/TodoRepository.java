package com.ziomacki.todo.task.model;

import com.ziomacki.todo.component.RealmWrapper;
import javax.inject.Inject;

public class TodoRepository {

    @Inject
    RealmWrapper realmWrapper;

    @Inject
    TodoRepository() {}
}

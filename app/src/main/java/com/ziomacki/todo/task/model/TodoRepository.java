package com.ziomacki.todo.task.model;

import com.ziomacki.todo.component.RealmWrapper;
import javax.inject.Inject;

public class TodoRepository {

    private RealmWrapper realmWrapper;

    @Inject
    TodoRepository(RealmWrapper realmWrapper) {
        this.realmWrapper = realmWrapper;
    }

}

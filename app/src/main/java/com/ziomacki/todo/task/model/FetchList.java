package com.ziomacki.todo.task.model;

import java.util.List;
import javax.inject.Inject;
import rx.Observable;

public class FetchList {

    @Inject
    TodoRepository todoRepository;
    @Inject
    TodoService todoService;

    @Inject
    FetchList() {}

    public Observable<List<Task>> fetchNextPartOfTasks(int currentTaskListSize) {
        return todoService.fetchTasks(currentTaskListSize);
    }
}

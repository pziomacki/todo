package com.ziomacki.todo.task.model;

import javax.inject.Inject;
import rx.Observable;
import rx.functions.Action1;

public class FetchList {

    private TodoRepository todoRepository;
    private TodoService todoService;

    @Inject
    FetchList(TodoRepository todoRepository, TodoService todoService) {
        this.todoRepository = todoRepository;
        this.todoService = todoService;
    }

    public Observable<TaskContainer> fetchNextPartOfTasks(int currentTaskListSize) {
        return todoService.fetchTasks(currentTaskListSize).doOnNext(new Action1<TaskContainer>() {
            @Override
            public void call(TaskContainer taskContainer) {
                //TODO: implement
            }
        });
    }
}

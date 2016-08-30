package com.ziomacki.todo.taskslist.model;

import javax.inject.Inject;
import rx.Observable;

public class FetchList {

    private TaskListRepository taskListRepository;
    private TodoService todoService;

    @Inject
    FetchList(TaskListRepository taskListRepository, TodoService todoService) {
        this.taskListRepository = taskListRepository;
        this.todoService = todoService;
    }

    public Observable<TaskContainer> fetchNextPartOfTasks(int currentTaskListSize) {
        return todoService.fetchTasks(currentTaskListSize)
                .doOnNext(taskContainer -> taskListRepository.updateTaskContainer(taskContainer));
    }
}

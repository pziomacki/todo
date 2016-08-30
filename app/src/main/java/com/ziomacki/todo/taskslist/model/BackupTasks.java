package com.ziomacki.todo.taskslist.model;

import com.ziomacki.todo.taskdetails.model.Task;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;

public class BackupTasks {

    private TodoService todoService;
    private TaskListRepository taskListRepository;

    @Inject
    public BackupTasks(TodoService todoService, TaskListRepository taskListRepository) {
        this.todoService = todoService;
        this.taskListRepository = taskListRepository;
    }

    public Observable<List<Task>> backup() {
        return taskListRepository.getUnmanagedModifiedTasks()
                .flatMap(tasks -> todoService.backupTasks(tasks))
                .map(tasks -> {
                    for (int i = 0; i < tasks.size(); i++) {
                        tasks.get(i).modified = false;
                    }
                    taskListRepository.updateTaskList(tasks);
                    return tasks;
                });
    }

}

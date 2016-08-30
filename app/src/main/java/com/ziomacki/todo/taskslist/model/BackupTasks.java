package com.ziomacki.todo.taskslist.model;

import com.ziomacki.todo.taskdetails.model.Task;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class BackupTasks {

    private TodoService todoService;
    private TaskListRepository taskListRepository;

    @Inject
    public BackupTasks(TodoService todoService, TaskListRepository taskListRepository) {
        this.todoService = todoService;
        this.taskListRepository = taskListRepository;
    }

    public Observable<List<Task>> backup() {
        return taskListRepository.getUnmanagedModifiedTasks().flatMap(
                new Func1<List<Task>,
                        Observable<List<Task>>>() {
                    @Override
                    public Observable<List<Task>> call(List<Task> tasks) {
                        return todoService.backupTasks(tasks);
                    }
                }).map(new Func1<List<Task>, List<Task>>() {
            @Override
            public List<Task> call(List<Task> taskList) {
                for (int i = 0; i < taskList.size(); i++) {
                    taskList.get(i).modified = false;
                }
                taskListRepository.updateTaskList(taskList);
                return taskList;
            }
        });
    }

}

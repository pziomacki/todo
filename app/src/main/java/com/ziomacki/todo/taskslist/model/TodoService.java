package com.ziomacki.todo.taskslist.model;

import com.ziomacki.todo.taskdetails.model.Task;
import java.util.List;
import javax.inject.Inject;
import io.realm.RealmList;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

public class TodoService {
    private static final String TOTAL_COUNT_HEADER_NAME = "X-Total-Count";
    private static final int PAGE_SIZE = 30;

    private TodoApiService todoApiService;

    @Inject
    public TodoService(TodoApiService todoApiService) {
        this.todoApiService = todoApiService;
    }

    public Observable<TaskContainer> fetchTasks(int start) {
        int limit = start + PAGE_SIZE;
        return todoApiService.getTaskList(start, limit).map(new Func1<Response<List<Task>>, TaskContainer>() {
            @Override
            public TaskContainer call(Response<List<Task>> tasksResponse) {
                TaskContainer taskContainer = readFetchTasksResponse(tasksResponse);
                return taskContainer;
            }
        });
    }

    private TaskContainer readFetchTasksResponse(Response<List<Task>> tasksResponse) {
        TaskContainer taskContainer = new TaskContainer();
        taskContainer.taskRealmList = new RealmList<>();
        taskContainer.taskRealmList.addAll(tasksResponse.body());
        int totalCount = Integer.parseInt(tasksResponse.headers().get(TOTAL_COUNT_HEADER_NAME));
        taskContainer.totalCount = totalCount;
        return taskContainer;
    }

    public Observable<List<Task>> backupTasks(final List<Task> taskList) {
        if (taskList.size() != 0) {
            Observable<Task> observable = mergePutTaskCalls(taskList);
            return observable.flatMap(new Func1<Task, Observable<List<Task>>>() {
                @Override
                public Observable<List<Task>> call(Task task) {
                    return Observable.just(taskList);
                }
            });
        } else {
            return Observable.empty();
        }
    }

    private Observable<Task> mergePutTaskCalls(List<Task> taskList) {
        Task firstTask = taskList.get(0);
        Observable<Task> observable = todoApiService.putTask(firstTask.id, firstTask);
        for (int i = 1; i < taskList.size(); i++) {
            Task task = taskList.get(i);
            observable = observable.mergeWith(todoApiService.putTask(task.id, task));
        }
        return observable;
    }
}

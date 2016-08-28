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
                TaskContainer taskContainer = readResponse(tasksResponse);
                return taskContainer;
            }
        });
    }

    private TaskContainer readResponse(Response<List<Task>> tasksResponse) {
        TaskContainer taskContainer = new TaskContainer();
        taskContainer.taskRealmList = new RealmList<>();
        taskContainer.taskRealmList.addAll(tasksResponse.body());
        int totalCount = Integer.parseInt(tasksResponse.headers().get(TOTAL_COUNT_HEADER_NAME));
        taskContainer.totalCount = totalCount;
        return taskContainer;
    }
}

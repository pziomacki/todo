package com.ziomacki.todo.task.model;

import java.util.List;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface TodoApiService {

    @GET("todos")
    Observable<Response<List<Task>>> getTaskList(@Query("_start") int start, @Query("_limit") int limit);

    @PUT("todos/{id}")
    Observable<Task> putTask(@Path("id") int id, @Body Task task);
}

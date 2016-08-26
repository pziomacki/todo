package com.ziomacki.todo.inject;

import com.ziomacki.todo.task.model.TodoApiService;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class TodoModule {

    @Provides
    @ActivityScope
    TodoApiService provideTodoApiService(Retrofit retrofit) {
        return retrofit.create(TodoApiService.class);
    }
}

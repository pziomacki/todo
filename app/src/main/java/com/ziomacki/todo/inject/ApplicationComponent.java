package com.ziomacki.todo.inject;

import com.ziomacki.todo.TodoApplication;
import com.ziomacki.todo.taskdetails.view.TaskDetailsActivity;
import dagger.Component;

@ApplicationScope
@Component(modules = {ApplicationModule.class, NetworkModule.class, StorageModule.class})
public interface ApplicationComponent {
    void inject(TodoApplication application);
    void inject(TaskDetailsActivity taskDetailsActivity);

    TodoComponent todoComponent(TodoModule todoModule);

}

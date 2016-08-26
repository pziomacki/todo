package com.ziomacki.todo.inject;

import com.ziomacki.todo.TodoApplication;
import dagger.Component;

@ApplicationScope
@Component(modules = {ApplicationModule.class, NetworkModule.class, StorageModule.class})
public interface ApplicationComponent {
    void inject(TodoApplication application);

}

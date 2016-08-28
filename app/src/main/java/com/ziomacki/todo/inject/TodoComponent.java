package com.ziomacki.todo.inject;

import com.ziomacki.todo.task.view.ListActivity;
import com.ziomacki.todo.task.view.TaskDetailsActivity;
import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = {TodoModule.class})
public interface TodoComponent {

    void inject(ListActivity listActivity);
    void inject(TaskDetailsActivity taskDetailsActivity);

}

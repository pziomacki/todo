package com.ziomacki.todo.task.presenter;

import com.ziomacki.todo.task.model.Task;
import com.ziomacki.todo.task.model.TodoRepository;
import com.ziomacki.todo.task.view.TaskDetailsView;
import javax.inject.Inject;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class TaskDetailsPresenter {

    private TaskDetailsView taskDetailsView;
    private int taskId;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    @Inject
    TodoRepository todoRepository;

    @Inject
    TaskDetailsPresenter() {}

    public void attachView(TaskDetailsView taskDetailsView) {
        this.taskDetailsView = taskDetailsView;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
        loadTask();
    }

    private void loadTask() {
        Subscription subscription = todoRepository.getTask(taskId).subscribe(new Action1<Task>() {
            @Override
            public void call(Task task) {
                taskDetailsView.displayTaskText(task.title);
                taskDetailsView.setIsCompleted(task.completed);
            }
        });
        compositeSubscription.add(subscription);
    }

    public void onDestroy() {
        compositeSubscription.clear();
    }
}

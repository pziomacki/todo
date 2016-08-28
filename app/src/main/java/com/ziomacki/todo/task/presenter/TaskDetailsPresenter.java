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
    private Task task;
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
                onTaskLoaded(task);
            }
        });
        compositeSubscription.add(subscription);
    }

    private void onTaskLoaded(Task task) {
        this.task = task;
        taskDetailsView.displayTaskText(task.title);
        taskDetailsView.setIsCompleted(task.completed);
    }

    public void onDestroy() {
        compositeSubscription.clear();
    }

    public void updateAndSaveTask(String taskText, boolean isComplete) {
        task.completed = isComplete;
        task.title = taskText;
        task.modified = true;
        Subscription subscription = todoRepository.saveTask(task).subscribe(new Action1<Task>() {
            @Override
            public void call(Task task) {
                taskDetailsView.close();
            }
        });
        compositeSubscription.add(subscription);
    }
}

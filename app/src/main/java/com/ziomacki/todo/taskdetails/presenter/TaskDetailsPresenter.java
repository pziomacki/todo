package com.ziomacki.todo.taskdetails.presenter;

import com.ziomacki.todo.taskdetails.model.Task;
import com.ziomacki.todo.taskdetails.model.TaskRepository;
import com.ziomacki.todo.taskdetails.view.TaskDetailsView;
import javax.inject.Inject;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class TaskDetailsPresenter {

    private TaskDetailsView taskDetailsView;
    private int taskId;
    private Task task;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    @Inject
    TaskRepository taskRepository;

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
        Subscription subscription = taskRepository.getTask(taskId).subscribe(task -> onTaskLoaded(task));
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
        Subscription subscription = taskRepository.saveTask(task).subscribe(task -> taskDetailsView.close());
        compositeSubscription.add(subscription);
    }
}

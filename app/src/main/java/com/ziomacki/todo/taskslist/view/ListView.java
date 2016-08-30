package com.ziomacki.todo.taskslist.view;

import com.ziomacki.todo.taskdetails.model.Task;
import java.util.List;

public interface ListView {
    void bindTaskList(List<Task> taskList);
    void showLoadingMore();
    void hideLoadingMore();
    void displayErrorMessage();
    void openDetails(int taskId);
    void displayNoModifiedTasks();
    void loadingMoreEnabled(boolean loadingMoreEnabled);
    void showLoading();
    void hideLoading();
}

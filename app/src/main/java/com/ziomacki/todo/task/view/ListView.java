package com.ziomacki.todo.task.view;

import com.ziomacki.todo.task.model.Task;
import java.util.List;

public interface ListView {
    void bindTaskList(List<Task> taskList);
    void showLoadingMore();
    void hideLoadingMore();
    void displayErrorMessage();

}

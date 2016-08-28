package com.ziomacki.todo.taskdetails.view;

public interface TaskDetailsView {
    void setIsCompleted(boolean isCompleted);
    void displayTaskText(String text);
    void close();
}

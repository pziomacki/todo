package com.ziomacki.todo.taskslist.eventbus;

public class OnTaskOpenEvent {

    public final int taskId;

    public OnTaskOpenEvent(int taskId) {
        this.taskId = taskId;
    }
}

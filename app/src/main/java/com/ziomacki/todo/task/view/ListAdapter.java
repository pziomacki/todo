package com.ziomacki.todo.task.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ziomacki.todo.R;
import com.ziomacki.todo.component.ResourceProvider;
import com.ziomacki.todo.task.model.Task;
import org.greenrobot.eventbus.EventBus;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class ListAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    private List<Task> taskList = Collections.emptyList();

    @Inject
    EventBus eventBus;
    @Inject
    ResourceProvider resourceProvider;

    @Inject
    ListAdapter() {}

    public void setResult(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item_view, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task, eventBus, resourceProvider);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
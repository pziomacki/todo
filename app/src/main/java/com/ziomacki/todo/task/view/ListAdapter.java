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

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_TASK = 0;
    private static final int ITEM_TYPE_PROGRESS = 1;

    private List<Task> taskList = Collections.emptyList();
    private boolean isLoadingMore = false;

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_TASK) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_item_view, parent, false);
            return new TaskViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.footer_progress_view, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_TASK) {
            Task task = taskList.get(position);
            ((TaskViewHolder) holder).bind(task, eventBus, resourceProvider);
        }
    }

    public void showLoadingMore() {
        isLoadingMore = true;
        notifyDataSetChanged();
    }

    public void hideLoadingMore() {
        isLoadingMore = false;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return taskList.size() + (isLoadingMore? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoadingMore && position == taskList.size()) {
            return ITEM_TYPE_PROGRESS;
        } else {
            return ITEM_TYPE_TASK;
        }
    }
}
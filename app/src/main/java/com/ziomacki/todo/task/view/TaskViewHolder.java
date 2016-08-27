package com.ziomacki.todo.task.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ziomacki.todo.R;
import com.ziomacki.todo.component.ResourceProvider;
import com.ziomacki.todo.task.eventbus.OnTaskOpenEvent;
import com.ziomacki.todo.task.model.Task;
import org.greenrobot.eventbus.EventBus;

public class TaskViewHolder extends RecyclerView.ViewHolder{

    LinearLayout mainContainerView;
    TextView titleView;
    TextView completedView;

    public TaskViewHolder(View itemView) {
        super(itemView);
        titleView = (TextView) itemView.findViewById(R.id.list_item_title);
        completedView = (TextView) itemView.findViewById(R.id.list_item_completed);
        mainContainerView = (LinearLayout) itemView.findViewById(R.id.list_item_container);

    }

    public void bind(Task task, EventBus eventBus, ResourceProvider resourceProvider) {
        setTitleView(task.title);
        setCompletedView(resourceProvider, task.completed);
        setOnClickListener(eventBus, task.id);
    }

    private void setTitleView(String title) {
        titleView.setText(title);
    }

    private void setCompletedView(ResourceProvider resourceProvider, boolean isCompleted) {
        completedView.setText(String.format(resourceProvider.getString(R.string.item_completed),
                Boolean.toString(isCompleted)));
    }

    private void setOnClickListener(final EventBus eventBus, final int taskId) {
        mainContainerView.setOnClickListener(new OnItemClickListener(eventBus, taskId));
    }

    private static class OnItemClickListener implements View.OnClickListener {
        private int taskId;
        private EventBus eventBus;

        public OnItemClickListener(EventBus eventBus, int taskId) {
            this.taskId = taskId;
            this.eventBus = eventBus;
        }

        @Override
        public void onClick(View view) {
            OnTaskOpenEvent event = new OnTaskOpenEvent(taskId);
            eventBus.post(event);
        }
    }
}
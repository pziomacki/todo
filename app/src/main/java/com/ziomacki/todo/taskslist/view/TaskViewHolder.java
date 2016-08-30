package com.ziomacki.todo.taskslist.view;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ziomacki.todo.R;
import com.ziomacki.todo.component.ResourceProvider;
import com.ziomacki.todo.taskslist.eventbus.OnTaskOpenEvent;
import com.ziomacki.todo.taskdetails.model.Task;
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
        setIsModified(task.modified);
        setOnClickListener(eventBus, task.id);
    }

    private void setIsModified(boolean modified) {
        int backgroundColor = modified ? Color.GRAY : Color.WHITE;
        mainContainerView.setBackgroundColor(backgroundColor);
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

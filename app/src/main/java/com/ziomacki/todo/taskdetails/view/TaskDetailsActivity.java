package com.ziomacki.todo.taskdetails.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import com.ziomacki.todo.R;
import com.ziomacki.todo.TodoApplication;
import com.ziomacki.todo.inject.ApplicationComponent;
import com.ziomacki.todo.taskdetails.presenter.TaskDetailsPresenter;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskDetailsActivity extends AppCompatActivity implements TaskDetailsView {

    private static final String KEY_TASK_ID = "KEY_TASK_ID";

    @BindView(R.id.details_completed_checkbox)
    CheckBox completedCheckboxView;
    @BindView(R.id.details_text)
    EditText detailsTextView;
    @BindView(R.id.details_toolbar)
    Toolbar toolbar;
    @Inject
    TaskDetailsPresenter taskDetailsPresenter;

    public static void startActivity(Context context, int taskId) {
        Intent intent = new Intent(context, TaskDetailsActivity.class);
        intent.putExtra(KEY_TASK_ID, taskId);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        ButterKnife.bind(this);
        initViews();
        injectDependencies();
        taskDetailsPresenter.attachView(this);
        readStartIntentBundle();
    }

    private void readStartIntentBundle() {
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(KEY_TASK_ID)) {
            int taskId = getIntent().getExtras().getInt(KEY_TASK_ID);
            taskDetailsPresenter.setTaskId(taskId);
        } else {
            throw new IllegalArgumentException("KEY_TASK_ID extras must be provided. Use " +
                    "TaskDetailsActivity.startActivity(Context context, long taskId)");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskDetailsPresenter.onDestroy();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
    }

    private void injectDependencies() {
        ApplicationComponent applicationComponent =
                ((TodoApplication) getApplication()).getApplicationComponent();
        applicationComponent.inject(this);
    }

    @Override
    public void setIsCompleted(boolean isCompleted) {
        completedCheckboxView.setChecked(isCompleted);
    }

    @Override
    public void displayTaskText(String text) {
        detailsTextView.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                taskDetailsPresenter.updateAndSaveTask(detailsTextView.getText().toString(),
                        completedCheckboxView.isChecked());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void close() {
        finish();
    }
}

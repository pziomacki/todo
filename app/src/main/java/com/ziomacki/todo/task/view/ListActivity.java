package com.ziomacki.todo.task.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.ziomacki.todo.R;
import com.ziomacki.todo.TodoApplication;
import com.ziomacki.todo.inject.ApplicationComponent;
import com.ziomacki.todo.inject.TodoModule;
import com.ziomacki.todo.task.presenter.ListPresenter;
import javax.inject.Inject;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity {

    @Inject
    ListPresenter listPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        injectDependencies();
    }

    private void injectDependencies() {
        ApplicationComponent applicationComponent =
                ((TodoApplication) getApplication()).getApplicationComponent();
        applicationComponent.todoComponent(new TodoModule()).inject(this);
    }
}

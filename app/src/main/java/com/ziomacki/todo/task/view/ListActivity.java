package com.ziomacki.todo.task.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.ziomacki.todo.R;
import com.ziomacki.todo.TodoApplication;
import com.ziomacki.todo.inject.ApplicationComponent;
import com.ziomacki.todo.inject.TodoModule;
import com.ziomacki.todo.task.eventbus.OnTaskOpenEvent;
import com.ziomacki.todo.task.model.Task;
import com.ziomacki.todo.task.presenter.ListPresenter;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity implements ListView{

    @BindView(R.id.list_recycler_view)
    RecyclerView listRecyclerView;
    @BindView(R.id.list_toolbar)
    Toolbar toolbar;
    @BindView(R.id.list_swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    ListPresenter listPresenter;
    @Inject
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        injectDependencies();
        initViews();
        listPresenter.attachView(this);
    }

    private void initViews() {
        setupRecyclerView();
        setSupportActionBar(toolbar);
    }

    private void injectDependencies() {
        ApplicationComponent applicationComponent =
                ((TodoApplication) getApplication()).getApplicationComponent();
        applicationComponent.todoComponent(new TodoModule()).inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        listPresenter.onStop();
    }
    private void setupRecyclerView() {
        listRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        HorizontalDividerItemDecoration listDivider = new HorizontalDividerItemDecoration
                .Builder(this).build();
        listRecyclerView.setLayoutManager(linearLayoutManager);
        listRecyclerView.setAdapter(listAdapter);
        listRecyclerView.addItemDecoration(listDivider);
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void bindTaskList(List<Task> taskList) {
        listAdapter.setResult(taskList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListItemClick(OnTaskOpenEvent openEvent) {
        //TODO: implement
    }
}

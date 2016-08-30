package com.ziomacki.todo.taskslist.view;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.ziomacki.todo.R;
import com.ziomacki.todo.TodoApplication;
import com.ziomacki.todo.inject.ApplicationComponent;
import com.ziomacki.todo.inject.TodoModule;
import com.ziomacki.todo.taskdetails.view.TaskDetailsActivity;
import com.ziomacki.todo.taskslist.eventbus.OnTaskOpenEvent;
import com.ziomacki.todo.taskdetails.model.Task;
import com.ziomacki.todo.taskslist.presenter.ListPresenter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

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
    @Inject
    LoadMoreOnScrollListener loadMoreOnScrollListener;
    @Inject
    EventBus eventBus;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

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
        eventBus.register(this);
    }

    @Override
    public void loadingMoreEnabled(boolean loadingMoreEnabled) {
        if (loadingMoreEnabled) {
            registerLoadMoreListener();
        } else {
            removeLoadMoreListener();
        }
    }

    private void removeLoadMoreListener() {
        listRecyclerView.removeOnScrollListener(loadMoreOnScrollListener);
        compositeSubscription.clear();
    }

    private void registerLoadMoreListener() {
        listRecyclerView.addOnScrollListener(loadMoreOnScrollListener);
        Subscription subscription = loadMoreOnScrollListener.getLoadMoreObservable().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                listPresenter.loadMore();
            }
        });
        compositeSubscription.add(subscription);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listPresenter.onDestroy();
    }

    private void unregisterListeners() {
        removeLoadMoreListener();
        eventBus.unregister(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                listPresenter.filterList();
                return true;
            case R.id.action_backup:
                //TODO: implement
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void bindTaskList(List<Task> taskList) {
        listAdapter.setResult(taskList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListItemClick(OnTaskOpenEvent openEvent) {
        listPresenter.onTaskClick(openEvent);
    }

    @Override
    public void openDetails(int taskId) {
        TaskDetailsActivity.startActivity(this, taskId);
    }

    @Override
    public void showLoadingMore() {
        listAdapter.showLoadingMore();
    }

    @Override
    public void hideLoadingMore() {
        listAdapter.hideLoadingMore();
    }

    private void displaySnackbar(String message) {
        Snackbar.make(swipeRefreshLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void displayErrorMessage() {
        displaySnackbar(getString(R.string.error_message));
    }

    @Override
    public void displayNoModifiedTasks() {
        displaySnackbar(getString(R.string.list_menu_no_modified_tasks));
    }
}

package com.ziomacki.todo.taskslist.presenter;

import android.os.Bundle;
import com.ziomacki.todo.component.RxTransformer;
import com.ziomacki.todo.taskdetails.model.Task;
import com.ziomacki.todo.taskslist.eventbus.OnTaskOpenEvent;
import com.ziomacki.todo.taskslist.model.BackupTasks;
import com.ziomacki.todo.taskslist.model.FetchList;
import com.ziomacki.todo.taskslist.model.TaskListRepository;
import com.ziomacki.todo.taskslist.view.ListView;
import java.util.List;
import javax.inject.Inject;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class ListPresenter {
    private static final String KEY_MODIFIED_ONLY = "KEY_MODIFIED_ONLY";
    private static final String KEY_LOADING_MORE_ENABLED = "KEY_LOADING_MORE_ENABLED";

    private ListView listView;
    private FetchList fetchList;
    private BackupTasks backupTasks;
    private TaskListRepository taskListRepository;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private List<Task> tasks;
    private boolean loading = false;
    private boolean showModifiedOnly = false;
    private boolean loadingMoreEnabled = true;

    @Inject
    public ListPresenter(FetchList fetchList, TaskListRepository taskListRepository, BackupTasks backupTasks) {
        this.fetchList = fetchList;
        this.taskListRepository = taskListRepository;
        this.backupTasks = backupTasks;
    }

    public void attachView(ListView listView) {
        this.listView = listView;
    }

    public void saveInstance(Bundle outState) {
        outState.putBoolean(KEY_MODIFIED_ONLY, showModifiedOnly);
        outState.putBoolean(KEY_LOADING_MORE_ENABLED, loadingMoreEnabled);
    }

    public void init(Bundle saveInstanceState) {
        if (saveInstanceState != null) {
            showModifiedOnly = saveInstanceState.getBoolean(KEY_MODIFIED_ONLY, false);
            loadingMoreEnabled = saveInstanceState.getBoolean(KEY_LOADING_MORE_ENABLED, true);
        }
        loadTasks();
    }

    public void onStart() {
        setIsLoadingMore();
    }

    private void loadTasks() {
        clearSubscriptions();
        if (showModifiedOnly) {
            compositeSubscription.add(getModifiedOnlyTasksSubscription());
        } else {
            compositeSubscription.add(getAllTaskSubscription());
        }
    }

    private Subscription getAllTaskSubscription() {
        return taskListRepository.getTasks(false).skip(1).subscribe(tasks -> setAllTasks(tasks));
    }

    private void setAllTasks(List<Task> tasks) {
        this.tasks = tasks;
        updateListViewTasks();
        if (isTaskListEmpty()) {
            fetchNextPage();
        }
    }

    public void filterList() {
        showModifiedOnly = !showModifiedOnly;
        loadingMoreEnabled = !loadingMoreEnabled;
        setIsLoadingMore();
        loadTasks();
    }

    private Subscription getModifiedOnlyTasksSubscription() {
        return taskListRepository.getTasks(true).skip(1).subscribe(tasks -> setModifiedTasks(tasks));
    }

    private void setModifiedTasks(List<Task> tasks) {
        this.tasks = tasks;
        updateListViewTasks();
        if (isTaskListEmpty()) {
            listView.displayNoModifiedTasks();
        }
    }

    private void setIsLoadingMore() {
        listView.loadingMoreEnabled(loadingMoreEnabled);
    }

    private boolean isTaskListEmpty() {
        return tasks == null || tasks.size() == 0;
    }

    private void updateListViewTasks() {
        listView.bindTaskList(tasks);
    }

    private void fetchNextPage() {
        int listSize = tasks.size();
        listView.showLoadingMore();
        setLoading(true);
        Subscription subscription = fetchList.fetchNextPartOfTasks(listSize)
                .compose(RxTransformer.applySchedulers())
                .subscribe(
                        container -> finishFetchingData(),
                        throwable -> {
                            //TODO: handle specific network errors
                            listView.displayErrorMessage();
                            finishFetchingData();
                        });
        compositeSubscription.add(subscription);
    }

    private void handleBackupSucces() {
        listView.hideLoading();
        if (showModifiedOnly) {
            filterList();
        }
    }

    public void backupTasks() {
        listView.showLoading();
        Subscription subscription = backupTasks.backup()
                .compose(RxTransformer.applySchedulers())
                .subscribe(
                        taskList -> handleBackupSucces(),
                        throwable -> {
                            listView.displayErrorMessage();
                            listView.hideLoading();
                        });
        compositeSubscription.add(subscription);
    }

    private void finishFetchingData() {
        setLoading(false);
        listView.hideLoadingMore();
    }

    private void setLoading(boolean isLoading) {
        this.loading = isLoading;
    }

    public void onDestroy() {
        clearSubscriptions();
    }

    private void clearSubscriptions() {
        compositeSubscription.clear();
    }

    public void loadMore() {
        if (!loading) {
            fetchNextPage();
        }
    }

    public void onTaskClick(OnTaskOpenEvent event) {
        listView.openDetails(event.taskId);
    }
}

package com.ziomacki.todo.taskslist.presenter;

import android.os.Bundle;
import com.ziomacki.todo.taskdetails.model.Task;
import com.ziomacki.todo.taskslist.eventbus.OnTaskOpenEvent;
import com.ziomacki.todo.taskslist.model.BackupTasks;
import com.ziomacki.todo.taskslist.model.FetchList;
import com.ziomacki.todo.taskslist.model.TaskContainer;
import com.ziomacki.todo.taskslist.model.TaskListRepository;
import com.ziomacki.todo.taskslist.view.ListView;
import java.util.List;
import javax.inject.Inject;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ListPresenter {

    private static final String KEY_MODIFIED_ONLY = "KEY_MODIFIED_ONLY";
    private static final String KEY_LOADING_MORE_ENABLED = "KEY_LOADING_MORE_ENABLED";

    private ListView listView;
    private FetchList fetchList;
    private BackupTasks backupTasks;
    private TaskListRepository taskListRepository;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private RealmResults<Task> tasks;
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
        setLoadingMore();
    }

    private void loadAllTasks() {
        Subscription subscription = taskListRepository.getTasks(false).subscribe(new Action1<RealmResults<Task>>() {
            @Override
            public void call(RealmResults<Task> tasks) {
                setAllTasks(tasks);
            }
        });
        compositeSubscription.add(subscription);
    }

    public void filterList() {
        showModifiedOnly = !showModifiedOnly;
        loadingMoreEnabled = !loadingMoreEnabled;
        setLoadingMore();
        loadTasks();
    }

    private void setLoadingMore() {
        listView.loadingMoreEnabled(loadingMoreEnabled);
    }

    private void loadTasks() {
        if (showModifiedOnly) {
            loadModifiedOnlyTasks();
        } else {
            loadAllTasks();
        }
    }

    private void loadModifiedOnlyTasks() {
        removeListenersAndClearSubscriptions();
        Subscription subscription = taskListRepository.getTasks(true).subscribe(new Action1<RealmResults<Task>>() {
            @Override
            public void call(RealmResults<Task> tasks) {
                setModifiedTasks(tasks);
            }
        });
        compositeSubscription.add(subscription);
    }

    private void setModifiedTasks(RealmResults<Task> tasks) {
        this.tasks = tasks;
        updateListViewTasks();
        addTasksListener();
        if (isTaskListEmpty()) {
            listView.displayNoModifiedTasks();
        }
    }

    private void setAllTasks(RealmResults<Task> tasks) {
        this.tasks = tasks;
        updateListViewTasks();
        addTasksListener();
        if (isTaskListEmpty()) {
            fetchNextPage();
        }
    }

    private boolean isTaskListEmpty() {
        return tasks == null || tasks.size() == 0;
    }

    private void addTasksListener() {
        tasks.addChangeListener(new RealmChangeListener<RealmResults<Task>>() {
            @Override
            public void onChange(RealmResults<Task> element) {
                updateListViewTasks();
            }
        });
    }

    private void updateListViewTasks() {
        listView.bindTaskList(tasks);
    }

    private void fetchNextPage() {
        int listSize = tasks.size();
        listView.showLoadingMore();
        setLoading(true);
        Subscription subscription = fetchList.fetchNextPartOfTasks(listSize).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Action1<TaskContainer>() {
                    @Override
                    public void call(TaskContainer taskContainer) {
                        finishFetchingData();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //TODO: handle specific errors
                        listView.displayErrorMessage();
                        finishFetchingData();
                    }
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
        Subscription subscription = backupTasks.backup().subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Task>>() {
                    @Override
                    public void call(List<Task> taskList) {
                        handleBackupSucces();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        listView.hideLoading();
                    }
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
        removeListenersAndClearSubscriptions();
    }

    private void removeListenersAndClearSubscriptions() {
        if (tasks != null) {
            tasks.removeChangeListeners();
        }
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

package com.ziomacki.todo.taskslist.presenter;

import com.ziomacki.todo.taskdetails.model.Task;
import com.ziomacki.todo.taskslist.eventbus.OnTaskOpenEvent;
import com.ziomacki.todo.taskslist.model.FetchList;
import com.ziomacki.todo.taskslist.model.TaskContainer;
import com.ziomacki.todo.taskslist.model.TaskListRepository;
import com.ziomacki.todo.taskslist.view.ListView;
import javax.inject.Inject;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ListPresenter {

    private ListView listView;
    private FetchList fetchList;
    private TaskListRepository taskListRepository;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private RealmResults<Task> tasks;
    private boolean loading = false;
    private boolean showModifiedOnly = false;
    private boolean loadingMoreEnabled = true;

    @Inject
    public ListPresenter(FetchList fetchList, TaskListRepository taskListRepository) {
        this.fetchList = fetchList;
        this.taskListRepository = taskListRepository;
    }

    public void attachView(ListView listView) {
        this.listView = listView;
        loadAllTasks();
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
        tasks.removeChangeListeners();
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

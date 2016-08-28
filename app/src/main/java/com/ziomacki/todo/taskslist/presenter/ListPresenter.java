package com.ziomacki.todo.taskslist.presenter;

import com.ziomacki.todo.taskslist.eventbus.OnTaskOpenEvent;
import com.ziomacki.todo.taskslist.model.FetchList;
import com.ziomacki.todo.taskdetails.model.Task;
import com.ziomacki.todo.taskslist.model.TaskContainer;
import com.ziomacki.todo.taskslist.model.TaskListRepository;
import com.ziomacki.todo.taskslist.view.ListView;
import javax.inject.Inject;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ListPresenter {

    private ListView listView;
    private FetchList fetchList;
    private TaskListRepository taskListRepository;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private RealmResults<Task> tasks;
    private boolean isLoading = false;

    @Inject
    public ListPresenter(FetchList fetchList, TaskListRepository taskListRepository) {
        this.fetchList = fetchList;
        this.taskListRepository = taskListRepository;
    }

    public void attachView(ListView listView) {
        this.listView = listView;
        loadTasks();
    }

    private void loadTasks() {
        Subscription subscription = taskListRepository.getTasks(false).subscribe(new Action1<RealmResults<Task>>() {
            @Override
            public void call(RealmResults<Task> tasks) {
                setTasks(tasks);
            }
        });
        compositeSubscription.add(subscription);
    }

    private void setTasks(RealmResults<Task> tasks) {
        this.tasks = tasks;
        updateListViewTasks();
        addTasksListener();
        if (tasks.size() == 0) {
            fetchNextPage();
        }
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
                .onErrorReturn(new Func1<Throwable, TaskContainer>() {
                    @Override
                    public TaskContainer call(Throwable throwable) {
                        //TODO: handle specific errors
                        listView.displayErrorMessage();
                        finishFetchingData();
                        return null;
                    }
                }).subscribe(new Action1<TaskContainer>() {
                    @Override
                    public void call(TaskContainer taskContainer) {
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
        this.isLoading = isLoading;
    }

    public void onDestroy() {
        tasks.removeChangeListeners();
        compositeSubscription.clear();
    }

    public void loadMore() {
        if (!isLoading) {
            fetchNextPage();
        }
    }

    public void onTaskClick(OnTaskOpenEvent event) {
        listView.openDetails(event.taskId);
    }
}

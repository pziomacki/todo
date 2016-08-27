package com.ziomacki.todo.task.presenter;

import com.ziomacki.todo.task.model.FetchList;
import com.ziomacki.todo.task.model.Task;
import com.ziomacki.todo.task.model.TaskContainer;
import com.ziomacki.todo.task.model.TodoRepository;
import com.ziomacki.todo.task.view.ListView;
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
    private TodoRepository todoRepository;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private RealmResults<Task> tasks;
    private boolean isLoading = false;

    @Inject
    public ListPresenter(FetchList fetchList, TodoRepository todoRepository) {
        this.fetchList = fetchList;
        this.todoRepository = todoRepository;
    }

    public void attachView(ListView listView) {
        this.listView = listView;
        loadTasks();
    }

    private void loadTasks() {
        Subscription subscription = todoRepository.getTasks(false).subscribe(new Action1<RealmResults<Task>>() {
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
                        throwable.printStackTrace();
                        setLoading(false);
                        listView.showLoadingMore();
                        return null;
                    }
                }).subscribe(new Action1<TaskContainer>() {
                    @Override
                    public void call(TaskContainer taskContainer) {
                        setLoading(false);
                        listView.showLoadingMore();
                    }
                });
        compositeSubscription.add(subscription);
    }

    private void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public void onStart() {
        addTasksListener();
    }

    public void onStop() {
        tasks.removeChangeListeners();
        compositeSubscription.clear();
    }

    public void loadMore() {
        if (!isLoading) {
            fetchNextPage();
        }
    }
}

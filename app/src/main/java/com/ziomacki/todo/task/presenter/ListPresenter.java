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
                fetchFirstPage();
                setTasks(tasks);
            }
        });
        compositeSubscription.add(subscription);
    }

    private void setTasks(RealmResults<Task> tasks) {
        this.tasks = tasks;
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

    private void fetchFirstPage() {
        Subscription subscription = fetchList.fetchNextPartOfTasks(0).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread())
                .onErrorReturn(new Func1<Throwable, TaskContainer>() {
                    @Override
                    public TaskContainer call(Throwable throwable) {
                        throwable.printStackTrace();
                        return null;
                    }
                }).subscribe();
        compositeSubscription.add(subscription);
    }

    public void onStart() {
        addTasksListener();
    }

    public void onStop() {
        tasks.removeChangeListeners();
        compositeSubscription.clear();
    }
}

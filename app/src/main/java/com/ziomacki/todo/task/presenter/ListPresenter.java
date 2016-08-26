package com.ziomacki.todo.task.presenter;

import android.util.Log;
import com.ziomacki.todo.task.model.FetchList;
import com.ziomacki.todo.task.model.TaskContainer;
import com.ziomacki.todo.task.view.ListView;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ListPresenter {

    private ListView listView;
    private FetchList fetchList;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    @Inject
    public ListPresenter(FetchList fetchList) {
        this.fetchList = fetchList;
    }

    public void attachView(ListView listView) {
        this.listView = listView;
        fetchFirstPage();
    }

    private void fetchFirstPage() {
        Subscription subscription = fetchList.fetchNextPartOfTasks(0).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Action1<TaskContainer>() {
            @Override
            public void call(TaskContainer taskContainer) {
                Log.d("Test", "sukces");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        compositeSubscription.add(subscription);
    }

    public void onStop() {
        compositeSubscription.clear();
    }
}

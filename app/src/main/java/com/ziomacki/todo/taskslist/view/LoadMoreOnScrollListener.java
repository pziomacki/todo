package com.ziomacki.todo.taskslist.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class LoadMoreOnScrollListener extends RecyclerView.OnScrollListener {
    private Subject<Integer, Integer> loadMoreSubject = PublishSubject.create();
    private static int THRESHOLD = 5;

    @Inject
    LoadMoreOnScrollListener() {}

    @Override
    public void onScrolled(RecyclerView recyclerView, int deltaX, int deltaY) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int totalItemsCount = layoutManager.getItemCount();
        int lastVisible = layoutManager.findLastVisibleItemPosition();
        if (totalItemsCount - lastVisible < THRESHOLD) {
            loadMoreSubject.onNext(deltaY);
        }
    }

    public Observable<Integer> getLoadMoreObservable() {
        return loadMoreSubject;
    }
}
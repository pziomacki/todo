package com.ziomacki.todo.task.presenter;

import com.ziomacki.todo.task.model.FetchList;
import com.ziomacki.todo.task.view.ListView;
import javax.inject.Inject;

public class ListPresenter {

    private ListView listView;
    @Inject
    FetchList fetchList;

    @Inject
    ListPresenter() {}

    public void attachView(ListView listView) {
        this.listView = listView;
    }


}

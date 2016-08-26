package com.ziomacki.todo.list.presenter;

import com.ziomacki.todo.list.view.ListView;
import javax.inject.Inject;

public class ListPresenter {

    private ListView listView;

    @Inject
    ListPresenter() {}

    public void attachView(ListView listView) {
        this.listView = listView;
    }


}

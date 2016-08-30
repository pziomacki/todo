package com.ziomacki.todo.component;

import rx.Observable.Transformer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxTransformer {

    public static <T> Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}

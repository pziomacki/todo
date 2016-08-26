package com.ziomacki.todo;

import android.app.Application;
import com.ziomacki.todo.inject.ApplicationComponent;
import com.ziomacki.todo.inject.DaggerApplicationComponent;

public class TodoApplication extends Application {

    ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initApplicationComponent();
    }

    private void initApplicationComponent() {
        applicationComponent = DaggerApplicationComponent.builder().build();
        applicationComponent.inject(this);
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}

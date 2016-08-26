package com.ziomacki.todo;

import android.app.Application;
import com.ziomacki.todo.inject.ApplicationComponent;
import com.ziomacki.todo.inject.ApplicationModule;
import com.ziomacki.todo.inject.DaggerApplicationComponent;

public class TodoApplication extends Application {

    ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initApplicationComponent();
    }

    private void initApplicationComponent() {
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}

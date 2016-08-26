package com.ziomacki.todo.inject;

import android.content.Context;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final Context context;

    public ApplicationModule(Context context) {
        setupBusIndex();
        this.context = context;
    }

    @Provides
    Context provideApplicationContext() {
        return this.context;
    }

    private void setupBusIndex() {
        //TODO: implement
    }
}

package com.ziomacki.todo.inject;

import android.content.Context;
import com.ziomacki.todo.TodoBusIndex;
import org.greenrobot.eventbus.EventBus;
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
        EventBus.builder().addIndex(new TodoBusIndex()).installDefaultEventBus();
    }

    @Provides
    @ApplicationScope
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }
}

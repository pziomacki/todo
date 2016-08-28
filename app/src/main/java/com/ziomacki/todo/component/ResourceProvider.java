package com.ziomacki.todo.component;

import android.content.Context;
import javax.inject.Inject;

public class ResourceProvider {

    private Context context;

    @Inject
    public ResourceProvider(Context context) {
        this.context = context;
    }

    public String getString(int stringId) {
        return context.getString(stringId);
    }

}

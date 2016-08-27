package com.ziomacki.todo.component;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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

    public int getColor(int colorId) {
        return ContextCompat.getColor(context, colorId);
    }
}

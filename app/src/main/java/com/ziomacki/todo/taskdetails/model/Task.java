package com.ziomacki.todo.taskdetails.model;

import com.google.gson.annotations.Expose;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject{

    public static final String KEY_ID = "id";
    public static final String KEY_MODIFIED = "modified";

    @Expose
    public int userId;
    @PrimaryKey
    @Expose
    public int id;
    @Expose
    public String title;
    @Expose
    public boolean completed;
    public boolean modified;

}

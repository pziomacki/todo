package com.ziomacki.todo.list.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.ziomacki.todo.R;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }
}

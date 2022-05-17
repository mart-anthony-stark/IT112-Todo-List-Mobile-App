package com.mycompany.todoapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewActivity extends Activity {
    private TextView title, desc, date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_item);
		
		title = findViewById(R.id.viewTitle);
		desc = findViewById(R.id.viewDescription);
		date = findViewById(R.id.viewDate);
		
		title.setText(Global.currentViewTodo.title);
		desc.setText(Global.currentViewTodo.description);
		date.setText(Global.currentViewTodo.date);
    }
    
}

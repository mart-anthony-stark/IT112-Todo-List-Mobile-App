package com.mycompany.todoapp;
 
import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Adapter;
import android.content.Intent;

public class MainActivity extends Activity { 
    private Cursor cur;
	private ArrayList<Todo> items;
    private Dialog addDialog, editDialog;
	private Button showAddBtn, addSaveBtn, editSaveBtn, editCancelBtn;
	private ListView todoListView;
	private EditText addTitle, addDescription, editTitle, editDescription;
	private DatePicker addDate, editDate;
	private Database db;
	private String currentEditId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		//Instantiate essentials
		db = new Database(this);
		items = new ArrayList<Todo>();
		todoListView = findViewById(R.id.todoList);
		
		//Setup add todo modal dialog
		addDialog = new Dialog(MainActivity.this);
		addDialog.setContentView(R.layout.add_modal);
		addDialog.setTitle("Add todo");
		addDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		addDialog.setCancelable(true);
		
		//Add input
		addTitle = addDialog.findViewById(R.id.addTitle);
		addDescription = addDialog.findViewById(R.id.addDescription);
		addDate = addDialog.findViewById(R.id.addDate);
		addSaveBtn = addDialog.findViewById(R.id.addBtn);
		
		
		showAddBtn = findViewById(R.id.showAddBtn);
		showAddBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					addDialog.show();
				}
			});
			
			
	    // Edit dialog
		editDialog = new Dialog(MainActivity.this);
		editDialog.setContentView(R.layout.edit_modal);
		editDialog.setTitle("Edit todo");
		editDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		editDialog.setCancelable(true);
		
		// edit inputs
		editTitle = editDialog.findViewById(R.id.editTitle);
		editDescription = editDialog.findViewById(R.id.editDescription);
		editDate = editDialog.findViewById(R.id.editDate);
		editSaveBtn = editDialog.findViewById(R.id.editSaveBtn);
		editCancelBtn = editDialog.findViewById(R.id.ediCancelBtn);
		
		editSaveBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
				    saveEdit();
				}
			});
		
		editCancelBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1) {
					editDialog.hide();
				}
			});
		try{
			getAllData();
			registerForContextMenu(todoListView);
			addSaveBtn.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View p1) {
						insertTodo();
					}
				});
		}catch(Exception e){
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.todoList) {
			ListView lv = (ListView) v;
			AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
			Todo obj = (Todo) lv.getItemAtPosition(acmi.position);
            
			menu.add("Edit");
			String opt = (obj.status.equals("pending") ? "Mark as done":"Mark as undone");
			menu.add(opt);
			menu.add("Delete");
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int listPosition = info.position;
        Todo todo = items.get(listPosition);
	    String id = todo.id;
		
		if (item.getTitle().equals("Delete")) {
            db.deleteOne(id);
		}else if(item.getTitle().equals("Edit")){
			setEditTodo(todo);
		}else if(item.getTitle().equals("Mark as done")){
			db.makeComplete(id);
		}else if(item.getTitle().equals("Mark as undone")){
			db.undone(id);
		}
		getAllData();
		return true;
	}
	
	//Method for fetching all todos
	void getAllData(){
		items.clear();
		cur = db.getAllData();
		while(cur.moveToNext()){
			items.add(new Todo(cur.getString(0),cur.getString(1), cur.getString(2),cur.getString(3), cur.getString(4)));
		}
		TodoAdapter adapter = new TodoAdapter(this, items);
		todoListView.setAdapter(adapter);
		todoListView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
					Todo t = items.get(p3);
					Global.currentViewTodo = t;
					Toast.makeText(getApplicationContext(), t.title, Toast.LENGTH_LONG).show();
					Intent i = new Intent(MainActivity.this, ViewActivity.class);
					startActivity(i);
				}
			});
	}
	
	//Ads new todo
	void insertTodo(){
		String title = addTitle.getText().toString();
		String description = addDescription.getText().toString();
		int day = addDate.getDayOfMonth();
		int month = addDate.getMonth();
		int year = addDate.getYear();
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);

		if(title.length()==0||description.length()==0){
			Toast.makeText(getApplicationContext(),"All fields are required", Toast.LENGTH_LONG).show();
		    return;
		}
		db.insertData(title, description, calendar.getTime().toString());
		Toast.makeText(getApplicationContext(), "Task added successfully".toString(),Toast.LENGTH_LONG).show();
		getAllData();
		addDialog.hide();
	}
	
	// Method for editing
	void setEditTodo(Todo todo){
		currentEditId = todo.id;
		editTitle.setText(todo.title);
		editDescription.setText(todo.description);
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			Date da = new Date(todo.date);
			String formattedDate = df.format(da);
			String[] dateValues = formattedDate.split("-");
			editDate.updateDate(Integer.parseInt(dateValues[0]), Integer.parseInt(dateValues[1])-1, Integer.parseInt(dateValues[2]));
			editDialog.show();
		}catch(Exception e){
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
	}
	
	void saveEdit(){
		String title = editTitle.getText().toString();
		String description = editDescription.getText().toString();
		int day = editDate.getDayOfMonth();
		int month = editDate.getMonth();
		int year = editDate.getYear();
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		
		if(title.length()==0||description.length()==0){
			Toast.makeText(getApplicationContext(),"All fields are required", Toast.LENGTH_LONG).show();
		    return;
		}
		db.updateData(currentEditId, title, description, calendar.getTime().toString());
		editDialog.hide();
		getAllData();
	}
} 

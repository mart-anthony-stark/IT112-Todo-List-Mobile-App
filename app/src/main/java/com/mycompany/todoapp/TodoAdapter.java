package com.mycompany.todoapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class TodoAdapter extends ArrayAdapter<Todo>{
    public TodoAdapter(Context context, ArrayList<Todo> todos){
		super(context,0,todos);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Todo todo = getItem(position);
		if(convertView == null){
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
		}
		
		TextView title = convertView.findViewById(R.id.list_title);
		title.setText(todo.title);
		ImageView icon = convertView.findViewById(R.id.icon);
		
		int img = todo.status.equals("completed") ? R.drawable.completed : R.drawable.notcompleted;
		icon.setImageResource(img);
		
		
		return convertView;
	}
		
}

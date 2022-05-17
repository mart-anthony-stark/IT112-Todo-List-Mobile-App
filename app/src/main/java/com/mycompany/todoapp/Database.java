package com.mycompany.todoapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	public final static String DATABASE_NAME="Todo.db";
	public final static String TABLE_NAME="Todos";
	public final static String COL1="ID";
	public final static String COL2="title";
	public final static String COL3="description";
	public final static String COL4="date";
	public final static String COL5="status";

	private SQLiteDatabase db;

	private ContentValues cv;
	public Database(Context context){
		super(context, DATABASE_NAME,null,1);
		db=this.getWritableDatabase();
		cv = new ContentValues();
	}

	@Override 
    public void onCreate(SQLiteDatabase db) { 
		db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("+COL1+" INTEGER PRIMARY KEY AUTOINCREMENT,"+COL2+" TEXT COLLATE NOCASE,"+COL3+" TEXT COLLATE NOCASE,"+COL4+" TEXT, "+COL5+" TEXT)"); 
    } 

    @Override 
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME); 
		onCreate(db); 
    }

	public void insertData(String title, String desc, String date) { 
	    ContentValues c = new ContentValues();
		c.put(COL2, title); 
		c.put(COL3, desc); 
		c.put(COL4, date);
		c.put(COL5, "pending");
		db.insert(TABLE_NAME, null, c);
    } 

	public Cursor getData(String title){ 
		SQLiteDatabase db = this.getWritableDatabase(); 
		String query="SELECT * FROM "+TABLE_NAME+" WHERE "+COL2+"='"+title+"'"; 
		Cursor  cursor = db.rawQuery(query,null); 
		return cursor; 
    } 

	public Cursor searchData(String search){ 
		SQLiteDatabase db = this.getWritableDatabase(); 
		String query="SELECT * FROM "+TABLE_NAME+" WHERE "+COL2+"='"+search+"' OR "+COL3+"='"+search+"' COLLATE NOCASE"; 
		Cursor  cursor = db.rawQuery(query,null); 
		return cursor; 
    } 

	public Cursor getAllData() { 
		Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME, null); 
		return res; 
    } 

	public void updateData(String id,String title, String desc, String date) { 
//		cv.put(COL1, id); 
//		cv.put(COL2, title); 
//		cv.put(COL3, desc); 
//		cv.put(COL4, date); 
//		cv.put(COL5, status);
//		db.update(TABLE_NAME, cv, "ID=?", new String[]{id});
		db.execSQL("UPDATE "+TABLE_NAME+" SET title='"+title+"', description='"+desc+"', date='"+date+"' WHERE ID='"+id+"'");
    }
	
	public void makeComplete(String id){
		db.execSQL("UPDATE "+TABLE_NAME+" SET status='completed' WHERE id='"+id+"'");
	}
	
	public void undone(String id){
		db.execSQL("UPDATE "+TABLE_NAME+" SET status='pending' WHERE id='"+id+"'");
	}

	public void deleteAll(){
		db.execSQL("DELETE FROM "+TABLE_NAME);
	}

	public void deleteOne(String id){
		db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE "+COL1+"='"+id+"'");
	}
}

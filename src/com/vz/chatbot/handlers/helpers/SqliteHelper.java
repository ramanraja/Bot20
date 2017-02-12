package com.vz.chatbot.handlers.helpers;

import java.util.HashMap;

import com.vz.chatbot.androidbot.helpers.G;

import android.content.ContentValues;  
import android.content.Context;  
import android.database.Cursor;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;  

/**
 * Provides simple database services using SQLite
 * @author Raja
 */
public class SqliteHelper extends SQLiteOpenHelper{

	public static final String DB_NAME = "chatbotDB";
	public static final int DB_VERSION = 1;
	
	public SqliteHelper(Context context) {
		// Context context, String name, CursorFactory factory, int version
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sqlStr = "create table chatbot (bot text, name text, value text)";
		db.execSQL(sqlStr);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sqlStr = "drop table if exists chatbot";
		db.execSQL(sqlStr);
		onCreate(db); 
	}
	
	public void insert(String botName, String param, String value) {
		SQLiteDatabase db = this.getWritableDatabase(); 
	    ContentValues content = new ContentValues();  
	    content.put("bot", botName);  
	    content.put("name", param);  
	    content.put("value", value);
        db.insert("chatbot", null, content);  //2nd argument is nullColumnHack 
        db.close(); //no need  ? 
	}
	
	public String get(String botName, String param) {
		SQLiteDatabase db = this.getReadableDatabase(); 
		String sqlStr = "select value from chatbot where bot=\'" +botName +"\' and name=\'" +param +"\'";
		Cursor cursor = db.rawQuery(sqlStr, null);
		String result = null;
		if(cursor.moveToFirst())
			result = cursor.getString(0);
		cursor.close();
        db.close(); //no need  ?
        return result;
	}	
	
	public int count(String botName) {
		SQLiteDatabase db = this.getReadableDatabase(); 
		String sqlStr = "select count(*) from chatbot where bot=\'" +botName +"\'";
		Cursor cursor = db.rawQuery(sqlStr, null);
		int result = 0;
		if(cursor.moveToFirst())
			result = cursor.getInt(0);
		cursor.close();
        db.close(); //no need  ?
        return result;
	}	
	public HashMap<String, String> getAll(String botName) {
		SQLiteDatabase db = this.getReadableDatabase(); 
		String sqlStr = "select name,value from chatbot where bot=\'" +botName +"\'";
		Cursor cursor = db.rawQuery(sqlStr, null);
		HashMap<String, String> resultMap = new HashMap<String, String>();
		if(cursor.moveToFirst()) {
			do {
				resultMap.put(cursor.getString(0), cursor.getString(1));
			} while (cursor.moveToNext());
		}
		cursor.close();
        db.close();   
        return resultMap;
	}
	
	public void delete(String botName, String param) {
		SQLiteDatabase db = this.getWritableDatabase(); 
		String sqlStr = "delete from chatbot where bot=" +botName +" and name=" +param;
		db.execSQL(sqlStr);
        db.close();  
	}	
	
	public void zap () {
		G.trace("Zapping database !");
		SQLiteDatabase db = this.getWritableDatabase(); 
		String sqlStr = "drop table if exists chatbot";
		db.execSQL(sqlStr);
		onCreate(db); 	
	}
	
	public void close() {
		// TODO
	}
	
	public void test() {
		G.trace("Original count for bot1: " +count("bot1"));
		insert("bot1", "user", "Raja");
		insert("bot1", "city", "Chennai");
		insert("bot1", "phone", "9884302932");
		G.trace("Now count for bot1: " +count("bot1"));
		HashMap<String, String> map = getAll("bot1");
		G.dump(map);
	}
}
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openideals.android.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PreferenceDB  {

	private static final String CREATE_TABLE_PREFS = "create table prefs (pref_id integer primary key autoincrement, "
			+ "prefkey text not null, prefval text not null);";

	
	private static final String PREFS_TABLE = "prefs";
	private static final String DATABASE_NAME = "prefsdb";
	private static final int DATABASE_VERSION = 2;


    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	try
        	{
        		db.execSQL(CREATE_TABLE_PREFS);
        	}
        	catch (Exception e)
        	{
        		Log.i(DATABASE_NAME,"tables already exist");
        	}
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DATABASE_NAME, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS prefs");
           
            
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;
    
    private PreferenceDB (Context ctx)
    {
    	mOpenHelper = new DatabaseHelper(ctx);
    }

	public boolean insertPref(String key, String value) {
		
		deletePref(key);
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("prefkey", key);
		values.put("prefval", value);
		
		
		boolean resp = (db.insert(PREFS_TABLE, null, values) > 0);
		
		return resp;
	}

	
	public boolean deletePref(String prefkey) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		
		boolean resp = (db.delete(PREFS_TABLE, "prefkey=?", new String[]{prefkey}) > 0);
		
		db.close();
		
		return resp;
	}
	

	public String getPref(String key) {
	
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String result = null;
		
		try {
			
			
			Cursor c = db.query(PREFS_TABLE, new String[] {"prefval"  }, "prefkey=?", new String[] {key}, null, null, null);
			
			int numRows = c.getCount();
			
			if (numRows > 0)
			{
			c.moveToFirst();
			result = c.getString(0);
			}
			
			c.close();
			
		} catch (SQLException e) {
			Log.e("NewsDroid", e.toString());
		} catch (Exception e) {
			Log.e("NewsDroid", e.toString());
		}
		finally
		{
			db.close();
		}
		return result;
	}

	
	
	private static PreferenceDB prefDB = null;
	
	public static PreferenceDB getInstance (Context ctx)
	{
		if (prefDB == null)
		{
			prefDB = new PreferenceDB(ctx);
		}
		
		return prefDB;
	}

}

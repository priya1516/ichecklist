/*
 * Copyright 2008 Technobuff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


// Package
package net.technobuff.ichecklist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The checklist database adapter.
 * 
 * @author Nitin
 */
public class ChecklistDBAdapter {
  
  /** The row id key. */
  public static final String KEY_ROWID = "_id";
  
  /** The name key. */
  public static final String KEY_NAME = "name";
  
  /** The list id key. */
  public static final String KEY_LIST_ID = "list_id";
  
  /** The isDone key. */
  public static final String KEY_IS_DONE = "is_done";
  
  /** The item key. */
  public static final String KEY_ITEM = "item";
  
  /** The database name. */
  protected static final String DB_NAME = "data";
  
  /** The database version. */
  protected static final int DB_VERSION = 2;
  
  /** The tag. */
  protected static final String TAG = "CheckListDBAdapter";

  /** The checklist table. */
  protected static final String CHECKLIST_TBL = "checklist";
  
  /** The checklist item table. */
  protected static final String CHECKLIST_ITEM_TBL = "checklist_item";
  
  /** The checklist table create query. */
  protected static final String CHECKLIST_TBL_CREATE_QRY =
    "create table checklist(_id integer primary key autoincrement,"
    + "name text not null);";
  
  /** The checklist item table create query. */
  protected static final String CHECKLIST_ITEM_TBL_CREATE_QRY =
    "create table checklist_item(_id integer primary key autoincrement,"
    + "list_id integer not null, is_done boolean, item text not null);";

  /** The context. */
  protected Context mCtx;
  
  /** The database helper. */
  protected DBHelper mDbHelper;
  
  /** The database. */
  protected SQLiteDatabase mDb;
  
  
  /** Initializes the database adapter. */
  public ChecklistDBAdapter(Context ctx) {
    this.mCtx = ctx;
  }
  
  /**
   * Opens the checklist database. If it cannot be opened, it tries to create one.
   * If it cannot be created, an exception is thrown.
   * 
   * @return Returns the database adapter.
   * 
   * @exception SQLException if the database could not be created or opened.
   */
  public ChecklistDBAdapter open() throws SQLException {
    mDbHelper = new DBHelper(mCtx);
    mDb = mDbHelper.getWritableDatabase();
    return this;
  }
  
  /**
   * Closes the database adapter.
   */
  public void close() {
    if (mDbHelper != null) {
      mDbHelper.close();
    }
  }
  
  /**
   * Creates a checklist with the specified name.
   * 
   * @param name The checklist name.
   * 
   * @return Returns the id of the newly created list.
   */
  public long createChecklist(String name) {
    ContentValues values = new ContentValues();
    values.put(KEY_NAME, name);
    return mDb.insert(CHECKLIST_TBL, null, values);
  }
  
  /**
   * Creates a copy of the supplied checklist. The copy begins with "Copy of " source list name.
   * All the items in the newly created list are set to not done.
   * 
   * @param listId The source checklist id.
   * 
   * @return Returns the id of the newly created list.
   */
  public long copyChecklist(long listId) {
    long id = 0;
    String name;
    Cursor checklistCursor = null;
    Cursor checklistItemsCursor = null;
    int n;
    String item;
    
    try {
      checklistCursor = fetchChecklist(listId);
      name = "Copy of " + checklistCursor.getString(checklistCursor.getColumnIndexOrThrow(KEY_NAME));
      id = createChecklist(name);
      checklistItemsCursor = fetchAllChecklistItems(listId);
      n = checklistItemsCursor.getCount();
      if (checklistItemsCursor != null && n > 0) {
        checklistItemsCursor.moveToFirst();
        for (int i = 0; i < n; i++) {
          item = checklistItemsCursor.getString(checklistItemsCursor.getColumnIndexOrThrow(KEY_ITEM));
          createChecklistItem(id, item);
          checklistItemsCursor.moveToNext();
        }
      }
    }
    finally {
      try {
        if (checklistCursor != null) {
          checklistCursor.close();
        }
      }
      catch(Exception ex) {}
      try {
        if (checklistItemsCursor != null) {
          checklistItemsCursor.close();
        }
      }
      catch(Exception ex) {}
    }
    
    return id;
  }
  
  /**
   * Creates a checklist item for the specified list.
   * 
   * @param listId The list id.
   * @param item The item.
   * 
   * @return Returns the id of the newly created item.
   */
  public long createChecklistItem(long listId, String item) {
    ContentValues values = new ContentValues();
    values.put(KEY_LIST_ID, listId);
    values.put(KEY_IS_DONE, false);
    values.put(KEY_ITEM, item);
    return mDb.insert(CHECKLIST_ITEM_TBL, null, values);
  }
  
  /**
   * Retrieves all the checklists.
   */
  public Cursor fetchAllChecklists() {
    return mDb.query(CHECKLIST_TBL, new String[] {KEY_ROWID, KEY_NAME},
        null, null, null, null, null);
  }
  
  /**
   * Returns the specified checklist.
   * 
   * @param listId The checklist id.
   */
  public Cursor fetchChecklist(long listId) {
    Cursor cursor = mDb.query(true, CHECKLIST_TBL, new String[] {KEY_ROWID, KEY_NAME},
        KEY_ROWID + "=" + listId,  null, null, null, null, null);
    if (cursor != null) {
      cursor.moveToFirst();
    }
    return cursor;
  }
  
  /**
   * Retrieves all the items for the specified checklist.
   * 
   * @param listId The list id.
   */
  public Cursor fetchAllChecklistItems(long listId) {
    return mDb.query(CHECKLIST_ITEM_TBL, new String[] {KEY_ROWID, KEY_IS_DONE, KEY_ITEM},
        KEY_LIST_ID + "=" + listId, null, null, null, null);
  }
  
  /**
   * Retrieves the specified checklist item.
   * 
   * @param itemId The item id.
   */
  public Cursor fetchChecklistItem(long itemId) {
    Cursor cursor = mDb.query(true, CHECKLIST_ITEM_TBL, new String[] {KEY_ROWID, KEY_IS_DONE, KEY_ITEM},
        KEY_ROWID + "=" + itemId, null, null, null, null, null);
    if (cursor != null) {
      cursor.moveToFirst();
    }
    return cursor;
  }
  
  /**
   * Updates the specified checklist.
   * 
   * @param listId The id of the checklist to update.
   * @param name The checklist name.
   * 
   * @return Returns true if the update was successful. Otherwise, false.
   */
  public boolean updateChecklist(long listId, String name) {
    ContentValues values = new ContentValues();
    values.put(KEY_NAME, name);
    return mDb.update(CHECKLIST_TBL, values, KEY_ROWID + "=" + listId, null) > 0;
  }
  
  /**
   * Updates the specified checklist item.
   * 
   * @param itemId The id of the checklist item to update.
   * @param item The checklist item.
   * @param isDone Whether the checklist item is done.
   * 
   * @return Returns true if the update was successful. Otherwise, false.
   */
  public boolean updateChecklistItem(long itemId, String item, boolean isDone) {
    ContentValues values = new ContentValues();
    values.put(KEY_ITEM, item);
    values.put(KEY_IS_DONE, isDone);
    return mDb.update(CHECKLIST_ITEM_TBL, values, KEY_ROWID + "=" + itemId, null) > 0;
  }
  
  /**
   * Deletes the specified checklist.
   * 
   * @param listId The id of the checklist to delete.
   * 
   * @return Returns true if the deletion was successful. Otherwise, false.
   */
  public boolean deleteChecklist(long listId) {
    boolean status = deleteChecklistItems(listId);
    if (status) {
      status = mDb.delete(CHECKLIST_TBL, KEY_ROWID + "=" + listId, null) > 0;
    }
    return status;
  }
  
  /**
   * Deletes the items for the specified checklist.
   * 
   * @param listId The list id.
   * 
   * @return Returns true if the deletion was successful. Otherwise, false.
   */
  public boolean deleteChecklistItems(long listId) {
    return mDb.delete(CHECKLIST_ITEM_TBL, KEY_LIST_ID + "=" + listId, null) > 0;
  }
  
  /** The database helper. */
  protected static class DBHelper extends SQLiteOpenHelper {
    
    public DBHelper(Context context) {
      super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(CHECKLIST_TBL_CREATE_QRY);
      db.execSQL(CHECKLIST_ITEM_TBL_CREATE_QRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, "Upgrading database '" + DB_NAME + "' from version " + oldVersion
           + " to version " + newVersion + ", which will erase all old data.");
      db.execSQL("DROP TABLE if exists " + CHECKLIST_TBL);
      db.execSQL("DROP TABLE if exists " + CHECKLIST_ITEM_TBL);
      onCreate(db);
    }
  }
}

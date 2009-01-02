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

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * The checklist activity.
 *
 * @author Nitin
 */
public class ChecklistEdit extends Activity {

  /** The create activity. */
  protected static final int ACTIVITY_CREATE = 0;

  /** The edit activity. */
  protected static final int ACTIVITY_EDIT = 1;

  /** The insert menu id. */
  protected static final int INSERT_ID = Menu.FIRST;

  /** The delete menu id. */
  protected static final int DELETE_ID = INSERT_ID + 1;

  /** The database helper. */
  protected ChecklistDBAdapter mDbHelper;

  /** The list row id. */
  protected Long mListRowId;

  /** The name control. */
  protected EditText mNameControl;

  /** The items control. */
  protected ListView mItemsControl;

  /** The save control. */
  protected Button mSaveControl;


  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.checklist_edit);
    mDbHelper = new ChecklistDBAdapter(this);
    mDbHelper.open();

    // Initialize controls
    mNameControl = (EditText) findViewById(R.id.name);
    mItemsControl = (ListView) findViewById(R.id.items);
    mSaveControl = (Button) findViewById(R.id.save);

    // Initialize data
    mListRowId = (savedInstanceState != null) ?
        savedInstanceState.getLong(ChecklistDBAdapter.KEY_ROWID) : null;
    if (mListRowId == null) {
      Bundle extras = getIntent().getExtras();
      mListRowId = (extras != null) ? extras.getLong(ChecklistDBAdapter.KEY_ROWID) : null;
    }
    populateFields();

    // Listeners
    mSaveControl.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        setResult(RESULT_OK);
        finish();
      }
    });
  }

  /**
   * Populates the fields with the data.
   */
  protected void populateFields() {
    if (mListRowId != null) {
      Cursor checklistCursor = mDbHelper.fetchChecklist(mListRowId);
      Cursor checklistItemsCursor = mDbHelper.fetchAllChecklistItems(mListRowId);
      String[] from = {ChecklistDBAdapter.KEY_ITEM};
      int[] to = {R.id.text1};
      SimpleCursorAdapter checklistItems;

      startManagingCursor(checklistCursor);
      mNameControl.setText(checklistCursor.getString(
          checklistCursor.getColumnIndexOrThrow(ChecklistDBAdapter.KEY_NAME)));
      startManagingCursor(checklistItemsCursor);
      checklistItems = new SimpleCursorAdapter(this, R.layout.checklist_items,
          checklistItemsCursor, from, to);
      mItemsControl.setAdapter(checklistItems);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    menu.add(0, INSERT_ID, 0, R.string.menu_add_item);
    menu.add(0, DELETE_ID, 0, R.string.menu_delete_item);
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    boolean retval = false;

    switch(item.getItemId()) {
    case INSERT_ID:
      retval = true;
    }

    return retval;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(ChecklistDBAdapter.KEY_ROWID, mListRowId);
  }

  @Override
  protected void onPause() {
    super.onPause();
    saveState();
  }

  @Override
  protected void onResume() {
    super.onResume();
    populateFields();
  }

  /**
   * Saves state.
   */
  protected void saveState() {
    String name = mNameControl.getText().toString();
    int nItems = mItemsControl.getChildCount();

    if (mListRowId == null) {
      long id = mDbHelper.createCheckList(name);
      if (id > 0) {
        mListRowId = id;
      }
    }
    else {
      //      mDbHelper.updateChecklist(mListRowId, name);
    }
  }
}
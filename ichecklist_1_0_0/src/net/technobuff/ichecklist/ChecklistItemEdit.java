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
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * The checklist item edit activity.
 *
 * @author Nitin
 */
public class ChecklistItemEdit extends Activity {

  /** The tag. */
  protected static final String TAG = "ChecklistItemEdit";
  
  /** The database helper. */
  protected ChecklistDBAdapter mDbHelper;

  /** The associated list id. */
  protected Long mListId;
  
  /** The item row id. */
  protected Long mItemRowId;
  
  /** The is done control. */
  protected CheckBox mIsDoneControl;

  /** The item control. */
  protected EditText mItemControl;

  /** The save control. */
  protected ImageButton mSaveControl;

  /** The cancel control. */
  protected ImageButton mCancelControl;
  
  /** Whether the activity is cancelled. */
  protected boolean isCancelled;
  

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.checklist_item_edit);
    mDbHelper = new ChecklistDBAdapter(this);
    mDbHelper.open();

    // Initialize controls
    mIsDoneControl = (CheckBox) findViewById(R.id.is_done);
    mItemControl = (EditText) findViewById(R.id.item);
    mSaveControl = (ImageButton) findViewById(R.id.save);
    mCancelControl = (ImageButton) findViewById(R.id.cancel);

    // Initialize data
    mItemRowId = (savedInstanceState != null) ?
        savedInstanceState.getLong(ChecklistDBAdapter.KEY_ROWID) : null;
    if (mItemRowId == null) {
      Bundle extras = getIntent().getExtras();
      if (extras != null) {
        if (extras.containsKey(ChecklistDBAdapter.KEY_ROWID)) {
          mItemRowId = extras.getLong(ChecklistDBAdapter.KEY_ROWID);
        }
        if (extras.containsKey(ChecklistDBAdapter.KEY_LIST_ID)) {
          mListId = extras.getLong(ChecklistDBAdapter.KEY_LIST_ID);
        }
      }
    }
    populateFields();

    // Listeners
    mSaveControl.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        setResult(RESULT_OK);
        finish();
      }
    });
    mCancelControl.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        setResult(RESULT_CANCELED);
        isCancelled = true;
        finish();
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mDbHelper != null) {
      mDbHelper.close();
    }
  }

  /**
   * Populates the fields with the data.
   */
  protected void populateFields() {
    if (mItemRowId != null) {
      Cursor cursor = mDbHelper.fetchChecklistItem(mItemRowId);
      String isDoneStr;
      boolean isDone;

      startManagingCursor(cursor);
      isDoneStr = cursor.getString(cursor.getColumnIndexOrThrow(ChecklistDBAdapter.KEY_IS_DONE));
      isDone = "1".equals(isDoneStr);
      mIsDoneControl.setChecked(isDone);
      mItemControl.setText(cursor.getString(
          cursor.getColumnIndexOrThrow(ChecklistDBAdapter.KEY_ITEM)));
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(ChecklistDBAdapter.KEY_ROWID, mItemRowId);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (!isCancelled) {
      saveState();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    isCancelled = false;
    populateFields();
  }

  /**
   * Saves state.
   */
  protected void saveState() {
    String item = mItemControl.getText().toString();
    boolean isDone = mIsDoneControl.isChecked();

    if (mItemRowId == null) {
      long id = mDbHelper.createChecklistItem(mListId, item, isDone);
      if (id > 0) {
        mItemRowId = id;
      }
      else {
        Log.e(TAG, "Could not create item '" + item + "'.");
      }
    }
    else {
      if (!mDbHelper.updateChecklistItem(mItemRowId, item, isDone)) {
        Log.e(TAG, "Could not update item '" + item + "'.");
      }
    }
  }
}
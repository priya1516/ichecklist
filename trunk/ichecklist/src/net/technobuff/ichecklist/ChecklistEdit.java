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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * The checklist edit activity.
 *
 * @author Nitin
 */
public class ChecklistEdit extends Activity {

  /** The checklist edit activity. */
  protected static final String TAG = "ChecklistEdit";
  
  /** The create activity. */
  protected static final int ACTIVITY_CREATE = 0;

  /** The edit activity. */
  protected static final int ACTIVITY_EDIT = 1;

  /** The insert menu id. */
  protected static final int INSERT_ID = Menu.FIRST;

  /** The copy menu id. */
  protected static final int COPY_ID = INSERT_ID + 1;
  
  /** The delete menu id. */
  protected static final int DELETE_ID = INSERT_ID + 2;

  /** The database helper. */
  protected ChecklistDBAdapter mDbHelper;

  /** The list row id. */
  protected Long mListRowId;

  /** The name control. */
  protected EditText mNameControl;

  /** The items control. */
  protected ListView mItemsControl;

  /** The save control. */
  protected ImageButton mSaveControl;

  /** The cancel control. */
  protected ImageButton mCancelControl;
  
  /** Whether the activity is cancelled. */
  protected boolean isCancelled;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.checklist_edit);
    mDbHelper = new ChecklistDBAdapter(this);
    mDbHelper.open();

    // Initialize controls
    mNameControl = (EditText) findViewById(R.id.name);
    mItemsControl = (ListView) findViewById(R.id.items);
    mSaveControl = (ImageButton) findViewById(R.id.save);
    mCancelControl = (ImageButton) findViewById(R.id.cancel);
    mItemsControl.setEmptyView(findViewById(R.id.no_items)); // View to show when no items exist

    // Initialize data
    mListRowId = (savedInstanceState != null) ?
        savedInstanceState.getLong(ChecklistDBAdapter.KEY_ROWID) : null;
    if (mListRowId == null) {
      Bundle extras = getIntent().getExtras();
      mListRowId = (extras != null) ? extras.getLong(ChecklistDBAdapter.KEY_ROWID) : null;
    }
    populateFields();

    // Listeners
    mItemsControl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Intent intent = new Intent(getBaseContext(), ChecklistItemEdit.class);
        intent.putExtra(ChecklistDBAdapter.KEY_ROWID, id);
        intent.putExtra(ChecklistDBAdapter.KEY_LIST_ID, mListRowId);
        startActivityForResult(intent, ACTIVITY_EDIT);
      }
    });
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
    if (mListRowId != null) {
      Cursor checklistCursor = mDbHelper.fetchChecklist(mListRowId);
      Cursor checklistItemsCursor = mDbHelper.fetchChecklistItems(mListRowId);
      String[] from = {ChecklistDBAdapter.KEY_IS_DONE, ChecklistDBAdapter.KEY_ITEM};
      int[] to = {R.id.item_is_done, R.id.item_text};
      ChecklistItemAdapter checklistItems;
      
      startManagingCursor(checklistCursor);
      mNameControl.setText(checklistCursor.getString(
          checklistCursor.getColumnIndexOrThrow(ChecklistDBAdapter.KEY_NAME)));
      startManagingCursor(checklistItemsCursor);
            
      checklistItems = new ChecklistItemAdapter(this, R.layout.checklist_item_row,
          checklistItemsCursor, from, to);
      mItemsControl.setAdapter(checklistItems);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    MenuItem item1 = menu.add(0, INSERT_ID, 0,R.string.menu_add_item);
    {
      item1.setAlphabeticShortcut('a');
      item1.setIcon( R.drawable.add);
    }
    MenuItem item2 = menu.add(0, COPY_ID, 0, R.string.menu_copy_item);
    {
      item2.setAlphabeticShortcut('c');
      item2.setIcon( R.drawable.copy);
    }
    MenuItem item3 = menu.add(0, DELETE_ID, 0, R.string.menu_delete_item);
    {
      item3.setAlphabeticShortcut('d');
      item3.setIcon( R.drawable.delete);
    }
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    boolean retval = false;

    switch(item.getItemId()) {
      case INSERT_ID:
        createChecklistItem();
        retval = true;
        break;
      case COPY_ID:
        copyChecklistItem(mItemsControl.getSelectedItemId());
        retval = true;
        break;
      case DELETE_ID:
        final long itemId = mItemsControl.getSelectedItemId();
        if(itemId > 0) {
          new AlertDialog.Builder(this)
          .setTitle(R.string.confirm)
          .setMessage(R.string.confirm_delete)
          .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
  
            public void onClick(DialogInterface dialog, int which) {
              // TODO Auto-generated method stub 
              mDbHelper.deleteChecklistItem(itemId);
              populateFields();
              setResult(RESULT_OK);
            }
          })
          .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
  
            public void onClick(DialogInterface dialog, int which) {
              // Do nothing
            }
            
          })
          .show();
  //        populateFields();
          retval = true;
        }
        break;
      default:
        retval = super.onMenuItemSelected(featureId, item);
        break;
    }

    return retval;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, 
      Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    populateFields();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(ChecklistDBAdapter.KEY_ROWID, mListRowId);
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
   * Creates a checklist item.
   */
  protected void createChecklistItem() {
    if (mListRowId != null) {
      Intent intent = new Intent(this, ChecklistItemEdit.class);
      intent.putExtra(ChecklistDBAdapter.KEY_LIST_ID, mListRowId);
      startActivityForResult(intent, ACTIVITY_CREATE);
    }
    else {
      Log.e(TAG, "Trying to add item to non-existing checklist.");
    }
  }
  
  /**
   * Creates a copy of the selected checklistitem.
   * <p/>
   * @param id The source checklistitem id.
   */
  protected void copyChecklistItem(long id) {
    Intent intent;
    
    if (id > 0) {
      // Copy checklist
      id = mDbHelper.copyChecklistItem(id);
      
      // Edit it now
      intent = new Intent(this, ChecklistItemEdit.class);
      intent.putExtra(ChecklistDBAdapter.KEY_ROWID, id);
      startActivityForResult(intent, ACTIVITY_EDIT);
    }
  }
  
  /**
   * Saves state.
   */
  protected void saveState() {
    String name = mNameControl.getText().toString();

    if (mListRowId == null) {
      long id = mDbHelper.createChecklist(name);
      if (id > 0) {
        mListRowId = id;
      }
      else {
        Log.e(TAG, "Could not create checklist '" + name + "'.");
      }
    }
    else {
      if (!mDbHelper.updateChecklist(mListRowId, name)) {
        Log.e(TAG, "Could not update checklist '" + name + "'.");
      }
    }
  }
}
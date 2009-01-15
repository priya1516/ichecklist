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

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * The checklist activity.
 *
 * @author Nitin
 */
public class Checklist extends ListActivity {
  
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
  

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.checklists);
    mDbHelper = new ChecklistDBAdapter(this);
    mDbHelper.open();
    fillData();
  }

  /**
   * Populates the view with all the checklists.
   */
  protected void fillData() {
    Cursor checklistCursor = mDbHelper.fetchAllCheckLists();
    String[] from = {ChecklistDBAdapter.KEY_NAME};
    int[] to = {R.id.checklist_name_text};
    ChecklistAdapter checklists;
    
    startManagingCursor(checklistCursor);
    checklists = new ChecklistAdapter(this, R.layout.checklist_row, checklistCursor,
        from, to);
    setListAdapter(checklists);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
     
    MenuItem item1 = menu.add(0, INSERT_ID, 0,R.string.menu_add_checklist);
    {
      item1.setAlphabeticShortcut('a');
      item1.setIcon( R.drawable.add);
    }
    MenuItem item2 = menu.add(0, DELETE_ID, 0, R.string.menu_delete_checklist);
    {
      item2.setAlphabeticShortcut('d');
      item2.setIcon( R.drawable.delete);
    }
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    boolean retval = false;
    
    switch(item.getItemId()) {
      case INSERT_ID:
        createChecklist();
        retval = true;
        break;
      case DELETE_ID:
        mDbHelper.deleteChecklist(getListView().getSelectedItemId());
        fillData();
        retval = true;
        break;
      default:
        retval = super.onMenuItemSelected(featureId, item);
        break;
    }
    
    return retval;
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Intent intent = new Intent(this, ChecklistEdit.class);
    intent.putExtra(ChecklistDBAdapter.KEY_ROWID, id);
    startActivityForResult(intent, ACTIVITY_EDIT);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, 
      Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    fillData();
  }

  /**
   * Creates a checklist.
   */
  protected void createChecklist() {
    Intent intent = new Intent(this, ChecklistEdit.class);
    startActivityForResult(intent, ACTIVITY_CREATE);
  }
}
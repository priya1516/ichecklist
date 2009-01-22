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
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * The checklist adapter.
 * <p/>
 * @author Kavita
 */
public class ChecklistAdapter extends SimpleCursorAdapter {
  private TextView text;
  private Context context;
  private Cursor cursor;

  public ChecklistAdapter(Activity context, int task_row, Cursor tasks2, String[] from, int[] to) { 
    super(context , task_row,tasks2, from, to); 
    this.cursor = cursor;
    this.context = context;
  } 

  public View getView(int position, View convertView, ViewGroup parent) { 
    View row = super.getView(position, convertView, parent); 
    TextView text = (TextView)row.findViewById(R.id.checklist_name_text);
    text.setTextColor(new ColorStateList(
        new int[][] { 
            new int[] { android.R.attr.state_selected}, 
            new int[0], 
        }, new int[] { 
            Color.parseColor("#D20D2A"),
            Color.BLACK, 
        } 
    ));    
    if(position % 2 == 0) {
      row.setBackgroundColor (Color.rgb(170, 172, 240) ); 
    } 
    else {
      row.setBackgroundColor(Color.WHITE);
    }
    return row;
  }
}

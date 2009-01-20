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

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * The checklist adapter.
 * <p/>
 * @author Kavita
 */
public class ChecklistItemAdapter extends SimpleCursorAdapter {
  private Cursor cursor;
  private Context context;
  public ChecklistItemAdapter(Context context, int task_row, Cursor cursor, String[] from, int[] to) { 
    super(context , task_row, cursor, from, to);
    this.cursor = cursor;
    this.context = context;
  } 

  public View getView(int position, View convertView, ViewGroup parent) { 
    super.getItem(position);
    
    RowWithIconView rowIcon;
    rowIcon = new RowWithIconView(context, position);
    return rowIcon;
  }
  
  class RowWithIconView extends LinearLayout {
    ImageView icon;
    TextView text;
    Context context;
    protected static final int ACTIVITY_EDIT = 1;
    public RowWithIconView(Context context) {
      super(context);
      this.context = context;
    }
    
    public RowWithIconView(Context context, int position) {
      this(context);
      this.setOrientation(HORIZONTAL);
      boolean isDone = "1".equals(cursor.getString(cursor.getColumnIndexOrThrow(ChecklistDBAdapter.KEY_IS_DONE)));
      text = new TextView(context);
      text.setText(cursor.getString(2));
      text.setTextColor(Color.BLACK);
      text.setPadding(5, 5, 5, 5);
      text.setTextSize(15);
      text.setTextColor(new ColorStateList(
          new int[][] { 
              new int[] { android.R.attr.state_selected }, 
              new int[0], 
          }, new int[] { 
              Color.parseColor("#D20D2A"), 
              Color.BLACK, 
          } 
));
      if(isDone) {
        icon = new ImageView(context);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.done));
        // left, top, right, bottom 
        icon.setPadding(5, 5, 5, 5); // 2px up, 5px to the right 
        addView(icon, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      }
      else {
        text.setPadding(30, 5, 5, 5);
      }
      
      addView(text, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      
      if(position % 2 == 0) {
        this.setBackgroundColor (Color.rgb(170, 172, 240) ); 
      } 
      else {
        this.setBackgroundColor(Color.WHITE);
      }
    }
  }
}

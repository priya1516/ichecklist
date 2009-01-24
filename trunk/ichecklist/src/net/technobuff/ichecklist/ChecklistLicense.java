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

import static net.technobuff.ichecklist.ChecklistCommon.IS_LICENSE_ACCEPTED;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * The checklist license activity.
 *
 * @author Nitin
 */
public class ChecklistLicense extends Activity {
  
  /** The license control. */
  protected TextView licenseControl;
  
  /** The accept control. */
  protected Button acceptControl;
  
  /** The reject control. */
  protected Button rejectControl;
  

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    showLicense();
  }

  public void showLicense() {
    final SharedPreferences preferences = ChecklistCommon.getPreferences();
    setContentView(R.layout.checklist_license);
    licenseControl= (TextView) findViewById(R.id.license);
    acceptControl= (Button) findViewById(R.id.accept);
    rejectControl = (Button) findViewById(R.id.reject);
    Linkify.addLinks(licenseControl, Linkify.WEB_URLS);
    rejectControl.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        finish();
      }
    });
    acceptControl.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putBoolean(IS_LICENSE_ACCEPTED, true);
        preferencesEditor.commit();
        finish();
      }
    });
  }
}
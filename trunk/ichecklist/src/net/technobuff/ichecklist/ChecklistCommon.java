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

import android.content.SharedPreferences;


/**
 * The class to provide common functionality, such as, constants and data sharing.
 *
 * @author Nitin
 */
public class ChecklistCommon {
  
  /** The is license accepted preference key. */
  public static final String IS_LICENSE_ACCEPTED = "isLicenseAccepted";
  
  /** The preferences used throughout checklist. */
  protected static SharedPreferences preferences;
  
  
  /**
   * Returns the preferences common to the entire checklist application.
   */
  public static SharedPreferences getPreferences() {
    return preferences;
  }
  
  /**
   * Sets the preferences common to the entire checklist application.
   * <p/>
   * @param prefs The preferences.
   */
  public static void setPreferences(SharedPreferences prefs) {
    preferences = prefs;
  }
}
package com.sentaca.dbpreferences;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TestActivity extends Activity {
  protected static final String PROPERTY_NAME = "just_a_name";
  private DatabaseBasedSharedPreferences preferences;
  private CheckBox checkBox;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    preferences = new DatabaseBasedSharedPreferences(this);

    checkBox = (CheckBox) findViewById(R.id.checkBox1);
    checkBox.setChecked(preferences.getBoolean(PROPERTY_NAME, false));

    checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        preferences.putBoolean(PROPERTY_NAME, isChecked);
      }
    });
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
      checkBox.setChecked(preferences.getBoolean(PROPERTY_NAME, false));
    }
  }
}
package com.sentaca.dbpreferences;

public class Preferences extends DataObject {

  private static final long serialVersionUID = -9139442711579311726L;

  public static final String TABLE_NAME = "preferences";
  public static final String COLUMN_NAME = "pref_name";
  public static final String COLUMN_VALUE = "pref_value";

  public final static String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_NAME
      + " text not null unique, " + COLUMN_VALUE + " text)";

  private String name;
  private String value;

  public Preferences(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setValue(String value) {
    this.value = value;
  }
}

package com.sentaca.dbpreferences;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class DatabaseBasedSharedPreferences {
  interface PropertyChangeListener {
    void handleNewValue(Context context, String property, String value);

    boolean supports(String property);
  }

  public Context context;
  private DaoAdapter<Preferences> dao;

  private final PropertyChangeListener[] listeners;

  DatabaseBasedSharedPreferences(Context context, PropertyChangeListener... listeners) {
    this.context = context;
    this.listeners = listeners;
    dao = new DaoAdapter<Preferences>(context) {

      @Override
      public List<Preferences> convertFromCursor(Cursor c) {
        int indexId = c.getColumnIndex(Preferences.COLUMN_ID);
        int indexName = c.getColumnIndex(Preferences.COLUMN_NAME);
        int indexValue = c.getColumnIndex(Preferences.COLUMN_VALUE);

        final List<Preferences> list = new ArrayList<Preferences>();
        if (c != null) {
          if (c.moveToFirst()) {
            do {
              final Preferences p = new Preferences(c.getString(indexName), c.isNull(indexValue) ? null : c.getString(indexValue));
              p.setId(c.getLong(indexId));
              list.add(p);
            } while (c.moveToNext());
          }
        }

        return list;
      }

      @Override
      public ContentValues convertToContentValues(Preferences t) {
        ContentValues cv = new ContentValues();
        cv.put(Preferences.COLUMN_NAME, t.getName());
        cv.put(Preferences.COLUMN_VALUE, t.getValue());
        return cv;
      }

      @Override
      public String getTableName() {
        return Preferences.TABLE_NAME;
      }
    };
  }

  boolean getBoolean(String property, boolean defaultValue) {
    final Preferences p = getPreferences(property);
    if (p == null || p.getValue() == null) {
      return defaultValue;
    }
    return Boolean.valueOf(p.getValue());
  }

  long getLong(String property, long defaultValue) {
    final Preferences p = getPreferences(property);
    if (p == null || p.getValue() == null) {
      return defaultValue;
    }
    return Long.valueOf(p.getValue());
  }

  private Preferences getPreferences(final String property) {
    return dao.doInTransaction(new DoInTransaction<Preferences>() {

      @Override
      public Preferences doInTransaction(SQLiteDatabase db) {
        final Cursor query = db.query(Preferences.TABLE_NAME, null, Preferences.COLUMN_NAME + " =  ? ", new String[] { property }, null, null, null);
        try {
          final List<Preferences> list = dao.convertFromCursor(query);
          return singleResult(list);
        } finally {
          query.close();
        }
      }
    });
  }

  String getString(String property, String defaultValue) {
    final Preferences p = getPreferences(property);
    if (p == null || p.getValue() == null) {
      return defaultValue;
    }
    return p.getValue();
  }

  void putBoolean(String property, boolean value) {
    putProperty(property, String.valueOf(value));
  }

  void putLong(String property, long value) {
    putProperty(property, String.valueOf(value));
  }

  private void putProperty(final String property, final String value) {
    dao.doInTransaction(new DoInTransaction<Void>() {

      @Override
      public Void doInTransaction(SQLiteDatabase db) {
        db.delete(Preferences.TABLE_NAME, Preferences.COLUMN_NAME + " = ?", new String[] { property });
        final Preferences p = new Preferences(property, value);
        db.insertOrThrow(Preferences.TABLE_NAME, null, dao.convertToContentValues(p));
        return null;
      }
    });

    for (PropertyChangeListener l : listeners) {
      if (l.supports(property)) {
        l.handleNewValue(context, property, value);
      }
    }
  }

  void putString(String property, String value) {
    putProperty(property, value);
  }

}
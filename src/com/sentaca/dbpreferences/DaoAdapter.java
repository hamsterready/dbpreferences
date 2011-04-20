package com.sentaca.dbpreferences;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class DaoAdapter<T extends DataObject> {

  private static class DatabaseHelper extends SQLiteOpenHelper {

    DatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      for (String query : DATABASE_CREATE) {
        db.execSQL(query);
      }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
      for (String table : DATABASE_TABLES) {
        db.execSQL("DROP TABLE IF EXISTS " + table);
      }
      onCreate(db);
    }
  }

  private static final String TAG = DaoAdapter.class.getSimpleName();
  public static final String DATABASE_NAME = "yourdb";
  private static final int DATABASE_VERSION = 1;
  private static final String[] DATABASE_TABLES = new String[] { Preferences.TABLE_NAME };
  protected final Context mCtx;

  /**
   * Database creation sql statement
   */
  private static final String[] DATABASE_CREATE = new String[] { Preferences.TABLE_CREATE };

  public DaoAdapter(Context ctx) {
    this.mCtx = ctx;
  }

  public abstract List<T> convertFromCursor(Cursor c);

  public abstract ContentValues convertToContentValues(T t);

  public <S> S doInTransaction(DoInTransaction<S> t) {
    DatabaseHelper mDbHelper = new DatabaseHelper(mCtx);
    SQLiteDatabase mDb = mDbHelper.getWritableDatabase();
    mDb.setLockingEnabled(true);
    mDb.beginTransaction();
    try {
      S result = t.doInTransaction(mDb);
      mDb.setTransactionSuccessful();
      return result;
    } finally {
      mDb.endTransaction();
      mDb.close();
      mDbHelper.close();
    }
  }

  public abstract String getTableName();

  public List<T> findAll() {
    return doInTransaction(new DoInTransaction<List<T>>() {

      @Override
      public List<T> doInTransaction(SQLiteDatabase db) {
        final Cursor c = db.query(getTableName(), null, null, null, null, null, null);
        try {
          return convertFromCursor(c);
        } finally {
          c.close();
        }
      }
    });
  }

  public int deleteAll() {
    return doInTransaction(new DoInTransaction<Integer>() {

      @Override
      public Integer doInTransaction(SQLiteDatabase db) {
        return deleteAll(db, DaoAdapter.this);
      }
    });
  }

}

package com.sentaca.dbpreferences;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public abstract class DoInTransaction<S> {
  public abstract S doInTransaction(SQLiteDatabase db);

  protected <T extends DataObject> int deleteAll(SQLiteDatabase db, DaoAdapter<T> dao) {
    return db.delete(dao.getTableName(), null, null);
  }

  protected <T extends DataObject> boolean add(SQLiteDatabase db, DaoAdapter<T> dao, T object) {
    long id = db.insertOrThrow(dao.getTableName(), null, dao.convertToContentValues(object));
    object.setId(id);
    return id > -1l;
  }
  
  protected <T extends DataObject> T singleResult(List<T> result) {
    if(result.size() > 0) {
      return result.get(0);
    }
    return null;
  }
}

package com.sentaca.dbpreferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class DataObject implements Serializable {

  private static final long serialVersionUID = -2928385608007018041L;
  public final static String COLUMN_ID = "_id";

  protected Long id;

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public static final <T extends DataObject> List<Long> getIds(List<T> objects) {
    List<Long> ids = new ArrayList<Long>();
    if (objects != null) {
      for (T obj : objects) {
        ids.add(obj.getId());
      }
    }
    return ids;
  }

}

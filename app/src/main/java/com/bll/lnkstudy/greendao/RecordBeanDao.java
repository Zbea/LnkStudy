package com.bll.lnkstudy.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bll.lnkstudy.mvp.model.RecordBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "RECORD_BEAN".
*/
public class RecordBeanDao extends AbstractDao<RecordBean, Long> {

    public static final String TABLENAME = "RECORD_BEAN";

    /**
     * Properties of entity RecordBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Title = new Property(1, String.class, "title", false, "TITLE");
        public final static Property Date = new Property(2, long.class, "date", false, "DATE");
        public final static Property Path = new Property(3, String.class, "path", false, "PATH");
        public final static Property CourseId = new Property(4, int.class, "courseId", false, "COURSE_ID");
    }


    public RecordBeanDao(DaoConfig config) {
        super(config);
    }
    
    public RecordBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"RECORD_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ," + // 0: id
                "\"TITLE\" TEXT," + // 1: title
                "\"DATE\" INTEGER NOT NULL ," + // 2: date
                "\"PATH\" TEXT," + // 3: path
                "\"COURSE_ID\" INTEGER NOT NULL );"); // 4: courseId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"RECORD_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, RecordBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
        stmt.bindLong(3, entity.getDate());
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(4, path);
        }
        stmt.bindLong(5, entity.getCourseId());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, RecordBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
        stmt.bindLong(3, entity.getDate());
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(4, path);
        }
        stmt.bindLong(5, entity.getCourseId());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public RecordBean readEntity(Cursor cursor, int offset) {
        RecordBean entity = new RecordBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.getLong(offset + 2), // date
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // path
            cursor.getInt(offset + 4) // courseId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, RecordBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDate(cursor.getLong(offset + 2));
        entity.setPath(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCourseId(cursor.getInt(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(RecordBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(RecordBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(RecordBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
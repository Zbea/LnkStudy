package com.bll.lnkstudy.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bll.lnkstudy.mvp.model.PaperContent;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PAPER_CONTENT".
*/
public class PaperContentDao extends AbstractDao<PaperContent, Long> {

    public static final String TABLENAME = "PAPER_CONTENT";

    /**
     * Properties of entity PaperContent.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, long.class, "userId", false, "USER_ID");
        public final static Property Type = new Property(2, int.class, "type", false, "TYPE");
        public final static Property CourseId = new Property(3, int.class, "courseId", false, "COURSE_ID");
        public final static Property CategoryId = new Property(4, int.class, "categoryId", false, "CATEGORY_ID");
        public final static Property ContentId = new Property(5, int.class, "contentId", false, "CONTENT_ID");
        public final static Property Date = new Property(6, long.class, "date", false, "DATE");
        public final static Property Path = new Property(7, String.class, "path", false, "PATH");
        public final static Property DrawPath = new Property(8, String.class, "drawPath", false, "DRAW_PATH");
        public final static Property Page = new Property(9, int.class, "page", false, "PAGE");
    }


    public PaperContentDao(DaoConfig config) {
        super(config);
    }
    
    public PaperContentDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PAPER_CONTENT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ," + // 0: id
                "\"USER_ID\" INTEGER NOT NULL ," + // 1: userId
                "\"TYPE\" INTEGER NOT NULL ," + // 2: type
                "\"COURSE_ID\" INTEGER NOT NULL ," + // 3: courseId
                "\"CATEGORY_ID\" INTEGER NOT NULL ," + // 4: categoryId
                "\"CONTENT_ID\" INTEGER NOT NULL ," + // 5: contentId
                "\"DATE\" INTEGER NOT NULL ," + // 6: date
                "\"PATH\" TEXT," + // 7: path
                "\"DRAW_PATH\" TEXT," + // 8: drawPath
                "\"PAGE\" INTEGER NOT NULL );"); // 9: page
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PAPER_CONTENT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PaperContent entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, entity.getType());
        stmt.bindLong(4, entity.getCourseId());
        stmt.bindLong(5, entity.getCategoryId());
        stmt.bindLong(6, entity.getContentId());
        stmt.bindLong(7, entity.getDate());
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(8, path);
        }
 
        String drawPath = entity.getDrawPath();
        if (drawPath != null) {
            stmt.bindString(9, drawPath);
        }
        stmt.bindLong(10, entity.getPage());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PaperContent entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, entity.getType());
        stmt.bindLong(4, entity.getCourseId());
        stmt.bindLong(5, entity.getCategoryId());
        stmt.bindLong(6, entity.getContentId());
        stmt.bindLong(7, entity.getDate());
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(8, path);
        }
 
        String drawPath = entity.getDrawPath();
        if (drawPath != null) {
            stmt.bindString(9, drawPath);
        }
        stmt.bindLong(10, entity.getPage());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public PaperContent readEntity(Cursor cursor, int offset) {
        PaperContent entity = new PaperContent( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // userId
            cursor.getInt(offset + 2), // type
            cursor.getInt(offset + 3), // courseId
            cursor.getInt(offset + 4), // categoryId
            cursor.getInt(offset + 5), // contentId
            cursor.getLong(offset + 6), // date
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // path
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // drawPath
            cursor.getInt(offset + 9) // page
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PaperContent entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.getLong(offset + 1));
        entity.setType(cursor.getInt(offset + 2));
        entity.setCourseId(cursor.getInt(offset + 3));
        entity.setCategoryId(cursor.getInt(offset + 4));
        entity.setContentId(cursor.getInt(offset + 5));
        entity.setDate(cursor.getLong(offset + 6));
        entity.setPath(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDrawPath(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setPage(cursor.getInt(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PaperContent entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PaperContent entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PaperContent entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
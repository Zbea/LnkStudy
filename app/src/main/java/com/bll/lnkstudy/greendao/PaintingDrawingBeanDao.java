package com.bll.lnkstudy.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PAINTING_DRAWING_BEAN".
*/
public class PaintingDrawingBeanDao extends AbstractDao<PaintingDrawingBean, Long> {

    public static final String TABLENAME = "PAINTING_DRAWING_BEAN";

    /**
     * Properties of entity PaintingDrawingBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, long.class, "userId", false, "USER_ID");
        public final static Property Type = new Property(2, int.class, "type", false, "TYPE");
        public final static Property Date = new Property(3, long.class, "date", false, "DATE");
        public final static Property Path = new Property(4, String.class, "path", false, "PATH");
        public final static Property Title = new Property(5, String.class, "title", false, "TITLE");
        public final static Property BgRes = new Property(6, String.class, "bgRes", false, "BG_RES");
        public final static Property CloudId = new Property(7, int.class, "cloudId", false, "CLOUD_ID");
    }


    public PaintingDrawingBeanDao(DaoConfig config) {
        super(config);
    }
    
    public PaintingDrawingBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PAINTING_DRAWING_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ," + // 0: id
                "\"USER_ID\" INTEGER NOT NULL ," + // 1: userId
                "\"TYPE\" INTEGER NOT NULL ," + // 2: type
                "\"DATE\" INTEGER NOT NULL ," + // 3: date
                "\"PATH\" TEXT," + // 4: path
                "\"TITLE\" TEXT," + // 5: title
                "\"BG_RES\" TEXT," + // 6: bgRes
                "\"CLOUD_ID\" INTEGER NOT NULL );"); // 7: cloudId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PAINTING_DRAWING_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PaintingDrawingBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, entity.getType());
        stmt.bindLong(4, entity.getDate());
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(5, path);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(6, title);
        }
 
        String bgRes = entity.getBgRes();
        if (bgRes != null) {
            stmt.bindString(7, bgRes);
        }
        stmt.bindLong(8, entity.getCloudId());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PaintingDrawingBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, entity.getType());
        stmt.bindLong(4, entity.getDate());
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(5, path);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(6, title);
        }
 
        String bgRes = entity.getBgRes();
        if (bgRes != null) {
            stmt.bindString(7, bgRes);
        }
        stmt.bindLong(8, entity.getCloudId());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public PaintingDrawingBean readEntity(Cursor cursor, int offset) {
        PaintingDrawingBean entity = new PaintingDrawingBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // userId
            cursor.getInt(offset + 2), // type
            cursor.getLong(offset + 3), // date
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // path
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // title
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // bgRes
            cursor.getInt(offset + 7) // cloudId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PaintingDrawingBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.getLong(offset + 1));
        entity.setType(cursor.getInt(offset + 2));
        entity.setDate(cursor.getLong(offset + 3));
        entity.setPath(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTitle(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setBgRes(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCloudId(cursor.getInt(offset + 7));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PaintingDrawingBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PaintingDrawingBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PaintingDrawingBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

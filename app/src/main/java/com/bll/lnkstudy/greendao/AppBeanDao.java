package com.bll.lnkstudy.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bll.lnkstudy.mvp.model.AppBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "APP_BEAN".
*/
public class AppBeanDao extends AbstractDao<AppBean, Long> {

    public static final String TABLENAME = "APP_BEAN";

    /**
     * Properties of entity AppBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, long.class, "userId", false, "USER_ID");
        public final static Property AppId = new Property(2, int.class, "appId", false, "APP_ID");
        public final static Property AppName = new Property(3, String.class, "appName", false, "APP_NAME");
        public final static Property PackageName = new Property(4, String.class, "packageName", false, "PACKAGE_NAME");
    }


    public AppBeanDao(DaoConfig config) {
        super(config);
    }
    
    public AppBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"APP_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ," + // 0: id
                "\"USER_ID\" INTEGER NOT NULL ," + // 1: userId
                "\"APP_ID\" INTEGER NOT NULL ," + // 2: appId
                "\"APP_NAME\" TEXT," + // 3: appName
                "\"PACKAGE_NAME\" TEXT);"); // 4: packageName
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"APP_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AppBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, entity.getAppId());
 
        String appName = entity.getAppName();
        if (appName != null) {
            stmt.bindString(4, appName);
        }
 
        String packageName = entity.getPackageName();
        if (packageName != null) {
            stmt.bindString(5, packageName);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AppBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, entity.getAppId());
 
        String appName = entity.getAppName();
        if (appName != null) {
            stmt.bindString(4, appName);
        }
 
        String packageName = entity.getPackageName();
        if (packageName != null) {
            stmt.bindString(5, packageName);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AppBean readEntity(Cursor cursor, int offset) {
        AppBean entity = new AppBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // userId
            cursor.getInt(offset + 2), // appId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // appName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // packageName
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AppBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.getLong(offset + 1));
        entity.setAppId(cursor.getInt(offset + 2));
        entity.setAppName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPackageName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AppBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AppBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AppBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
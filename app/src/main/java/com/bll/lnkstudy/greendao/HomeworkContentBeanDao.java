package com.bll.lnkstudy.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bll.lnkstudy.mvp.model.HomeworkContentBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "HOMEWORK_CONTENT_BEAN".
*/
public class HomeworkContentBeanDao extends AbstractDao<HomeworkContentBean, Long> {

    public static final String TABLENAME = "HOMEWORK_CONTENT_BEAN";

    /**
     * Properties of entity HomeworkContentBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, long.class, "userId", false, "USER_ID");
        public final static Property BgResId = new Property(2, String.class, "bgResId", false, "BG_RES_ID");
        public final static Property Course = new Property(3, String.class, "course", false, "COURSE");
        public final static Property HomeworkTypeId = new Property(4, int.class, "homeworkTypeId", false, "HOMEWORK_TYPE_ID");
        public final static Property Title = new Property(5, String.class, "title", false, "TITLE");
        public final static Property State = new Property(6, int.class, "state", false, "STATE");
        public final static Property Date = new Property(7, long.class, "date", false, "DATE");
        public final static Property CommitDate = new Property(8, long.class, "commitDate", false, "COMMIT_DATE");
        public final static Property FolderPath = new Property(9, String.class, "folderPath", false, "FOLDER_PATH");
        public final static Property FilePath = new Property(10, String.class, "filePath", false, "FILE_PATH");
        public final static Property PathName = new Property(11, String.class, "pathName", false, "PATH_NAME");
        public final static Property Page = new Property(12, int.class, "page", false, "PAGE");
    }


    public HomeworkContentBeanDao(DaoConfig config) {
        super(config);
    }
    
    public HomeworkContentBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"HOMEWORK_CONTENT_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ," + // 0: id
                "\"USER_ID\" INTEGER NOT NULL ," + // 1: userId
                "\"BG_RES_ID\" TEXT," + // 2: bgResId
                "\"COURSE\" TEXT," + // 3: course
                "\"HOMEWORK_TYPE_ID\" INTEGER NOT NULL ," + // 4: homeworkTypeId
                "\"TITLE\" TEXT," + // 5: title
                "\"STATE\" INTEGER NOT NULL ," + // 6: state
                "\"DATE\" INTEGER NOT NULL ," + // 7: date
                "\"COMMIT_DATE\" INTEGER NOT NULL ," + // 8: commitDate
                "\"FOLDER_PATH\" TEXT," + // 9: folderPath
                "\"FILE_PATH\" TEXT," + // 10: filePath
                "\"PATH_NAME\" TEXT," + // 11: pathName
                "\"PAGE\" INTEGER NOT NULL );"); // 12: page
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"HOMEWORK_CONTENT_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, HomeworkContentBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
 
        String bgResId = entity.getBgResId();
        if (bgResId != null) {
            stmt.bindString(3, bgResId);
        }
 
        String course = entity.getCourse();
        if (course != null) {
            stmt.bindString(4, course);
        }
        stmt.bindLong(5, entity.getHomeworkTypeId());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(6, title);
        }
        stmt.bindLong(7, entity.getState());
        stmt.bindLong(8, entity.getDate());
        stmt.bindLong(9, entity.getCommitDate());
 
        String folderPath = entity.getFolderPath();
        if (folderPath != null) {
            stmt.bindString(10, folderPath);
        }
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(11, filePath);
        }
 
        String pathName = entity.getPathName();
        if (pathName != null) {
            stmt.bindString(12, pathName);
        }
        stmt.bindLong(13, entity.getPage());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, HomeworkContentBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
 
        String bgResId = entity.getBgResId();
        if (bgResId != null) {
            stmt.bindString(3, bgResId);
        }
 
        String course = entity.getCourse();
        if (course != null) {
            stmt.bindString(4, course);
        }
        stmt.bindLong(5, entity.getHomeworkTypeId());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(6, title);
        }
        stmt.bindLong(7, entity.getState());
        stmt.bindLong(8, entity.getDate());
        stmt.bindLong(9, entity.getCommitDate());
 
        String folderPath = entity.getFolderPath();
        if (folderPath != null) {
            stmt.bindString(10, folderPath);
        }
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(11, filePath);
        }
 
        String pathName = entity.getPathName();
        if (pathName != null) {
            stmt.bindString(12, pathName);
        }
        stmt.bindLong(13, entity.getPage());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public HomeworkContentBean readEntity(Cursor cursor, int offset) {
        HomeworkContentBean entity = new HomeworkContentBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // bgResId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // course
            cursor.getInt(offset + 4), // homeworkTypeId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // title
            cursor.getInt(offset + 6), // state
            cursor.getLong(offset + 7), // date
            cursor.getLong(offset + 8), // commitDate
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // folderPath
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // filePath
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // pathName
            cursor.getInt(offset + 12) // page
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, HomeworkContentBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.getLong(offset + 1));
        entity.setBgResId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCourse(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setHomeworkTypeId(cursor.getInt(offset + 4));
        entity.setTitle(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setState(cursor.getInt(offset + 6));
        entity.setDate(cursor.getLong(offset + 7));
        entity.setCommitDate(cursor.getLong(offset + 8));
        entity.setFolderPath(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setFilePath(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setPathName(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setPage(cursor.getInt(offset + 12));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(HomeworkContentBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(HomeworkContentBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(HomeworkContentBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
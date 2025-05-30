package com.bll.lnkstudy.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "HOMEWORK_TYPE_BEAN".
*/
public class HomeworkTypeBeanDao extends AbstractDao<HomeworkTypeBean, Long> {

    public static final String TABLENAME = "HOMEWORK_TYPE_BEAN";

    /**
     * Properties of entity HomeworkTypeBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property StudentId = new Property(1, long.class, "studentId", false, "STUDENT_ID");
        public final static Property TeacherId = new Property(2, Long.class, "teacherId", false, "TEACHER_ID");
        public final static Property Teacher = new Property(3, String.class, "teacher", false, "TEACHER");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property Grade = new Property(5, int.class, "grade", false, "GRADE");
        public final static Property TypeId = new Property(6, int.class, "typeId", false, "TYPE_ID");
        public final static Property State = new Property(7, int.class, "state", false, "STATE");
        public final static Property Date = new Property(8, long.class, "date", false, "DATE");
        public final static Property ContentResId = new Property(9, String.class, "contentResId", false, "CONTENT_RES_ID");
        public final static Property BgResId = new Property(10, String.class, "bgResId", false, "BG_RES_ID");
        public final static Property Course = new Property(11, String.class, "course", false, "COURSE");
        public final static Property BookId = new Property(12, int.class, "bookId", false, "BOOK_ID");
        public final static Property CreateStatus = new Property(13, int.class, "createStatus", false, "CREATE_STATUS");
        public final static Property FromStatus = new Property(14, int.class, "fromStatus", false, "FROM_STATUS");
        public final static Property MessageTotal = new Property(15, int.class, "messageTotal", false, "MESSAGE_TOTAL");
        public final static Property IsCloud = new Property(16, boolean.class, "isCloud", false, "IS_CLOUD");
        public final static Property AutoState = new Property(17, int.class, "autoState", false, "AUTO_STATE");
    }


    public HomeworkTypeBeanDao(DaoConfig config) {
        super(config);
    }
    
    public HomeworkTypeBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"HOMEWORK_TYPE_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ," + // 0: id
                "\"STUDENT_ID\" INTEGER NOT NULL ," + // 1: studentId
                "\"TEACHER_ID\" INTEGER," + // 2: teacherId
                "\"TEACHER\" TEXT," + // 3: teacher
                "\"NAME\" TEXT," + // 4: name
                "\"GRADE\" INTEGER NOT NULL ," + // 5: grade
                "\"TYPE_ID\" INTEGER NOT NULL ," + // 6: typeId
                "\"STATE\" INTEGER NOT NULL ," + // 7: state
                "\"DATE\" INTEGER NOT NULL ," + // 8: date
                "\"CONTENT_RES_ID\" TEXT," + // 9: contentResId
                "\"BG_RES_ID\" TEXT," + // 10: bgResId
                "\"COURSE\" TEXT," + // 11: course
                "\"BOOK_ID\" INTEGER NOT NULL ," + // 12: bookId
                "\"CREATE_STATUS\" INTEGER NOT NULL ," + // 13: createStatus
                "\"FROM_STATUS\" INTEGER NOT NULL ," + // 14: fromStatus
                "\"MESSAGE_TOTAL\" INTEGER NOT NULL ," + // 15: messageTotal
                "\"IS_CLOUD\" INTEGER NOT NULL ," + // 16: isCloud
                "\"AUTO_STATE\" INTEGER NOT NULL );"); // 17: autoState
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"HOMEWORK_TYPE_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, HomeworkTypeBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getStudentId());
 
        Long teacherId = entity.getTeacherId();
        if (teacherId != null) {
            stmt.bindLong(3, teacherId);
        }
 
        String teacher = entity.getTeacher();
        if (teacher != null) {
            stmt.bindString(4, teacher);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
        stmt.bindLong(6, entity.getGrade());
        stmt.bindLong(7, entity.getTypeId());
        stmt.bindLong(8, entity.getState());
        stmt.bindLong(9, entity.getDate());
 
        String contentResId = entity.getContentResId();
        if (contentResId != null) {
            stmt.bindString(10, contentResId);
        }
 
        String bgResId = entity.getBgResId();
        if (bgResId != null) {
            stmt.bindString(11, bgResId);
        }
 
        String course = entity.getCourse();
        if (course != null) {
            stmt.bindString(12, course);
        }
        stmt.bindLong(13, entity.getBookId());
        stmt.bindLong(14, entity.getCreateStatus());
        stmt.bindLong(15, entity.getFromStatus());
        stmt.bindLong(16, entity.getMessageTotal());
        stmt.bindLong(17, entity.getIsCloud() ? 1L: 0L);
        stmt.bindLong(18, entity.getAutoState());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, HomeworkTypeBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getStudentId());
 
        Long teacherId = entity.getTeacherId();
        if (teacherId != null) {
            stmt.bindLong(3, teacherId);
        }
 
        String teacher = entity.getTeacher();
        if (teacher != null) {
            stmt.bindString(4, teacher);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
        stmt.bindLong(6, entity.getGrade());
        stmt.bindLong(7, entity.getTypeId());
        stmt.bindLong(8, entity.getState());
        stmt.bindLong(9, entity.getDate());
 
        String contentResId = entity.getContentResId();
        if (contentResId != null) {
            stmt.bindString(10, contentResId);
        }
 
        String bgResId = entity.getBgResId();
        if (bgResId != null) {
            stmt.bindString(11, bgResId);
        }
 
        String course = entity.getCourse();
        if (course != null) {
            stmt.bindString(12, course);
        }
        stmt.bindLong(13, entity.getBookId());
        stmt.bindLong(14, entity.getCreateStatus());
        stmt.bindLong(15, entity.getFromStatus());
        stmt.bindLong(16, entity.getMessageTotal());
        stmt.bindLong(17, entity.getIsCloud() ? 1L: 0L);
        stmt.bindLong(18, entity.getAutoState());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public HomeworkTypeBean readEntity(Cursor cursor, int offset) {
        HomeworkTypeBean entity = new HomeworkTypeBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // studentId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // teacherId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // teacher
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.getInt(offset + 5), // grade
            cursor.getInt(offset + 6), // typeId
            cursor.getInt(offset + 7), // state
            cursor.getLong(offset + 8), // date
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // contentResId
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // bgResId
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // course
            cursor.getInt(offset + 12), // bookId
            cursor.getInt(offset + 13), // createStatus
            cursor.getInt(offset + 14), // fromStatus
            cursor.getInt(offset + 15), // messageTotal
            cursor.getShort(offset + 16) != 0, // isCloud
            cursor.getInt(offset + 17) // autoState
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, HomeworkTypeBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setStudentId(cursor.getLong(offset + 1));
        entity.setTeacherId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setTeacher(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setGrade(cursor.getInt(offset + 5));
        entity.setTypeId(cursor.getInt(offset + 6));
        entity.setState(cursor.getInt(offset + 7));
        entity.setDate(cursor.getLong(offset + 8));
        entity.setContentResId(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setBgResId(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setCourse(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setBookId(cursor.getInt(offset + 12));
        entity.setCreateStatus(cursor.getInt(offset + 13));
        entity.setFromStatus(cursor.getInt(offset + 14));
        entity.setMessageTotal(cursor.getInt(offset + 15));
        entity.setIsCloud(cursor.getShort(offset + 16) != 0);
        entity.setAutoState(cursor.getInt(offset + 17));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(HomeworkTypeBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(HomeworkTypeBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(HomeworkTypeBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

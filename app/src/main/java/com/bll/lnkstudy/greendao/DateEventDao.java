package com.bll.lnkstudy.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bll.lnkstudy.utils.greendao.DatePlanConverter;
import com.bll.lnkstudy.utils.greendao.DateRemindConverter;
import java.util.List;

import com.bll.lnkstudy.mvp.model.DateEvent;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DATE_EVENT".
*/
public class DateEventDao extends AbstractDao<DateEvent, Long> {

    public static final String TABLENAME = "DATE_EVENT";

    /**
     * Properties of entity DateEvent.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Type = new Property(1, int.class, "type", false, "TYPE");
        public final static Property Title = new Property(2, String.class, "title", false, "TITLE");
        public final static Property DayLong = new Property(3, Long.class, "dayLong", false, "DAY_LONG");
        public final static Property DayLongStr = new Property(4, String.class, "dayLongStr", false, "DAY_LONG_STR");
        public final static Property Explain = new Property(5, String.class, "explain", false, "EXPLAIN");
        public final static Property StartTime = new Property(6, Long.class, "startTime", false, "START_TIME");
        public final static Property EndTime = new Property(7, Long.class, "endTime", false, "END_TIME");
        public final static Property StartTimeStr = new Property(8, String.class, "startTimeStr", false, "START_TIME_STR");
        public final static Property EndTimeStr = new Property(9, String.class, "endTimeStr", false, "END_TIME_STR");
        public final static Property List = new Property(10, String.class, "list", false, "LIST");
        public final static Property RemindList = new Property(11, String.class, "remindList", false, "REMIND_LIST");
        public final static Property Repeat = new Property(12, String.class, "repeat", false, "REPEAT");
    }

    private final DatePlanConverter listConverter = new DatePlanConverter();
    private final DateRemindConverter remindListConverter = new DateRemindConverter();

    public DateEventDao(DaoConfig config) {
        super(config);
    }
    
    public DateEventDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DATE_EVENT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ," + // 0: id
                "\"TYPE\" INTEGER NOT NULL ," + // 1: type
                "\"TITLE\" TEXT," + // 2: title
                "\"DAY_LONG\" INTEGER," + // 3: dayLong
                "\"DAY_LONG_STR\" TEXT," + // 4: dayLongStr
                "\"EXPLAIN\" TEXT," + // 5: explain
                "\"START_TIME\" INTEGER," + // 6: startTime
                "\"END_TIME\" INTEGER," + // 7: endTime
                "\"START_TIME_STR\" TEXT," + // 8: startTimeStr
                "\"END_TIME_STR\" TEXT," + // 9: endTimeStr
                "\"LIST\" TEXT," + // 10: list
                "\"REMIND_LIST\" TEXT," + // 11: remindList
                "\"REPEAT\" TEXT);"); // 12: repeat
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DATE_EVENT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DateEvent entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getType());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        Long dayLong = entity.getDayLong();
        if (dayLong != null) {
            stmt.bindLong(4, dayLong);
        }
 
        String dayLongStr = entity.getDayLongStr();
        if (dayLongStr != null) {
            stmt.bindString(5, dayLongStr);
        }
 
        String explain = entity.getExplain();
        if (explain != null) {
            stmt.bindString(6, explain);
        }
 
        Long startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(7, startTime);
        }
 
        Long endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindLong(8, endTime);
        }
 
        String startTimeStr = entity.getStartTimeStr();
        if (startTimeStr != null) {
            stmt.bindString(9, startTimeStr);
        }
 
        String endTimeStr = entity.getEndTimeStr();
        if (endTimeStr != null) {
            stmt.bindString(10, endTimeStr);
        }
 
        List list = entity.getList();
        if (list != null) {
            stmt.bindString(11, listConverter.convertToDatabaseValue(list));
        }
 
        List remindList = entity.getRemindList();
        if (remindList != null) {
            stmt.bindString(12, remindListConverter.convertToDatabaseValue(remindList));
        }
 
        String repeat = entity.getRepeat();
        if (repeat != null) {
            stmt.bindString(13, repeat);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DateEvent entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getType());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        Long dayLong = entity.getDayLong();
        if (dayLong != null) {
            stmt.bindLong(4, dayLong);
        }
 
        String dayLongStr = entity.getDayLongStr();
        if (dayLongStr != null) {
            stmt.bindString(5, dayLongStr);
        }
 
        String explain = entity.getExplain();
        if (explain != null) {
            stmt.bindString(6, explain);
        }
 
        Long startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(7, startTime);
        }
 
        Long endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindLong(8, endTime);
        }
 
        String startTimeStr = entity.getStartTimeStr();
        if (startTimeStr != null) {
            stmt.bindString(9, startTimeStr);
        }
 
        String endTimeStr = entity.getEndTimeStr();
        if (endTimeStr != null) {
            stmt.bindString(10, endTimeStr);
        }
 
        List list = entity.getList();
        if (list != null) {
            stmt.bindString(11, listConverter.convertToDatabaseValue(list));
        }
 
        List remindList = entity.getRemindList();
        if (remindList != null) {
            stmt.bindString(12, remindListConverter.convertToDatabaseValue(remindList));
        }
 
        String repeat = entity.getRepeat();
        if (repeat != null) {
            stmt.bindString(13, repeat);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DateEvent readEntity(Cursor cursor, int offset) {
        DateEvent entity = new DateEvent( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // type
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // title
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // dayLong
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // dayLongStr
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // explain
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // startTime
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // endTime
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // startTimeStr
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // endTimeStr
            cursor.isNull(offset + 10) ? null : listConverter.convertToEntityProperty(cursor.getString(offset + 10)), // list
            cursor.isNull(offset + 11) ? null : remindListConverter.convertToEntityProperty(cursor.getString(offset + 11)), // remindList
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12) // repeat
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DateEvent entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setType(cursor.getInt(offset + 1));
        entity.setTitle(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDayLong(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setDayLongStr(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setExplain(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setStartTime(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setEndTime(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setStartTimeStr(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setEndTimeStr(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setList(cursor.isNull(offset + 10) ? null : listConverter.convertToEntityProperty(cursor.getString(offset + 10)));
        entity.setRemindList(cursor.isNull(offset + 11) ? null : remindListConverter.convertToEntityProperty(cursor.getString(offset + 11)));
        entity.setRepeat(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DateEvent entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DateEvent entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DateEvent entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

package com.bll.lnkstudy.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.bll.lnkstudy.mvp.model.homework.HomeworkBookCorrectBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "HOMEWORK_BOOK_CORRECT_BEAN".
*/
public class HomeworkBookCorrectBeanDao extends AbstractDao<HomeworkBookCorrectBean, Long> {

    public static final String TABLENAME = "HOMEWORK_BOOK_CORRECT_BEAN";

    /**
     * Properties of entity HomeworkBookCorrectBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, long.class, "userId", false, "USER_ID");
        public final static Property BookId = new Property(2, int.class, "bookId", false, "BOOK_ID");
        public final static Property HomeworkTitle = new Property(3, String.class, "homeworkTitle", false, "HOMEWORK_TITLE");
        public final static Property Page = new Property(4, int.class, "page", false, "PAGE");
        public final static Property State = new Property(5, int.class, "state", false, "STATE");
        public final static Property CorrectMode = new Property(6, int.class, "correctMode", false, "CORRECT_MODE");
        public final static Property ScoreMode = new Property(7, int.class, "scoreMode", false, "SCORE_MODE");
        public final static Property Score = new Property(8, int.class, "score", false, "SCORE");
        public final static Property AnswerUrl = new Property(9, String.class, "answerUrl", false, "ANSWER_URL");
        public final static Property CorrectJson = new Property(10, String.class, "correctJson", false, "CORRECT_JSON");
        public final static Property IsSelfCorrect = new Property(11, boolean.class, "isSelfCorrect", false, "IS_SELF_CORRECT");
        public final static Property CommitJson = new Property(12, String.class, "commitJson", false, "COMMIT_JSON");
        public final static Property StartTime = new Property(13, Long.class, "startTime", false, "START_TIME");
    }


    public HomeworkBookCorrectBeanDao(DaoConfig config) {
        super(config);
    }
    
    public HomeworkBookCorrectBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"HOMEWORK_BOOK_CORRECT_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ," + // 0: id
                "\"USER_ID\" INTEGER NOT NULL ," + // 1: userId
                "\"BOOK_ID\" INTEGER NOT NULL ," + // 2: bookId
                "\"HOMEWORK_TITLE\" TEXT," + // 3: homeworkTitle
                "\"PAGE\" INTEGER NOT NULL ," + // 4: page
                "\"STATE\" INTEGER NOT NULL ," + // 5: state
                "\"CORRECT_MODE\" INTEGER NOT NULL ," + // 6: correctMode
                "\"SCORE_MODE\" INTEGER NOT NULL ," + // 7: scoreMode
                "\"SCORE\" INTEGER NOT NULL ," + // 8: score
                "\"ANSWER_URL\" TEXT," + // 9: answerUrl
                "\"CORRECT_JSON\" TEXT," + // 10: correctJson
                "\"IS_SELF_CORRECT\" INTEGER NOT NULL ," + // 11: isSelfCorrect
                "\"COMMIT_JSON\" TEXT," + // 12: commitJson
                "\"START_TIME\" INTEGER);"); // 13: startTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"HOMEWORK_BOOK_CORRECT_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, HomeworkBookCorrectBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, entity.getBookId());
 
        String homeworkTitle = entity.getHomeworkTitle();
        if (homeworkTitle != null) {
            stmt.bindString(4, homeworkTitle);
        }
        stmt.bindLong(5, entity.getPage());
        stmt.bindLong(6, entity.getState());
        stmt.bindLong(7, entity.getCorrectMode());
        stmt.bindLong(8, entity.getScoreMode());
        stmt.bindLong(9, entity.getScore());
 
        String answerUrl = entity.getAnswerUrl();
        if (answerUrl != null) {
            stmt.bindString(10, answerUrl);
        }
 
        String correctJson = entity.getCorrectJson();
        if (correctJson != null) {
            stmt.bindString(11, correctJson);
        }
        stmt.bindLong(12, entity.getIsSelfCorrect() ? 1L: 0L);
 
        String commitJson = entity.getCommitJson();
        if (commitJson != null) {
            stmt.bindString(13, commitJson);
        }
 
        Long startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(14, startTime);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, HomeworkBookCorrectBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUserId());
        stmt.bindLong(3, entity.getBookId());
 
        String homeworkTitle = entity.getHomeworkTitle();
        if (homeworkTitle != null) {
            stmt.bindString(4, homeworkTitle);
        }
        stmt.bindLong(5, entity.getPage());
        stmt.bindLong(6, entity.getState());
        stmt.bindLong(7, entity.getCorrectMode());
        stmt.bindLong(8, entity.getScoreMode());
        stmt.bindLong(9, entity.getScore());
 
        String answerUrl = entity.getAnswerUrl();
        if (answerUrl != null) {
            stmt.bindString(10, answerUrl);
        }
 
        String correctJson = entity.getCorrectJson();
        if (correctJson != null) {
            stmt.bindString(11, correctJson);
        }
        stmt.bindLong(12, entity.getIsSelfCorrect() ? 1L: 0L);
 
        String commitJson = entity.getCommitJson();
        if (commitJson != null) {
            stmt.bindString(13, commitJson);
        }
 
        Long startTime = entity.getStartTime();
        if (startTime != null) {
            stmt.bindLong(14, startTime);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public HomeworkBookCorrectBean readEntity(Cursor cursor, int offset) {
        HomeworkBookCorrectBean entity = new HomeworkBookCorrectBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // userId
            cursor.getInt(offset + 2), // bookId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // homeworkTitle
            cursor.getInt(offset + 4), // page
            cursor.getInt(offset + 5), // state
            cursor.getInt(offset + 6), // correctMode
            cursor.getInt(offset + 7), // scoreMode
            cursor.getInt(offset + 8), // score
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // answerUrl
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // correctJson
            cursor.getShort(offset + 11) != 0, // isSelfCorrect
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // commitJson
            cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13) // startTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, HomeworkBookCorrectBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.getLong(offset + 1));
        entity.setBookId(cursor.getInt(offset + 2));
        entity.setHomeworkTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPage(cursor.getInt(offset + 4));
        entity.setState(cursor.getInt(offset + 5));
        entity.setCorrectMode(cursor.getInt(offset + 6));
        entity.setScoreMode(cursor.getInt(offset + 7));
        entity.setScore(cursor.getInt(offset + 8));
        entity.setAnswerUrl(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setCorrectJson(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setIsSelfCorrect(cursor.getShort(offset + 11) != 0);
        entity.setCommitJson(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setStartTime(cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(HomeworkBookCorrectBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(HomeworkBookCorrectBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(HomeworkBookCorrectBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
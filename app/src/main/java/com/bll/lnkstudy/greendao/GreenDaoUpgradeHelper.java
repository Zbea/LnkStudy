package com.bll.lnkstudy.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.database.Database;

public class GreenDaoUpgradeHelper extends DaoMaster.OpenHelper {

    public GreenDaoUpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }
    //这里重写onUpgrade方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                    @Override
                    public void onCreateAllTables(Database db, boolean ifNotExists) {
                        onCreate(db);
                    }

                    @Override
                    public void onDropAllTables(Database db, boolean ifExists) {
                        DaoMaster.dropAllTables(db, true);
                    }

                }, AppBeanDao.class,
                BaseTypeBeanDao.class,
                BookDao.class,
                CourseBeanDao.class,
                DateEventDao.class,
                HomeworkContentDao.class,
                HomeworkTypeDao.class,
                NoteDao.class,
                PaintingBeanDao.class,
                PaperDao.class,
                RecordBeanDao.class,
                WallpaperBeanDao.class
        );
    }
}
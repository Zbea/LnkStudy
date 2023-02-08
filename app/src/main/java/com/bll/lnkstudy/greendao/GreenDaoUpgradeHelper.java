package com.bll.lnkstudy.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.database.Database;

public class GreenDaoUpgradeHelper extends DaoMaster.DevOpenHelper{

    public GreenDaoUpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }
    //这里重写onUpgrade方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                    @Override
                    public void onCreateAllTables(Database db, boolean ifNotExists) {
                        DaoMaster.createAllTables(db,ifNotExists);
                    }

                    @Override
                    public void onDropAllTables(Database db, boolean ifExists) {
                        DaoMaster.dropAllTables(db, true);
                    }

                }, AppBeanDao.class,
                BaseTypeBeanDao.class,
                BookBeanDao.class,
                CourseBeanDao.class,
                DateEventBeanDao.class,
                HomeworkContentBeanDao.class,
                HomeworkTypeBeanDao.class,
                NoteContentBeanDao.class,
                PaintingDrawingBeanDao.class,
                PaperBeanDao.class,
                PaperTypeBeanDao.class,
                PaperContentBeanDao.class,
                RecordBeanDao.class,
                PaintingBeanDao.class
        );
    }
}
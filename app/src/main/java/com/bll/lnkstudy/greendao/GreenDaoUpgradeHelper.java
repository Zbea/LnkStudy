package com.bll.lnkstudy.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bll.lnkstudy.mvp.model.DiaryBean;
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
                NoteDao.class,
                BookBeanDao.class,
                DateEventBeanDao.class,
                HomeworkContentBeanDao.class,
                HomeworkTypeBeanDao.class,
                NoteContentBeanDao.class,
                PaintingDrawingBeanDao.class,
                PaperTypeBeanDao.class,
                PaperBeanDao.class,
                PaperContentBeanDao.class,
                RecordBeanDao.class,
                PaintingBeanDao.class,
                DataUpdateBeanDao.class,
                HomeworkBookBeanDao.class,
                FreeNoteBeanDao.class,
                DiaryBeanDao.class,
                ItemTypeBeanDao.class,
                HomeworkDetailsBeanDao.class,
                TextbookBeanDao.class,
                CalenderItemBeanDao.class,
                HomeworkBookCorrectBeanDao.class
        );
    }
}
package com.bll.lnkstudy.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.bll.lnkstudy.mvp.model.AppBean;
import com.bll.lnkstudy.mvp.model.CalenderItemBean;
import com.bll.lnkstudy.mvp.model.DataUpdateBean;
import com.bll.lnkstudy.mvp.model.DiaryBean;
import com.bll.lnkstudy.mvp.model.FreeNoteBean;
import com.bll.lnkstudy.mvp.model.ItemTypeBean;
import com.bll.lnkstudy.mvp.model.RecordBean;
import com.bll.lnkstudy.mvp.model.book.BookBean;
import com.bll.lnkstudy.mvp.model.book.TextbookBean;
import com.bll.lnkstudy.mvp.model.date.DateEventBean;
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean;
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookCorrectBean;
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean;
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperBean;
import com.bll.lnkstudy.mvp.model.homework.HomeworkShareBean;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.note.Note;
import com.bll.lnkstudy.mvp.model.note.NoteContentBean;
import com.bll.lnkstudy.mvp.model.painting.PaintingBean;
import com.bll.lnkstudy.mvp.model.painting.PaintingDrawingBean;
import com.bll.lnkstudy.mvp.model.paper.PaperBean;
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean;

import com.bll.lnkstudy.greendao.AppBeanDao;
import com.bll.lnkstudy.greendao.CalenderItemBeanDao;
import com.bll.lnkstudy.greendao.DataUpdateBeanDao;
import com.bll.lnkstudy.greendao.DiaryBeanDao;
import com.bll.lnkstudy.greendao.FreeNoteBeanDao;
import com.bll.lnkstudy.greendao.ItemTypeBeanDao;
import com.bll.lnkstudy.greendao.RecordBeanDao;
import com.bll.lnkstudy.greendao.BookBeanDao;
import com.bll.lnkstudy.greendao.TextbookBeanDao;
import com.bll.lnkstudy.greendao.DateEventBeanDao;
import com.bll.lnkstudy.greendao.HomeworkBookBeanDao;
import com.bll.lnkstudy.greendao.HomeworkBookCorrectBeanDao;
import com.bll.lnkstudy.greendao.HomeworkContentBeanDao;
import com.bll.lnkstudy.greendao.HomeworkPaperBeanDao;
import com.bll.lnkstudy.greendao.HomeworkShareBeanDao;
import com.bll.lnkstudy.greendao.HomeworkTypeBeanDao;
import com.bll.lnkstudy.greendao.NoteDao;
import com.bll.lnkstudy.greendao.NoteContentBeanDao;
import com.bll.lnkstudy.greendao.PaintingBeanDao;
import com.bll.lnkstudy.greendao.PaintingDrawingBeanDao;
import com.bll.lnkstudy.greendao.PaperBeanDao;
import com.bll.lnkstudy.greendao.PaperTypeBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig appBeanDaoConfig;
    private final DaoConfig calenderItemBeanDaoConfig;
    private final DaoConfig dataUpdateBeanDaoConfig;
    private final DaoConfig diaryBeanDaoConfig;
    private final DaoConfig freeNoteBeanDaoConfig;
    private final DaoConfig itemTypeBeanDaoConfig;
    private final DaoConfig recordBeanDaoConfig;
    private final DaoConfig bookBeanDaoConfig;
    private final DaoConfig textbookBeanDaoConfig;
    private final DaoConfig dateEventBeanDaoConfig;
    private final DaoConfig homeworkBookBeanDaoConfig;
    private final DaoConfig homeworkBookCorrectBeanDaoConfig;
    private final DaoConfig homeworkContentBeanDaoConfig;
    private final DaoConfig homeworkPaperBeanDaoConfig;
    private final DaoConfig homeworkShareBeanDaoConfig;
    private final DaoConfig homeworkTypeBeanDaoConfig;
    private final DaoConfig noteDaoConfig;
    private final DaoConfig noteContentBeanDaoConfig;
    private final DaoConfig paintingBeanDaoConfig;
    private final DaoConfig paintingDrawingBeanDaoConfig;
    private final DaoConfig paperBeanDaoConfig;
    private final DaoConfig paperTypeBeanDaoConfig;

    private final AppBeanDao appBeanDao;
    private final CalenderItemBeanDao calenderItemBeanDao;
    private final DataUpdateBeanDao dataUpdateBeanDao;
    private final DiaryBeanDao diaryBeanDao;
    private final FreeNoteBeanDao freeNoteBeanDao;
    private final ItemTypeBeanDao itemTypeBeanDao;
    private final RecordBeanDao recordBeanDao;
    private final BookBeanDao bookBeanDao;
    private final TextbookBeanDao textbookBeanDao;
    private final DateEventBeanDao dateEventBeanDao;
    private final HomeworkBookBeanDao homeworkBookBeanDao;
    private final HomeworkBookCorrectBeanDao homeworkBookCorrectBeanDao;
    private final HomeworkContentBeanDao homeworkContentBeanDao;
    private final HomeworkPaperBeanDao homeworkPaperBeanDao;
    private final HomeworkShareBeanDao homeworkShareBeanDao;
    private final HomeworkTypeBeanDao homeworkTypeBeanDao;
    private final NoteDao noteDao;
    private final NoteContentBeanDao noteContentBeanDao;
    private final PaintingBeanDao paintingBeanDao;
    private final PaintingDrawingBeanDao paintingDrawingBeanDao;
    private final PaperBeanDao paperBeanDao;
    private final PaperTypeBeanDao paperTypeBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        appBeanDaoConfig = daoConfigMap.get(AppBeanDao.class).clone();
        appBeanDaoConfig.initIdentityScope(type);

        calenderItemBeanDaoConfig = daoConfigMap.get(CalenderItemBeanDao.class).clone();
        calenderItemBeanDaoConfig.initIdentityScope(type);

        dataUpdateBeanDaoConfig = daoConfigMap.get(DataUpdateBeanDao.class).clone();
        dataUpdateBeanDaoConfig.initIdentityScope(type);

        diaryBeanDaoConfig = daoConfigMap.get(DiaryBeanDao.class).clone();
        diaryBeanDaoConfig.initIdentityScope(type);

        freeNoteBeanDaoConfig = daoConfigMap.get(FreeNoteBeanDao.class).clone();
        freeNoteBeanDaoConfig.initIdentityScope(type);

        itemTypeBeanDaoConfig = daoConfigMap.get(ItemTypeBeanDao.class).clone();
        itemTypeBeanDaoConfig.initIdentityScope(type);

        recordBeanDaoConfig = daoConfigMap.get(RecordBeanDao.class).clone();
        recordBeanDaoConfig.initIdentityScope(type);

        bookBeanDaoConfig = daoConfigMap.get(BookBeanDao.class).clone();
        bookBeanDaoConfig.initIdentityScope(type);

        textbookBeanDaoConfig = daoConfigMap.get(TextbookBeanDao.class).clone();
        textbookBeanDaoConfig.initIdentityScope(type);

        dateEventBeanDaoConfig = daoConfigMap.get(DateEventBeanDao.class).clone();
        dateEventBeanDaoConfig.initIdentityScope(type);

        homeworkBookBeanDaoConfig = daoConfigMap.get(HomeworkBookBeanDao.class).clone();
        homeworkBookBeanDaoConfig.initIdentityScope(type);

        homeworkBookCorrectBeanDaoConfig = daoConfigMap.get(HomeworkBookCorrectBeanDao.class).clone();
        homeworkBookCorrectBeanDaoConfig.initIdentityScope(type);

        homeworkContentBeanDaoConfig = daoConfigMap.get(HomeworkContentBeanDao.class).clone();
        homeworkContentBeanDaoConfig.initIdentityScope(type);

        homeworkPaperBeanDaoConfig = daoConfigMap.get(HomeworkPaperBeanDao.class).clone();
        homeworkPaperBeanDaoConfig.initIdentityScope(type);

        homeworkShareBeanDaoConfig = daoConfigMap.get(HomeworkShareBeanDao.class).clone();
        homeworkShareBeanDaoConfig.initIdentityScope(type);

        homeworkTypeBeanDaoConfig = daoConfigMap.get(HomeworkTypeBeanDao.class).clone();
        homeworkTypeBeanDaoConfig.initIdentityScope(type);

        noteDaoConfig = daoConfigMap.get(NoteDao.class).clone();
        noteDaoConfig.initIdentityScope(type);

        noteContentBeanDaoConfig = daoConfigMap.get(NoteContentBeanDao.class).clone();
        noteContentBeanDaoConfig.initIdentityScope(type);

        paintingBeanDaoConfig = daoConfigMap.get(PaintingBeanDao.class).clone();
        paintingBeanDaoConfig.initIdentityScope(type);

        paintingDrawingBeanDaoConfig = daoConfigMap.get(PaintingDrawingBeanDao.class).clone();
        paintingDrawingBeanDaoConfig.initIdentityScope(type);

        paperBeanDaoConfig = daoConfigMap.get(PaperBeanDao.class).clone();
        paperBeanDaoConfig.initIdentityScope(type);

        paperTypeBeanDaoConfig = daoConfigMap.get(PaperTypeBeanDao.class).clone();
        paperTypeBeanDaoConfig.initIdentityScope(type);

        appBeanDao = new AppBeanDao(appBeanDaoConfig, this);
        calenderItemBeanDao = new CalenderItemBeanDao(calenderItemBeanDaoConfig, this);
        dataUpdateBeanDao = new DataUpdateBeanDao(dataUpdateBeanDaoConfig, this);
        diaryBeanDao = new DiaryBeanDao(diaryBeanDaoConfig, this);
        freeNoteBeanDao = new FreeNoteBeanDao(freeNoteBeanDaoConfig, this);
        itemTypeBeanDao = new ItemTypeBeanDao(itemTypeBeanDaoConfig, this);
        recordBeanDao = new RecordBeanDao(recordBeanDaoConfig, this);
        bookBeanDao = new BookBeanDao(bookBeanDaoConfig, this);
        textbookBeanDao = new TextbookBeanDao(textbookBeanDaoConfig, this);
        dateEventBeanDao = new DateEventBeanDao(dateEventBeanDaoConfig, this);
        homeworkBookBeanDao = new HomeworkBookBeanDao(homeworkBookBeanDaoConfig, this);
        homeworkBookCorrectBeanDao = new HomeworkBookCorrectBeanDao(homeworkBookCorrectBeanDaoConfig, this);
        homeworkContentBeanDao = new HomeworkContentBeanDao(homeworkContentBeanDaoConfig, this);
        homeworkPaperBeanDao = new HomeworkPaperBeanDao(homeworkPaperBeanDaoConfig, this);
        homeworkShareBeanDao = new HomeworkShareBeanDao(homeworkShareBeanDaoConfig, this);
        homeworkTypeBeanDao = new HomeworkTypeBeanDao(homeworkTypeBeanDaoConfig, this);
        noteDao = new NoteDao(noteDaoConfig, this);
        noteContentBeanDao = new NoteContentBeanDao(noteContentBeanDaoConfig, this);
        paintingBeanDao = new PaintingBeanDao(paintingBeanDaoConfig, this);
        paintingDrawingBeanDao = new PaintingDrawingBeanDao(paintingDrawingBeanDaoConfig, this);
        paperBeanDao = new PaperBeanDao(paperBeanDaoConfig, this);
        paperTypeBeanDao = new PaperTypeBeanDao(paperTypeBeanDaoConfig, this);

        registerDao(AppBean.class, appBeanDao);
        registerDao(CalenderItemBean.class, calenderItemBeanDao);
        registerDao(DataUpdateBean.class, dataUpdateBeanDao);
        registerDao(DiaryBean.class, diaryBeanDao);
        registerDao(FreeNoteBean.class, freeNoteBeanDao);
        registerDao(ItemTypeBean.class, itemTypeBeanDao);
        registerDao(RecordBean.class, recordBeanDao);
        registerDao(BookBean.class, bookBeanDao);
        registerDao(TextbookBean.class, textbookBeanDao);
        registerDao(DateEventBean.class, dateEventBeanDao);
        registerDao(HomeworkBookBean.class, homeworkBookBeanDao);
        registerDao(HomeworkBookCorrectBean.class, homeworkBookCorrectBeanDao);
        registerDao(HomeworkContentBean.class, homeworkContentBeanDao);
        registerDao(HomeworkPaperBean.class, homeworkPaperBeanDao);
        registerDao(HomeworkShareBean.class, homeworkShareBeanDao);
        registerDao(HomeworkTypeBean.class, homeworkTypeBeanDao);
        registerDao(Note.class, noteDao);
        registerDao(NoteContentBean.class, noteContentBeanDao);
        registerDao(PaintingBean.class, paintingBeanDao);
        registerDao(PaintingDrawingBean.class, paintingDrawingBeanDao);
        registerDao(PaperBean.class, paperBeanDao);
        registerDao(PaperTypeBean.class, paperTypeBeanDao);
    }
    
    public void clear() {
        appBeanDaoConfig.clearIdentityScope();
        calenderItemBeanDaoConfig.clearIdentityScope();
        dataUpdateBeanDaoConfig.clearIdentityScope();
        diaryBeanDaoConfig.clearIdentityScope();
        freeNoteBeanDaoConfig.clearIdentityScope();
        itemTypeBeanDaoConfig.clearIdentityScope();
        recordBeanDaoConfig.clearIdentityScope();
        bookBeanDaoConfig.clearIdentityScope();
        textbookBeanDaoConfig.clearIdentityScope();
        dateEventBeanDaoConfig.clearIdentityScope();
        homeworkBookBeanDaoConfig.clearIdentityScope();
        homeworkBookCorrectBeanDaoConfig.clearIdentityScope();
        homeworkContentBeanDaoConfig.clearIdentityScope();
        homeworkPaperBeanDaoConfig.clearIdentityScope();
        homeworkShareBeanDaoConfig.clearIdentityScope();
        homeworkTypeBeanDaoConfig.clearIdentityScope();
        noteDaoConfig.clearIdentityScope();
        noteContentBeanDaoConfig.clearIdentityScope();
        paintingBeanDaoConfig.clearIdentityScope();
        paintingDrawingBeanDaoConfig.clearIdentityScope();
        paperBeanDaoConfig.clearIdentityScope();
        paperTypeBeanDaoConfig.clearIdentityScope();
    }

    public AppBeanDao getAppBeanDao() {
        return appBeanDao;
    }

    public CalenderItemBeanDao getCalenderItemBeanDao() {
        return calenderItemBeanDao;
    }

    public DataUpdateBeanDao getDataUpdateBeanDao() {
        return dataUpdateBeanDao;
    }

    public DiaryBeanDao getDiaryBeanDao() {
        return diaryBeanDao;
    }

    public FreeNoteBeanDao getFreeNoteBeanDao() {
        return freeNoteBeanDao;
    }

    public ItemTypeBeanDao getItemTypeBeanDao() {
        return itemTypeBeanDao;
    }

    public RecordBeanDao getRecordBeanDao() {
        return recordBeanDao;
    }

    public BookBeanDao getBookBeanDao() {
        return bookBeanDao;
    }

    public TextbookBeanDao getTextbookBeanDao() {
        return textbookBeanDao;
    }

    public DateEventBeanDao getDateEventBeanDao() {
        return dateEventBeanDao;
    }

    public HomeworkBookBeanDao getHomeworkBookBeanDao() {
        return homeworkBookBeanDao;
    }

    public HomeworkBookCorrectBeanDao getHomeworkBookCorrectBeanDao() {
        return homeworkBookCorrectBeanDao;
    }

    public HomeworkContentBeanDao getHomeworkContentBeanDao() {
        return homeworkContentBeanDao;
    }

    public HomeworkPaperBeanDao getHomeworkPaperBeanDao() {
        return homeworkPaperBeanDao;
    }

    public HomeworkShareBeanDao getHomeworkShareBeanDao() {
        return homeworkShareBeanDao;
    }

    public HomeworkTypeBeanDao getHomeworkTypeBeanDao() {
        return homeworkTypeBeanDao;
    }

    public NoteDao getNoteDao() {
        return noteDao;
    }

    public NoteContentBeanDao getNoteContentBeanDao() {
        return noteContentBeanDao;
    }

    public PaintingBeanDao getPaintingBeanDao() {
        return paintingBeanDao;
    }

    public PaintingDrawingBeanDao getPaintingDrawingBeanDao() {
        return paintingDrawingBeanDao;
    }

    public PaperBeanDao getPaperBeanDao() {
        return paperBeanDao;
    }

    public PaperTypeBeanDao getPaperTypeBeanDao() {
        return paperTypeBeanDao;
    }

}

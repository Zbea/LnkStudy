package com.bll.lnkstudy.mvp.view;

import com.bll.lnkstudy.mvp.model.AccountOrder;
import com.bll.lnkstudy.mvp.model.AccountQdBean;
import com.bll.lnkstudy.mvp.model.AppList;
import com.bll.lnkstudy.mvp.model.CalenderList;
import com.bll.lnkstudy.mvp.model.ClassGroup;
import com.bll.lnkstudy.mvp.model.ClassGroupUserList;
import com.bll.lnkstudy.mvp.model.CommonData;
import com.bll.lnkstudy.mvp.model.DataUpdateBean;
import com.bll.lnkstudy.mvp.model.book.TeachingMaterialList;
import com.bll.lnkstudy.mvp.model.homework.HomeworkShareBean;
import com.bll.lnkstudy.mvp.model.paper.ExamCorrectBean;
import com.bll.lnkstudy.mvp.model.paper.ExamItem;
import com.bll.lnkstudy.mvp.model.MessageList;
import com.bll.lnkstudy.mvp.model.SchoolBean;
import com.bll.lnkstudy.mvp.model.TeachingVideoList;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.book.BookStore;
import com.bll.lnkstudy.mvp.model.book.BookStoreType;
import com.bll.lnkstudy.mvp.model.cloud.CloudList;
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessageList;
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList;
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperList;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.homework.ParentTypeBean;
import com.bll.lnkstudy.mvp.model.painting.PaintingList;
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean;
import com.bll.lnkstudy.mvp.model.permission.PermissionParentBean;
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolBean;
import com.bll.lnkstudy.mvp.model.book.TextbookStore;
import com.bll.lnkstudy.net.IBaseView;

import java.util.List;
import java.util.Map;

public interface IContractView {

    //文件上传
    interface IFileUploadView extends IBaseView{
        void onToken(String token);
        default void onCommitSuccess(){};
    }
    /**
     * 增量更新
     */
    interface IDataUpdateView extends IBaseView{
        void onSuccess();
        void onList(List<DataUpdateBean> list);
    }
    /**
     * 云书库上传
     */
    interface ICloudUploadView extends IBaseView{
        void onSuccess(List<Integer> cloudIds);
        void onDeleteSuccess();
    }
    //登录
    interface ILoginView extends IBaseView {
        void getLogin(User user);
        void getAccount(User user);
    }

    //注册 找回密码
    interface IRegisterOrFindPsdView extends IBaseView {
        void onRegister();
        void onFindPsd();
    }

    //账户页面回调
    interface IAccountInfoView extends IBaseView {
        void getAccount(User user);
        void onLogout();
        void onEditPhone();
        void onEditBirthday();
        void onEditNameSuccess();
        void onEditSchool();
        void onEditParent();
    }

    //短信回调
    interface ISmsView extends IBaseView {
        default void onSms(){};
        default void onCheckSuccess(){};
    }

    //钱包页面回调
    interface IWalletView extends IBaseView {
        void onXdList(List<AccountQdBean> list);
        void onXdOrder(AccountOrder order);
        void checkOrder(AccountOrder order);
        void getAccount(User user);
    }

    //书城
    interface IBookStoreView extends IBaseView {
        void onBook(BookStore bookStore);
        void onType(BookStoreType bookStoreType);
        void buySuccess();
    }

    //教材
    interface ITextbookStoreView extends IBaseView {
        void onTextbook(TextbookStore bookStore);
        void buySuccess();
    }

    interface IDictionaryResourceView extends IBaseView {
        void onList(TextbookStore bookStore);
        void buySuccess();
    }

    //应用
    interface IAPPView extends IBaseView {
        void onAppList(AppList appBean);
        void buySuccess();
    }

    //书画以及壁纸
    interface IPaintingView extends IBaseView {
        void onList(PaintingList bean);
        void buySuccess();
    }

    //班群管理
    interface IClassGroupView extends IBaseView {
        default void onInsert(){}
        default void onClassInfo(ClassGroup classGroup){}
        default void onClassGroupList(List<ClassGroup> classGroups) {}
        default void onQuit(){}
        default void onUser(ClassGroupUserList userList){}
    }

    //教学视频
    interface ITeachingVideoView extends IBaseView {
        void onList(TeachingVideoList list);
    }

    interface IMainLeftView extends IBaseView {
        void onParentPermission(PermissionParentBean permissionParentBean);
        void onSchoolPermission(PermissionSchoolBean permissionSchoolBean);
        default void onClassGroupPermission(long time){}
    }

    interface IMainRightView extends IBaseView {
        void onExam(ExamItem exam);
        void onCourseUrl(String url);
        void onClassGroupList(List<ClassGroup> classGroups);
        void onCourseItems(List<String> courseItems);
    }

    interface IHomeworkNoticeView extends IBaseView {
        default void onHomeworkNotice(HomeworkNoticeList list){}
        default void onCorrect(HomeworkNoticeList list){}
        default void onClearSuccess(){}
    }

    //公共接口
    interface IPaperView extends IBaseView {
        /**
         * 获取考卷分类
         */
        void onTypeList(List<PaperTypeBean> list);
        void onList(HomeworkPaperList list);
        void onDownloadSuccess();
        void onExamList(List<ExamCorrectBean> list);
    }

    //作业
    interface IHomeworkView extends IBaseView{
        default void onTypeList(List<HomeworkTypeBean> list){}
        default void onTypeParentList(List<ParentTypeBean> list) {}
        default void onTypeError() {}
        default void onMessageList(Map<String, HomeworkMessageList> map){}
        default void onMessageAll(List<HomeworkMessageList.MessageBean> list){};
        default void onParentMessageList(Map<String, HomeworkMessageList> map){};
        default void onPaperList(HomeworkPaperList list){};
        default void onParentReel(HomeworkMessageList list) {}
        default void onDownloadSuccess() {}
        default void onShareList(List<HomeworkShareBean> list){}
    }

    interface IMessageView extends IBaseView{
        void onList(MessageList message);
        default void onCommitSuccess(){};
    }

    //云
    interface ICloudView extends IBaseView {
        void onList(CloudList item);
        void onType(List<String> types);
        void onDelete();
    }

    //公共接口
    interface ICommonView extends IBaseView {
        default void onList(CommonData commonData){};
        default void onListSchools(List<SchoolBean> list){}
        default void onSchoolInfo(SchoolBean schoolBean){}
    }

    interface ICalenderView extends IBaseView {
        void onList(CalenderList list);
        void buySuccess();
    }

    interface IQiniuView extends IBaseView {
        void onToken(String token);
    }

    interface ITeachingMaterialView extends IBaseView {
        void onList(TeachingMaterialList list);
        default void onDownloadSuccess(){};
    }

}

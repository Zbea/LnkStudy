package com.bll.lnkstudy.mvp.view;

import com.bll.lnkstudy.mvp.model.AccountOrder;
import com.bll.lnkstudy.mvp.model.AccountQdBean;
import com.bll.lnkstudy.mvp.model.AppList;
import com.bll.lnkstudy.mvp.model.AppUpdateBean;
import com.bll.lnkstudy.mvp.model.CalenderList;
import com.bll.lnkstudy.mvp.model.ClassGroup;
import com.bll.lnkstudy.mvp.model.ClassGroupUserList;
import com.bll.lnkstudy.mvp.model.CommonData;
import com.bll.lnkstudy.mvp.model.DataUpdateBean;
import com.bll.lnkstudy.mvp.model.SystemUpdateInfo;
import com.bll.lnkstudy.mvp.model.paper.ExamCorrectBean;
import com.bll.lnkstudy.mvp.model.paper.ExamItem;
import com.bll.lnkstudy.mvp.model.MessageList;
import com.bll.lnkstudy.mvp.model.SchoolBean;
import com.bll.lnkstudy.mvp.model.TeachingVideoList;
import com.bll.lnkstudy.mvp.model.TeachingVideoType;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.book.BookStore;
import com.bll.lnkstudy.mvp.model.book.BookStoreType;
import com.bll.lnkstudy.mvp.model.cloud.CloudList;
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessage;
import com.bll.lnkstudy.mvp.model.homework.HomeworkNoticeList;
import com.bll.lnkstudy.mvp.model.homework.HomeworkPaperList;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.mvp.model.homework.ParentHomeworkBean;
import com.bll.lnkstudy.mvp.model.homework.ParentHomeworkMessage;
import com.bll.lnkstudy.mvp.model.homework.ParentTypeBean;
import com.bll.lnkstudy.mvp.model.painting.PaintingList;
import com.bll.lnkstudy.mvp.model.paper.PaperList;
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean;
import com.bll.lnkstudy.mvp.model.permission.PermissionParentBean;
import com.bll.lnkstudy.mvp.model.permission.PermissionSchoolBean;
import com.bll.lnkstudy.mvp.model.textbook.TextbookStore;
import com.bll.lnkstudy.net.IBaseView;

import java.util.List;
import java.util.Map;

public interface IContractView {

    interface ISystemView extends IBaseView{
        void onUpdateInfo(SystemUpdateInfo item);
    }

    //文件上传
    interface IFileUploadView extends IBaseView{
        void onToken(String token);
        void onCommitSuccess();
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
        void onSms();
        void onRegister();
        void onFindPsd();
        void onEditPsd();
    }

    //账户页面回调
    interface IAccountInfoView extends IBaseView {
        void onLogout();
        void onEditNameSuccess();
        void onEditSchool();
        void onEditParent();
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
        void onInsert();
        void onClassGroupList(List<ClassGroup> classGroups);
        void onQuit();
        void onUser(ClassGroupUserList userList);
    }

    //教学视频
    interface ITeachingVideoView extends IBaseView {
        void onList(TeachingVideoList list);
        void onType(TeachingVideoType type);//视频 其他分类
    }

    interface IMainLeftView extends IBaseView {
        void onHomeworkNotice(HomeworkNoticeList list);
        void onAppUpdate(AppUpdateBean item);
        void onCorrect(HomeworkNoticeList list);
        void onType(TeachingVideoType type);
        void onParentPermission(PermissionParentBean permissionParentBean);
        void onSchoolPermission(PermissionSchoolBean permissionSchoolBean);
    }

    interface IMainRightView extends IBaseView {
        void onExam(ExamItem exam);
        void onCourse(String url);
        void onClassGroupList(List<ClassGroup> classGroups);
        void onCourseItems(List<String> courseItems);
    }

    interface IHomeworkNoticeView extends IBaseView {
        void onHomeworkNotice(HomeworkNoticeList list);
        void onCorrect(HomeworkNoticeList list);
    }

    //公共接口
    interface IPaperView extends IBaseView {
        /**
         * 获取考卷分类
         */
        void onTypeList(List<PaperTypeBean> list);
        void onList(PaperList paper);
        void onDeleteSuccess();
        void onExamList(List<ExamCorrectBean> list);
    }

    //作业
    interface IHomeworkView extends IBaseView{
        /**
         * 获取作业分类
         */
        void onTypeList(List<HomeworkTypeBean> list);
        void onTypeParentList(List<ParentTypeBean> list);
        void onMessageList(Map<String, HomeworkMessage> map);
        void onParentMessageList(Map<String, ParentHomeworkMessage> map);
        void onListReel(Map<String, HomeworkPaperList> map);
        void onParentReel(Map<String, List<ParentHomeworkBean>> map);
        /**
         * 下发作业下载成功
         */
        void onDownloadSuccess();
    }

    interface IMessageView extends IBaseView{
        void onList(MessageList message);
        void onCommitSuccess();
    }

    //云
    interface ICloudView extends IBaseView {
        void onList(CloudList item);
        void onType(List<String> types);
        void onDelete();
    }

    //公共接口
    interface ICommonView extends IBaseView {
        void onList(CommonData commonData);
        void onListSchools(List<SchoolBean> list);
    }

    interface ICalenderView extends IBaseView {
        void onList(CalenderList list);
        void buySuccess();
    }

    interface IQiniuView extends IBaseView {
        void onToken(String token);
    }

}

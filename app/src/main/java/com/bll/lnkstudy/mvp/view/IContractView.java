package com.bll.lnkstudy.mvp.view;

import com.bll.lnkstudy.mvp.model.AccountXDList;
import com.bll.lnkstudy.mvp.model.AccountOrder;
import com.bll.lnkstudy.mvp.model.AppList;
import com.bll.lnkstudy.mvp.model.BookStore;
import com.bll.lnkstudy.mvp.model.ClassGroup;
import com.bll.lnkstudy.mvp.model.ClassGroupUser;
import com.bll.lnkstudy.mvp.model.BookStoreType;
import com.bll.lnkstudy.mvp.model.CommonData;
import com.bll.lnkstudy.mvp.model.ControlMessage;
import com.bll.lnkstudy.mvp.model.Message;
import com.bll.lnkstudy.mvp.model.PaintingList;
import com.bll.lnkstudy.mvp.model.cloud.CloudExamList;
import com.bll.lnkstudy.mvp.model.cloud.CloudHomeworkList;
import com.bll.lnkstudy.mvp.model.cloud.CloudPaintingList;
import com.bll.lnkstudy.mvp.model.homework.HomeworkDetails;
import com.bll.lnkstudy.mvp.model.paper.PaperType;
import com.bll.lnkstudy.mvp.model.paper.PaperList;
import com.bll.lnkstudy.mvp.model.TeachingVideoList;
import com.bll.lnkstudy.mvp.model.TeachingVideoType;
import com.bll.lnkstudy.mvp.model.User;
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessage;
import com.bll.lnkstudy.mvp.model.homework.HomeworkReel;
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean;
import com.bll.lnkstudy.net.IBaseView;

import java.util.List;

public interface IContractView {

    //文件上传
    interface IFileUploadView extends IBaseView{
        void onSuccess(List<String> urls);
        void onCommitSuccess();
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
        void onEditGradeSuccess();
    }

    //钱包页面回调
    interface IWalletView extends IBaseView {
        void onXdList(AccountXDList list);
        void onXdOrder(AccountOrder order);
        void checkOrder(AccountOrder order);
    }

    //书城教材
    interface IBookStoreView extends IBaseView {
        void onBook(BookStore bookStore);
        void onType(BookStoreType bookStoreType);
        void buyBookSuccess();
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
        void onUser(List<ClassGroupUser> lists);
    }

    //教学视频
    interface ITeachingVideoView extends IBaseView {
        void onList(TeachingVideoList list);
        void onCourse(TeachingVideoType type);//视频 课程分类
        void onType(TeachingVideoType type);//视频 其他分类
    }

    interface IMainView extends IBaseView {
        void onClassGroupList(List<ClassGroup> classGroups);
        /**
         * 获取考试列表
         * @param exam
         */
        void onExam(PaperList exam);
    }

    //公共接口
    interface IPaperView extends IBaseView {
        /**
         * 获取考卷分类
         */
        void onTypeList(List<PaperType.PaperTypeBean> list);
        void onList(PaperList paper);
        void onCommitSuccess();
        void onDeleteSuccess();

    }

    //作业
    interface IHomeworkView extends IBaseView{
        /**
         * 获取作业分类
         */
        void onTypeList(List<HomeworkTypeBean> list);
        void onList(HomeworkMessage homeworkMessage);
        void onListReel(HomeworkReel reel);
        void onDetails(List<HomeworkDetails.HomeworkDetailBean> details);
        /**
         * 下发作业下载成功
         */
        void onDownloadSuccess();
        void onCommitSuccess();
    }

    interface IMessageView extends IBaseView{
        void onList(Message message);
        void onCommitSuccess();
    }

    //云作业
    interface ICloudHomeworkView extends IBaseView {
        void onType(CloudHomeworkList item);
    }
    //云考卷
    interface ICloudExamView extends IBaseView {
        void onType(CloudExamList item);
    }

    //云书画
    interface ICloudPaintingView extends IBaseView {
        void onList(CloudPaintingList item);
    }

    //公共接口
    interface ICommonView extends IBaseView {
        void onList(CommonData commonData);
    }

    //公共接口
    interface IControlMessageView extends IBaseView {
        /**
         * 获取老师控制移除指定
         */
        void onControl(List<ControlMessage> controlMessages);
        void onDelete();
    }

}

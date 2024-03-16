package com.bll.lnkstudy.base

import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.DataUpdateBean
import com.bll.lnkstudy.mvp.presenter.AccountInfoPresenter
import com.bll.lnkstudy.mvp.presenter.CloudUploadPresenter
import com.bll.lnkstudy.mvp.presenter.DataUpdatePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.mvp.view.IContractView.IAccountInfoView
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.SPUtil
import org.greenrobot.eventbus.EventBus
import java.io.File


abstract class BaseMainFragment : BaseFragment(), IContractView.ICloudUploadView,IContractView.IDataUpdateView,IAccountInfoView{

    val mCloudUploadPresenter= CloudUploadPresenter(this)
    val mDataUploadPresenter=DataUpdatePresenter(this)
    val mAccountInfoPresenter=AccountInfoPresenter(this,getScreenPosition())

    override fun onLogout() {
    }
    override fun onEditNameSuccess() {
    }
    override fun onEditGradeSuccess() {
        mUser?.grade=grade+1
        SPUtil.putObj("user", mUser!!)
        EventBus.getDefault().post(Constants.USER_CHANGE_EVENT)
    }
    override fun onEditSchool() {
    }

    //云端上传回调
    override fun onSuccess(cloudIds: MutableList<Int>?) {
        uploadSuccess(cloudIds)
    }
    override fun onDeleteSuccess() {
    }

    //增量更新回调
    override fun onSuccess() {
    }
    override fun onList(list: MutableList<DataUpdateBean>?) {
    }

    /**
     * 清空作业本
     */
    protected fun setClearHomework(){
        //删除所有作业分类
        HomeworkTypeDaoManager.getInstance().clear()
        //删除所有作业
        HomeworkContentDaoManager.getInstance().clear()
        //删除所有朗读
        RecordDaoManager.getInstance().clear()
        //删除所有作业卷内容
        HomeworkPaperDaoManager.getInstance().clear()
        HomeworkPaperContentDaoManager.getInstance().clear()
        //题卷本
        HomeworkBookDaoManager.getInstance().clear()
        //提交详情
        HomeworkDetailsDaoManager.getInstance().clear()

        FileUtils.deleteFile(File(Constants.HOMEWORK_PATH))
        //清除本地增量数据
        DataUpdateManager.clearDataUpdate(2)
        val map=HashMap<String,Any>()
        map["type"]=2
        mDataUploadPresenter.onDeleteData(map)
    }

    /**
     * 清空考卷
     */
    protected fun setClearExamPaper(){
        //删除本地考卷分类
        PaperTypeDaoManager.getInstance().clear()
        //删除所有考卷内容
        PaperDaoManager.getInstance().clear()
        PaperContentDaoManager.getInstance().clear()
        FileUtils.deleteFile(File(Constants.TESTPAPER_PATH))
        //清除本地增量数据
        DataUpdateManager.clearDataUpdate(3)
        val map=HashMap<String,Any>()
        map["type"]=3
        mDataUploadPresenter.onDeleteData(map)
    }

    /**
     * 系统控制（在上传完成后删除作业、考卷，升年级）
     */
    protected fun setSystemControlClear(){
        val homeworkTypes=HomeworkTypeDaoManager.getInstance().queryAll()
        val paperTypes=PaperTypeDaoManager.getInstance().queryAll()
        if (homeworkTypes.isNullOrEmpty()&&paperTypes.isNullOrEmpty()){
            //考卷上传完之后升年级
            mAccountInfoPresenter.editGrade(grade+1)
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.REFRESH_EVENT){
            lazyLoad()
        }
        super.onEventBusMessage(msgFlag)
    }


    /**
     * 上传成功(书籍云id)
     */
    open fun uploadSuccess(cloudIds: MutableList<Int>?){
        if (!cloudIds.isNullOrEmpty())
        {
            mCloudUploadPresenter.deleteCloud(cloudIds)
        }
    }

}

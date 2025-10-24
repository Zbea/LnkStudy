package com.bll.lnkstudy.ui.activity.account

import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.MyApplication
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.AccountEditPhoneDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.AppDaoManager
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.manager.CalenderDaoManager
import com.bll.lnkstudy.manager.DataUpdateDaoManager
import com.bll.lnkstudy.manager.DateEventGreenDaoManager
import com.bll.lnkstudy.manager.DiaryDaoManager
import com.bll.lnkstudy.manager.FreeNoteDaoManager
import com.bll.lnkstudy.manager.HomeworkBookCorrectDaoManager
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkPaperDaoManager
import com.bll.lnkstudy.manager.HomeworkShareDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.manager.NoteContentDaoManager
import com.bll.lnkstudy.manager.NoteDaoManager
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.manager.PaperTypeDaoManager
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.manager.TextbookGreenDaoManager
import com.bll.lnkstudy.mvp.presenter.SmsPresenter
import com.bll.lnkstudy.mvp.view.IContractView.ISmsView
import com.bll.lnkstudy.ui.activity.MainActivity
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.ac_account_setting_data.tv_clear
import kotlinx.android.synthetic.main.ac_account_setting_data.tv_download
import kotlinx.android.synthetic.main.ac_account_setting_data.tv_rent
import org.greenrobot.eventbus.EventBus
import java.io.File

class AccountSettingDataActivity:BaseAppCompatActivity(),ISmsView {

    private var smsPresenter: SmsPresenter?=null
    private var type=0

    override fun onSms() {
        showToast("短信发送成功")
    }
    override fun onCheckSuccess() {
        setOnClick()
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_setting_data
    }

    override fun initData() {
        initChangeScreenData()
    }

    override fun initChangeScreenData() {
        smsPresenter=SmsPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle("一键功能")

        tv_download.setOnClickListener {
            getSms(1)
        }

        tv_rent.setOnClickListener {
            getSms(2)
        }

        tv_clear.setOnClickListener {
            getSms(3)
        }
    }

    private fun getSms(type:Int){
        this.type=type
        if (ToolUtils.isPhoneNum(mUser?.telNumber)){
            AccountEditPhoneDialog(this,mUser?.telNumber!!).builder().setOnDialogClickListener(object : AccountEditPhoneDialog.OnDialogClickListener {
                override fun onClick(code: String, phone: String) {
                    smsPresenter?.checkPhone(code)
                }
                override fun onPhone(phone: String) {
                    smsPresenter?.sms(phone)
                }
            })
        }
        else{
            setOnClick()
        }
    }

    private fun setOnClick(){
        val str=when(type){
            1->{
                "确认导入全部资料？"
            }
            2->{
                "确认导入部分资料？"
            }
            else->{
                "确认清空本地数据？"
            }
        }
        CommonDialog(this).setContent(str).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
            override fun ok() {
                when(type){
                    1->{
                        clearData()
                        EventBus.getDefault().post(Constants.SETTING_DOWNLOAD_EVENT)
                        ActivityManager.getInstance().finishOthers(MainActivity::class.java)
                    }
                    2->{
                        clearData()
                        EventBus.getDefault().post(Constants.SETTING_RENT_EVENT)
                        ActivityManager.getInstance().finishOthers(MainActivity::class.java)
                    }
                    3->{
                        clearData()
                    }
                }
            }
        })
    }

    /**
     * 一键清除
     */
    private fun clearData() {
        SPUtil.removeObj(Constants.SP_PRIVACY_PW_DIARY)
        SPUtil.removeObj(Constants.SP_PRIVACY_PW_NOTE)
        SPUtil.removeObj(Constants.SP_SCHOOL_PERMISSION)
        SPUtil.removeObj(Constants.SP_PARENT_PERMISSION)
        SPUtil.putListInt(Constants.SP_WEEK_DATE_LIST, mutableListOf())
        SPUtil.putListLong(Constants.SP_DATE_LIST, mutableListOf())
        SPUtil.putString(Constants.SP_DIARY_BG_SET,"")
        SPUtil.putString(Constants.SP_COURSE_URL,"")
        SPUtil.putBoolean(Constants.SP_EXAM_MODE,false)

        MyApplication.mDaoSession?.clear()
        DataUpdateDaoManager.getInstance().clear()
        FreeNoteDaoManager.getInstance().clear()
        DiaryDaoManager.getInstance().clear()
        BookGreenDaoManager.getInstance().clear()
        TextbookGreenDaoManager.getInstance().clear()

        HomeworkTypeDaoManager.getInstance().clear()
        //删除所有作业
        HomeworkContentDaoManager.getInstance().clear()
        //删除所有录音
        RecordDaoManager.getInstance().clear()
        //删除所有作业卷内容
        HomeworkPaperDaoManager.getInstance().clear()
        //题卷本
        HomeworkBookDaoManager.getInstance().clear()
        HomeworkBookCorrectDaoManager.getInstance().clear()
        HomeworkShareDaoManager.getInstance().clear()

        //删除本地考卷分类
        PaperTypeDaoManager.getInstance().clear()
        //删除所有考卷内容
        PaperDaoManager.getInstance().clear()

        NoteDaoManager.getInstance().clear()
        NoteContentDaoManager.getInstance().clear()

        PaintingDrawingDaoManager.getInstance().clear()
        PaintingBeanDaoManager.getInstance().clear()

        DateEventGreenDaoManager.getInstance().clear()
        AppDaoManager.getInstance().clear()

        ItemTypeDaoManager.getInstance().clear()
        CalenderDaoManager.getInstance().clear()

        FileUtils.deleteFile(File(Constants.BOOK_PATH))
        FileUtils.deleteFile(File(Constants.SCREEN_PATH))
        FileUtils.deleteFile(File(Constants.ZIP_PATH).parentFile)

        Glide.get(this).clearMemory()
        Thread{
            Glide.get(this).clearDiskCache()
        }.start()
        if (type==3)
            MethodManager.logout(this)
    }
}
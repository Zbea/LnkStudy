package com.bll.lnkstudy.ui.fragment

import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.manager.PaperTypeDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson
import java.io.File

class TestPaperManageFragment: BaseMainFragment() {

    private var lastFragment: Fragment? = null
    private var mCoursePos=0
    private var currentCourses= mutableListOf<ItemTypeBean>()
    private var fragments= mutableListOf<TestPaperFragment>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_testpaper_manage
    }

    override fun initView() {
        setTitle(DataBeanManager.listTitle[4])
        initTab()
    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab() {
        val courseItems= ItemTypeDaoManager.getInstance().queryAll(7)
        if (grade>0&&currentCourses!=courseItems){
            mCoursePos = 0
            itemTabTypes.clear()
            currentCourses=courseItems
            for (i in currentCourses.indices) {
                itemTabTypes.add(ItemTypeBean().apply {
                    title=currentCourses[i].title
                    isCheck=i==0
                })
            }
            mTabTypeAdapter?.setNewData(itemTabTypes)
            setLocalPaperType()
            initFragment()
        }
    }

    override fun onTabClickListener(view: View, position: Int) {
        mCoursePos=position
        switchFragment(lastFragment, fragments[mCoursePos])
    }

    private fun initFragment(){
        removeAllFragment()
        fragments.clear()
        for (courseItem in currentCourses){
            fragments.add(TestPaperFragment().newInstance(courseItem.title))
        }
        if (fragments.size>0){
            switchFragment(lastFragment, fragments[mCoursePos])
        }
    }

    /**
     * 设置本地学校考试卷
     */
    private fun setLocalPaperType(){
        if (grade==0){
            return
        }
        for (item in currentCourses){
            val course=item.title
            //创建学校考试卷
            val typeId = MethodManager.getTestPaperAutoTypeId("学校考试卷",course)
            if (PaperTypeDaoManager.getInstance().queryById(typeId)==null){
                val typeItem= PaperTypeBean()
                typeItem.name="学校考试卷"
                typeItem.course=course
                typeItem.createStatus=2
                typeItem.date=System.currentTimeMillis()
                typeItem.grade=grade
                typeItem.typeId=typeId
                PaperTypeDaoManager.getInstance().insertOrReplace(typeItem)
            }
        }
    }

    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = activity?.supportFragmentManager!!
            val ft = fm.beginTransaction()

            if (!to?.isAdded!!) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(R.id.fl_content_paper, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }

    private fun removeAllFragment(){
        for (fragment in fragments){
            val fm = activity?.supportFragmentManager!!
            val ft = fm.beginTransaction()
            ft.remove(fragment).commit()
        }
    }

    /**
     * 控制上传
     */
    fun uploadPaper(token: String) {
        val cloudList = mutableListOf<CloudListBean>()
        val nullItems = mutableListOf<PaperTypeBean>()
        val types = PaperTypeDaoManager.getInstance().queryAllExceptCloud()
        for (item in types) {
            val papers = PaperDaoManager.getInstance().queryAll(item.course, item.typeId)
            val path = FileAddress().getPathTestPaper(item.course,item.typeId)
            if (FileUtils.isExistContent(path)) {
                FileUploadManager(token).apply {
                    startZipUpload(path, item.name)
                    setCallBack {
                        cloudList.add(CloudListBean().apply {
                            type = 3
                            subTypeStr = item.course
                            date = System.currentTimeMillis()
                            grade = item.grade
                            listJson = Gson().toJson(item)
                            contentJson = Gson().toJson(papers)
                            downloadUrl = it
                        })
                        startUpload(cloudList,nullItems)
                    }
                }
            } else {
                //没有考卷内容不上传
                nullItems.add(item)
                startUpload(cloudList,nullItems)
            }
        }
    }

    private fun startUpload(cloudList: MutableList<CloudListBean>, nullItems: MutableList<PaperTypeBean>){
        if (cloudList.size == PaperTypeDaoManager.getInstance().queryAllExceptCloud().size - nullItems.size){
            if (cloudList.size>0){
                mCloudUploadPresenter.upload(cloudList)
            }
            else{
                clearPaperType()
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        clearPaperType()
    }

    private fun clearPaperType(){
        setClearExamPaper()
        setLocalPaperType()
        onRefreshData()
    }
    /**
     * 清空考卷
     */
    private fun setClearExamPaper(){
        //删除本地考卷分类
        PaperTypeDaoManager.getInstance().clear()
        //删除所有考卷内容
        PaperDaoManager.getInstance().clear()
        FileUtils.deleteFile(File(Constants.TESTPAPER_PATH))
        //清除本地增量数据
        DataUpdateManager.clearDataUpdate(3)
        val map=HashMap<String,Any>()
        map["type"]=3
        mDataUploadPresenter.onDeleteData(map)
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.COURSEITEM_EVENT -> {
                initTab()
            }
        }
    }

    override fun onRefreshData() {
        for (fragment in fragments){
            fragment.onRefreshData()
        }
    }

}
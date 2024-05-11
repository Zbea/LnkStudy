package com.bll.lnkstudy.ui.fragment

import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.manager.PaperTypeDaoManager
import com.bll.lnkstudy.mvp.model.CourseItem
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.paper.PaperTypeBean
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson

class TestPaperManageFragment: BaseMainFragment() {

    private var lastFragment: Fragment? = null
    private var mCoursePos=0
    private var currentCourses= mutableListOf<CourseItem>()
    private var fragments= mutableListOf<PaperFragment>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_testpaper_manage
    }

    override fun initView() {
        setTitle(R.string.main_testpaper_title)

    }

    override fun lazyLoad() {
        for (item in MethodManager.getCourses()){
            val typeId=MethodManager.getExamTypeId(item.subject)
            if (PaperTypeDaoManager.getInstance().queryById(typeId)==null){
                val typeItem=PaperTypeBean()
                typeItem.name="学校考试卷"
                typeItem.course=item.subject
                typeItem.date=System.currentTimeMillis()
                typeItem.grade=mUser?.grade!!
                typeItem.typeId=typeId
                typeItem.userId=item.userId
                PaperTypeDaoManager.getInstance().insertOrReplace(typeItem)
            }
        }
        initTab()
    }

    //设置头部索引
    private fun initTab() {
        if (currentCourses!=MethodManager.getCourses()){
            mCoursePos = 0
            itemTabTypes.clear()
            currentCourses=MethodManager.getCourses()
            for (i in currentCourses.indices) {
                itemTabTypes.add(ItemTypeBean().apply {
                    title=currentCourses[i].subject
                    isCheck=i==0
                })
            }
            mTabTypeAdapter?.setNewData(itemTabTypes)
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
        for (item in currentCourses){
            fragments.add(PaperFragment().newInstance(item))
        }
        if (fragments.size>0){
            switchFragment(lastFragment, fragments[mCoursePos])
        }
    }

    /**
     * 控制上传
     */
    fun uploadPaper(token: String) {
        if (grade == 0) return
        val cloudList = mutableListOf<CloudListBean>()
        val nullItems = mutableListOf<PaperTypeBean>()
        val types = PaperTypeDaoManager.getInstance().queryAll()
        for (item in types) {
            val papers = PaperDaoManager.getInstance().queryAll(item.course, item.typeId)
            val paperContents = PaperContentDaoManager.getInstance().queryAllByType(item.course, item.typeId)
            val path = FileAddress().getPathTestPaper(item.typeId)
            if (FileUtils.isExistContent(path)) {
                FileUploadManager(token).apply {
                    startUpload(path, item.name)
                    setCallBack {
                        cloudList.add(CloudListBean().apply {
                            type = 3
                            subTypeStr = item.course
                            date = System.currentTimeMillis()
                            grade = item.grade
                            listJson = Gson().toJson(item)
                            contentJson = Gson().toJson(papers)
                            contentSubtypeJson = Gson().toJson(paperContents)
                            downloadUrl = it
                        })
                        if (cloudList.size == types.size - nullItems.size)
                            mCloudUploadPresenter.upload(cloudList)
                    }
                }
            } else {
                //没有考卷内容不上传
                nullItems.add(item)
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        setClearExamPaper()
        setSystemControlClear()
        for (fragment in fragments){
            fragment.clearData()
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
        val fm = activity?.supportFragmentManager!!
        val ft = fm.beginTransaction()
        for (fragment in fragments){
            ft.remove(fragment).commit()
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        when (msgFlag) {
            Constants.COURSEITEM_EVENT -> {
                lazyLoad()
            }
        }
    }

}
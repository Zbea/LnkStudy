package com.bll.lnkstudy.ui.activity

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.book.TeachingMaterialList
import com.bll.lnkstudy.mvp.presenter.TeachingMaterialPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.DocumentListAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileBigDownManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.SPUtil
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_title.tv_course
import org.greenrobot.eventbus.EventBus
import java.io.File

class TeachingMaterialListActivity:BaseAppCompatActivity(), IContractView.ITeachingMaterialView {

    private var mPresenter=TeachingMaterialPresenter(this)
    private var items= mutableListOf<TeachingMaterialList.TeachingMaterialBean>()
    private var popCourses= mutableListOf<PopupBean>()
    private var mAdapter:DocumentListAdapter?=null
    private var subject=0

    override fun onList(list: TeachingMaterialList) {
        items=list.list
        mAdapter?.setNewData(items)
        setPageNumber(list.total)
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        pageSize=12

        val courseItems= ItemTypeDaoManager.getInstance().queryAll(7)
        for (item in courseItems){
            popCourses.add(PopupBean(DataBeanManager.getCourseId(item.title),item.title))
        }

        fetchData()
    }

    override fun initView() {
        setPageTitle("教学列表")
        showView(tv_course)

        tv_course.text="全部"
        tv_course.setOnClickListener {
            PopupList(this, popCourses, tv_course, tv_course.width, 5).builder().setOnSelectListener { item ->
                subject=item.id
                tv_course.text = item.name
                pageIndex = 1
                fetchData()
            }
        }

        initRecyclerView()
    }
    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this@TeachingMaterialListActivity,50f),
            DP2PX.dip2px(this@TeachingMaterialListActivity,20f),
            DP2PX.dip2px(this@TeachingMaterialListActivity,50f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = DocumentListAdapter(R.layout.item_document_list, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setEmptyView(R.layout.common_empty)
        }
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            downLoadStart(items[position])
        }
    }

    private fun downLoadStart(item:TeachingMaterialList.TeachingMaterialBean){
        val targetFileStr = FileAddress().getPathDocument(item.title+ FileUtils.getUrlFormat(item.url))
        if (FileUtils.isExist(targetFileStr)){
            showToast(R.string.toast_downloaded)
            return
        }
        showLoading()
        FileBigDownManager.with(this).create(item.url).setPath(targetFileStr)
            .startSingleTaskDownLoad(object :
                FileBigDownManager.SingleTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Long, totalBytes: Long) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    hideLoading()
                    SPUtil.putString(File(targetFileStr).name,item.url)
                    EventBus.getDefault().post(Constants.DOCUMENT_DOWNLOAD_EVENT)
                    showToast(getString(R.string.book_download_success))
                    hideLoading()
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    showLog(e?.message.toString())
                    hideLoading()
                    showToast(getString(R.string.book_download_fail))
                }
            })
    }

    override fun fetchData() {
        val map= HashMap<String,Any>()
        map["page"]=pageIndex
        map["size"]=pageSize
        if (subject>0)
            map["subject"]=subject
        mPresenter.getList(map)
    }

}
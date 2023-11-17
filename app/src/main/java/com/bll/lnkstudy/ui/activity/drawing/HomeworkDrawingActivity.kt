package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.BitmapFactory
import android.view.EinkPWInterface
import com.bll.lnkstudy.Constants.Companion.DEFAULT_PAGE
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.dialog.DrawingCommitDialog
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommit
import com.bll.lnkstudy.mvp.model.homework.HomeworkContentBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import java.io.File

/**
 * 普通作业本
 */
class HomeworkDrawingActivity : BaseDrawingActivity(), IContractView.IFileUploadView {

    private val mUploadPresenter = FileUploadPresenter(this,getCurrentScreenPos())
    private var course = ""//科目
    private var homeworkTypeId = 0//作业分组id
    private var homeworkType: HomeworkTypeBean? = null
    private var homeworkContent: HomeworkContentBean? = null//当前作业内容
    private var homeworkContent_a: HomeworkContentBean? = null//a屏作业

    private var homeworks = mutableListOf<HomeworkContentBean>() //所有作业内容

    private var page = 0//页码
    private var messages = mutableListOf<ItemList>()
    private var homeworkCommit: HomeworkCommit? = null
    private val commitItems = mutableListOf<ItemList>()

    override fun onToken(token: String) {
        val commitPaths = mutableListOf<String>()
        for (item in commitItems) {
            commitPaths.add(item.url)
        }
        FileImageUploadManager(token, commitPaths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    val map = HashMap<String, Any>()
                    if (homeworkType?.createStatus == 1) {
                        map["studentTaskId"] = homeworkCommit?.messageId!!
                        map["studentUrl"] = ToolUtils.getImagesStr(urls)
                        map["commonTypeId"] = homeworkTypeId
                        mUploadPresenter.commit(map)
                    } else {
                        map["id"] = homeworkCommit?.messageId!!
                        map["submitUrl"] = ToolUtils.getImagesStr(urls)
                        map["commonTypeId"] = homeworkTypeId
                        mUploadPresenter.commitParent(map)
                    }
                }

                override fun onUploadFail() {
                    hideLoading()
                    showToast(R.string.upload_fail)
                }
            })
        }
    }

    override fun onSuccess(urls: MutableList<String>?) {
    }

    override fun onCommitSuccess() {
        showToast(R.string.toast_commit_success)
        messages.removeAt(homeworkCommit?.index!!)
        for (i in homeworkCommit?.contents!!) {
            val homework = homeworks[i - 1]
            homework.state = 1
            homework.title = homeworkCommit?.title
            homework.contentId = homeworkCommit?.messageId!!
            homework.commitDate = System.currentTimeMillis()
            HomeworkContentDaoManager.getInstance().insertOrReplace(homework)
            DataUpdateManager.editDataUpdate(2, homework.id.toInt(), 2, homeworkTypeId, Gson().toJson(homework))
        }
        //设置不能手写
        if (homeworkCommit?.contents!!.contains(page + 1)) {
            elik_a?.setPWEnabled(false)
            elik_b?.setPWEnabled(false)
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        val bundle = intent.getBundleExtra("homeworkBundle")
        homeworkType = bundle?.getSerializable("homework") as HomeworkTypeBean
        page = intent.getIntExtra("page", DEFAULT_PAGE)
        homeworkTypeId = homeworkType?.typeId!!
        course = homeworkType?.course!!

        when (homeworkType?.createStatus) {
            1 -> {
                val list = homeworkType?.messages
                if (!list.isNullOrEmpty()) {
                    for (item in list) {
                        if (item.endTime > 0 && item.status == 3) {
                            messages.add(ItemList().apply {
                                id = item.studentTaskId
                                name = item.title
                            })
                        }
                    }
                }
            }
            2 -> {
                val list = homeworkType?.parents
                if (!list.isNullOrEmpty()) {
                    for (item in list) {
                        if (item.endTime > 0 && item.status == 1) {
                            messages.add(ItemList().apply {
                                id = item.id
                                name = item.content
                            })
                        }
                    }
                }
            }
        }

        homeworks = HomeworkContentDaoManager.getInstance().queryAllByType(course, homeworkTypeId)

        if (homeworks.size > 0) {
            if (page == DEFAULT_PAGE)
                page = homeworks.size - 1
            homeworkContent = homeworks[page]
        } else {
            newHomeWorkContent()
        }

    }

    override fun initView() {
        changeExpandView()
        changeContent()

        iv_catalog.setOnClickListener {
            showCatalog()
        }

        iv_expand_left.setOnClickListener {
            if (homeworks.size == 1) {
                newHomeWorkContent()
            }
            onChangeExpandContent()
        }
        iv_expand_right.setOnClickListener {
            if (homeworks.size == 1) {
                newHomeWorkContent()
            }
            onChangeExpandContent()
        }
        iv_expand_a.setOnClickListener {
            onChangeExpandContent()
        }
        iv_expand_b.setOnClickListener {
            onChangeExpandContent()
        }

        iv_btn.setOnClickListener {
            if (NetworkUtil(this).isNetworkConnected()) {
                commit()
            } else {
                showNetworkDialog()
            }
        }

    }

    override fun onPageUp() {
        if (isExpand) {
            if (page > 2) {
                page -= 2
                changeContent()
            } else if (page == 2) {//当页面不够翻两页时
                page = 1
                changeContent()
            }
        } else {
            if (page > 0) {
                page -= 1
                changeContent()
            }
        }
    }

    override fun onPageDown() {
        val total = homeworks.size - 1
        if (isExpand) {
            when (page) {
                total -> {
                    newHomeWorkContent()
                    newHomeWorkContent()
                    page = homeworks.size - 1
                }
                total - 1 -> {
                    newHomeWorkContent()
                    page = homeworks.size - 1
                }
                else -> {
                    page += 2
                }
            }
        } else {
            if (page >= total) {
                newHomeWorkContent()
                page = homeworks.size - 1
            } else {
                page += 1
            }
        }
        changeContent()
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand = !isExpand
        moveToScreen(isExpand)
        changeExpandView()
        changeContent()
    }

    /**
     * 弹出目录
     */
    private fun showCatalog() {
        var titleStr = ""
        val list = mutableListOf<ItemList>()
        for (item in homeworks) {
            val itemList = ItemList()
            itemList.name = item.title
            itemList.page = item.page
            if (titleStr != item.title) {
                titleStr = item.title
                list.add(itemList)
            }

        }
        DrawingCatalogDialog(this, list).builder()?.setOnDialogClickListener { position ->
            page = list[position].page
            changeContent()
        }
    }

    //翻页内容更新切换
    private fun changeContent() {

        homeworkContent = homeworks[page]
        if (isExpand) {
            if (page > 0) {
                homeworkContent_a = homeworks[page - 1]
            } else {
                page = 1
                homeworkContent = homeworks[page]
                homeworkContent_a = homeworks[page - 1]
            }
        }

        tv_title_b.text = homeworkContent?.title
        if (isExpand) {
            tv_title_a.text = homeworkContent_a?.title
        }
        if (homeworkType?.isCloud == true) {
            elik_b?.setPWEnabled(false)
        } else {
            //已提交后不能手写，显示合图后的图片
            elik_b?.setPWEnabled(homeworkContent?.state == 0)
        }

        if (homeworkContent?.state == 0) {
            setElikLoadPath(elik_b!!, homeworkContent!!)
            v_content_b.setImageResource(ToolUtils.getImageResId(this, homeworkType?.contentResId))//设置背景
        } else {
            GlideUtils.setImageFileNoCache(this, File(homeworkContent?.path), v_content_b)
        }
        tv_page_b.text = "${page + 1}"

        if (isExpand) {
            if (homeworkType?.isCloud == true) {
                elik_a?.setPWEnabled(false)
            } else {
                //已提交后不能手写，显示合图后的图片
                elik_a?.setPWEnabled(homeworkContent?.state == 0)
            }
            if (homeworkContent_a?.state == 0) {
                setElikLoadPath(elik_a!!, homeworkContent_a!!)
                v_content_a.setImageResource(ToolUtils.getImageResId(this, homeworkType?.contentResId))//设置背景
            } else {
                GlideUtils.setImageFileNoCache(this, File(homeworkContent_a?.path), v_content_a)
            }
            tv_page_a.text = "$page"
        }

    }

    //设置手写
    private fun setElikLoadPath(elik: EinkPWInterface, homeworkContent: HomeworkContentBean) {
        elik.setLoadFilePath(homeworkContent.path.replace("png", "tch"), true)
    }

    override fun onElikSava_a() {
        saveElik(elik_a!!, homeworkContent_a!!)
    }

    override fun onElikSava_b() {
        saveElik(elik_b!!, homeworkContent!!)
    }

    /**
     * 抬笔后保存手写
     */
    private fun saveElik(elik: EinkPWInterface, homeworkContent: HomeworkContentBean) {
        elik.saveBitmap(true) {}
        DataUpdateManager.editDataUpdate(2, homeworkContent.id.toInt(), 2, homeworkTypeId)
    }


    //创建新的作业内容
    private fun newHomeWorkContent() {

        val path = FileAddress().getPathHomework(course, homeworkTypeId, homeworks.size)
        val pathName = DateUtils.longToString(System.currentTimeMillis())

        homeworkContent = HomeworkContentBean()
        homeworkContent?.course = course
        homeworkContent?.date = System.currentTimeMillis()
        homeworkContent?.homeworkTypeId = homeworkTypeId
        homeworkContent?.bgResId = homeworkType?.bgResId
        homeworkContent?.typeStr = homeworkType?.name
        homeworkContent?.title = getString(R.string.unnamed) + (homeworks.size + 1)
        homeworkContent?.path = "$path/$pathName.png"
        homeworkContent?.page = homeworks.size
        homeworkContent?.state = 0

        page = homeworks.size

        val id = HomeworkContentDaoManager.getInstance().insertOrReplaceGetId(homeworkContent)
        homeworkContent?.id = id
        homeworks.add(homeworkContent!!)

        DataUpdateManager.createDataUpdate(2, id.toInt(), 2, homeworkTypeId, 2, Gson().toJson(homeworkContent), path)
    }


    //作业提交
    private fun commit() {
        if (messages.size == 0 || homeworkContent?.state != 0)
            return
        DrawingCommitDialog(this, getCurrentScreenPos(), messages).builder()
            ?.setOnDialogClickListener {
                homeworkCommit = it
                showLoading()
                commitItems.clear()
                for (i in homeworkCommit?.contents!!) {
                    if (i > homeworks.size) {
                        hideLoading()
                        showToast(R.string.toast_page_inexistence)
                        return@setOnDialogClickListener
                    }
                    val homework = homeworks[i - 1]
                    //异步合图后排序
                    Thread {
                        val path = saveImage(homework)
                        commitItems.add(ItemList().apply {
                            id = i
                            url = path
                        })
                        if (commitItems.size == homeworkCommit?.contents!!.size) {
                            commitItems.sort()
                            mUploadPresenter.getToken()
                        }
                    }.start()
                }
            }
    }

    /**
     * 合图
     */
    private fun saveImage(homework: HomeworkContentBean): String {
        val resId = ToolUtils.getImageResId(this, homeworkType?.contentResId)
        val oldBitmap = BitmapFactory.decodeResource(resources, resId)
        val drawPath = homework.path
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        val drawBitmap = BitmapFactory.decodeFile(drawPath, options)
        if (drawBitmap != null) {
            val mergeBitmap = BitmapUtils.mergeBitmap(oldBitmap, drawBitmap)
            BitmapUtils.saveBmpGallery(this, mergeBitmap, drawPath)
        }
        FileUtils.deleteFile(File(drawPath.replace("png", "tch")))

        return drawPath
    }

    override fun setDrawingTitle_a(title: String) {
        homeworkContent_a?.title = title
        homeworks[page - 1].title = title
        HomeworkContentDaoManager.getInstance().insertOrReplace(homeworkContent_a)
        DataUpdateManager.editDataUpdate(2, homeworkContent_a?.id!!.toInt(), 2, homeworkTypeId, Gson().toJson(homeworkContent_a))
    }

    override fun setDrawingTitle_b(title: String) {
        homeworkContent?.title = title
        homeworks[page].title = title
        HomeworkContentDaoManager.getInstance().insertOrReplace(homeworkContent)
        DataUpdateManager.editDataUpdate(2, homeworkContent?.id!!.toInt(), 2, homeworkTypeId, Gson().toJson(homeworkContent))
    }

    override fun onNetworkConnectionSuccess() {
        commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkUtil(this).toggleNetwork(false)
    }

}
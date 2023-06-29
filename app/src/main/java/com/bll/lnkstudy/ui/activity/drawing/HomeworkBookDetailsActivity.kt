package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.EinkPWInterface
import android.view.View
import android.widget.ImageView
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.manager.HomeworkBookDaoManager
import com.bll.lnkstudy.mvp.model.CatalogChild
import com.bll.lnkstudy.mvp.model.CatalogMsg
import com.bll.lnkstudy.mvp.model.CatalogParent
import com.bll.lnkstudy.mvp.model.homework.HomeworkBookBean
import com.bll.lnkstudy.utils.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_book_details_drawing.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import org.greenrobot.eventbus.EventBus
import java.io.File


class HomeworkBookDetailsActivity : BaseDrawingActivity() {

    //屏幕当前位置
    private var book: HomeworkBookBean? = null
    private var catalogMsg: CatalogMsg? = null
    private var catalogs = mutableListOf<MultiItemEntity>()
    private var parentItems = mutableListOf<CatalogParent>()
    private var childItems = mutableListOf<CatalogChild>()

    private var pageCount = 0
    private var page = 0 //当前页码


    override fun layoutId(): Int {
        return R.layout.ac_book_details_drawing
    }

    override fun initData() {
        val id = intent.getIntExtra("book_id", 0)
        book = HomeworkBookDaoManager.getInstance().queryBookByID(id)
        if (book == null) return
        page = book?.pageIndex!!
        val catalogFilePath = FileAddress().getPathTextbookCatalog(book?.bookPath!!)
        if (FileUtils.isExist(catalogFilePath))
        {
            val catalogMsgStr = FileUtils.readFileContent(FileUtils.file2InputStream(File(catalogFilePath)))
            catalogMsg = Gson().fromJson(catalogMsgStr, CatalogMsg::class.java)

            for (item in catalogMsg?.contents!!) {
                val catalogParent = CatalogParent()
                catalogParent.title = item.title
                catalogParent.pageNumber = item.pageNumber
                catalogParent.picName = item.picName
                for (ite in item.subItems) {
                    val catalogChild = CatalogChild()
                    catalogChild.title = ite.title
                    catalogChild.pageNumber = ite.pageNumber
                    catalogChild.picName = ite.picName

                    catalogParent.addSubItem(catalogChild)
                    childItems.add(catalogChild)
                }
                parentItems.add(catalogParent)
                catalogs.add(catalogParent)
            }
        }

    }

    override fun initView() {
        setDrawingTitleClick(false)
        pageCount = catalogMsg?.totalCount!!
        tv_title_a.text=book?.bookName
        tv_title_b.text=book?.bookName

        changeExpandView()

        changeContent()
        bindClick()
    }


    private fun bindClick() {

        iv_expand.setOnClickListener {
            onChangeExpandContent()
        }
        iv_expand_a.setOnClickListener {
            onChangeExpandContent()
        }
        iv_expand_b.setOnClickListener {
            onChangeExpandContent()
        }

        iv_catalog.setOnClickListener {
            DrawingCatalogDialog(this, catalogs, 1).builder()
                ?.setOnDialogClickListener { position ->
                    page = position - 1
                    changeContent()
                }

        }
    }

    override fun onPageUp() {
        if (isExpand) {
            if (page > 1) {
                page -= 2
            } else {
                page = 0
            }
        } else {
            if (page > 0) {
                page -= 1
            }
        }
        changeContent()
    }

    override fun onPageDown() {
        page += if (isExpand) 2 else 1
        changeContent()
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        moveToScreen(isExpand)
        changeExpandView()
        changeContent()
    }

    //单屏、全屏内容切换
    private fun changeExpandView() {
        showView(v_content_a,ll_page_content_a)
        iv_expand.visibility=if (isExpand) View.GONE else View.VISIBLE
        v_content_b.visibility = if (isExpand) View.VISIBLE else View.GONE
        ll_page_content_b.visibility = if (isExpand) View.VISIBLE else View.GONE
        v_empty.visibility = if (isExpand) View.VISIBLE else View.GONE
        iv_tool_right.visibility = if (isExpand) View.VISIBLE else View.GONE
        if (isExpand){
            if (screenPos==1){
                showView(iv_expand_a)
                disMissView(iv_expand_b)
            }
            else{
                showView(iv_expand_b)
                disMissView(iv_expand_a)
            }
        }
    }

    /**
     * 更新内容
     */
    private fun changeContent() {
        //如果页码超出 则全屏展示最后两页
        if (page >= pageCount - 1) {
            page = if (isExpand) pageCount - 2 else pageCount - 1
        }

        tv_page_a.text = "${page + 1}/$pageCount"
        loadPicture(page, elik_a!!, v_content_a)
        if (isExpand) {
            tv_page_b.text = "${page + 1 + 1}/$pageCount"
            loadPicture(page + 1, elik_b!!, v_content_b)
        }

        //设置当前展示页
        book?.pageUrl = getIndexFile(page)?.path
    }

    //加载图片
    private fun loadPicture(index: Int, elik: EinkPWInterface, view: ImageView) {
        val showFile = getIndexFile(index)
        if (showFile != null) {
            val simpleTarget = object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    view.background = resource
                }
            }
            Glide.with(this)
                .load(showFile)
                .skipMemoryCache(false)
                .fitCenter().into(simpleTarget)

            val drawPath = book?.bookDrawPath+"/${index+1}.tch"
            elik.setLoadFilePath(drawPath, true)
            elik.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
                override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
                }

                override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
                }

                override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                    elik.saveBitmap(true) {}
                    if (File(drawPath).exists()){
                        DataUpdateManager.editDataUpdate(8,book?.bookId!!,2,book?.bookId!!)
                    }
                    else{
                        //创建增量更新
                        DataUpdateManager.createDataUpdate(8,book?.bookId!!,2,book?.bookId!!
                            ,"",book?.bookDrawPath!!)
                    }

                }

            })
        }
    }

    //获得图片地址
    private fun getIndexFile(index: Int): File? {
        val path = FileAddress().getPathTextbookPicture(book?.bookPath!!)
        val listFiles = FileUtils.getFiles(path)
        return listFiles[index]
    }

    override fun onDestroy() {
        super.onDestroy()
        book?.pageIndex = page
        HomeworkBookDaoManager.getInstance().insertOrReplaceBook(book)
        EventBus.getDefault().post(TEXT_BOOK_EVENT)
    }

    override fun changeScreenPage() {
        if (isExpand) {
            onChangeExpandContent()
        }
    }

}
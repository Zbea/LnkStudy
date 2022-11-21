package com.bll.lnkstudy.ui.activity.drawing

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import android.view.View
import android.widget.ImageView
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.DrawingCatalogDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.mvp.model.CatalogChildBean
import com.bll.lnkstudy.mvp.model.CatalogMsg
import com.bll.lnkstudy.mvp.model.CatalogParentBean
import com.bll.lnkstudy.utils.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_book_details.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
import org.greenrobot.eventbus.EventBus
import java.io.File


class BookDetailsActivity : BaseActivity() {

    //屏幕当前位置
    private var book: Book? = null
    private var catalogMsg: CatalogMsg? = null
    private var catalogs = mutableListOf<MultiItemEntity>()
    private var parentItems = mutableListOf<CatalogParentBean>()
    private var childItems = mutableListOf<CatalogChildBean>()

    private var pageCount = 0
    private var page = 0 //当前页码

    override fun layoutId(): Int {
        return R.layout.ac_book_details
    }

    override fun initData() {
        val id = intent.getIntExtra("book_id", 0)
        book = BookGreenDaoManager.getInstance().queryBookByBookID(id.toLong())
        page = book?.pageIndex!!
        if (book == null) return
        val cataLogFilePath = FileAddress().getPathBookCatalog(book?.bookPath!!)
        val cataMsgStr =
            FileUtils.readFileContent(FileUtils.file2InputStream(File(cataLogFilePath)))
        catalogMsg = Gson().fromJson(cataMsgStr, CatalogMsg::class.java)
        if (catalogMsg == null) return

        for (item in catalogMsg?.contents!!) {
            var catalogParentBean = CatalogParentBean()
            catalogParentBean.title = item.title
            catalogParentBean.pageNumber = item.pageNumber
            catalogParentBean.picName = item.picName
            for (ite in item.subItems) {
                var catalogChildBean = CatalogChildBean()
                catalogChildBean.title = ite.title
                catalogChildBean.pageNumber = ite.pageNumber
                catalogChildBean.picName = ite.picName

                catalogParentBean.addSubItem(catalogChildBean)
                childItems.add(catalogChildBean)
            }
            parentItems.add(catalogParentBean)
            catalogs.add(catalogParentBean)
        }
    }

    override fun initView() {
        pageCount = catalogMsg?.totalCount!!

        changeExpandView()
        getElik()

        changeContent()
        bindClick()
    }


    private fun bindClick() {

        iv_expand.setOnClickListener {
            changeExpandContent()
        }
        iv_expand_a.setOnClickListener {
            changeExpandContent()
        }
        iv_expand_b.setOnClickListener {
            changeExpandContent()
        }

        iv_catalog.setOnClickListener {
            DrawingCatalogDialog(this, catalogs, 1).builder()
                ?.setOnDialogClickListener { position ->
                    page = position - 1
                    changeContent()
                }

        }

        btn_page_up.setOnClickListener {
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

        btn_page_down.setOnClickListener {
            page += if (isExpand) 2 else 1
            changeContent()
        }
    }

    /**
     * 获取elik实例
     */
    private fun getElik(){
        elik_a = v_content_a.pwInterFace
        elik_b = v_content_b.pwInterFace
    }

    /**
     * 切换屏幕
     */
    private fun changeExpandContent(){
        changeErasure()
        isExpand=!isExpand
        moveToScreen(isExpand)
        changeExpandView()
        changeContent()
    }

    //单屏、全屏内容切换
    private fun changeExpandView() {
        iv_expand.visibility=if (isExpand) View.GONE else View.VISIBLE
        ll_page_content_a.visibility = View.VISIBLE
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

        //设置当前展示页以及前一页
        if (page == 0) {
            book?.pageUpUrl = getIndexFile(page)?.path
            book?.pageUrl = getIndexFile(page)?.path
        } else {
            book?.pageUpUrl = getIndexFile(page - 1)?.path
            book?.pageUrl = getIndexFile(page)?.path
        }

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
                .thumbnail(0.1f).fitCenter().into(simpleTarget)

            val drawPath = showFile?.path.replace(".jpg", ".tch")
            elik?.setLoadFilePath(drawPath, true)
            elik?.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
                override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
                }

                override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
                }

                override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                    elik?.saveBitmap(true) {}
                }

            })
        }
    }

    //获得图片地址
    private fun getIndexFile(index: Int): File? {
        val path = FileAddress().getPathBookPicture(book?.bookPath!!)
        val listFiles = FileUtils.getFiles(path, ".jpg")
        return if (listFiles.isNullOrEmpty()) null else listFiles[index]
    }

    override fun onDestroy() {
        super.onDestroy()
        book?.time = System.currentTimeMillis()
        book?.pageIndex = page
        BookGreenDaoManager.getInstance().insertOrReplaceBook(book)
        if (book?.type != "0")
            EventBus.getDefault().post(BOOK_EVENT)
        else
            EventBus.getDefault().post(TEXT_BOOK_EVENT)
    }

    override fun changeScreenPage() {
        if (isExpand) {
            changeExpandContent()
        }
    }

    override fun onErasure() {
        if (isExpand){
                elik_a?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
                elik_b?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
        }
        else{
                elik_a?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
        }
    }

}
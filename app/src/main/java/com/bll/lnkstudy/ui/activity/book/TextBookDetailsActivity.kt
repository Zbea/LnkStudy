package com.bll.lnkstudy.ui.activity.book

import android.view.EinkPWInterface
import android.widget.ImageView
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogBookDialog
import com.bll.lnkstudy.manager.DataUpdateDaoManager
import com.bll.lnkstudy.manager.TextbookGreenDaoManager
import com.bll.lnkstudy.mvp.model.book.TextbookBean
import com.bll.lnkstudy.mvp.model.calalog.CatalogChildBean
import com.bll.lnkstudy.mvp.model.calalog.CatalogMsg
import com.bll.lnkstudy.mvp.model.calalog.CatalogParentBean
import com.bll.lnkstudy.utils.FileUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import org.greenrobot.eventbus.EventBus
import java.io.File


class TextBookDetailsActivity : BaseDrawingActivity(){

    private var book: TextbookBean? = null
    private var catalogMsg: CatalogMsg? = null
    private var catalogs = mutableListOf<MultiItemEntity>()
    private var startCount=0
    private var page = 0 //当前页码
    private var bookId=0


    override fun layoutId(): Int {
        return R.layout.ac_drawing
    }

    override fun initData() {
        bookId=intent.getIntExtra("bookId",0)
        book = TextbookGreenDaoManager.getInstance().queryTextBookByID(bookId)
        if (book == null) return
        page = book?.pageIndex!!
        val catalogFilePath = FileAddress().getPathBookCatalog(book?.bookPath!!)
        if (FileUtils.isExist(catalogFilePath))
        {
            val catalogMsgStr = FileUtils.readFileContent(FileUtils.file2InputStream(File(catalogFilePath)))
            catalogMsg = Gson().fromJson(catalogMsgStr, CatalogMsg::class.java)
            if (catalogMsg!=null){
                for (item in catalogMsg?.contents!!) {
                    val catalogParentBean = CatalogParentBean()
                    catalogParentBean.title = item.title
                    catalogParentBean.pageNumber = item.pageNumber
                    catalogParentBean.picName = item.picName
                    for (ite in item.subItems) {
                        val catalogChildBean = CatalogChildBean()
                        catalogChildBean.title = ite.title
                        catalogChildBean.pageNumber = ite.pageNumber
                        catalogChildBean.picName = ite.picName
                        catalogParentBean.addSubItem(catalogChildBean)
                    }
                    catalogs.add(catalogParentBean)
                }
                pageCount =  catalogMsg?.totalCount!!
                startCount =  if (catalogMsg?.startCount!!-1<0)0 else catalogMsg?.startCount!!-1
            }
        }
        else{
            pageCount=FileUtils.getFiles(FileAddress().getPathBookPicture(book?.bookPath!!)).size
        }
    }

    override fun initView() {
        disMissView(iv_btn)

        onContent()
    }

    override fun onPageUp() {
        if (page > 0) {
            page -= if(isExpand)2 else 1
            onContent()
        }
    }

    override fun onPageDown() {
        if (page<pageCount-1){
            page+=if(isExpand)2 else 1
            onContent()
        }
    }

    override fun onCatalog() {
        CatalogBookDialog(this,screenPos, getCurrentScreenPos(),catalogs, startCount).builder().setOnDialogClickListener { pageNumber ->
            if (page != pageNumber - 1) {
                page = pageNumber - 1
                onContent()
            }
        }
    }

    override fun onChangeExpandContent() {
        changeErasure()
        isExpand=!isExpand
        moveToScreen(isExpand)
        onChangeExpandView()
        onContent()
    }

    /**
     * 更新内容
     */
    override fun onContent() {
        if (pageCount==0)
            return
        if (page<0)
            page=0
        if (page>=pageCount){
            page=pageCount-1
        }
        if (page>pageCount-2&&isExpand)
            page=pageCount-2

        if (isExpand){
            val page_up=page+1//上一页页码
            loadPicture(page, elik_a!!, v_content_a!!)
            loadPicture(page_up, elik_b!!, v_content_b!!)

            if (screenPos== Constants.SCREEN_RIGHT){
                setPageCurrent(page,tv_page_a,tv_page_total_a)
                setPageCurrent(page_up,tv_page,tv_page_total)
            }
            else{
                setPageCurrent(page,tv_page,tv_page_total)
                setPageCurrent(page_up,tv_page_a,tv_page_total_a)
            }
        }
        else{
            loadPicture(page, elik_b!!, v_content_b!!)
            setPageCurrent(page,tv_page,tv_page_total)
        }
    }

    /**
     * 设置当前页面页码
     */
    private fun setPageCurrent(currentPage:Int,tvPage:TextView,tvPageTotal: TextView){
        tvPage.text = if (currentPage>=startCount) "${currentPage-startCount+1}" else ""
        tvPageTotal.text=if (currentPage>=startCount) "${pageCount-startCount}" else ""
    }

    //加载图片
    private fun loadPicture(index: Int, elik: EinkPWInterface, view: ImageView) {
        val showFile = FileUtils.getIndexFile(book?.bookPath,index)
        if (showFile != null) {
            MethodManager.setImageFile(showFile.path,view)
            val drawPath = book?.bookDrawPath+"/${index+1}.png"
            elik.setLoadFilePath(drawPath, true)
        }
    }

    override fun onElikSava_a() {
        refreshDataUpdate()
    }

    override fun onElikSava_b() {
        refreshDataUpdate()
    }

    /**
     * 刷新增量更新
     */
    private fun refreshDataUpdate(){
        val item= DataUpdateDaoManager.getInstance().queryBean(1,bookId,2,bookId)
        if (item==null){
            DataUpdateManager.createDataUpdateDrawing(1,bookId,2,bookId,book?.bookDrawPath!!)
        }
        else{
            DataUpdateManager.editDataUpdate(1,bookId,2,bookId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        book?.pageIndex = page
        book?.pageUrl = FileUtils.getIndexFile(book?.bookPath,page).path
        TextbookGreenDaoManager.getInstance().insertOrReplaceBook(book)
        EventBus.getDefault().post(TEXT_BOOK_EVENT)
    }

}
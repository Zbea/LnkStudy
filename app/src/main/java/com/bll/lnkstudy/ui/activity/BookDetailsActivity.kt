package com.bll.lnkstudy.ui.activity

import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.Constants.Companion.BOOK_PICTURE_FILES
import com.bll.lnkstudy.Constants.Companion.CATALOG_TXT
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.mvp.model.CatalogChildBean
import com.bll.lnkstudy.mvp.model.CatalogMsg
import com.bll.lnkstudy.mvp.model.CatalogParentBean
import com.bll.lnkstudy.ui.adapter.BookCatalogAdapter
import com.bll.utilssdk.utils.FileUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_book_details.*
import org.greenrobot.eventbus.EventBus
import java.io.File

class BookDetailsActivity:BaseActivity() {
    private var book: Book?=null
    private var catalogMsg: CatalogMsg?=null
    private var mAdapter:BookCatalogAdapter?=null
    private var catalogs= mutableListOf<MultiItemEntity>()
    private var parentItems= mutableListOf<CatalogParentBean>()
    private var childItems= mutableListOf<CatalogChildBean>()

    private var pageCount = 1
    private var pageIndex = 1 //当前页码

    private var isScreen=false //是否全屏

    private var elik_a: EinkPWInterface?=null
    private var elik_b: EinkPWInterface?=null

    override fun layoutId(): Int {
        return R.layout.ac_book_details
    }

    override fun initData() {
        val id=intent.getLongExtra("book_id",0)
        book = BookGreenDaoManager.getInstance(this).queryBookByBookID(id)
        pageIndex=book?.pageIndex!!
        if (book==null) return
        val cataLogFilePath =File(book?.bookPath).path + File.separator + CATALOG_TXT
        val cataMsgStr = FileUtils.readFileContent(FileUtils.file2InputStream(File(cataLogFilePath)))
        catalogMsg=Gson().fromJson(cataMsgStr, CatalogMsg::class.java)
        if (catalogMsg==null) return

        for (item in catalogMsg?.contents!!)
        {
            var catalogParentBean=CatalogParentBean()
            catalogParentBean.title=item.title
            catalogParentBean.pageNumber=item.pageNumber
            catalogParentBean.picName=item.picName
            for (ite in item.subItems){
                var catalogChildBean= CatalogChildBean()
                catalogChildBean.title=ite.title
                catalogChildBean.pageNumber=ite.pageNumber
                catalogChildBean.picName=ite.picName

                catalogParentBean.addSubItem(catalogChildBean)
                childItems.add(catalogChildBean)
            }
            parentItems.add(catalogParentBean)
            catalogs.add(catalogParentBean)
        }
    }

    override fun initView() {
        if (catalogMsg!=null){
            setPageTitle(catalogMsg?.title!!)
            pageCount=catalogMsg?.totalCount!!
        }

        elik_a=v_content_a.pwInterFace
        elik_b=v_content_b.pwInterFace

        selectScreen()

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = BookCatalogAdapter(catalogs)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnCatalogClickListener(object : BookCatalogAdapter.onCatalogClickListener {
            override fun onParentClick(page: Int) {
                pageIndex=page
                selectScreen()
            }
            override fun onChildClick(page: Int) {
                pageIndex=page
                selectScreen()
            }
        })

        bindClick()
    }

    private fun bindClick(){

        iv_screen.setOnClickListener {
            if (isScreen){
                isScreen=false
                v_content_b.visibility=View.GONE
                tv_page_b.visibility=View.GONE
                this.moveToScreenPanel(SCREEN_PANEL_A)
            }
            else{
                isScreen=true
                v_content_b.visibility=View.VISIBLE
                tv_page_b.visibility=View.VISIBLE
                this.moveToScreenPanel(SCREEN_PANEL_FULL)
            }

        }

        iv_catalog.setOnClickListener {
            if (ll_catalog.visibility== View.GONE){
                showView(ll_catalog)
                elik_a?.setPWEnabled(false)
                elik_b?.setPWEnabled(false)
            }
            else{
                disMissView(ll_catalog)
                elik_a?.setPWEnabled(true)
                elik_b?.setPWEnabled(true)
            }
        }

        iv_pen.setOnClickListener {
            elik_a?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            elik_a?.penSettingWidth=2
            elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            elik_b?.penSettingWidth=2
        }

        iv_erase.setOnClickListener {
            elik_a?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
            elik_b?.drawObjectType= PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
        }

        //用来设置点击试图其他位置目录关闭
        ll_content.setOnClickListener {
            if (ll_catalog.visibility== View.VISIBLE)
            {
                disMissView(ll_catalog)
                elik_a?.setPWEnabled(true)
                elik_b?.setPWEnabled(true)
            }
        }


        btn_page_up.setOnClickListener {
            if (isScreen){
                if (pageIndex>2){
                    pageIndex-=3
                    updateScreenFull()
                }
            }
            else{
                if (pageIndex>1){
                    pageIndex-=1
                    updateScreenA()
                }
            }

        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1

                selectScreen()
            }
        }
    }


    private fun selectScreen(){
        if (isScreen){
            updateScreenFull()
        }
        else{
            updateScreenA()
        }
    }

    //单屏翻页
    private fun updateScreenA(){
        tv_page_a.text="$pageIndex/$pageCount"
        loadPicture(pageIndex,elik_a!!,v_content_a)
    }


    //向前翻页
    private fun updateScreenFull(){

        if (pageIndex<1){
            //当处于第一页
            pageIndex=1
            tv_page_a.text="$pageIndex/$pageCount"
            loadPicture(pageIndex,elik_a!!,v_content_a)

            pageIndex += 1//第二屏页码加一
            tv_page_b.text="$pageIndex/$pageCount"
            loadPicture(pageIndex,elik_b!!,v_content_b)
        }
        else if (pageIndex>0&&pageIndex+1<=pageCount)
        {
            tv_page_a.text="$pageIndex/$pageCount"
            loadPicture(pageIndex,elik_a!!,v_content_a)

            pageIndex += 1//第二屏页码加一
            tv_page_b.text="$pageIndex/$pageCount"
            loadPicture(pageIndex,elik_b!!,v_content_b)
        }
        else{
            //当翻页后处于倒数一页
            pageIndex=pageCount-1
            tv_page_a.text="$pageIndex/$pageCount"
            loadPicture(pageIndex,elik_a!!,v_content_a)

            pageIndex=pageCount
            tv_page_a.text="$pageIndex/$pageCount"
            loadPicture(pageIndex,elik_b!!,v_content_b)
        }

    }



    //加载图片
    private fun loadPicture(index: Int,elik:EinkPWInterface,view:ImageView) {
        val showFile = getIndexFile(index)
        book?.pageUrl=showFile.path //设置当前页面路径
        Glide.with(this)
            .load(showFile)
            .thumbnail(0.1f).into(view)

        val drawPath=showFile.path.replace(".jpg",".tch")
        elik?.setLoadFilePath(drawPath,true)
    }

    //获得图片地址
    private fun getIndexFile(index: Int): File {
        val path=book?.bookPath + File.separator + BOOK_PICTURE_FILES
        val listFiles = FileUtils.getFiles(path,".jpg")
        return listFiles[index - 1]
    }

    override fun onDestroy() {
        super.onDestroy()
        book?.time=System.currentTimeMillis()
        book?.pageIndex=pageIndex
        BookGreenDaoManager.getInstance(this).insertOrReplaceBook(book)
        if (book?.type!="0")
            EventBus.getDefault().post(BOOK_EVENT)
        else
            EventBus.getDefault().post(TEXT_BOOK_EVENT)
    }



}
package com.bll.lnkstudy.ui.activity

import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.Constants.Companion.CATALOG_TXT
import com.bll.lnkstudy.Constants.Companion.PICTURE_FILES
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.mvp.model.CatalogMsg
import com.bll.lnkstudy.mvp.model.CatalogChildBean
import com.bll.lnkstudy.mvp.model.CatalogParentBean
import com.bll.lnkstudy.ui.adapter.BookCatalogAdapter
import com.bll.utilssdk.utils.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_book_details.*
import kotlinx.android.synthetic.main.ac_book_details.rv_list
import kotlinx.android.synthetic.main.common_page_number.*
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
            tv_page_total.text=pageCount.toString()
        }

        updateUI()

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = BookCatalogAdapter(catalogs)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnCatalogClickListener(object : BookCatalogAdapter.onCatalogClickListener {
            override fun onParentClick(page: Int) {
                pageIndex=page
                updateUI()
            }
            override fun onChildClick(page: Int) {
                pageIndex=page
                updateUI()
            }
        })

        bindClick()
    }

    private fun bindClick(){

        iv_catalog.setOnClickListener {
            if (ll_catalog.visibility== View.GONE){
                showView(ll_catalog)
            }
            else{
                disMissView(ll_catalog)
            }
        }
        //用来设置点击试图其他位置目录关闭
        ll_content.setOnClickListener {
            if (ll_catalog.visibility== View.VISIBLE)
                disMissView(ll_catalog)
        }

        v_content.setOnClickListener {
            if (ll_catalog.visibility== View.VISIBLE)
            {
                disMissView(ll_catalog)
            }
            else{
                if(pageIndex<pageCount){
                    pageIndex+=1
                    updateUI()
                }
            }
        }

        btn_page_up.setOnClickListener {
            if (pageIndex>1){
                if(pageIndex<pageCount){
                    pageIndex-=1
                    updateUI()
                }
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                updateUI()
            }
        }

    }

    //刷新显示的页码和内容
    private fun updateUI(){
        tv_page_current.text=pageIndex.toString()
        loadPicture(pageIndex)
    }

    //加载图片
    private fun loadPicture(index: Int) {
        val showFile = getIndexFile(index)
        book?.pageUrl=showFile.path
        val simpleTarget = object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                v_content.background=resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        }
        Glide.with(this)
            .load(showFile)
            .thumbnail(0.1f).centerCrop().into(v_content)
    }

    //获得图片地址
    private fun getIndexFile(index: Int): File {
        val listFiles = FileUtils.getFiles(book?.bookPath + File.separator + PICTURE_FILES)
        return listFiles[index - 1]
    }

    override fun onDestroy() {
        super.onDestroy()
        book?.time=System.currentTimeMillis()
        book?.pageIndex=pageIndex
        BookGreenDaoManager.getInstance(this).insertOrReplaceBook(book)
        if (book?.type=="1")
            EventBus.getDefault().post(BOOK_EVENT)
    }

}
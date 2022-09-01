package com.bll.lnkstudy.ui.activity

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
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
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.utilssdk.utils.FileUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_book_details.*
import kotlinx.android.synthetic.main.common_drawing_bottom.*
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
    private var page = 1 //当前页码

    private var isExpand=false //是否全屏

    private var elik_a: EinkPWInterface?=null
    private var elik_b: EinkPWInterface?=null

    override fun layoutId(): Int {
        return R.layout.ac_book_details
    }

    override fun initData() {
        val id=intent.getLongExtra("book_id",0)
        book = BookGreenDaoManager.getInstance(this).queryBookByBookID(id)
        page=book?.pageIndex!!
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

        changeExpandView()

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = BookCatalogAdapter(catalogs)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnCatalogClickListener(object : BookCatalogAdapter.onCatalogClickListener {
            override fun onParentClick(page: Int) {
                this@BookDetailsActivity.page =page
                selectScreen()
            }
            override fun onChildClick(page: Int) {
                this@BookDetailsActivity.page =page
                selectScreen()
            }
        })

        bindClick()
    }

    //单屏、全屏内容切换
    private fun changeExpandView(){
        tv_page_a.visibility = View.VISIBLE
        tv_page_b.visibility = if (isExpand) View.VISIBLE else View.GONE
        iv_tool_right.visibility=if (isExpand) View.VISIBLE else View.GONE
        v_content_b.visibility=if (isExpand) View.VISIBLE else View.GONE
//        if (isExpand) this.moveToScreenPanel(Activity.SCREEN_PANEL_FULL) else this.moveToScreenPanel(Activity.SCREEN_PANEL_A)
    }

    private fun bindClick(){

        iv_expand.setOnClickListener {
            isExpand=!isExpand
            changeExpandView()
        }

        iv_catalog.setOnClickListener {
            if (ll_catalog.visibility== View.GONE){
                showView(ll_catalog)
                setPWEnabled(false)
            }
            else{
                disMissView(ll_catalog)
                setPWEnabled(true)
            }
        }


        //用来设置点击试图其他位置目录关闭
        ll_content.setOnClickListener {
            if (ll_catalog.visibility== View.VISIBLE)
            {
                disMissView(ll_catalog)
                setPWEnabled(true)
            }
        }


        btn_page_up.setOnClickListener {
            if (isExpand){
                if (page>2){
                    page-=3
                    updateScreenFull()
                }
            }
            else{
                if (page>1){
                    page-=1
                    updateScreenA()
                }
            }

        }

        btn_page_down.setOnClickListener {
            if(page<pageCount){
                page+=1

                selectScreen()
            }
        }
    }

    //设置手绘是否可以绘制
    private fun setPWEnabled(boolean: Boolean){
        elik_a?.setPWEnabled(boolean)
        elik_b?.setPWEnabled(boolean)
    }


    private fun selectScreen(){
        if (isExpand){
            updateScreenFull()
        }
        else{
            updateScreenA()
        }
    }

    //单屏翻页
    private fun updateScreenA(){
        tv_page_a.text="$page/$pageCount"
        loadPicture(page,elik_a!!,v_content_a)
    }


    //向前翻页
    private fun updateScreenFull(){

        if (page<1){
            //当处于第一页
            page=1
            tv_page_a.text="$page/$pageCount"
            loadPicture(page,elik_a!!,v_content_a)

            page += 1//第二屏页码加一
            tv_page_b.text="$page/$pageCount"
            loadPicture(page,elik_b!!,v_content_b)
        }
        else if (page>0&&page+1<=pageCount)
        {
            tv_page_a.text="$page/$pageCount"
            loadPicture(page,elik_a!!,v_content_a)

            page += 1//第二屏页码加一
            tv_page_b.text="$page/$pageCount"
            loadPicture(page,elik_b!!,v_content_b)
        }
        else{
            //当翻页后处于倒数一页
            page=pageCount-1
            tv_page_a.text="$page/$pageCount"
            loadPicture(page,elik_a!!,v_content_a)

            page=pageCount
            tv_page_b.text="$page/$pageCount"
            loadPicture(page,elik_b!!,v_content_b)
        }

    }



    //加载图片
    private fun loadPicture(index: Int,elik:EinkPWInterface,view:ImageView) {
        val showFile = getIndexFile(index)
        if (showFile!=null){
            book?.pageUrl=showFile?.path //设置当前页面路径
            if (index>1){
                book?.pageUpUrl=getIndexFile(index-1)?.path
            }

            GlideUtils.setImageFile(this,showFile,view)

            val drawPath=showFile.path.replace(".jpg",".tch")
            elik?.setLoadFilePath(drawPath,true)
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
        val path=book?.bookPath + File.separator + BOOK_PICTURE_FILES
        val listFiles = FileUtils.getFiles(path,".jpg")
        if (listFiles.size==0)
            return null
        return listFiles[index - 1]
    }

    override fun onDestroy() {
        super.onDestroy()
        book?.time=System.currentTimeMillis()
        book?.pageIndex=page
        BookGreenDaoManager.getInstance(this).insertOrReplaceBook(book)
        if (book?.type!="0")
            EventBus.getDefault().post(BOOK_EVENT)
        else
            EventBus.getDefault().post(TEXT_BOOK_EVENT)
    }



}
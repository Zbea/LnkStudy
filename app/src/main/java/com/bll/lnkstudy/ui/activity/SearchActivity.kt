package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.*
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.PopupList
import com.bll.lnkstudy.dialog.PrivacyPasswordDialog
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.SearchBean
import com.bll.lnkstudy.mvp.model.book.BookBean
import com.bll.lnkstudy.ui.activity.drawing.FileDrawingActivity
import com.bll.lnkstudy.ui.adapter.SearchAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import kotlinx.android.synthetic.main.ac_search.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlin.math.ceil

class SearchActivity : BaseAppCompatActivity() {

    private var typeId=0
    private var popups= mutableListOf<PopupBean>()
    private val searchBeans= mutableListOf<SearchBean>()
    private var mAdapter: SearchAdapter?=null
    private var listMap=HashMap<Int,MutableList<SearchBean>>()

    override fun layoutId(): Int {
        return R.layout.ac_search
    }

    override fun initData() {
        pageSize=8
        for (i in DataBeanManager.searchType.indices){
            popups.add(PopupBean(i, DataBeanManager.searchType[i],i==0))
        }
    }

    override fun initView() {
        tv_class?.text=popups[0].name

        et_search?.doAfterTextChanged {
            val titleStr=it.toString()
            if (titleStr.isNotEmpty()){
                searchContent(titleStr)
            }
        }
        tv_class.setOnClickListener {
            showClassTypeView()
        }

        rv_list.layoutManager = GridLayoutManager(this, 4)//创建布局管理
        mAdapter= SearchAdapter(R.layout.item_search, searchBeans)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list.addItemDecoration(SpaceGridItemDeco1(4,0, 40))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val item= listMap[pageIndex]?.get(position)
            onItemClick(item!!)
        }

    }

    /**
     * 点击事件
     */
    private fun onItemClick(item:SearchBean){
        when(item.category){
            0->{
                val bookBean=Gson().fromJson(item.listJson, BookBean::class.java)
                MethodManager.gotoBookDetails(this,bookBean,screenPos)
                finish()
            }
            1->{
                MethodManager.gotoTextBookDetails(this,item.id)
                finish()
            }
            2->{
                val typeItem= HomeworkTypeDaoManager.getInstance().queryAllById(item.type)
                when(item.state){
                    1->{
                        MethodManager.gotoHomeworkReelDrawing(this,typeItem,item.page)
                    }
                    2->{
                        MethodManager.gotoHomeworkDrawing(this,typeItem,item.page)
                    }
                    3->{
                        MethodManager.gotoHomeworkRecord(this,typeItem)
                    }
                    4->{
                        val typeBean=HomeworkTypeDaoManager.getInstance().queryByBookId(item.type)
                        MethodManager.gotoHomeworkBookDetails(this,typeBean)
                    }
                    5->{
                        customStartActivity(
                            Intent(this, FileDrawingActivity::class.java)
                            .putExtra("pageIndex", Constants.DEFAULT_PAGE)
                            .putExtra("pagePath", FileAddress().getPathScreenHomework(typeItem.name,typeItem.grade))
                        )
                    }
                }
                finish()
            }
            3->{
                MethodManager.gotoPaperDrawing(this,item.course,item.type,item.page)
                finish()
            }
            4->{
                val note= NoteDaoManager.getInstance().queryBean(item.typeStr,item.noteStr)
                val privacyPassword=MethodManager.getPrivacyPassword(1)
                if (item.typeStr==getString(R.string.note_tab_diary)&&privacyPassword!=null&&!note.isCancelPassword)
                {
                    PrivacyPasswordDialog(this).builder().setOnDialogClickListener{
                        MethodManager.gotoNoteDrawing(this,note,0,item.page)
                        finish()
                    }
                }
                else{
                    MethodManager.gotoNoteDrawing(this,note,0,item.page)
                    finish()
                }
            }
        }
    }



    /**
     * 分类选择
     */
    private fun showClassTypeView(){
        PopupList(this,popups,tv_class,tv_class.width,5).builder()
            .setOnSelectListener {
                typeId=it.id
                tv_class.text=it.name

                val titleStr=et_search.text.toString()
                if (titleStr.isNotEmpty()){
                    searchContent(titleStr)
                }
            }
    }

    /**
     * 搜索内容
     */
    private fun searchContent(titleStr:String){
        pageIndex=1
        searchBeans.clear()
        listMap.clear()
        when(typeId){
            0->{
                val books= BookGreenDaoManager.getInstance().search(titleStr)
                for (book in books){
                    searchBeans.add(SearchBean().apply {
                        category=0
                        id=book.bookId
                        title=book.bookName
                        imageUrl=book.imageUrl
                        path=book.bookPath
                        listJson= Gson().toJson(book)
                    })
                }
            }
            1->{
                val books= TextbookGreenDaoManager.getInstance().search(titleStr)
                for (book in books){
                    searchBeans.add(SearchBean().apply {
                        category=1
                        id=book.bookId
                        title=book.bookName
                        imageUrl=book.imageUrl
                        path=book.bookPath
                    })
                }
            }
            2->{
                val homeworkContents= HomeworkContentDaoManager.getInstance().search(titleStr)
                for (item in homeworkContents){
                    searchBeans.add(SearchBean().apply {
                        category=2
                        title=item.title
                        state=2
                        course=item.course
                        type=item.homeworkTypeId
                        typeStr=item.typeStr
                        page=item.page
                    })
                }

                val paperContents= HomeworkPaperDaoManager.getInstance().search(titleStr)
                for (item in paperContents){
                    searchBeans.add(SearchBean().apply {
                        category=2
                        title=item.title
                        state=1
                        type=item.typeId
                        typeStr=item.type
                        course=item.course
                        page=item.page
                    })
                }

                val recordBeans= RecordDaoManager.getInstance().search(titleStr)
                for (item in recordBeans){
                    searchBeans.add(SearchBean().apply {
                        category=2
                        title=item.title
                        state=3
                        type=item.typeId
                        typeStr=item.typeStr
                        course=item.course
                        listJson= Gson().toJson(item)
                    })
                }

                val homeBooks= HomeworkBookDaoManager.getInstance().search(titleStr)
                for (item in homeBooks){
                    searchBeans.add(SearchBean().apply {
                        category=2
                        title=item.bookName
                        state=4
                        type=item.bookId
                        typeStr=item.bookName
                        course=DataBeanManager.getCourseStr(item.subject)
                        page=item.pageIndex
                    })
                }
            }
            3->{
                val paperContents= PaperDaoManager.getInstance().search(titleStr)
                for (item in paperContents){
                    searchBeans.add(SearchBean().apply {
                        category=3
                        title=item.title
                        type=item.typeId
                        typeStr=item.type
                        course=item.course
                        page=item.page
                    })
                }
            }
            4->{
                val noteContents= NoteContentDaoManager.getInstance().search(titleStr)
                for (item in noteContents){
                    searchBeans.add(SearchBean().apply {
                        category=4
                        title=item.title
                        noteStr=item.noteTitle
                        typeStr=item.typeStr
                        page=item.page
                        grade=item.grade
                    })
                }
            }
        }
        pageNumberView()
    }

    private fun pageNumberView(){
        val pageTotal=searchBeans.size //全部数量
        pageCount = ceil(pageTotal.toDouble()/ pageSize).toInt()//总共页码
        if (pageTotal==0)
        {
            ll_page_number.visibility= View.GONE
            mAdapter?.setNewData(searchBeans)
        }
        else{
            var toIndex= pageSize
            for(i in 0 until pageCount){
                val index=i* pageSize
                if(index+ pageSize>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
                    toIndex=pageTotal-index
                }
                val newList = searchBeans.subList(index,index+toIndex)
                listMap[i+1]=newList
            }
            fetchData()
        }
    }

    override fun fetchData() {
        mAdapter?.setNewData(listMap[pageIndex])
        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageCount.toString()
        ll_page_number.visibility= View.VISIBLE
    }

}
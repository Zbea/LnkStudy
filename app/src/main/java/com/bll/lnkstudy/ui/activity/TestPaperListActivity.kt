package com.bll.lnkstudy.ui.activity

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.mvp.model.CourseBean
import com.bll.lnkstudy.mvp.model.TestPaper
import com.bll.lnkstudy.mvp.model.TestPaperType
import com.bll.lnkstudy.ui.adapter.TestPaperAdapter
import com.bll.lnkstudy.utils.ImageDownLoadUtils
import com.bll.lnkstudy.widget.CustomImageView
import com.bll.lnkstudy.widget.SpaceGridItemDeco2
import kotlinx.android.synthetic.main.ac_testpaper_list.*
import kotlinx.android.synthetic.main.common_page_number.*

class TestPaperListActivity:BaseActivity() {

    private var courseBean:CourseBean?=null
    private var testPaperType:TestPaperType?=null
    private var testPapers= mutableListOf<TestPaper>()
    private var mAdapter:TestPaperAdapter?=null
    private var pageIndex=1 //当前页码
    private var bookMap=HashMap<Int,MutableList<TestPaper>>()

    override fun layoutId(): Int {
        return R.layout.ac_testpaper_list
    }

    override fun initData() {
        var bundle=intent.getBundleExtra("testPaper")
        courseBean= bundle?.getSerializable("course") as CourseBean
        testPaperType= bundle?.getSerializable("testPaperType") as TestPaperType

        var testPaper=TestPaper()
        testPaper.id=1
        testPaper.isPg=false
        testPaper.name="测试卷"
        testPaper.createDate=System.currentTimeMillis()

        var testPaper1=TestPaper()
        testPaper1.id=2
        testPaper1.isPg=true
        testPaper1.name="测试卷"
        testPaper1.rank= 5
        testPaper1.score= 98.5
        testPaper1.createDate=System.currentTimeMillis()


        testPapers.add(testPaper)
        testPapers.add(testPaper1)
        testPapers.add(testPaper1)
        testPapers.add(testPaper)
        testPapers.add(testPaper1)

    }

    override fun initView() {
        setPageTitle(courseBean?.name+testPaperType?.name)

        rv_list.layoutManager = GridLayoutManager(this,2)//创建布局管理
        mAdapter = TestPaperAdapter(R.layout.item_testpaper, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list.addItemDecoration(SpaceGridItemDeco2(20,20))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.iv_content1){

                ImageDownLoadUtils(this,testPapers[position].image,testPaperType?.namePath,testPapers[position].id.toString(),1)
                    .startDownload(object : ImageDownLoadUtils.ImageDownLoadCallBack {
                        override fun onDownLoadSuccess(bitmap: Bitmap?, path: String?) {
                            showLog(path!!)
                        }
                        override fun onDownLoadFailed() {
                        }
                    })

            }
        }

        pageNumberView()

    }

    //翻页处理
    private fun pageNumberView(){
        var pageTotal=testPapers.size //全部数量
        var pageCount=Math.ceil((pageTotal.toDouble()/10)).toInt()//总共页码
        if (pageTotal==0)
        {
            ll_page_number.visibility= View.GONE
            mAdapter?.notifyDataSetChanged()
            return
        }


        var toIndex=10
        for(i in 0 until pageCount){
            var index=i*10
            if(index+10>pageTotal){        //作用为toIndex最后没有12条数据则剩余几条newList中就装几条
                toIndex=pageTotal-index
            }
            var newList = testPapers.subList(index,index+toIndex)
            bookMap[i+1]=newList
        }

        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageCount.toString()
        upDateUI()

        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                upDateUI()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                upDateUI()
            }
        }

    }

    //刷新数据
    private fun upDateUI()
    {
        mAdapter?.setNewData(bookMap[pageIndex]!!)
        tv_page_current.text=pageIndex.toString()
    }

}
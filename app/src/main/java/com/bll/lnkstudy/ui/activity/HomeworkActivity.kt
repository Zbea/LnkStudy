package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.view.EinkPWInterface
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkDaoManager
import com.bll.lnkstudy.mvp.model.Homework
import com.bll.lnkstudy.mvp.model.HomeworkContent
import com.bll.lnkstudy.mvp.model.HomeworkType
import com.bll.lnkstudy.ui.adapter.HomeworkCatalogAdapter
import com.bll.lnkstudy.utils.StringUtils
import kotlinx.android.synthetic.main.ac_homework.*

class HomeworkActivity:BaseActivity() {

    private var elik: EinkPWInterface?=null
    private var mAdapter:HomeworkCatalogAdapter?=null

    private var courseId=0 //科目id
    private var homeworkTypeId=0//作业分组id
    private var homeworkType: HomeworkType?=null

    private var newHomework: Homework?=null //新创建作业
    private var homework: Homework?=null//当前作业
    private var homeworkContent: HomeworkContent?=null//当前作业内容
    private var homeworkContent_a: HomeworkContent?=null//a屏作业

    private var currentHomeworkContentLists=mutableListOf<HomeworkContent>() //新创建作业中的内容
    private var homeworkLists= mutableListOf<Homework>() //所有作业
    private var homeworkContentLists= mutableListOf<HomeworkContent>() //所有作业内容

    private var isScreen=false //是否是全屏

    private var page=0//页码
    private var pageTotal=1//全部页码

    override fun layoutId(): Int {
        return R.layout.ac_homework
    }

    override fun initData() {

        var bundle=intent.getBundleExtra("homeworkBundle")
        courseId= bundle?.getInt("courseId")!!
        homeworkType=bundle?.getSerializable("homework") as HomeworkType
        homeworkTypeId= homeworkType?.type!!

        homeworkLists=HomeworkDaoManager.getInstance(this).queryAllByType(courseId,homeworkTypeId)
        homeworkContentLists=HomeworkContentDaoManager.getInstance(this).queryAllByType(courseId,homeworkTypeId)

        if (homeworkLists.size==0){
            newHomeWork()
            newHomeWorkContent()
        }
        else{
            var hm=homeworkLists[homeworkLists.size-1]
            //如果上次作业已经结束 创建新的作业
            if (hm.isSave){
                newHomeWork()
                newHomeWorkContent()
            }
            else{
                //未做完作业继续 页面最后一张
                newHomework=hm
                homework=hm
                et_title.setText(newHomework?.title)
                homeworkContent=homeworkContentLists[homeworkContentLists.size-1]
                currentHomeworkContentLists=HomeworkContentDaoManager.getInstance(this).queryByID(newHomework?.id)
                page=homeworkContentLists.size-1
                pageTotal=homeworkContentLists.size
            }
        }

    }

    override fun initView() {

//        iv_content.setImageResource(homeworkType?.bgResId!!)//设置背景
//        elik=iv_content.pwInterFace

        updateUI()

        initRecyclerCatalog()

        tv_save.setOnClickListener {

            saveNewHomework()
            currentHomeworkContentLists.clear()

            et_title.setText("")

            newHomeWork()
            newHomeWorkContent()

            changeContent()
            mAdapter?.notifyDataSetChanged()

        }

        btn_page_down.setOnClickListener {

            if (page+1==pageTotal) {
                newHomeWorkContent()
            }
            else{
                page+=1
            }
            changeContent()
        }

        btn_page_up.setOnClickListener {

            if (isScreen){
                if (page>1){
                    page-=1
                    changeContent()
                }
            }else{
                if (page>0){
                    page-=1
                    changeContent()
                }
            }

        }

        iv_catalog.setOnClickListener {
            if (ll_catalog.visibility== View.VISIBLE)
            {
                disMissView(ll_catalog)
            }
            else{
                showView(ll_catalog)
            }
        }

        iv_screen.setOnClickListener {
            if (isScreen){
                isScreen=false
                ll_content_a.visibility=View.GONE
                iv_content_a.visibility=View.GONE
                tv_page_a.visibility=View.GONE
                homeworkContent_a==null
            }
            else{
                isScreen=true
                ll_content_a.visibility=View.VISIBLE
                iv_content_a.visibility=View.VISIBLE
                tv_page_a.visibility=View.VISIBLE
            }
            changeContent()
        }

    }


    //目录列表
    private fun initRecyclerCatalog(){
        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = HomeworkCatalogAdapter(R.layout.item_catalog_parent,homeworkLists)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            page=homeworkLists[position].page-1
            changeContent()
        }
    }

    //翻页内容更新切换
    private fun changeContent(){
        homeworkContent=homeworkContentLists[page]

        if (isScreen){
            if (page>0){
                homeworkContent_a =homeworkContentLists[page-1]
            }
            else{
                if (homeworkContentLists.size>1){
                    homeworkContent=homeworkContentLists[page+1]
                    homeworkContent_a=homeworkContentLists[page]
                    page=1
                }
                else{
                    homeworkContent_a=null
                }
            }
        }
        else{
            homeworkContent_a=null
        }

        var fdHomework=HomeworkDaoManager.getInstance(this).queryByID(homeworkContent?.homeworkId)

        if (fdHomework.id!=homework?.id){
            homework=fdHomework
            et_title.setText(homework?.title)
        }

        if (homework?.isSave == true){
            tv_save.visibility= View.GONE
            et_title.setFocusable(false)//不可编辑
            et_title.setFocusableInTouchMode(false)//不可编辑
        }
        else{
            tv_save.visibility=View.VISIBLE
            et_title.setFocusable(true)//可编辑
            et_title.setFocusableInTouchMode(true)//可编辑
        }

        updateUI()
    }


    @SuppressLint("SetTextI18n")
    private fun updateUI(){

//        elik?.saveBitmap(true){}
//        elik?.setLoadFilePath(pathContent,true)

        if (isScreen){

            tv_b.text=homeworkContent?.id.toString()
            tv_page_b.text=(page+1).toString()+"/"+pageTotal.toString()

            if (homeworkContent_a!=null){
                tv_a.text=homeworkContent_a?.id.toString()
                tv_page_a.text= "$page/$pageTotal"
            }

        }
        else{
            tv_b.text=homeworkContent?.id.toString()
            tv_page_b.text=(page+1).toString()+"/"+pageTotal.toString()
        }

    }


    //创建新的作业
    private fun newHomeWork(){

        newHomework= Homework()
        newHomework?.index=homeworkLists.size
        newHomework?.courseId=courseId
        newHomework?.startDate=System.currentTimeMillis()
        newHomework?.homeworkTypeId=homeworkType?.type
        newHomework?.bgResId=homeworkType?.bgResId
        newHomework?.state=0

        newHomework?.page=(homeworkContentLists.size-currentHomeworkContentLists.size)+1 //设置作业页码 作业内容的第一个

        HomeworkDaoManager.getInstance(this).insertOrReplace(newHomework)
        newHomework?.id=HomeworkDaoManager.getInstance(this).insertId

        newHomework?.path= Constants.HOMEWORK_PATH+"/$courseId"+"/${homeworkType?.type}"+"/${newHomework?.id}"

        homeworkLists.add(newHomework!!)
        homework=newHomework

    }

    //创建新的作业内容
    private fun newHomeWorkContent(){

        var date=StringUtils.longToString(System.currentTimeMillis())

        homeworkContent= HomeworkContent()
        homeworkContent?.courseId=courseId
        homeworkContent?.date=System.currentTimeMillis()
        homeworkContent?.homeworkTypeId=newHomework?.homeworkTypeId
        homeworkContent?.bgResId=newHomework?.bgResId
        homeworkContent?.homeworkId=newHomework?.id

        homeworkContent?.path="${newHomework?.path}/$date.tch"
        homeworkContent?.page=homeworkContentLists.size

        page=homeworkContentLists.size
        homeworkContentLists.add(homeworkContent!!)
        currentHomeworkContentLists.add(homeworkContent!!)

        HomeworkContentDaoManager.getInstance(this).insertOrReplace(homeworkContent)

        pageTotal=homeworkContentLists.size


    }

    //保存新增作业
    private fun saveNewHomework(){
        var s= et_title.text.toString()
        newHomework?.title=if (s.isNullOrEmpty()) "作业${newHomework?.index?.plus(1)}" else s
        newHomework?.isSave=true
        newHomework?.endDate=System.currentTimeMillis()
        newHomework?.count=currentHomeworkContentLists.size
        HomeworkDaoManager.getInstance(this).insertOrReplace(newHomework)
    }


    override fun onDestroy() {
        super.onDestroy()
        newHomework?.title=et_title.text.toString()
        HomeworkDaoManager.getInstance(this).insertOrReplace(newHomework)
    }


}
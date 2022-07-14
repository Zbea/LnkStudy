package com.bll.lnkstudy.ui.fragment

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.NoteBookAddDialog
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.mvp.model.HomeWorkType
import com.bll.lnkstudy.ui.adapter.HomeWorkAdapter
import com.bll.lnkstudy.utils.PopWindowUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_homework.*


/**
 * 作业
 */
class HomeWorkFragment : BaseFragment(){

    private var popWindow:PopWindowUtil?=null
    private var mAdapter:HomeWorkAdapter?=null

    private var courseID=0//当前科目id
    private var datas= mutableListOf<HomeWorkType>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_homework
    }

    override fun initView() {
        setPageTitle("作业")
        setDisBackShow()
        setShowHomework()

        ivHomework?.setOnClickListener {
            setTopSelectView()
        }

        tv_add.setOnClickListener {
            addHomeWorkType()
        }

        initRecyclerView()
        initTab()
        findDatas(true,0)
    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab(){

        val courses=DataBeanManager.getIncetance().courses

        for (item in courses){
            xtab?.newTab()?.setText(item.name)?.let { it -> xtab?.addTab(it) }
        }
        xtab?.getTabAt(1)?.select()
        xtab?.getTabAt(0)?.select()

        xtab?.setOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                courseID= tab?.position!!
                when(courseID){
                    0,2->{
                        findDatas(true,courseID)
                    }
                    else->{
                        findDatas(false,courseID)
                    }
                }

            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }

            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }

        })

    }


    private fun initRecyclerView(){
        mAdapter = HomeWorkAdapter(R.layout.item_homework, null)
        rv_list.layoutManager = GridLayoutManager(activity,3)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(0,90))

    }


    //查找分类数据
    private fun findDatas(islg: Boolean,courseID:Int){
        datas.clear()
        datas=DataBeanManager.getIncetance().getHomeWorkTypes(islg)

        mAdapter?.setNewData(datas)
    }

    //添加作业本
    private fun addHomeWorkType(){
        NoteBookAddDialog(requireContext(),"新建作业本","","请输入作业本标题").builder()?.setOnDialogClickListener(
            object : NoteBookAddDialog.OnDialogClickListener {
            override fun onClick(string: String) {
                val time=System.currentTimeMillis()
                var item= HomeWorkType()
                item.name=string
                item.date=time
                item.type= mAdapter?.data?.size!!//新增id为该类所有和
                item.courseId=courseID
                item.resId=R.mipmap.icon_homework_zy

                datas.add(item)
                mAdapter?.notifyDataSetChanged()

            }
        })
    }


    //顶部弹出选择
    private fun setTopSelectView(){
        if (popWindow==null){
            val popView = LayoutInflater.from(activity).inflate(R.layout.popwindow_homework, null, false)
            val llTj=popView?.findViewById<LinearLayout>(R.id.ll_tj)
            val ivTj=popView?.findViewById<ImageView>(R.id.iv_select_tj)
            val llPg=popView?.findViewById<LinearLayout>(R.id.ll_pg)
            val ivPg=popView?.findViewById<ImageView>(R.id.iv_select_pg)
            llTj?.setOnClickListener {
                ivTj?.visibility=View.VISIBLE
                ivPg?.visibility=View.GONE
                popWindow?.dismiss()
            }
            llPg?.setOnClickListener {
                ivTj?.visibility=View.GONE
                ivPg?.visibility=View.VISIBLE
                popWindow?.dismiss()
            }
            popWindow=PopWindowUtil().makePopupWindow(activity,ivHomework,popView, -160,20, Gravity.LEFT)
            popWindow?.show()
        }
        else{
            popWindow?.show()
        }
    }


}
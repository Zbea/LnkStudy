package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.ModuleAddDialog
import com.bll.lnkstudy.dialog.NoteBookAddDialog
import com.bll.lnkstudy.dialog.PopWindowList
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.mvp.model.HomeworkType
import com.bll.lnkstudy.mvp.model.ModuleBean
import com.bll.lnkstudy.mvp.model.PopWindowBean
import com.bll.lnkstudy.ui.activity.HomeworkDrawingActivity
import com.bll.lnkstudy.ui.activity.RecordListActivity
import com.bll.lnkstudy.ui.adapter.HomeworkAdapter
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_homework.*


/**
 * 作业
 */
class HomeworkFragment : BaseFragment(){

    private var popWindowList:PopWindowList?=null
    private var popWindowBeans = mutableListOf<PopWindowBean>()
    private var mAdapter:HomeworkAdapter?=null

    private var courseID=0//当前科目id
    private var datas= mutableListOf<HomeworkType>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_homework
    }

    override fun initView() {
        setPageTitle("作业")
        setDisBackShow()
        setShowHomework()

        var popWindowBean=PopWindowBean()
        popWindowBean.name="提交详情"
        popWindowBean.isCheck=true
        var popWindowBean1=PopWindowBean()
        popWindowBean1.name="批改详情"
        popWindowBean1.isCheck=false

        popWindowBeans.add(popWindowBean)
        popWindowBeans.add(popWindowBean1)

        ivHomework?.setOnClickListener {
            setPopWindow()
        }

        tv_add.setOnClickListener {
            addCover()
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
        mAdapter = HomeworkAdapter(R.layout.item_homework, null)
        rv_list.layoutManager = GridLayoutManager(activity,3)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco(0,70))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            if(datas[position].isListenToRead){
                startActivity(Intent(context,RecordListActivity::class.java).putExtra("courseId",courseID))
            }
            else{
                var bundle=Bundle()
                bundle.putSerializable("homework",datas[position])
                var intent=Intent(context,HomeworkDrawingActivity::class.java)
                intent.putExtra("homeworkBundle",bundle)
//                intent.putExtra(Intent.EXTRA_LAUNCH_SCREEN, Intent.EXTRA_LAUNCH_SCREEN_PANEL_BOTH)
                startActivity(intent)
            }
        }

    }


    //查找分类数据
    private fun findDatas(islg: Boolean,courseID:Int){
        datas.clear()
        datas=DataBeanManager.getIncetance().getHomeWorkTypes(islg,courseID,mUser?.grade!!)
        datas.addAll(HomeworkTypeDaoManager.getInstance(context).queryAllByCourseId(courseID))
        mAdapter?.setNewData(datas)
    }

    //添加作业本
    private fun addHomeWorkType(item:HomeworkType){
        NoteBookAddDialog(requireContext(),"新建作业本","","请输入作业本标题").builder()?.setOnDialogClickListener(
            object : NoteBookAddDialog.OnDialogClickListener {
            override fun onClick(string: String) {
                val time=System.currentTimeMillis()
                item.name=string
                item.date=time
                item.type= mAdapter?.data?.size!!//新增id为该类所有和
                item.courseId=courseID

                HomeworkTypeDaoManager.getInstance(context).insertOrReplace(item)
                datas.add(item)
                mAdapter?.notifyDataSetChanged()



            }
        })
    }

    //添加封面
    private fun addCover(){
        val list=DataBeanManager.getIncetance().homeworkCover
        ModuleAddDialog(requireContext(),"封面模块",list).builder()?.setOnDialogClickListener(
            object : ModuleAddDialog.OnDialogClickListener {
                override fun onClick(moduleBean: ModuleBean) {
                    addContentModule(moduleBean.resId)
                }

            }
        )
    }

    //选择内容背景
    private fun addContentModule(coverResId:Int){

        var list=if (courseID==0)
        {
            DataBeanManager.getIncetance().getYw(mUser?.grade!!)
        }
        else if (courseID==1){
            DataBeanManager.getIncetance().getSx(mUser?.grade!!)
        }
        else if (courseID==2){
            DataBeanManager.getIncetance().getYy(mUser?.grade!!)
        }
        else{
            DataBeanManager.getIncetance().other
        }

        ModuleAddDialog(requireContext(),"作业本模板",list).builder()?.setOnDialogClickListener(
            object : ModuleAddDialog.OnDialogClickListener {
                override fun onClick(moduleBean: ModuleBean) {
                    val item=HomeworkType()
                    item.resId=moduleBean.resId
                    item.bgResId=coverResId

                    addHomeWorkType(item)
                }

            }
        )

    }



    private fun setPopWindow(){
        if (popWindowList==null)
        {
            popWindowList= PopWindowList(requireActivity(),popWindowBeans,ivHomework!!,-220,20).builder()
            popWindowList?.setOnSelectListener(object : PopWindowList.OnSelectListener {
                override fun onSelect(item: PopWindowBean) {

                }
            })
        }
        else{
            popWindowList?.show()
        }
    }




}
package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.ui.activity.HomeworkDrawingActivity
import com.bll.lnkstudy.ui.activity.PaperDrawingActivity
import com.bll.lnkstudy.ui.activity.RecordListActivity
import com.bll.lnkstudy.ui.adapter.HomeworkAdapter
import com.bll.lnkstudy.utils.ImageDownLoadUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco5
import kotlinx.android.synthetic.main.common_xtab.*
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
    private var messages= mutableListOf<HomeworkMessage>()

    private var homeworkMessageAllDialog:HomeworkMessageAllDialog?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_homework
    }

    override fun initView() {
        setTitle("作业")
        showHomeworkView()

        var popWindowBean=PopWindowBean()
        popWindowBean.id=0
        popWindowBean.name="提交详情"
        popWindowBean.isCheck=true
        var popWindowBean1=PopWindowBean()
        popWindowBean1.id=1
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

        getMessageDatas()
        loadImage()

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
        rv_list.addItemDecoration(SpaceGridItemDeco5(71,40))
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item=datas[position]
            if (view.id==R.id.iv_image){
                item.isMessage=false
                item.isPg=false
                mAdapter?.notifyDataSetChanged()
                if(item.isListenToRead){
                    startActivity(Intent(context,RecordListActivity::class.java).putExtra("courseId",courseID))
                }
                else if (item.type==2||item.type==3){
                    var intent=Intent(activity, PaperDrawingActivity::class.java)
                    intent.putExtra("courseId",courseID)
                    intent.putExtra("categoryId",item.type)
                    intent.flags=0
                    startActivity(intent)
                }
                else{
                    var bundle=Bundle()
                    bundle.putSerializable("homework",item)
                    var intent=Intent(context,HomeworkDrawingActivity::class.java)
                    intent.putExtra("homeworkBundle",bundle)
                    startActivity(intent)
                }
            }
            if (view.id==R.id.tv_message){
                if (item.message!=null)
                    HomeworkMessageDialog(requireActivity(),item.message).builder()
            }
            if (view.id==R.id.iv_message){
                homeworkMessageAllDialog= HomeworkMessageAllDialog(requireActivity(),messages).builder()
                homeworkMessageAllDialog?.setOnDialogClickListener(object : HomeworkMessageAllDialog.OnDialogClickListener {
                    override fun onClick(position:Int,id: String) {
                        messages.removeAt(position)
                        homeworkMessageAllDialog?.setData(messages)
                    }

                })
            }

        }

    }


    //查找分类数据
    private fun findDatas(islg: Boolean,courseID:Int){
        datas.clear()
        datas=DataBeanManager.getIncetance().getHomeWorkTypes(islg,courseID,mUser?.grade!!)
        datas.addAll(HomeworkTypeDaoManager.getInstance(context).queryAllByCourseId(courseID))

        for (item in datas){
            for (mes in messages){
                if (item.courseId==mes.courseId&&item.type==mes.homeworkTypeId){
                    item.isMessage=true
                    item.isPg=mes.isPg
                    item.message=mes
                }
            }
        }

        mAdapter?.setNewData(datas)

    }

    private fun getMessageDatas(){
        val homeworkMessage=HomeworkMessage()
        homeworkMessage.id=0
        homeworkMessage.title="语文家庭作业1、3、5页"
        homeworkMessage.date=System.currentTimeMillis()
        homeworkMessage.course="语文"
        homeworkMessage.courseId=0
        homeworkMessage.state=0
        homeworkMessage.homeworkTypeId=0

        val homeworkMessage1=HomeworkMessage()
        homeworkMessage1.id=1
        homeworkMessage1.title="数学作业"
        homeworkMessage1.date=System.currentTimeMillis()
        homeworkMessage1.course="数学"
        homeworkMessage1.courseId=1
        homeworkMessage1.state=1
        homeworkMessage1.homeworkTypeId=2
        homeworkMessage1.isPg=true

        val homeworkMessage2=HomeworkMessage()
        homeworkMessage2.id=2
        homeworkMessage2.title="数学作业112"
        homeworkMessage2.date=System.currentTimeMillis()
        homeworkMessage2.course="数学"
        homeworkMessage2.courseId=1
        homeworkMessage2.state=1
        homeworkMessage2.homeworkTypeId=3
        homeworkMessage2.isPg=false

        messages.add(homeworkMessage)
        messages.add(homeworkMessage1)
        messages.add(homeworkMessage2)

    }

    /**
     * 下载图片
     */
    private fun loadImage(){
        for (item in messages){
            if (item.homeworkTypeId==2||item.homeworkTypeId==3){
                //设置路径
                val pathStr=FileAddress().getPathHomework(item.courseId,item.homeworkTypeId,item.id)
                var imageDownLoad= ImageDownLoadUtils(activity,if (item.isPg)item.pgImages else item.images,pathStr)
                imageDownLoad.startDownload()
                imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
                    override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                        val paperDaoManager=PaperDaoManager.getInstance(requireContext())
                        val paperContentDaoManager=PaperContentDaoManager.getInstance(requireContext())
                        if (item.isPg){
                            var paper=paperDaoManager.queryByContentID(item.id)
                            if(paper!=null){
                                paper.isPg=true
                                paperDaoManager.insertOrReplace(paper)
                            }
                        }
                        else{
                            //查找到之前已经存储的数据、用于页码计算
                            var papers=paperDaoManager.queryAll(0,item.courseId,item.homeworkTypeId) as MutableList<Paper>
                            var paperContents=paperContentDaoManager.queryAll(0,item.courseId,item.homeworkTypeId) as MutableList<PaperContent>

                            var paper= Paper()
                            paper.contentId=item?.id
                            paper.type=0//作业
                            paper.courseId=item?.courseId
                            paper.course=item?.course
                            paper.categoryId=item?.homeworkTypeId
                            paper.title=item?.title
                            paper.path=pathStr
                            paper.page=paperContents.size //子内容的第一个页码位置
                            paper.index=papers.size //作业位置
                            paper.createDate=item?.date
                            paper.images=item?.images?.toString()
                            paper.isPg=false
                            paperDaoManager.insertOrReplace(paper)

                            for (i in 0 until map?.size!!){
                                val path=map[i]
                                val drawPath=pathStr+"/${i + 1}/draw.tch"
                                var paperContent= PaperContent()
                                paperContent.type=0
                                paperContent.courseId=item?.courseId
                                paperContent.categoryId=item?.homeworkTypeId
                                paperContent.contentId=paper?.contentId
                                paperContent.path=path
                                paperContent.drawPath=drawPath
                                paperContent.date=item?.date
                                paperContent.page=paperContents.size+i
                                paperContentDaoManager.insertOrReplace(paperContent)
                            }
                        }
                    }
                    override fun onDownLoadFailed(unLoadList: MutableList<Int>?) {
                        imageDownLoad.reloadImage()
                    }
                })

            }
        }
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
                    item.resId=ToolUtils.getImageResStr(activity,moduleBean.resContentId)
                    item.bgResId=ToolUtils.getImageResStr(activity,coverResId)

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
                    if (item.id==0){
                        HomeworkCommitDetailsDialog(requireActivity(),messages).builder()
                    }
                }
            })
        }
        else{
            popWindowList?.show()
        }
    }




}
package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.ui.activity.RecordListActivity
import com.bll.lnkstudy.ui.adapter.HomeworkAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ImageDownLoadUtils
import com.bll.lnkstudy.utils.ToolUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_radiogroup.*
import kotlinx.android.synthetic.main.fragment_homework.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File


/**
 * 作业
 */
class HomeworkFragment : BaseFragment(){

    private var popWindowList:PopWindowList?=null
    private var popWindowBeans = mutableListOf<PopWindowBean>()
    private var mAdapter:HomeworkAdapter?=null

    private var courseID=0//当前科目id
    private var homeworkTypes= mutableListOf<HomeworkType>()
    private var messages= mutableListOf<HomeworkMessage>()

    private var homeworkMessageDialog:HomeworkMessageDialog?=null

    override fun getLayoutId(): Int {
        return R.layout.fragment_homework
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setTitle("作业")
        showView(iv_manager)

        popWindowBeans.add(PopWindowBean(0,"提交详情",true))
        popWindowBeans.add(PopWindowBean(1,"批改详情",false))
        popWindowBeans.add(PopWindowBean(2,"添加作业本",false))

        iv_manager.setOnClickListener {
            setPopWindow()
        }

        initRecyclerView()
        initTab()

        getMessageDatas()
        if (screenPos==3)
            loadImage()

        findDatas(0)
    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab(){
        val courses= DataBeanManager.getIncetance().courses
        for (i in courses.indices) {
            rg_group.addView(getRadioButton(i ,courses[i].name,courses.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            courseID= id
            findDatas(courseID)
        }
    }


    private fun initRecyclerView(){
        mAdapter = HomeworkAdapter(R.layout.item_homework, null)
        rv_list.layoutManager = GridLayoutManager(activity,3)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        rv_list.addItemDecoration(SpaceGridItemDeco1(DP2PX.dip2px(activity,33f),40))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val item=homeworkTypes[position]
            item.isMessage=false
            item.isPg=false
            mAdapter?.notifyDataSetChanged()

            when(item.state){
                1->{
                    customStartActivity(Intent(context,RecordListActivity::class.java).putExtra("courseId",courseID))
                }
                2->{
                    gotoPaperDrawing(0,courseID,item.typeId)
                }
                3->{
                    gotoBookDetails(item?.typeId!!)
                }
                else->{
                    gotoHomeworkDrawing(item)
                }
            }
        }

        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.ll_message){
                HomeworkMessageDialog(requireActivity(),screenPos,messages).builder()?.setOnDialogClickListener { position, id ->
                    messages.removeAt(position)
                    homeworkMessageDialog?.setData(messages)
                }
            }

        }
        mAdapter?.setOnItemLongClickListener { adapter, view, position ->
            showHomeworkManage(position)
        }
    }


    //查找分类数据
    private fun findDatas(courseID:Int){
        homeworkTypes.clear()
        var datas= DataBeanManager.getIncetance().getHomeWorkTypes(courseID,mUser?.grade!!)
        var datas1=HomeworkTypeDaoManager.getInstance(context).queryAllByCourseId(courseID,false)
        if (datas.size!=datas1.size){
            HomeworkTypeDaoManager.getInstance(context).insertOrReplaceAll(courseID,datas)
        }
        homeworkTypes.addAll(HomeworkTypeDaoManager.getInstance(context).queryAllByCourseId(courseID,false))
        homeworkTypes.addAll(HomeworkTypeDaoManager.getInstance(context).queryAllByCourseId(courseID,true))
        for (item in homeworkTypes){
            for (mes in messages){
                if (item.courseId==mes.courseId&&item.typeId==mes.homeworkTypeId){
                    item.isMessage=true
                    item.isPg=mes.isPg
                    item.message=mes
                }
            }
        }

        mAdapter?.setNewData(homeworkTypes)

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
     * 下载图片、将图片保存到作业
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


    /**
     * 长按展示作业换肤、删除
     */
    private fun showHomeworkManage(pos:Int):Boolean{
        val item=homeworkTypes[pos]
        HomeworkManageDialog(requireContext(),screenPos,item.isCreate).builder().setOnDialogClickListener(object :
            HomeworkManageDialog.OnDialogClickListener {
            override fun onSkin() {

                val list= DataBeanManager.getIncetance().homeworkCover
                ModuleAddDialog(requireContext(),screenPos,"封面模块",list).builder()
                    ?.setOnDialogClickListener { moduleBean ->
                        item.bgResId = ToolUtils.getImageResStr(activity, moduleBean.resId)
                        mAdapter?.notifyDataSetChanged()
                        HomeworkTypeDaoManager.getInstance(activity).insertOrReplace(item)
                    }
            }

            override fun onDelete() {
                if(item.isCreate){
                    HomeworkTypeDaoManager.getInstance(activity).deleteBean(item)
                    val path=FileAddress().getPathHomework(courseID,item.typeId)
                    FileUtils.deleteFile(File(path))
                    homeworkTypes.removeAt(pos)
                    mAdapter?.notifyDataSetChanged()
                }
            }

        })
        return true
    }


    //添加作业本
    private fun addHomeWorkType(item:HomeworkType){
        NoteBookAddDialog(requireContext(),screenPos,"新建作业本","","请输入作业本标题").builder()
            ?.setOnDialogClickListener { string ->
            val time = System.currentTimeMillis()
            item.name = string
            item.date = time
            item.typeId = System.currentTimeMillis().toInt()
            item.courseId = courseID
            item.isCreate=true

            HomeworkTypeDaoManager.getInstance(context).insertOrReplace(item)
            homeworkTypes.add(item)
            mAdapter?.notifyDataSetChanged()
        }
    }

    //添加封面
    private fun addCover(){
        val list= DataBeanManager.getIncetance().homeworkCover
        ModuleAddDialog(requireContext(),screenPos,"封面模板",list).builder()
            ?.setOnDialogClickListener { moduleBean ->
            addContentModule(
                moduleBean.resId
            )
        }
    }

    //选择内容背景
    private fun addContentModule(coverResId:Int){

        var list= when (courseID) {
            0 -> {
                DataBeanManager.getIncetance().getYw(mUser?.grade!!)
            }
            1 -> {
                DataBeanManager.getIncetance().getSx(mUser?.grade!!)
            }
            2 -> {
                DataBeanManager.getIncetance().getYy(mUser?.grade!!)
            }
            else -> {
                DataBeanManager.getIncetance().other
            }
        }

        ModuleAddDialog(requireContext(),screenPos,"作业本模板",list).builder()
            ?.setOnDialogClickListener { moduleBean ->
            val item = HomeworkType()
            item.contentResId = ToolUtils.getImageResStr(activity, moduleBean.resContentId)
            item.bgResId = ToolUtils.getImageResStr(activity, coverResId)

            addHomeWorkType(item)
        }

    }


    private fun setPopWindow(){
        if (popWindowList==null)
        {
            popWindowList= PopWindowList(requireActivity(),popWindowBeans,iv_manager,5).builder()
            popWindowList?.setOnSelectListener { item ->
                if (item.id == 0) {
                    HomeworkCommitDetailsDialog(requireActivity(), screenPos, messages).builder()
                }
                if (item.id==2){
                    addCover()
                }
            }
        }
        else{
            popWindowList?.show()
        }
    }


    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag== Constants.BOOK_HOMEWORK_EVENT){
            findDatas(courseID)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.manager.HomeworkContentDaoManager
import com.bll.lnkstudy.manager.HomeworkTypeDaoManager
import com.bll.lnkstudy.manager.PaperContentDaoManager
import com.bll.lnkstudy.manager.PaperDaoManager
import com.bll.lnkstudy.mvp.model.PaperBean
import com.bll.lnkstudy.mvp.model.PaperContentBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessage
import com.bll.lnkstudy.mvp.model.homework.HomeworkTypeBean
import com.bll.lnkstudy.mvp.presenter.HomeworkPresenter
import com.bll.lnkstudy.mvp.view.IContractView.IHomeworkView
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
class HomeworkFragment : BaseFragment(),IHomeworkView{

    private val mPresenter=HomeworkPresenter(this)
    private var popWindowBeans = mutableListOf<PopupBean>()
    private var mAdapter:HomeworkAdapter?=null

    private var courses= mutableListOf<String>()
    private var mCourse=""//当前科目
    private var homeworkTypes= mutableListOf<HomeworkTypeBean>()
    private var messages= mutableListOf<HomeworkMessage>()

    private var homeworkMessageDialog:HomeworkMessageDialog?=null

    override fun onTypeList(list: MutableList<HomeworkTypeBean>?) {
        homeworkTypes.clear()
        if (!list.isNullOrEmpty()){
            for (item in list){
                item.contentResId=DataBeanManager.getHomeWorkContentStr(mCourse,mUser?.grade!!)
                item.course=mCourse
            }
            homeworkTypes.addAll(list)
        }
        homeworkTypes.addAll(getLocalHomeworkTypeData())
        for (item in homeworkTypes){
            for (mes in messages){
                if (item.course==mes.course&&item.typeId==mes.homeworkTypeId){
                    item.isMessage=true
                    item.isPg=mes.isPg
                    item.message=mes
                }
            }
        }
        mAdapter?.setNewData(homeworkTypes)
    }



    override fun getLayoutId(): Int {
        return R.layout.fragment_homework
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setTitle("作业")
        showView(iv_manager)

        popWindowBeans.add(PopupBean(0, "新建作业本", true))
        popWindowBeans.add(PopupBean(1, "提交详情", false))
        popWindowBeans.add(PopupBean(2, "批改详情", false))

        iv_manager.setOnClickListener {
            PopupList(requireActivity(), popWindowBeans, iv_manager, 5).builder()
                .setOnSelectListener { item ->
                    when(item.id){
                        0->{
                            addCover()
                        }
                        1->{
                            HomeworkCommitDetailsDialog(requireActivity(), screenPos, messages).builder()
                        }
                        else->{

                        }
                    }
                }
        }

        initRecyclerView()
        initTab()

        getMessageDatas()
        if (screenPos==3)
            loadImage()
    }

    override fun lazyLoad() {
    }

    private fun initRecyclerView(){
        mAdapter = HomeworkAdapter(R.layout.item_homework, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity,3)
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3,DP2PX.dip2px(activity,33f),40))
            setOnItemClickListener { adapter, view, position ->
                val item= homeworkTypes[position]
                item.isMessage=false
                item.isPg=false
                notifyDataSetChanged()

                when(item.state){
                    3->{
                        customStartActivity(Intent(context,RecordListActivity::class.java).putExtra("course",mCourse))
                    }
                    1->{
                        gotoPaperDrawing(0,mCourse,item.typeId)
                    }
                    else->{
                        gotoHomeworkDrawing(item)
                    }
                }
            }
           setOnItemChildClickListener { adapter, view, position ->
                if (view.id==R.id.ll_message){
                    HomeworkMessageDialog(requireActivity(),screenPos,messages).builder()?.setOnDialogClickListener { position, id ->
                        messages.removeAt(position)
                        homeworkMessageDialog?.setData(messages)
                    }
                }
            }
            setOnItemLongClickListener { adapter, view, position ->
                if (homeworkTypes[position].isCreate){
                    showHomeworkManage(position)
                }
                true
            }
        }
    }

    //设置头部索引
    private fun initTab(){
        rg_group.removeAllViews()
        courses=DataBeanManager.courses
        if (courses.size>0){
            mCourse=courses[0]
            for (i in courses.indices) {
                rg_group.addView(getRadioButton(i ,courses[i],courses.size-1))
            }
            rg_group.setOnCheckedChangeListener { radioGroup, id ->
                mCourse= courses[id]
                fetchData()
            }
            fetchData()
        }
    }

    /**
     * 获取本地作业本
     */
    private fun getLocalHomeworkTypeData():MutableList<HomeworkTypeBean>{
        return HomeworkTypeDaoManager.getInstance().queryAllByCourse(mCourse,true)
    }


    /**
     * 判断 作业本是否已经保存本地
     */
    private fun isSaveHomework(item: HomeworkTypeBean):Boolean{
        var isSave=false
        for (list in getLocalHomeworkTypeData()){
            if (item.name==list.name&&item.typeId==list.typeId){
                isSave=true
            }
        }
        return isSave
    }

    private fun getMessageDatas(){
        val homeworkMessage=
            HomeworkMessage()
        homeworkMessage.id=0
        homeworkMessage.title="语文家庭作业1、3、5页"
        homeworkMessage.date=System.currentTimeMillis()
        homeworkMessage.course="语文"
        homeworkMessage.state=0
        homeworkMessage.homeworkTypeId=0

        val homeworkMessage1=
            HomeworkMessage()
        homeworkMessage1.id=1
        homeworkMessage1.title="数学作业"
        homeworkMessage1.date=System.currentTimeMillis()
        homeworkMessage1.course="数学"
        homeworkMessage1.state=1
        homeworkMessage1.homeworkTypeId=2
        homeworkMessage1.isPg=true

        val homeworkMessage2=
            HomeworkMessage()
        homeworkMessage2.id=2
        homeworkMessage2.title="数学作业112"
        homeworkMessage2.date=System.currentTimeMillis()
        homeworkMessage2.course="数学"
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
                val pathStr=FileAddress().getPathHomework(item.course,item.homeworkTypeId,item.id)
                val imageDownLoad= ImageDownLoadUtils(activity,if (item.isPg)item.pgImages else item.images,pathStr)
                imageDownLoad.startDownload()
                imageDownLoad.setCallBack(object : ImageDownLoadUtils.ImageDownLoadCallBack {
                    override fun onDownLoadSuccess(map: MutableMap<Int, String>?) {
                        val paperDaoManager=PaperDaoManager.getInstance()
                        val paperContentDaoManager=PaperContentDaoManager.getInstance()
                        if (item.isPg){
                            val paper=paperDaoManager.queryByContentID(item.id)
                            if(paper!=null){
                                paper.isPg=true
                                paperDaoManager.insertOrReplace(paper)
                            }
                        }
                        else{
                            //查找到之前已经存储的数据、用于页码计算
                            val papers=paperDaoManager.queryAll(0,item.course,item.homeworkTypeId) as MutableList<PaperBean>
                            val paperContents=paperContentDaoManager.queryAll(0,item.course,item.homeworkTypeId) as MutableList<PaperContentBean>

                            val paper= PaperBean().apply {
                                contentId=item.id
                                type=0//作业
                                course= item.course
                                categoryId=item.homeworkTypeId
                                title=item.title
                                path=pathStr
                                page=paperContents.size //子内容的第一个页码位置
                                index=papers.size //作业位置
                                createDate=item.date
                                images=item.images?.toString()
                                isPg=false
                            }
                            paperDaoManager.insertOrReplace(paper)
                            for (i in 0 until map?.size!!){
                                val paperContent= PaperContentBean().apply {
                                    type=0
                                    course=item.course
                                    categoryId=item.homeworkTypeId
                                    contentId=paper.contentId
                                    path=map[i]
                                    drawPath=pathStr+"/${i + 1}/draw.tch"
                                    date=item.date
                                    page=paperContents.size+i
                                }
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
    private fun showHomeworkManage(pos:Int){
        val item=homeworkTypes[pos]
        HomeworkManageDialog(requireContext(),screenPos,item.isCreate).builder().setOnDialogClickListener(object :
            HomeworkManageDialog.OnDialogClickListener {
            override fun onSkin() {

                val list= DataBeanManager.homeworkCover
                ModuleAddDialog(requireContext(),screenPos,"封面模块",list).builder()
                    ?.setOnDialogClickListener { moduleBean ->
                        item.bgResId = ToolUtils.getImageResStr(activity, moduleBean.resId)
                        mAdapter?.notifyDataSetChanged()
                        HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
                    }
            }

            override fun onDelete() {
                //删除本地当前作业本
                HomeworkTypeDaoManager.getInstance().deleteBean(item)
                //删除作本内容
                HomeworkContentDaoManager.getInstance().deleteAll(mCourse,item.typeId)
                //删除本地文件
                val path=FileAddress().getPathHomework(mCourse,item.typeId)
                FileUtils.deleteFile(File(path))
                homeworkTypes.removeAt(pos)
                mAdapter?.notifyDataSetChanged()
            }

        })
    }


    //添加作业本
    private fun addHomeWorkType(item: HomeworkTypeBean){
        NotebookAddDialog(requireContext(),screenPos,"新建作业本","","请输入作业本标题").builder()
            ?.setOnDialogClickListener { string ->
                item.apply {
                    name = string
                    date = System.currentTimeMillis()
                    typeId = System.currentTimeMillis().toInt()
                    course = mCourse
                    isCreate=true
                    state=2
                }
            HomeworkTypeDaoManager.getInstance().insertOrReplace(item)
            homeworkTypes.add(item)
            mAdapter?.notifyDataSetChanged()
        }
    }

    //添加封面
    private fun addCover(){
        val list= DataBeanManager.homeworkCover
        ModuleAddDialog(requireContext(),screenPos,"封面模板",list).builder()
            ?.setOnDialogClickListener { moduleBean ->
            addContentModule(
                moduleBean.resId
            )
        }
    }

    //选择内容背景
    private fun addContentModule(coverResId:Int){

        val list= when (mCourse) {
            "语文" -> {
                DataBeanManager.getYw(mUser?.grade!!)
            }
            "数学" -> {
                DataBeanManager.getSx(mUser?.grade!!)
            }
            "英语" -> {
                DataBeanManager.getYy(mUser?.grade!!)
            }
            else -> {
                DataBeanManager.other
            }
        }

        if (list.size>1){
            ModuleAddDialog(requireContext(),screenPos,"作业本模板",list).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    val item =
                        HomeworkTypeBean()
                    item.contentResId = ToolUtils.getImageResStr(activity, moduleBean.resContentId)
                    item.bgResId = ToolUtils.getImageResStr(activity, coverResId)
                    addHomeWorkType(item)
                }
        }
        else{
            val item = HomeworkTypeBean()
            item.contentResId = ToolUtils.getImageResStr(activity, list[0].resContentId)
            item.bgResId = ToolUtils.getImageResStr(activity, coverResId)
            addHomeWorkType(item)
        }
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag== Constants.BOOK_HOMEWORK_EVENT){
            fetchData()
        }
        if (msgFlag==Constants.COURSE_EVENT){
            //刷新科目
            initTab()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun refreshData() {
        fetchData()
    }

    override fun fetchData() {
        mPresenter.getTypeList(mCourse)
    }

}
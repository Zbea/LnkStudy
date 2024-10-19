package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.ui.activity.PaintingDrawingTypeActivity
import com.bll.lnkstudy.ui.activity.PaintingListActivity
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_painting.iv_dd
import kotlinx.android.synthetic.main.fragment_painting.iv_dd_1
import kotlinx.android.synthetic.main.fragment_painting.iv_han
import kotlinx.android.synthetic.main.fragment_painting.iv_hb
import kotlinx.android.synthetic.main.fragment_painting.iv_jd
import kotlinx.android.synthetic.main.fragment_painting.iv_jd_1
import kotlinx.android.synthetic.main.fragment_painting.iv_ming
import kotlinx.android.synthetic.main.fragment_painting.iv_qing
import kotlinx.android.synthetic.main.fragment_painting.iv_sf
import kotlinx.android.synthetic.main.fragment_painting.iv_song
import kotlinx.android.synthetic.main.fragment_painting.iv_tang
import kotlinx.android.synthetic.main.fragment_painting.iv_yuan
import kotlinx.android.synthetic.main.fragment_painting.ll_content1
import kotlinx.android.synthetic.main.fragment_painting.ll_content2
import java.io.File


/**
 * 书画
 */
class PaintingFragment : BaseMainFragment(){
    private var typeId = 0//类型

    override fun getLayoutId(): Int {
        return R.layout.fragment_painting
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        setTitle(DataBeanManager.listTitle[6])
        initTab()

        iv_han.setOnClickListener {
            onClick(1)
        }
        iv_tang.setOnClickListener {
            onClick(2)
        }
        iv_song.setOnClickListener {
            onClick(3)
        }
        iv_yuan.setOnClickListener {
            onClick(4)
        }
        iv_ming.setOnClickListener {
            onClick(5)
        }
        iv_qing.setOnClickListener {
            onClick(6)
        }
        iv_jd.setOnClickListener {
            onClick(7)
        }
        iv_dd.setOnClickListener {
            onClick(8)
        }
        iv_jd_1.setOnClickListener {
            onClick(7)
        }
        iv_dd_1.setOnClickListener {
            onClick(8)
        }

        iv_hb.setOnClickListener {
            gotoPaintingDrawing(3)
        }
        iv_sf.setOnClickListener {
            gotoPaintingDrawing(4)
        }

        iv_hb.setOnLongClickListener {
            onDelete(3)
            true
        }

        iv_sf.setOnLongClickListener {
            onDelete(4)
            true
        }

    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab() {
        val tabStrs = DataBeanManager.PAINTING
        for (i in tabStrs.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=tabStrs[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        typeId = position
        if (typeId==4||typeId==5){
            showView(ll_content2)
            disMissView(ll_content1)
        }
        else{
            showView(ll_content1)
            disMissView(ll_content2)
        }
    }

    /**
     * 调整本地我的书画
     */
    private fun onClick(time: Int) {
        val intent = Intent(activity, PaintingListActivity::class.java)
        intent.putExtra("title", "${getString(DataBeanManager.dynastys[time-1])}   ${DataBeanManager.PAINTING[typeId]}")
        intent.putExtra("time", time)
        intent.putExtra("paintingType", typeId+1)
        customStartActivity(intent)
    }


    /**
     * 跳转手写画本
     */
    private fun gotoPaintingDrawing(type: Int){
        val items=ItemTypeDaoManager.getInstance().queryAll(type)

        //当前年级 画本、书法分类为null则创建
        var item=ItemTypeDaoManager.getInstance().queryBean(type,grade)
        if (item==null) {
            item= ItemTypeBean()
            item.title=if (type==3) "我的画本" else "我的书法"
            item.type = type
            item.grade = grade
            item.date = System.currentTimeMillis()
            item.path=FileAddress().getPathPaintingDraw(if (type==3) 0 else 1,grade)
            item.typeId=MethodManager.getPaintingTypeId(type,grade)
            ItemTypeDaoManager.getInstance().insertOrReplace(item)
            //创建本地画本增量更新
            DataUpdateManager.createDataUpdate(5,item.typeId,1, Gson().toJson(item))
        }

        //当本地画本或者书法分类不止一个时候，进去列表
        if (items.size>1){
            val intent=Intent(activity, PaintingDrawingTypeActivity::class.java)
            intent.flags=type
            customStartActivity(intent)
        } else{
            MethodManager.gotoPaintingDrawing(requireActivity(),item,type)
        }

    }

    /**
     * 长按删除当前画本或者书法
     */
    private fun onDelete(type: Int){
        val items=ItemTypeDaoManager.getInstance().queryAll(type)
        if (items.size==1){
            val item=ItemTypeDaoManager.getInstance().queryBean(type,grade)
            CommonDialog(requireActivity()).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    MethodManager.deletePaintingDrawing(item)
                }
            })
        }
    }

    /**
     * 上传本地手绘书画
     */
    fun uploadLocalDrawing(token: String) {
        if (grade==0) return
        val cloudList= mutableListOf<CloudListBean>()
        val uploadList= mutableListOf<ItemTypeBean>()
        val types= mutableListOf<ItemTypeBean>()
        //查找所有分类
        types.addAll(ItemTypeDaoManager.getInstance().queryAll(3))
        types.addAll(ItemTypeDaoManager.getInstance().queryAll(4))
        for (item in types){
            val paintingContents=PaintingDrawingDaoManager.getInstance().queryAllByType(item.type,item.grade)
            val fileName="${if (item.type==3) "画本" else "书法"}${DataBeanManager.getCourseStr(item.grade)}"
            if (paintingContents.size>0){
                uploadList.add(item)
                FileUploadManager(token).apply {
                    startZipUpload(item.path,fileName)
                    setCallBack{
                        cloudList.add(CloudListBean().apply {
                            type=5
                            subTypeStr=if (item.type==3) "我的画本" else "我的书法"
                            date=System.currentTimeMillis()
                            grade=item.grade
                            listJson=Gson().toJson(item)
                            contentJson=Gson().toJson(paintingContents)
                            downloadUrl=it
                        })
                        if (cloudList.size==uploadList.size){
                            mCloudUploadPresenter.upload(cloudList)
                        }
                    }
                }
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        //删除所有本地画本、书法分类
        ItemTypeDaoManager.getInstance().clear(3)
        ItemTypeDaoManager.getInstance().clear(4)
        //删除所有本地画本、书法内容
        PaintingDrawingDaoManager.getInstance().clear()
        FileUtils.deleteFile(File(Constants.PAINTING_PATH))
        //清除增量数据
        DataUpdateManager.clearDataUpdate(5)
        val map=HashMap<String,Any>()
        map["type"]=5
        mDataUploadPresenter.onDeleteData(map)
    }

}
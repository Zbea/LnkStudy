package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.CloudDownloadListDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.PaintingDetailsDialog
import com.bll.lnkstudy.dialog.PopupUpClick
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.manager.PaintingDrawingDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.mvp.model.painting.PaintingBean
import com.bll.lnkstudy.mvp.presenter.QiniuPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.PaintingListActivity
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.tv_btn
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
class PaintingFragment : BaseMainFragment(),IContractView.IQiniuView{
    private var presenter=QiniuPresenter(this,2)
    private var tabPos = 0//类型
    private var popupHbs= mutableListOf<PopupBean>()
    private var popupSfs= mutableListOf<PopupBean>()
    private var uploadTitleStr=""
    private var type=0
    private var uploadType=0
    private var uploadPaintings= mutableListOf<PaintingBean>()

    override fun onToken(token: String) {
        showLoading()
        uploadLocalDrawing(token)
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_painting
    }

    @SuppressLint("WrongConstant")
    override fun initView() {
        setTitle(DataBeanManager.listTitle[6])
        showView(tv_btn)
        tv_btn.text="书画明细"

        popupHbs.add(PopupBean().apply {
            id=0
            name="删除画本"
        })
        popupHbs.add(PopupBean().apply {
            id=1
            name="画本上传"
        })
        popupHbs.add(PopupBean().apply {
            id=2
            name="云库画本"
        })

        popupSfs.add(PopupBean().apply {
            id=0
            name="删除书法"
        })
        popupSfs.add(PopupBean().apply {
            id=1
            name="书法上传"
        })
        popupSfs.add(PopupBean().apply {
            id=2
            name="云库书法"
        })


        tv_btn.setOnClickListener {
            PaintingDetailsDialog(requireActivity()).builder().setOnDialogClickListener{
                uploadPaintings= it.toMutableList()
                uploadType=2
                uploadPainting()
            }
        }

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
            MethodManager.gotoPaintingDrawing(requireActivity(),0,0)
        }
        iv_sf.setOnClickListener {
            MethodManager.gotoPaintingDrawing(requireActivity(),1,0)
        }

        iv_hb.setOnLongClickListener {
            onLongClick(0)
            true
        }

        iv_sf.setOnLongClickListener {
            onLongClick(1)
            true
        }

        initTab()
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
        tabPos = position
        if (tabPos==4||tabPos==5){
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
        intent.putExtra("title", "${getString(DataBeanManager.dynastys[time-1])}   ${DataBeanManager.PAINTING[tabPos]}")
        intent.putExtra("time", time)
        intent.putExtra("paintingType", tabPos+1)
        customStartActivity(intent)
    }

    /**
     * 长按点击事件
     */
    private fun onLongClick(type: Int){
        this.type=type
        val view=if (type==0) iv_hb else iv_sf
        val pops=if (type==0) popupHbs else popupSfs
        PopupUpClick(requireActivity(),pops,view,160,(view.width-160)/2,-(view.height+10)) .builder().setOnSelectListener{
            when(it.id){
                0->{
                    CommonDialog(requireActivity()).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            MethodManager.deletePaintingDrawing(type,null)
                            showToast("删除${getTypeStr()}成功")
                        }
                    })
                }
                1->{
                    InputContentDialog(requireActivity(),2,"请输入我的${getTypeStr()}上传标题").builder().setOnDialogClickListener{
                        uploadTitleStr=it
                        uploadType=1
                        presenter.getToken(true)
                    }
                }
                2->{
                    CloudDownloadListDialog(requireActivity(),if (type==0) 3 else 4).builder().setOnDialogClickListener{
                        MethodManager.gotoPaintingDrawing(requireActivity(),type,it)
                    }
                }
            }
        }
    }

    /**
     * 获取画本、书法标记
     */
    private fun getTypeStr():String{
        return if (type==0) "画本" else "书法"
    }

    /**
     * 上传书画
     */
    private fun uploadPainting(){
        val cloudList= mutableListOf<CloudListBean>()
        for(item in uploadPaintings){
            showLoading()
            cloudList.add(CloudListBean().apply {
                type=5
                title=item.title
                subTypeStr="我的书画"
                date=System.currentTimeMillis()
                listJson=Gson().toJson(item)
                downloadUrl=item.bodyUrl
                bookId=item.contentId
            })
            if (cloudList.size==uploadPaintings.size)
                mCloudUploadPresenter.upload(cloudList,true)
        }
    }

    /**
     * 上传本地手绘书画
     */
    private fun uploadLocalDrawing(token: String) {
        val itemTypeBean=ItemTypeBean()
        itemTypeBean.type=if (type==0) 3 else 4
        itemTypeBean.title=uploadTitleStr
        itemTypeBean.typeId=0
        itemTypeBean.date=System.currentTimeMillis()
        itemTypeBean.path=FileAddress().getPathPaintingDraw(type,0)

        val cloudList= mutableListOf<CloudListBean>()
        val paintingContents=PaintingDrawingDaoManager.getInstance().queryAllByType(type,0)
        if (paintingContents.isNotEmpty()){
            FileUploadManager(token).apply {
                startZipUpload(itemTypeBean.path,uploadTitleStr)
                setCallBack{
                    cloudList.add(CloudListBean().apply {
                        type=5
                        title=itemTypeBean.title
                        subTypeStr="我的${getTypeStr()}"
                        date=itemTypeBean.date
                        grade=this@PaintingFragment.grade
                        listJson=Gson().toJson(itemTypeBean)
                        contentJson=Gson().toJson(paintingContents)
                        downloadUrl=it
                    })
                    mCloudUploadPresenter.upload(cloudList,true)
                }
            }
        }
        else{
            hideLoading()
            showToast("我的${getTypeStr()}暂无内容，无需上传")
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        if (uploadType==1){
            showToast("我的${getTypeStr()}上传成功")
            MethodManager.deletePaintingDrawing(type,null)
        }
        else{
            showToast("书画上传成功")
            for (item in uploadPaintings){
                PaintingBeanDaoManager.getInstance().deleteBean(item)
                FileUtils.deleteFile(File(FileAddress().getPathImage("painting",item.contentId)))
            }
        }
    }
}
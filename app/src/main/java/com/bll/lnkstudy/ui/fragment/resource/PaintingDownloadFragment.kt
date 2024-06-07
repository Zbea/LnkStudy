package com.bll.lnkstudy.ui.fragment.resource

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.painting.PaintingBean
import com.bll.lnkstudy.mvp.model.painting.PaintingList
import com.bll.lnkstudy.mvp.presenter.DownloadPaintingPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.DownloadPaintingAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileMultitaskDownManager
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.liulishuo.filedownloader.BaseDownloadTask
import kotlinx.android.synthetic.main.fragment_list_content.*

class PaintingDownloadFragment :BaseMainFragment(), IContractView.IPaintingView{

    private var presenter= DownloadPaintingPresenter(this,getScreenPosition())
    private var items= mutableListOf<PaintingList.ListBean>()
    private var mAdapter: DownloadPaintingAdapter?=null

    private var supply=1
    private var dynasty=1
    private var painting=1
    private var position=0

    override fun onList(bean: PaintingList) {
        setPageNumber(bean.total)
        items=bean.list
        mAdapter?.setNewData(items)
    }

    override fun buySuccess() {
        items[position].buyStatus=1
        mAdapter?.notifyDataSetChanged()
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    override fun initView() {
        pageSize=6
        initRecyclerView()
    }

    override fun lazyLoad() {
        if (NetworkUtil(requireActivity()).isNetworkConnected()) {
            fetchData()
        }
        else{
            showNetworkDialog()
        }
    }

    private fun initRecyclerView(){
        val distance= DP2PX.dip2px(requireActivity(),30f)
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(distance, DP2PX.dip2px(requireActivity(),60f),distance,0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        rv_list.layoutManager = GridLayoutManager(requireActivity(),2)//创建布局管理
        mAdapter = DownloadPaintingAdapter(R.layout.item_download_painting, items)
        rv_list.adapter = mAdapter
        rv_list.addItemDecoration(SpaceGridItemDeco1(2,distance,100))
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            ImageDialog(requireActivity(), arrayListOf(items[position].bodyUrl) ).builder()
        }
        mAdapter?.setOnItemChildClickListener{ adapter, view, position ->
            this.position=position
            val item=items[position]
            if (view.id==R.id.btn_download){
                if (item.buyStatus==1){
                    val paintingBean= PaintingBeanDaoManager.getInstance().queryBean(item.fontDrawId)
                    if (paintingBean==null){
                        onDownload()
                    }
                    else{
                        showToast(R.string.toast_downloaded)
                    }
                }
                else{
                    val map = HashMap<String, Any>()
                    map["type"] = 5
                    map["bookId"] = item.fontDrawId
                    presenter.buy(map)
                }
            }
        }
    }


    /**
     * 下载
     */
    private fun onDownload(){
        showLoading()
        val item=items[position]
        val pathStr= FileAddress().getPathImage("painting" ,item.fontDrawId)
        val images = mutableListOf(item.bodyUrl)
        val savePaths= arrayListOf("$pathStr/1.png")
        FileMultitaskDownManager.with(requireActivity()).create(images).setPath(savePaths).startMultiTaskDownLoad(
            object : FileMultitaskDownManager.MultiTaskCallBack {
                override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int, ) {
                }
                override fun completed(task: BaseDownloadTask?) {
                    hideLoading()
                    val bean= PaintingBean()
                    bean.contentId=item.fontDrawId
                    bean.type=2
                    bean.title=item.drawName
                    bean.date=System.currentTimeMillis()
                    bean.paths=savePaths
                    bean.time=dynasty
                    bean.timeStr=DataBeanManager.popupDynasty()[dynasty-1].name
                    bean.paintingType=painting
                    bean.paintingTypeStr=DataBeanManager.popupPainting()[painting-1].name
                    bean.info=item.drawDesc
                    bean.price=item.price
                    bean.imageUrl=item.bodyUrl
                    bean.author=item.author
                    bean.publisher=item.publisher
                    bean.supply=item.supply
                    bean.bodyUrl=item.bodyUrl
                    PaintingBeanDaoManager.getInstance().insertOrReplaceGetId(bean)
                    showToast(R.string.book_download_success)
                }
                override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                }
                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                    hideLoading()
                    showToast(R.string.book_download_fail)
                }
            })
    }


    /**
     * 改变供应商
     */
    fun changeSupply(supply:Int){
        this.supply=supply
        fetchData()
    }

    /**
     * 改变朝代
     */
    fun changeDynasty(dynasty:Int){
        this.dynasty=dynasty
        fetchData()
    }

    /**
     * 改变书画分类
     */
    fun changePainting(painting:Int){
        this.painting=painting
        fetchData()
    }

    open override fun initChangeScreenData() {
        super.initChangeScreenData()
        presenter= DownloadPaintingPresenter(this,getScreenPosition())
    }

    override fun fetchData() {
        val map = HashMap<String, Any>()
        map["page"] = pageIndex
        map["size"] = pageSize
        map["supply"]=supply
        map["type"]=2
        if (painting!=5&&painting!=6){
            map["dynasty"] =dynasty
        }
        map["subType"]=painting
        presenter.getList(map)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }

}
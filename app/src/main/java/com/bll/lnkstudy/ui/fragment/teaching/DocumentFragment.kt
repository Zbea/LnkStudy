package com.bll.lnkstudy.ui.fragment.teaching

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.ui.adapter.DocumentAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import kotlinx.android.synthetic.main.fragment_list_content.rv_list

class DocumentFragment:BaseFragment() {

    private var mAdapter: DocumentAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }
    override fun initView() {
        pageSize=25
        initRecycleView()
    }
    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecycleView() {
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(), 20f), DP2PX.dip2px(requireActivity(), 20f),
            DP2PX.dip2px(requireActivity(), 20f), 0
        )
        layoutParams.weight = 1f
        rv_list.layoutParams = layoutParams

        rv_list.layoutManager = GridLayoutManager(requireActivity(), 5)//创建布局管理
        mAdapter = DocumentAdapter(R.layout.item_document, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list?.addItemDecoration(SpaceGridItemDeco(3, 20))
            setEmptyView(R.layout.common_empty)
            setOnItemClickListener { adapter, view, position ->
                val file = data[position]
                MethodManager.gotoDocument(requireActivity(), file)
            }
            setOnItemLongClickListener { adapter, view, position ->
                val file= mAdapter?.data?.get(position)!!
                CommonDialog(requireActivity(),1).setContent(R.string.item_is_delete_tips).builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun ok() {
                            FileUtils.deleteFile(file)
                            val drawPath = file.parent + "/${FileUtils.getUrlName(file.path)}draw/"
                            FileUtils.delete(drawPath)
                            mAdapter?.data?.indexOf(file)?.let { mAdapter?.remove(it) }
                        }
                    })
                true
            }
        }
    }

    override fun fetchData() {
        val path=FileAddress().getPathDocument()
        val totalNum = FileUtils.getFiles(path).size
        setPageNumber(totalNum)
        val files = FileUtils.getDescFiles(path, pageIndex, pageSize)
        mAdapter?.setNewData(files)
    }

    override fun onRefreshData() {
        fetchData()
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.DOCUMENT_DOWNLOAD_EVENT){
            pageIndex=1
            fetchData()
        }
    }
}
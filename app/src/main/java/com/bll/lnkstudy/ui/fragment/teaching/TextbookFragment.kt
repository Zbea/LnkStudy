package com.bll.lnkstudy.ui.fragment.teaching

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.dialog.LongClickManageDialog
import com.bll.lnkstudy.manager.TextbookGreenDaoManager
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.book.TextbookBean
import com.bll.lnkstudy.ui.adapter.TextBookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_list_content.rv_list

/**
 * 课本
 */
class TextbookFragment : BaseMainFragment() {

    private var mAdapter: TextBookAdapter?=null
    private var books= mutableListOf<TextbookBean>()
    private var textId=0
    private var position=0

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_content
    }

    /**
     * 实例 传送数据
     */
    fun newInstance(textId: Int): TextbookFragment {
        val fragment = TextbookFragment()
        val bundle = Bundle()
        bundle.putInt("textBook", textId)
        fragment.arguments = bundle
        return fragment
    }


    override fun initView() {
        textId = arguments?.getInt("textBook",0)!!
        pageSize=9

        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(requireActivity(),20f),
            DP2PX.dip2px(requireActivity(),40f),
            DP2PX.dip2px(requireActivity(),20f),0)
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = TextBookAdapter(R.layout.item_textbook, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity,3)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco(3,40))
            setOnItemClickListener { adapter, view, position ->
                MethodManager.gotoTextBookDetails(requireActivity(),books[position])
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@TextbookFragment.position=position
                onLongClick()
                true
            }
        }
    }

    private fun onLongClick(){
        val book=books[position]

        val beans= mutableListOf<ItemList>()
        beans.add(ItemList().apply {
            name="删除"
            resId=R.mipmap.icon_setting_delete
        })
        //将参考课本或者往期课本加锁
        if (textId==1||textId==3){
            beans.add(ItemList().apply {
                name=if (book.isLock)"解锁" else "加锁"
                resId=if (book.isLock) R.mipmap.icon_setting_unlock else R.mipmap.icon_setting_lock
            })
        }
        LongClickManageDialog(requireActivity(),1,book.bookName,beans).builder()
            .setOnDialogClickListener {
                when(it){
                    0->{
                        MethodManager.deleteTextbook(book)
                        mAdapter?.remove(position)
                    }
                    1->{
                        book.isLock=!book.isLock
                        mAdapter?.notifyItemChanged(position)
                        TextbookGreenDaoManager.getInstance().insertOrReplaceBook(book)
                        //修改增量更新
                        DataUpdateManager.editDataUpdate(1,book.bookId,1,book.bookId,Gson().toJson(book))
                    }
                }
            }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==TEXT_BOOK_EVENT){
            fetchData()
        }
    }

    override fun fetchData() {
        val textBook=DataBeanManager.textbookType[textId]
        val total = TextbookGreenDaoManager.getInstance().queryAllTextBook(textBook).size
        books = TextbookGreenDaoManager.getInstance().queryAllTextBook(textBook, pageIndex, pageSize)
        setPageNumber(total)
        mAdapter?.setNewData(books)
    }

    override fun onRefreshData() {
        fetchData()
    }

}
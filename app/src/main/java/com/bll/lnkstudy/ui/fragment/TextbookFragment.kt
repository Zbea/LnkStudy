package com.bll.lnkstudy.ui.fragment

import android.view.View
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
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.book.TextbookBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.ui.adapter.TextBookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_list_tab.rv_list
import java.io.File

/**
 * 课本
 */
class TextbookFragment : BaseMainFragment() {

    private var mAdapter: TextBookAdapter?=null
    private var books= mutableListOf<TextbookBean>()
    private var typeId=0
    private var textBook=""//用来区分课本类型
    private var position=0
    private val bookGreenDaoManager=TextbookGreenDaoManager.getInstance()

    override fun getLayoutId(): Int {
        return R.layout.fragment_list_tab
    }

    override fun initView() {
        pageSize=9
        setTitle(DataBeanManager.listTitle[2])

        initTab()
        initRecyclerView()
    }

    override fun lazyLoad() {
        fetchData()
    }

    private fun initTab(){
        val tabStrs= DataBeanManager.textbookType
        textBook=tabStrs[typeId]
        for (i in tabStrs.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=tabStrs[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        typeId=position
        textBook=itemTabTypes[position].title
        pageIndex=1
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
            name="置顶"
            resId=R.mipmap.icon_setting_top
        })
        when(typeId){
            1->{
                beans.add(ItemList().apply {
                    name=if (book.isLock)"解锁" else "加锁"
                    resId=if (book.isLock) R.mipmap.icon_setting_unlock else R.mipmap.icon_setting_lock
                })
                beans.add(ItemList().apply {
                    name="删除"
                    resId=R.mipmap.icon_setting_delete
                })
            }
            2->{
                beans.add(ItemList().apply {
                    name="删除"
                    resId=R.mipmap.icon_setting_delete
                })
            }
            3->{
                beans.add(ItemList().apply {
                    name=if (book.isLock)"解锁" else "加锁"
                    resId=if (book.isLock) R.mipmap.icon_setting_unlock else R.mipmap.icon_setting_lock
                })
            }
        }

        LongClickManageDialog(requireActivity(),1,book.bookName,beans).builder()
            .setOnDialogClickListener {
                when(it){
                    0->{
                        book.time = System.currentTimeMillis()
                        bookGreenDaoManager.insertOrReplaceBook(book)
                        //修改增量更新
                        DataUpdateManager.editDataUpdate(1,book.bookId,1,book.bookId,Gson().toJson(book))
                        pageIndex=1
                        fetchData()
                    }
                    1->{
                        if (typeId==2){
                            MethodManager.deleteTextbook(book)
                            mAdapter?.remove(position)
                        }
                        else{
                            book.isLock=!book.isLock
                            mAdapter?.notifyItemChanged(position)
                            bookGreenDaoManager.insertOrReplaceBook(book)
                            //修改增量更新
                            DataUpdateManager.editDataUpdate(1,book.bookId,1,book.bookId,Gson().toJson(book))
                        }
                    }
                    2->{
                        MethodManager.deleteTextbook(book)
                        mAdapter?.remove(position)
                    }
                }
            }
    }

    /**
     * 获取未加锁的往期课本、参考课本
     */
    private fun getTextbooksUnLock():MutableList<TextbookBean>{
        val oldStr=getString(R.string.textbook_tab_old)
        val assistStr=getString(R.string.textbook_tab_assist)
        //获取未加锁的课本、参考课本
        val textBooks= mutableListOf<TextbookBean>()
        textBooks.addAll(bookGreenDaoManager.queryAllTextbook(oldStr,false))
        textBooks.addAll(bookGreenDaoManager.queryAllTextbook(assistStr,false))
        return textBooks
    }

    /**
     * 上传本地课本
     */
    fun uploadTextBook(token:String){
        val cloudList= mutableListOf<CloudListBean>()
        val textBooks= getTextbooksUnLock()
        for (book in textBooks){
            //判读是否存在手写内容
            if (FileUtils.isExistContent(book.bookDrawPath)){
                FileUploadManager(token).apply {
                    startZipUpload(book.bookDrawPath,File(book.bookDrawPath).name)
                    setCallBack{
                        cloudList.add(CloudListBean().apply {
                            type=1
                            subTypeStr=book.typeStr
                            grade=book.grade
                            date=System.currentTimeMillis()
                            listJson= Gson().toJson(book)
                            downloadUrl=it
                            zipUrl=book.downloadUrl
                            bookId=book.bookId
                        })
                        if (cloudList.size==textBooks.size)
                            mCloudUploadPresenter.upload(cloudList)
                    }
                }

            }
            else{
                cloudList.add(CloudListBean().apply {
                    type=1
                    grade=book.grade
                    subTypeStr=book.typeStr
                    date=System.currentTimeMillis()
                    listJson= Gson().toJson(book)
                    zipUrl=book.downloadUrl
                    bookId=book.bookId
                })
                if (cloudList.size==textBooks.size)
                    mCloudUploadPresenter.upload(cloudList)
            }
        }
    }

    /**
     * 移除课本到往期课本
     */
    private fun moveTextbook(){
        //所有教材更新为往期教材
        val items= bookGreenDaoManager.queryAllTextBook(getString(R.string.textbook_tab_my))
        for (item in items){
            item.typeStr=getString(R.string.textbook_tab_old)
            //修改增量更新
            DataUpdateManager.editDataUpdate(1,item.bookId,1,item.bookId,Gson().toJson(item))
        }
        bookGreenDaoManager.insertOrReplaceBooks(items)

    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==TEXT_BOOK_EVENT){
            fetchData()
        }
    }

    override fun fetchData() {
        val total = bookGreenDaoManager.queryAllTextBook(textBook).size
        books = bookGreenDaoManager.queryAllTextBook(textBook, pageIndex, pageSize)
        setPageNumber(total)
        mAdapter?.setNewData(books)
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        //删除所有往期教材的图片文件
        for (book in getTextbooksUnLock()){
            MethodManager.deleteTextbook(book)
        }
        moveTextbook()
        fetchData()
    }

}
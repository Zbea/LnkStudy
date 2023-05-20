package com.bll.lnkstudy.ui.fragment

import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.BookManageDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.BookBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.ui.adapter.TextBookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_painting.*
import kotlinx.android.synthetic.main.fragment_textbook.*
import java.io.File

/**
 * 课本
 */
class TextbookFragment : BaseFragment() {

    private var mAdapter: TextBookAdapter?=null
    private var books= mutableListOf<BookBean>()
    private var typeId=0
    private var textBook=""//用来区分课本类型
    private var position=0
    private val bookGreenDaoManager=BookGreenDaoManager.getInstance()

    override fun getLayoutId(): Int {
        return R.layout.fragment_textbook
    }

    override fun initView() {
        pageSize=9
        setTitle(R.string.main_textbook_title)

        initTab()
        initRecyclerView()

        fetchData()
    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab(){
        val tabStrs= DataBeanManager.textbookType
        textBook=tabStrs[typeId]
        for (i in tabStrs.indices) {
            rg_group.addView(getRadioButton(i ,tabStrs[i],tabStrs.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            typeId=id
            textBook=tabStrs[id]
            pageIndex=1
            fetchData()
        }
    }

    private fun initRecyclerView(){
        mAdapter = TextBookAdapter(R.layout.item_textbook, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity,3)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(3,DP2PX.dip2px(activity,33f),38))
            setOnItemClickListener { adapter, view, position ->
                gotoTextBookDetails(books[position].bookId)
            }
            onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
                this@TextbookFragment.position=position
                if (typeId==0){
                    CommonDialog(requireActivity()).setContent(R.string.book_is_delete_all_textbook).builder()
                        .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                for (book in books){
                                    bookGreenDaoManager.deleteBook(book) //删除本地数据库
                                    FileUtils.deleteFile(File(book.bookPath))//删除下载的书籍资源
                                    FileUtils.deleteFile(File(book.bookDrawPath))
                                    mAdapter?.remove(position)
                                    //删除增量更新
                                    DataUpdateManager.deleteDateUpdate(1,book.id.toInt(),0,book.bookId)
                                }
                                books.clear()
                                mAdapter?.notifyDataSetChanged()

                            }
                        })
                }
                else{
                    onLongClick()
                }
                true
            }
        }
    }

    private fun onLongClick(){
        val book=books[position]
        val type=if (typeId==3) 2 else 1
        BookManageDialog(requireActivity(),screenPos,type,book).builder()
            .setOnDialogClickListener(object : BookManageDialog.OnDialogClickListener {
                override fun onCollect() {
                }
                override fun onDelete() {
                    delete()
                }
                override fun onLock() {
                    book.isLock=!book.isLock
                    mAdapter?.notifyItemChanged(position)
                    bookGreenDaoManager.insertOrReplaceBook(book)
                    //修改增量更新
                    DataUpdateManager.editDataUpdate(1,book.id.toInt(),0,book.bookId
                        ,Gson().toJson(book))
                }
            })

    }

    //删除书架书籍
    private fun delete(){
        CommonDialog(requireActivity(),screenPos).setContent(R.string.item_is_delete_tips).builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                val book=books[position]
                bookGreenDaoManager.deleteBook(book) //删除本地数据库
                FileUtils.deleteFile(File(book.bookPath))//删除下载的书籍资源
                FileUtils.deleteFile(File(book.bookDrawPath))
                mAdapter?.remove(position)
                //删除增量更新
                DataUpdateManager.deleteDateUpdate(1,book.id.toInt(),0,book.bookId)
            }
        })
    }


    /**
     * 上传本地课本
     */
    fun uploadTextBook(token:String){
        if (grade==0) return
        val cloudList= mutableListOf<CloudListBean>()

        //获取当前往期教材中未加锁的教材
        val textBooks=bookGreenDaoManager.queryAllTextBookOldUnlock()
        if (textBooks.size==0){
            moveTextbook()
            return
        }
        for (book in textBooks){
            val fileName=book.bookId.toString()
            val drawPath=FileAddress().getPathTextBookDraw(fileName)
            val subTypeId=DataBeanManager.textbookType.indexOf(book.textBookType)
            //判读是否存在手写内容
            if (File(drawPath).exists()){
                FileUploadManager(token).apply {
                    startUpload(drawPath,fileName)
                    setCallBack{
                        cloudList.add(CloudListBean().apply {
                            type=1
                            subType=subTypeId
                            subTypeStr=book.textBookType
                            grade=getGrade(book.grade)
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
                    subType=subTypeId
                    grade=getGrade(book.grade)
                    subTypeStr=book.textBookType
                    date=System.currentTimeMillis()
                    listJson= Gson().toJson(book)
                    downloadUrl="null"
                    zipUrl=book.downloadUrl
                    bookId=book.bookId
                })
                if (cloudList.size==textBooks.size)
                    mCloudUploadPresenter.upload(cloudList)
            }
        }
    }

    /**
     * 得到年级
     */
    private fun getGrade(gradeStr:String):Int{
        var grade=0
        for (item in DataBeanManager.grades){
            if (item.desc==gradeStr){
                grade=item.type
            }
        }
        return grade
    }

    /**
     * 移除教材到往期课本
     */
    private fun moveTextbook(){
        //获取所有往期教材
        val oldBooks=bookGreenDaoManager.queryAllTextBookOldUnlock()
        //删除现在所有所有往期教材
        bookGreenDaoManager.deleteBooks(oldBooks)
        //删除所有往期教材的图片文件
        for (item in oldBooks){
            FileUtils.deleteFile(File(item.bookPath))
            FileUtils.deleteFile(File(item.bookDrawPath))
            //删除增量更新
            DataUpdateManager.deleteDateUpdate(1,item.id.toInt(),0,item.bookId)
        }

        //所有教材更新为往期教材
        val items=bookGreenDaoManager.queryAllTextBookOther()
        for (item in items){
            item.dateState=1
            //修改增量更新
            DataUpdateManager.editDataUpdate(1,item.id.toInt(),0,item.bookId
                ,Gson().toJson(item))
        }
        bookGreenDaoManager.insertOrReplaceBooks(items)
        if (typeId!=3){
            books.clear()
            mAdapter?.notifyDataSetChanged()
        }
        else{
            pageIndex=1
            fetchData()
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==TEXT_BOOK_EVENT){
            fetchData()
        }
    }

    override fun fetchData() {
        var total =0
        if (typeId == 3){
            total = bookGreenDaoManager.queryAllTextBookOld().size
            books = bookGreenDaoManager.queryAllTextBookOld(pageIndex, pageSize)
        }
        else{
            books = bookGreenDaoManager.queryAllTextBook(textBook, pageIndex, pageSize)
            total = bookGreenDaoManager.queryAllTextBook(textBook).size
        }
        setPageNumber(total)
        mAdapter?.setNewData(books)
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        super.uploadSuccess(cloudIds)
        moveTextbook()
    }

}
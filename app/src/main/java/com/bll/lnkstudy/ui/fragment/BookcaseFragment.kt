package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bll.lnkstudy.Constants.Companion.BOOK_EVENT
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.manager.ItemTypeDaoManager
import com.bll.lnkstudy.mvp.model.book.BookBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.ui.activity.book.BookcaseTypeActivity
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco1
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_bookcase.iv_content_down
import kotlinx.android.synthetic.main.fragment_bookcase.iv_content_up
import kotlinx.android.synthetic.main.fragment_bookcase.iv_tips
import kotlinx.android.synthetic.main.fragment_bookcase.ll_book_top
import kotlinx.android.synthetic.main.fragment_bookcase.rv_list
import kotlinx.android.synthetic.main.fragment_bookcase.tv_name
import kotlinx.android.synthetic.main.fragment_bookcase.tv_type


/**
 * 书架
 */
class BookcaseFragment: BaseMainFragment() {

    private var mAdapter: BookAdapter?=null
    private var books= mutableListOf<BookBean>()//所有数据
    private var bookTopBean: BookBean?=null
    private val cloudList= mutableListOf<CloudListBean>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_bookcase
    }

    override fun initView() {
        setTitle(DataBeanManager.listTitle[1])

        tv_type.setOnClickListener {
            ItemTypeDaoManager.getInstance().saveBookBean("诗经楚辞",false)
            customStartActivity(Intent(activity, BookcaseTypeActivity::class.java))
        }
        
        ll_book_top.setOnClickListener {
            if (bookTopBean!=null)
                MethodManager.gotoBookDetails(requireActivity(),bookTopBean)
        }

        initRecyclerView()
        findBook()
    }

    override fun lazyLoad() {

    }

    private fun initRecyclerView(){
        mAdapter = BookAdapter(R.layout.item_book, null).apply {
            rv_list.layoutManager = GridLayoutManager(activity,4)//创建布局管理
            rv_list.adapter = mAdapter
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceGridItemDeco1(4,DP2PX.dip2px(activity,22f),30))
            setOnItemClickListener { adapter, view, position ->
                val bookBean=books[position]
                MethodManager.gotoBookDetails(requireActivity(),bookBean)
            }
        }
    }


    /**
     * 查找本地书籍
     */
    private fun findBook(){
        iv_tips?.visibility=if (ItemTypeDaoManager.getInstance().isExistBookType) View.VISIBLE else View.GONE

        books=BookGreenDaoManager.getInstance().queryAllBook(true,13)
        if (books.size==0){
            bookTopBean=null
        }
        else{
            bookTopBean=books[0]
            books.removeFirst()
        }
        mAdapter?.setNewData(books)
        onChangeTopView()
    }


    //设置头部view显示 (当前页的第一个)
    private fun onChangeTopView(){
        if (bookTopBean!=null){
            setImageUrl(bookTopBean?.imageUrl!!,iv_content_up)
            setImageUrl(bookTopBean?.imageUrl!!,iv_content_down)
            tv_name?.text=bookTopBean?.bookName
        }
        else{
            iv_content_up?.setImageResource(0)
            iv_content_down?.setImageResource(0)
            tv_name?.text=""
        }
    }


    private fun setImageUrl(url: String,image:ImageView){
        GlideUtils.setImageRoundUrl(activity,url,image,5)
    }


    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==BOOK_EVENT){
            findBook()
        }
    }

    fun upload(token:String){
        cloudList.clear()
        val books= BookGreenDaoManager.getInstance().queryAllByHalfYear()
        //遍历上传书籍
        for (item in books){
            if (FileUtils.isExistContent(item.bookDrawPath)){
                FileUploadManager(token).apply {
                    startZipUpload(item.bookDrawPath,item.bookId.toString())
                    setCallBack{
                        cloudList.add(CloudListBean().apply {
                            type=6
                            zipUrl=item.downloadUrl
                            downloadUrl=it
                            subTypeStr=item.subtypeStr
                            date=System.currentTimeMillis()
                            listJson= Gson().toJson(item)
                            bookId=item.bookId
                        })
                        if (cloudList.size==books.size)
                            mCloudUploadPresenter.upload(cloudList)
                    }
                }
            }
            else{
                cloudList.add(CloudListBean().apply {
                    type=6
                    zipUrl=item.downloadUrl
                    subTypeStr=item.subtypeStr
                    date=System.currentTimeMillis()
                    listJson= Gson().toJson(item)
                    bookId=item.bookId
                })
                if (cloudList.size==books.size)
                    mCloudUploadPresenter.upload(cloudList)
            }
        }
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        for (item in cloudList){
            val bookBean=BookGreenDaoManager.getInstance().queryBookByID(item.bookId)
            MethodManager.deleteBook(bookBean)
        }
    }

}
package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.Constants.Companion.TEXT_BOOK_EVENT
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.BookManageDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.manager.BookGreenDaoManager
import com.bll.lnkstudy.mvp.model.Book
import com.bll.lnkstudy.ui.activity.BookDetailsActivity
import com.bll.lnkstudy.ui.adapter.BookAdapter
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.widget.SpaceGridItemDeco5
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.common_xtab.*
import kotlinx.android.synthetic.main.fragment_textbook.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * 课本
 */
class TextbookFragment : BaseFragment(){

    private var mAdapter: BookAdapter?=null
    private var books= mutableListOf<Book>()
    private var textbook=0//用来区分课本类型
    private var position=0
    private var book:Book?=null


    override fun getLayoutId(): Int {
        return R.layout.fragment_textbook
    }

    /**
     * 查找本地课本
     */
    private fun findData(){
        books=BookGreenDaoManager.getInstance(activity).queryAllBook("0")
        mAdapter?.setNewData(books)

    }

    override fun initView() {
        EventBus.getDefault().register(this)
        setTitle("课本")

        initTab()
        initRecyclerView()
        findData()
    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab(){

        xtab?.newTab()?.setText("我的课本")?.let { it -> xtab?.addTab(it) }
        xtab?.newTab()?.setText("上期课本")?.let { it -> xtab?.addTab(it) }
        xtab?.newTab()?.setText("参考课本")?.let { it -> xtab?.addTab(it) }
        xtab?.getTabAt(1)?.select()
        xtab?.getTabAt(0)?.select()

        xtab?.setOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                when(tab?.text.toString() ) {
                    "我的课本" -> {
                        textbook=0
                    }
                    "上期课本" -> {
                        textbook=1
                    }
                    "参考课本" -> {
                        textbook=2
                    }
                }
                findData()
            }
            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }
            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }

        })

    }

    private fun initRecyclerView(){

        rv_list.layoutManager = GridLayoutManager(activity,3)//创建布局管理
        mAdapter = BookAdapter(R.layout.item_textbook, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_book_empty)
        rv_list?.addItemDecoration(SpaceGridItemDeco5(71,38))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            var intent=Intent(activity,BookDetailsActivity::class.java)
//            intent.putExtra(Intent.EXTRA_LAUNCH_SCREEN, Intent.EXTRA_LAUNCH_SCREEN_PANEL_BOTH)
            intent.putExtra("book_id",books[position].id)
            startActivity(intent)
        }

        mAdapter?.onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
            this.position=position
            book=books[position]
            onLongClick()
        }

    }

    //长按显示课本管理
    private fun onLongClick(): Boolean {
        val dialogManager= BookManageDialog(requireActivity(),0,book!!)
        dialogManager.builder().setOnDialogClickListener(object : BookManageDialog.OnDialogClickListener {
            override fun onCollect() {
                book?.isCollect=true
                books[position].isCollect=true
                mAdapter?.notifyDataSetChanged()
                BookGreenDaoManager.getInstance(activity).insertOrReplaceBook(book)
                showToast("收藏成功")
            }
            override fun onDelete() {
                delete()
            }
            override fun onMove() {
                book?.bookType=1
                BookGreenDaoManager.getInstance(activity).insertOrReplaceBook(book)
                books.remove(book)
                mAdapter?.notifyDataSetChanged()
            }
        })

        return true
    }

    //删除课本书籍
    private fun delete(){
        CommonDialog(activity).setContent("确认删除该书籍？").builder().setDialogClickListener(object :
            CommonDialog.OnDialogClickListener {
            override fun cancel() {
            }
            override fun ok() {
                BookGreenDaoManager.getInstance(activity).deleteBook(book) //删除本地数据库
                FileUtils.deleteFile(File(book?.bookPath))//删除下载的书籍资源
                books.remove(book)
                mAdapter?.notifyDataSetChanged()
                EventBus.getDefault().post(TEXT_BOOK_EVENT)
            }
        })
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag==TEXT_BOOK_EVENT){
            findData()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
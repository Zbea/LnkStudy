package com.bll.lnkstudy.ui.activity

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.EventBusData
import com.bll.lnkstudy.mvp.model.MainList
import com.bll.lnkstudy.ui.adapter.MainListAdapter
import com.bll.lnkstudy.ui.fragment.*
import com.bll.lnkstudy.utils.SPUtil
import kotlinx.android.synthetic.main.ac_main.*
import org.greenrobot.eventbus.EventBus

open class MainActivity : BaseAppCompatActivity() {

    private var lastPosition = 0
    private var mHomeAdapter: MainListAdapter? = null
    private var mData= mutableListOf<MainList>()
    private var lastFragment: Fragment? = null

    private var mainFragment: MainFragment? = null
    private var bookcaseFragment: BookCaseFragment? = null
    private var textbookFragment: TextbookFragment? = null
    private var paperFragment: PaperFragment? = null
    private var homeworkFragment: HomeworkFragment? = null
    private var noteFragment: NoteFragment? = null
    private var paintingFragment: PaintingFragment? = null
    private var teachFragment: TeachFragment? = null


    override fun layoutId(): Int {
        return R.layout.ac_main
    }

    override fun initData() {
        mData= DataBeanManager.getIndexData()
        val courses=SPUtil.getList("courses")
        if (courses!=null){
            DataBeanManager.courses=courses
        }
    }

    override fun initView() {
        //发送通知，全屏自动收屏到主页的另外一边
        EventBus.getDefault().post(EventBusData().apply {
            event=Constants.SCREEN_EVENT
            screen=getCurrentScreenPos()
        })

        mainFragment = MainFragment()
        bookcaseFragment = BookCaseFragment()
        textbookFragment= TextbookFragment()
        paperFragment = PaperFragment()
        homeworkFragment = HomeworkFragment()
        noteFragment= NoteFragment()
        paintingFragment = PaintingFragment()
        teachFragment = TeachFragment()

        switchFragment(lastFragment, mainFragment)

        mHomeAdapter = MainListAdapter(R.layout.item_main_list, mData).apply {
            rv_list.layoutManager = LinearLayoutManager(this@MainActivity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemClickListener { adapter, view, position ->
                updateItem(lastPosition, false)//原来的位置去掉勾选
                updateItem(position, true)//更新新的位置
                when (position) {
                    0 -> switchFragment(lastFragment, mainFragment)//首页
                    1 -> switchFragment(lastFragment, bookcaseFragment)//书架
                    2 -> switchFragment(lastFragment, textbookFragment)//课本
                    3 -> switchFragment(lastFragment, homeworkFragment)//作业
                    4 -> switchFragment(lastFragment, paperFragment)//考卷
                    5 -> switchFragment(lastFragment, noteFragment)//笔记
                    6 -> switchFragment(lastFragment, paintingFragment)//书画
                    7 -> switchFragment(lastFragment, teachFragment)//义教
                }
                lastPosition=position
            }
        }

        iv_user.setOnClickListener {
            customStartActivity(Intent(this,AccountInfoActivity::class.java))
        }

    }

    //跳转笔记
    fun goToNote(){
        mHomeAdapter?.updateItem(lastPosition, false)//原来的位置去掉勾选
        mHomeAdapter?.updateItem(5, true)//更新新的位置
        switchFragment(lastFragment, noteFragment)
        lastPosition=5
    }


    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            if (!to!!.isAdded) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(R.id.frame_layout, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }


}
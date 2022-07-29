package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.SettingDialog
import com.bll.lnkstudy.manager.DataBeanManager
import com.bll.lnkstudy.mvp.model.MainListBean
import com.bll.lnkstudy.ui.adapter.MainListAdapter
import com.bll.lnkstudy.ui.fragment.*
import com.bll.lnkstudy.utils.SystemSettingUtils
import kotlinx.android.synthetic.main.ac_main.*

class MainActivity : BaseActivity() {

    private var lastPosition = 0;
    private var mHomeAdapter: MainListAdapter? = null
    private var mData: ArrayList<MainListBean>? = null
    private var lastFragment: Fragment? = null

    private var mainFragment: MainFragment? = null
    private var bookcaseFragment: BookCaseFragment? = null
    private var textbookFragment: TextbookFragment? = null
    private var testPaperFragment: TestPaperFragment? = null
    private var homeworkFragment: HomeworkFragment? = null
    private var noteFragment: NoteFragment? = null
    private var paintingFragment: PaintingFragment? = null
    private var teachFragment: TeachFragment? = null

    override fun layoutId(): Int {
        return R.layout.ac_main
    }

    override fun initData() {
        mData=DataBeanManager.getIncetance().getIndexData(this)
    }


    override fun initView() {

        mainFragment = MainFragment()
        bookcaseFragment = BookCaseFragment()
        textbookFragment= TextbookFragment()
        testPaperFragment = TestPaperFragment()
        homeworkFragment = HomeworkFragment()
        noteFragment= NoteFragment()
        paintingFragment = PaintingFragment()
        teachFragment = TeachFragment()

        switchFragment(lastFragment, mainFragment)

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mHomeAdapter = MainListAdapter(R.layout.item_main_list, mData)
        rv_list.adapter = mHomeAdapter
        mHomeAdapter?.bindToRecyclerView(rv_list)
        mHomeAdapter?.setOnItemClickListener { adapter, view, position ->

            mHomeAdapter?.updateItem(lastPosition, false)//原来的位置去掉勾选
            mHomeAdapter?.updateItem(position, true)//更新新的位置

            when (position) {
                0 -> switchFragment(lastFragment, mainFragment)//首页
                1 -> switchFragment(lastFragment, bookcaseFragment)//书架
                2 -> switchFragment(lastFragment, textbookFragment)//课本
                3 -> switchFragment(lastFragment, homeworkFragment)//作业
                4 -> switchFragment(lastFragment, testPaperFragment)//考卷
                5 -> switchFragment(lastFragment, noteFragment)//笔记
                6 -> switchFragment(lastFragment, paintingFragment)//书画
                7 -> switchFragment(lastFragment, teachFragment)//义教
            }

            lastPosition=position

        }

        iv_user.setOnClickListener {
            startActivity(Intent(this,AccountInfoActivity::class.java))
        }

        tv_setting.setOnClickListener {
            showSettingView()
        }

    }

    //跳转课本
    fun goToTextBook(){
        mHomeAdapter?.updateItem(lastPosition, false)//原来的位置去掉勾选
        mHomeAdapter?.updateItem(2, true)//更新新的位置
        switchFragment(lastFragment, textbookFragment)
        lastPosition=2
    }

    //跳转笔记
    fun goToNote(){
        mHomeAdapter?.updateItem(lastPosition, false)//原来的位置去掉勾选
        mHomeAdapter?.updateItem(5, true)//更新新的位置
        switchFragment(lastFragment, noteFragment)
        lastPosition=5
    }

    private var settingDialog:SettingDialog?=null
    //展示设置view
    private fun showSettingView(){
        if (settingDialog==null)
        {
            settingDialog=SettingDialog(this).builder()
            settingDialog?.setOnDialogClickListener(object : SettingDialog.OnClickListener {
                override fun onClickBookStore() {
                    startActivity(Intent(this@MainActivity,BookStoreTypeActivity::class.java))
                }

                override fun onClickAppStore() {
                    startActivity(Intent(this@MainActivity,AppListActivity::class.java))
                }

                override fun onClickAirPlaneMode() {
                    SystemSettingUtils.setAirPlaneMode(this@MainActivity,true)
                }

                override fun onRecycleBin() {

                }
            })
        }
        else{
            settingDialog?.show()
        }

    }

    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = supportFragmentManager
            val ft = fm.beginTransaction()

            val bookCaseTypeFragment=fm.findFragmentByTag( "BookCaseTypeFragment")
            val bookCaseMyCollectFragment=fm.findFragmentByTag( "BookCaseMyCollectFragment")
            if (bookCaseTypeFragment?.isVisible == true){
                ft.remove(bookCaseTypeFragment)
            }
            if (bookCaseMyCollectFragment?.isVisible == true){
                ft.remove(bookCaseMyCollectFragment)
            }

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



    private var startY=0
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action==MotionEvent.ACTION_DOWN)
        {
            startY= event.y.toInt()
        }
        if(event?.action==MotionEvent.ACTION_UP){
            var sub=event.y.toInt()-startY
            if (startY!!<100&&sub!!>200){
                showSettingView()
            }
        }

        return super.onTouchEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.getKeyCode() === KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }

}
package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.ui.activity.ListActivity
import kotlinx.android.synthetic.main.fragment_painting.*

/**
 * 书画
 */
class PaintingFragment : BaseFragment(){

    private var typeStr="书法"//类型
    private var dynastyStr="汉朝"//朝代

    override fun getLayoutId(): Int {
        return R.layout.fragment_painting
    }

    override fun initView() {
        setPageTitle("书画")
        setDisBackShow()
        initTab()

        iv_han.setOnClickListener {
            dynastyStr="汉朝"
            onClick(1)
        }
        iv_tang.setOnClickListener {
            dynastyStr="唐朝"
            onClick(1)
        }
        iv_song.setOnClickListener {
            dynastyStr="宋朝"
            onClick(1)
        }
        iv_yuan.setOnClickListener {
            dynastyStr="元朝"
            onClick(1)
        }
        iv_ming.setOnClickListener {
            dynastyStr="明朝"
            onClick(1)
        }
        iv_qing.setOnClickListener {
            dynastyStr="清朝"
            onClick(1)
        }
        iv_jd.setOnClickListener {
            dynastyStr="近代"
            onClick(1)
        }
        iv_dd.setOnClickListener {
            dynastyStr="当代"
            onClick(1)
        }
        iv_sm.setOnClickListener {
            dynastyStr="素描绘画"
            onClick(1)
        }
        iv_yb.setOnClickListener {
            dynastyStr="硬笔书法"
            onClick(2)
        }

    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab(){

        xtab?.newTab()?.setText("书法")?.let { it -> xtab?.addTab(it) }
        xtab?.newTab()?.setText("山水画")?.let { it -> xtab?.addTab(it) }
        xtab?.newTab()?.setText("花鸟画")?.let { it -> xtab?.addTab(it) }
        xtab?.newTab()?.setText("人物画")?.let { it -> xtab?.addTab(it) }
        xtab?.newTab()?.setText("我的画本")?.let { it -> xtab?.addTab(it) }
        xtab?.newTab()?.setText("我的硬笔")?.let { it -> xtab?.addTab(it) }
        xtab?.getTabAt(1)?.select()
        xtab?.getTabAt(0)?.select()

        xtab?.setOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: XTabLayout.Tab?) {
                typeStr=tab?.text.toString()
            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }

            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }

        })

    }


    private fun onClick(t: Int){
        var intent= Intent(activity,ListActivity::class.java)
        intent.putExtra("title", if (t==1) "$dynastyStr   $typeStr" else  dynastyStr)
        intent.putExtra("type",t)
        startActivity(intent)
    }

}
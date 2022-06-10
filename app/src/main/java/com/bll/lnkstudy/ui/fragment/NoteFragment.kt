package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import com.androidkun.xtablayout.XTabLayout
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.NoteAddDialog
import kotlinx.android.synthetic.main.fragment_note.*

/**
 * 笔记
 */
class NoteFragment : BaseFragment(){


    override fun getLayoutId(): Int {
        return R.layout.fragment_note
    }

    override fun initView() {
        setPageTitle("笔记")
        setDisBackShow()
        setShowNoteAdd()
        initTab()
        bindClick()
    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab(){

        xtab?.newTab()?.setText("全部笔记")?.let { it -> xtab?.addTab(it) }
        xtab?.newTab()?.setText("我的日记")?.let { it -> xtab?.addTab(it) }
        xtab?.newTab()?.setText("金句彩段")?.let { it -> xtab?.addTab(it) }
        xtab?.newTab()?.setText("典型题型")?.let { it -> xtab?.addTab(it) }
        xtab?.getTabAt(1)?.select()
        xtab?.getTabAt(0)?.select()

        xtab?.setOnTabSelectedListener(object : XTabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: XTabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: XTabLayout.Tab?) {
            }

            override fun onTabReselected(tab: XTabLayout.Tab?) {
            }

        })

    }


    @SuppressLint("UseRequireInsteadOfGet")
    private fun bindClick(){
        tvAdd?.setOnClickListener {
            NoteAddDialog(activity!!).builder()?.setOnClickListener(object : NoteAddDialog.OnClickListener {
                override fun onClick() {

                }

            })
        }
    }

}
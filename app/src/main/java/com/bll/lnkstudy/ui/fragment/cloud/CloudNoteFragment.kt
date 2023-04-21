package com.bll.lnkstudy.ui.fragment.cloud

import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_painting.*

class CloudNoteFragment:BaseFragment() {

    private var noteTypeStr=""

    override fun getLayoutId(): Int {
        return R.layout.fragment_content
    }

    override fun initView() {
        initTab()
    }

    override fun lazyLoad() {
    }

    private fun initTab(){
        val notes= DataBeanManager.noteBook
        noteTypeStr=notes[0].name
        for (i in notes.indices) {
            rg_group.addView(getRadioButton(i ,notes[i].name,notes.size-1))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            noteTypeStr=notes[id].name
            pageIndex=1
            fetchData()
        }
    }
}
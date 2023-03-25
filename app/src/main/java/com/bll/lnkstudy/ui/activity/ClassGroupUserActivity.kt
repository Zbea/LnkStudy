package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ClassGroupUser
import com.bll.lnkstudy.mvp.presenter.ClassGroupPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.ClassGroupUserAdapter
import kotlinx.android.synthetic.main.ac_classgroup.*


class ClassGroupUserActivity : BaseAppCompatActivity(), IContractView.IClassGroupView {

    private var presenter = ClassGroupPresenter(this)
    private var mAdapter: ClassGroupUserAdapter? = null

    override fun onInsert() {
    }
    override fun onClassGroupList(classGroups: List<ClassGroup>) {
    }
    override fun onQuit() {
    }
    override fun onUser(lists: MutableList<ClassGroupUser>?) {
        mAdapter?.setNewData(lists)
    }

    override fun layoutId(): Int {
        return R.layout.ac_classgroup_user
    }

    override fun initData() {
        presenter.getClassGroupUser()
    }

    override fun initView() {
        setPageTitle(R.string.addressList)

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ClassGroupUserAdapter(R.layout.item_classgroup_user, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
        }
    }



}
package com.bll.lnkstudy.ui.activity

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.ClassGroupAddDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ClassGroupUser
import com.bll.lnkstudy.mvp.presenter.ClassGroupPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.ClassGroupAdapter
import com.bll.lnkstudy.utils.NetworkUtil
import kotlinx.android.synthetic.main.ac_classgroup.*
import kotlinx.android.synthetic.main.common_title.*


class ClassGroupActivity : BaseAppCompatActivity(), IContractView.IClassGroupView {

    private var presenter = ClassGroupPresenter(this)
    private var mAdapter: ClassGroupAdapter? = null
    private var groups = mutableListOf<ClassGroup>()
    private var positionGroup = 0

    override fun onInsert() {
        showToast(R.string.toast_add_classGroup_success)
        presenter.getClassGroupList(true)
    }

    override fun onClassGroupList(classGroups: MutableList<ClassGroup>) {
        groups = classGroups
        mAdapter?.setNewData(groups)

        MethodManager.saveClassGroups(classGroups)
    }

    override fun onQuit() {
        showToast(R.string.toast_out_classGroup_success)
        groups.removeAt(positionGroup)
        mAdapter?.setNewData(groups)
        //退出班群，刷新科目
        MethodManager.saveClassGroups(groups)
    }
    override fun onUser(lists: MutableList<ClassGroupUser>?) {
    }

    override fun layoutId(): Int {
        return R.layout.ac_classgroup
    }

    override fun initData() {
        if (NetworkUtil(this).isNetworkConnected()){
            presenter.getClassGroupList(false)
        }
        else{
            showNetworkDialog()
        }
    }

    override fun initView() {
        setPageTitle(R.string.classGroup)

        showView(iv_setting,iv_manager)
        iv_setting.setImageResource(R.mipmap.icon_group_user)
        iv_manager.setImageResource(R.mipmap.icon_group_add)

        mAdapter = ClassGroupAdapter(R.layout.item_classgroup, groups).apply {
            rv_list.layoutManager = LinearLayoutManager(this@ClassGroupActivity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemChildClickListener { adapter, view, position ->
                if (view.id == R.id.tv_out) {
                    CommonDialog(this@ClassGroupActivity).setContent(R.string.classGroup_is_classGroup_tips).builder()
                        .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                            override fun cancel() {
                            }
                            override fun ok() {
                                positionGroup = position
                                presenter.onQuitClassGroup(groups[position].id)
                            }
                        })
                }
            }
        }

        iv_manager?.setOnClickListener {
            addGroup()
        }

        iv_setting?.setOnClickListener {
            customStartActivity(Intent(this, ClassGroupUserActivity::class.java))
        }

    }

    //加入班群
    private fun addGroup() {
        ClassGroupAddDialog(this).builder()?.setOnDialogClickListener { code ->
            presenter.onInsertClassGroup(code)
        }
    }

    override fun onNetworkConnectionSuccess() {
        presenter.getClassGroupList(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkUtil(this).toggleNetwork(false)
    }
}
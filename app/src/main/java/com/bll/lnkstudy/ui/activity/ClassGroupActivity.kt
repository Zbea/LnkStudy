package com.bll.lnkstudy.ui.activity

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.ClassGroupAddDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ClassGroupUser
import com.bll.lnkstudy.mvp.presenter.ClassGroupPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.ClassGroupAdapter
import kotlinx.android.synthetic.main.ac_classgroup.*
import kotlinx.android.synthetic.main.common_title.*


class ClassGroupActivity : BaseAppCompatActivity(), IContractView.IClassGroupView {

    private var presenter = ClassGroupPresenter(this)
    private var mAdapter: ClassGroupAdapter? = null
    private var groups = mutableListOf<ClassGroup>()
    private var positionGroup = 0

    override fun onInsert() {
        showToast("加入班群成功")
        presenter.getClassGroupList(true)
    }

    override fun onClassGroupList(classGroups: List<ClassGroup>) {
        groups = classGroups as MutableList<ClassGroup>
        mAdapter?.setNewData(groups)
    }

    override fun onQuit() {
        showToast("退出班群成功")
        groups.removeAt(positionGroup)
        mAdapter?.setNewData(groups)
    }
    override fun onUser(lists: MutableList<ClassGroupUser>?) {
    }

    override fun layoutId(): Int {
        return R.layout.ac_classgroup
    }

    override fun initData() {
        presenter.getClassGroupList(true)
    }

    override fun initView() {
        setPageTitle("班群管理")

        showView(iv_manager,iv_save)
        iv_manager.setImageResource(R.mipmap.icon_group_user)
        iv_save.setImageResource(R.mipmap.icon_group_add)

        showLog(System.currentTimeMillis().toString())
        mAdapter = ClassGroupAdapter(R.layout.item_classgroup, groups).apply {
            rv_list.layoutManager = LinearLayoutManager(this@ClassGroupActivity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemChildClickListener { adapter, view, position ->
                if (view.id == R.id.tv_out) {
                    CommonDialog(this@ClassGroupActivity).setContent("确认退出班群？").builder()
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

        iv_save?.setOnClickListener {
            addGroup()
        }

        iv_manager.setOnClickListener {
            customStartActivity(Intent(this,ClassGroupUserActivity::class.java))
        }

    }


    //加入班群
    private fun addGroup() {
        ClassGroupAddDialog(this).builder()?.setOnDialogClickListener { code ->
            presenter.onInsertClassGroup(code)
        }
    }


}
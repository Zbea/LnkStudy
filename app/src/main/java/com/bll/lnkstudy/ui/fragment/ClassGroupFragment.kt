package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.dialog.ClassGroupAddDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ClassGroupUser
import com.bll.lnkstudy.mvp.presenter.ClassGroupPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.ClassGroupUserActivity
import com.bll.lnkstudy.ui.adapter.ClassGroupAdapter
import kotlinx.android.synthetic.main.common_fragment_title.*
import kotlinx.android.synthetic.main.common_title.iv_manager
import kotlinx.android.synthetic.main.fragment_classgroup.*
import org.greenrobot.eventbus.EventBus


class ClassGroupFragment : BaseFragment(), IContractView.IClassGroupView {

    private var presenter = ClassGroupPresenter(this)
    private var mAdapter: ClassGroupAdapter? = null
    private var groups = mutableListOf<ClassGroup>()
    private var positionGroup = 0

    override fun onInsert() {
        showToast(1,R.string.toast_add_classGroup_success)
        presenter.getClassGroupList(true)
    }

    override fun onClassGroupList(classGroups: MutableList<ClassGroup>) {
        groups = classGroups
        mAdapter?.setNewData(groups)

        if (DataBeanManager.classGroups != classGroups){
            DataBeanManager.classGroups=classGroups
            EventBus.getDefault().post(Constants.CLASSGROUP_EVENT)
        }
    }

    override fun onQuit() {
        showToast(1,R.string.toast_out_classGroup_success)
        groups.removeAt(positionGroup)
        mAdapter?.setNewData(groups)
        //退出班群，刷新科目
        DataBeanManager.classGroups=groups
        EventBus.getDefault().post(Constants.CLASSGROUP_EVENT)
    }
    override fun onUser(lists: MutableList<ClassGroupUser>?) {
    }


    override fun getLayoutId(): Int {
        return R.layout.fragment_classgroup
    }

    override fun initView() {
        setTitle(R.string.main_classgroup)

        showView(iv_setting,iv_manager)
        iv_setting.setImageResource(R.mipmap.icon_group_user)
        iv_manager.setImageResource(R.mipmap.icon_group_add)

        mAdapter = ClassGroupAdapter(R.layout.item_classgroup, groups).apply {
            rv_list.layoutManager = LinearLayoutManager(requireActivity())//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            setOnItemChildClickListener { adapter, view, position ->
                if (view.id == R.id.tv_out) {
                    CommonDialog(requireActivity()).setContent(R.string.classGroup_is_classGroup_tips).builder()
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
            customStartActivity(Intent(requireActivity(), ClassGroupUserActivity::class.java))
        }

    }

    override fun lazyLoad() {
        presenter.getClassGroupList(false)
    }

    //加入班群
    private fun addGroup() {
        ClassGroupAddDialog(requireActivity()).builder()?.setOnDialogClickListener { code ->
            presenter.onInsertClassGroup(code)
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        lazyLoad()
    }

}
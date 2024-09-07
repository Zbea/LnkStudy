package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.ClassGroupAddDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.ImageDialog
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ClassGroupUserList
import com.bll.lnkstudy.mvp.presenter.ClassGroupPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.ClassGroupAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_title.iv_manager


class ClassGroupActivity : BaseAppCompatActivity(), IContractView.IClassGroupView {

    private lateinit var presenter :ClassGroupPresenter
    private var mAdapter: ClassGroupAdapter? = null
    private var groups = mutableListOf<ClassGroup>()
    private var positionGroup = 0

    override fun onInsert() {
        showToast(R.string.toast_add_classGroup_success)
        presenter.getClassGroupList(true)
    }
    override fun onClassGroupList(classGroups: MutableList<ClassGroup>) {
        MethodManager.saveClassGroups(classGroups)
        groups = classGroups
        mAdapter?.setNewData(groups)
    }
    override fun onQuit() {
        presenter.getClassGroupList(false)
    }
    override fun onUser(userList: ClassGroupUserList?) {
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        initChangeScreenData()
        if (NetworkUtil(this).isNetworkConnected()){
            presenter.getClassGroupList(true)
        }
        else{
            groups=MethodManager.getClassGroups()
        }
    }

    override fun initChangeScreenData() {
        presenter = ClassGroupPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle(R.string.classGroup)
        setImageManager(R.mipmap.icon_add)

        iv_manager?.setOnClickListener {
            addGroup()
        }

        initRecyclerView()
    }

    private fun initRecyclerView(){
        val layoutParams= LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.setMargins(
            DP2PX.dip2px(this,100f), DP2PX.dip2px(this,40f),
            DP2PX.dip2px(this,100f), DP2PX.dip2px(this,20f))
        layoutParams.weight=1f
        rv_list.layoutParams= layoutParams

        mAdapter = ClassGroupAdapter(R.layout.item_classgroup, groups).apply {
            rv_list.layoutManager = LinearLayoutManager(this@ClassGroupActivity)//创建布局管理
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
            rv_list.addItemDecoration(SpaceItemDeco(0,0,0, DP2PX.dip2px(this@ClassGroupActivity,20f),false))
            setOnItemChildClickListener { adapter, view, position ->
                positionGroup = position
                val classGroup=groups[position]
                when(view.id){
                    R.id.tv_out->{
                        CommonDialog(this@ClassGroupActivity).setContent(R.string.classGroup_is_classGroup_tips).builder()
                            .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                                override fun cancel() {
                                }
                                override fun ok() {
                                    presenter.onQuitClassGroup(classGroup.classId)
                                }
                            })
                    }
                    R.id.tv_info->{
                        val intent= Intent(this@ClassGroupActivity, ClassGroupUserActivity::class.java)
                        val bundle= Bundle()
                        bundle.putSerializable("classGroup",classGroup)
                        intent.putExtra("bundle",bundle)
                        customStartActivity(intent)
                    }
                    R.id.tv_course->{
                        ImageDialog(this@ClassGroupActivity, arrayListOf(classGroup.imageUrl)).builder()
                    }
                }
            }
        }
    }

    //加入班群
    private fun addGroup() {
        ClassGroupAddDialog(this).builder()?.setOnDialogClickListener { code ->
            presenter.onInsertClassGroup(code)
        }
    }

    override fun onNetworkConnectionSuccess() {
        presenter.getClassGroupList(true)
    }

}
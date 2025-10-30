package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.ClassGroupAddDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.presenter.ClassGroupPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.ClassGroupAdapter
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_list.rv_list
import kotlinx.android.synthetic.main.common_title.iv_manager
import org.greenrobot.eventbus.EventBus


class ClassGroupActivity : BaseAppCompatActivity(), IContractView.IClassGroupView {

    private lateinit var presenter :ClassGroupPresenter
    private var mAdapter: ClassGroupAdapter? = null
    private var groups = mutableListOf<ClassGroup>()
    private var positionGroup = 0
    private var classGroupAddDialog:ClassGroupAddDialog?=null

    override fun onInsert() {
        showToast(R.string.toast_add_classGroup_success)
        EventBus.getDefault().post(Constants.CLASSGROUP_REFRESH_EVENT)
        presenter.getClassGroupList(true)
    }

    override fun onClassInfo(classGroup: ClassGroup) {
        if (classGroup.name==null){
            classGroupAddDialog?.setTextInfo("")
        }
        else{
            val info="班级信息：${classGroup.name} ${classGroup.teacher}"
            classGroupAddDialog?.setTextInfo(info)
        }
    }

    override fun onClassGroupList(classGroups: MutableList<ClassGroup>) {
        MethodManager.saveClassGroups(classGroups)
        groups = classGroups
        mAdapter?.setNewData(groups)
    }
    override fun onQuit() {
        EventBus.getDefault().post(Constants.CLASSGROUP_REFRESH_EVENT)
        presenter.getClassGroupList(false)
    }

    override fun layoutId(): Int {
        return R.layout.ac_list
    }

    override fun initData() {
        initChangeScreenData()
        if (NetworkUtil.isNetworkConnected()){
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
                }
            }
        }
    }

    //加入班群
    private fun addGroup() {
        classGroupAddDialog=ClassGroupAddDialog(this).builder()
        classGroupAddDialog?.setOnDialogClickListener (object : ClassGroupAddDialog.OnDialogClickListener {
            override fun onClick(code: Int) {
                presenter.onInsertClassGroup(code)
            }
            override fun onEditTextCode(code: Int) {
                presenter.onClassGroupInfo(code)
            }
        })
    }

    override fun onNetworkConnectionSuccess() {
        presenter.getClassGroupList(true)
    }

}
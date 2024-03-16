package com.bll.lnkstudy.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.model.ClassGroupUser
import com.bll.lnkstudy.mvp.model.ClassGroupUserList
import com.bll.lnkstudy.mvp.presenter.ClassGroupPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.ClassGroupUserAdapter
import kotlinx.android.synthetic.main.ac_classgroup_user.*


class ClassGroupUserActivity : BaseAppCompatActivity(), IContractView.IClassGroupView {

    private lateinit var presenter :ClassGroupPresenter
    private var mAdapter: ClassGroupUserAdapter? = null
    private var mClassGroup: ClassGroup? = null
    private var users= mutableListOf<ClassGroupUser>()

    override fun onInsert() {
    }
    override fun onClassGroupList(classGroups: List<ClassGroup>) {
    }
    override fun onQuit() {
    }
    override fun onUser(userList: ClassGroupUserList) {
       if (mClassGroup?.state==1){
           for (item in userList.teacherList){
               users.add(ClassGroupUser().apply {
                   name=item.name
                   job=item.subject
                   phone=item.phone
               })
           }
       }
        users.addAll(userList.studentList)
        mAdapter?.setNewData(users)
    }

    override fun layoutId(): Int {
        return R.layout.ac_classgroup_user
    }

    override fun initData() {
        initChangeData()
        mClassGroup = intent.getBundleExtra("bundle")?.getSerializable("classGroup") as ClassGroup
        val map=HashMap<String,Any>()
        map["classId"]=mClassGroup?.classId!!
        map["classGroupId"]=mClassGroup?.classGroupId!!
        presenter.getClassGroupUser(map)
    }

    override fun initChangeData() {
        presenter = ClassGroupPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle("详情")

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ClassGroupUserAdapter(R.layout.item_classgroup_user, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
        }
    }

}
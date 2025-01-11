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
import com.bll.lnkstudy.utils.DP2PX
import kotlinx.android.synthetic.main.ac_classgroup_user.iv_arrow_page_down
import kotlinx.android.synthetic.main.ac_classgroup_user.iv_arrow_page_up
import kotlinx.android.synthetic.main.ac_classgroup_user.rv_list


class ClassGroupUserActivity : BaseAppCompatActivity(), IContractView.IClassGroupView {

    private lateinit var presenter :ClassGroupPresenter
    private var mAdapter: ClassGroupUserAdapter? = null
    private var mClassGroup: ClassGroup? = null
    private var users= mutableListOf<ClassGroupUser>()

    override fun onUser(userList: ClassGroupUserList) {
        users.clear()
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
        initChangeScreenData()
        mClassGroup = intent.getBundleExtra("bundle")?.getSerializable("classGroup") as ClassGroup
        fetchData()
    }

    override fun initChangeScreenData() {
        presenter = ClassGroupPresenter(this,getCurrentScreenPos())
    }

    override fun initView() {
        setPageTitle("详情")

        iv_arrow_page_up.setOnClickListener {
            rv_list.scrollBy(0,-DP2PX.dip2px(this,100f))
        }

        iv_arrow_page_down.setOnClickListener {
            rv_list.scrollBy(0, DP2PX.dip2px(this,100f))
        }

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = ClassGroupUserAdapter(R.layout.item_classgroup_user, null).apply {
            rv_list.adapter = this
            bindToRecyclerView(rv_list)
        }
    }


    override fun fetchData() {
        val map=HashMap<String,Any>()
        map["classId"]=mClassGroup?.classId!!
        map["classGroupId"]=mClassGroup?.classGroupId!!
        presenter.getClassGroupUser(map)
    }

    override fun onNetworkConnectionSuccess() {
        fetchData()
    }
}
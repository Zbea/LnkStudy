package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.AccountBuyVipDialog
import com.bll.lnkstudy.dialog.ClassGroupAddDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.mvp.model.AccountList
import com.bll.lnkstudy.mvp.model.AccountOrder
import com.bll.lnkstudy.mvp.model.ClassGroup
import com.bll.lnkstudy.mvp.presenter.AccountInfoPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.AccountGroupAdapter
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.StringUtils
import kotlinx.android.synthetic.main.ac_account_info.*

class AccountInfoActivity:BaseActivity(), IContractView.IAccountInfoViewI {

    private val presenter=AccountInfoPresenter(this)
    private var nickname=""
    private var mAdapter:AccountGroupAdapter?=null
    private var groups= mutableListOf<ClassGroup>()

    private var accountBuyVipDialog:AccountBuyVipDialog?=null
    private var vipList= mutableListOf<AccountList.ListBean>()

    override fun onLogout() {
        SPUtil.putString("token", "")
        SPUtil.removeObj("user")
        Handler().postDelayed(Runnable {
            startActivity(Intent(this, AccountLoginActivity::class.java))
            ActivityManager.getInstance().finishOthers(AccountLoginActivity::class.java)
        }, 500)
    }

    override fun onEditNameSuccess() {
        showToast("修改姓名成功")
        mUser?.nickname=nickname
        tv_name.text = nickname
    }

    override fun getVipList(list: AccountList?) {
        vipList= list?.list as MutableList<AccountList.ListBean>
        if (vipList.size>0){
            getVipView(vipList)
        }
    }

    override fun onVipOrder(order: AccountOrder?) {
        runOnUiThread {
            mUser?.vipExpiredAt = order?.vipExpiredAt
            tv_member.text = "有效期"+StringUtils.longToStringData(order?.vipExpiredAt!! * 1000L)
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        setTitle("我的账户")

        tv_xd.text =mUser?.balance.toString()
        tv_user.text = mUser?.account
        tv_name.text = mUser?.nickname
        tv_phone.text =  mUser?.telNumber?.substring(0,3)+"****"+mUser?.telNumber?.substring(7,11)

        if (mUser?.vipExpiredAt==0) {
            tv_member.text = "普通会员"
        } else {
            tv_member.text = "有效期"+StringUtils.longToStringData(mUser?.vipExpiredAt!! * 1000L)
        }

        btn_edit_psd.setOnClickListener {
            startActivity(Intent(this,AccountRegisterActivity::class.java).setFlags(2))
        }

        btn_edit_name.setOnClickListener {
            editName()
        }

        btn_logout.setOnClickListener {
            CommonDialog(this).setContent("确认退出登录？").builder().setDialogClickListener(object :
                CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    presenter.logout()
                }
            })
        }


        btn_buy_member.setOnClickListener {
            if (vipList.size>0){
                getVipView(vipList)
            }
            else{
                presenter.getVipList()
            }
        }

        tv_add.setOnClickListener {
            addGroup()
        }

        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = AccountGroupAdapter(R.layout.item_classgroup, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            if (view.id==R.id.tv_out){
                CommonDialog(this).setContent("确认退出班群？").builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                    override fun cancel() {
                    }
                    override fun ok() {
                        groups.removeAt(position)
                        ll_group.visibility=if (groups.size>0) View.VISIBLE else View.GONE
                        mAdapter?.setNewData(groups)
                    }
                })
            }
        }

//        presenter.getVipList()

    }

    /**
     * 修改名称
     */
    private fun editName(){
        InputContentDialog(this,tv_name.text.toString()).builder()?.setOnDialogClickListener(object :
            InputContentDialog.OnDialogClickListener {
            override fun onClick(string: String) {
                nickname = string
                presenter.editName(nickname)
            }
        })
    }

    //获取vip列表
    private fun getVipView(list: List<AccountList.ListBean>){
        if (accountBuyVipDialog==null){
            accountBuyVipDialog=AccountBuyVipDialog(this,list).builder()
            accountBuyVipDialog?.setOnDialogClickListener(object : AccountBuyVipDialog.OnDialogClickListener {
                override fun onClick(id: String) {
                    presenter.postVip(id)
                }
            })
        }
        else{
            accountBuyVipDialog?.show()
        }
    }

    //加入班群
    private fun addGroup(){
        ClassGroupAddDialog(this).builder()?.setOnDialogClickListener(object :
            ClassGroupAddDialog.OnDialogClickListener {
            override fun onClick(code: String) {
                val classGroup=ClassGroup()
                classGroup.groupNumber=code
                classGroup.name="三年一班"
                classGroup.teacher="周老师"
                classGroup.course="语文"
                classGroup.number=10
                groups.add(classGroup)
                ll_group.visibility=if (groups.size>0) View.VISIBLE else View.GONE
                mAdapter?.setNewData(groups)

            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        mUser?.let { SPUtil.putObj("user", it) }
    }


}
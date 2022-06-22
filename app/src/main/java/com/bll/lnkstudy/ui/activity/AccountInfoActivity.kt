package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.mvp.model.AccountList
import com.bll.lnkstudy.mvp.model.AccountOrder
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.presenter.AccountInfoPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.adapter.AccountXdAdapter
import com.bll.lnkstudy.ui.adapter.AccountVipAdapter
import com.bll.lnkstudy.utils.ActivityManager
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.StringUtils
import com.king.zxing.util.CodeUtils
import kotlinx.android.synthetic.main.ac_account_info.*

class AccountInfoActivity:BaseActivity(),
    IContractView.IAccountInfoViewI {

    private val presenter=AccountInfoPresenter(this)
    private var mUser:User?=null
    private var nickname=""
    private var payFlag=0 //支付方式
    private var qrCodeDialog:Dialog?=null
    private var orderThread:OrderThread?=null//定时器
    private val handlerThread = Handler(Looper.myLooper()!!)

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

    override fun getXdList(list: AccountList?) {
        getXDView(list)
    }

    override fun getVipList(list: AccountList?) {
        getVipView(list)
    }

    override fun onXdOrder(order: AccountOrder?) {
        showQrCodeDialog(order?.qrCode)
        checkOrderState(order?.outTradeNo)
    }

    override fun onVipOrder(order: AccountOrder?) {
        runOnUiThread {
            mUser?.vipExpiredAt = order?.vipExpiredAt
            tv_member.text = "有效期"+StringUtils.longToStringData(order?.vipExpiredAt!! * 1000L)
        }
    }

    override fun checkOrder(order: AccountOrder?) {
        //订单支付成功
        if (order?.status == 2) {
            handlerThread.removeCallbacks(orderThread!!)
            if (qrCodeDialog!=null)
                qrCodeDialog?.dismiss()
            runOnUiThread {
                tv_xd.text = "" + order.amount
                mUser?.balance = order.amount
            }
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_account_info
    }

    override fun initData() {
        mUser=SPUtil.getObj("user",User::class.java)

    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        setPageTitle("我的账户")

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

        btn_buy_xd.setOnClickListener {
            presenter.getXdList()
        }

        btn_buy_member.setOnClickListener {
            presenter.getVipList()
        }

    }


    /**
     * 修改名称
     */
    private fun editName(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_account_edit_name)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val name = dialog.findViewById<EditText>(R.id.ed_name)
        name.setText(tv_name.text.toString())
        dialog.show()
        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            nickname = name.text.toString()
            if (nickname.isNullOrEmpty()) {
                showToast("姓名不能为空")
                return@setOnClickListener
            }
            presenter.editName(nickname)
            dialog.dismiss()
        }

    }

    /**
     * 获取学豆列表
     */
    private fun getXDView(list: AccountList?){
        var xuedouID=0;
        var dialog = Dialog(this)
        dialog!!.setContentView(R.layout.dialog_account_xd)
//        dialog!!.window!!.setDimAmount(0f)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val recyclerview = dialog!!.findViewById<RecyclerView>(R.id.rv_list)
        val btn_ok = dialog!!.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog!!.findViewById<Button>(R.id.btn_cancel)
        val rb_wx = dialog!!.findViewById<RadioButton>(R.id.rb_wx)

        recyclerview.layoutManager = LinearLayoutManager(this)
        var mXueDouAdapter = AccountXdAdapter(R.layout.item_account_smoney, list?.list)
        recyclerview.adapter = mXueDouAdapter
        mXueDouAdapter?.setOnItemClickListener { adapter, view, position ->
            mXueDouAdapter?.setItemView(position)
            xuedouID= list?.list?.get(position)?.id !!
        }
        if (list != null && list?.list.size > 0) {
            xuedouID = list?.list[0].id
        }
        btn_cancel.setOnClickListener {
            dialog?.dismiss()
        }
        btn_ok.setOnClickListener {
            payFlag = if (rb_wx.isChecked)  0  else  1
            dialog?.dismiss()
            presenter.postXdOrder(xuedouID.toString())
        }
        dialog?.show()
    }

    //获取vip列表
    private fun getVipView(list: AccountList?){
        var vipID=0;
        var dialog = Dialog(this)
        dialog!!.setContentView(R.layout.dialog_account_vip)
//        dialog!!.window!!.setDimAmount(0f)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val recyclerview = dialog!!.findViewById<RecyclerView>(R.id.rv_list)
        val btn_ok = dialog!!.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog!!.findViewById<Button>(R.id.btn_cancel)
        recyclerview.layoutManager = LinearLayoutManager(this)
        var vipAdapter = AccountVipAdapter(R.layout.item_account_vip, list?.list)
        recyclerview.adapter = vipAdapter
        vipAdapter?.setOnItemClickListener { adapter, view, position ->
            vipAdapter?.setItemView(position)
            vipID= list?.list?.get(position)?.id !!
        }
        if (list != null && list?.list.size > 0) {
            vipID = list?.list[0].id
        }
        btn_cancel.setOnClickListener {
            dialog!!.dismiss()
        }
        btn_ok.setOnClickListener {
            dialog!!.dismiss()
            presenter.postVip(vipID.toString())
        }
        dialog?.show()
    }

    //展示支付二维码的图片
    private fun showQrCodeDialog(url: String?) {
        qrCodeDialog = Dialog(this)
        qrCodeDialog?.setContentView(R.layout.dialog_account_qrcode)
        val iv_qrcode = qrCodeDialog?.findViewById<ImageView>(R.id.iv_qrcode)
        qrCodeDialog?.show()
        val bitmap = CodeUtils.createQRCode(url, 300, null)
        iv_qrcode?.setImageBitmap(bitmap)

        qrCodeDialog?.setOnDismissListener {
            handlerThread.removeCallbacks(orderThread!!)
        }
    }

    //订单轮询 handler?
    private fun checkOrderState(orderID: String?) {
        //create thread
        if (orderThread != null) {
            handlerThread.removeCallbacks(orderThread!!)
        }
        orderThread = OrderThread(orderID)
        orderThread!!.run()
    }

    //定时器 (定时请求订单状态)
    inner class OrderThread(private val orderID: String?) : Runnable {
        override fun run() {
            queryOrderById(orderID!!)
            handlerThread.postDelayed(this, 30*1000)
        }
        //查询订单状态接口
        private fun queryOrderById(orderID: String) {
            presenter.checkOrder(orderID)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mUser?.let { SPUtil.putObj("user", it) }
    }


}
package com.bll.lnkstudy.ui.activity

import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.WalletBuyXdDialog
import com.bll.lnkstudy.mvp.model.AccountOrder
import com.bll.lnkstudy.mvp.model.AccountQdBean
import com.bll.lnkstudy.mvp.model.User
import com.bll.lnkstudy.mvp.presenter.WalletPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.SPUtil
import com.king.zxing.util.CodeUtils
import kotlinx.android.synthetic.main.ac_wallet.tv_buy
import kotlinx.android.synthetic.main.ac_wallet.tv_xdmoney

class WalletActivity:BaseAppCompatActivity(),IContractView.IWalletView{

    private lateinit var walletPresenter:WalletPresenter
    private var xdDialog:WalletBuyXdDialog?=null
    private var xdList= mutableListOf<AccountQdBean>()
    private var qrCodeDialog:Dialog?=null
    private var orderThread: OrderThread?=null//定时器
    private val handlerThread = Handler(Looper.myLooper()!!)

    override fun onXdList(list: MutableList<AccountQdBean>) {
        xdList= list
    }

    override fun onXdOrder(order: AccountOrder) {
        showQrCodeDialog(order.qrCode)
        checkOrderState(order.outTradeNo)
    }

    override fun checkOrder(order: AccountOrder?) {
        //订单支付成功
        if (order?.status == 2) {
            handlerThread.removeCallbacks(orderThread!!)
            qrCodeDialog?.dismiss()
            runOnUiThread {
                mUser?.balance = mUser?.balance?.plus(order.amount)
                tv_xdmoney.text = "" + mUser?.balance
                SPUtil.putObj("user",mUser!!)
            }
        }
    }

    override fun getAccount(user: User) {
        mUser=user
        tv_xdmoney.text="青豆:  "+mUser?.balance
        SPUtil.putObj("user",mUser!!)
    }


    override fun layoutId(): Int {
        return R.layout.ac_wallet
    }

    override fun initData() {
        initChangeScreenData()
        if (NetworkUtil(this).isNetworkConnected()){
            walletPresenter.getXdList(false)
            walletPresenter.accounts()
        }
    }

    override fun initChangeScreenData() {
        walletPresenter=WalletPresenter(this,1)
    }

    override fun initView() {
        tv_xdmoney.text=getString(R.string.xd)+"  "+mUser?.balance

        tv_buy.setOnClickListener {
            if (xdList.size>0){
                getXdView()
            }
            else{
                walletPresenter.getXdList(true)
            }
        }
    }

    //购买学豆
    private fun getXdView(){
        if (xdDialog==null){
            xdDialog= WalletBuyXdDialog(this,xdList).builder()
            xdDialog?.setOnDialogClickListener { id ->
                xdDialog?.dismiss()
                walletPresenter.postXdOrder(id)
            }
        }
        else{
            xdDialog?.show()
        }
    }

    //展示支付二维码的图片
    private fun showQrCodeDialog(url: String) {
        qrCodeDialog = Dialog(this)
        qrCodeDialog?.setContentView(R.layout.dialog_account_qrcode)
        qrCodeDialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        qrCodeDialog?.setCanceledOnTouchOutside(false)
        val iv_qrcode = qrCodeDialog?.findViewById<ImageView>(R.id.iv_qrcode)
        qrCodeDialog?.show()
        val bitmap = CodeUtils.createQRCode(url, DP2PX.dip2px(this,300f), null)
        iv_qrcode?.setImageBitmap(bitmap)

        val iv_close = qrCodeDialog?.findViewById<ImageView>(R.id.iv_close)
        iv_close?.setOnClickListener {
            qrCodeDialog?.dismiss()
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
        orderThread?.run()
    }

    //定时器 (定时请求订单状态)
    inner class OrderThread(private val orderID: String?) : Runnable {
        override fun run() {
            queryOrderById(orderID!!)
            handlerThread.postDelayed(this, 30*1000)
        }
        //查询订单状态接口
        private fun queryOrderById(orderID: String) {
            walletPresenter.checkOrder(orderID)
        }
    }

    override fun onNetworkConnectionSuccess() {
        walletPresenter.getXdList(false)
        walletPresenter.accounts()
    }

}
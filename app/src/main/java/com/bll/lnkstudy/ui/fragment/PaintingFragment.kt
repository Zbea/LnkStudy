package com.bll.lnkstudy.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.ui.activity.MyPaintingListActivity
import com.bll.lnkstudy.utils.ZipUtils
import kotlinx.android.synthetic.main.fragment_painting.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 书画
 */
class PaintingFragment : BaseFragment(){

    private var typeId=0//类型

    override fun getLayoutId(): Int {
        return R.layout.fragment_painting
    }

    @SuppressLint("WrongConstant")
    override fun initView() {

        EventBus.getDefault().register(this)

        setTitle("书画")
        initTab()

        iv_han.setOnClickListener {
            onClick(0)
        }
        iv_tang.setOnClickListener {
            onClick(1)
        }
        iv_song.setOnClickListener {
            onClick(2)
        }
        iv_yuan.setOnClickListener {
            onClick(3)
        }
        iv_ming.setOnClickListener {
            onClick(4)
        }
        iv_qing.setOnClickListener {
            onClick(5)
        }
        iv_jd.setOnClickListener {
            onClick(6)
        }
        iv_dd.setOnClickListener {
            onClick(7)
        }
        iv_hb.setOnClickListener {
            gotoPaintingDrawing(0)
        }
        iv_sf.setOnClickListener {
            gotoPaintingDrawing(1)
        }

        tv_sm.setOnClickListener {
            var intent= Intent(activity,MyPaintingListActivity::class.java)
            intent.putExtra("title", "素描画")
            intent.putExtra("paintingType",4)
            intent.flags= 1
            customStartActivity(intent)
        }

        tv_yb.setOnClickListener {
            var intent= Intent(activity,MyPaintingListActivity::class.java)
            intent.putExtra("title", "硬笔书法")
            intent.putExtra("paintingType",5)
            intent.flags=1
            customStartActivity(intent)
        }

    }

    override fun lazyLoad() {
    }

    //设置头部索引
    private fun initTab(){
        val tabStrs= DataBeanManager.PAINTING
        for (i in 0..3) {
            rg_group.addView(getRadioButton(i ,tabStrs[i],3))
        }
        rg_group.setOnCheckedChangeListener { radioGroup, id ->
            typeId= id
        }
    }

    private fun onClick(time:Int){
        var intent= Intent(activity,MyPaintingListActivity::class.java)
        intent.putExtra("title", "${DataBeanManager.YEARS[time]}   ${DataBeanManager.PAINTING[typeId]}" )
        intent.putExtra("time",time)
        intent.putExtra("paintingType",typeId)
        intent.flags=0
        customStartActivity(intent)
    }

    /**
     * 自动压缩zip
     */
    private fun autoZip() {

        ZipUtils.zip(Constants.PAINTING_PATH + "/$mUserId", "painting", object : ZipUtils.ZipCallback {
            override fun onStart() {
                showLog("painting开始打包上传")
            }
            override fun onProgress(percentDone: Int) {
            }
            override fun onFinish(success: Boolean) {
                showLog("onFinish painting:$success")
            }
            override fun onError(msg: String?) {
                showLog("onError painting:$msg")
            }
        })
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    fun onMessageEvent(msgFlag: String) {
        if (msgFlag == Constants.AUTO_UPLOAD_EVENT) {
            autoZip()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
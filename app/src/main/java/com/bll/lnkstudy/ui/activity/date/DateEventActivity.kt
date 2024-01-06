package com.bll.lnkstudy.ui.activity.date

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.view.EinkPWInterface
import android.view.EinkPWInterface.PWDrawEvent
import android.view.PWDrawObjectHandler
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.dayLong
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.DateDialog
import com.bll.lnkstudy.mvp.model.date.DateBean
import com.bll.lnkstudy.utils.DateUtils
import kotlinx.android.synthetic.main.ac_date_event.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class DateEventActivity:BaseAppCompatActivity() {
    private var mDate: DateBean?=null
    private var nowLong=0L
    private var elik: EinkPWInterface?=null
    private var isErasure=false

    override fun layoutId(): Int {
        return R.layout.ac_date_event
    }

    override fun initData() {
        mDate = intent.getBundleExtra("bundle")?.getSerializable("dateBean") as DateBean
        nowLong=mDate?.time!!
    }

    override fun initView() {
        setPageTitle("日程")
        elik = v_content.pwInterFace
        setContentView()

        iv_up.setOnClickListener {
            nowLong-=dayLong
            setContentView()
        }

        iv_down.setOnClickListener {
            nowLong+=dayLong
            setContentView()
        }

        tv_date.setOnClickListener {
            DateDialog(this).builder().setOnDateListener { dateStr, dateTim ->
                nowLong=dateTim
                setContentView()
            }
        }

        iv_erasure?.setOnClickListener {
            isErasure=!isErasure
            if (isErasure){
                iv_erasure?.setImageResource(R.mipmap.icon_draw_erasure_big)
                elik?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
            }
            else{
                isErasure=false
                //关闭橡皮擦
                iv_erasure?.setImageResource(R.mipmap.icon_draw_erasure)
                elik?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            }
        }

    }

    private fun setContentView(){
        tv_date.text= SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date(nowLong))

        val path=FileAddress().getPathDate(DateUtils.longToStringCalender(nowLong))+"/draw.png"
        elik?.setLoadFilePath(path, true)
        elik?.setDrawEventListener(object : PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }
            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }
            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik?.saveBitmap(true) {}
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().post(Constants.DATE_DRAWING_EVENT)
    }

}
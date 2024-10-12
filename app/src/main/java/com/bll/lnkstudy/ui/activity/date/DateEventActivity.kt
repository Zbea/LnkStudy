package com.bll.lnkstudy.ui.activity.date

import android.os.Handler
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.dayLong
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CalendarSingleDialog
import com.bll.lnkstudy.utils.DateUtils
import kotlinx.android.synthetic.main.common_date_arrow.iv_down
import kotlinx.android.synthetic.main.common_date_arrow.iv_up
import kotlinx.android.synthetic.main.common_date_arrow.tv_date
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateEventActivity:BaseDrawingActivity() {
    private var nowLong=0L

    override fun layoutId(): Int {
        return R.layout.ac_date_event
    }

    override fun initData() {
        nowLong=intent.getLongExtra("date",0)
    }

    override fun initView() {
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
            CalendarSingleDialog(this,45f,190f).builder().setOnDateListener() { dateTim ->
                nowLong=dateTim
                setContentView()
            }
        }

    }

    private fun setContentView(){
        tv_date.text= SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date(nowLong))

        val path=FileAddress().getPathDate(DateUtils.longToStringCalender(nowLong))+"/draw.png"
        elik_b?.setLoadFilePath(path, true)
    }

    override fun onElikSava_b() {
        elik_b?.saveBitmap(true) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        stopErasure()
        Handler().postDelayed( {
            EventBus.getDefault().post(Constants.DATE_DRAWING_EVENT)
        },500)
    }

}
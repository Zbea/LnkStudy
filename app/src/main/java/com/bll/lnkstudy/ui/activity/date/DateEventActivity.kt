package com.bll.lnkstudy.ui.activity.date

import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.Constants.Companion.dayLong
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.DateDialog
import com.bll.lnkstudy.mvp.model.date.DateBean
import com.bll.lnkstudy.utils.DateUtils
import kotlinx.android.synthetic.main.ac_date_event.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class DateEventActivity:BaseDrawingActivity() {
    private var mDate: DateBean?=null
    private var nowLong=0L
    private var isDraw=false

    override fun layoutId(): Int {
        return R.layout.ac_date_event
    }

    override fun initData() {
        mDate = intent.getBundleExtra("bundle")?.getSerializable("dateBean") as DateBean
        nowLong=mDate?.time!!
    }

    override fun initView() {
        elik_b = v_content.pwInterFace
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

    }

    private fun setContentView(){
        tv_date.text= SimpleDateFormat("MM月dd日 E", Locale.CHINA).format(Date(nowLong))

        val path=FileAddress().getPathDate(DateUtils.longToStringCalender(nowLong))+"/draw.png"
        elik_b?.setLoadFilePath(path, true)
    }

    override fun onElikSava_b() {
        elik_b?.saveBitmap(true) {}
        isDraw=true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isDraw)
            EventBus.getDefault().post(Constants.DATE_DRAWING_EVENT)
    }

}
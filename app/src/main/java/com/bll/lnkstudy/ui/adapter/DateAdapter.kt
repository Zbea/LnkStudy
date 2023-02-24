package com.bll.lnkstudy.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.defaultFromStyle
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Date
import com.bll.lnkstudy.mvp.model.DateEventBean
import com.bll.lnkstudy.ui.activity.date.DateDayDetailsActivity
import com.bll.lnkstudy.ui.activity.date.DatePlanDetailsActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DateAdapter(layoutResId: Int, data: List<Date>?) :
    BaseQuickAdapter<Date, BaseViewHolder>(layoutResId, data) {

    @SuppressLint("WrongConstant")
    override fun convert(helper: BaseViewHolder, item: Date) {
        helper.apply {
            item.apply {
                val tvDay = getView<TextView>(R.id.tv_day)
                val tvLunar=getView<TextView>(R.id.tv_lunar)
                tvDay.text = if (day == 0) "" else day.toString()
                if (isNow)
                    tvDay.typeface = defaultFromStyle(BOLD)
                if (isNowMonth) {
                    tvDay.setTextColor(mContext.getColor(R.color.black))
                    tvLunar.setTextColor(mContext.getColor(R.color.gray))
                } else {
                    tvDay.setTextColor(mContext.getColor(R.color.black_90))
                    tvLunar.setTextColor(mContext.getColor(R.color.black_90))
                }
                val str = if (!solar.solar24Term.isNullOrEmpty()) {
                    solar.solar24Term
                } else {
                    if (!solar.solarFestivalName.isNullOrEmpty()) {
                        solar.solarFestivalName
                    } else {
                        if (!lunar.lunarFestivalName.isNullOrEmpty()) {
                            lunar.lunarFestivalName
                        } else {
                            lunar.getChinaDayString(lunar.lunarDay)
                        }
                    }
                }
                tvLunar.text=str

                val rvList=getView<RecyclerView>(R.id.rv_list)
                rvList.layoutManager = LinearLayoutManager(mContext)//创建布局管理
                MyAdapter(R.layout.item_date_child, item.dateEventBeans).apply {
                    rvList.adapter = this
                    bindToRecyclerView(rvList)
                    setOnItemClickListener { adapter, view, position ->
                        val dateEvent=dateEventBeans[position]
                        if (dateEvent.type==0){
                            val intent= Intent(mContext, DatePlanDetailsActivity::class.java)
                            intent.addFlags(1)
                            val bundle = Bundle()
                            bundle.putSerializable("dateEvent", dateEvent)
                            intent.putExtra("bundle", bundle)
                            mContext.startActivity(intent)
                        } else{
                            val intent= Intent(mContext, DateDayDetailsActivity::class.java)
                            intent.addFlags(1)
                            val bundle = Bundle()
                            bundle.putSerializable("dateEvent", dateEvent)
                            intent.putExtra("bundle", bundle)
                            mContext.startActivity(intent)
                        }

                    }
                }

            }
        }

    }


    class MyAdapter(layoutResId: Int, data: List<DateEventBean>?) : BaseQuickAdapter<DateEventBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: DateEventBean) {
            helper.setText(R.id.tv_title,item.title)
        }

    }

}

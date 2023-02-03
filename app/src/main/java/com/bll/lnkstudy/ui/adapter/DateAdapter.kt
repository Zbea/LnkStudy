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
        var tvDay = helper.getView<TextView>(R.id.tv_day)
        var tvLunar=helper.getView<TextView>(R.id.tv_lunar)
        tvDay.text = if (item.day == 0) "" else item.day.toString()
        if (item.isNow)
            tvDay.typeface = defaultFromStyle(BOLD)
        if (item.isNowMonth) {
            tvDay.setTextColor(mContext.getColor(R.color.black))
            tvLunar.setTextColor(mContext.getColor(R.color.gray))
        } else {
            tvDay.setTextColor(mContext.getColor(R.color.black_90))
            tvLunar.setTextColor(mContext.getColor(R.color.black_90))
        }

        val str = if (!item.solar.solar24Term.isNullOrEmpty()) {
            item.solar.solar24Term
        } else {
            if (!item.solar.solarFestivalName.isNullOrEmpty()) {
                item.solar.solarFestivalName
            } else {
                if (!item.lunar.lunarFestivalName.isNullOrEmpty()) {
                    item.lunar.lunarFestivalName
                } else {
                    item.lunar.getChinaDayString(item.lunar.lunarDay)
                }
            }
        }
        tvLunar.text=str

        val rv=helper.getView<RecyclerView>(R.id.rv_list)
        rv.layoutManager = LinearLayoutManager(mContext)//创建布局管理
        val childAdapter= MyAdapter(R.layout.item_date_child, item.dateEventBeans)
        rv.adapter = childAdapter
        childAdapter?.bindToRecyclerView(rv)
        childAdapter.setOnItemClickListener { adapter, view, position ->
            val dateEvent=item.dateEventBeans[position]
            if (dateEvent.type==0){
                val intent= Intent(mContext, DatePlanDetailsActivity::class.java)
                intent.addFlags(1)
                var bundle = Bundle()
                bundle.putSerializable("dateEvent", dateEvent)
                intent.putExtra("bundle", bundle)
                mContext.startActivity(intent)
            } else{
                val intent=Intent(mContext, DateDayDetailsActivity::class.java)
                intent.addFlags(1)
                val bundle = Bundle()
                bundle.putSerializable("dateEvent", dateEvent)
                intent.putExtra("bundle", bundle)
                mContext.startActivity(intent)
            }

        }

    }


    class MyAdapter(layoutResId: Int, data: List<DateEventBean>?) : BaseQuickAdapter<DateEventBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: DateEventBean) {
            helper.setText(R.id.tv_title,item.title)
        }

    }

}

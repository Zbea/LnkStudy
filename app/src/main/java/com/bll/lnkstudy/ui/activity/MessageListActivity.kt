package com.bll.lnkstudy.ui.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.MessageDetailsDialog
import com.bll.lnkstudy.mvp.model.MessageList
import com.bll.lnkstudy.ui.adapter.MainMessageAdapter
import com.bll.lnkstudy.widget.SpaceItemDeco
import kotlinx.android.synthetic.main.ac_message_list.*
import kotlinx.android.synthetic.main.common_page_number.*
import kotlinx.android.synthetic.main.common_title.*

class MessageListActivity:BaseAppCompatActivity() {

    private var lists= mutableListOf<MessageList>()
    private var mAdapter:MainMessageAdapter?=null
    private var pageIndex=1 //当前页码

    override fun layoutId(): Int {
        return R.layout.ac_message_list
    }

    override fun initData() {
        lists= DataBeanManager.getIncetance().message
    }

    override fun initView() {
        setPageTitle("消息中心")
        setPageSetting("删除")
        showView(cb_all)


        rv_list.layoutManager = LinearLayoutManager(this)//创建布局管理
        mAdapter = MainMessageAdapter(R.layout.item_message, null)
        rv_list.adapter = mAdapter
        mAdapter?.bindToRecyclerView(rv_list)
        mAdapter?.setEmptyView(R.layout.common_empty)
        rv_list.addItemDecoration(SpaceItemDeco(0, 0, 0, 20, 0))
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            lists[position].isLook=true
            mAdapter?.notifyDataSetChanged()
            MessageDetailsDialog(this,getCurrentScreenPos(), lists[position]).builder()
        }
        mAdapter?.setType(2)
        pageNumberView()

        cb_all.setOnCheckedChangeListener { compoundButton, b ->
            for (item in lists){
                item.isCheck=b
            }
            mAdapter?.notifyDataSetChanged()
        }

        tv_setting.setOnClickListener {

            var datas=mAdapter?.data
            var it=datas?.iterator()
            while (it?.hasNext() == true){
                if (it?.next()?.isCheck == true){
                    it?.remove()
                }
            }
            mAdapter?.notifyDataSetChanged()
        }

    }

    //翻页处理
    private fun pageNumberView(){
        var pageTotal=lists.size //全部数量
        var pageNum=12
        var pageCount=Math.ceil((pageTotal.toDouble()/pageNum)).toInt()//总共页码
        if (pageTotal==0)
        {
            ll_page_number.visibility= View.GONE
            mAdapter?.notifyDataSetChanged()
            return
        }

        tv_page_current.text=pageIndex.toString()
        tv_page_total.text=pageCount.toString()
        upDateUI()

        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                upDateUI()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                upDateUI()
            }
        }

    }

    //刷新数据
    private fun upDateUI()
    {
        mAdapter?.setNewData(lists)
        tv_page_current.text=pageIndex.toString()
    }

}
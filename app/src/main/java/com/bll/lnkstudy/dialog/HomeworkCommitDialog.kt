package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.SToast
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * pageStart 为课辅作业的 页码开始值
 */
class HomeworkCommitDialog(val context: Context, val screenPos: Int, private val startCount: Int, private val countSize: Int, var title:String) {

    private var dialog: Dialog? = null
    private var pages = mutableListOf<Int>()
    private var type = 1

    fun builder(): HomeworkCommitDialog {

        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_drawing_commit)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val window = dialog?.window
        val layoutParams = window?.attributes
        if (screenPos == 3) {
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.END
            layoutParams?.x = (Constants.WIDTH - DP2PX.dip2px(context, 500f)) / 2
        }
        dialog?.show()

        dialog?.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        val tv_cancel = dialog?.findViewById<TextView>(R.id.tv_cancel)
        val tv_ok = dialog?.findViewById<TextView>(R.id.tv_ok)
        val tv_selector = dialog?.findViewById<TextView>(R.id.tv_message_content)
        val rvList = dialog?.findViewById<RecyclerView>(R.id.rv_list)
        val et_page_start = dialog?.findViewById<EditText>(R.id.et_page_start)
        val et_page_end = dialog?.findViewById<EditText>(R.id.et_page_end)
        val ll_batch = dialog?.findViewById<LinearLayout>(R.id.ll_batch)

        tv_selector?.text = title

        val list = mutableListOf<ItemList>()
        val pageItem1 = ItemList()
        pageItem1.isAdd = false
        list.add(pageItem1)

        val pageItem2 = ItemList()
        pageItem2.isAdd = true
        list.add(pageItem2)

        val rg_group = dialog?.findViewById<RadioGroup>(R.id.rg_group)
        rg_group?.setOnCheckedChangeListener { radioGroup, i ->
            if (i == R.id.rb_single) {
                type = 1
                ll_batch?.visibility = View.GONE
                rvList?.visibility = View.VISIBLE
            } else {
                type = 2
                rvList?.visibility = View.GONE
                ll_batch?.visibility = View.VISIBLE
            }
        }

        val mAdapter = MyAdapter(R.layout.item_drawing_commit_page, list)
        rvList?.layoutManager = GridLayoutManager(context, 6)
        rvList?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        rvList?.addItemDecoration(SpaceGridItemDeco(6, 20))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (list[position].isAdd) {
                val item=list[position]
                item.page=0

                val newItem = ItemList()
                newItem.isAdd = false
                list.add(list.size - 1, newItem)
                mAdapter.setNewData(list)
            }
        }

        tv_cancel?.setOnClickListener {
            dismiss()
        }

        tv_ok?.setOnClickListener {
                pages.clear()
                if (type == 1) {
                    for (item in list) {
                        if (item.page > 0) {
                            if (!pages.contains(item.page))
                                pages.add(item.page)
                        }
                    }
                } else {
                    if (!et_page_start?.text.isNullOrEmpty() && !et_page_end?.text.isNullOrEmpty()) {
                        val pageStart = et_page_start?.text.toString().toInt()
                        val pageEnd = et_page_end?.text.toString().toInt()
                        if (pageEnd > pageStart) {
                            for (i in pageStart..pageEnd) {
                                if (!pages.contains(i))
                                    pages.add(i)
                            }
                        }
                    }
                }
                pages.sort()

                if (pages.size==0){
                    showToast("请输入页码")
                    return@setOnClickListener
                }

                //设置题卷本初始页码不为1的情况
                val realPageIndexs= mutableListOf<Int>()
                for (page in pages){
                    realPageIndexs.add(page+startCount-1)
                }

                if (realPageIndexs.last()>countSize){
                    showToast("输入的页码超出")
                    return@setOnClickListener
                }


                listener?.onClick(realPageIndexs)
                dismiss()
        }

        return this
    }

    private fun showToast(str: String) {
        SToast.showText(if (screenPos == 3) 2 else screenPos, str)
    }

    fun dismiss() {
        if (dialog != null)
            dialog?.dismiss()
    }

    fun show() {
        if (dialog != null)
            dialog?.show()
    }

    private var listener: OnDialogClickListener? = null

    fun interface OnDialogClickListener {
        fun onClick(pages:List<Int>)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }


    class MyAdapter(layoutResId: Int, list: List<ItemList>) : BaseQuickAdapter<ItemList, BaseViewHolder>(layoutResId, list) {

        override fun convert(helper: BaseViewHolder, item: ItemList) {
            helper.setVisible(R.id.et_name, !item.isAdd)
            helper.setVisible(R.id.iv_add, item.isAdd)
            val etName = helper.getView<EditText>(R.id.et_name)
            etName.setText(if (item.page != 0) item.page.toString() else "")

            etName.doAfterTextChanged {
                val str = it.toString()
                if (str.isNotEmpty()){
                    data[helper.adapterPosition].page=str.toInt()
                }
            }

            if (helper.adapterPosition == data.size - 2) {
                etName.requestFocus()
                etName.isFocusableInTouchMode = true
            }

        }

    }

}
package com.bll.lnkstudy.dialog

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.ItemList
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommitInfoItem
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.SToast
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * type 1作业本 2作业卷
 * pageStart 为课辅作业的 页码开始值
 *
 */
class DrawingCommitDialog(val context: Context, val screenPos: Int, private val pageStart:Int, private val countSize:Int,var items:MutableList<ItemList>) {

    private var dialog: Dialog? = null
    private var pages = mutableListOf<Int>()
    private var messageId=0
    private var messageTitle=""
    private var postion=0
    private var isCorrect=false

    fun builder(): DrawingCommitDialog {

        dialog = Dialog(context)
        dialog?.setContentView(R.layout.dialog_drawing_commit)
        dialog?.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val window = dialog?.window
        val layoutParams = window?.attributes
        if (screenPos == 3) {
            layoutParams?.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            layoutParams?.x = (Constants.WIDTH - DP2PX.dip2px(context, 480f)) / 2
        }
        dialog?.show()

        dialog?.setOnDismissListener {
            KeyboardUtils.hideSoftKeyboard(context)
        }

        val tv_selector = dialog?.findViewById<TextView>(R.id.tv_selector)
        tv_selector?.setOnClickListener {
            if (items.size>1){
                HomeworkMessageSelectorDialog(context, screenPos,items).builder()
                    ?.setOnDialogClickListener {postion,it->
                        this.postion=postion
                        messageId = it.id
                        messageTitle=it.name
                        isCorrect=it.isSelfCorrect
                        tv_selector.text=messageTitle
                    }
            }
        }
        if (items.size==1){
            messageId = items[0].id
            messageTitle=items[0].name
            isCorrect=items[0].isSelfCorrect
            tv_selector?.text=messageTitle
        }

        val et_page1 = dialog?.findViewById<EditText>(R.id.et_page1)
        val et_page2 = dialog?.findViewById<EditText>(R.id.et_page2)

        val list= mutableListOf<ItemList>()
        val pageItem1= ItemList()
        pageItem1.isAdd=false
        list.add(pageItem1)

        val pageItem2= ItemList()
        pageItem2.isAdd=true
        list.add(pageItem2)

        val rvList = dialog?.findViewById<RecyclerView>(R.id.rv_list)
        val mAdapter = MyAdapter(R.layout.item_drawing_commit_page, list)
        rvList?.layoutManager = GridLayoutManager(context,4)
        rvList?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        rvList?.addItemDecoration(SpaceGridItemDeco(4,10))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (list[position].isAdd){
                val item= ItemList()
                item.isAdd=false
                list.add(list.size-1,item)
                mAdapter.setNewData(list)
            }
        }

        val tv_cancel = dialog?.findViewById<TextView>(R.id.tv_cancel)
        tv_cancel?.setOnClickListener {
            dismiss()
        }

        val tv_ok = dialog?.findViewById<TextView>(R.id.tv_ok)
        tv_ok?.setOnClickListener {
            if (messageId >0) {
                pages.clear()
                val page1 = et_page1?.text.toString()
                val page2 = et_page2?.text.toString()
                if (page1.isNotEmpty() && page2.isNotEmpty() && page2.toInt() > page1.toInt()) {
                    for (i in page1.toInt()..page2.toInt()) {
                        if (!pages.contains(i))
                            pages.add(i)
                    }
                }

                for (item in list){
                    if (item.page>0){
                        if (!pages.contains(item.page))
                            pages.add(item.page)
                    }
                }
                pages.sort()

                if (pages.size==0){
                    SToast.showText(if (screenPos == 3) 2 else screenPos, R.string.toast_homework_page)
                    return@setOnClickListener
                }

                val realPageIndexs= mutableListOf<Int>()
                for (page in pages){
                    realPageIndexs.add(page+pageStart-1)
                }

                if (realPageIndexs.last()>=countSize){
                    SToast.showText(if (screenPos == 3) 2 else screenPos, "输入的页码超出")
                    return@setOnClickListener
                }

                val item = HomeworkCommitInfoItem()
                item.index=postion
                item.messageId = messageId
                item.title = messageTitle
                item.contents = realPageIndexs
                item.isSelfCorrect=isCorrect
                listener?.onClick(item)
                dismiss()
            } else {
                SToast.showText(if (screenPos == 3) 2 else screenPos, R.string.toast_homework_selector)
            }

        }

        return this
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
        fun onClick(item: HomeworkCommitInfoItem)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }


    class MyAdapter(layoutResId: Int, data: List<ItemList>) : BaseQuickAdapter<ItemList, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: ItemList) {
            helper.setVisible(R.id.et_name,!item.isAdd)
            helper.setVisible(R.id.iv_add,item.isAdd)
            val etName=helper.getView<EditText>(R.id.et_name)
            val textWatcher=object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
                override fun afterTextChanged(p0: Editable?) {
                    val str=p0.toString()
                    if (str.isNotEmpty())
                        item.page=str.toInt()
                }
            }
            etName.setOnFocusChangeListener{_,hasFocus->
                if (hasFocus){
                    etName.addTextChangedListener(textWatcher)
                }else{
                    etName.removeTextChangedListener(textWatcher)
                }
            }

        }

    }

}
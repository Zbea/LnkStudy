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
import com.bll.lnkstudy.mvp.model.homework.HomeworkCommit
import com.bll.lnkstudy.mvp.model.homework.HomeworkMessage
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.KeyboardUtils
import com.bll.lnkstudy.utils.SToast
import com.bll.lnkstudy.widget.SpaceGridItemDeco
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class DrawingCommitDialog(val context: Context, val screenPos: Int, val messages: List<HomeworkMessage.MessageBean>) {

    private var dialog: Dialog? = null
    private var pages = mutableListOf<Int>()
    private var homeworkMessage: HomeworkMessage.MessageBean? = null

    fun builder(): DrawingCommitDialog? {

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
            HomeworkMessageSelectorDialog(context, screenPos, messages).builder()
                ?.setOnDialogClickListener {
                    homeworkMessage = it
                    tv_selector.text=homeworkMessage?.title
                }
        }
        val et_title = dialog?.findViewById<EditText>(R.id.et_title)
        val et_page1 = dialog?.findViewById<EditText>(R.id.et_page1)
        val et_page2 = dialog?.findViewById<EditText>(R.id.et_page2)

        val list= mutableListOf<HomeworkCommit>()
        val homeworkCommit= HomeworkCommit()
        homeworkCommit.isAdd=false
        list.add(homeworkCommit)

        val homeworkCommit1= HomeworkCommit()
        homeworkCommit1.isAdd=true
        list.add(homeworkCommit1)

        val rvList = dialog?.findViewById<RecyclerView>(R.id.rv_list)
        val mAdapter = MyAdapter(R.layout.item_drawing_commit_page, list)
        rvList?.layoutManager = GridLayoutManager(context,4)
        rvList?.adapter = mAdapter
        mAdapter.bindToRecyclerView(rvList)
        rvList?.addItemDecoration(SpaceGridItemDeco(4,10))
        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (list[position].isAdd){
                val homeworkCommit= HomeworkCommit()
                homeworkCommit.isAdd=false
                list.add(list.size-1,homeworkCommit)
                mAdapter.setNewData(list)
            }
        }

        val tv_cancel = dialog?.findViewById<TextView>(R.id.tv_cancel)
        tv_cancel?.setOnClickListener {
            dismiss()
        }

        val tv_ok = dialog?.findViewById<TextView>(R.id.tv_ok)
        tv_ok?.setOnClickListener {
            if (homeworkMessage != null) {
                val page1 = et_page1?.text.toString()
                val page2 = et_page2?.text.toString()
                if (page1.isNotEmpty() && page2.isNotEmpty() && page2.toInt() > page1.toInt()) {
                    for (i in page1.toInt()..page2.toInt()) {
                        if (!pages.contains(i))
                            pages.add(i)
                    }
                }

                val datas=mAdapter?.data
                for (item in datas){
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

                val item = HomeworkCommit()
                item.messageId = homeworkMessage?.studentTaskId!!
                item.title = et_title?.text.toString()
                item.contents = pages
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
        fun onClick(item: HomeworkCommit)
    }

    fun setOnDialogClickListener(listener: OnDialogClickListener) {
        this.listener = listener
    }


    class MyAdapter(layoutResId: Int, data: List<HomeworkCommit>?) : BaseQuickAdapter<HomeworkCommit, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: HomeworkCommit) {
            helper.setVisible(R.id.et_name,!item.isAdd)
            helper.setVisible(R.id.iv_add,item.isAdd)
            val etName=helper.getView<EditText>(R.id.et_name)
            etName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val str=p0.toString()
                    if (str.isNotEmpty())
                        item.page=str.toInt()
                }
                override fun afterTextChanged(p0: Editable?) {
                }
            })
        }

    }

}
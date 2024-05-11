package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.note.Note
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NotebookAdapter(private var type:Int,layoutResId: Int, data: List<Note>?) : BaseQuickAdapter<Note, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Note) {

        helper.apply {
            if (type==0){
                setGone(R.id.iv_password,false)
                setGone(R.id.iv_delete,false)
                setGone(R.id.iv_edit,false)
            }
            setText(R.id.tv_title,item.title)
            setText(R.id.tv_date, DateUtils.longToStringDataNoYear(item.date) )
            setVisible(R.id.iv_password,item.typeStr==mContext.getString(R.string.note_tab_diary))
            if (MethodManager.getPrivacyPassword(1)!=null)
                setImageResource(R.id.iv_password,if (item.isCancelPassword) R.mipmap.icon_encrypt_cancel else R.mipmap.icon_encrypt_check)

            addOnClickListener(R.id.iv_password,R.id.iv_delete,R.id.iv_edit)
        }
    }

}

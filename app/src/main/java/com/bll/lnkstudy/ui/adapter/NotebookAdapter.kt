package com.bll.lnkstudy.ui.adapter

import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.mvp.model.Note
import com.bll.lnkstudy.utils.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class NotebookAdapter(private var type:Int,layoutResId: Int, data: List<Note>?) : BaseQuickAdapter<Note, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: Note) {

        helper.apply {
            var title=""
            if (type==0){
                title=item.title
                setGone(R.id.iv_password,false)
                setGone(R.id.iv_delete,false)
                setGone(R.id.iv_edit,false)
            }
            else{
                if (item.isCloud){
                    if (DataBeanManager.grades.size>0){
                        title =  "(${DataBeanManager.grades[item.grade-1].desc})${item.title}"
                    }
                } else{
                    title = item.title
                }
            }
            setText(R.id.tv_title,title)
            setText(R.id.tv_date, DateUtils.longToStringDataNoYear(item.date) )
            setGone(R.id.iv_password,item.isSet&&item.typeStr==mContext.getString(R.string.note_tab_diary))
            if (item.isSet)
                helper.setImageResource(R.id.iv_password,if (item.isCancelPassword) R.mipmap.icon_encrypt else R.mipmap.icon_encrypt_check)

            addOnClickListener(R.id.iv_password,R.id.iv_delete,R.id.iv_edit)
        }
    }

}

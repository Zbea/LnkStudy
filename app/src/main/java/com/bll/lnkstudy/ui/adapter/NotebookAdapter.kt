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
                setGone(R.id.iv_encrypt,false)
                setGone(R.id.iv_more,false)
            }
            else{
                title = if (item.isCloud){
                    if (item.typeStr==DataBeanManager.noteBook[0].name){
                        "(${item.grade})${item.title}"

                    } else{
                        "(${DataBeanManager.grades[item.grade-1].desc})${item.title}"
                    }
                } else{
                    item.title
                }
                setGone(R.id.iv_encrypt,item.typeStr==DataBeanManager.noteBook[0].name)
                setImageResource(R.id.iv_encrypt,if (item.isEncrypt) R.mipmap.icon_encrypt_check else R.mipmap.icon_encrypt)
            }
            setText(R.id.tv_title,title)
            setText(R.id.tv_date, DateUtils.longToStringDataNoYear(item.date) )

            addOnClickListener(R.id.iv_encrypt)
            addOnClickListener(R.id.iv_more)
        }
    }

}

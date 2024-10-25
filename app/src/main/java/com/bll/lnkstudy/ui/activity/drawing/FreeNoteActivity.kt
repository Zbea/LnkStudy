package com.bll.lnkstudy.ui.activity.drawing


import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CatalogFreeNoteDialog
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.ModuleAddDialog
import com.bll.lnkstudy.manager.FreeNoteDaoManager
import com.bll.lnkstudy.mvp.model.FreeNoteBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_freenote.tv_delete
import kotlinx.android.synthetic.main.ac_freenote.tv_name
import kotlinx.android.synthetic.main.ac_freenote.tv_save
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_draft
import kotlinx.android.synthetic.main.common_drawing_tool.iv_expand
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total
import java.io.File

class FreeNoteActivity : BaseDrawingActivity() {

    private var bgRes = ""
    private var freeNoteBean: FreeNoteBean? = null
    private var posImage = 0
    private var images = mutableListOf<String>()//手写地址
    private var bgResList = mutableListOf<String>()//背景地址

    override fun layoutId(): Int {
        return R.layout.ac_freenote
    }

    override fun initData() {
        bgRes=ToolUtils.getImageResStr(this,R.mipmap.icon_freenote_bg_1)
        freeNoteBean=FreeNoteDaoManager.getInstance().queryBean()
        freeNoteBean?.title=DateUtils.longToStringNoYear(System.currentTimeMillis())
        if (freeNoteBean==null){
            createFreeNote()
        }
        posImage=freeNoteBean?.page!!
    }

    override fun initView() {
        disMissView(iv_btn,iv_draft)
        iv_expand.setImageResource(R.mipmap.icon_draw_change)

        tv_save.setOnClickListener {
            freeNoteBean?.isSave=true
            saveFreeNote()
            posImage=0
            createFreeNote()
            setChangeContent()
        }

        tv_name.setOnClickListener {
            InputContentDialog(this, tv_name.text.toString()).builder().setOnDialogClickListener {
                tv_name.text = it
                freeNoteBean?.title = it
            }
        }

        iv_expand.setOnClickListener {
            ModuleAddDialog(this, getCurrentScreenPos(), getString(R.string.freenote_module_str), DataBeanManager.freenoteModules).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    v_content_b?.setBackgroundResource(moduleBean.resContentId)
                    bgResList[posImage] = ToolUtils.getImageResStr(this, moduleBean.resContentId)
                }
        }

        tv_delete.setOnClickListener {
            CommonDialog(this).setContent("确定删除当前随笔？").builder().setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                override fun cancel() {
                }
                override fun ok() {
                    FreeNoteDaoManager.getInstance().deleteBean(freeNoteBean)
                    FileUtils.deleteFile(File(FileAddress().getPathFreeNote(DateUtils.longToString(freeNoteBean?.date!!))))
                    if (freeNoteBean?.isSave==true){
                        freeNoteBean=FreeNoteDaoManager.getInstance().queryBean()
                        posImage=freeNoteBean?.page!!
                    }
                    else{
                        posImage=0
                        createFreeNote()
                    }
                    showView(tv_save)
                    setChangeContent()
                }
            })
        }

        setChangeContent()
    }

    override fun onCatalog() {
        CatalogFreeNoteDialog(this,freeNoteBean!!.date).builder().setOnItemClickListener {
            saveFreeNote()
            freeNoteBean=it
            posImage=freeNoteBean?.page!!
            if (freeNoteBean?.isSave==true){
                disMissView(tv_save)
            }
            else{
                showView(tv_save)
            }
            setChangeContent()
        }
    }

    /**
     * 创建新随笔
     */
    private fun createFreeNote(){
        freeNoteBean= FreeNoteBean()
        freeNoteBean?.date=System.currentTimeMillis()
        freeNoteBean?.title=DateUtils.longToStringNoYear(freeNoteBean?.date!!)
        freeNoteBean?.bgRes= arrayListOf(bgRes)
        freeNoteBean?.paths= mutableListOf(getPath(posImage))
        FreeNoteDaoManager.getInstance().insertOrReplace(freeNoteBean)
    }

    private fun setChangeContent(){
        bgResList= freeNoteBean?.bgRes as MutableList<String>
        //兼容以前版本
        images = if (freeNoteBean?.paths.isNullOrEmpty()){
            mutableListOf(getPath(0))
        } else{
            freeNoteBean?.paths as MutableList<String>
        }
        tv_name.text=freeNoteBean?.title
        onContent()
    }

    override fun onPageDown() {
        if (posImage<images.size-1){
            posImage+=1
            onContent()
        }
        else{
            if (isDrawLastContent()){
                images.add(getPath(images.size))
                bgResList.add(bgRes)
                posImage=images.size-1
                onContent()
            }
        }
    }

    override fun onPageUp() {
        if (posImage > 0) {
            posImage -= 1
            onContent()
        }
    }

    override fun onContent() {
        v_content_b?.setBackgroundResource(ToolUtils.getImageResId(this, bgResList[posImage]))
        tv_page.text = "${posImage + 1}"
        tv_page_total.text="${images.size}"
        elik_b?.setLoadFilePath(getPath(posImage), true)
    }

    /**
     * 最后一个是否已写
     */
    private fun isDrawLastContent():Boolean{
        val path = images.last()
        return File(path).exists()
    }

    private fun getPath(index:Int):String{
        return FileAddress().getPathFreeNote(DateUtils.longToString(freeNoteBean?.date!!)) + "/${index + 1}.png"
    }

    private fun saveFreeNote() {
        freeNoteBean?.paths = images
        freeNoteBean?.bgRes = bgResList
        freeNoteBean?.page=posImage
        FreeNoteDaoManager.getInstance().insertOrReplace(freeNoteBean)
    }

    override fun onPause() {
        super.onPause()
        saveFreeNote()
    }
}

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
        freeNoteBean=FreeNoteDaoManager.getInstance().queryBean()
        freeNoteBean?.title=DateUtils.longToStringNoYear(System.currentTimeMillis())
        if (freeNoteBean==null){
            createFreeNote()
        }
        posImage=freeNoteBean?.page!!
    }

    override fun initView() {
        disMissView(iv_expand,iv_draft)
        iv_btn.setImageResource(R.mipmap.icon_draw_change)

        tv_save.setOnClickListener {
            freeNoteBean?.isSave=true
            saveFreeNote()
            createFreeNote()
            posImage=0
            initFreeNote()
            setContentImage()
        }

        tv_name.setOnClickListener {
            InputContentDialog(this, tv_name.text.toString()).builder().setOnDialogClickListener {
                tv_name.text = it
                freeNoteBean?.title = it
            }
        }

        iv_btn.setOnClickListener {
            ModuleAddDialog(this, getCurrentScreenPos(), getString(R.string.freenote_module_str), DataBeanManager.freenoteModules).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    bgRes = ToolUtils.getImageResStr(this, moduleBean.resContentId)
                    v_content_b?.setBackgroundResource(moduleBean.resContentId)
                    bgResList[posImage] = bgRes
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
                        createFreeNote()
                        posImage=0
                    }
                    showView(tv_save)
                    initFreeNote()
                    setContentImage()
                }
            })
        }

        initFreeNote()
        setContentImage()
    }

    override fun onCatalog() {
        CatalogFreeNoteDialog(this,freeNoteBean!!.date).builder().setOnItemClickListener {
            saveFreeNote()
            freeNoteBean=it
            posImage=freeNoteBean?.page!!
            initFreeNote()
            if (freeNoteBean?.isSave==true){
                disMissView(tv_save)
            }
            else{
                showView(tv_save)
            }
            setContentImage()
        }
    }

    override fun onPageDown() {
        posImage += 1
        if (posImage >= bgResList.size) {
            bgRes = ToolUtils.getImageResStr(this, R.mipmap.icon_freenote_bg_1)
            bgResList.add(bgRes)
        }
        setContentImage()
    }

    override fun onPageUp() {
        if (posImage > 0) {
            posImage -= 1
            setContentImage()
        }
    }

    private fun initFreeNote(){
        bgResList= freeNoteBean?.bgRes as MutableList<String>
        if (!freeNoteBean?.paths.isNullOrEmpty()) {
            images= freeNoteBean?.paths as MutableList<String>
        }
        else{
            images.clear()
        }
        tv_name.text=freeNoteBean?.title
    }

    /**
     * 创建新随笔
     */
    private fun createFreeNote(){
        bgRes= ToolUtils.getImageResStr(this,R.mipmap.icon_freenote_bg_1)
        freeNoteBean= FreeNoteBean()
        freeNoteBean?.date=System.currentTimeMillis()
        freeNoteBean?.title=DateUtils.longToStringNoYear(freeNoteBean?.date!!)
        freeNoteBean?.bgRes= arrayListOf(bgRes)
        FreeNoteDaoManager.getInstance().insertOrReplace(freeNoteBean)
    }

    /**
     * 更换内容
     */
    private fun setContentImage() {

        v_content_b?.setBackgroundResource(ToolUtils.getImageResId(this, bgResList[posImage]))
        val path = FileAddress().getPathFreeNote(DateUtils.longToString(freeNoteBean?.date!!)) + "/${posImage + 1}.png"
        //判断路径是否已经创建
        if (!images.contains(path)) {
            images.add(path)
        }
        tv_page.text = "${posImage + 1}"
        tv_page_total.text="${images.size}"

        elik_b?.setLoadFilePath(path, true)
    }

    override fun onElikSava_b() {
        elik_b?.saveBitmap(true) {}
    }

    private fun saveFreeNote() {
        val path=FileAddress().getPathFreeNote(DateUtils.longToString(freeNoteBean?.date!!))
        if (!File(path).list().isNullOrEmpty()){
            freeNoteBean?.paths = images
            freeNoteBean?.bgRes = bgResList
            freeNoteBean?.page=posImage
            FreeNoteDaoManager.getInstance().insertOrReplace(freeNoteBean)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveFreeNote()
    }
}

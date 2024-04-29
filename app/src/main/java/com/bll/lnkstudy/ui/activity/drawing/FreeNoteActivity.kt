package com.bll.lnkstudy.ui.activity.drawing


import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.ModuleAddDialog
import com.bll.lnkstudy.dialog.PopupFreeNoteList
import com.bll.lnkstudy.manager.FreeNoteDaoManager
import com.bll.lnkstudy.mvp.model.FreeNoteBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_freenote.*
import kotlinx.android.synthetic.main.common_drawing_tool_bottom.*
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
        if (freeNoteBean==null){
            createFreeNote()
        }
        posImage=freeNoteBean?.page!!
    }

    override fun initView() {
        disMissView(iv_catalog,iv_btn)

        iv_save.setOnClickListener {
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

        tv_theme.setOnClickListener {
            ModuleAddDialog(this, getCurrentScreenPos(), getString(R.string.freenote_module_str), DataBeanManager.noteModuleBook).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    bgRes = ToolUtils.getImageResStr(this, moduleBean.resFreeNoteBg)
                    v_content_b.setImageResource(ToolUtils.getImageResId(this, bgRes))
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
                    showView(iv_save)
                    initFreeNote()
                    setContentImage()
                }
            })
        }

        tv_free_list.setOnClickListener {
            PopupFreeNoteList(this, tv_free_list,freeNoteBean!!.date).builder().setOnSelectListener {
                saveFreeNote()
                freeNoteBean=it
                posImage=freeNoteBean?.page!!
                initFreeNote()
                if (freeNoteBean?.isSave==true){
                    disMissView(iv_save)
                }
                else{
                    showView(iv_save)
                }
                setContentImage()
            }
        }

        initFreeNote()
        setContentImage()
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
        images= freeNoteBean?.paths as MutableList<String>
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
        freeNoteBean?.paths= arrayListOf()
        FreeNoteDaoManager.getInstance().insertOrReplace(freeNoteBean)
    }

    /**
     * 更换内容
     */
    private fun setContentImage() {
        v_content_b.setImageResource(ToolUtils.getImageResId(this, bgResList[posImage]))
        val path =
            FileAddress().getPathFreeNote(DateUtils.longToString(freeNoteBean?.date!!)) + "/${posImage + 1}.tch"
        //判断路径是否已经创建
        if (!images.contains(path)) {
            images.add(path)
        }
        tv_page.text = "${posImage + 1}/${images.size}"

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

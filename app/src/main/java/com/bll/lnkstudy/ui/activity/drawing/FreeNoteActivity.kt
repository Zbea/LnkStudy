package com.bll.lnkstudy.ui.activity.drawing


import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.ModuleAddDialog
import com.bll.lnkstudy.dialog.PopupFreeNoteList
import com.bll.lnkstudy.manager.FreeNoteDaoManager
import com.bll.lnkstudy.mvp.model.FreeNoteBean
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_freenote.*
import kotlinx.android.synthetic.main.common_drawing_bottom_one.*
import java.io.File

class FreeNoteActivity : BaseDrawingActivity() {

    private var bgRes = ""
    private var freeNoteBean: FreeNoteBean? = null
    private var posImage = 0
    private var images = mutableListOf<String>()//手写地址
    private var bgResList = mutableListOf<String>()//背景地址
    private var freeNotePopWindow: PopupFreeNoteList? = null

    override fun layoutId(): Int {
        return R.layout.ac_freenote
    }

    override fun initData() {
        bgRes = ToolUtils.getImageResStr(this, R.mipmap.icon_freenote_bg_1)
        freeNoteBean = FreeNoteBean()
        freeNoteBean?.date = System.currentTimeMillis()
        freeNoteBean?.title = DateUtils.longToStringNoYear(freeNoteBean?.date!!)

    }

    override fun initView() {
        disMissView(iv_catalog,iv_btn)
        setPageTitle(R.string.freenote_title_str)
        tv_name.text = freeNoteBean?.title
        elik_b=v_content_b.pwInterFace

        tv_name.setOnClickListener {
            InputContentDialog(this, tv_name.text.toString()).builder()?.setOnDialogClickListener {
                tv_name.text = it
                freeNoteBean?.title = it
            }
        }

        tv_theme.setOnClickListener {
            ModuleAddDialog(this, screenPos, getString(R.string.freenote_module_str), DataBeanManager.noteModuleBook).builder()
                ?.setOnDialogClickListener { moduleBean ->
                    bgRes = ToolUtils.getImageResStr(this, moduleBean.resFreeNoteBg)
                    v_content_b.setImageResource(ToolUtils.getImageResId(this, bgRes))
                    bgResList[posImage] = bgRes
                }
        }

        tv_free_list.setOnClickListener {
            if (freeNotePopWindow == null) {
                freeNotePopWindow = PopupFreeNoteList(this, tv_free_list).builder()
                freeNotePopWindow?.setOnSelectListener {
                    saveFreeNote()
                    posImage = 0
                    freeNoteBean = it
                    bgResList = freeNoteBean?.bgRes as MutableList<String>
                    images = freeNoteBean?.paths as MutableList<String>
                    tv_name.text = freeNoteBean?.title
                    setContentImage()
                }
            } else {
                freeNotePopWindow?.show()
            }
        }


        if (posImage >= bgResList.size) {
            bgResList.add(bgRes)
        }
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
            FreeNoteDaoManager.getInstance().insertOrReplace(freeNoteBean)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveFreeNote()
    }
}

package com.bll.lnkstudy.ui.activity.drawing


import PopupFreeNoteRecordList
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import android.media.MediaRecorder
import android.view.EinkPWInterface
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.dialog.ModuleAddDialog
import com.bll.lnkstudy.dialog.PopupClick
import com.bll.lnkstudy.dialog.PopupFreeNoteList
import com.bll.lnkstudy.manager.*
import com.bll.lnkstudy.mvp.model.*
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_freenote.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.IOException

class FreeNoteActivity : BaseDrawingActivity() {

    private var isRecord = false
    private var recordBean: RecordBean? = null
    private var mRecorder: MediaRecorder? = null
    private var recordPath: String? = null
    private var bgRes = ""
    private var freeNoteBean: FreeNoteBean? = null
    private var posImage = 0
    private var images = mutableListOf<String>()//手写地址
    private var bgResList = mutableListOf<String>()//背景地址
    private var freeNotePopWindow: PopupFreeNoteList? = null
    private var popsNote = mutableListOf<PopupBean>()
    private var notebooks = mutableListOf<Notebook>()

    override fun layoutId(): Int {
        return R.layout.ac_freenote
    }

    override fun initData() {
        bgRes = ToolUtils.getImageResStr(this, R.mipmap.icon_freenote_bg_1)
        freeNoteBean = FreeNoteBean()
        freeNoteBean?.date = System.currentTimeMillis()
        freeNoteBean?.title = DateUtils.longToStringNoYear(freeNoteBean?.date!!)

        notebooks.addAll(NotebookDaoManager.getInstance().queryAll())

        for (i in notebooks.indices) {
            popsNote.add(PopupBean(i, notebooks[i].name))
        }
    }

    override fun initView() {
        elik_b = v_content_b.pwInterFace
        tv_name.text = freeNoteBean?.title

        tv_name.setOnClickListener {
            InputContentDialog(this, tv_name.text.toString()).builder()?.setOnDialogClickListener {
                tv_name.text = it
                freeNoteBean?.title = it
            }
        }

        iv_record.setOnClickListener {
            isRecord = !isRecord
            if (isRecord) {
                startRecord()
            } else {
                stopRecord()
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

        tv_insert.setOnClickListener {
            insertNote()
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

        tv_record_list.setOnClickListener {
            PopupFreeNoteRecordList(this, tv_record_list).builder()
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
        elik_b?.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik_b?.saveBitmap(true) {}
            }
        })
    }

    /**
     * 插入笔记
     */
    private fun insertNote() {
        if (popsNote.size == 0) {
            showToast(R.string.toast_freenote_insert_fail)
            return
        }
        PopupClick(this, popsNote, tv_insert, 10).builder().setOnSelectListener {
            val note = Note()
            note.title = freeNoteBean?.title
            note.date = System.currentTimeMillis()
            note.typeStr = it.name
            note.contentResId = ToolUtils.getImageResStr(this, 0)
            NoteDaoManager.getInstance().insertOrReplace(note)
            for (i in images.indices) {
                val oldPath = images[i]
                if (File(oldPath).exists()) {
                    val date = System.currentTimeMillis()
                    val pathName = DateUtils.longToString(date)
                    val path = FileAddress().getPathNote(mUser?.grade!!, it.name, note.title, date) + "/${pathName}.tch"
                    FileUtils.copyFile(oldPath, path)
                    val noteContent = NoteContentBean()
                    noteContent.date = date
                    noteContent.typeStr = note.typeStr
                    noteContent.noteTitle = note.title
                    noteContent.resId = note.contentResId
                    noteContent.title = "未命名${i + 1}"
                    noteContent.filePath = path
                    noteContent.page = i
                    NoteContentDaoManager.getInstance().insertOrReplaceNote(noteContent)
                }
            }
            showToast(R.string.toast_freenote_insert_success)
            EventBus.getDefault().post(Constants.NOTE_EVENT)
        }
    }

    /**
     * 开始录音
     */
    private fun startRecord() {
        iv_record.setImageResource(R.mipmap.icon_freenote_recording)
        recordBean = RecordBean()
        recordBean?.date = System.currentTimeMillis()
        recordBean?.title = tv_name.text.toString()

        val path = FileAddress().getPathFreeRecord()
        if (!File(path).exists())
            File(path).mkdir()
        recordPath = File(path, "${DateUtils.longToString(recordBean?.date!!)}.mp3").path

        mRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setOutputFile(recordPath)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
            try {
                prepare()//准备
                start()//开始录音
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 结束录音
     */
    private fun stopRecord() {
        iv_record.setImageResource(R.mipmap.icon_freenote_recorder)
        mRecorder?.apply {
            setOnErrorListener(null)
            setOnInfoListener(null)
            setPreviewDisplay(null)
            stop()
            release()
            mRecorder = null
        }
        recordBean?.path = recordPath
        RecordDaoManager.getInstance().insertOrReplace(recordBean)
        recordBean = null
        recordPath = null
    }

    private fun saveFreeNote() {
        //清空没有手写页面
        val sImages = mutableListOf<String>()
        for (i in images.indices) {
            if (File(images[i]).exists()) {
                sImages.add(images[i])
            }
        }
        freeNoteBean?.paths = images
        freeNoteBean?.bgRes = bgResList
        if (sImages.size > 0)
            FreeNoteDaoManager.getInstance().insertOrReplace(freeNoteBean)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (recordBean != null) {
            stopRecord()
        }
        saveFreeNote()
    }
}

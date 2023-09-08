
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.CommonDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.manager.RecordDaoManager
import com.bll.lnkstudy.mvp.model.RecordBean
import com.bll.lnkstudy.utils.DP2PX
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.KeyboardUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.io.File
import kotlin.math.ceil

class PopupFreeNoteRecordList(var context: Context, var view: View) {

    private var recordBeans= mutableListOf<RecordBean>()
    private var mPopupWindow: PopupWindow? = null
    private var mAdapter: RecordAdapter?=null
    private var pageSize=10
    private var pageIndex=1
    private var pageCount=1
    private var ll_page_number:LinearLayout?=null
    private var tv_page_current:TextView?=null
    private var tv_page_total:TextView?=null
    private var position=0
    private var currentPos=-1
    private var mediaPlayer: MediaPlayer? = null

    fun builder(): PopupFreeNoteRecordList {
        val popView = LayoutInflater.from(context).inflate(R.layout.popup_freenote_record_list, null, false)
        mPopupWindow = PopupWindow(context).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 设置PopupWindow的内容view
            contentView = popView
            isFocusable = true // 设置PopupWindow可获得焦点
            isTouchable = true // 设置PopupWindow可触摸
            isOutsideTouchable = true // 设置非PopupWindow区域可触摸
            width=DP2PX.dip2px(context,280f)
        }

        ll_page_number = popView.findViewById(R.id.ll_page_number)
        tv_page_current = popView.findViewById(R.id.tv_page_current)
        tv_page_total = popView.findViewById(R.id.tv_page_total)

        val btn_page_up = popView.findViewById<TextView>(R.id.btn_page_up)
        val btn_page_down = popView.findViewById<TextView>(R.id.btn_page_down)

        btn_page_up.setOnClickListener {
            if(pageIndex>1){
                pageIndex-=1
                fetchData()
            }
        }

        btn_page_down.setOnClickListener {
            if(pageIndex<pageCount){
                pageIndex+=1
                fetchData()
            }
        }

        val rvList = popView.findViewById<RecyclerView>(R.id.rv_list)
        rvList.layoutManager = LinearLayoutManager(context)//创建布局管理
        mAdapter = RecordAdapter(R.layout.item_freenote, null)
        rvList.adapter=mAdapter
        mAdapter?.bindToRecyclerView(rvList)
        mAdapter?.setEmptyView(R.layout.common_empty)
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            this.position=position
            setPlay()
        }
        mAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val recordBean=recordBeans[position]
            if (view.id==R.id.iv_edit){
                InputContentDialog(context,recordBean.title).builder()?.setOnDialogClickListener { string ->
                    recordBean.title=string
                    mAdapter?.notifyItemChanged(position)
                    RecordDaoManager.getInstance().insertOrReplace(recordBean)
                    KeyboardUtils.hideSoftKeyboard(context)
                }
            }
            if (view.id==R.id.iv_delete){
                CommonDialog(context).setContent(R.string.item_is_delete_tips).builder()
                    .setDialogClickListener(object : CommonDialog.OnDialogClickListener {
                        override fun cancel() {
                        }
                        override fun ok() {
                            mAdapter?.remove(position)
                            RecordDaoManager.getInstance().deleteBean(recordBean)
                            FileUtils.deleteFile(File(recordBean.path))
                        }
                    })
            }
        }

        mPopupWindow?.setOnDismissListener {
            if (currentPos!=-1){
                pause(currentPos)
                release()
            }
        }

        fetchData()

        show()
        return this
    }

    private fun fetchData(){
        recordBeans = RecordDaoManager.getInstance().queryAllRecord(pageIndex, pageSize)
        val total = RecordDaoManager.getInstance().queryAllRecord().size
        pageCount = ceil(total.toDouble() / pageSize).toInt()
        if (total == 0) {
            ll_page_number?.visibility=View.GONE
        } else {
            tv_page_current?.text = pageIndex.toString()
            tv_page_total?.text = pageCount.toString()
            ll_page_number?.visibility=View.VISIBLE
        }
        mAdapter?.setNewData(recordBeans)
    }

    //点击播放
    private fun setPlay(){
        val path=recordBeans[position].path
        if (!File(path).exists())return
        if (currentPos == position) {
            if (mediaPlayer?.isPlaying == true) {
                pause(position)
            } else {
                mediaPlayer?.start()
                recordBeans[position].state=1
                mAdapter?.notifyItemChanged(position)//刷新为播放状态
            }
        } else {
            if (mediaPlayer?.isPlaying == true) {
                pause(currentPos)
            }
            release()
            play(path)
        }
        currentPos = position
    }

    private fun release(){
        if (mediaPlayer!=null){
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    private fun play(path:String){
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(path)
        mediaPlayer?.setOnCompletionListener {
            recordBeans[position].state=0
            mAdapter?.notifyItemChanged(position)//刷新为结束状态
        }
        mediaPlayer?.prepare()
        mediaPlayer?.start()
        recordBeans[position].state=1
        mAdapter?.notifyItemChanged(position)//刷新为播放状态
    }

    private fun pause(pos:Int){
        mediaPlayer?.pause()
        recordBeans[pos].state=0
        mAdapter?.notifyItemChanged(pos)//刷新为结束状态
    }


    fun dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow?.dismiss()
        }
    }

    fun show() {
        if (mPopupWindow != null) {
            mPopupWindow?.showAsDropDown(view, 0, 10, Gravity.RIGHT)
        }
    }

    class RecordAdapter(layoutResId: Int, data: MutableList<RecordBean>?) : BaseQuickAdapter<RecordBean, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder, item: RecordBean) {
            helper.apply {
                setText(R.id.tv_title,item.title)
                setVisible(R.id.iv_record,true)
                setImageResource(R.id.iv_record,if (item.state==0) R.mipmap.icon_record_play else R.mipmap.icon_record_pause)
                addOnClickListener(R.id.iv_edit,R.id.iv_delete)
            }
        }
    }


}
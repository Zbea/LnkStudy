package com.bll.lnkstudy.ui.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.view.EinkPWInterface
import android.view.View
import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.utilssdk.utils.BitmapMergeUtils
import com.bll.utilssdk.utils.BitmapUtils
import com.bll.utilssdk.utils.FileUtils
import kotlinx.android.synthetic.main.ac_testpaper_drawing.*

class TestPaperDrawingActivity : BaseActivity(), View.OnClickListener {
    private var outImageStr = ""
    private var paths = mutableListOf<String>()
    private var commitPaths = mutableListOf<String>()
    private var elik_a: EinkPWInterface? = null
    private var elik_b: EinkPWInterface? = null

    private var pageCount = 1
    private var pageIndex = 1 //当前页码


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_drawing
    }

    override fun initData() {
        outImageStr = intent.getStringExtra("outImageStr").toString()
        paths = intent.getStringArrayListExtra("imagePaths")!!
        pageCount = paths.size
    }

    override fun initView() {
        disMissView(ivBack)

        tv_save.setOnClickListener(this)
        btn_page_up.setOnClickListener(this)
        btn_page_down.setOnClickListener(this)

        elik_a = v_content_a.pwInterFace

        if (pageCount == 1) {
            v_content_b.visibility = View.GONE
            tv_page_a.text = "$pageIndex/$pageCount"
            loadPicture(pageIndex, elik_a!!, v_content_a)
        } else {
            elik_b = v_content_b.pwInterFace
            updateScreen()
        }

    }

    override fun onClick(view: View?) {

        if (view == tv_save) {
            showLoading()
            Handler().postDelayed(Runnable {
                commit()
            },500)

        }

        if (view == btn_page_up) {
            if (pageIndex > 2) {
                pageIndex -= 3
                updateScreen()
            }
        }
        if (view == btn_page_down) {
            if (pageIndex < pageCount) {
                pageIndex += 1
                updateScreen()

            }
        }

    }


    //提交
    private fun commit(){

        for (i in 0 until paths.size) {
            val index = i + 1 //当前名
            val path = paths[i] //当前原图路径
            val drawPath = "$outImageStr/$index/draw.png" //当前绘图路径
            val mergePath = "$outImageStr/$index/"//合并后的路径
            val mergePathStr = "$outImageStr/$index/merge.png"//合并后图片地址

            val oldBitmap = BitmapFactory.decodeFile(path)
            var drawBitmap = if (FileUtils.isExist(drawPath)) {
                BitmapFactory.decodeFile(drawPath)
            } else {
                null
            }
            if (drawBitmap != null) {
                val mergeBitmap = BitmapMergeUtils.mergeBitmap(oldBitmap, drawBitmap)
                BitmapUtils.saveBmpGallery(this, mergeBitmap, mergePath, "merge")
            } else {
                showToast("试卷$index 未做")
                BitmapUtils.saveBmpGallery(this, oldBitmap, mergePath, "merge")
            }
            commitPaths.add(mergePathStr)
        }

        hideLoading()
        finish()
    }

    private fun updateScreen() {
        if (pageIndex < 1) {
            //当处于第一页
            pageIndex = 1
            tv_page_a.text = "$pageIndex/$pageCount"
            loadPicture(pageIndex, elik_a!!, v_content_a)

            pageIndex += 1//第二屏页码加一
            tv_page_b.text = "$pageIndex/$pageCount"
            loadPicture(pageIndex, elik_b!!, v_content_b)
        } else if (pageIndex > 0 && pageIndex + 1 <= pageCount) {
            tv_page_a.text = "$pageIndex/$pageCount"
            loadPicture(pageIndex, elik_a!!, v_content_a)

            pageIndex += 1//第二屏页码加一
            tv_page_b.text = "$pageIndex/$pageCount"
            loadPicture(pageIndex, elik_b!!, v_content_b)
        } else {
            //当翻页后处于倒数一页
            pageIndex = pageCount - 1
            tv_page_a.text = "$pageIndex/$pageCount"
            loadPicture(pageIndex, elik_a!!, v_content_a)

            pageIndex = pageCount
            tv_page_a.text = "$pageIndex/$pageCount"
            loadPicture(pageIndex, elik_b!!, v_content_b)
        }

    }

    //加载图片
    private fun loadPicture(index: Int, elik: EinkPWInterface, view: ImageView) {

        GlideUtils.setImageNoCacheUrl(this,paths[index - 1],view)

        val drawPath = "$outImageStr/$index/draw.tch"
        elik?.setLoadFilePath(drawPath, true)
        elik?.setDrawEventListener(object : EinkPWInterface.PWDrawEvent {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean) {
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: ArrayList<Point>?) {
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
                elik?.saveBitmap(true) {}
            }

        })
    }

    //屏蔽返回键
    override fun onBackPressed() {
//        super.onBackPressed()
    }


}
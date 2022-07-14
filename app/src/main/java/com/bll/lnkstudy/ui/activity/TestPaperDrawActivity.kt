package com.bll.lnkstudy.ui.activity

import android.graphics.BitmapFactory
import android.os.Handler
import android.view.EinkPWInterface
import android.view.PWDrawObjectHandler
import android.view.View
import android.widget.ImageView
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.utilssdk.utils.BitmapMergeUtils
import com.bll.utilssdk.utils.BitmapUtils
import com.bll.utilssdk.utils.FileUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.ac_testpaper_draw.*

class TestPaperDrawActivity : BaseActivity(), View.OnClickListener {
    private var outImageStr = ""
    private var paths = mutableListOf<String>()
    private var commitPaths = mutableListOf<String>()
    private var elik_a: EinkPWInterface? = null
    private var elik_b: EinkPWInterface? = null

    private var pageCount = 1
    private var pageIndex = 1 //当前页码


    override fun layoutId(): Int {
        return R.layout.ac_testpaper_draw
    }

    override fun initData() {
        outImageStr = intent.getStringExtra("outImageStr").toString()
        paths = intent.getStringArrayListExtra("imagePaths")!!
        pageCount = paths.size
    }

    override fun initView() {
        disMissView(ivBack)

        tv_save.setOnClickListener(this)
        iv_fine.setOnClickListener(this)
        iv_thick.setOnClickListener(this)
        iv_clear.setOnClickListener(this)
        iv_clear_all.setOnClickListener(this)
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

            saveElikNote()//保存
            showLoading()
            Handler().postDelayed(Runnable {
                commit()
            },1000)

        }
        if (view == iv_fine) {
            if (elik_a?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                elik_a?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            }
            elik_a?.penSettingWidth = 2

            if (elik_b?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            }
            elik_b?.penSettingWidth = 2
        }
        if (view == iv_thick) {
            if (elik_a?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                elik_a?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            }
            elik_a?.penSettingWidth = 6

            if (elik_b?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            }
            elik_b?.penSettingWidth = 6
        }
        if (view == iv_clear) {
            elik_a?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
            elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_CHOICERASE
        }
        if (view == iv_clear_all) {
            elik_a?.clearContent(null, true, true)
            elik_a?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN

            elik_b?.clearContent(null, true, true)
            elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
        }
        if (view == btn_page_up) {
            if (pageIndex > 2) {
                pageIndex -= 3
                saveElikNote()//翻页保存涂写
                updateScreen()
            }
        }
        if (view == btn_page_down) {
            if (pageIndex < pageCount) {
                pageIndex += 1
                saveElikNote()//翻页保存涂写
                updateScreen()

            }
        }

    }

    //保存涂写
    private fun saveElikNote() {
        elik_a?.saveBitmap(true) {
        }
        if (pageCount > 1) {
            elik_b?.saveBitmap(true) {
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

        for (str in commitPaths){
            showLog("merge:$str")
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
        Glide.with(this)
            .load(paths[index - 1])
            .skipMemoryCache(true)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(view)

        val drawPath = "$outImageStr/$index/draw.tch"
        elik?.setLoadFilePath(drawPath, true)
    }


}
package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.view.Gravity
import android.view.PWDrawObjectHandler
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.DateUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.ac_drawing_draft.iv_clear
import kotlinx.android.synthetic.main.ac_drawing_webview.tv_commit

/**
 * 草稿纸
 */
class WebViewDrawingActivity:BaseDrawingActivity(), IContractView.IFileUploadView{

    private var mUploadPresenter= FileUploadPresenter(this,getCurrentScreenPos())
    private var path=""

    override fun onToken(token: String) {
        val paths= mutableListOf(path)
        FileImageUploadManager(token, paths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    hideLoading()
                    FileUtils.delete(path)
                    val intent= Intent()
                    intent.putExtra("path", ToolUtils.getImagesStr(urls))
                    setResult(10001,intent )
                    finish()
                }
                override fun onUploadFail() {
                    hideLoading()
                    showToast(R.string.upload_fail)
                }
            })
        }
    }

    override fun layoutId(): Int {
        return R.layout.ac_drawing_webview
    }

    override fun initData() {
        path=FileAddress().getPathWebView(DateUtils.longToString(System.currentTimeMillis()))
    }

    override fun initView() {
        val layoutParams=window?.attributes
        layoutParams?.gravity = Gravity.BOTTOM
        window?.attributes = layoutParams

        iv_clear.setOnClickListener {
            elik_b ?.clearContent(null,true,true)
            if (elik_b?.drawObjectType != PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN) {
                elik_b?.drawObjectType = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN
            }
        }

        tv_commit.setOnClickListener {
            if (FileUtils.isExist(path)){
                showLoading()
                mUploadPresenter.getToken()
            }
        }

        elik_b?.setLoadFilePath(path, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        FileUtils.delete(path)
    }

}
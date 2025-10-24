package com.bll.lnkstudy.ui.activity

import android.content.Intent
import android.os.Handler
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseDrawingActivity
import com.bll.lnkstudy.mvp.presenter.FileUploadPresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.ui.activity.drawing.DraftDrawingActivity
import com.bll.lnkstudy.utils.BitmapUtils
import com.bll.lnkstudy.utils.FileImageUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.ToolUtils
import kotlinx.android.synthetic.main.common_title.tv_setting
import kotlinx.android.synthetic.main.common_title.tv_setting_1

/**
 * 草稿纸
 */
class WebViewDrawingActivity: BaseDrawingActivity(), IContractView.IFileUploadView{

    private var mUploadPresenter= FileUploadPresenter(this,getCurrentScreenPos())
    private var idStr=""
    private var url=""
    private var path=""
    private var pathDraw=""
    private var pathMerge=""

    override fun onToken(token: String) {
        val paths= mutableListOf(pathDraw,pathMerge)
        FileImageUploadManager(token, paths).apply {
            startUpload()
            setCallBack(object : FileImageUploadManager.UploadCallBack {
                override fun onUploadSuccess(urls: List<String>) {
                    hideLoading()
                    FileUtils.delete(path)
                    val intent= Intent()
                    intent.putExtra("path", ToolUtils.getImagesStr(urls))
                    intent.putExtra("id", idStr)
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
        idStr= intent.getStringExtra("webViewInfoId").toString()
        url=intent.getStringExtra("webViewInfoUrl").toString()

        path=FileAddress().getPathWebView(idStr)
        pathDraw= "$path/draw.png"
        pathMerge= "$path/marge.png"
    }

    override fun initView() {
        showView(tv_setting,tv_setting_1)
        tv_setting_1.text="草稿纸"
        tv_setting.text="提交"

        tv_setting_1.setOnClickListener {
            customStartActivity(Intent(this, DraftDrawingActivity::class.java))
        }

        tv_setting.setOnClickListener {
            if (FileUtils.isExistContent(path)){
                showLoading()
                Handler().postDelayed({
                    if (bitmapBatchSaver.isAccomplished){
                        mUploadPresenter.getToken()
                    }
                    else{
                        hideLoading()
                        showToast("未保存，请稍后提交")
                    }
                },500)
            }
            else{
                showToast("未写答案")
            }
        }

        GlideUtils.setImageUrl(this,url,v_content_b)
        elik_b?.setLoadFilePath(pathDraw, true)
    }

    override fun onElikSava_b() {
        bitmapBatchSaver.submitBitmap(BitmapUtils.loadBitmapFromViewByCanvas(v_content_b),pathMerge,null)
    }

}
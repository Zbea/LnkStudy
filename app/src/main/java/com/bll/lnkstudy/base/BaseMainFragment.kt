package com.bll.lnkstudy.base

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MyApplication
import com.bll.lnkstudy.dialog.AppUpdateDialog
import com.bll.lnkstudy.mvp.model.AppUpdateBean
import com.bll.lnkstudy.mvp.model.DataUpdateBean
import com.bll.lnkstudy.mvp.presenter.CloudUploadPresenter
import com.bll.lnkstudy.mvp.presenter.DataUpdatePresenter
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.utils.AppUtils
import com.bll.lnkstudy.utils.DeviceUtil
import com.bll.lnkstudy.utils.FileDownManager
import com.bll.lnkstudy.utils.NetworkUtil
import com.bll.lnkstudy.utils.ToolUtils
import com.google.gson.Gson
import com.htfy.params.ServerParams
import com.liulishuo.filedownloader.BaseDownloadTask
import org.json.JSONObject


abstract class BaseMainFragment : BaseFragment(), IContractView.ICloudUploadView,IContractView.IDataUpdateView{

    val mCloudUploadPresenter= CloudUploadPresenter(this)
    val mDataUploadPresenter=DataUpdatePresenter(this)

    //云端上传回调
    override fun onSuccess(cloudIds: MutableList<Int>?) {
        uploadSuccess(cloudIds)
    }
    override fun onDeleteSuccess() {
    }

    //增量更新回调
    override fun onSuccess() {
    }
    override fun onList(list: MutableList<DataUpdateBean>?) {
    }

    /**
     * 常用数据更新
     */
    fun onCheckUpdate() {
        if (NetworkUtil.isNetworkConnected()) {
            mCommonPresenter.getCommon()
            checkAppUpdate()
            checkSystemUpdate()
        }
    }

    /**
     * 检查系统更新
     */
    private fun checkSystemUpdate(){
        val url=Constants.RELEASE_BASE_URL+"Device/CheckUpdate"

        val  jsonBody = JSONObject()
        jsonBody.put(Constants.SN, DeviceUtil.getOtaSerialNumber())
        jsonBody.put(Constants.KEY, ServerParams.getInstance().GetHtMd5Key(DeviceUtil.getOtaSerialNumber()))
        jsonBody.put(Constants.VERSION_NO, DeviceUtil.getOtaProductVersion())

        val  jsonObjectRequest= JsonObjectRequest(Request.Method.POST,url,jsonBody, {
            showLog(it.toString())
            val code= it.optInt("Code")
            val jsonObject=it.optJSONObject("Data")
            if (code==200&&jsonObject!=null){
                AppUtils.startAPP(context,Constants.PACKAGE_SYSTEM_UPDATE)
            }
        },null)
        MyApplication.requestQueue?.add(jsonObjectRequest)
    }

    /**
     * 检查应用更新
     */
    private fun checkAppUpdate(){
        val url=Constants.URL_BASE+"app/info/one?type=1"

        val  jsonObjectRequest=StringRequest(Request.Method.GET,url, {
            val jsonObject=JSONObject(it)
            val code= jsonObject.optInt("code")
            val dataString=jsonObject.optString("data")
            val item=Gson().fromJson(dataString,AppUpdateBean::class.java)
            if (code==0){
                if (item.versionCode > AppUtils.getVersionCode(requireActivity())) {
                    requireActivity().runOnUiThread {
                        downLoadAPP(item)
                    }
                }
            }
        },null)
        MyApplication.requestQueue?.add(jsonObjectRequest)
    }

    /**
     * 下载应用
     */
    private fun downLoadAPP(bean: AppUpdateBean) {
        val updateDialog = AppUpdateDialog(requireActivity(), 1, bean).builder()

        val targetFileStr = FileAddress().getPathApk("lnkstudy")
        FileDownManager.with(requireActivity()).create(bean.downloadUrl).setPath(targetFileStr).startSingleTaskDownLoad(object :
            FileDownManager.SingleTaskCallBack {
            override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                if (task != null && task.isRunning) {
                    requireActivity().runOnUiThread {
                        val s = ToolUtils.getFormatNum(soFarBytes.toDouble() / (1024 * 1024), "0.0M") + "/" +
                                ToolUtils.getFormatNum(totalBytes.toDouble() / (1024 * 1024), "0.0M")
                        updateDialog?.setUpdateBtn(s)
                    }
                }
            }

            override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
            }

            override fun completed(task: BaseDownloadTask?) {
                updateDialog.dismiss()
                AppUtils.installApp(requireActivity(), targetFileStr)
            }

            override fun error(task: BaseDownloadTask?, e: Throwable?) {
                updateDialog.dismiss()
            }
        })
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.REFRESH_EVENT){
            lazyLoad()
        }
        super.onEventBusMessage(msgFlag)
    }


    /**
     * 上传成功(书籍云id)
     */
    open fun uploadSuccess(cloudIds: MutableList<Int>?){
    }

}

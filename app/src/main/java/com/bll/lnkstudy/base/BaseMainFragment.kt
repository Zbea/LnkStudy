package com.bll.lnkstudy.base

import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.mvp.model.DataUpdateBean
import com.bll.lnkstudy.mvp.presenter.CloudUploadPresenter
import com.bll.lnkstudy.mvp.presenter.DataUpdatePresenter
import com.bll.lnkstudy.mvp.view.IContractView


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

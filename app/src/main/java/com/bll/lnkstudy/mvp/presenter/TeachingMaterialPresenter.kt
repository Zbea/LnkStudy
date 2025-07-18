package com.bll.lnkstudy.mvp.presenter

import com.bll.lnkstudy.mvp.model.book.TeachingMaterialList
import com.bll.lnkstudy.mvp.view.IContractView
import com.bll.lnkstudy.net.BasePresenter
import com.bll.lnkstudy.net.BaseResult
import com.bll.lnkstudy.net.Callback
import com.bll.lnkstudy.net.RetrofitManager

class TeachingMaterialPresenter(view: IContractView.ITeachingMaterialView, var screen:Int=0): BasePresenter<IContractView.ITeachingMaterialView>(view) {

    fun getList(map: HashMap<String,Any>){
        val list= RetrofitManager.service.getTeachingMaterials(map)
        doRequest(list, object : Callback<TeachingMaterialList>(view,screen) {
            override fun failed(tBaseResult: BaseResult<TeachingMaterialList>): Boolean {
                return false
            }
            override fun success(tBaseResult: BaseResult<TeachingMaterialList>) {
                if (tBaseResult.data!=null)
                    view.onList(tBaseResult.data)
            }
        },true)
    }


}
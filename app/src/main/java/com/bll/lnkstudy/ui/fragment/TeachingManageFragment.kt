package com.bll.lnkstudy.ui.fragment

import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import com.bll.lnkstudy.DataBeanManager
import com.bll.lnkstudy.DataUpdateManager
import com.bll.lnkstudy.FileAddress
import com.bll.lnkstudy.MethodManager
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseFragment
import com.bll.lnkstudy.base.BaseMainFragment
import com.bll.lnkstudy.manager.TextbookGreenDaoManager
import com.bll.lnkstudy.mvp.model.ItemTypeBean
import com.bll.lnkstudy.mvp.model.book.TextbookBean
import com.bll.lnkstudy.mvp.model.cloud.CloudListBean
import com.bll.lnkstudy.ui.activity.TeachingMaterialListActivity
import com.bll.lnkstudy.ui.fragment.teaching.DocumentFragment
import com.bll.lnkstudy.ui.fragment.teaching.TextbookFragment
import com.bll.lnkstudy.utils.FileUploadManager
import com.bll.lnkstudy.utils.FileUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.common_fragment_title.tv_btn
import java.io.File

class TeachingManageFragment: BaseMainFragment() {

    private var lastFragment: Fragment? = null
    private var fragments= mutableListOf<BaseFragment>()

    override fun getLayoutId(): Int {
        return R.layout.fragment_teaching_manage
    }

    override fun initView() {
        setTitle(DataBeanManager.listTitle[2])
        tv_btn.text="资料中心"

        tv_btn.setOnClickListener {
            customStartActivity(Intent(requireActivity(),TeachingMaterialListActivity::class.java))
        }

        for (i in 0..3){
            fragments.add(TextbookFragment().newInstance(i))
        }
        fragments.add(DocumentFragment())

        switchFragment(lastFragment,fragments[0])
        initTab()
    }

    override fun lazyLoad() {
    }

    private fun initTab(){
        val tabStrs= DataBeanManager.textbookType
        for (i in tabStrs.indices) {
            itemTabTypes.add(ItemTypeBean().apply {
                title=tabStrs[i]
                isCheck=i==0
            })
        }
        mTabTypeAdapter?.setNewData(itemTabTypes)
    }

    override fun onTabClickListener(view: View, position: Int) {
        when(position){
            4->{
                showView(tv_btn)
            }
            else->{
                disMissView(tv_btn)
            }
        }
        switchFragment(lastFragment, fragments[position])
    }

    //页码跳转
    private fun switchFragment(from: Fragment?, to: Fragment?) {
        if (from != to) {
            lastFragment = to
            val fm = activity?.supportFragmentManager!!
            val ft = fm.beginTransaction()

            if (!to?.isAdded!!) {
                if (from != null) {
                    ft.hide(from)
                }
                ft.add(R.id.fl_content_teaching, to).commit()
            } else {
                if (from != null) {
                    ft.hide(from)
                }
                ft.show(to).commit()
            }
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        for (fragment in fragments){
            fragment.onRefreshData()
        }
    }

    /**
     * 删除文档
     */
    fun deleteDocument(){
        FileUtils.delete(FileAddress().getPathDocument())
        onRefreshData()
    }

    /**
     * 上传本地课本
     */
    fun uploadTextBook(token:String){
        val cloudList= mutableListOf<CloudListBean>()
        val textBooks= getTextbooksUnLock()
        for (book in textBooks){
            //判读是否存在手写内容
            if (FileUtils.isExistContent(book.bookDrawPath)){
                FileUploadManager(token).apply {
                    startZipUpload(book.bookDrawPath, File(book.bookDrawPath).name)
                    setCallBack{
                        cloudList.add(CloudListBean().apply {
                            type=1
                            subTypeStr=book.typeStr
                            grade=book.grade
                            date=System.currentTimeMillis()
                            listJson= Gson().toJson(book)
                            downloadUrl=it
                            zipUrl=book.downloadUrl
                            bookId=book.bookId
                        })
                        if (cloudList.size==textBooks.size)
                            mCloudUploadPresenter.upload(cloudList)
                    }
                }

            }
            else{
                cloudList.add(CloudListBean().apply {
                    type=1
                    grade=book.grade
                    subTypeStr=book.typeStr
                    date=System.currentTimeMillis()
                    listJson= Gson().toJson(book)
                    zipUrl=book.downloadUrl
                    bookId=book.bookId
                })
                if (cloudList.size==textBooks.size)
                    mCloudUploadPresenter.upload(cloudList)
            }
        }
    }

    /**
     * 移除课本到往期课本
     */
    private fun moveTextbook(){
        //所有教材更新为往期教材
        val items= TextbookGreenDaoManager.getInstance().queryAllTextBook(getString(R.string.textbook_tab_my))
        for (item in items){
            item.typeStr=getString(R.string.textbook_tab_old)
            //修改增量更新
            DataUpdateManager.editDataUpdate(1,item.bookId,1,item.bookId, Gson().toJson(item))
        }
        TextbookGreenDaoManager.getInstance().insertOrReplaceBooks(items)
    }

    /**
     * 获取未加锁的往期课本、参考课本
     */
    private fun getTextbooksUnLock():MutableList<TextbookBean>{
        val oldStr=getString(R.string.textbook_tab_old)
        val assistStr=getString(R.string.textbook_tab_assist)
        //获取未加锁的课本、参考课本
        val textBooks= mutableListOf<TextbookBean>()
        textBooks.addAll(TextbookGreenDaoManager.getInstance().queryAllTextbook(oldStr,false))
        textBooks.addAll(TextbookGreenDaoManager.getInstance().queryAllTextbook(assistStr,false))
        return textBooks
    }

    override fun uploadSuccess(cloudIds: MutableList<Int>?) {
        //删除所有往期教材的图片文件
        for (book in getTextbooksUnLock()){
            MethodManager.deleteTextbook(book)
        }
        moveTextbook()
        onRefreshData()
    }

}
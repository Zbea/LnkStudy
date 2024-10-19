package com.bll.lnkstudy.ui.activity

import android.content.res.Configuration
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.manager.PaintingBeanDaoManager
import com.bll.lnkstudy.mvp.model.painting.PaintingBean
import com.bll.lnkstudy.utils.GlideUtils
import com.bll.lnkstudy.utils.SPUtil
import kotlinx.android.synthetic.main.ac_painting_image.iv_rule
import kotlinx.android.synthetic.main.ac_painting_image.ll_draw_content
import kotlinx.android.synthetic.main.ac_painting_image.v_content
import kotlinx.android.synthetic.main.common_drawing_tool.iv_btn
import kotlinx.android.synthetic.main.common_drawing_tool.iv_catalog
import kotlinx.android.synthetic.main.common_drawing_tool.iv_draft
import kotlinx.android.synthetic.main.common_drawing_tool.iv_erasure
import kotlinx.android.synthetic.main.common_drawing_tool.iv_expand
import kotlinx.android.synthetic.main.common_drawing_tool.iv_page_down
import kotlinx.android.synthetic.main.common_drawing_tool.iv_page_up
import kotlinx.android.synthetic.main.common_drawing_tool.iv_tool
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page
import kotlinx.android.synthetic.main.common_drawing_tool.tv_page_total

class PaintingImageActivity:BaseAppCompatActivity() {
    private lateinit var paintingBean:PaintingBean
    private var page=0
    private var paths= mutableListOf<String>()
    private lateinit var rl_draw_content:RelativeLayout
    private lateinit var ll_page_content:LinearLayout

    override fun layoutId(): Int {
        return R.layout.ac_painting_image
    }

    override fun initData() {
        paintingBean=PaintingBeanDaoManager.getInstance().queryBean(intent.flags)
        paths=paintingBean.paths
    }

    override fun initView() {
        disMissView(iv_draft,iv_erasure,iv_btn,iv_catalog,iv_tool,iv_expand)

        rl_draw_content=findViewById(R.id.rl_draw_content)
        ll_page_content=findViewById(R.id.ll_page_content)

        iv_page_down?.setOnClickListener {
            if (page<paths.size-1){
                page+=1
                onContent()
            }
        }

        iv_page_up?.setOnClickListener {
            if (page>0){
                page-=1
                onContent()
            }
        }

        setRule()
        onContent()
    }

    private fun onChangeExpandView() {
        if (screenPos==Constants.SCREEN_LEFT){
            ll_draw_content?.removeAllViews()
            ll_draw_content?.addView(rl_draw_content)
            ll_draw_content?.addView(ll_page_content)
        }
        else if (screenPos==Constants.SCREEN_RIGHT){
            ll_draw_content?.removeAllViews()
            ll_draw_content?.addView(ll_page_content)
            ll_draw_content?.addView(rl_draw_content)
        }
    }

    private fun onContent() {
        GlideUtils.setImageUrl(this,paths[page],v_content)
        tv_page_total.text=paths.size.toString()
        tv_page.text="${page+1}"
    }

    /**
     * 设置规矩图
     */
    private fun setRule(){
        if (SPUtil.getBoolean(Constants.SP_PAINTING_RULE_SET)){
            GlideUtils.setImageUrl(this,R.mipmap.icon_painting_draw_hb,iv_rule)
        }
        else{
            GlideUtils.setImageUrl(this,0,iv_rule)
        }
    }

    override fun onEventBusMessage(msgFlag: String) {
        if (msgFlag==Constants.PAINTING_RULE_IMAGE_SET_EVENT){
            setRule()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        onChangeExpandView()
    }

}
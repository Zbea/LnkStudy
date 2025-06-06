package com.bll.lnkstudy.base

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.os.Handler
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.dialog.*
import com.bll.lnkstudy.mvp.model.PopupBean
import com.bll.lnkstudy.ui.activity.date.DateEventActivity
import com.bll.lnkstudy.ui.activity.drawing.*
import com.bll.lnkstudy.utils.*
import kotlinx.android.synthetic.main.ac_drawing.*
import kotlinx.android.synthetic.main.ac_homework_correct.*
import kotlinx.android.synthetic.main.common_correct_score.*
import kotlinx.android.synthetic.main.common_drawing_geometry.*
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_a
import kotlinx.android.synthetic.main.common_drawing_page_number.tv_page_total_a
import kotlinx.android.synthetic.main.common_drawing_tool.*
import kotlinx.android.synthetic.main.common_title.*
import java.util.regex.Pattern


abstract class BaseDrawingActivity : BaseAppCompatActivity() {

    var isExpand = false
    var elik_a: EinkPWInterface? = null
    var elik_b: EinkPWInterface? = null
    var isErasure = false
    private var circlePos = 0
    private var axisPos = 0
    private var isGeometry = false//是否处于几何绘图
    private var isParallel = false//是否选中平行
    private var isCurrent = false//当前支持的几何绘图笔形
    private var isScale = false//是否选中刻度
    private var revocationList = mutableListOf<Int>()
    private var currentGeometry = 0
    private var currentDrawObj = PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN//当前笔形

    var ll_page_content_a: LinearLayout? = null
    var ll_page_content_b: LinearLayout? = null
    var ll_draw_content: LinearLayout? = null
    var v_content_a: ImageView? = null
    var v_content_b: ImageView? = null

    private var isAllowChange = true //是否运行移屏幕
    var isDrawingSave=false//是否手写保存


    override fun initCreate() {
        if (this is FreeNoteActivity || this is PlanOverviewActivity || this is DraftDrawingActivity || this is DateEventActivity) {
            isAllowChange = false
        }

        v_content_b = findViewById(R.id.v_content_b)
        if (isAllowChange) {
            ll_draw_content = findViewById(R.id.ll_draw_content)
            ll_page_content_a = findViewById(R.id.ll_page_content_a)
            ll_page_content_b = findViewById(R.id.ll_page_content_b)
            v_content_a = findViewById(R.id.v_content_a)
        }

        onInStanceElik()

        if (isAllowChange)
            onChangeExpandView()

        if (iv_top != null)
            setViewElikUnable(iv_top)

        if (iv_score != null)
            setViewElikUnable(iv_score)

        initClick()
        initGeometryView()
    }

    open fun onInStanceElik() {
        elik_a = v_content_a?.pwInterFace
        elik_b = v_content_b?.pwInterFace
    }

    private fun initClick() {
        iv_tool?.setOnClickListener {
            showDialogAppTool()
        }

        iv_erasure?.setOnClickListener {
            isErasure = !isErasure
            if (isErasure) {
                iv_erasure?.setImageResource(R.mipmap.icon_draw_erasure_big)
                onErasure()
            } else {
                stopErasure()
            }
        }

        iv_draft?.setOnClickListener {
            startActivity(Intent(this, DraftDrawingActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        iv_page_up?.setOnClickListener {
            Handler().postDelayed({
                onPageUp()
            },if (isDrawingSave)500 else 0)
        }

        iv_page_down?.setOnClickListener {
            Handler().postDelayed({
                onPageDown()
            },if (isDrawingSave)500 else 0)
        }

        iv_catalog?.setOnClickListener {
            onCatalog()
        }

        iv_expand?.setOnClickListener {
            isClickExpend=true
            onChangeExpandContent()
        }
    }

    /**
     * 几何绘图
     */
    private fun initGeometryView() {
        val popsCircle = mutableListOf<PopupBean>()
        popsCircle.add(PopupBean(0, getString(R.string.circle_1), R.mipmap.icon_geometry_circle_1))
        popsCircle.add(PopupBean(1, getString(R.string.circle_2), R.mipmap.icon_geometry_circle_2))
        popsCircle.add(PopupBean(2, getString(R.string.circle_3), R.mipmap.icon_geometry_circle_3))

        val popsAxis = mutableListOf<PopupBean>()
        popsAxis.add(PopupBean(0, getString(R.string.axis_one), R.mipmap.icon_geometry_axis_1))
        popsAxis.add(PopupBean(1, getString(R.string.axis_two), R.mipmap.icon_geometry_axis_2))
        popsAxis.add(PopupBean(2, getString(R.string.axis_three), R.mipmap.icon_geometry_axis_3))

        val pops = mutableListOf<PopupBean>()
        pops.add(PopupBean(0, getString(R.string.line_black), false))
        pops.add(PopupBean(1, getString(R.string.line_gray), false))
        pops.add(PopupBean(2, getString(R.string.line_dotted), false))

        iv_geometry?.setOnClickListener {
            setViewElikUnable(ll_geometry)
            showView(ll_geometry)
            disMissView(iv_geometry)
        }

        iv_line?.setOnClickListener {
            setCheckView(ll_line)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_LINE)
            currentGeometry = 1
        }

        iv_rectangle?.setOnClickListener {
            setCheckView(ll_rectangle)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_RECTANGLE)
            currentGeometry = 2
        }

        tv_circle?.setOnClickListener {
            PopupClick(this, popsCircle, tv_circle, 5).builder().setOnSelectListener {
                iv_circle.setImageResource(it.resId)
                circlePos = it.id
                setEilkCircle()
            }
        }

        iv_circle?.setOnClickListener {
            setEilkCircle()
        }

        iv_arc?.setOnClickListener {
            setCheckView(ll_arc)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_ARC)
            currentGeometry = 4
        }

        iv_oval?.setOnClickListener {
            setCheckView(ll_oval)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_OVAL)
            currentGeometry = 5
        }

        iv_vertical?.setOnClickListener {
            setCheckView(ll_vertical)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_VERTICALLINE)
            currentGeometry = 6
        }

        iv_parabola?.setOnClickListener {
            setCheckView(ll_parabola)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_PARABOLA)
            currentGeometry = 7
        }

        iv_angle?.setOnClickListener {
            setCheckView(ll_angle)
            setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_ANGLE)
            currentGeometry = 8
        }

        tv_axis?.setOnClickListener {
            PopupClick(this, popsAxis, tv_axis, 5).builder().setOnSelectListener {
                iv_axis?.setImageResource(it.resId)
                axisPos = it.id
                setEilkAxis()
            }
        }

        iv_axis?.setOnClickListener {
            setEilkAxis()
        }

        iv_pen?.setOnClickListener {
            setDrawing()
        }

        tv_revocation?.setOnClickListener {
            if (revocationList.size > 0) {
                val type = revocationList.last()
                if (type == 1) {
                    elik_a?.unDo()
                } else {
                    elik_b?.unDo()
                }
                revocationList.removeLast()
            }
        }

        tv_gray_line?.setOnClickListener {
            if (!isGeometry) {
                return@setOnClickListener
            }
            PopupClick(this, pops, tv_gray_line, 5).builder().setOnSelectListener {
                tv_gray_line?.text = it.name
                setLine(it.id)
            }
        }

        tv_scale?.setOnClickListener {
            if (isErasure) {
                stopErasure()
            }
            tv_scale.isSelected = !tv_scale.isSelected
            isScale = tv_scale.isSelected
            tv_scale.setTextColor(getColor(if (isScale) R.color.white else R.color.black))
            if (currentGeometry == 9) {
                setEilkAxis()
            }
        }

        tv_parallel?.setOnClickListener {
            if (isErasure) {
                stopErasure()
            }
            tv_parallel.isSelected = !isParallel
            isParallel = tv_parallel.isSelected
            tv_parallel.setTextColor(getColor(if (isParallel) R.color.white else R.color.black))
        }

        tv_reduce?.setOnClickListener {
            setDrawing()
            disMissView(ll_geometry)
            showView(iv_geometry)
            setViewElikUnable(iv_geometry)
            if (isParallel) {
                tv_parallel?.callOnClick()
            }
            if (isScale) {
                tv_scale?.callOnClick()
            }
        }

        tv_out?.setOnClickListener {
            if (this is ExamCommitDrawingActivity) {
                tv_reduce?.callOnClick()
                return@setOnClickListener
            }
            setDrawing()
            disMissView(ll_geometry, iv_geometry)
            if (isParallel) {
                tv_parallel?.callOnClick()
            }
            if (isScale) {
                tv_scale?.callOnClick()
            }
        }

        elik_a?.setDrawEventListener(object : EinkPWInterface.PWDrawEventWithPoint {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean, p2: PWInputPoint?) {
                elik_a?.setShifted(isCurrent && isParallel)
                onElikStart_a()
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: PWInputPoint?, p3: PWInputPoint?) {
                revocationList.add(1)
                if (revocationList.size > 2)
                    revocationList.remove(0)
                if (elik_a?.curDrawObjStatus == true) {
                    reDrawGeometry(elik_a!!, 1)
                }
                onElikSava_a()
                elik_a?.saveBitmap(true) {}
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {

            }
        })

        elik_b?.setDrawEventListener(object : EinkPWInterface.PWDrawEventWithPoint {
            override fun onTouchDrawStart(p0: Bitmap?, p1: Boolean, p2: PWInputPoint?) {
                elik_b?.setShifted(isCurrent && isParallel)
                onElikStart_b()
            }

            override fun onTouchDrawEnd(p0: Bitmap?, p1: Rect?, p2: PWInputPoint?, p3: PWInputPoint?) {
                revocationList.add(2)
                if (revocationList.size > 2)
                    revocationList.remove(0)
                if (elik_b?.curDrawObjStatus == true) {
                    reDrawGeometry(elik_b!!, 2)
                }
                onElikSava_b()
                elik_b?.saveBitmap(true) {}
            }

            override fun onOneWordDone(p0: Bitmap?, p1: Rect?) {
            }
        })

        this.setTouchAsFocus(true)
    }

    /**
     * 设置刻度重绘
     */
    private fun reDrawGeometry(elik: EinkPWInterface, location: Int) {
        if (isErasure)
            return
        if (isScale) {
            if (currentGeometry == 1 || currentGeometry == 2 || currentGeometry == 3 || currentGeometry == 5 || currentGeometry == 7 || currentGeometry == 8 || currentGeometry == 9) {
                Handler().postDelayed({
                    if (location == 1) {
                        v_content_a!!.invalidate()
                    } else {
                        v_content_b!!.invalidate()
                    }
                    GeometryScaleDialog(this, currentGeometry, circlePos, location).builder()
                        ?.setOnDialogClickListener { width, height ->
                            when (currentGeometry) {
                                2, 5 -> {
                                    elik.reDrawShape(width, height)
                                }

                                7 -> {
                                    val info = elik.curHandlerInfo
                                    elik.reDrawShape(if (setA(info) > 0) width else -width, info.split("&")[1].toFloat())
                                }

                                9 -> {
                                    elik.reDrawShape(width, 5f)
                                }

                                else -> {
                                    elik.reDrawShape(width, -1f)
                                }
                            }
                        }
                }, 200)
            }
        }
    }

    /**
     * 获取a值
     */
    private fun setA(info: String): Float {
        val list = mutableListOf<String>()
        val pattern = Pattern.compile("-?\\d+(\\.\\d+)") // 编译正则表达式，匹配连续的数字
        val matcher = pattern.matcher(info) // 创建匹配器对象
        while (matcher.find()) {
            list.add(matcher.group())
        }
        return list[0].toFloat()
    }

    /**
     * 画圆
     */
    private fun setEilkCircle() {
        setCheckView(ll_circle)
        when (circlePos) {
            0 -> setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CIRCLE)
            1 -> setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CIRCLE2)
            else -> setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CIRCLE3)
        }
        currentGeometry = 3
    }

    /**
     * 画坐标
     */
    private fun setEilkAxis() {
        setCheckView(ll_axis)
        when (axisPos) {
            0 -> {
                setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_AXIS)
                elik_a?.setDrawAxisProperty(1, 10, 5, isScale)
                elik_b?.setDrawAxisProperty(1, 10, 5, isScale)
            }

            1 -> {
                setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_AXIS2)
                elik_a?.setDrawAxisProperty(2, 10, 5, isScale)
                elik_b?.setDrawAxisProperty(2, 10, 5, isScale)
            }

            2 -> {
                setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_AXIS3)
                elik_a?.setDrawAxisProperty(3, 10, 5, isScale)
                elik_b?.setDrawAxisProperty(3, 10, 5, isScale)
            }
        }
        currentGeometry = 9
    }

    /**
     * 设置线
     */
    private fun setLine(type: Int) {
        when (type) {
            0 -> {
                elik_a?.penColor = Color.BLACK
                elik_a?.setPaintEffect(0)

                elik_b?.penColor = Color.BLACK
                elik_b?.setPaintEffect(0)
            }

            1 -> {
                elik_a?.penColor = Color.parseColor("#999999")
                elik_a?.setPaintEffect(0)

                elik_b?.penColor = Color.parseColor("#999999")
                elik_b?.setPaintEffect(0)
            }

            else -> {
                elik_a?.penColor = Color.BLACK
                elik_a?.setPaintEffect(1)

                elik_b?.penColor = Color.BLACK
                elik_b?.setPaintEffect(1)
            }
        }
    }

    /**
     * 设置选中笔形
     */
    private fun setCheckView(view: View) {
        if (isErasure) {
            stopErasure()
        }
        if (view != ll_pen) {
            isGeometry = true
        }
        //当前支持平行的view
        isCurrent = view == ll_line || view == ll_angle || view == ll_axis
        ll_line?.setBackgroundResource(R.color.color_transparent)
        ll_rectangle?.setBackgroundResource(R.color.color_transparent)
        ll_circle?.setBackgroundResource(R.color.color_transparent)
        ll_arc?.setBackgroundResource(R.color.color_transparent)
        ll_oval?.setBackgroundResource(R.color.color_transparent)
        ll_vertical?.setBackgroundResource(R.color.color_transparent)
        ll_parabola?.setBackgroundResource(R.color.color_transparent)
        ll_angle?.setBackgroundResource(R.color.color_transparent)
        ll_axis?.setBackgroundResource(R.color.color_transparent)
        ll_pen?.setBackgroundResource(R.color.color_transparent)
        view.setBackgroundResource(R.drawable.bg_geometry_select)
    }

    /**
     * 设置笔类型
     */
    protected fun setDrawOjectType(type: Int) {
        elik_a?.drawObjectType = type
        elik_b?.drawObjectType = type
        if (type != PWDrawObjectHandler.DRAW_OBJ_CHOICERASE)
            currentDrawObj = type
    }

    /**
     * 设置笔宽
     */
    protected fun setDrawWidth(width: Int) {
        elik_a?.penSettingWidth = width
        elik_b?.penSettingWidth = width
    }

    /**
     * 工具栏弹窗
     */
    private fun showDialogAppTool() {
        AppToolDialog(this, screenPos, getCurrentScreenPos()).builder().setDialogClickListener {
            setViewElikUnable(ll_geometry)
            showView(ll_geometry)
            if (isErasure)
                stopErasure()
        }
    }

    /**
     * 设置不能手写
     */
    protected fun setViewElikUnable(view: View) {
        elik_a?.addOnTopView(view)
        elik_b?.addOnTopView(view)
    }

    /**
     * 页面清空
     */
    fun setContentImageClear(){
        v_content_a?.setImageResource(0)
        v_content_b?.setImageResource(0)
        tv_page_total.text=""
        tv_page_total_a.text=""
        tv_page.text=""
        tv_page_a.text=""
    }

    /**
     * 下一页
     */
    open fun onPageDown() {
    }
    /**
     * 上一页
     */
    open fun onPageUp() {
    }

    /**
     * 打开目录
     */
    open fun onCatalog() {
    }

    /**
     * 左屏下笔
     */
    open fun onElikStart_a() {
    }

    /**
     * 右屏下笔
     */
    open fun onElikStart_b() {
    }

    /**
     * 左屏抬笔
     */
    open fun onElikSava_a() {
    }

    /**
     * 右屏抬笔
     */
    open fun onElikSava_b() {
    }

    /**
     * 设置是否可以手写
     */
    fun setPWEnabled(boolean: Boolean) {
        elik_a?.setPWEnabled(boolean)
        elik_b?.setPWEnabled(boolean)
    }

    /**
     * 设置是否可以手写
     */
    fun setDisableTouchInput(boolean: Boolean) {
        elik_a?.disableTouchInput(boolean)
        elik_b?.disableTouchInput(boolean)
    }

    /**
     * 设置擦除
     */
    private fun onErasure() {
        setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_CHOICERASE)
    }

    /**
     * 结束擦除
     * （在展平、收屏时候都结束擦除）
     */
    fun stopErasure() {
        isErasure = false
        //关闭橡皮擦
        iv_erasure?.setImageResource(R.mipmap.icon_draw_erasure)
        setDrawOjectType(currentDrawObj)
    }

    /**
     * 恢复手写
     */
    private fun setDrawing() {
        setCheckView(ll_pen)
        setDrawOjectType(PWDrawObjectHandler.DRAW_OBJ_RANDOM_PEN)
        currentGeometry = 0
        //设置黑线
        setLine(0)
        tv_gray_line?.text = getString(R.string.line_black)

        isGeometry = false
    }

    /**
     * 自动收屏后自动取消橡皮擦
     */
    protected fun changeErasure() {
        if (isErasure) {
            isErasure = false
            stopErasure()
        }
    }

    /**
     * 单双屏切换以及创建新数据
     */
    open fun onChangeExpandView() {
        if (screenPos == Constants.SCREEN_LEFT) {
            if (!isExpand) {
                ll_draw_content?.removeAllViews()
                ll_draw_content?.addView(v_content_a)
                ll_draw_content?.addView(ll_page_content_a)
                ll_draw_content?.addView(v_content_b)
                ll_draw_content?.addView(ll_page_content_b)
                disMissView(ll_page_content_a, v_content_a)
                showView(ll_page_content_b, v_content_b)
            } else {
                ll_draw_content?.removeAllViews()
                ll_draw_content?.addView(v_content_a)
                ll_draw_content?.addView(ll_page_content_b)
                ll_draw_content?.addView(ll_page_content_a)
                ll_draw_content?.addView(v_content_b)
                showView(ll_page_content_a, v_content_a, ll_page_content_b, v_content_b)
            }
        } else if (screenPos == Constants.SCREEN_RIGHT) {
            if (!isExpand) {
                ll_draw_content?.removeAllViews()
                ll_draw_content?.addView(ll_page_content_b)
                ll_draw_content?.addView(v_content_a)
                ll_draw_content?.addView(ll_page_content_a)
                ll_draw_content?.addView(v_content_b)
                disMissView(ll_page_content_a, v_content_a)
                showView(ll_page_content_b, v_content_b)
            } else {
                ll_draw_content?.removeAllViews()
                ll_draw_content?.addView(v_content_a)
                ll_draw_content?.addView(ll_page_content_a)
                ll_draw_content?.addView(ll_page_content_b)
                ll_draw_content?.addView(v_content_b)
                showView(ll_page_content_a, v_content_a, ll_page_content_b, v_content_b)
            }
        } else {
            ll_draw_content?.removeAllViews()
            ll_draw_content?.addView(v_content_a)
            ll_draw_content?.addView(ll_page_content_a)
            ll_draw_content?.addView(ll_page_content_b)
            ll_draw_content?.addView(v_content_b)
            showView(ll_page_content_a, v_content_a, ll_page_content_b, v_content_b)
        }
    }

    /**
     * 单双屏切换
     */
    open fun onChangeExpandContent() {
    }

    /**
     * 设置内容
     */
    open fun onContent() {
    }

    /**
     * 单双屏展开
     */
    fun moveToScreen(isExpand: Boolean) {
        moveToScreen(if (isExpand) 3 else screenPos)
    }

    /**
     * 换屏 0默认左屏幕 1左屏幕 2右屏幕 3全屏
     */
    fun moveToScreen(scree: Int) {
        moveToScreenPanel(scree)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        //屏蔽长按切焦点造成原手写翻页
        if (getKeyEventStatus() == 17 || getKeyEventStatus() == 34) {
            return super.onKeyUp(keyCode, event)
        }
        when (keyCode) {
            KeyEvent.KEYCODE_PAGE_DOWN -> {
                onPageDown()
            }

            KeyEvent.KEYCODE_PAGE_UP -> {
                onPageUp()
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isAllowChange) {
            onChangeExpandView()
            onContent()
        }
    }

}



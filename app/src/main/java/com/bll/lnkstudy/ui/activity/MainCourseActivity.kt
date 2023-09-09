package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseAppCompatActivity
import com.bll.lnkstudy.dialog.CourseSelectDialog
import com.bll.lnkstudy.dialog.CourseTimeSelectorDialog
import com.bll.lnkstudy.dialog.InputContentDialog
import com.bll.lnkstudy.manager.CourseGreenDaoManager
import com.bll.lnkstudy.mvp.model.CourseBean
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.SystemSettingUtils
import kotlinx.android.synthetic.main.ac_course.*
import kotlinx.android.synthetic.main.common_title.*
import org.greenrobot.eventbus.EventBus

//课程表
class MainCourseActivity : BaseAppCompatActivity() {

    private var type = 0//0五天六节 1六天六节 2五天七节 3六天七节 4五天八节 5六天八节
    private var row = 11
    private var column = 7
    private var isAdd = true //是否是重新编辑课表
    private var selectLists = mutableListOf<CourseBean>()//已经选择了的课程

    private var timeWidth = 60
    private var weekHeight = 80
    private var lessonsWidth = 230
    private var dividerHeight = 52
    private var dividerHeight1 = 76

    private var totalWidth = 1330
    private var totalHeight = 1150

    private var height = 120
    private var width = 210

    override fun layoutId(): Int {
        return R.layout.ac_course
    }

    override fun initData() {
        isAdd = intent.flags == 0
        type = intent.getIntExtra("courseType", 0)

        when (type) {
            0 -> {//五天六节课
                row = 11
                column = 7

                width = (totalWidth - timeWidth - lessonsWidth) / 5
                height = (totalHeight - weekHeight - dividerHeight1 - dividerHeight * 3) / 6

            }
            1 -> {//六天六节课
                row = 11
                column = 8

                width = (totalWidth - timeWidth - lessonsWidth) / 6
                height = (totalHeight - weekHeight - dividerHeight1 - dividerHeight * 3) / 6

            }
            2 -> {//五天七节课
                row = 11
                column = 7

                width = (totalWidth - timeWidth - lessonsWidth) / 5
                height = (totalHeight - weekHeight - dividerHeight1 - dividerHeight * 2) / 7
            }
            3 -> {//六天七节课
                row = 11
                column = 8

                width = (totalWidth - timeWidth - lessonsWidth) / 6
                height = (totalHeight - weekHeight - dividerHeight1 - dividerHeight * 2) / 7

            }
            4 -> {//五天八节课
                row = 10
                column = 7

                width = (totalWidth - timeWidth - lessonsWidth) / 5
                height = (totalHeight - weekHeight - dividerHeight1) / 8
            }
            5 -> {//六天八节课
                row = 10
                column = 8

                width = (totalWidth - timeWidth - lessonsWidth) / 6
                height = (totalHeight - weekHeight - dividerHeight1) / 8
            }
        }
        grid.columnCount = column
        grid.rowCount = row
    }

    override fun initView() {
        setPageTitle(R.string.course_title_str)
        showView(tv_setting)
        tv_setting.text=getString(R.string.save)

        tv_setting?.setOnClickListener {
            if (selectLists.size == 0) return@setOnClickListener
            CourseGreenDaoManager.getInstance().deleteAll()//清除以前存储的课程
            CourseGreenDaoManager.getInstance().insertAll(selectLists)
            SystemSettingUtils.saveScreenShot(this, grid, "course")
            EventBus.getDefault().post(Constants.COURSE_EVENT)
            SPUtil.putInt("courseType", type)
            finish()
        }

        addTimeLayout()
        addWeekLayout()
        addLessonsLayout()
        addContentLayout()

    }

    //添加时间布局在第一列
    @SuppressLint("SuspiciousIndentation")
    private fun addTimeLayout() {

        var heightTime1: Int
        var heightTime2: Int
        when (type) {
            0, 1 -> {
                heightTime1=weekHeight + dividerHeight + 4 * height
                heightTime2=dividerHeight * 2 + 2 * height

                val view = getDateView("上午")
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(0, 6)
                params.width = timeWidth
                params.height = heightTime1
                params.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view, params)

                val view1 = getDateView("下午")
                val params1 = GridLayout.LayoutParams()
                params1.rowSpec = GridLayout.spec(if (type==4||type==5) 6 else 7, row - 7)
                params1.width = timeWidth
                params1.height = heightTime2
                params1.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view1, params1)
            }
            2, 3 -> {
                heightTime1 =weekHeight + dividerHeight + 4 * height
                heightTime2=dividerHeight + 3 * height

                val view = getDateView("上午")
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(0, 6)
                params.width = timeWidth
                params.height = heightTime1
                params.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view, params)

                val view1 = getDateView("下午")
                val params1 = GridLayout.LayoutParams()
                params1.rowSpec = GridLayout.spec(if (type==4||type==5) 6 else 7, row - 7)
                params1.width = timeWidth
                params1.height = heightTime2
                params1.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view1, params1)
            }
            else -> {
                heightTime1 =weekHeight + 4 * height
                heightTime2=4 * height

                val view = getDateView("上午")
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(0, 5)
                params.width = timeWidth
                params.height = heightTime1
                params.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view, params)

                val view1 = getDateView("下午")
                val params1 = GridLayout.LayoutParams()
                params1.rowSpec = GridLayout.spec( 6 , row - 6)
                params1.width = timeWidth
                params1.height = heightTime2
                params1.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view1, params1)
            }
        }
    }

    //添加第一行星期几的布局
    private fun addWeekLayout() {
        val weeks = arrayOf(
            "课程",
            "星期一",
            "星期二",
            "星期三",
            "星期四",
            "星期五",
            "星期六"
        )

        for (i in 1 until column) {
            val index = i - 1
            val view = getWeekView(weeks[index])

            val widths = if (i == 1) {//如果是第一个，则是课节的宽度
                lessonsWidth
            } else {
                width
            }

            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(0, 1)
            params.width = widths
            params.height = weekHeight
            params.columnSpec = GridLayout.spec(i, 1)
            grid.addView(view, params)
        }

    }

    //添加第二列课节布局
    private fun addLessonsLayout() {

        val lessons = when (type) {
            0, 1 -> {
                arrayOf(
                    "第一节",
                    "第二节",
                    "",
                    "第三节",
                    "第四节",
                    "",
                    "第五节",
                    "",
                    "第六节",
                    ""
                )
            }
            2, 3 -> {
                arrayOf(
                    "第一节",
                    "第二节",
                    "",
                    "第三节",
                    "第四节",
                    "",
                    "第五节",
                    "第六节",
                    "第七节",
                    ""
                )
            }
            else -> {
                arrayOf(
                    "第一节",
                    "第二节",
                    "第三节",
                    "第四节",
                    "",
                    "第五节",
                    "第六节",
                    "第七节",
                    "第八节"
                )
            }
        }

        for (i in 1 until row) {
            val id = "1$i".toInt()
            //根据id 查询是否已经存储了对应的时间
            val course = CourseGreenDaoManager.getInstance().queryID(id)
            val index = i - 1

            val view = getLessonsView(lessons[index])

            view.id = id
            val tvTime = view.findViewById<TextView>(R.id.tv_time)
            view.setOnClickListener {
                selectTime(tvTime, id)
            }

            //不重置
            if (!isAdd) {
                if (course != null) {
                    tvTime.text = course.name
                    selectLists.add(course)//将已经存在的加入课程集合
                }
            }

            val params = GridLayout.LayoutParams()
            params.width = lessonsWidth

            when (type) {
                0, 1 -> {
                    when (i) {
                        3, 8, 10 -> {
                            params.rowSpec = GridLayout.spec(i, 1)
                            params.height = dividerHeight
                            params.columnSpec = GridLayout.spec(1, column - 1)
                        }
                        6 -> {
                            params.rowSpec = GridLayout.spec(i, 1)
                            params.height = dividerHeight1
                            params.columnSpec = GridLayout.spec(0, column)
                        }
                        else -> {
                            params.rowSpec = GridLayout.spec(i, 1)
                            params.height = height
                            params.columnSpec = GridLayout.spec(1, 1)
                        }
                    }

                }
                2, 3 -> {
                    when (i) {
                        3, 10 -> {
                            params.rowSpec = GridLayout.spec(i, 1)
                            params.height = dividerHeight
                            params.columnSpec = GridLayout.spec(1, column - 1)
                        }
                        6 -> {
                            params.rowSpec = GridLayout.spec(i, 1)
                            params.height = dividerHeight1
                            params.columnSpec = GridLayout.spec(0, column)
                        }
                        else -> {
                            params.rowSpec = GridLayout.spec(i, 1)
                            params.height = height
                            params.columnSpec = GridLayout.spec(1, 1)
                        }
                    }

                }
                else -> {
                    when (i) {
                        5 -> {
                            params.rowSpec = GridLayout.spec(i, 1)
                            params.height = dividerHeight1
                            params.columnSpec = GridLayout.spec(0, column)
                        }
                        else -> {
                            params.rowSpec = GridLayout.spec(i, 1)
                            params.height = height
                            params.columnSpec = GridLayout.spec(1, 1)
                        }
                    }
                }
            }
            grid.addView(view, params)
        }

    }


    //内容
    private fun addContentLayout() {
        for (i in 2 until column) {
            for (j in 1 until row) {
                var view: TextView? = null
                val id = (i.toString() + j.toString()).toInt()
                //根据textview id 查询是否已经存储了对应的课程
                val course = CourseGreenDaoManager.getInstance().queryID(id)

                if (type == 0 || type == 1) {

                    if (j == 3 || j == 6 || j == 8 || j == 10) {
                        view = getCourseView1()
                        view.setOnClickListener {
                            inputContent(view as TextView)
                        }
                    } else {
                        view = getCourseView()
                        view.setOnClickListener {
                            selectCourse(view as TextView)
                        }
                    }

                    val params = GridLayout.LayoutParams()
                    when (j) {
                        3, 8, 10 -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth
                            params.height = dividerHeight
                            params.columnSpec = GridLayout.spec(1, column - 1)
                        }
                        6 -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth + timeWidth
                            params.height = dividerHeight1
                            params.columnSpec = GridLayout.spec(0, column)
                        }
                        else -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width
                            params.height = height
                            params.columnSpec = GridLayout.spec(i, 1)
                        }
                    }
                    grid.addView(view, params)

                }
                if (type == 2 || type == 3) {

                    if (j == 3 || j == 6 || j == 10) {
                        view = getCourseView1()
                        view.setOnClickListener {
                            inputContent(view as TextView)
                        }
                    } else {
                        view = getCourseView()
                        view.setOnClickListener {
                            selectCourse(view as TextView)
                        }
                    }

                    val params = GridLayout.LayoutParams()
                    when (j) {
                        3, 10 -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth
                            params.height = dividerHeight
                            params.columnSpec = GridLayout.spec(1, column - 1)
                        }
                        6 -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth + timeWidth
                            params.height = dividerHeight1
                            params.columnSpec = GridLayout.spec(0, column)
                        }
                        else -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width
                            params.height = height
                            params.columnSpec = GridLayout.spec(i, 1)
                        }
                    }
                    grid.addView(view, params)

                }
                if (type == 4 || type == 5) {

                    if (j == 5) {
                        view = getCourseView1()
                        view.setOnClickListener {
                            inputContent(view as TextView)
                        }
                    } else {
                        view = getCourseView()
                        view.setOnClickListener {
                            selectCourse(view as TextView)
                        }
                    }

                    val params = GridLayout.LayoutParams()
                    when (j) {
                        5 -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth + timeWidth
                            params.height = dividerHeight1
                            params.columnSpec = GridLayout.spec(0, column)
                        }
                        else -> {
                            view.setBackgroundResource(R.drawable.bg_course)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width
                            params.height = height
                            params.columnSpec = GridLayout.spec(i, 1)
                        }
                    }
                    grid.addView(view, params)
                }

                view?.id = id

                //不重置
                if (!isAdd) {
                    if (course != null) {
                        view?.text = course.name
                        selectLists.add(course)//将已经存在的加入课程集合
                    }
                }
            }

        }
    }


    //获得第二列课节view
    private fun getLessonsView(str: String): View {
        return layoutInflater.inflate(R.layout.common_course_lessons, null).also {
            it.findViewById<TextView>(R.id.tv_name).also { iv ->
                iv.text=str
            }
            it.setBackgroundResource(R.drawable.bg_course)
        }
    }

    //获取星期
    private fun getWeekView(str: String): View {
        return TextView(this).also {
            it.setTextColor(Color.BLACK)
            it.text=str
            it.textSize = 30f
            it.gravity = Gravity.CENTER
            it.setBackgroundResource(R.drawable.bg_course)
        }
    }

    //获得课程
    private fun getCourseView(): TextView {
        return TextView(this).also {
            it.setTextColor(Color.BLACK)
            it.textSize = 40f
            it.gravity = Gravity.CENTER
        }
    }

    //空白view
    private fun getCourseView1(): TextView {
        return TextView(this).also {
            it.setTextColor(Color.BLACK)
            it.textSize = 20f
            it.gravity = Gravity.CENTER
            it.setPadding(20, 0, 20, 0)
        }
    }

    //获得第一列 时间
    private fun getDateView(str: String): View {
        return layoutInflater.inflate(R.layout.common_course_date, null).also {
            it.findViewById<TextView>(R.id.tv_name).also { tv ->
                tv.text=str
            }
            it.setBackgroundResource(R.drawable.bg_course)
        }
    }


    /**
     * 选择课程
     */
    private fun selectCourse(v: TextView) {
        CourseSelectDialog(this).builder()
            .setOnDialogClickListener { course ->
                if (course != null) {
                    v.text = course
                    val courseBean=CourseBean()
                    courseBean.name=course
                    courseBean.viewId = v.id
                    courseBean.type = type

                    //删除已经存在了的
                    if (selectLists.size > 0) {
                        var it = selectLists.iterator()
                        while (it.hasNext()) {
                            if (it.next().viewId == courseBean.viewId) {
                                it.remove()
                            }
                        }
                    }
                    selectLists.add(courseBean)
                }
            }
    }


    //时间选择器
    private fun selectTime(tvStart: TextView, id: Int) {

        CourseTimeSelectorDialog(this).builder().setOnDateListener {
                startStr, endStr->

            tvStart.text = "$startStr~$endStr"

            val course = CourseBean()
            course.name = "$startStr~$endStr"
            course.viewId = id
            course.type = type

            //删除已经存在了的
            if (selectLists.size > 0) {
                val it = selectLists.iterator()
                while (it.hasNext()) {
                    if (it.next().viewId == id) {
                        it.remove()
                    }
                }
            }
            selectLists.add(course)

        }

    }


    private fun inputContent(v: TextView) {

        InputContentDialog(this,getCurrentScreenPos(),v.text.toString()).builder()
            ?.setOnDialogClickListener { string ->
            v.text = string

            val course = CourseBean()
            course.viewId = v.id
            course.name = string
            course.type = type

            //删除已经存在了的
            if (selectLists.size > 0) {
                val it = selectLists.iterator()
                while (it.hasNext()) {
                    if (it.next().viewId == v.id) {
                        it.remove()
                    }
                }
            }
            selectLists.add(course)
        }

    }


}
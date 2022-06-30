package com.bll.lnkstudy.ui.activity

import android.app.Dialog
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.*
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.CourseSelectDialog
import com.bll.lnkstudy.dialog.CourseTimeDialog
import com.bll.lnkstudy.manager.CourseGreenDaoManager
import com.bll.lnkstudy.mvp.model.CourseBean
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.SystemSettingUtils
import kotlinx.android.synthetic.main.ac_course.*
import org.greenrobot.eventbus.EventBus

//课程表
class MainCourseActivity : BaseActivity() {

    private var type = 0//0五天六节 1六天六节 2五天七节 3六天七节 4五天八节 5六天八节
    private var row = 11
    private var column = 7
    private var isAdd = true //是否是重新编辑课表
    private var selectLists = mutableListOf<CourseBean>()//已经选择了的课程

    private var timeWidth = 60
    private var weekHeight = 102
    private var lessonsWidth = 230
    private var dividerHeight = 52
    private var dividerHeight1 = 76

    private var totalWidth = 1330
    private var totalHeight = 1050

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

        tv_save.setOnClickListener {
            if (selectLists.size == 0) return@setOnClickListener
            CourseGreenDaoManager.getInstance(this@MainCourseActivity).deleteAll()//清除以前存储的课程
            CourseGreenDaoManager.getInstance(this@MainCourseActivity).insertAll(selectLists)
            SystemSettingUtils.saveScreenShot(this, grid, "course")
            EventBus.getDefault().post(Constants.COURSE_EVENT)
            SPUtil.putObj("courseType", type)
            finish()
        }

        addTimeLayout()
        addWeekLayout()
        addLessonsLayout()
        addContentLayout()

    }

    //添加时间布局在第一列
    private fun addTimeLayout() {

        var heightTime1 = 0
        var heightTime2 = 0
            when (type) {
            0, 1 -> {
                heightTime1=weekHeight + dividerHeight + 4 * height
                heightTime2=dividerHeight * 2 + 2 * height

                var view = getDateView(R.mipmap.icon_course_time_1, 200)
                var params = GridLayout.LayoutParams()
                view.setBackgroundResource(R.drawable.bg_course_3)
                params.rowSpec = GridLayout.spec(0, 6)
                params.width = timeWidth
                params.height = heightTime1
                params.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view, params)

                var view1 = getDateView(R.mipmap.icon_course_time_2, 60)
                var params1 = GridLayout.LayoutParams()
                view1.setBackgroundResource(R.drawable.bg_course_3)
                params1.rowSpec = GridLayout.spec(if (type==4||type==5) 6 else 7, row - 7)
                params1.width = timeWidth
                params1.height = heightTime2
                params1.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view1, params1)
            }
            2, 3 -> {
                heightTime1 =weekHeight + dividerHeight + 4 * height
                heightTime2=dividerHeight + 3 * height

                var view = getDateView(R.mipmap.icon_course_time_1, 200)
                var params = GridLayout.LayoutParams()
                view.setBackgroundResource(R.drawable.bg_course_3)
                params.rowSpec = GridLayout.spec(0, 6)
                params.width = timeWidth
                params.height = heightTime1
                params.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view, params)

                var view1 = getDateView(R.mipmap.icon_course_time_2, 80)
                var params1 = GridLayout.LayoutParams()
                view1.setBackgroundResource(R.drawable.bg_course_3)
                params1.rowSpec = GridLayout.spec(if (type==4||type==5) 6 else 7, row - 7)
                params1.width = timeWidth
                params1.height = heightTime2
                params1.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view1, params1)
            }
            else -> {
                heightTime1 =weekHeight + 4 * height
                heightTime2=4 * height

                var view = getDateView(R.mipmap.icon_course_time_1, 200)
                var params = GridLayout.LayoutParams()
                view.setBackgroundResource(R.drawable.bg_course_3)
                params.rowSpec = GridLayout.spec(0, 5)
                params.width = timeWidth
                params.height = heightTime1
                params.columnSpec = GridLayout.spec(0, 1)
                grid.addView(view, params)

                var view1 = getDateView(R.mipmap.icon_course_time_2, 100)
                var params1 = GridLayout.LayoutParams()
                view1.setBackgroundResource(R.drawable.bg_course_3)
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
        var weeks = arrayOf(
            R.mipmap.icon_course_week_0,
            R.mipmap.icon_course_week_1,
            R.mipmap.icon_course_week_2,
            R.mipmap.icon_course_week_3,
            R.mipmap.icon_course_week_4,
            R.mipmap.icon_course_week_5,
            R.mipmap.icon_course_week_6
        )

        for (i in 1 until column) {
            var index = i - 1
            var view = getWeekView(weeks[index])
            view.setBackgroundResource(R.drawable.bg_course_1)

            var widths = if (i == 1) {//如果是第一个，则是课节的宽度
                lessonsWidth
            } else {
                width
            }

            var params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(0, 1)
            params.width = widths
            params.height = weekHeight
            params.columnSpec = GridLayout.spec(i, 1)
            grid.addView(view, params)
        }

    }

    //添加第二列课节布局
    private fun addLessonsLayout() {

        var lessons = when (type) {
            0, 1 -> {
                arrayOf(
                    R.mipmap.icon_course_lessons_1,
                    R.mipmap.icon_course_lessons_2,
                    0,
                    R.mipmap.icon_course_lessons_3,
                    R.mipmap.icon_course_lessons_4,
                    0,
                    R.mipmap.icon_course_lessons_5,
                    0,
                    R.mipmap.icon_course_lessons_6,
                    0
                )
            }
            2, 3 -> {
                arrayOf(
                    R.mipmap.icon_course_lessons_1,
                    R.mipmap.icon_course_lessons_2,
                    0,
                    R.mipmap.icon_course_lessons_3,
                    R.mipmap.icon_course_lessons_4,
                    0,
                    R.mipmap.icon_course_lessons_5,
                    R.mipmap.icon_course_lessons_6,
                    R.mipmap.icon_course_lessons_7,
                    0
                )
            }
            else -> {
                arrayOf(
                    R.mipmap.icon_course_lessons_1,
                    R.mipmap.icon_course_lessons_2,
                    R.mipmap.icon_course_lessons_3,
                    R.mipmap.icon_course_lessons_4,
                    0,
                    R.mipmap.icon_course_lessons_5,
                    R.mipmap.icon_course_lessons_6,
                    R.mipmap.icon_course_lessons_7,
                    R.mipmap.icon_course_lessons_8
                )
            }
        }

        for (i in 1 until row) {
            var id = "1$i".toInt()
            //根据id 查询是否已经存储了对应的时间
            var course = CourseGreenDaoManager.getInstance(this@MainCourseActivity).queryID(id)
            var index = i - 1

            var view = getLessonsView(lessons[index])

            view.id = id
            var tvTime = view.findViewById<TextView>(R.id.tv_time)
            view.setOnClickListener {
                selectTime(tvTime, id)
            }

            //不重置
            if (!isAdd) {
                if (course != null) {
                    tvTime.text = course.name.toString()
                    selectLists.add(course)//将已经存在的加入课程集合
                }
            }

            var params = GridLayout.LayoutParams()
            params.width = lessonsWidth

            if (type == 0 || type == 1) {

                when (i) {
                    3, 8, 10 -> {
                        view.setBackgroundResource(R.drawable.bg_course_3)
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = dividerHeight
                        params.columnSpec = GridLayout.spec(1, column - 1)
                    }
                    6 -> {
                        view.setBackgroundResource(R.drawable.bg_course_3)
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = dividerHeight1
                        params.columnSpec = GridLayout.spec(0, column)
                    }
                    else -> {
                        view.setBackgroundResource(R.drawable.bg_course_2)
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = height
                        params.columnSpec = GridLayout.spec(1, 1)
                    }
                }

            } else if (type == 2 || type == 3) {

                when (i) {
                    3, 10 -> {
                        view.setBackgroundResource(R.drawable.bg_course_3)
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = dividerHeight
                        params.columnSpec = GridLayout.spec(1, column - 1)
                    }
                    6 -> {
                        view.setBackgroundResource(R.drawable.bg_course_3)
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = dividerHeight1
                        params.columnSpec = GridLayout.spec(0, column)
                    }
                    else -> {
                        view.setBackgroundResource(R.drawable.bg_course_2)
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = height
                        params.columnSpec = GridLayout.spec(1, 1)
                    }
                }

            } else {

                when (i) {
                    5 -> {
                        view.setBackgroundResource(R.drawable.bg_course_3)
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = dividerHeight1
                        params.columnSpec = GridLayout.spec(0, column)
                    }
                    else -> {
                        view.setBackgroundResource(R.drawable.bg_course_2)
                        params.rowSpec = GridLayout.spec(i, 1)
                        params.height = height
                        params.columnSpec = GridLayout.spec(1, 1)
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
                var id = (i.toString() + j.toString()).toInt()
                //根据textview id 查询是否已经存储了对应的课程
                var course = CourseGreenDaoManager.getInstance(this@MainCourseActivity).queryID(id)

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

                    var params = GridLayout.LayoutParams()
                    when (j) {
                        3, 8, 10 -> {
                            view.setBackgroundResource(R.drawable.bg_course_3)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth
                            params.height = dividerHeight
                            params.columnSpec = GridLayout.spec(1, column - 1)
                        }
                        6 -> {
                            view.setBackgroundResource(R.drawable.bg_course_3)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth + timeWidth
                            params.height = dividerHeight1
                            params.columnSpec = GridLayout.spec(0, column)
                        }
                        else -> {
                            view.setBackgroundResource(R.drawable.bg_course_2)
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

                    var params = GridLayout.LayoutParams()
                    when (j) {
                        3, 10 -> {
                            view.setBackgroundResource(R.drawable.bg_course_3)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth
                            params.height = dividerHeight
                            params.columnSpec = GridLayout.spec(1, column - 1)
                        }
                        6 -> {
                            view.setBackgroundResource(R.drawable.bg_course_3)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth + timeWidth
                            params.height = dividerHeight1
                            params.columnSpec = GridLayout.spec(0, column)
                        }
                        else -> {
                            view.setBackgroundResource(R.drawable.bg_course_2)
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



                    var params = GridLayout.LayoutParams()
                    when (j) {
                        5 -> {
                            view.setBackgroundResource(R.drawable.bg_course_3)
                            params.rowSpec = GridLayout.spec(j, 1)
                            params.width = width * (column - 2) + lessonsWidth + timeWidth
                            params.height = dividerHeight1
                            params.columnSpec = GridLayout.spec(0, column)
                        }
                        else -> {
                            view.setBackgroundResource(R.drawable.bg_course_2)
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
    private fun getLessonsView(resId: Int): View {
        var view = layoutInflater.inflate(R.layout.common_course_lessons, null)
        var ivName = view.findViewById<ImageView>(R.id.iv_name)
        ivName.setImageResource(resId)

        return view
    }

    //获取星期
    private fun getWeekView(resId: Int): View {
        var view = ImageView(this)
        view.setImageResource(resId)
        view.scaleType = ImageView.ScaleType.CENTER
        view.setPadding(0,0,0,10)
        return view
    }

    //获得课程
    private fun getCourseView(): TextView {
        var view = TextView(this)
        view.setTextColor(Color.BLACK)
        view.textSize = 46f
        view.gravity = Gravity.CENTER
        return view
    }

    //空白view
    private fun getCourseView1(): TextView {
        var view = TextView(this)
        view.setTextColor(Color.BLACK)
        view.textSize = 20f
        view.gravity = Gravity.CENTER
        view.setPadding(20, 0, 20, 0)
        return view
    }

    //获得第一列 时间
    private fun getDateView(resId: Int, padding: Int): View {
        var view = layoutInflater.inflate(R.layout.common_course_date, null)
        var tv1 = view.findViewById<ImageView>(R.id.tv_1)
        tv1.setImageResource(resId)
        tv1.setPadding(0, 0, 0, padding)

        return view
    }


    /**
     * 选择课程
     */
    private fun selectCourse(v: TextView) {
        CourseSelectDialog(this).builder()
            .setOnDialogClickListener(object : CourseSelectDialog.OnDialogClickListener {
                override fun onSelect(course: CourseBean) {
                    if (course != null) {
                        v.text = course.name
                        course.viewId = v.id
                        course.type=type

                        //删除已经存在了的
                        if (selectLists.size > 0) {
                            var it = selectLists.iterator()
                            while (it.hasNext()) {
                                if (it.next().viewId == course.viewId) {
                                    it.remove()
                                }
                            }
                        }
                        selectLists.add(course)
                    }
                }
            })
    }


    //时间选择器
    private fun selectTime(v: TextView, id: Int) {
        CourseTimeDialog(this).builder()
            .setOnDialogClickListener(object : CourseTimeDialog.OnDialogClickListener {
                override fun onSelect(course: String) {
                    if (course != null) {
                        v.text = course
                        var course1 = CourseBean()
                        course1.name = course
                        course1.viewId = id
                        course1.type=type

                        //删除已经存在了的
                        if (selectLists.size > 0) {
                            var it = selectLists.iterator()
                            while (it.hasNext()) {
                                if (it.next().viewId == id) {
                                    it.remove()
                                }
                            }
                        }
                        selectLists.add(course1)
                    }
                }
            })
    }


    private fun inputContent(v: TextView) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_account_edit_name)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
        val btn_cancel = dialog.findViewById<Button>(R.id.btn_cancel)
        val name = dialog.findViewById<EditText>(R.id.ed_name)
        name.hint = "请输入内容"
        name.setText(v.text.toString())
        dialog.show()
        btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btn_ok.setOnClickListener {
            var content = name.text.toString()
            if (content.isNullOrEmpty()) {
                showToast("请输入内容")
                return@setOnClickListener
            }
            dialog.dismiss()
            v.text = content

            var course = CourseBean()
            course.viewId = v.id
            course.name = content
            course.type=type

            //删除已经存在了的
            if (selectLists.size > 0) {
                var it = selectLists.iterator()
                while (it.hasNext()) {
                    if (it.next().viewId == v.id) {
                        it.remove()
                    }
                }
            }
            selectLists.add(course)

        }
        dialog.setOnDismissListener {
            hideKeyboard()
        }
    }


}
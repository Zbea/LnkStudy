package com.bll.lnkstudy.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import com.bll.lnkstudy.Constants
import com.bll.lnkstudy.R
import com.bll.lnkstudy.base.BaseActivity
import com.bll.lnkstudy.dialog.CourseSelectDialog
import com.bll.lnkstudy.manager.CourseGreenDaoManager
import com.bll.lnkstudy.mvp.model.CourseList
import com.bll.lnkstudy.utils.SPUtil
import com.bll.lnkstudy.utils.StringUtils
import com.bll.lnkstudy.utils.SystemSettingUtils
import kotlinx.android.synthetic.main.ac_course.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.greenrobot.eventbus.EventBus

//课程表
class MainCourseActivity :BaseActivity() {

    private var type=0//0五天六节 1六天六节 2五天七节 3六天七节 4五天八节 5六天八节
    private var row=9
    private var column=6
    private var isAdd=true //是否是重新编辑课表
    private var selectLists= mutableListOf<CourseList>()//已经选择了的课程

    override fun layoutId(): Int {
        return R.layout.ac_course
    }

    override fun initData() {
        isAdd=intent.flags==0
        type=intent.getIntExtra("courseType",0)

        when(type){
            0->{//五天六节课
                row=9
                column=6
            }
            1->{//六天六节课
                row=9
                column=7
            }
            2->{//五天七节课
                row=10
                column=6
            }
            3->{//六天七节课
                row=10
                column=7
            }
            4->{//五天八节课
                row=11
                column=6
            }
            5->{//六天八节课
                row=11
                column=7
            }
        }
        grid.columnCount=column
        grid.rowCount=row
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {

        for (i in 1..column)
        {
            for (j in 1..row){
                var textView = TextView(this)
                var id=(i.toString()+j.toString()).toInt()
                textView.id=id
                textView.setTextColor(Color.BLACK)
                textView.textSize=20f
                textView.gravity=Gravity.CENTER
                textView.setOnClickListener {
                    selectCourse(textView)
                }

                var params = GridLayout.LayoutParams()
                //第一列不可点击
                if (i==1){
                    textView.isClickable=false
                    textView.text=when(j){
                        1->{
                            "课程"
                        }
                        4,7->{
                            ""
                        }
                        else->{
                            if (j in 5..7){
                                var co=j-2
                                "第 $co 节"
                            }
                            else if (j>7)
                            {
                                var co=j-3
                                "第 $co 节"
                            }
                            else{
                                var co=j-1
                                "第 $co 节"
                            }
                        }
                    }
                }

                //第一行、留白处不可点击
                if(j==1||j==4||j==7){
                    textView.isClickable=false
                }
                //第一行（除开第一列）内容填写成星期
                if (i>1&&j==1){
                    textView.text=StringUtils.intToWeek(i-1)
                }

                when (j){
                    1->{
                        textView.setBackgroundResource(R.drawable.bg_course_1)
                        params.rowSpec = GridLayout.spec( j-1,1)
                        params.width=200
                        params.height=80
                        params.columnSpec=GridLayout.spec( i-1,1)
                    }
                    4,7->{
                        textView.setBackgroundResource(R.drawable.bg_course_3)
                        params.rowSpec = GridLayout.spec( j-1,1)
                        params.width=200*column
                        params.height=40
                        params.columnSpec=GridLayout.spec( 0,column)
                        showLog(id.toString())
                    }
                    else->{
                        textView.setBackgroundResource(R.drawable.bg_course_2)
                        params.rowSpec = GridLayout.spec( j-1,1)
                        params.width=200
                        params.height=110
                        params.columnSpec=GridLayout.spec( i-1,1)
                    }
                }
                //不重置
                if (!isAdd){
                    //根据textview id 查询是否已经存储了对应的课程
                    var course=CourseGreenDaoManager.getInstance(this@MainCourseActivity).queryID(id)
                    if (course!=null)
                    {
                        textView.text=course.name
                        selectLists.add(course)//将已经存在的加入课程集合
                    }
                }
                grid.addView(textView, params)
            }
        }

        tv_save.setOnClickListener {
            if (selectLists.size==0)return@setOnClickListener
            CourseGreenDaoManager.getInstance(this@MainCourseActivity).deleteAll()//清除以前存储的课程
            CourseGreenDaoManager.getInstance(this@MainCourseActivity).insertAll(selectLists)
            SystemSettingUtils.saveScreenShot(this,grid,"course")
            EventBus.getDefault().post(Constants.COURSE_EVENT)
            SPUtil.putObj("courseType",type)
        }

    }

    /**
     * 选择课程
     */
     private fun selectCourse(v: TextView){
         CourseSelectDialog(this).builder().setOnDialogClickListener(object : CourseSelectDialog.OnDialogClickListener {
             override fun onSelect(course: CourseList) {
                 if (course!=null){
                     v.text=course.name
                     course.viewId=v.id

                     //删除已经存在了的
                     if (selectLists.size>0){
                         var it=selectLists.iterator()
                         while (it.hasNext()){
                             if(it.next().viewId==course.viewId)
                             {
                                 it.remove()
                             }
                         }
                     }

                     selectLists.add(course)
//                     //选择后保存数据库
//                     CourseGreenDaoManager.getInstance(this@MainCourseActivity).insertOrReplaceCourse(course)
                 }
             }
         })
     }


}
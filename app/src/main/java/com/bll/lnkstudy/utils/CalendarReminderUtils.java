package com.bll.lnkstudy.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.bll.lnkstudy.mvp.model.date.DateWeek;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


/**
 * 添加日历事件
 */
public class CalendarReminderUtils {
    private static String CALENDER_URL = "content://com.android.calendar/calendars";
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = "LnkCalender";
    private static String CALENDARS_ACCOUNT_NAME = "lnkstudy";
    private static String CALENDARS_ACCOUNT_TYPE = "com.bll.lnkstudy";
    private static String CALENDARS_DISPLAY_NAME = "BLL";

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    @SuppressLint("Range")
    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALENDER_URL), null, null, null, null);
        try {
            if (userCursor == null) { //查询返回空值
                return -1;
            }
            int count = userCursor.getCount();
            if (count > 0) { //存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */

    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void addCalendarEvent(Context context, String title, String time,long startDate, long endDate, List<DateWeek> weeks) {
        int calId = checkAndAddCalendarAccount(context); //获取日历账户的id
        if (calId < 0) { //获取账户id失败直接返回，添加日历事件失败
            return;
        }
        //添加日历事件
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(DateUtils.date3Stamp(DateUtils.longToStringDataNoHour(startDate)+" "+time));//设置开始时间
        long start = mCalendar.getTime().getTime();
        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("calendar_id", calId); //插入账户的id
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DURATION, "P60S");
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        String weekStr="WKST=SU;BYDAY=";
        for (int i = 0; i < weeks.size(); i++) {
            if (i==weeks.size()-1){
                weekStr=weekStr+weeks.get(i).identify;
            }
            else {
                weekStr=weekStr+weeks.get(i).identify+",";
            }
        }
        String dateStr="UNTIL="+DateUtils.longToStringCalender(endDate)+"T235959Z;";

        event.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;"+dateStr+weekStr);

        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");//这个是时区，必须有
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALENDER_EVENT_URL), event); //添加事件
        if (newEvent == null) { //添加日历事件失败直接返回
            return;
        }

        //扩展属性 用于高版本安卓系统设置闹钟提醒
        Uri extendedPropUri = CalendarContract.ExtendedProperties.CONTENT_URI;
        extendedPropUri = extendedPropUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE).build();
        ContentValues extendedProperties = new ContentValues();
        extendedProperties.put(CalendarContract.ExtendedProperties.EVENT_ID, ContentUris.parseId(newEvent));
        extendedProperties.put(CalendarContract.ExtendedProperties.VALUE, "{\"need_alarm\":true}");
        extendedProperties.put(CalendarContract.ExtendedProperties.NAME, "agenda_info");
        Uri uriExtended = context.getContentResolver().insert(extendedPropUri, extendedProperties);
        if (uriExtended == null) { //添加事件提醒失败直接返回
            return ;
        }

    }

    /**
     * 添加日历重要日子事件
     * @param context
     * @param title
     * @param dateLong
     * @param day 提前天数
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void addCalendarEvent2(Context context, String title, long dateLong, int day) {
        int calId = checkAndAddCalendarAccount(context); //获取日历账户的id
        if (calId < 0) { //获取账户id失败直接返回，添加日历事件失败
            return;
        }

        //添加日历事件
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(dateLong);//设置开始时间
        long start = mCalendar.getTime().getTime();
        ContentValues event = new ContentValues();
        event.put("title", title);
//        event.put("description", description);
        event.put("calendar_id", calId); //插入账户的id
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, start);
        event.put(CalendarContract.Events.ALL_DAY, 1);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
//        if (repeat.equals("每天"))
//        {
//            event.put(CalendarContract.Events.RRULE, "FREQ=DAILY");
//        }
//        if (repeat.equals("每周"))
//        {
//            event.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY");
//        }
//        if (repeat.equals("每月"))
//        {
//            event.put(CalendarContract.Events.RRULE, "FREQ=MONTHLY");
//        }
//        if (repeat.equals("每年"))
//        {
//            event.put(CalendarContract.Events.RRULE, "FREQ=YEARLY");
//        }

        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");//这个是时区，必须有
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALENDER_EVENT_URL), event); //添加事件
        if (newEvent == null) { //添加日历事件失败直接返回
            return;
        }

        //扩展属性 用于高版本安卓系统设置闹钟提醒
        Uri extendedPropUri = CalendarContract.ExtendedProperties.CONTENT_URI;
        extendedPropUri = extendedPropUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE).build();
        ContentValues extendedProperties = new ContentValues();
        extendedProperties.put(CalendarContract.ExtendedProperties.EVENT_ID, ContentUris.parseId(newEvent));
        extendedProperties.put(CalendarContract.ExtendedProperties.VALUE, "{\"need_alarm\":true}");
        extendedProperties.put(CalendarContract.ExtendedProperties.NAME, "agenda_info");
        Uri uriExtended = context.getContentResolver().insert(extendedPropUri, extendedProperties);
        if (uriExtended == null) { //添加事件提醒失败直接返回
            return ;
        }

        int leadTime=day*24*60; //(上午九点)
        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        values.put(CalendarContract.Reminders.MINUTES, leadTime);// 提前多少分钟提醒
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(CALENDER_REMINDER_URL), values);
        if (uri == null) { //添加事件提醒失败直接返回
            return;
        }

    }

    /**
     * 检查日历事件
     *
     * @param context
     * @param title
     */
    @SuppressLint("Range")
    public static boolean checkCalendarEvent(Context context, String title, String description, long startTime) {
        if (context == null) {
            return false;
        }
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
        try {
            if (eventCursor == null) { //查询返回空值
                return false;
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                String eventTitle = "";
                String eventDescription = "";
                long eventStartTime;
                long eventEndTime;
                while (eventCursor.moveToNext()) {
                    eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    eventDescription = eventCursor.getString(eventCursor.getColumnIndex("description"));
                    eventStartTime = Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtstart")));
                    eventEndTime = Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtend")));
                    if ((title != null && title.equals(eventTitle)) && (description != null && description.equals(eventDescription)) && (startTime == eventStartTime) && (startTime == eventEndTime)) {
                        return true;
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        return false;
    }

    /**
     * 删除日历事件
     */
    @SuppressLint("Range")
    public static void deleteCalendarEvent(Context context, String title) {
        if (context == null) {
            return;
        }
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
        try {
            if (eventCursor == null) { //查询返回空值
                return;
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDER_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) { //事件删除失败
                            return;
                        }
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }
}
package com.bll.lnkstudy.utils.date;

public class CalenderUtils {
    String date;
    String[] dateArray;
    // 年份
    int year;
    // 月份
    int month;
    // 日期
    int day;
    // 总天数
    int allDay = 365;

    public CalenderUtils(String date) {
        this.date = date;
        this.dateArray = date.trim().split("-");
        this.year = Integer.parseInt(dateArray[0]);
        this.month = Integer.parseInt(dateArray[1]);
        this.day = Integer.parseInt(dateArray[2]);
    }

    // 判断是闰年还是平年
    public boolean year(int year) {
        boolean judge = false;
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            allDay = 366;
            judge = true;
        }
        return judge;
    }

    // 输入月份返回共有多少天
    public int month(int year, int month) {
        int day = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                day = 30;
                break;
            case 2:
                if (year(year)) {
                    day = 29;
                } else {
                    day = 28;
                }
                break;
        }
        return day;
    }

    // 计算在输入的月份里还剩多少天
    public int day(int year, int month, int day) {
        int daysLeft = month(year, month) - day;
        return daysLeft;
    }

    // 输入指定日期，返回已经过去了多少天
    public int elapsedTime() {
        // 计算经历过的天数
        int elapsedTime = day;
        for (int i = 1; i < this.month; i++) {
            elapsedTime = elapsedTime + month(this.year, i);
        }
        return elapsedTime;
    }

    // 输入指定日期，返回还剩多少天
    public int daysLeft() {
        // 计算剩下的天数
        int daysLeft = allDay - elapsedTime();
        return daysLeft;
    }
}

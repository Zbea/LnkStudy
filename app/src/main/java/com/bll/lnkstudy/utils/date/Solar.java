package com.bll.lnkstudy.utils.date;


public class Solar {
    public int solarDay;
    public int solarMonth;
    public int solarYear;
    public boolean isSFestival;
    public String solarFestivalName;//公历节日
    public String solar24Term;//24节气

    @Override
    public String toString() {
        return "Solar{" +
                "solarDay=" + solarDay +
                ", solarMonth=" + solarMonth +
                ", solarYear=" + solarYear +
                '}';
    }
}
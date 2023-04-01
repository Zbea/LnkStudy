package com.bll.lnkstudy.utils.greendao;

import com.bll.lnkstudy.mvp.model.date.DateWeek;
import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.List;

public class DateWeekConverter implements PropertyConverter<List<DateWeek>, String> {
    @Override
    public List<DateWeek> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        String[] list_str = databaseValue.split("~");
        List<DateWeek> list_transport = new ArrayList<>();
        for (String s : list_str) {
            list_transport.add(new Gson().fromJson(s, DateWeek.class));
        }
        return list_transport;
    }

    @Override
    public String convertToDatabaseValue(List<DateWeek> arrays) {
        if (arrays == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (DateWeek array : arrays) {
                String str = new Gson().toJson(array, DateWeek.class);
                sb.append(str);
                sb.append("~");
            }
            return sb.toString();
        }
    }
}
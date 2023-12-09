package com.bll.lnkstudy.utils.greendao;

import com.bll.lnkstudy.mvp.model.date.DateWeek;
import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DateLongConverter implements PropertyConverter<List<Long>, String> {
    @Override
    public List<Long> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        String[] list_str = databaseValue.split("~");
        List<Long> list_transport = new ArrayList<>();
        for (String s : list_str) {
            list_transport.add(new Gson().fromJson(s, Long.class));
        }
        return list_transport;
    }

    @Override
    public String convertToDatabaseValue(List<Long> arrays) {
        if (arrays == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (Long array : arrays) {
                String str = new Gson().toJson(array, Long.class);
                sb.append(str);
                sb.append("~");
            }
            return sb.toString();
        }
    }
}
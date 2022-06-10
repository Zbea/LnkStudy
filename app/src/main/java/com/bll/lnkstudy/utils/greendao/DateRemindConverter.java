package com.bll.lnkstudy.utils.greendao;

import com.bll.lnkstudy.mvp.model.DateRemind;
import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.List;

public class DateRemindConverter implements PropertyConverter<List<DateRemind>, String> {
    @Override
    public List<DateRemind> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        String[] list_str = databaseValue.split("~");
        List<DateRemind> list_transport = new ArrayList<>();
        for (String s : list_str) {
            list_transport.add(new Gson().fromJson(s, DateRemind.class));
        }
        return list_transport;
    }

    @Override
    public String convertToDatabaseValue(List<DateRemind> arrays) {
        if (arrays == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (DateRemind array : arrays) {
                String str = new Gson().toJson(array,DateRemind.class);
                sb.append(str);
                sb.append("~");
            }
            return sb.toString();
        }
    }
}
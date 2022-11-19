package com.bll.lnkstudy.utils.greendao;

import com.bll.lnkstudy.mvp.model.DatePlanBean;
import com.bll.lnkstudy.mvp.model.DateWeekBean;
import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.List;

public class DateWeekConverter implements PropertyConverter<List<DateWeekBean>, String> {
    @Override
    public List<DateWeekBean> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        String[] list_str = databaseValue.split("~");
        List<DateWeekBean> list_transport = new ArrayList<>();
        for (String s : list_str) {
            list_transport.add(new Gson().fromJson(s, DateWeekBean.class));
        }
        return list_transport;
    }

    @Override
    public String convertToDatabaseValue(List<DateWeekBean> arrays) {
        if (arrays == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (DateWeekBean array : arrays) {
                String str = new Gson().toJson(array,DateWeekBean.class);
                sb.append(str);
                sb.append("~");
            }
            return sb.toString();
        }
    }
}
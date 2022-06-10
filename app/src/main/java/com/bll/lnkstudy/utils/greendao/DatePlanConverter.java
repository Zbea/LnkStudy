package com.bll.lnkstudy.utils.greendao;

import com.bll.lnkstudy.mvp.model.DatePlanBean;
import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.List;

public class DatePlanConverter implements PropertyConverter<List<DatePlanBean>, String> {
    @Override
    public List<DatePlanBean> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        String[] list_str = databaseValue.split("~");
        List<DatePlanBean> list_transport = new ArrayList<>();
        for (String s : list_str) {
            list_transport.add(new Gson().fromJson(s, DatePlanBean.class));
        }
        return list_transport;
    }

    @Override
    public String convertToDatabaseValue(List<DatePlanBean> arrays) {
        if (arrays == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (DatePlanBean array : arrays) {
                String str = new Gson().toJson(array,DatePlanBean.class);
                sb.append(str);
                sb.append("~");
            }
            return sb.toString();
        }
    }
}
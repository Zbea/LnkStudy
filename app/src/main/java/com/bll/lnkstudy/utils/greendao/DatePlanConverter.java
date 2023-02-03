package com.bll.lnkstudy.utils.greendao;

import com.bll.lnkstudy.mvp.model.DatePlan;
import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.List;

public class DatePlanConverter implements PropertyConverter<List<DatePlan>, String> {
    @Override
    public List<DatePlan> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        String[] list_str = databaseValue.split("~");
        List<DatePlan> list_transport = new ArrayList<>();
        for (String s : list_str) {
            list_transport.add(new Gson().fromJson(s, DatePlan.class));
        }
        return list_transport;
    }

    @Override
    public String convertToDatabaseValue(List<DatePlan> arrays) {
        if (arrays == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (DatePlan array : arrays) {
                String str = new Gson().toJson(array, DatePlan.class);
                sb.append(str);
                sb.append("~");
            }
            return sb.toString();
        }
    }
}
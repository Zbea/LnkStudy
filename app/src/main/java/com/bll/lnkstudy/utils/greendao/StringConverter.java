package com.bll.lnkstudy.utils.greendao;

import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.List;

public class StringConverter implements PropertyConverter<List<String>, String> {
    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        String[] list_str = databaseValue.split("~");
        List<String> list_transport = new ArrayList<>();
        for (String s : list_str) {
            list_transport.add(new Gson().fromJson(s, String.class));
        }
        return list_transport;
    }

    @Override
    public String convertToDatabaseValue(List<String> arrays) {
        if (arrays == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (String array : arrays) {
                String str = new Gson().toJson(array,String.class);
                sb.append(str);
                sb.append("~");
            }
            return sb.toString();
        }
    }
}
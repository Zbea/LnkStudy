package com.bll.lnkstudy.utils.greendao;

import android.util.Log;

import com.bll.lnkstudy.mvp.model.NoteBook;
import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.List;

public class NoteBookConverter implements PropertyConverter<List<NoteBook>, String> {
    @Override
    public List<NoteBook> convertToEntityProperty(String databaseValue) {
        if (databaseValue.isEmpty()) {
            return null;
        }
        String[] list_str = databaseValue.split("~");
        List<NoteBook> list_transport = new ArrayList<>();
        for (String s : list_str) {
            list_transport.add(new Gson().fromJson(s, NoteBook.class));
        }
        return list_transport;
    }

    @Override
    public String convertToDatabaseValue(List<NoteBook> arrays) {
        if (arrays.isEmpty()) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (NoteBook array : arrays) {
                String str = new Gson().toJson(array,NoteBook.class);
                sb.append(str);
                sb.append("~");
            }
            return sb.toString();
        }
    }
}
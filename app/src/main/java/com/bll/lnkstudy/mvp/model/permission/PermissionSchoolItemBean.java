package com.bll.lnkstudy.mvp.model.permission;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PermissionSchoolItemBean implements Serializable {

    public boolean isAllowBook;
    public boolean isAllowVideo;
    public boolean isAllowPainting;
    public List<Integer> weeks;
    public List<TimeBean>  limitTime;
    public Map<String,Boolean> dateMap;

    public static class TimeBean{
        public String startTime;
        public String endTime;
    }

}

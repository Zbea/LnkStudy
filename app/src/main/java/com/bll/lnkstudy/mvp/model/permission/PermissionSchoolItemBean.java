package com.bll.lnkstudy.mvp.model.permission;

import java.io.Serializable;
import java.util.List;

public class PermissionSchoolItemBean implements Serializable {

    public boolean isAllowBook;
    public boolean isAllowVideo;

    public boolean isAllowPainting;
    public List<Integer> weeks;
    public List<TimeBean>  limitTime;

    public class TimeBean{
        public String startTime;
        public String endTime;
    }

}

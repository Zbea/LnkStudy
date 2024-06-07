package com.bll.lnkstudy.mvp.model.permission;

import java.util.List;

public class PermissionSchoolBean {
    public boolean isAllowBook;
//    public List<PermissionTimesBean> bookList;
    public boolean isAllowVideo;
//    public List<PermissionTimesBean>  videoList;
   public boolean isAllowPainting;

    public List<Integer> weeks;
    public String[] startTime;
    public String[] endTime;
}

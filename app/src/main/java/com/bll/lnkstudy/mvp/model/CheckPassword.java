package com.bll.lnkstudy.mvp.model;

import java.io.Serializable;

public class CheckPassword implements Serializable {

    public String question;//密保
    public String answer;//答案
    public String password;
    public boolean isSet;//设置密码

}

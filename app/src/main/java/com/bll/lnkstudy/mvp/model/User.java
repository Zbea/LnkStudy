package com.bll.lnkstudy.mvp.model;

public class User {

    public int id;
    public String token;
    public String account;
    public int role;
    public long accountId;
    public String telNumber;
    public String nickname;
    public int balance;//学豆
    public int vipExpiredAt;//vip时间
    public int grade=4;//年级

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", account='" + account + '\'' +
                ", role=" + role +
                ", accountId=" + accountId +
                ", telNumber='" + telNumber + '\'' +
                ", nickname='" + nickname + '\'' +
                ", balance=" + balance +
                ", vipExpiredAt=" + vipExpiredAt +
                '}';
    }
}

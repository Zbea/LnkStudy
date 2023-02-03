package com.bll.lnkstudy.mvp.model;

public class AccountOrder {

    //学豆支付订单
    public String outTradeNo;
    public String qrCode;
    /**
     * status : 状态: 1 新增; 2 成功支付; 3 支付失败; 4 其它错误
     */
    public int status;
    public int amount;

}

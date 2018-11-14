package com.shoppay.wyoilnew.bean;

/**
 * Created by songxiaotao on 2017/7/4.
 */

public class VipInfo {

    public String MemID;//	会员ID
    public String MemCard;//		会员卡号
    public String MemName;//	会员姓名
    public String MemLevelID;//		会员等级ID
    public String MemShopID;//		会员开卡店铺ID
    public String MemPoint;//
    public String MemMoney;//
    public String ClassRechargePointRate;
    public int MemState; //0：正常 1：锁定 2：挂失
    public int  CardType; //卡类型 0父卡；1子卡；2个人
    public String       MemCardNumber;//车牌号
    public boolean       IsMustSwingCard;//是否必须刷卡
    public int           PayType;//支付方式 0余额；1现金





}

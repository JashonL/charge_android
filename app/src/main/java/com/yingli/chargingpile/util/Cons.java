package com.yingli.chargingpile.util;

import com.yingli.chargingpile.bean.NoConfigBean;
import com.yingli.chargingpile.bean.RegisterMap;
import com.yingli.chargingpile.bean.UserBean;

public class Cons {
    public static String isflagId="ceshi007";
    public static RegisterMap regMap = new RegisterMap();
    public static UserBean userBean;

    public static NoConfigBean noConfigBean;

    public static NoConfigBean getNoConfigBean() {
        return noConfigBean;
    }

    public static void setNoConfigBean(NoConfigBean noConfigBean) {
        Cons.noConfigBean = noConfigBean;
    }
}

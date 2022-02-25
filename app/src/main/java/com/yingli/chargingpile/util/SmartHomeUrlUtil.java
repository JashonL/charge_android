package com.yingli.chargingpile.util;

/**
 * Created by Administrator on 2018/6/12.
 */

public class SmartHomeUrlUtil {

//    public static final String SMARTHOME_BASE_URL = "http://chat.growatt.com";

    //欧洲服务器
    public static  String SMARTHOME_BASE_URL = "https://charge.ying-power.cn/ev/version/1.0.0";

    //测试地址
//    public static final String SMARTHOME_BASE_URL = "http://192.168.30.69:8080";

//    public static final String SMARTHOME_BASE_URL = "http://192.168.3.228";



    public static String getServer(){
       return SMARTHOME_BASE_URL;
    }

    public static String postGetChargingList() {
        return SMARTHOME_BASE_URL + "/api/list";
    }

    public static String postGetChargingGunNew() {
        return SMARTHOME_BASE_URL + "/charge/info";
    }

    public static String postGetAuthorizationList() {
        return SMARTHOME_BASE_URL + "/api/userList";
    }

    public static String postAddAuthorizationUser() {
        return SMARTHOME_BASE_URL + "/api/author";
    }

    public static String postDeleteAuthorizationUser() {
        return SMARTHOME_BASE_URL + "/api/deleteAuthor";
    }

    public static String postAddCharging() {
        return SMARTHOME_BASE_URL + "/api/add";
    }

    public static String postUserChargingRecord() {
        return SMARTHOME_BASE_URL + "/api/chargeRecord";
    }

    public static String postSetChargingParams() {
        return SMARTHOME_BASE_URL + "/api/config";
    }

    public static String postRequestChargingParams() {
        return SMARTHOME_BASE_URL + "/api/configInfo";
    }

    public static String postRequestChargingReserveList() {
        return SMARTHOME_BASE_URL + "/api/reserveList";
    }
    public static String postUpdateChargingReservelist() {
        return SMARTHOME_BASE_URL + "/api/updateReserve";
    }
    public static String postRequestReseerveCharging() {
        return SMARTHOME_BASE_URL + "/cmd/";
    }

    public static String postRequestDeleteCharging() {
        return SMARTHOME_BASE_URL + "/api/deleteAuthor";
    }
    public static String postRequestReserveNowList() {
        return SMARTHOME_BASE_URL + "/api/ReserveNow";
    }
    public static String postGetDemoUser() {
        return SMARTHOME_BASE_URL + "/user/glanceUser";
    }

    public static String postGetDemoCode() {
        return SMARTHOME_BASE_URL + "/user/checkCode";
    }

    public static String postRequestSwitchAp() {
        return SMARTHOME_BASE_URL + "/user/appMode";
    }

    public static String postByCmd(){
        return SMARTHOME_BASE_URL+"/api/";
    }

}

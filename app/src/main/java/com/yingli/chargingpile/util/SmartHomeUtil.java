package com.yingli.chargingpile.util;

import android.text.TextUtils;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2018/6/13.
 */

public class SmartHomeUtil {

    /**
     * map数组转成String
     *
     * @param map
     * @return
     */
    public static String mapToJsonString(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }


    public interface OperationListener {
        void sendCommandSucces();

        void sendCommandError(String code, String error);
    }

    /**
     * byte数组转为String
     */

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            String hexString = Integer.toHexString(aByte & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result.append(hexString.toUpperCase());
        }
        return result.toString();
    }

    /**
     * 数据解密密钥
     */
    public static byte[] commonkeys = {(byte) 0x5A, (byte) 0xA5, (byte) 0x5A, (byte) 0xA5};

    public static byte[] decodeKey(byte[] src, byte[] keys) {
        if (src == null) return null;
        for (int j = 0; j < src.length; j++)    // Payload数据做掩码处理
        {
            src[j] = (byte) (src[j] ^ keys[j % 4]);
        }
        return src;
    }

    /**
     * 生成新的密钥
     */
    public static byte[] createKey() {
        Random randomno = new Random();
        byte[] nbyte = new byte[4];
        randomno.nextBytes(nbyte);
        return nbyte;
    }


    /**
     * @param buffer 有效数据数组
     * @return
     */
    public static byte getCheckSum(byte[] buffer) {
        int sum = 0;
        int length = buffer.length;
        for (int i = 0; i < length - 2; i++) {
            sum += (int) buffer[i];
        }
        return (byte) (sum & 0xff);
    }

    /**
     * 字节数组转为普通字符串（ASCII对应的字符）
     *
     * @param bytearray byte[]
     * @return String
     */
    public static String bytetoString(byte[] bytearray) {
        try {
            int length = 0;
            for (int i = 0; i < bytearray.length; ++i) {
                if (bytearray[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(bytearray, 0, length, "ascii");
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 不显示科学计数法
     */
    public static String showDouble(double value) {
        DecimalFormat df = new DecimalFormat("0.#####");
        return df.format(value);
    }


    /**
     * 判断是否是浏览用户
     */

    public static boolean isFlagUser() {
        int auth = Cons.userBean.getAuthnum();
        boolean isflag = false;
        if (auth == 1) {
            isflag = true;
        }
        return isflag;
    }


    /**
     * 获取用户名
     *
     * @return
     */
    public static String getUserId() {
//        return "user02";
//        return Cons.userBean.getId();
        if (Cons.userBean == null) return "";
        if (TextUtils.isEmpty(Cons.userBean.getName())) return "";
        return Cons.userBean.getName();
    }


    /**
     * 获取用户名
     *
     * @return
     */
    public static String getUserName() {
//        return "user02";
//        return Cons.userBean.getId();
        if (Cons.userBean == null) return "";
        if (TextUtils.isEmpty(Cons.userBean.getName())) return "";
        return Cons.userBean.getName();
    }


    /**
     * 获取权限
     *
     * @return
     */
    public static String getUserAuthority() {
//        return "user02";
//        return Cons.userBean.getId();
        if (Cons.userBean == null) return "endUser";
        if (TextUtils.isEmpty(Cons.userBean.getRoleId())) return "endUser";
        return Cons.userBean.getRoleId();
    }


    /**
     * 获取字母列表
     */

    public static List<String> getLetter() {
        List<String> letters = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            char letter = (char) ('A' + i);
            letters.add(String.valueOf(letter));
        }
        return letters;
    }


    /**
     * 获取24小时
     */
    public static List<String> getHours() {
        List<String> hours = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            if (hour < 10) hours.add("0" + hour);
            else hours.add(String.valueOf(hour));
        }
        return hours;
    }

    /**
     * 获取60分钟
     */
    public static List<String> getMins() {
        List<String> getMins = new ArrayList<>();
        for (int min = 0; min < 60; min++) {
            if (min < 10) getMins.add("0" + min);
            else getMins.add(String.valueOf(min));
        }
        return getMins;
    }


    /**
     * 将byte[2]转成byte[2]
     *
     * @return
     */
    public static int byte2Int(byte[] b) {
        int value = 0;
        if (b.length > 0) {
//            value = (b[0] & 0xff << 8) | (b[1] & 0xff);
            value = 0x000000ff & b[0];
            return value;

        }
        return value;
    }


    /**
     * 将int转成byte[2]
     *
     * @param a
     * @return
     */
    public static byte[] int2Byte(int a) {
        byte[] b = new byte[2];

        b[0] = (byte) (a >> 8);
        b[1] = (byte) (a);

        return b;
    }


    /**
     * 获取新语言
     *
     * @return
     */
    public static List<String> getZones() {
        List<String> zones = new ArrayList<>();
        zones.add("UTC-12:00");
        zones.add("UTC-11:00");
        zones.add("UTC-10:00");
        zones.add("UTC-09:00");
        zones.add("UTC-08:00");
        zones.add("UTC-07:00");
        zones.add("UTC-06:00");
        zones.add("UTC-05:00");
        zones.add("UTC-04:00");
        zones.add("UTC-03:00");
        zones.add("UTC-02:00");
        zones.add("UTC-01:00");
        zones.add("UTC+00:00");
        zones.add("UTC+01:00");
        zones.add("UTC+02:00");
        zones.add("UTC+03:00");
        zones.add("UTC+04:00");
        zones.add("UTC+05:00");
        zones.add("UTC+06:00");
        zones.add("UTC+07:00");
        zones.add("UTC+08:00");
        zones.add("UTC+09:00");
        zones.add("UTC+10:00");
        zones.add("UTC+11:00");
        zones.add("UTC+12:00");
        return zones;
    }


    public static String getDescodePassword(String str) {
        String password="000000";
        try {
            if (!TextUtils.isEmpty(str)) {
                byte[] strs = Base64.decode(str);
                byte[] bytes = DecoudeUtil.DESDecrypt(DecoudeUtil.DES_KEY, strs);
                if (bytes != null) {
                    password = new String(bytes).substring(0, 6);
                } else {
                    password = "000000";
                }
            }
        } catch (Exception e) {
            password = "000000";
        }

        return password;
    }


}

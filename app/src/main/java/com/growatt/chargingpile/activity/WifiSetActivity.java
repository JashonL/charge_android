package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.ParamsSetAdapter;
import com.growatt.chargingpile.adapter.WifiSetAdapter;
import com.growatt.chargingpile.bean.ParamsSetBean;
import com.growatt.chargingpile.bean.WiFiRequestMsgBean;
import com.growatt.chargingpile.bean.WifiSetBean;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.growatt.chargingpile.util.SocketClientUtil;
import com.growatt.chargingpile.util.T;
import com.growatt.chargingpile.util.WiFiMsgConstant;
import com.mylhyl.circledialog.CircleDialog;

import org.xutils.common.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiSetActivity extends BaseActivity {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.relativeLayout1)
    RelativeLayout relativeLayout1;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.srl_pull)
    SwipeRefreshLayout srlPull;
    private TextView tvId;
    private TextView tvName;

    private WifiSetAdapter mAdapter;
    private List<WifiSetBean> list = new ArrayList<>();
    private String[] keys;
    private boolean isReceiveSucc = false;

    private String ip;
    private int port;
    private String devId;
    private boolean isAllowed = false;//是否允许进入

    private byte devType;//交流直流
    private byte encryption;//加密类型

    //网络相关设置
    private byte[] ipByte;
    private byte[] gatewayByte;
    private byte[] maskByte;
    private byte[] macByte;
    private byte[] dnsByte;
    //wifi相关设置
    private byte[] ssidByte;
    private byte[] wifiKeyByte;
    private byte[] bltNameByte;
    private byte[] bltPwdByte;
    private byte[] name4GByte;
    private byte[] pwd4GByte;
    private byte[] apn4GByte;
    //url相关配置
    private byte[] urlByte;
    private byte[] hskeyByte;
    private byte[] heatByte;
    private byte[] pingByte;
    private byte[] intervalByte;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String text = "";
            int what = msg.what;
            switch (what) {
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    String message = (String) msg.obj;
                    text = "异常退出：" + message;
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                    text = "连接关闭";
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    text = "连接成功";
                    sendCmdConnect();
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    text = "发送消息";

                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    text = "回应字符串消息";
                    String receiString = (String) msg.obj;
                    LogUtil.d("回应字符串消息" + receiString);
                    break;

                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    text = "回应字节消息";
                    byte[] receiByte = (byte[]) msg.obj;
                    parseReceivData(receiByte);
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    text = "socket已连接";
                    break;
                case SocketClientUtil.SOCKET_SEND:

                    break;
                case 100://恢复按钮点击

                    break;
                case 101:
                    connectSendMsg();
                    break;
                default:

                    break;
            }
            Log.d("liaojinsha", text);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_set);
        ButterKnife.bind(this);
        initIntent();
        initHeaderView();
        initResource();
        initRecyclerView();
        refresh();
        setOnclickListener();
    }

    private void initIntent() {
        ip = getIntent().getStringExtra("ip");
        port = getIntent().getIntExtra("port", -1);
        devId = getIntent().getStringExtra("devId");
    }

    private void initHeaderView() {
        ivLeft.setImageResource(R.drawable.back);
        tvTitle.setText("电桩设置");
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        srlPull.setColorSchemeColors(ContextCompat.getColor(this, R.color.green_1));
        srlPull.setOnRefreshListener(this::refresh);
        srlPull.setEnabled(false);

    }


    private void initRecyclerView() {
        View paramHeadView = LayoutInflater.from(this).inflate(R.layout.item_wifi_set_head, null);
        tvId = paramHeadView.findViewById(R.id.tv_id);
        tvName = paramHeadView.findViewById(R.id.tv_name);
        View view = paramHeadView.findViewById(R.id.ll_name);
        view.setVisibility(View.GONE);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new WifiSetAdapter(list);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.addHeaderView(paramHeadView);
    }


    private void initResource() {
        keys = new String[]{getString(R.string.m155高级设置), getString(R.string.m156充电桩IP), getString(R.string.m157网关), getString(R.string.m158子网掩码), getString(R.string.m159网络MAC地址), getString(R.string.m160服务器URL), getString(R.string.m161DNS地址)
                , getString(R.string.wifi), getString(R.string.wifi_ssid), getString(R.string.wifi_key)};
        for (int i = 0; i < keys.length; i++) {
            WifiSetBean bean = new WifiSetBean();
            if (i == 0 || i == 7) {
                bean.setTitle(keys[i]);
                bean.setType(WifiSetBean.PARAM_TITILE);
            } else {
                bean.setType(WifiSetBean.PARAM_ITEM);
                bean.setKey(keys[i]);
                bean.setValue("");
            }
            list.add(bean);
        }
    }


    /**
     * 刷新界面
     */
    private void refresh() {
        connectSendMsg();
    }


    /**
     * 真正的连接逻辑
     */
    private void connectSendMsg() {
        isReceiveSucc = false;
        connectServer();
    }

    //连接对象
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler, ip, port);
        }
    }

    private void setOnclickListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (isAllowed) return;
                if (position == 0 || position == 7) return;
                WifiSetBean bean = mAdapter.getData().get(position);
                switch (position) {
                    case 1:
                        inputEdit("name", (String) bean.getValue());
                        break;
                    case 2:
                        inputEdit("address", (String) bean.getValue());
                        break;
                    case 3:
                        inputEdit("site", (String) bean.getValue());
                        break;
                    case 4:
                        inputEdit("rate", String.valueOf(bean.getValue()));
                        break;
                    case 5:
                        inputEdit("power", String.valueOf(bean.getValue()));
                        break;
                    case 6:
                        inputEdit("model", (String) bean.getValue());
                        break;
                    case 8:
                        inputEdit("ip", (String) bean.getValue());
                        break;
                    case 9:
                        inputEdit("gateway", (String) bean.getValue());
                        break;
                    case 10:
                        inputEdit("mask", (String) bean.getValue());
                        break;
                    case 11:
                        inputEdit("mac", (String) bean.getValue());
                        break;
                    case 12:
                        inputEdit("host", (String) bean.getValue());
                        break;
                    case 13:
                        inputEdit("dns", (String) bean.getValue());
                        break;
                }
            }
        });
    }


    /**
     * 弹框输入修改内容
     * item 修改项
     */
    private void inputEdit(final String key, final String value) {
        new CircleDialog.Builder()
                .setWidth(0.8f)
                .setTitle(this.getString(R.string.m27温馨提示))
                .setInputHint(value)
                .setNegative(this.getString(R.string.m7取消), null)
                .setPositiveInput(this.getString(R.string.m9确定), (text, v) -> {

                })
                .show(this.getSupportFragmentManager());
    }

    /*发送连接命令*/
    private void sendCmdConnect() {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = WiFiMsgConstant.CONSTANT_MSG_10;
        //加密方式
        byte encryption = WiFiMsgConstant.CONSTANT_MSG_00;
        //指令
        byte cmd = WiFiMsgConstant.CMD_A0;

        /*****有效数据*****/
        byte len = (byte) 38;
        byte[] prayload = new byte[38];

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String time = simpleDateFormat.format(new Date());
        byte[] timeBytes = time.getBytes();
        System.arraycopy(timeBytes, 0, prayload, 0, timeBytes.length);

        byte[] idBytes = new byte[20];
        byte[] idBytesReal = devId.getBytes();
        System.arraycopy(idBytesReal, 0, idBytes, idBytes.length - idBytesReal.length, idBytesReal.length);
        System.arraycopy(idBytes, 0, prayload, timeBytes.length, idBytes.length);

        byte[] key = SmartHomeUtil.commonkeys;
        System.arraycopy(key, 0, prayload, timeBytes.length + idBytes.length, key.length);

//        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, key);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] start = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(prayload)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(start);

        LogUtil.i("连接命令：" + SmartHomeUtil.bytesToHexString(start));
        LogUtil.i("时间：" + time);
        LogUtil.i("时间转成16进制：" + SmartHomeUtil.bytesToHexString(timeBytes));
        LogUtil.i("id：" + SmartHomeUtil.bytesToHexString(timeBytes));
        LogUtil.i("idBytes：" + SmartHomeUtil.bytesToHexString(idBytes));
        LogUtil.i("key：" + SmartHomeUtil.bytesToHexString(timeBytes));
        LogUtil.i("keyBytes：" + SmartHomeUtil.bytesToHexString(key));
    }


    /**
     * 获取设备相关信息参数
     * cmd = 0x01 获取设备信息参数
     * cmd = 0x02 获取设备以太网参数
     * cmd =0x03  获取设备账号密码参数
     * cmd =0x04  获取服务器参数
     */

    private void getDeviceInfo(byte cmd) {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = this.devType;
        //加密方式
        byte encryption = this.encryption;
        //指令
        byte getCmd = cmd;

        /*****有效数据*****/
        byte len = (byte) 1;
        byte[] prayload = new byte[1];
        byte[] getInfo = "1".getBytes();
        System.arraycopy(getInfo, 0, prayload, 0, getInfo.length);
//        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, key);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] infoBytes = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(prayload)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(infoBytes);

        LogUtil.i("连接命令：" + SmartHomeUtil.bytesToHexString(infoBytes));
        LogUtil.i("转成16进制：" + SmartHomeUtil.bytesToHexString(getInfo));
    }


    /*********设置充电桩**********/

    //网络设置
    private void setInternt() {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = this.devType;
        //加密方式
        byte encryption = this.encryption;
        //指令
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_12;

        /*****有效数据*****/
        byte len = (byte) 77;
        byte[] prayload = new byte[77];

        //ip
        System.arraycopy(ipByte, 0, prayload, 0, ipByte.length);
        //网关
        System.arraycopy(gatewayByte, 0, prayload, ipByte.length, gatewayByte.length);
        //掩码
        System.arraycopy(maskByte, 0, prayload, ipByte.length + gatewayByte.length, maskByte.length);
        //mac
        System.arraycopy(macByte, 0, prayload, ipByte.length + gatewayByte.length + maskByte.length, macByte.length);
        //dns
        System.arraycopy(dnsByte, 0, prayload, ipByte.length + gatewayByte.length + maskByte.length + macByte.length, dnsByte.length);


//        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, key);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setInterNet = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(prayload)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setInterNet);
    }

    //设置wifi
    private void setWifi() {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = this.devType;
        //加密方式
        byte encryption = this.encryption;
        //指令
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_12;

        /*****有效数据*****/
        byte len = (byte) 112;
        byte[] prayload = new byte[112];

        //ssid
        System.arraycopy(ssidByte, 0, prayload, 0, ssidByte.length);
        //key
        System.arraycopy(wifiKeyByte, 0, prayload, ssidByte.length, wifiKeyByte.length);
        //蓝牙名称
        System.arraycopy(bltNameByte, 0, prayload, ssidByte.length + wifiKeyByte.length, bltNameByte.length);
        //蓝牙密码
        System.arraycopy(bltPwdByte, 0, prayload, ssidByte.length + wifiKeyByte.length + bltNameByte.length, bltPwdByte.length);
        //4G用户名
        System.arraycopy(name4GByte, 0, prayload, ssidByte.length + wifiKeyByte.length + bltNameByte.length + bltPwdByte.length, name4GByte.length);
        //4G密码
        System.arraycopy(pwd4GByte, 0, prayload, ssidByte.length + wifiKeyByte.length + bltNameByte.length + bltPwdByte.length + name4GByte.length, pwd4GByte.length);
        //4GAPN
        System.arraycopy(apn4GByte, 0, prayload, ssidByte.length + wifiKeyByte.length + bltNameByte.length + bltPwdByte.length + name4GByte.length + pwd4GByte.length, apn4GByte.length);


//        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, key);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setWifi = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(prayload)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setWifi);
    }



    //设置url
    private void setUrl() {
        //头1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //头2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //交流直流
        byte devType = this.devType;
        //加密方式
        byte encryption = this.encryption;
        //指令
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_12;

        /*****有效数据*****/
        byte len = (byte) 102;
        byte[] prayload = new byte[102];

        //url
        System.arraycopy(urlByte, 0, prayload, 0, urlByte.length);
        //key
        System.arraycopy(hskeyByte, 0, prayload, 70, hskeyByte.length);
        //蓝牙名称
        System.arraycopy(heatByte, 0, prayload, 90, heatByte.length);
        //蓝牙密码
        System.arraycopy(pingByte, 0, prayload, 94, pingByte.length);
        //4G用户名
        System.arraycopy(intervalByte, 0, prayload, 100, intervalByte.length);


//        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, key);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setUrl = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(prayload)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setUrl);
    }



    /**********************************解析数据************************************/

    private void parseReceivData(byte[] data) {
        byte cmd = data[4];//指令类型
        switch (cmd) {
            case WiFiMsgConstant.CMD_A0://连接命令
                //电桩类型，直流或者交流
                devType = data[2];
                //加密还是不加密
                encryption = data[3];
                //是否允许进入
                byte allow = data[6];
                if ((int) allow == 0) {
                    isAllowed = false;
                    srlPull.setEnabled(true);
                    T.make("拒绝进入", WifiSetActivity.this);
                } else {
                    srlPull.setEnabled(true);
                    isAllowed = true;
                    T.make("允许进入", WifiSetActivity.this);
                    getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
                }
                break;

            case WiFiMsgConstant.CONSTANT_MSG_01://获取设备信息
                String devId = new String(data, 6, 20);
                tvId.setText(devId);
                getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_02);
                break;
            case WiFiMsgConstant.CONSTANT_MSG_02://获取
                ipByte = new byte[15];
                System.arraycopy(data, 6, ipByte, 0, 15);
                String devIp = new String(ipByte, 0, ipByte.length);
                mAdapter.getData().get(1).setValue(devIp);

                gatewayByte = new byte[15];
                System.arraycopy(data, 21, gatewayByte, 0, 15);
                String gateway = new String(gatewayByte, 0, gatewayByte.length);
                mAdapter.getData().get(2).setValue(gateway);


                maskByte = new byte[15];
                System.arraycopy(data, 36, maskByte, 0, 15);
                String mask = new String(maskByte, 0, maskByte.length);
                mAdapter.getData().get(3).setValue(mask);


                macByte = new byte[17];
                System.arraycopy(data, 51, macByte, 0, 17);
                String mac = new String(macByte, 0, macByte.length);
                mAdapter.getData().get(4).setValue(mac);

                dnsByte = new byte[15];
                System.arraycopy(data, 68, dnsByte, 0, 15);
                String dns = new String(dnsByte, 0, dnsByte.length);
                mAdapter.getData().get(6).setValue(dns);

                mAdapter.notifyDataSetChanged();
                getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_03);
                break;
            case WiFiMsgConstant.CONSTANT_MSG_03:
                ssidByte = new byte[16];
                System.arraycopy(data, 6, ssidByte, 0, 16);
                String ssid = new String(ssidByte, 0, ssidByte.length);
                mAdapter.getData().get(8).setValue(ssid);

                wifiKeyByte = new byte[16];
                System.arraycopy(data, 22, wifiKeyByte, 0, 16);
                String wifikey = new String(wifiKeyByte, 0, wifiKeyByte.length);
                mAdapter.getData().get(9).setValue(wifikey);


                bltNameByte = new byte[16];
                System.arraycopy(data, 6, bltNameByte, 0, 16);

                bltPwdByte = new byte[16];
                System.arraycopy(data, 22, bltPwdByte, 0, 16);

                name4GByte = new byte[16];
                System.arraycopy(data, 70, name4GByte, 0, 16);

                pwd4GByte = new byte[16];
                System.arraycopy(data, 86, pwd4GByte, 0, 16);

                apn4GByte = new byte[16];
                System.arraycopy(data, 102, apn4GByte, 0, 16);


                mAdapter.notifyDataSetChanged();
                getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_04);
                break;
            case WiFiMsgConstant.CONSTANT_MSG_04:
                urlByte=new byte[70];
                System.arraycopy(data, 6, urlByte, 0, 70);
                String url = new String(urlByte, 0, urlByte.length);
                mAdapter.getData().get(5).setValue(url);

                hskeyByte = new byte[16];
                System.arraycopy(data, 76, hskeyByte, 0, 20);

                heatByte=new byte[4];
                System.arraycopy(data, 96, heatByte, 0, 4);

                pingByte=new byte[4];
                System.arraycopy(data, 100, pingByte, 0, 4);

                intervalByte=new byte[4];
                System.arraycopy(data, 104, intervalByte, 0, 4);

                mAdapter.notifyDataSetChanged();
                break;
        }
    }


}

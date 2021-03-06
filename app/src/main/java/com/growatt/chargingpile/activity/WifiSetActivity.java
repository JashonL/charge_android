package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.WifiSetAdapter;
import com.growatt.chargingpile.bean.LockBean;
import com.growatt.chargingpile.bean.SolarBean;
import com.growatt.chargingpile.bean.WiFiRequestMsgBean;
import com.growatt.chargingpile.bean.WifiParseBean;
import com.growatt.chargingpile.bean.WifiSetBean;
import com.growatt.chargingpile.util.Base64;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.DecoudeUtil;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.PickViewUtils;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.growatt.chargingpile.util.SocketClientUtil;
import com.growatt.chargingpile.util.T;
import com.growatt.chargingpile.util.WiFiMsgConstant;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;

import org.xutils.common.util.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class WifiSetActivity extends BaseActivity {

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.srl_pull)
    SwipeRefreshLayout srlPull;

    private WifiSetAdapter mAdapter;
    private List<MultiItemEntity> list = new ArrayList<>();
    public String[] lanArray;
    public String[] rcdArray;
    public String[] modeArray;
    public String[] enableArray;
    public String[] wiringArray;
    public String[] solarArrray;
    public String[] gunArrray;
    public String[] lockArrray;

    public String[] ammterTypeArray;
    public String[] unLockTypeArray;
    public String[] netModeArray;

    private String ip;
    private int port;
    private String devId;
    private boolean isAllowed = false;//??????????????????

    private byte devType;//????????????
    private byte encryption;//????????????
    private String startTime;
    private String endTime;

    //??????????????????
    private byte[] idByte;
    private byte[] lanByte;
    private byte[] cardByte;
    private byte[] rcdByte;
    private byte[] versionByte;
    private byte[] zoneByte;
    private int infoLength;
    //??????????????????
    private byte[] ipByte;
    private byte[] gatewayByte;
    private byte[] maskByte;
    private byte[] macByte;
    private byte[] dnsByte;
    private byte[] netModeByte;
    private int internetLength;
    //wifi????????????
    private byte[] ssidByte;
    private byte[] wifiKeyByte;
//    private byte[] bltNameByte;
    private byte[] bltPwdByte;
    private byte[] name4GByte;
    private byte[] pwd4GByte;
    private byte[] apn4GByte;
    private int wifiLength;
    //url????????????
    private byte[] urlByte;
    private byte[] hskeyByte;
    private byte[] heatByte;
    private byte[] pingByte;
    private byte[] intervalByte;
    private int urlLength;
    //????????????
    private byte[] modeByte;
    private byte[] maxCurrentByte;
    private byte[] rateByte;
    private byte[] tempByte;
    private byte[] powerByte;
    private byte[] timeByte;
    private byte[] chargingEnableByte;
    private byte[] powerdistributionByte;
    private byte[] wiringByte;
    private byte[] solarByte;
    private byte[] solarCurrentByte;
    //    private byte[] currentByte;
    private byte[] ammeterByte;
    private byte[] ammeterTypeByte;
    private byte[] unLockTypeByte;
    private int chargingLength;
    //?????????????????????
    private byte[] lockByte;
    private int lockLength;

    //????????????
    private byte[] oldKey;
    private byte[] newKey;


    //??????????????????
    private boolean isConnected = false;

    private String tips;

    private boolean isEditInfo = false;
    private boolean isEditInterNet = false;
    private boolean isEditWifi = false;
    private boolean isEditUrl = false;
    private boolean isEditCharging = false;


    private WifiParseBean initPileSetBean;

    private List<SolarBean> solarBeans;
    private List<LockBean> lockBeans;


    private int proversion;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            String text = "";
            int what = msg.what;
            switch (what) {
                case SocketClientUtil.SOCKET_EXCETION_CLOSE:
                    String message = (String) msg.obj;
                    text = "???????????????" + message;
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_CLOSE:
                    text = "????????????";
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_OPEN:
                    text = "????????????";
                    isConnected = true;
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_SEND_MSG:
                    text = "????????????";
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_RECEIVE_MSG:
                    text = "?????????????????????";
                    String receiString = (String) msg.obj;
                    Log.d("liaojinsha", text + receiString);
                    break;

                case SocketClientUtil.SOCKET_RECEIVE_BYTES:
                    text = "??????????????????";
                    byte[] receiByte = (byte[]) msg.obj;

                    try {
                        parseReceivData(receiByte);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_CONNECT:
                    text = "socket?????????";
                    Log.d("liaojinsha", text);
                    break;
                case SocketClientUtil.SOCKET_SEND:
                    Log.d("liaojinsha", "socket?????????????????????");
                    this.postDelayed(() -> sendCmdConnect(), 3500);
                    break;
                case 100://??????????????????

                    break;
                case 101:
                    connectSendMsg();
                    break;
                default:

                    break;
            }
            if (srlPull != null)
                srlPull.setRefreshing(false);
        }
    };
    private Unbinder bind;
    private String[] keys;
    private String[] keySfields;
    private List<String> noConfigKeys;
    private boolean isVerified = false;//?????????????????????
    private String password;

    private int gunPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_set);
        bind = ButterKnife.bind(this);
        createNewKey();
        initIntent();
        initHeaderView();
        initResource();
        initRecyclerView();
        connectSendMsg();
        setOnclickListener();
    }

    private void createNewKey() {
        oldKey = SmartHomeUtil.commonkeys;
        newKey = SmartHomeUtil.createKey();
    }

    private void initIntent() {
        ip = getIntent().getStringExtra("ip");
        port = getIntent().getIntExtra("port", -1);
        devId = getIntent().getStringExtra("devId");
    }

    private void initHeaderView() {
        ivLeft.setImageResource(R.drawable.back);
        tvTitle.setText(getString(R.string.m105????????????));
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvRight.setText(R.string.m182??????);
        tvRight.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        srlPull.setColorSchemeColors(ContextCompat.getColor(this, R.color.maincolor_1));
        srlPull.setOnRefreshListener(this::refresh);
    }


    private void initRecyclerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new WifiSetAdapter(list);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }


    private void initResource() {
        keys = new String[]{
                getString(R.string.m255????????????????????????), getString(R.string.m146?????????ID), getString(R.string.m260??????), getString(R.string.m264???????????????), getString(R.string.m265RCD?????????), getString(R.string.m296?????????), getString(R.string.m??????),
                getString(R.string.m256???????????????????????????), getString(R.string.m156?????????IP), getString(R.string.m157??????), getString(R.string.m158????????????), getString(R.string.m159??????MAC??????), getString(R.string.m161DNS??????), getString(R.string.m??????????????????),
                getString(R.string.m257??????????????????????????????), getString(R.string.m266Wifi??????), getString(R.string.m267Wifi??????), getString(R.string.m2704G?????????), getString(R.string.m2714G??????), getString(R.string.m2724GAPN),
                getString(R.string.m258???????????????????????????), getString(R.string.m160?????????URL), getString(R.string.m273????????????????????????), getString(R.string.m274??????????????????), getString(R.string.m275PING????????????), getString(R.string.m276????????????????????????),
                getString(R.string.m259????????????????????????), getString(R.string.m154????????????), getString(R.string.m277????????????????????????), getString(R.string.m152????????????), getString(R.string.m278????????????), getString(R.string.m279??????????????????????????????),
                getString(R.string.m280??????????????????), getString(R.string.m297??????????????????), getString(R.string.m298??????????????????), getString(R.string.m??????????????????????????????), getString(R.string.mSolar??????), getString(R.string.m??????????????????), getString(R.string.m????????????), getString(R.string.m???????????????),
                getString(R.string.m???????????????)
        };

        keySfields = new String[]{"", "chargeId", "G_ChargerLanguage", "G_CardPin", "G_RCDProtection", "G_Version", "TimeZone",
                "", "ip", "gateway", "mask", "mac", "dns", "G_NetworkMode",
                "", "G_WifiSSID", "G_WifiPassword", "G_4GUserName", "G_4GPassword", "G_4GAPN",
                "", "host", "G_Authentication", "G_HearbeatInterval", "G_WebSocketPingInterval", "G_MeterValueInterval",
                "", "G_ChargerMode", "G_MaxCurrent", "rate", "G_MaxTemperature", "G_ExternalLimitPower",
                "G_AutoChargeTime", "G_PeakValleyEnable", "G_ExternalLimitPowerEnable", "G_ExternalSamplingCurWring", "G_SolarMode", "G_PowerMeterAddr", "G_PowerMeterType",
                "UnlockConnectorOnEVSideDisconnect", "G_Lock"};
        if (Cons.getNoConfigBean() != null) {
            noConfigKeys = Cons.getNoConfigBean().getSfield();
            String configWord = Cons.getNoConfigBean().getConfigWord();
            password = SmartHomeUtil.getDescodePassword(configWord);
        }
        if (noConfigKeys == null) noConfigKeys = new ArrayList<>();
        //????????????????????????
        initPileSetBean = new WifiParseBean();
        for (int i = 0; i < keys.length; i++) {
            WifiSetBean bean = new WifiSetBean();
            bean.setIndex(i);
            String keySfield = keySfields[i];
            if ("".equals(keySfield) || "G_Lock".equals(keySfield)) {
                bean.setTitle(keys[i]);
                bean.setType(WifiSetAdapter.PARAM_TITILE);
            } else if ("chargeId".equals(keySfield) || "G_Version".equals(keySfield) || "mac".equals(keySfield)) {
                bean.setType(WifiSetAdapter.PARAM_ITEM_CANT_CLICK);
                bean.setKey(keys[i]);
                bean.setValue("");
            } else {
                bean.setType(WifiSetAdapter.PARAM_ITEM);
                bean.setKey(keys[i]);
                bean.setValue("");
            }
            bean.setSfield(keySfields[i]);
            if (noConfigKeys.contains(bean.getSfield())) {
                bean.setAuthority(false);
            } else {
                bean.setAuthority(true);
            }
            list.add(bean);
        }
        lanArray = new String[]{getString(R.string.m263??????), getString(R.string.m262??????), getString(R.string.m261??????)};
        rcdArray = new String[9];
        for (int i = 0; i < 9; i++) {
            int rcdValue = (i + 1);
            rcdArray[i] = rcdValue + getString(R.string.m287???);
        }
        modeArray = new String[]{getString(R.string.m217????????????), getString(R.string.m218???????????????), getString(R.string.m219????????????)};
        enableArray = new String[]{getString(R.string.m300??????), getString(R.string.m299??????)};
//        wiringArray = new String[]{getString(R.string.mCT), getString(R.string.m??????)};
        wiringArray = new String[]{"CT2000", getString(R.string.m??????),"CT3000"};
        solarArrray = new String[]{"FAST", "ECO", "ECO+"};
        gunArrray = new String[]{getString(R.string.m110A???), getString(R.string.m111B???), getString(R.string.m112C???)};
        lockArrray = new String[]{getString(R.string.m?????????), getString(R.string.m?????????)};
        ammterTypeArray = new String[]{getString(R.string.m?????????), getString(R.string.m??????),"Acrel DDS1352",
                "Acrel DTSD1352(Three)","Eastron SDM230","Eastron SDM630(Three)","Eastron SDM120 MID","Eastron SDM72D MID(Three)","Din-Rail DTSU666 MID(Three)"};
        unLockTypeArray = new String[]{getString(R.string.m??????), getString(R.string.m??????)};
        netModeArray = new String[]{"STATIC", "DHCP"};
        solarBeans = new ArrayList<>();
        lockBeans = new ArrayList<>();
    }


    /*??????TCP??????*/
    private void connectSendMsg() {
        Mydialog.Show(this);
        connectServer();
    }


    private void refresh() {
        if (!isConnected) connectSendMsg();
        else getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
    }


    //????????????
    private SocketClientUtil mClientUtil;

    private void connectServer() {
        mClientUtil = SocketClientUtil.newInstance();
        if (mClientUtil != null) {
            mClientUtil.connect(mHandler, ip, port);
        }
    }

    private void setOnclickListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (!isAllowed) return;
            MultiItemEntity multiItemEntity = mAdapter.getData().get(position);
            if (multiItemEntity == null) return;
            int itemType = multiItemEntity.getItemType();
            if (itemType == WifiSetAdapter.PARAM_ITEM) {
                WifiSetBean bean = (WifiSetBean) mAdapter.getData().get(position);
                if (!bean.isAuthority() && !isVerified) {//????????????????????????????????????????????????
                    showInputPassword(position, WifiSetAdapter.PARAM_ITEM, bean);
                } else {
                    setCommonParams(bean);
                }
            } else if (itemType == WifiSetAdapter.PARAM_ITEM_SOLAR) {//??????solar??????
                String solarMode = MyUtil.ByteToString(solarByte);
                int modeIndext;
                try {
                    modeIndext = Integer.parseInt(solarMode);
                } catch (NumberFormatException e) {
                    modeIndext = 0;
                }
                if (modeIndext != 1) {
                    toast(R.string.m??????ECO????????????);
                    return;
                }
                SolarBean bean = (SolarBean) mAdapter.getData().get(position);
                if (!bean.isAuthority() && !isVerified) {//????????????????????????????????????????????????
                    showInputPassword(position, WifiSetAdapter.PARAM_ITEM_SOLAR, new WifiSetBean());
                } else {
                    setECOLimit(position);
                }
            } else if (itemType == WifiSetAdapter.PARAM_ITEM_LOCK) {
                if (lockLength > 0) {
                    LockBean bean = (LockBean) mAdapter.getData().get(position);
                    gunPos = bean.getIndex();
                    setLock(bean.getGunId());
                } else toast(R.string.m?????????????????????);
            }

        });
    }

    private void setCommonParams(WifiSetBean bean) {
        int index = bean.getIndex();
        String sfield = bean.getSfield();
        switch (sfield) {
            case "G_ChargerLanguage"://????????????
                setLanguage();
                break;
            case "G_RCDProtection":
                setRcd();
                break;
            case "G_ChargerMode":
                setMode();
                break;
            case "G_AutoChargeTime":
                Intent intent = new Intent(this, TimeSelectActivity.class);
                intent.putExtra("start", startTime);
                intent.putExtra("end", endTime);
                startActivityForResult(intent, 100);
                break;
            case "G_PeakValleyEnable":
                if (chargingLength > 24)
                    setEnable(31);
                else toast(R.string.m?????????????????????);
                break;
            case "G_ExternalLimitPowerEnable":
                if (chargingLength > 25)
                    setEnable(32);
                else toast(R.string.m?????????????????????);
                break;
            case "G_ExternalSamplingCurWring":
                if (chargingLength > 26)
                    setWiring();
                else toast(R.string.m?????????????????????);
                break;
            case "G_SolarMode":
                if (chargingLength > 27)
                    setSolarMode();
                else toast(R.string.m?????????????????????);
                break;
            case "G_PowerMeterAddr"://
                if (chargingLength > 30)
                    inputEdit("G_PowerMeterAddr", String.valueOf(bean.getValue()));
                else toast(R.string.m?????????????????????);
                break;
            case "G_PowerMeterType"://
                if (chargingLength > 42)
                    setAmmterType();
                else toast(R.string.m?????????????????????);
                break;
            case "UnlockConnectorOnEVSideDisconnect"://
                if (chargingLength > 43)
                    setLockType();
                else toast(R.string.m?????????????????????);
                break;

            case "G_NetworkMode":
                if (internetLength > 77)
                    setNetMode();
                else toast(R.string.m?????????????????????);
                break;
            case "TimeZone":
                if (infoLength > 52)
                    setZone();
                else toast(R.string.m?????????????????????);
                break;
            default:
                inputEdit(sfield, String.valueOf(bean.getValue()));
                break;
        }
    }


    private void setECOLimit(int position) {
        tips = "";
        SolarBean bean = (SolarBean) mAdapter.getData().get(position);
        String value = bean.getValue();
        new CircleDialog.Builder()
                .setWidth(0.75f)
                .setTitle(this.getString(R.string.m27????????????))
                .setInputHint(tips)
                .setInputCounter(1000, (maxLen, currentLen) -> "")
                .setInputText(value)
                .setNegative(this.getString(R.string.m7??????), null)
                .setPositiveInput(this.getString(R.string.m9??????), new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        if (TextUtils.isEmpty(text)) {
                            toast(R.string.m140????????????);
                            return true;
                        }
                        byte[] bytes = text.trim().getBytes();
                        boolean numeric_eco = MyUtil.isNumberiZidai(text);
                        if (!numeric_eco) {
                            toast(R.string.m177?????????????????????);
                            return true;
                        }


                        System.arraycopy(bytes, 0, solarCurrentByte, 0, bytes.length);
                        isEditCharging = true;
                        bean.setValue(text);
                        mAdapter.notifyDataSetChanged();
                        return true;
                    }
                })
                .show(this.getSupportFragmentManager());

    }


    /**
     * ????????????????????????
     * item ?????????
     */
    private void inputEdit(String key, final String value) {
        tips = "";
        switch (key) {
            case "G_HearbeatInterval":
            case "G_WebSocketPingInterval":
            case "G_MeterValueInterval"://?????????????????????PING?????????????????????????????????
                tips = "5~300(s)";
                break;
            case "G_MaxCurrent"://??????????????????
                tips = getString(R.string.m291?????????????????????);
                break;
            case "G_MaxTemperature"://????????????
                tips = "65~85(???)";
                break;
            case "G_ExternalLimitPower"://??????????????????????????????
                tips = getString(R.string.m291?????????????????????);
                break;
            case "G_PowerMeterAddr":
                tips = getString(R.string.m??????????????????????????????) + " " + 3;
                break;
            default:
                break;
        }
        new CircleDialog.Builder()
                .setWidth(0.75f)
                .setTitle(this.getString(R.string.m27????????????))
                .setInputHint(tips)
                .setInputText(value)
                .setInputCounter(1000, (maxLen, currentLen) -> "")
                .setNegative(this.getString(R.string.m7??????), null)
                .setPositiveInput(this.getString(R.string.m9??????), new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        if (TextUtils.isEmpty(text)) {
                            toast(R.string.m140????????????);
                            return true;
                        }
                        byte[] bytes = text.trim().getBytes();
                        switch (key) {
                            case "G_CardPin":
                                boolean letterDigit_card = MyUtil.isLetterDigit2(text);
                                if (!letterDigit_card) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }
                                if (bytes.length > 6) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                cardByte = new byte[6];
                                System.arraycopy(bytes, 0, cardByte, 0, bytes.length);
                                isEditInfo = true;
                                break;
                            case "ip":
                                boolean b = MyUtil.isboolIp(text);
                                if (!b) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }
                                if (bytes.length > 15) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                ipByte = new byte[15];
                                System.arraycopy(bytes, 0, ipByte, 0, bytes.length);
                                isEditInterNet = true;
                                break;
                            case "gateway":
                                boolean b1 = MyUtil.isboolIp(text);
                                if (!b1) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }
                                if (bytes.length > 15) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                gatewayByte = new byte[15];
                                System.arraycopy(bytes, 0, gatewayByte, 0, bytes.length);
                                isEditInterNet = true;
                                break;
                            case "mask":
                                boolean b2 = MyUtil.isboolIp(text);
                                if (!b2) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }
                                if (bytes.length > 15) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                maskByte = new byte[15];
                                System.arraycopy(bytes, 0, maskByte, 0, bytes.length);
                                isEditInterNet = true;
                                break;
                            case "mac":
                                boolean letterDigit_mac = MyUtil.isLetterDigit2(text);
                                if (!letterDigit_mac) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }
                                if (bytes.length > 17) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                macByte = new byte[17];
                                System.arraycopy(bytes, 0, macByte, 0, bytes.length);
                                isEditInterNet = true;
                                break;
                            case "dns":
                                boolean b3 = MyUtil.isboolIp(text);
                                if (!b3) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }
                                if (bytes.length > 15) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                dnsByte = new byte[15];
                                System.arraycopy(bytes, 0, dnsByte, 0, bytes.length);
                                isEditInterNet = true;
                                break;
                            case "G_WifiSSID":
                                boolean letterDigit_ssid = MyUtil.isWiFiLetter(text);
                                if (!letterDigit_ssid) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }

                                int length = proversion < 10 ? 16 : 30;
                                if (bytes.length > length) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }

                                ssidByte = new byte[length];
                                System.arraycopy(bytes, 0, ssidByte, 0, bytes.length);
                                isEditWifi = true;
                                break;
                            case "G_WifiPassword":
                                boolean letterDigit_key = MyUtil.isWiFiLetter(text);
                                if (!letterDigit_key) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }

                                int length2 = proversion < 10 ? 32 : 64;

                                if (bytes.length > length2) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                wifiKeyByte = new byte[length2];
                                System.arraycopy(bytes, 0, wifiKeyByte, 0, bytes.length);
                                isEditWifi = true;
                                break;

                            case "G_4GUserName":
                                boolean letterDigit_4gname = MyUtil.isWiFiLetter(text);
                                if (!letterDigit_4gname) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }
                                int length3 = proversion < 10 ? 16 : 30;
                                if (bytes.length > length3) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                name4GByte = new byte[length3];
                                System.arraycopy(bytes, 0, name4GByte, 0, bytes.length);
                                isEditWifi = true;
                                break;
                            case "G_4GPassword":
                                boolean letterDigit_4gpwd = MyUtil.isWiFiLetter(text);
                                if (!letterDigit_4gpwd) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }

                                int length4 = proversion < 10 ? 16 : 30;

                                if (bytes.length > length4) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                pwd4GByte = new byte[length4];
                                System.arraycopy(bytes, 0, pwd4GByte, 0, bytes.length);
                                isEditWifi = true;
                                break;
                            case "G_4GAPN":
                                boolean letterDigit_4gapn = MyUtil.isLetterDigit2(text);
                                if (!letterDigit_4gapn) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }


                                int length5 = proversion < 10 ? 16 : 30;
                                if (bytes.length > length5) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                apn4GByte = new byte[length5];
                                System.arraycopy(bytes, 0, apn4GByte, 0, bytes.length);
                                isEditWifi = true;
                                break;

                            case "host":
                                if (bytes.length > 70) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                urlByte = new byte[70];
                                System.arraycopy(bytes, 0, urlByte, 0, bytes.length);
                                isEditUrl = true;
                                break;
                            case "G_Authentication":
                                boolean letterDigit_hskey = MyUtil.isLetterDigit2(text);
                                if (!letterDigit_hskey) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }
                                if (bytes.length > 20) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                hskeyByte = new byte[20];
                                System.arraycopy(bytes, 0, hskeyByte, 0, bytes.length);
                                isEditUrl = true;
                                break;
                            case "G_HearbeatInterval":
                                boolean numeric_heat = MyUtil.isNumberiZidai(text);
                                if (!numeric_heat) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }
                                if (Integer.parseInt(text) < 5 || Integer.parseInt(text) > 300) {
                                    toast(getString(R.string.m290??????????????????) + tips);
                                    return true;
                                }

                                if (bytes.length > 4) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                heatByte = new byte[4];
                                System.arraycopy(bytes, 0, heatByte, 0, bytes.length);
                                isEditUrl = true;
                                break;
                            case "G_WebSocketPingInterval":
                                boolean numeric_ping = MyUtil.isNumberiZidai(text);
                                if (!numeric_ping) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }

                                if (Integer.parseInt(text) < 5 || Integer.parseInt(text) > 300) {
                                    toast(getString(R.string.m290??????????????????) + tips);
                                    return true;
                                }

                                if (bytes.length > 4) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                pingByte = new byte[4];
                                System.arraycopy(bytes, 0, pingByte, 0, bytes.length);
                                isEditUrl = true;
                                break;
                            case "G_MeterValueInterval":
                                boolean numeric_interval = MyUtil.isNumberiZidai(text);
                                if (!numeric_interval) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }

                                if (Integer.parseInt(text) < 5 || Integer.parseInt(text) > 300) {
                                    toast(getString(R.string.m290??????????????????) + tips);
                                    return true;
                                }

                                if (bytes.length > 4) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                intervalByte = new byte[4];
                                System.arraycopy(bytes, 0, intervalByte, 0, bytes.length);
//                            setUrl();
                                isEditUrl = true;
                                break;

                            case "G_MaxCurrent":
                                boolean numeric_maxcurrent = MyUtil.isNumberiZidai(text);
                                if (!numeric_maxcurrent) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }

                                if (Integer.parseInt(text) < 3) {
                                    toast(R.string.m291?????????????????????);
                                    return true;
                                }

                                if (bytes.length > 2) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                maxCurrentByte = new byte[2];
                                System.arraycopy(bytes, 0, maxCurrentByte, 0, bytes.length);
                                isEditCharging = true;
                                break;
                            case "rate":
                                boolean numeric_rate = MyUtil.isNumeric(text);
                                if (!numeric_rate) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }
                                if (bytes.length > 5) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                rateByte = new byte[5];
                                System.arraycopy(bytes, 0, rateByte, 0, bytes.length);
                                isEditCharging = true;
                                break;
                            case "G_MaxTemperature":
                                boolean numeric_temp = MyUtil.isNumberiZidai(text);
                                if (!numeric_temp) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }

                                if (Integer.parseInt(text) < 65 || Integer.parseInt(text) > 85) {
                                    toast(getString(R.string.m290??????????????????) + tips);
                                    return true;
                                }

                                if (bytes.length > 3) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                tempByte = new byte[3];
                                System.arraycopy(bytes, 0, tempByte, 0, bytes.length);
                                isEditCharging = true;
                                break;
                            case "G_ExternalLimitPower":
                                boolean numeric_power = MyUtil.isNumberiZidai(text);
                                if (!numeric_power) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }
                                if (Integer.parseInt(text) < 3) {
                                    toast(R.string.m291?????????????????????);
                                    return true;
                                }
                                if (bytes.length > 2) {
                                    toast(R.string.m286???????????????????????????);
                                    return true;
                                }
                                powerByte = new byte[2];
                                System.arraycopy(bytes, 0, powerByte, 0, bytes.length);
                                isEditCharging = true;
                                break;
                            case "G_PowerMeterAddr":
                                boolean numeric_ammeter = MyUtil.isNumberiZidai(text);
                                if (!numeric_ammeter) {
                                    toast(R.string.m177?????????????????????);
                                    return true;
                                }
                                if (bytes.length > 3) {
                                    toast(getString(R.string.m286???????????????????????????) + " " + 3);
                                    return true;
                                }
                                ammeterByte = new byte[12];
                                System.arraycopy(bytes, 0, ammeterByte, 0, bytes.length);
                                isEditCharging = true;
                                break;
                        }
                        setBean(key, text);
                        mAdapter.notifyDataSetChanged();
                        return true;
                    }
                })
                .configInput(params -> {
                    if ("G_CardPin".equals(key) || "G_WifiPassword".equals(key) || "G_4GPassword".equals(key)) {
                        params.inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                                | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
                    }

                })
                .show(this.getSupportFragmentManager());
    }


    private void setBean(String key, String value) {
        switch (key) {
            case "chargeId":
                initPileSetBean.setDevId(value);
                break;
            case "G_ChargerLanguage":
                initPileSetBean.setLan(value);
                break;
            case "G_CardPin":
                initPileSetBean.setCard(value);
                break;
            case "G_RCDProtection":
                initPileSetBean.setRcd(value);
                break;
            case "G_Version":
                initPileSetBean.setVersion(value);
                break;
            case "ip":
                initPileSetBean.setIp(String.valueOf(value));
                break;
            case "gateway":
                initPileSetBean.setGateway(value);
                break;
            case "mask":
                initPileSetBean.setMask(value);
                break;
            case "mac":
                initPileSetBean.setMac(value);
                break;
            case "dns":
                initPileSetBean.setDns(value);
                break;
            case "G_WifiSSID":
                initPileSetBean.setSsid(value);
                break;
            case "G_WifiPassword":
                initPileSetBean.setWifiKey(value);
                break;
 /*           case 15:
                initPileSetBean.setBltName(value);
                break;
            case 16:
                initPileSetBean.setBltPwd(value);
                break;*/
            case "G_4GUserName":
                initPileSetBean.setName4G(value);
                break;
            case "G_4GPassword":
                initPileSetBean.setPwd4G(value);
                break;
            case "G_4GAPN":
                initPileSetBean.setApn4G(value);
                break;
            case "host":
                initPileSetBean.setUrl(value);
                break;
            case "G_Authentication":
                initPileSetBean.setHskey(value);
                break;
            case "G_HearbeatInterval":
                initPileSetBean.setHeat(value);
                break;
            case "G_WebSocketPingInterval":
                initPileSetBean.setPing(value);
                break;
            case "G_MeterValueInterval":
                initPileSetBean.setInterval(value);
                break;
            case "G_ChargerMode":
                initPileSetBean.setMode(value);
                break;
            case "G_MaxCurrent":
                initPileSetBean.setMaxCurrent(value);
                break;
            case "rate":
                initPileSetBean.setRate(value);
                break;
            case "G_MaxTemperature":
                initPileSetBean.setTemp(value);
                break;
            case "G_ExternalLimitPower":
                initPileSetBean.setPower(value);
                break;
            case "G_AutoChargeTime":
                initPileSetBean.setTime(value);
                break;
            case "G_PeakValleyEnable":
                initPileSetBean.setChargingEnable(value);
                break;
            case "G_ExternalLimitPowerEnable":
                initPileSetBean.setPowerdistribution(value);
                break;
            case "G_ExternalSamplingCurWring":
                initPileSetBean.setWiring(value);
                break;
            case "G_SolarMode":
                initPileSetBean.setSolar(value);
                break;
            case "G_PowerMeterAddr":
                initPileSetBean.setAmmeter(value);
                break;
            case "G_PowerMeterType":
                initPileSetBean.setAmmeterType(value);
                break;
            case "UnlockConnectorOnEVSideDisconnect":
                initPileSetBean.setUnLockType(value);
                break;
            case "G_NetworkMode":
                initPileSetBean.setNetMode(value);
                break;
            case "TimeZone":
                initPileSetBean.setTimezone(value);
                break;
            default:
                break;
        }
        refreshRv();

    }

    private void refreshRv() {
        List<MultiItemEntity> newlist = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            WifiSetBean bean = new WifiSetBean();
            String sfield = keySfields[i];
            bean.setIndex(i);
            switch (sfield) {
                case "":
                    bean.setTitle(keys[i]);
                    bean.setType(WifiSetAdapter.PARAM_TITILE);
                    break;
                case "G_Lock":
                    bean.setTitle(keys[i]);
                    bean.setType(WifiSetAdapter.PARAM_TITILE);
                    if (lockBeans.size() == 0) {
                        LockBean lockBean = new LockBean();
                        lockBean.setValue("");
                        lockBean.setGunId(1);
                        lockBean.setIndex(0);
                        lockBean.setType(WifiSetAdapter.PARAM_ITEM_LOCK);
                        List<String> letter = SmartHomeUtil.getLetter();
                        String name = letter.get(0) + " " + getString(R.string.???);
                        lockBean.setKey(name);
                        lockBeans.add(lockBean);
                    }
                    for (int j = 0; j < lockBeans.size(); j++) {
                        LockBean lockBean = lockBeans.get(j);
                        bean.addSubItem(lockBean);
                    }
                    break;
                case "chargeId":
                    bean.setType(WifiSetAdapter.PARAM_ITEM_CANT_CLICK);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getDevId());
                    break;
                case "G_ChargerLanguage":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getLan());
                    break;

                case "G_CardPin":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getCard());
                    break;
                case "G_RCDProtection":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getRcd());
                    break;
                case "G_Version":
                    bean.setType(WifiSetAdapter.PARAM_ITEM_CANT_CLICK);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getVersion());
                    break;

                case "ip":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getIp());
                    break;
                case "gateway":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getGateway());
                    break;
                case "mask":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getMask());
                    break;
                case "mac":
                    bean.setType(WifiSetAdapter.PARAM_ITEM_CANT_CLICK);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getMac());
                    break;
                case "dns":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getDns());
                    break;

                case "G_WifiSSID":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getSsid());
                    break;
                case "G_WifiPassword":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getWifiKey());
                    break;
                case "G_4GUserName":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getName4G());
                    break;
                case "G_4GPassword":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getPwd4G());
                    break;
                case "G_4GAPN":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getApn4G());
                    break;

                case "host":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getUrl());
                    break;
                case "G_Authentication":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getHskey());
                    break;
                case "G_HearbeatInterval":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getHeat());
                    break;
                case "G_WebSocketPingInterval":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getPing());
                    break;
                case "G_MeterValueInterval":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getInterval());
                    break;
                case "G_ChargerMode":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getMode());
                    break;
                case "G_MaxCurrent":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getMaxCurrent());
                    break;
                case "rate":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getRate());
                    break;
                case "G_MaxTemperature":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getTemp());
                    break;
                case "G_ExternalLimitPower":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getPower());
                    break;
                case "G_AutoChargeTime":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getTime());
                    break;
                case "G_PeakValleyEnable":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getChargingEnable());
                    break;
                case "G_ExternalLimitPowerEnable":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getPowerdistribution());
                    break;
                case "G_ExternalSamplingCurWring":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getWiring());
                    break;
                case "G_SolarMode":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getSolar());
                    for (int j = 0; j < solarBeans.size(); j++) {
                        SolarBean solarBean = solarBeans.get(j);
                        solarBean.setType(WifiSetAdapter.PARAM_ITEM_SOLAR);
                        solarBean.setSfield(keySfields[i]);
                        if (noConfigKeys.contains(solarBean.getSfield())) {
                            solarBean.setAuthority(false);
                        } else {
                            solarBean.setAuthority(true);
                        }
                        bean.addSubItem(solarBean);
                    }
                    break;
                case "G_PowerMeterAddr":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getAmmeter());
                    break;

                case "G_PowerMeterType":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getAmmeterType());
                    break;

                case "UnlockConnectorOnEVSideDisconnect":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getUnLockType());
                    break;
                case "G_NetworkMode":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getNetMode());
                    break;
                case "TimeZone":
                    bean.setType(WifiSetAdapter.PARAM_ITEM);
                    bean.setKey(keys[i]);
                    bean.setValue(initPileSetBean.getTimezone());
                    break;
            }
            bean.setSfield(keySfields[i]);
            if (noConfigKeys.contains(bean.getSfield())) {
                bean.setAuthority(false);
            } else {
                bean.setAuthority(true);
            }
            newlist.add(bean);
        }

        mAdapter.setNewData(newlist);
        mAdapter.expandAll();
    }


    /*??????????????????*/
    private void sendCmdConnect() {
        //???1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //???2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //????????????
        byte devType = WiFiMsgConstant.CONSTANT_MSG_10;
        //????????????
        byte encryption = WiFiMsgConstant.CONSTANT_MSG_01;
        this.encryption = encryption;
        //??????
        byte cmd = WiFiMsgConstant.CMD_A0;

        /*****????????????*****/
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
        System.arraycopy(newKey, 0, prayload, timeBytes.length + idBytes.length, newKey.length);

        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, oldKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] start = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(start);

        LogUtil.i("???????????????" + SmartHomeUtil.bytesToHexString(start));
        LogUtil.i("?????????" + time);
        LogUtil.i("????????????16?????????" + SmartHomeUtil.bytesToHexString(timeBytes));
        LogUtil.i("id???" + SmartHomeUtil.bytesToHexString(timeBytes));
        LogUtil.i("idBytes???" + SmartHomeUtil.bytesToHexString(idBytes));
        LogUtil.i("key???" + SmartHomeUtil.bytesToHexString(timeBytes));
        LogUtil.i("keyBytes???" + SmartHomeUtil.bytesToHexString(newKey));
    }


    /*????????????*/
    private void sendCmdExit() {
        //???1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //???2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //????????????
        byte devType = this.devType;
        //????????????
        byte encryption = this.encryption;
        //??????
        byte cmd = WiFiMsgConstant.CMD_A1;

        /*****????????????*****/
        byte len = (byte) 14;
        byte[] prayload = new byte[14];

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String time = simpleDateFormat.format(new Date());
        byte[] timeBytes = time.getBytes();
        System.arraycopy(timeBytes, 0, prayload, 0, timeBytes.length);

        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] exit = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(exit);
    }


    /**
     * ??????????????????????????????
     * cmd = 0x01 ????????????????????????
     * cmd = 0x02 ???????????????????????????
     * cmd =0x03  ??????????????????????????????
     * cmd =0x04  ?????????????????????
     */

    private void getDeviceInfo(byte cmd) {
        //???1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //???2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //????????????
        byte devType = this.devType;
        //????????????
        byte encryption = this.encryption;
        //??????
        byte getCmd = cmd;

        /*****????????????*****/
        byte len = (byte) 1;
        byte[] prayload = new byte[1];
        byte[] getInfo = "1".getBytes();
        System.arraycopy(getInfo, 0, prayload, 0, getInfo.length);
        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] infoBytes = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(infoBytes);

        LogUtil.i("???????????????" + SmartHomeUtil.bytesToHexString(infoBytes));
        LogUtil.i("??????16?????????" + SmartHomeUtil.bytesToHexString(getInfo));
    }


    /**************************************???????????????*****************************************/

    //??????????????????
    private void setInfo() {
        //???1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //???2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //????????????
        byte devType = this.devType;
        //????????????
        byte encryption = this.encryption;
        //??????
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_11;

        /*****????????????*****/
        byte len = (byte) infoLength;
        byte[] prayload = new byte[infoLength];

        if (infoLength > 52) {
            if (idByte == null || lanByte == null || cardByte == null || rcdByte == null || zoneByte == null) {
                T.make(R.string.m244????????????, this);
                return;
            }
        } else if (infoLength > 28) {
            if (idByte == null || lanByte == null || cardByte == null || rcdByte == null) {
                T.make(R.string.m244????????????, this);
                return;
            }
        } else {
            if (idByte == null || lanByte == null || cardByte == null || rcdByte == null) {
                T.make(R.string.m244????????????, this);
                return;
            }
        }

        //id
        System.arraycopy(idByte, 0, prayload, 0, idByte.length);
        //??????
        System.arraycopy(lanByte, 0, prayload, idByte.length, lanByte.length);
        //card
        System.arraycopy(cardByte, 0, prayload, idByte.length + lanByte.length, cardByte.length);
        //rcd
        System.arraycopy(rcdByte, 0, prayload, idByte.length + lanByte.length + cardByte.length, rcdByte.length);
      /*  if (infoLength > 28) {
            //?????????
            System.arraycopy(versionByte, 0, prayload, idByte.length + lanByte.length + cardByte.length + rcdByte.length, versionByte.length);
        }*/
        if (infoLength > 28) {
            //??????
            System.arraycopy(zoneByte, 0, prayload, idByte.length + lanByte.length + cardByte.length + rcdByte.length, zoneByte.length);
        }
        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setInfo = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setInfo);
        LogUtil.i("?????????????????????" + SmartHomeUtil.bytesToHexString(setInfo));
    }


    //????????????
    private void setInternt() {
        //???1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //???2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //????????????
        byte devType = this.devType;
        //????????????
        byte encryption = this.encryption;
        //??????
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_12;

        /*****????????????*****/
        byte len = (byte) internetLength;
        byte[] prayload = new byte[internetLength];


        if (internetLength > 77) {
            if (ipByte == null || gatewayByte == null || maskByte == null || macByte == null || dnsByte == null || netModeByte == null) {
                T.make(R.string.m244????????????, this);
                return;
            }
        } else {
            if (ipByte == null || gatewayByte == null || maskByte == null || macByte == null || dnsByte == null) {
                T.make(R.string.m244????????????, this);
                return;
            }
        }

        //ip
        System.arraycopy(ipByte, 0, prayload, 0, ipByte.length);
        //??????
        System.arraycopy(gatewayByte, 0, prayload, ipByte.length, gatewayByte.length);
        //??????
        System.arraycopy(maskByte, 0, prayload, ipByte.length + gatewayByte.length, maskByte.length);
        //mac
        System.arraycopy(macByte, 0, prayload, ipByte.length + gatewayByte.length + maskByte.length, macByte.length);
        //dns
        System.arraycopy(dnsByte, 0, prayload, ipByte.length + gatewayByte.length + maskByte.length + macByte.length, dnsByte.length);
        //netmode
        if (internetLength > 77) {
            System.arraycopy(netModeByte, 0, prayload, ipByte.length + gatewayByte.length + maskByte.length + macByte.length + dnsByte.length, netModeByte.length);
        }

        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setInterNet = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setInterNet);
        LogUtil.i("???????????????" + SmartHomeUtil.bytesToHexString(setInterNet));
    }

    //??????wifi
    private void setWifi() {
        //???1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //???2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //????????????
        byte devType = this.devType;
        //????????????
        byte encryption = this.encryption;
        //??????
        byte cmd = proversion<10? WiFiMsgConstant.CONSTANT_MSG_13:WiFiMsgConstant.CONSTANT_MSG_33;

        /*****????????????*****/
        if (ssidByte == null ) {
            ssidByte=new byte[0];
        }
        if (wifiKeyByte == null ) {
            wifiKeyByte=new byte[0];
        }
        if (bltPwdByte == null ) {
            bltPwdByte=new byte[0];
        }
        if (name4GByte == null ) {
            name4GByte=new byte[0];
        }
        if (pwd4GByte == null ) {
            pwd4GByte=new byte[0];
        }
        if (apn4GByte == null ) {
            apn4GByte=new byte[0];
        }
        int len = ssidByte.length + wifiKeyByte.length  + bltPwdByte.length + name4GByte.length + pwd4GByte.length + apn4GByte.length;
        byte[] prayload = new byte[len];

        //ssid
        System.arraycopy(ssidByte, 0, prayload, 0, ssidByte.length);
        //key
        System.arraycopy(wifiKeyByte, 0, prayload, ssidByte.length, wifiKeyByte.length);
//        //????????????
//        System.arraycopy(bltNameByte, 0, prayload, ssidByte.length + wifiKeyByte.length, bltNameByte.length);
        //????????????
        System.arraycopy(bltPwdByte, 0, prayload, ssidByte.length + wifiKeyByte.length, bltPwdByte.length);
        //4G?????????
        System.arraycopy(name4GByte, 0, prayload, ssidByte.length + wifiKeyByte.length  + bltPwdByte.length, name4GByte.length);
        //4G??????
        System.arraycopy(pwd4GByte, 0, prayload, ssidByte.length + wifiKeyByte.length  + bltPwdByte.length + name4GByte.length, pwd4GByte.length);
        //4GAPN
        System.arraycopy(apn4GByte, 0, prayload, ssidByte.length + wifiKeyByte.length  + bltPwdByte.length + name4GByte.length + pwd4GByte.length, apn4GByte.length);


        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setWifi = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen((byte) len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setWifi);
        LogUtil.i("wif?????????" + SmartHomeUtil.bytesToHexString(setWifi));
    }


    //??????url
    private void setUrl() {
        //???1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //???2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //????????????
        byte devType = this.devType;
        //????????????
        byte encryption = this.encryption;
        //??????
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_14;

        /*****????????????*****/
        byte len = (byte) 102;
        byte[] prayload = new byte[102];


        if (urlByte == null || hskeyByte == null || heatByte == null || pingByte == null || intervalByte == null) {
            T.make(R.string.m244????????????, this);
            return;
        }


        //url
        System.arraycopy(urlByte, 0, prayload, 0, urlByte.length);
        //key
        System.arraycopy(hskeyByte, 0, prayload, 70, hskeyByte.length);
        //????????????
        System.arraycopy(heatByte, 0, prayload, 90, heatByte.length);
        //????????????
        System.arraycopy(pingByte, 0, prayload, 94, pingByte.length);
        //4G?????????
        System.arraycopy(intervalByte, 0, prayload, 98, intervalByte.length);


        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setUrl = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setUrl);
        LogUtil.i("??????url???" + SmartHomeUtil.bytesToHexString(setUrl));
    }


    //??????????????????
    private void setCharging() {
        //???1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //???2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //????????????
        byte devType = this.devType;
        //????????????
        byte encryption = this.encryption;
        //??????
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_15;

        /*****????????????*****/
        byte len = (byte) chargingLength;
        byte[] prayload = new byte[chargingLength];

        if (modeByte == null || maxCurrentByte == null || rateByte == null || tempByte == null
                || powerByte == null || timeByte == null) {
            T.make(R.string.m244????????????, this);
            return;
        }

        if (chargingLength > 24) {//????????????
            if (chargingEnableByte == null || powerdistributionByte == null) {
                T.make(R.string.m244????????????, this);
                return;
            }
        }
        if (chargingLength > 26) {//????????????????????????
            if (wiringByte == null) {
                T.make(R.string.m244????????????, this);
                return;
            }
        }
        if (chargingLength > 27) {//solar
            if (solarByte == null) {
                T.make(R.string.m244????????????, this);
                return;
            }
        }

        if (chargingLength > 29) {
            String solarMode = MyUtil.ByteToString(solarByte);
            int modeIndext;
            try {
                modeIndext = Integer.parseInt(solarMode);
            } catch (NumberFormatException e) {
                modeIndext = 0;
            }
            if (modeIndext < 0) modeIndext = 1;
            if (modeIndext == 2) {
                if (solarCurrentByte == null) {
                    T.make(R.string.m244????????????, this);
                }
            }
        }

        if (chargingLength > 30) {//????????????
            if (ammeterByte == null) {
                T.make(R.string.m244????????????, this);
                return;
            }
        }

        if (chargingLength > 43) {//????????????
            if (ammeterTypeByte == null || unLockTypeByte == null) {
                T.make(R.string.m244????????????, this);
                return;
            }
        }

        //??????
        System.arraycopy(modeByte, 0, prayload, 0, modeByte.length);
        //??????
        System.arraycopy(maxCurrentByte, 0, prayload, 1, maxCurrentByte.length);
        //????????????
        System.arraycopy(rateByte, 0, prayload, 3, rateByte.length);
        //????????????
        System.arraycopy(tempByte, 0, prayload, 8, tempByte.length);
        //????????????
        System.arraycopy(powerByte, 0, prayload, 11, powerByte.length);
        //??????????????????
        System.arraycopy(timeByte, 0, prayload, 13, timeByte.length);
        if (chargingLength > 24) {
            //??????????????????
            System.arraycopy(chargingEnableByte, 0, prayload, 24, chargingEnableByte.length);
            //??????????????????
            System.arraycopy(powerdistributionByte, 0, prayload, 25, powerdistributionByte.length);
        }

        if (chargingLength > 26) {
            //?????????????????????????????????0:CT,1:?????????
            System.arraycopy(wiringByte, 0, prayload, 26, wiringByte.length);
        }
        if (chargingLength > 27) {//solar
            System.arraycopy(solarByte, 0, prayload, 27, solarByte.length);
        }

        if (chargingLength > 29) {
            String solarMode = MyUtil.ByteToString(solarByte);
            System.arraycopy(solarCurrentByte, 0, prayload, 28, solarCurrentByte.length);
        }
        if (chargingLength > 30) {
            System.arraycopy(ammeterByte, 0, prayload, 30, ammeterByte.length);
        }

        if (chargingLength > 43) {//???????????????????????????
            System.arraycopy(ammeterTypeByte, 0, prayload, 42, ammeterTypeByte.length);
            System.arraycopy(unLockTypeByte, 0, prayload, 43, unLockTypeByte.length);
        }

        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setCharging = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setCharging);
        LogUtil.i("?????????????????????" + SmartHomeUtil.bytesToHexString(setCharging));
    }


    //??????
    private void setUnLoack() {
        //???1
        byte frame1 = WiFiMsgConstant.FRAME_1;
        //???2
        byte frame2 = WiFiMsgConstant.FRAME_2;
        //????????????
        byte devType = this.devType;
        //????????????
        byte encryption = this.encryption;
        //??????
        byte cmd = WiFiMsgConstant.CONSTANT_MSG_16;

        /*****????????????*****/
        byte len = (byte) 1;
        byte[] prayload = new byte[1];
        if (lockByte == null) {
            T.make(R.string.m??????, this);
            return;
        }

        System.arraycopy(lockByte, 0, prayload, 0, lockByte.length);

        byte[] encryptedData = SmartHomeUtil.decodeKey(prayload, newKey);

        byte end = WiFiMsgConstant.BLT_MSG_END;

        byte[] setUrl = WiFiRequestMsgBean.Builder.newInstance()
                .setFrame_1(frame1)
                .setFrame_2(frame2)
                .setDevType(devType)
                .setEncryption(encryption)
                .setCmd(cmd)
                .setDataLen(len)
                .setPrayload(encryptedData)
                .setMsgEnd(end)
                .create();

        mClientUtil.sendMsg(setUrl);
        LogUtil.i("?????????" + SmartHomeUtil.bytesToHexString(setUrl));
    }


    private void back() {
        if (isEditInfo || isEditInterNet || isEditWifi || isEditUrl || isEditCharging) {//?????????
            new CircleDialog.Builder()
                    .setTitle(getString(R.string.m27????????????))
                    .setWidth(0.8f)
                    .setText(getString(R.string.m???????????????))
                    .setPositive(getString(R.string.m9??????), v -> {
                        sendCmdExit();
                        finish();
                    })
                    .setNegative(getString(R.string.m7??????), null)
                    .show(getSupportFragmentManager());
        } else {
            sendCmdExit();
            finish();
        }
    }


    /**
     * ????????????
     */
    private void save() {
        if (!isEditInfo && !isEditInterNet && !isEditWifi && !isEditUrl && !isEditCharging) {//?????????
            new CircleDialog.Builder()
                    .setTitle(getString(R.string.m27????????????))
                    .setWidth(0.8f)
                    .setText(getString(R.string.m304????????????????????????))
                    .setPositive(getString(R.string.m9??????), v -> {
                        sendCmdExit();
                        finish();
                    })
                    .setNegative(getString(R.string.m7??????), null)
                    .show(getSupportFragmentManager());
        } else {
            new CircleDialog.Builder()
                    .setTitle(getString(R.string.m27????????????))
                    .setWidth(0.8f)
                    .setText(getString(R.string.m????????????))
                    .setPositive(getString(R.string.m9??????), v -> {
                        if (isEditInfo) {//??????????????????
                            setInfo();
                        } else if (isEditInterNet) {//??????????????????
                            setInternt();
                        } else if (isEditWifi) {
                            setWifi();
                        } else if (isEditUrl) {
                            setUrl();
                        } else {
                            setCharging();
                        }
                    })
                    .setNegative(getString(R.string.m7??????), null)
                    .show(getSupportFragmentManager());
        }
    }


    /**********************************????????????************************************/

    private void parseReceivData(byte[] data) throws IndexOutOfBoundsException{
        if (data == null) return;
        int length = data.length;
        if (length > 4) {
            byte frame1 = data[0];
            byte frame2 = data[1];
            byte end = data[length - 1];
            if (frame1 == WiFiMsgConstant.FRAME_1 && frame2 == WiFiMsgConstant.FRAME_2 && end == WiFiMsgConstant.BLT_MSG_END) {
                byte cmd = data[4];//????????????
                //?????????
                byte sum = data[length - 2];
                byte checkSum = SmartHomeUtil.getCheckSum(data);
                if (checkSum != sum) {
                    LogUtil.d("??????????????????-->" + "?????????????????????" + sum + "??????????????????:" + checkSum);
                    return;
                }
                int len = SmartHomeUtil.byte2Int(new byte[]{data[5]});
                //????????????
                byte[] prayload = new byte[len];
                System.arraycopy(data, 6, prayload, 0, prayload.length);
                if (WifiSetActivity.this.encryption == WiFiMsgConstant.CONSTANT_MSG_01) {//??????
                    if (cmd == WiFiMsgConstant.CMD_A0) {
                        prayload = SmartHomeUtil.decodeKey(prayload, oldKey);
                    } else {
                        prayload = SmartHomeUtil.decodeKey(prayload, newKey);
                    }
                }
                Log.d("liaojinsha", SmartHomeUtil.bytesToHexString(prayload));
                switch (cmd) {
                    case WiFiMsgConstant.CMD_A0://????????????
                        //?????????????????????????????????
                        devType = data[2];
                        //??????????????????
                        int allow = SmartHomeUtil.byte2Int(new byte[]{ prayload[0]});
                        Mydialog.Dismiss();
                        proversion = allow;
                        if (allow == 0) {
                            isAllowed = false;
                            T.make(getString(R.string.m254????????????), WifiSetActivity.this);
                        } else {
                            isAllowed = true;
                            T.make(getString(R.string.m169????????????), WifiSetActivity.this);
                            getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
                        }
                        break;

                    case WiFiMsgConstant.CMD_A1:
                        byte exit = prayload[0];
                  /*  if ((int) exit == 1) {
                        T.make(getString(R.string.m281????????????), WifiSetActivity.this);
                    }*/
                        SocketClientUtil.close(mClientUtil);
                        break;

                    case WiFiMsgConstant.CONSTANT_MSG_01://??????????????????
                        infoLength = len;
                        idByte = new byte[20];
                        System.arraycopy(prayload, 0, idByte, 0, 20);
                        String devId = MyUtil.ByteToString(idByte);
                        setBean("chargeId", devId);
                        lanByte = new byte[1];
                        System.arraycopy(prayload, 20, lanByte, 0, 1);
                        String lan = MyUtil.ByteToString(lanByte);
                        if ("1".equals(lan)) {
                            lan = lanArray[0];
                        } else if ("2".equals(lan)) {
                            lan = lanArray[1];
                        } else {
                            lan = lanArray[2];
                        }
                        setBean("G_ChargerLanguage", lan);
                        cardByte = new byte[6];
                        System.arraycopy(prayload, 21, cardByte, 0, 6);
                        String card = MyUtil.ByteToString(cardByte);
                        setBean("G_CardPin", card);
                        rcdByte = new byte[1];
                        System.arraycopy(prayload, 27, rcdByte, 0, 1);
                        String rcd = MyUtil.ByteToString(rcdByte);
                        int i;
                        try {
                            i = Integer.parseInt(rcd);
                        } catch (NumberFormatException e) {
                            i = 0;
                        }
                        if (i <= 0) i = 1;
                        String s = rcdArray[i - 1];
                        setBean("G_RCDProtection", s);
                        //???????????????
                        if (len > 28) {
                            versionByte = new byte[24];
                            System.arraycopy(prayload, 28, versionByte, 0, 24);
                            String version = MyUtil.ByteToString(versionByte);
                            setBean("G_Version", version);
                        }
                        if (len > 52) {
                            zoneByte = new byte[10];
                            System.arraycopy(prayload, 52, zoneByte, 0, 10);
                            String version = MyUtil.ByteToString(zoneByte);
                            setBean("TimeZone", version);
                        }
                        mAdapter.notifyDataSetChanged();

                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_02);
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_02://?????????????????????
                        internetLength = len;
                        ipByte = new byte[15];
                        System.arraycopy(prayload, 0, ipByte, 0, 15);
                        String devIp = MyUtil.ByteToString(ipByte);
                        setBean("ip", devIp);
                        gatewayByte = new byte[15];
                        System.arraycopy(prayload, 15, gatewayByte, 0, 15);
                        String gateway = MyUtil.ByteToString(gatewayByte);
                        setBean("gateway", gateway);
                        maskByte = new byte[15];
                        System.arraycopy(prayload, 30, maskByte, 0, 15);
                        String mask = MyUtil.ByteToString(maskByte);
                        setBean("mask", mask);
                        macByte = new byte[17];
                        System.arraycopy(prayload, 45, macByte, 0, 17);
                        String mac = MyUtil.ByteToString(macByte);
                        setBean("mac", mac);
                        dnsByte = new byte[15];
                        System.arraycopy(prayload, 62, dnsByte, 0, 15);
                        String dns = MyUtil.ByteToString(dnsByte);
                        setBean("dns", dns);
                        //???????????????
                        if (len > 77) {
                            netModeByte = new byte[1];
                            System.arraycopy(prayload, 77, netModeByte, 0, 1);
                            String netMode = MyUtil.ByteToString(netModeByte);
                            int netModeIndex;
                            try {
                                netModeIndex = Integer.parseInt(netMode);
                            } catch (NumberFormatException e) {
                                netModeIndex = 0;
                            }
                            if (netModeIndex < 0) netModeIndex = 0;
                            netMode = netModeArray[netModeIndex];
                            setBean("G_NetworkMode", netMode);
                        }
                        mAdapter.notifyDataSetChanged();
                        if (proversion < 10) {
                            getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_03);
                        } else {
                            getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_23);
                        }
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_03://??????????????????????????????
                        wifiLength = len;
                        ssidByte = new byte[16];
                        System.arraycopy(prayload, 0, ssidByte, 0, 16);
                        String ssid = MyUtil.ByteToString(ssidByte);
                        setBean("G_WifiSSID", ssid);
                        wifiKeyByte = new byte[32];
                        System.arraycopy(prayload, 16, wifiKeyByte, 0, 32);
                        String wifikey = MyUtil.ByteToString(wifiKeyByte);
                        setBean("G_WifiPassword", wifikey);
//                        bltNameByte = new byte[16];
//                        System.arraycopy(prayload, 32, bltNameByte, 0, 16);
//                        String bltName = MyUtil.ByteToString(bltNameByte);
//                    setBean(15, bltName);
                        bltPwdByte = new byte[16];
                        System.arraycopy(prayload, 48, bltPwdByte, 0, 16);
                        String bltPwd = MyUtil.ByteToString(bltPwdByte);
//                    setBean(16, bltPwd);
                        name4GByte = new byte[16];
                        System.arraycopy(prayload, 64, name4GByte, 0, 16);
                        String name4G = MyUtil.ByteToString(name4GByte);
                        setBean("G_4GUserName", name4G);
                        pwd4GByte = new byte[16];
                        System.arraycopy(prayload, 80, pwd4GByte, 0, 16);
                        String pwd4G = MyUtil.ByteToString(pwd4GByte);
                        setBean("G_4GPassword", pwd4G);
                        apn4GByte = new byte[16];
                        System.arraycopy(prayload, 96, apn4GByte, 0, 16);
                        String apn4G = MyUtil.ByteToString(apn4GByte);
                        setBean("G_4GAPN", apn4G);
                        mAdapter.notifyDataSetChanged();
                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_04);
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_23:
                        wifiLength = len;
                        ssidByte = new byte[30];
                        System.arraycopy(prayload, 0, ssidByte, 0, 30);
                        String ssid1 = MyUtil.ByteToString(ssidByte);
                        setBean("G_WifiSSID", ssid1);
                        wifiKeyByte = new byte[64];
                        System.arraycopy(prayload, 30, wifiKeyByte, 0, 64);
                        String wifikey1 = MyUtil.ByteToString(wifiKeyByte);
                        setBean("G_WifiPassword", wifikey1);
//                        bltNameByte = new byte[30];
//                        System.arraycopy(prayload, 94, bltNameByte, 0, 30);
//                        String bltName1 = MyUtil.ByteToString(bltNameByte);
//                    setBean(15, bltName);
//                        bltPwdByte = new byte[16];
//                        System.arraycopy(prayload, 124, bltPwdByte, 0, 16);
//                        String bltPwd1 = MyUtil.ByteToString(bltPwdByte);
//                    setBean(16, bltPwd);
                        name4GByte = new byte[30];
                        System.arraycopy(prayload, 94, name4GByte, 0, 30);
                        String name4G1 = MyUtil.ByteToString(name4GByte);
                        setBean("G_4GUserName", name4G1);
                        pwd4GByte = new byte[30];
                        System.arraycopy(prayload, 124, pwd4GByte, 0, 30);
                        String pwd4G1 = MyUtil.ByteToString(pwd4GByte);
                        setBean("G_4GPassword", pwd4G1);
                        apn4GByte = new byte[30];
                        System.arraycopy(prayload, 154, apn4GByte, 0, 30);
                        String apn4G1 = MyUtil.ByteToString(apn4GByte);
                        setBean("G_4GAPN", apn4G1);
                        mAdapter.notifyDataSetChanged();
                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_04);
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_04://?????????????????????
                        urlLength = len;
                        urlByte = new byte[70];
                        System.arraycopy(prayload, 0, urlByte, 0, 70);
                        String url = MyUtil.ByteToString(urlByte);
                        setBean("host", url);

                        hskeyByte = new byte[20];
                        System.arraycopy(prayload, 70, hskeyByte, 0, 20);
                        String hskey = MyUtil.ByteToString(hskeyByte);
                        setBean("G_Authentication", hskey);

                        heatByte = new byte[4];
                        System.arraycopy(prayload, 90, heatByte, 0, 4);
                        String heat = MyUtil.ByteToString(heatByte);
                        setBean("G_HearbeatInterval", heat);

                        pingByte = new byte[4];
                        System.arraycopy(prayload, 94, pingByte, 0, 4);
                        String ping = MyUtil.ByteToString(pingByte);
                        setBean("G_WebSocketPingInterval", ping);

                        intervalByte = new byte[4];
                        System.arraycopy(prayload, 98, intervalByte, 0, 4);
                        String interval = MyUtil.ByteToString(intervalByte);
                        setBean("G_MeterValueInterval", interval);

                        mAdapter.notifyDataSetChanged();
                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_05);
                        break;

                    case WiFiMsgConstant.CONSTANT_MSG_05://??????????????????
                        chargingLength = len;
                        modeByte = new byte[1];
                        System.arraycopy(prayload, 0, modeByte, 0, 1);
                        String mode = MyUtil.ByteToString(modeByte);
                        int modeSet;
                        try {
                            modeSet = Integer.parseInt(mode);
                        } catch (NumberFormatException e) {
                            modeSet = 0;
                        }
                        if (modeSet <= 0) modeSet = 1;
                        String modeValue = modeArray[modeSet - 1];
                        setBean("G_ChargerMode", modeValue);

                        maxCurrentByte = new byte[2];
                        System.arraycopy(prayload, 1, maxCurrentByte, 0, 2);
                        String maxCurrent = MyUtil.ByteToString(maxCurrentByte);
                        setBean("G_MaxCurrent", maxCurrent);

                        rateByte = new byte[5];
                        System.arraycopy(prayload, 3, rateByte, 0, 5);
                        String rate = MyUtil.ByteToString(rateByte);
                        setBean("rate", rate);

                        tempByte = new byte[3];
                        System.arraycopy(prayload, 8, tempByte, 0, 3);
                        String temp = MyUtil.ByteToString(tempByte);
                        setBean("G_MaxTemperature", temp);

                        powerByte = new byte[2];
                        System.arraycopy(prayload, 11, powerByte, 0, 2);
                        String power = MyUtil.ByteToString(powerByte);
                        setBean("G_ExternalLimitPower", power);

                        timeByte = new byte[11];
                        System.arraycopy(prayload, 13, timeByte, 0, 11);
                        String time = MyUtil.ByteToString(timeByte);
                        if (time.contains("-")) {
                            String[] split = time.split("-");
                            if (split.length >= 2) {
                                startTime = split[0];
                                endTime = split[1];
                            }
                        }
                        setBean("G_AutoChargeTime", time);


                        //???????????????
                        if (len > 24) {
                            chargingEnableByte = new byte[1];
                            System.arraycopy(prayload, 24, chargingEnableByte, 0, 1);
                            String chargingEnable = MyUtil.ByteToString(chargingEnableByte);
                            int enable1;
                            try {
                                enable1 = Integer.parseInt(chargingEnable);
                            } catch (NumberFormatException e) {
                                enable1 = 0;
                            }
                            if (enable1 < 0) enable1 = 1;
                            String enableValue1 = enableArray[enable1];
                            setBean("G_PeakValleyEnable", enableValue1);
                        }


                        if (len > 25) {
                            powerdistributionByte = new byte[1];
                            System.arraycopy(prayload, 25, powerdistributionByte, 0, 1);
                            String powerdistribution = MyUtil.ByteToString(powerdistributionByte);
                            int enable2;
                            try {
                                enable2 = Integer.parseInt(powerdistribution);
                            } catch (NumberFormatException e) {
                                enable2 = 0;
                            }
                            if (enable2 < 0) enable2 = 1;
                            String enableValue2 = enableArray[enable2];
                            setBean("G_ExternalLimitPowerEnable", enableValue2);
                        }

                        if (len > 26) {
                            wiringByte = new byte[1];
                            System.arraycopy(prayload, 26, wiringByte, 0, 1);
                            String wiringType = MyUtil.ByteToString(wiringByte);
                            int wiring;
                            try {
                                wiring = Integer.parseInt(wiringType);
                            } catch (NumberFormatException e) {
                                wiring = 0;
                            }
                            if (wiring < 0) wiring = 1;


                            String wiringValue;
                            if (wiring < wiringArray.length) {
                                wiringValue = wiringArray[wiring];
                            } else {
                                wiringValue = wiring + "";
                            }

                            setBean("G_ExternalSamplingCurWring", wiringValue);
                        }
                        if (len > 27) {
                            solarByte = new byte[1];
                            System.arraycopy(prayload, 27, solarByte, 0, 1);
                            String solarMode = MyUtil.ByteToString(solarByte);
                            int modeIndext;
                            try {
                                modeIndext = Integer.parseInt(solarMode);
                            } catch (NumberFormatException e) {
                                modeIndext = 0;
                            }
                            if (modeIndext < 0) modeIndext = 1;
                            if (len > 29) {
                                solarCurrentByte = new byte[2];
                                solarBeans.clear();
                                System.arraycopy(prayload, 28, solarCurrentByte, 0, 2);
                                String current = MyUtil.ByteToString(solarCurrentByte);//??????????????????8A
                                SolarBean solarBean = new SolarBean();
                                solarBean.setValue(current);
                                solarBean.setType(WifiSetAdapter.PARAM_ITEM_SOLAR);
                                solarBean.setKey(getString(R.string.m????????????) + "(A)");
                                solarBeans.add(solarBean);
                            }
                            String solarModeValue = solarArrray[modeIndext];
                            setBean("G_SolarMode", solarModeValue);
                        }
                        if (len > 30) {
                            ammeterByte = new byte[12];
                            System.arraycopy(prayload, 30, ammeterByte, 0, 12);
                            String ammeterAdd = MyUtil.ByteToString(ammeterByte);
                            setBean("G_PowerMeterAddr", ammeterAdd);
                        }
                        if (len > 42) {
                            ammeterTypeByte = new byte[1];
                            System.arraycopy(prayload, 42, ammeterTypeByte, 0, 1);
                            String ammeterType = MyUtil.ByteToString(ammeterTypeByte);
                            int ammeterTypeIndext;
                            try {
                                ammeterTypeIndext = Integer.parseInt(ammeterType);
                            } catch (NumberFormatException e) {
                                ammeterTypeIndext = 0;
                            }
                            String ammeterTypeValue;
                            if (ammeterTypeIndext < ammterTypeArray.length) {
                                ammeterTypeValue = ammterTypeArray[ammeterTypeIndext];
                            } else {
                                ammeterTypeValue = ammeterTypeIndext + "";
                            }


                            setBean("G_PowerMeterType", ammeterTypeValue);
                        }

                        if (len > 43) {
                            unLockTypeByte = new byte[1];
                            System.arraycopy(prayload, 43, unLockTypeByte, 0, 1);
                            String lockType = MyUtil.ByteToString(unLockTypeByte);
                            int lockTypeIndext;
                            try {
                                lockTypeIndext = Integer.parseInt(lockType);
                            } catch (NumberFormatException e) {
                                lockTypeIndext = 0;
                            }
                            String lockTypeValue = unLockTypeArray[lockTypeIndext];
                            setBean("UnlockConnectorOnEVSideDisconnect", lockTypeValue);
                        }

                        mAdapter.notifyDataSetChanged();
                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_06);
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_06:
                        lockLength = len;
                        lockByte = new byte[len];
                        System.arraycopy(prayload, 0, lockByte, 0, len);
                        String lockStatus = MyUtil.ByteToString(lockByte);
                        lockBeans.clear();
                        if (!TextUtils.isEmpty(lockStatus)) {
                            char[] chars = lockStatus.toCharArray();
                            for (int k = 0; k < chars.length; k++) {
                                LockBean lockBean = new LockBean();
                                if (String.valueOf(chars[k]).equals("1")) {
                                    lockBean.setValue(lockArrray[1]);
                                } else {
                                    lockBean.setValue(lockArrray[0]);
                                }
                                lockBean.setType(WifiSetAdapter.PARAM_ITEM_LOCK);
                                List<String> letter = SmartHomeUtil.getLetter();
                                String name = letter.get(k) + " " + getString(R.string.???);
                                lockBean.setKey(name);
                                lockBean.setGunId(k + 1);
                                lockBean.setIndex(k);
                                lockBeans.add(lockBean);
                            }
                        } else {
                            LockBean lockBean = new LockBean();
                            lockBean.setValue("");
                            lockBean.setGunId(1);
                            lockBean.setType(WifiSetAdapter.PARAM_ITEM_LOCK);
                            List<String> letter = SmartHomeUtil.getLetter();
                            String name = letter.get(0) + " " + getString(R.string.???);
                            lockBean.setKey(name);
                            lockBeans.add(lockBean);
                        }
                        refreshRv();
                        break;
                    //????????????
                    case WiFiMsgConstant.CONSTANT_MSG_11:
                        if (isEditInterNet) {//??????????????????
                            setInternt();
                        } else if (isEditWifi) {
                            setWifi();
                        } else if (isEditUrl) {
                            setUrl();
                        } else if (isEditCharging) {
                            setCharging();
                        } else {
//                            byte result = prayload[0];
                            int result = SmartHomeUtil.byte2Int(new byte[]{prayload[0]});
                            if (result == 1) {
                                T.make(getString(R.string.m243????????????), WifiSetActivity.this);
                            } else {
                                T.make(getString(R.string.m244????????????), WifiSetActivity.this);
                            }
                            sendCmdExit();
                            finish();
                        }
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_12:
                        if (isEditWifi) {
                            setWifi();
                        } else if (isEditUrl) {
                            setUrl();
                        } else if (isEditCharging) {
                            setCharging();
                        } else {
                            int result = SmartHomeUtil.byte2Int(new byte[]{prayload[0]});
                            if (result == 1) {
//                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
                                T.make(getString(R.string.m243????????????), WifiSetActivity.this);
                            } else {
                                T.make(getString(R.string.m244????????????), WifiSetActivity.this);
                            }
                            sendCmdExit();
                            finish();
                        }
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_33:
                    case WiFiMsgConstant.CONSTANT_MSG_13:
                        if (isEditUrl) {
                            setUrl();
                        } else if (isEditCharging) {
                            setCharging();
                        } else {
                            int result = SmartHomeUtil.byte2Int(new byte[]{prayload[0]});
                            if (result == 1) {
//                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
                                T.make(getString(R.string.m243????????????), WifiSetActivity.this);
                            } else {
                                T.make(getString(R.string.m244????????????), WifiSetActivity.this);
                            }
                            sendCmdExit();
                            finish();
                        }
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_14:
                        if (isEditCharging) {
                            setCharging();
                        } else {
                            int result = SmartHomeUtil.byte2Int(new byte[]{prayload[0]});
                            if (result == 1) {
//                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
                                T.make(getString(R.string.m243????????????), WifiSetActivity.this);
                            } else {
                                T.make(getString(R.string.m244????????????), WifiSetActivity.this);
                            }
                            sendCmdExit();
                            finish();
                        }
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_15:
                        int result = SmartHomeUtil.byte2Int(new byte[]{prayload[0]});
                        if ( result == 1) {
//                        getDeviceInfo(WiFiMsgConstant.CONSTANT_MSG_01);
                            T.make(getString(R.string.m243????????????), WifiSetActivity.this);
                        } else {
                            T.make(getString(R.string.m244????????????), WifiSetActivity.this);
                        }
                        sendCmdExit();
                        finish();
                        break;
                    case WiFiMsgConstant.CONSTANT_MSG_16://??????
                        int unlock = SmartHomeUtil.byte2Int(new byte[]{prayload[0]});
                        if ((int) unlock == 1) {
                            lockBeans.get(gunPos).setValue(lockArrray[0]);
                            refreshRv();
                        } else {
                            T.make(getString(R.string.m??????), WifiSetActivity.this);
                        }
                        break;

                    default:
                        break;
                }
            }

        }


    }


    @OnClick({R.id.ivLeft, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivLeft:
                back();
                break;
            case R.id.tvRight:
                save();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            sendCmdExit();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*????????????*/
    private void setLanguage() {
        List<String> list = Arrays.asList(lanArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            final String tx = list.get(options1);
            String pos = String.valueOf(options1 + 1);
            byte[] bytes = pos.trim().getBytes();
            if (bytes.length > 1) {
                T.make(getString(R.string.m286???????????????????????????), WifiSetActivity.this);
                return;
            }
            lanByte = new byte[1];
            System.arraycopy(bytes, 0, lanByte, 0, bytes.length);
//                setInfo();
            setBean("G_ChargerLanguage", tx);
            mAdapter.notifyDataSetChanged();
            isEditInfo = true;
        })
                .setTitleText(getString(R.string.m260??????))
                .setSubmitText(getString(R.string.m9??????))
                .setCancelText(getString(R.string.m7??????))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(14)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    /*??????rcd*/
    private void setRcd() {
        List<String> list = Arrays.asList(rcdArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1 + 1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286???????????????????????????), WifiSetActivity.this);
                    return;
                }
                rcdByte = new byte[1];
                System.arraycopy(bytes, 0, rcdByte, 0, bytes.length);
//                setInfo();
                setBean("G_RCDProtection", tx);
                mAdapter.notifyDataSetChanged();
                isEditInfo = true;
            }
        })
                .setTitleText(getString(R.string.m265RCD?????????))
                .setSubmitText(getString(R.string.m9??????))
                .setCancelText(getString(R.string.m7??????))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(14)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    /*????????????*/
    private void setMode() {
        List<String> list = Arrays.asList(modeArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1 + 1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286???????????????????????????), WifiSetActivity.this);
                    return;
                }
                modeByte = new byte[1];
                System.arraycopy(bytes, 0, modeByte, 0, bytes.length);
//                setCharging();
                setBean("G_ChargerMode", tx);
                mAdapter.notifyDataSetChanged();
                isEditCharging = true;
            }
        })
                .setTitleText(getString(R.string.m154????????????))
                .setSubmitText(getString(R.string.m9??????))
                .setCancelText(getString(R.string.m7??????))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(14)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    /*????????????*/
    private void setEnable(int position) {
        String title;
        List<String> list = Arrays.asList(enableArray);
        if (position == 31) title = getString(R.string.m297??????????????????);
        else title = getString(R.string.m298??????????????????);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286???????????????????????????), WifiSetActivity.this);
                    return;
                }
                if (position == 31) {
                    chargingEnableByte = new byte[1];
                    System.arraycopy(bytes, 0, chargingEnableByte, 0, bytes.length);
                    setBean("G_PeakValleyEnable", tx);
                } else {
                    powerdistributionByte = new byte[1];
                    System.arraycopy(bytes, 0, powerdistributionByte, 0, bytes.length);
                    setBean("G_ExternalLimitPowerEnable", tx);
                }

                mAdapter.notifyDataSetChanged();
                isEditCharging = true;
            }
        })
                .setTitleText(title)
                .setSubmitText(getString(R.string.m9??????))
                .setCancelText(getString(R.string.m7??????))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(14)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    /*????????????*/
    private void setWiring() {
        List<String> list = Arrays.asList(wiringArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286???????????????????????????), WifiSetActivity.this);
                    return;
                }
                wiringByte = new byte[1];
                System.arraycopy(bytes, 0, wiringByte, 0, bytes.length);
//                setInfo();
                setBean("G_ExternalSamplingCurWring", tx);
                mAdapter.notifyDataSetChanged();
                isEditCharging = true;
            }
        })
                .setTitleText(getString(R.string.m??????????????????????????????))
                .setSubmitText(getString(R.string.m9??????))
                .setCancelText(getString(R.string.m7??????))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(14)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    /*solar??????*/
    private void setSolarMode() {
        List<String> list = Arrays.asList(solarArrray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286???????????????????????????), WifiSetActivity.this);
                    return;
                }
                solarByte = new byte[1];
                System.arraycopy(bytes, 0, solarByte, 0, bytes.length);
                setBean("G_SolarMode", tx);
                mAdapter.notifyDataSetChanged();
                isEditCharging = true;
            }
        })
                .setTitleText(getString(R.string.mSolar??????))
                .setSubmitText(getString(R.string.m9??????))
                .setCancelText(getString(R.string.m7??????))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(14)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    /*????????????*/
    private void setAmmterType() {
        List<String> list = Arrays.asList(ammterTypeArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286???????????????????????????), WifiSetActivity.this);
                    return;
                }
                ammeterTypeByte = new byte[1];
                System.arraycopy(bytes, 0, ammeterTypeByte, 0, bytes.length);
                setBean("G_PowerMeterType", tx);
                mAdapter.notifyDataSetChanged();
                isEditCharging = true;
            }
        })
                .setTitleText(getString(R.string.m????????????))
                .setSubmitText(getString(R.string.m9??????))
                .setCancelText(getString(R.string.m7??????))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(14)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }

    /*???????????????*/
    private void setLockType() {
        List<String> list = Arrays.asList(unLockTypeArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286???????????????????????????), WifiSetActivity.this);
                    return;
                }
                unLockTypeByte = new byte[1];
                System.arraycopy(bytes, 0, unLockTypeByte, 0, bytes.length);
                setBean("UnlockConnectorOnEVSideDisconnect", tx);
                mAdapter.notifyDataSetChanged();
                isEditCharging = true;
            }
        })
                .setTitleText(getString(R.string.m???????????????))
                .setSubmitText(getString(R.string.m9??????))
                .setCancelText(getString(R.string.m7??????))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(14)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();
    }


    private void setLock(int gunId) {
        new CircleDialog.Builder().setTitle(getString(R.string.m27????????????))
                .setText(getString(R.string.m???????????????????????????))
                .setWidth(0.8f)
                .setPositive(getString(R.string.m9??????), view -> {
                    String pos = String.valueOf(gunId);
                    byte[] bytes = pos.trim().getBytes();
                    if (bytes.length > 1) {
                        T.make(getString(R.string.m286???????????????????????????), WifiSetActivity.this);
                        return;
                    }
                    lockByte = new byte[1];
                    System.arraycopy(bytes, 0, lockByte, 0, bytes.length);
                    setUnLoack();
                })
                .setNegative(getString(R.string.m7??????), view -> {

                })
                .show(getSupportFragmentManager());
    }


    /*????????????*/
    private void setNetMode() {
        List<String> list = Arrays.asList(netModeArray);
        OptionsPickerView<String> pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String tx = list.get(options1);
                String pos = String.valueOf(options1);
                byte[] bytes = pos.trim().getBytes();
                if (bytes.length > 1) {
                    T.make(getString(R.string.m286???????????????????????????), WifiSetActivity.this);
                    return;
                }
                netModeByte = new byte[1];
                System.arraycopy(bytes, 0, netModeByte, 0, bytes.length);
                setBean("G_NetworkMode", tx);
                mAdapter.notifyDataSetChanged();
                isEditInterNet = true;
            }
        })
                .setTitleText(getString(R.string.m??????????????????))
                .setSubmitText(getString(R.string.m9??????))
                .setCancelText(getString(R.string.m7??????))
                .setTitleBgColor(0xffffffff)
                .setTitleColor(0xff333333)
                .setSubmitColor(0xff333333)
                .setCancelColor(0xff999999)
                .setBgColor(0xffffffff)
                .setTitleSize(14)
                .setTextColorCenter(0xff333333)
                .build();
        pvOptions.setPicker(list);
        pvOptions.show();

    }


    /**
     * ????????????
     */
    public void setZone() {
        List<String> zones = SmartHomeUtil.getZones();
        PickViewUtils.showPickView(this, zones, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String zone = zones.get(options1);
                byte[] bytes = zone.getBytes();
                if (bytes.length > 10) {
                    T.make(getString(R.string.m286???????????????????????????), WifiSetActivity.this);
                    return;
                }
                zoneByte = new byte[10];
                System.arraycopy(bytes, 0, zoneByte, 0, bytes.length);
                setBean("TimeZone", zone);
                mAdapter.notifyDataSetChanged();
                isEditInfo = true;

            }
        }, getString(R.string.m??????));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                String starttime = data.getStringExtra("start");
                String endtime = data.getStringExtra("end");
                if (TextUtils.isEmpty(starttime)) {
                    toast(R.string.m130?????????????????????);
                    return;
                }
                if (TextUtils.isEmpty(endtime)) {
                    toast(R.string.m284?????????????????????);
                    return;
                }
                startTime = starttime;
                endTime = endtime;
                String chargingTime = starttime + "-" + endtime;
                byte[] bytes = chargingTime.trim().getBytes();
                if (bytes.length > 11) {
                    T.make(getString(R.string.m286???????????????????????????), WifiSetActivity.this);
                    return;
                }
                timeByte = new byte[11];
                System.arraycopy(bytes, 0, timeByte, 0, bytes.length);
                setBean("G_AutoChargeTime", chargingTime);
                mAdapter.notifyDataSetChanged();
                isEditCharging = true;
            }
        }
    }


    private void showInputPassword(int position, int type, WifiSetBean bean) {
        new CircleDialog.Builder()
                .setTitle(getString(R.string.m27????????????))
                //????????????????????????????????????
                .setInputHint(getString(R.string.m26???????????????))//??????
                .setInputCounter(1000, (maxLen, currentLen) -> "")
                .configInput(params -> {
                    params.inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                            | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
                    params.gravity = Gravity.CENTER;
//                    params.textSize = 45;
//                            params.backgroundColor=ContextCompat.getColor(ChargingPileActivity.this, R.color.preset_edit_time_background);
                    params.strokeColor = ContextCompat.getColor(this, R.color.preset_edit_time_background);
                })
                .setPositiveInput(getString(R.string.m9??????), new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        if (password.equals(text)) {
                            isVerified = true;
                            switch (type) {
                                case WifiSetAdapter.PARAM_ITEM:
                                    setCommonParams(bean);
                                    break;
                                case WifiSetAdapter.PARAM_ITEM_SOLAR:
                                    setECOLimit(position);
                                    break;
                            }
                        } else {
                            toast(R.string.m????????????);
                        }
                        return true;
                    }
                })
                //??????????????????????????????????????????
                .setNegative(getString(R.string.m7??????), v -> {

                })
                .show(getSupportFragmentManager());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) bind.unbind();
    }
}

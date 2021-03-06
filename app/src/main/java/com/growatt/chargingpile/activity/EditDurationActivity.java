package com.growatt.chargingpile.activity;


import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.FreshTimingMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.bean.ReservationBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.CircleDialogUtils;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EditDurationActivity extends BaseActivity {

    @BindView(R.id.headerView)
    View headerView;

    @BindView(R.id.tv_open)
    TextView tvOpen;
    @BindView(R.id.tv_close)
    TextView tvClose;
    @BindView(R.id.tv_duration_time)
    TextView tvDuration;
    @BindView(R.id.rl_delete_reserva)
    RelativeLayout rlDelete;
    @BindView(R.id.cb_everyday)
    CheckBox cbEveryday;


    private long duration = 0;
    private int reservationId;
    private ReservationBean.DataBean dataBean;
    private int type = 1;


    private String expiryDate;
    private String endDate;
    private int loopType = -1;

    private String loopValue;
    private String chargingId;
    private Unbinder bind;
    private DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_duration);
        bind = ButterKnife.bind(this);
        initHeaderView();
        initIntent();
        initViews();
    }


    private void initIntent() {
        chargingId = getIntent().getStringExtra("sn");
        String jsonBean = getIntent().getStringExtra("bean");
        if (!TextUtils.isEmpty(jsonBean)) {
            type = 1;
            dataBean = new Gson().fromJson(jsonBean, ReservationBean.DataBean.class);
            expiryDate = dataBean.getExpiryDate();
            endDate = dataBean.getEndDate();
            reservationId = dataBean.getReservationId();
            loopType = dataBean.getLoopType();
            duration = Long.parseLong(dataBean.getcValue2());
        } else {
            type = 2;
        }
    }

    private void initViews() {
        if (type == 2) {
            tvOpen.setText(getString(R.string.m185?????????));
            tvClose.setText(getString(R.string.m185?????????));
            MyUtil.hideAllView(View.GONE, rlDelete);
        } else {
            cbEveryday.setChecked(loopType != -1);
            if (!TextUtils.isEmpty(expiryDate))
                tvOpen.setText(expiryDate.substring(11, 16));
            if (!TextUtils.isEmpty(endDate))
                tvClose.setText(endDate.substring(11, 16));
            MyUtil.showAllView(rlDelete);
            long hour = duration / 60;
            long min = duration % 60;
            String time = hour + "h" + min + "min";
            tvDuration.setText(time);
        }
    }


    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, v -> finish());
        setHeaderTitle(headerView, getString(R.string.m181????????????), R.color.title_1, true);
        setHeaderTvRight(headerView, getString(R.string.m182??????), v -> {
            if (type == 1) {
                dataBean.setLoopType(cbEveryday.isChecked() ? 0 : -1);
                editTime(expiryDate, dataBean.getLoopType());
            } else {
                int loopType = cbEveryday.isChecked() ? 0 : -1;
                addReserve(loopType, duration, loopValue);

            }
        }, R.color.main_text_color);
    }

    @OnClick({R.id.rl_start, R.id.rl_close, R.id.rl_delete_reserva})
    public void onClickListener(View view) {
        switch (view.getId()) {
            case R.id.rl_start:
                selectTime(1);
                break;
            case R.id.rl_close:
                selectTime(2);
                break;
            case R.id.rl_delete_reserva:
                delete();
                break;
        }
    }


    private void selectTime(final int openOrclose) {
        Calendar calendar=Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min=calendar.get(Calendar.MINUTE);
        if (openOrclose==1){
            if (!TextUtils.isEmpty(expiryDate)){
                String startTime = expiryDate.substring(11, 16);
                String[] time = startTime.split("[\\D]");
                hour= Integer.parseInt(time[0]);
                min= Integer.parseInt(time[1]);
            }
        }else {
            if (!TextUtils.isEmpty(endDate)){
                String endTime = endDate.substring(11, 16);
                String[] time = endTime.split("[\\D]");
                hour= Integer.parseInt(time[0]);
                min= Integer.parseInt(time[1]);
            }
        }
        dialogFragment = CircleDialogUtils.showWhiteTimeSelect(this, hour, min, getSupportFragmentManager(), false, new CircleDialogUtils.timeSelectedListener() {
            @Override
            public void cancle() {
                dialogFragment.dismiss();
            }

            @Override
            public void ok(boolean status, int hour, int min) {
                String hourString=hour <10?("0"+hour):hour+"";
                String minString=min <10?("0"+min):min+"";
                String time = hourString + ":" + minString;
                //????????????
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = new Date();
                String yMd = sdf.format(date);
                if (openOrclose == 1) {
                    tvOpen.setText(time);
                    expiryDate = yMd + "T" + time + ":00.000Z";
                    loopValue = time;
                } else {
                    tvClose.setText(time);
                    endDate = yMd + "T" + time + ":00.000Z";
                }
                if (TextUtils.isEmpty(expiryDate) || TextUtils.isEmpty(endDate)) {
                    return;
                }
                //??????????????????
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                try {
                    //???????????????
                    Date start = format.parse(expiryDate);
                    //???????????????
                    Date end = format.parse(endDate);
                    //????????????
                    long startTime = start.getTime();
                    long endTime = end.getTime();
                    long diffTime = 0;
                    if (startTime > endTime) {
                        diffTime = endTime + 24 * 60 * 60 * 1000 - startTime;
                    } else {
                        diffTime = endTime - startTime;
                    }
                    //???????????????
                    long nd = 1000 * 24 * 60 * 60;
                    long nh = 1000 * 60 * 60;
                    long nm = 1000 * 60;
                    // ?????????????????????
                    long diffHour = diffTime % nd / nh;
                    // ?????????????????????
                    long diffMin = diffTime % nd % nh / nm;
                    duration = diffHour * 60 + diffMin;
                    String sDiffTime = diffHour + "h" + diffMin + "min";
                    tvDuration.setText(sDiffTime);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dialogFragment.dismiss();
            }
        });

    }


    private void delete() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        new CircleDialog.Builder()
                .setWidth(0.75f)
                .setTitle(getString(R.string.m8??????))
                .setText(getString(R.string.m????????????))
                .setGravity(Gravity.CENTER).setPositive(getString(R.string.m9??????), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTime();
            }
        })
                .setNegative(getString(R.string.m7??????), null)
                .show(fragmentManager);

    }


    /**
     * ????????????
     */

    private void deleteTime() {
        LogUtil.d("????????????");
        Mydialog.Show(EditDurationActivity.this);
        String json = new Gson().toJson(dataBean);
        JSONObject object = null;
        try {
            object = new JSONObject(json);
            object.put("ctype", "3");
            object.put("lan", getLanguage());
            object.put("userId",SmartHomeUtil.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postUpdateChargingReservelist(), object.toString(), new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    if (code == 0) {
                        toast(R.string.m135????????????);
                        EventBus.getDefault().post(new FreshTimingMsg());
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });
    }


    /**
     * ????????????
     */

    private void editTime(String expiryDate, int loopType) {
        LogUtil.d("?????????????????????");
        Mydialog.Show(EditDurationActivity.this);
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("sn", chargingId);
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        jsonMap.put("ctype", "1");
        jsonMap.put("connectorId", dataBean.getConnectorId());
        jsonMap.put("cKey", dataBean.getCKey());
        jsonMap.put("cValue", duration);
        jsonMap.put("reservationId", dataBean.getReservationId());
        jsonMap.put("expiryDate", expiryDate);
        jsonMap.put("lan", getLanguage());

        if (loopType == -1) {
            jsonMap.put("loopType", loopType);
        } else {
            jsonMap.put("loopType", loopType);
            jsonMap.put("loopValue", expiryDate.substring(11, 16));
        }
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postUpdateChargingReservelist(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    if (code == 0) {
                        EventBus.getDefault().post(new FreshTimingMsg());
                        toast(R.string.m????????????);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });
    }


    private void addReserve(int loopType, long cValue, String loopValue) {
        Mydialog.Show(EditDurationActivity.this);
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("action", "ReserveNow");
        jsonMap.put("connectorId", 1);
        jsonMap.put("expiryDate", expiryDate);
        jsonMap.put("chargeId", chargingId);
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        jsonMap.put("cKey", "G_SetTime");
        jsonMap.put("cValue", cValue);
        jsonMap.put("loopType", loopType);
        jsonMap.put("lan", getLanguage());
        jsonMap.put("loopValue", loopValue);
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postRequestReseerveCharging(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    String data = object.getString("data");
                    toast(data);
                    if (code == 0) {
                        EventBus.getDefault().post(new FreshTimingMsg());
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

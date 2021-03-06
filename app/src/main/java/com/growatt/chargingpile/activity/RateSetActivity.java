package com.growatt.chargingpile.activity;

import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.EventBusMsg.RefreshRateMsg;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.RateSetAdapter;
import com.growatt.chargingpile.bean.ChargingBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RateSetActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {


    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.tvRight)
    TextView tvRight;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.rv_rate_list)
    RecyclerView rvRateList;

    private RateSetAdapter mAdapter;

    private List<ChargingBean.DataBean.PriceConfBean> priceConfBeanList;
    private String chargingId;
    private Unbinder bind;
    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_set);
        bind = ButterKnife.bind(this);
        initIntent();
        initViews();
    }

    private void initIntent() {
        chargingId = getIntent().getStringExtra("sn");
        symbol = getIntent().getStringExtra("symbol");
        if (TextUtils.isEmpty(symbol)) symbol = "";
        priceConfBeanList = getIntent().getParcelableArrayListExtra("rate");
        if (priceConfBeanList == null) priceConfBeanList = new ArrayList<>();
        for (int i = 0; i < priceConfBeanList.size(); i++) {
            ChargingBean.DataBean.PriceConfBean bean = priceConfBeanList.get(i);
            bean.setSymbol(symbol);
            String timeX = bean.getTimeX();
            if (!TextUtils.isEmpty(timeX)) {
                if (timeX.contains("-")) {
                    String[] split = timeX.split("-");
                    if (split.length >= 2) {
                     /*   startTime = split[0];
                        endTime = split[1];*/
                        bean.setStartTime(split[0]);
                        bean.setEndTime(split[1]);
                    }
                }
            }

        }
    }

    private void initViews() {
        ivLeft.setImageResource(R.drawable.back);
        tvTitle.setText(R.string.m152????????????);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvRight.setText(R.string.m182??????);
        tvRight.setTextColor(ContextCompat.getColor(this, R.color.maincolor_1));
        //???????????????
        mAdapter = new RateSetAdapter(R.layout.item_set_rate, priceConfBeanList);
        rvRateList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRateList.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.btnAdd, R.id.ivLeft, R.id.tvRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                addItem();
                break;
            case R.id.ivLeft:
                finish();
                break;
            case R.id.tvRight:
                if (isItemAllSetting()) {
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        String startTime = mAdapter.getData().get(i).getStartTime();
                        String endTime = mAdapter.getData().get(i).getEndTime();
                        String[] start = startTime.split(":");
                        String[] end = endTime.split(":");
                        int statValue = Integer.parseInt(start[0]) * 60 + Integer.parseInt(start[1]);
                        int endValue = Integer.parseInt(end[0]) * 60 + Integer.parseInt(end[1]);
                        if (isTimeCoincide(statValue, endValue, i)) {
                            toast(R.string.m333?????????????????????);
                            return;
                        }
                        if (statValue == endValue) {
                            toast(R.string.m332??????????????????);
                            return;
                        }
                    }
                    if (!isTimeCoveredOneDay()) {
                        setNosetRateDialog();
                        return;
                    }
                    requestEdit();
                } else {
                    toast(R.string.m330??????????????????????????????);
                }
                break;
        }
    }


    private void addItem() {
        if (priceConfBeanList.size() < 6) {
            ChargingBean.DataBean.PriceConfBean bean = new ChargingBean.DataBean.PriceConfBean();
            bean.setSymbol(symbol);
            mAdapter.addData(bean);
        } else {
            toast(R.string.m328??????????????????6???);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.fl_delete:
                if (mAdapter.getData().size() <= 1) {
                    toast(R.string.m329????????????1???);
                    return;
                }
                mAdapter.remove(position);
                break;
            case R.id.ll_select_time:
                showTimePickView(false, position);
                break;
            case R.id.tv_rate_value:
                inputEdit(position);
                break;
        }
    }

    /**
     * ?????????????????????
     */
    public void showTimePickView(boolean isEnd, int pos) {
        Calendar selectedDate = Calendar.getInstance();//??????????????????
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        String tittleText;
        if (isEnd) {
            tittleText = getString(R.string.m282????????????);
        } else {
            tittleText = getString(R.string.m204????????????);
        }
        ChargingBean.DataBean.PriceConfBean bean = mAdapter.getData().get(pos);
        TimePickerView pvCustomTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//??????????????????
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", getResources().getConfiguration().locale);
                String time = sdf.format(date);
                if (isEnd) {
                /*    if (!TextUtils.isEmpty(bean.getStartTime())) {
                        if (isStartMore(bean.getStartTime(), time)) {
                            toast(R.string.m285????????????????????????????????????);
                            return;
                        }
                    }*/
                    if (!TextUtils.isEmpty(bean.getStartTime())) {
                        String rateTime = bean.getStartTime() + "-" + time;
                        mAdapter.getData().get(pos).setTimeX(rateTime);
                    }
                    bean.setEndTime(time);
                    mAdapter.notifyItemChanged(pos);
                } else {
                    if (!TextUtils.isEmpty(bean.getEndTime())) {
                        String rateTime = time + "-" + bean.getEndTime();
                        mAdapter.getData().get(pos).setTimeX(rateTime);
                    }
                    bean.setStartTime(time);
                    showTimePickView(true, pos);
                }
            }
        })
                .setType(new boolean[]{false, false, false, true, true, false})// ??????????????????
                .setCancelText(getString(R.string.m7??????))//??????????????????
                .setSubmitText(getString(R.string.m9??????))//??????????????????
                .setContentTextSize(18)
                .setTitleSize(20)//??????????????????
                .setTitleText(tittleText)//????????????
                .setOutSideCancelable(false)//???????????????????????????????????????????????????????????????
                .isCyclic(true)//??????????????????
                .setTitleColor(0xff333333)//??????????????????
                .setSubmitColor(0xff333333)//????????????????????????
                .setCancelColor(0xff999999)//????????????????????????
                .setTitleBgColor(0xffffffff)//?????????????????? Night mode
                .setBgColor(0xffffffff)//?????????????????? Night mode
                .setTextColorCenter(0xff333333)
                .setDate(selectedDate)// ?????????????????????????????????????????????*/
                .setRangDate(startDate, endDate)//???????????????????????????
                .setLabel("", "", "", getString(R.string.m207???), getString(R.string.m208???), "")//?????????????????????????????????
                .isCenterLabel(false) //?????????????????????????????????label?????????false?????????item???????????????label???
                .isDialog(false)//??????????????????????????????
                .build();
        pvCustomTime.show();
    }


    /**
     * ??????????????????
     */

    private void requestEdit() {
        Mydialog.Show(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmd", "addPrice");
            jsonObject.put("userId", SmartHomeUtil.getUserName());
            jsonObject.put("chargeId", chargingId);
            jsonObject.put("lan", getLanguage());
            JSONArray jsonArray = new JSONArray();
            List<ChargingBean.DataBean.PriceConfBean> data = mAdapter.getData();
            if (!data.isEmpty()) {
                for (ChargingBean.DataBean.PriceConfBean bean : data) {
                    JSONObject rateJson = new JSONObject();
                    rateJson.put("time", bean.getTimeX());
                    rateJson.put("price", bean.getPrice());
                    rateJson.put("name", "");
                    jsonArray.put(rateJson);
                }

                jsonObject.put("priceConf", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = jsonObject.toString();
        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), json, new PostUtil.postListener() {
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
                        EventBus.getDefault().post(new RefreshRateMsg(mAdapter.getData()));
                        finish();
                    }
                    toast(object.getString("data"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void LoginError(String str) {

            }
        });
    }


    /*????????????????????????*/
    private boolean isTimeCoincide(int start, int end, int pos) {
        List<ChargingBean.DataBean.PriceConfBean> data = mAdapter.getData();
        List<Integer> oldlist = new ArrayList<>();
        if (start > end) {
            for (int i = start; i < 24 * 60; i++) {
                oldlist.add(i);
            }
            for (int i = 0; i < end + 1; i++) {
                oldlist.add(i);
            }
        } else {
            for (int i = start; i < end + 1; i++) {
                oldlist.add(i);
            }
        }
        for (int i = 0; i < data.size(); i++) {
            ChargingBean.DataBean.PriceConfBean bean = data.get(i);
            String time = bean.getTimeX();
            if (TextUtils.isEmpty(time)) continue;
            if (i == pos) continue;//????????????????????????
            List<Integer> newList = new ArrayList<>();
            String[] s = time.split("[\\D]");
            int alreadyStart = Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);
            int alreadyEnd = Integer.parseInt(s[2]) * 60 + Integer.parseInt(s[3]);
            if (alreadyEnd < alreadyStart) {
                for (int j = alreadyStart; j < 24 * 60; j++) {
                    newList.add(j);
                }
                for (int j = 0; j < alreadyEnd + 1; j++) {
                    newList.add(j);
                }
            } else {
                for (int j = alreadyStart; j < alreadyEnd + 1; j++) {
                    newList.add(j);
                }
            }
            if (!Collections.disjoint(oldlist, newList)) return true;
        }
        return false;
    }


    /*????????????????????????24??????*/
    private boolean isTimeCoveredOneDay() {
        List<ChargingBean.DataBean.PriceConfBean> data = mAdapter.getData();
        int timeLong = 0;
        int oneday = 24 * 60;
        for (int i = 0; i < data.size(); i++) {
            ChargingBean.DataBean.PriceConfBean bean = data.get(i);
            String time = bean.getTimeX();
            if (TextUtils.isEmpty(time)) continue;
            String[] s = time.split("[\\D]");
            int start = Integer.parseInt(s[0]) * 60 + Integer.parseInt(s[1]);
            int end = Integer.parseInt(s[2]) * 60 + Integer.parseInt(s[3]) + 1;
            if (start > end) {
                timeLong += (oneday - start) + end;
            } else {
                timeLong += end - start;
            }
        }
//        if (data.size() == 1) return timeLong == oneday - 1;
        return timeLong == oneday - 1 || timeLong == oneday;
    }

    /*???????????????item???????????????*/
    private boolean isItemAllSetting() {
        List<ChargingBean.DataBean.PriceConfBean> data = mAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            ChargingBean.DataBean.PriceConfBean bean = data.get(i);
            String time = bean.getTimeX();
            if (TextUtils.isEmpty(time)) return false;
        }
        return true;
    }


    /**
     * ????????????????????????
     * item ?????????
     */
    private void inputEdit(int pos) {
        double price = mAdapter.getData().get(pos).getPrice();
        new CircleDialog.Builder()
                .setWidth(0.8f)
                .setTitle(this.getString(R.string.m152????????????))
                .setInputText(String.valueOf(price))
                .setNegative(this.getString(R.string.m7??????), null)
                .setInputCounter(1000, (maxLen, currentLen) -> "")
                .configInput(params -> {
                    params.inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
                })
                .setPositiveInput(this.getString(R.string.m9??????), new OnInputClickListener() {
                    @Override
                    public boolean onClick(String text, View v) {
                        if (!TextUtils.isEmpty(text)) {
                            mAdapter.getData().get(pos).setPrice(Double.parseDouble(text));
                            mAdapter.notifyItemChanged(pos);
                        }
                        return true;
                    }
                })
                .show(this.getSupportFragmentManager());
    }

    private boolean isStartMore(String startTime, String endTime) {
        String[] start = startTime.split(":");
        String[] end = endTime.split(":");
        int statValue = Integer.parseInt(start[0]) * 60 + Integer.parseInt(start[1]);
        int endValue = Integer.parseInt(end[0]) * 60 + Integer.parseInt(end[1]);
        return statValue > endValue;
    }


    private void setNosetRateDialog() {
        new CircleDialog.Builder()
                .setTitle(getString(R.string.m27????????????))
                .setText(getString(R.string.m????????????24??????))
                .setWidth(0.7f)
                .setPositive(getString(R.string.m9??????), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestEdit();
                    }
                })
                .show(getSupportFragmentManager());
    }

}

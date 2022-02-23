package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.adapter.ChargingRecordAdapter;
import com.growatt.chargingpile.bean.ChargingRecordBean;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChargingRecoderActivity extends BaseActivity {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<ChargingRecordBean.DataBean> mRecordList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private ChargingRecordAdapter mChargingRecordAdapter;

    private int currentPage = 1;
    private int pageSize = 30;
    private boolean isLastPage = false;
    /*是否在加载*/
    private boolean isLoading = false;
    private int lastVisiblePosition = 0;

    private String chargingId;
    private Unbinder bind;
    private String symbol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_recoder);
        bind = ButterKnife.bind(this);
        initIntent();
        initHeaderView();
        initRecyclerView();
        setOnClickListener();
        refresh(1, 30);
    }

    private void initIntent() {
        chargingId = getIntent().getStringExtra("sn");
        symbol = getIntent().getStringExtra("symbol");
    }

    private void setOnClickListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !isLoading) {
                    if (mLinearLayoutManager.getChildCount() > 0 && lastVisiblePosition + 1 >= mLinearLayoutManager.getItemCount()) {
                        if (!isLastPage) {
                            currentPage++;
                            refresh(currentPage, pageSize);
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();//当前可见的最后一行
            }
        });
    }

    private void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChargingRecordAdapter = new ChargingRecordAdapter(mRecordList);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mChargingRecordAdapter);
    }

    private void initHeaderView() {

        initToobar(toolbar);
        tvTitle.setText(R.string.m104充电记录);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.main_theme_text_color));
    }


    private void refresh(int page, int psize) {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        jsonMap.put("sn", chargingId);
        jsonMap.put("page", page);
        jsonMap.put("psize", psize);
        jsonMap.put("lan", getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        PostUtil.postJson(SmartHomeUrlUtil.postUserChargingRecord(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                try {
                    Mydialog.Dismiss();
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    if (code == 0) {
                        ChargingRecordBean recordBean = new Gson().fromJson(json, ChargingRecordBean.class);
                        List<ChargingRecordBean.DataBean> recordList = recordBean.getData();
                        if (recordList.size() < pageSize) {
                            isLastPage = true;
                        }
                        if (recordList.size() == 0) {
                            isLastPage = true;
                            if (currentPage > 1) {
                                currentPage--;
                            }
                        } else {
                            for (int i = 0; i < recordList.size(); i++) {
                                recordList.get(i).setSymbol(symbol);
                            }
                            if (currentPage == 1) {
                                mChargingRecordAdapter.replaceData(recordList);
                            } else {
                                mChargingRecordAdapter.addData(recordList);
                            }
                        }

                    } else {
                        isLastPage = true;
                        if (currentPage > 1) {
                            currentPage--;
                        }
                    }
                } catch (Exception e) {
                    isLastPage = true;
                    if (currentPage > 1) {
                        currentPage--;
                    }
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

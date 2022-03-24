package com.yingli.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.yingli.chargingpile.BaseActivity;
import com.yingli.chargingpile.EventBusMsg.FreshAuthMsg;
import com.yingli.chargingpile.R;
import com.yingli.chargingpile.adapter.ChargingUserAdapter;
import com.yingli.chargingpile.bean.ChargingUserBean;
import com.yingli.chargingpile.connutil.PostUtil;
import com.yingli.chargingpile.util.Mydialog;
import com.yingli.chargingpile.util.SmartHomeUrlUtil;
import com.yingli.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ChargingAuthorizationActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener {


    /*授权列表*/
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.headerView)
    LinearLayout headerView;


    private List<ChargingUserBean.DataBean> mUserList = new ArrayList<>();
    private ChargingUserAdapter mChargingUserAdapter;
    private String chargingId;
    private Unbinder bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charging_authorization);
        bind = ButterKnife.bind(this);
        initIntent();
        initHeaderView();
        initRecyclerView();
        initListeners();
        refresh();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void aa(FreshAuthMsg msg) {
        refresh();
    }


    private void initListeners() {
        mSwipeRefresh.setColorSchemeResources(R.color.maincolor_1);
        mSwipeRefresh.setOnRefreshListener(this::refresh);
    }

    private void initIntent() {
        chargingId = getIntent().getStringExtra("sn");
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void refresh() {
        Mydialog.Show(this);
        Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
        jsonMap.put("sn", chargingId);
        jsonMap.put("userId", SmartHomeUtil.getUserName());
        jsonMap.put("page", 1);
        jsonMap.put("psize", 30);
        jsonMap.put("lan", getLanguage());//测试id
        String json = SmartHomeUtil.mapToJsonString(jsonMap);
        LogUtil.i(json);
        PostUtil.postJson(SmartHomeUrlUtil.postGetAuthorizationList(), json, new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                mSwipeRefresh.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getInt("code") == 0) {
                        ChargingUserBean userBean = new Gson().fromJson(jsonObject.toString(), ChargingUserBean.class);
                        if (userBean != null) {
                            List<ChargingUserBean.DataBean> dataBeans = userBean.getData();
                            mChargingUserAdapter.replaceData(dataBeans);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {
                mSwipeRefresh.setRefreshing(false);
            }
        });
    }

    /**
     * 初始化头部
     */
    private void initHeaderView() {

        initToobar(toolbar);
        tvTitle.setText(R.string.m142授权管理);
        tvTitle.setTextColor(ContextCompat.getColor(this,R.color.main_theme_text_color));

        toolbar.inflateMenu(R.menu.menu_authorization);
        toolbar.setOnMenuItemClickListener(this);

    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.right_action) {
            gotoGrant();
        }
        return true;
    }



    private void gotoGrant() {
        Intent intent = new Intent(this, AddAuthorizationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("sn", chargingId);
        startActivity(intent);
    }


    private void initRecyclerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChargingUserAdapter = new ChargingUserAdapter(mUserList);
        mChargingUserAdapter.setDelListener(new ChargingUserAdapter.DeleteListener() {
            @Override
            public void deleteItem(String userId, int position) {
                deleteUser(userId, position);
            }
        });
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mChargingUserAdapter);
        View emptyView = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        mChargingUserAdapter.setEmptyView(emptyView);
    }


    private void deleteUser(final String userId, final int pos) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        new CircleDialog.Builder()
                .setWidth(0.75f)
                .setTitle(getString(R.string.m8警告))
                .setText(getString(R.string.m确认删除))
                .setGravity(Gravity.CENTER).setPositive(getString(R.string.m9确定), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mydialog.Show(ChargingAuthorizationActivity.this);
                Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
                jsonMap.put("sn", chargingId);
                jsonMap.put("userId", userId);
                jsonMap.put("lan", getLanguage());//测试id
                String json = SmartHomeUtil.mapToJsonString(jsonMap);
                LogUtil.i(json);
                PostUtil.postJson(SmartHomeUrlUtil.postDeleteAuthorizationUser(), json, new PostUtil.postListener() {
                    @Override
                    public void Params(Map<String, String> params) {

                    }

                    @Override
                    public void success(String json) {
                        Mydialog.Dismiss();
                        try {
                            JSONObject object = new JSONObject(json);
                            int code = object.getInt("code");
                            if (code == 0) mChargingUserAdapter.remove(pos);
                            String data = object.getString("data");
                            toast(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void LoginError(String str) {

                    }
                });
            }
        })
                .setNegative(getString(R.string.m7取消), null)
                .show(fragmentManager);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
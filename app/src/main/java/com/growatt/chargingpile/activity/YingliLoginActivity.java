package com.growatt.chargingpile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.application.MyApplication;
import com.growatt.chargingpile.listener.OnViewEnableListener;
import com.growatt.chargingpile.sqlite.SqliteUtil;
import com.growatt.chargingpile.util.Constant;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.SharedPreferencesUnit;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.gyf.immersionbar.ImmersionBar;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class YingliLoginActivity extends BaseActivity {


    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.bt_register)
    Button btRegistere;
    @BindView(R.id.tv_foget)
    TextView tvForget;

    private Unbinder bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yingli_login);
        bind = ButterKnife.bind(this);
        initUrl();
        initUser();
        AutoLogin();
    }

    @Override
    public void initStatusBar() {
        //设置共同沉浸式样式
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarDarkFont(false);
        mImmersionBar.keyboardEnable(true).init();
    }

    private void initUrl() {
        String url = SqliteUtil.inquiryurl();
        if (!TextUtils.isEmpty(url)){
            SmartHomeUrlUtil.SMARTHOME_BASE_URL = url;
        }

    }


    private void initUser() {
        Map<String, Object> inquirylogin = SqliteUtil.inquirylogin();
        if (inquirylogin.size() > 0) {
            String name = inquirylogin.get("name").toString();
            String pwd = inquirylogin.get("pwd").toString();
            etUsername.setText(name);
            etPassword.setText(pwd);
            etUsername.setSelection(name.length());
            etPassword.setSelection(pwd.length());
        }
    }




    /**
     * 自动登录
     */
    private void AutoLogin() {
        final Map<String, Object> map = SqliteUtil.inquirylogin();
        int autoLoginNum = SharedPreferencesUnit.getInstance(this).getInt(Constant.AUTO_LOGIN);
        if (autoLoginNum == 0 || map.size() == 0) {
            return;
        }
        //oss登录
        LoginUtil.login(mContext, etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), new OnViewEnableListener() {
            @Override
            public void onViewEnable() {

            }
        });
    }



    @OnClick({R.id.bt_login,R.id.bt_register, R.id.tv_foget})
    public void onClickListeners(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                btnLogin();
                break;
            case R.id.tv_foget:
                jumpTo(ForgotPasswordActivity.class, false);
                break;
            case R.id.bt_register:
                Intent intent = new Intent();
                intent.setClass(this, UserRegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("activity", "LoginActivity");
                startActivity(intent);
                break;
        }
    }

    /**
     * 登录
     */
    public void btnLogin() {
        String userName = String.valueOf(etUsername.getText()).trim();
        if (TextUtils.isEmpty(userName)) {
            toast(R.string.m21用户名密码为空);
            return;
        }
        String pwd = String.valueOf(etPassword.getText()).trim();
        if (TextUtils.isEmpty(pwd)) {
            toast(R.string.m21用户名密码为空);
            return;
        }
        btLogin.setEnabled(false);
        LoginUtil.login(mContext, etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), new OnViewEnableListener() {
            @Override
            public void onViewEnable() {
                if (!btLogin.isEnabled()) {
                    btLogin.setEnabled(true);
                }
            }
        });

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MyApplication.getInstance().exit();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) bind.unbind();
    }

}

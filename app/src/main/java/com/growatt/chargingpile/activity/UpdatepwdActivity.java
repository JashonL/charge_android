package com.growatt.chargingpile.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Constant;
import com.growatt.chargingpile.util.DialogUtil;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SharedPreferencesUnit;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UpdatepwdActivity extends BaseActivity {


    @BindView(R.id.et_old_password)
    EditText etOldPassword;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.et_repeat_password)
    EditText etRepeatPassword;
    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.ll_edit)
    LinearLayout llEdit;
    @BindView(R.id.bt_finish)
    Button btFinish;

    private Unbinder bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatepwd);
        bind = ButterKnife.bind(this);
        initHeaderView();
    }


    private void initHeaderView() {

        initToobar(toolbar);
        tvTitle.setText(R.string.m57修改密码);
        tvTitle.setTextColor(ContextCompat.getColor(this,R.color.main_theme_text_color));


    }

    @OnClick({R.id.bt_finish})
    public void onClicklistener(View view) {
        switch (view.getId()) {
            case R.id.bt_finish:
//                tvOK();
                updatePassword();
                break;
        }

    }


    public void updatePassword() {
        String oldPwd = etOldPassword.getText().toString();
        String newPwd = etNewPassword.getText().toString();
        String repeatPwd = etRepeatPassword.getText().toString();
        if (oldPwd.equals("") || newPwd.equals("") || repeatPwd.equals("")) {
            toast(R.string.m21用户名密码为空);
            return;
        }

        if (newPwd.length() < 6) {
            toast(R.string.m100密码必须大于6位);
            return;
        }

        if (!newPwd.equals(repeatPwd)) {
            toast(R.string.m98请输入相同的密码);
            return;
        }

        Mydialog.Show(this);
        JSONObject object = new JSONObject();
        try {
            object.put("command", "updateUser");//cmd  注册
            object.put("userId", SmartHomeUtil.getUserName());//用户名
            object.put("password", oldPwd);//密码
            object.put("newPassword", newPwd);//密码
            object.put("lan", getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        PostUtil.postJson(SmartHomeUrlUtil.postByCmd(), object.toString(), new PostUtil.postListener() {
            @Override
            public void Params(Map<String, String> params) {

            }

            @Override
            public void success(String json) {
                Mydialog.Dismiss();
                try {
                    JSONObject object = new JSONObject(json);
                    int code = object.getInt("code");
                    String data = object.optString("data");
                    if (code == 0) {
                        DialogUtil.circlerDialog(UpdatepwdActivity.this, data, code, false, () -> {
                            SharedPreferencesUnit.getInstance(UpdatepwdActivity.this).putInt(Constant.AUTO_LOGIN, 0);
                            SharedPreferencesUnit.getInstance(UpdatepwdActivity.this).putInt(Constant.AUTO_LOGIN_TYPE, 0);
                            LoginUtil.logout(UpdatepwdActivity.this);
                        });
                    }
                    String errorMsg = object.optString("data");
                    if (!TextUtils.isEmpty(errorMsg))
                        toast(errorMsg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });
    }


  /*  public void tvOK() {
        String oldPwd = etOldPassword.getText().toString();
        String newPwd = etNewPassword.getText().toString();
        String repeatPwd = etRepeatPassword.getText().toString();
        if (oldPwd.equals("") || newPwd.equals("") || repeatPwd.equals("")) {
            toast(R.string.m21用户名密码为空);
            return;
        }
        if (!newPwd.equals(repeatPwd)) {
            toast(R.string.m98请输入相同的密码);
            return;
        }
        Mydialog.Show(UpdatepwdActivity.this, "");
        PostUtil.post(new Urlsutil().updateUserPassword, new PostUtil.postListener() {

            @Override
            public void success(String json) {
                try {
                    Mydialog.Dismiss();
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.get("msg").toString().equals("200")) {
                        toast(R.string.m成功);
                        //设置不自动登录
                        //设置不自动登录
                        SharedPreferencesUnit.getInstance(UpdatepwdActivity.this).putInt(Constant.AUTO_LOGIN, 0);
                        SharedPreferencesUnit.getInstance(UpdatepwdActivity.this).putInt(Constant.AUTO_LOGIN_TYPE, 0);
                        jumpTo(LoginActivity.class, true);
                    } else if (jsonObject.get("msg").toString().equals("502")) {
                        toast(R.string.m64原密码错误);
                    } else if ("701".equals(jsonObject.get("msg").toString())) {
                        toast(R.string.m66你的账号没有操作权限);
                    } else {
                        toast(R.string.m37服务器错误);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void Params(Map<String, String> params) {
                Map<String, Object> map = SqliteUtil.inquirylogin();
                params.put("accountName", map.get("name").toString());
                params.put("passwordOld", et1.getText().toString());
                params.put("passwordNew", et2.getText().toString());
            }

            @Override
            public void LoginError(String str) {
                // TODO Auto-generated method stub

            }
        });
    }*/


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

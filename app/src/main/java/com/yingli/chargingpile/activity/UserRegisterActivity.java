package com.yingli.chargingpile.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.yingli.chargingpile.BaseActivity;
import com.yingli.chargingpile.R;
import com.yingli.chargingpile.connutil.PostUtil;
import com.yingli.chargingpile.listener.OnViewEnableListener;
import com.yingli.chargingpile.sqlite.SqliteUtil;
import com.yingli.chargingpile.util.Cons;
import com.yingli.chargingpile.util.LoginUtil;
import com.yingli.chargingpile.util.MyUtil;
import com.yingli.chargingpile.util.Mydialog;
import com.yingli.chargingpile.util.SmartHomeUrlUtil;
import com.mylhyl.circledialog.CircleDialog;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserRegisterActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_repeat_password)
    EditText etRepeatPassword;
    @BindView(R.id.et_user_email)
    EditText etUserEmail;
    @BindView(R.id.et_postal_code)
    EditText etPostalCode;
    @BindView(R.id.et_mobile_phonenum)
    EditText etMobilePhoneNum;
    @BindView(R.id.et_installer)
    EditText etInstaller;
    @BindView(R.id.et_installer_email)
    EditText etInstallerEmail;
    @BindView(R.id.et_installser_phone)
    EditText etInstallerPhone;
    @BindView(R.id.et_installer_address)
    EditText etInstallerAddress;
    @BindView(R.id.tv_installer_date)
    TextView etInstallerDate;
    @BindView(R.id.et_charge_sn)
    EditText etChargeSn;
    @BindView(R.id.textView4)
    TextView terms;
    @BindView(R.id.checkBox)
    CheckBox checkBox;

    private Calendar calendar = Calendar.getInstance();
    private String username;
    private String password;
    private String email;
    private String postCode;
    private String phone;
    private String installer;
    private String installerEmail;
    private String installerPhone;
    private String installerAddress;
    private String installerDate;
    private String installChargeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        ButterKnife.bind(this);
        initHeaderView();
        initViews();
    }


    private void initHeaderView() {
        initToobar(toolbar);
        tvTitle.setText(getString(R.string.m23注册));
    }


    private void initViews() {
        terms.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        terms.getPaint().setAntiAlias(true);//抗锯齿
    }

    @OnClick({R.id.btRegister, R.id.textView4, R.id.ll_date})
    public void toRegister(View view) {
        switch (view.getId()) {
            case R.id.textView4:
                startActivity(new Intent(this, AgreementActivity.class));
                break;
            case R.id.btRegister:
                register();
                break;
            case R.id.ll_date:
                new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                    String sbDate = year +
                            "-" + ((month + 1) < 10 ? "0" + (month + 1) : (month + 1)) +
                            "-" + ((dayOfMonth < 10) ? "0" + dayOfMonth : dayOfMonth);
                    etInstallerDate.setText(sbDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) {
                    @Override
                    protected void onStop() {
                    }
                }.show();
                break;
        }

    }


    private void register() {
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        email = etUserEmail.getText().toString().trim();
        postCode = etPostalCode.getText().toString().trim();
        phone = etMobilePhoneNum.getText().toString().trim();
        installer = etInstaller.getText().toString().trim();
        installerEmail = etInstallerEmail.getText().toString().trim();
        installerPhone = etInstallerPhone.getText().toString().trim();
        installerAddress = etInstallerAddress.getText().toString().trim();
        installerDate = etInstallerDate.getText().toString().trim();
        installChargeId = etChargeSn.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            toast(R.string.m21用户名密码为空);
            return;
        }
        if (username.length() < 3) {
            toast(R.string.m99用户名必须大于3位);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            toast(R.string.m21用户名密码为空);
            return;
        }

        if (password.length() < 6) {
            toast(R.string.m100密码必须大于6位);
            return;
        }

        if (TextUtils.isEmpty(String.valueOf(etRepeatPassword.getText()))) {
            toast(R.string.m21用户名密码为空);
            return;
        }

        if (!etPassword.getText().toString().trim().equals(etRepeatPassword.getText().toString().trim())) {
            toast(R.string.m98请输入相同的密码);
            return;
        }

        if (TextUtils.isEmpty(email)) {
            toast(R.string.m35请输入正确邮箱格式);
            return;
        }

        //校验邮箱
        if (!MyUtil.regexCheckEmail(email)) {
            toast(R.string.m35请输入正确邮箱格式);
            return;
        }


        //邮政编码
        if (TextUtils.isEmpty(postCode)) {
            toast(R.string.m邮政编码不能为空);
            return;
        }


        //安装者
        if (TextUtils.isEmpty(installer)) {
            toast(R.string.m安装商不能为空);
            return;
        }
        //安装者邮箱
        if (TextUtils.isEmpty(installerEmail)) {
            toast(R.string.m安装商邮箱不能为空);
            return;
        }
        //安装者电话
        if (TextUtils.isEmpty(installerPhone)) {
            toast(R.string.m安装商电话不能为空);
            return;
        }
        //安装者地址
        if (TextUtils.isEmpty(installerAddress)) {
            toast(R.string.m安装商地址不能为空);
            return;
        }
        //安装日期
        if (TextUtils.isEmpty(installerDate)) {
            toast(R.string.m安装商日期不能为空);
            return;
        }

        //充电桩序列号
        if (TextUtils.isEmpty(installChargeId)){
            toast(R.string.m充电桩序列号不能为空);
            return;
        }


        if (!checkBox.isChecked()) {
            toast(R.string.m34选择用户协议);
            return;
        }
        showDisclaimer();
    }


    public void showDisclaimer() {
        new CircleDialog.Builder()
                .setText(getString(R.string.m免责声明))
                .setGravity(Gravity.CENTER)
                .setPositive(getString(R.string.m9确定), v -> {
                    try {
                        requestRegister();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .show(getSupportFragmentManager());
    }


    private void requestRegister() {
        final String country = MyUtil.getCountryAndPhoneCodeByCountryCode(this, 1);
        Mydialog.Show(this);
        JSONObject object = new JSONObject();
        try {
            object.put("command", "register");//cmd  注册
            object.put("userId", username);//用户名
            object.put("roleId", "endUser");//角色
            object.put("phone", phone);
            object.put("password", password);//密码
            object.put("installer", installer);//安装者
            object.put("company", installer);//公司
            object.put("email", email);//邮箱
            object.put("installerInfo", installer);//安装商信息
            object.put("zipCode", postCode);//邮编
            object.put("country", country);//国家
            object.put("lan", getLanguage());
            object.put("installEmail", installerEmail);
            object.put("installPhone", installerPhone);
            object.put("installAddress", installerAddress);
            object.put("installDate", installerDate);
            object.put("installChargeId", installChargeId);

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
                    if (code == 0) {
                        toast(R.string.m42注册成功);
                        SqliteUtil.url(SmartHomeUrlUtil.getServer());
                        LoginUtil.login(mContext, etUsername.getText().toString().trim(), etPassword.getText().toString().trim(), new OnViewEnableListener() {
                            @Override
                            public void onViewEnable() {

                            }
                        });
                    } else {
                        String errorMsg = object.optString("data");
                        toast(errorMsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void LoginError(String str) {

            }
        });
        Cons.regMap.setRegEmail(email);
        Cons.regMap.setRegPassword(password);
        Cons.regMap.setRegPhoneNumber(phone);
        Cons.regMap.setRegUserName(username);
        Cons.regMap.setRegPostCode(postCode);
        Cons.regMap.setRegInstaller(installer);
        Cons.regMap.setRegInstallEmail(installerEmail);
        Cons.regMap.setRegInstallPhone(installerPhone);
        Cons.regMap.setRegInstallAddress(installerAddress);
        Cons.regMap.setRegInstallDate(installerDate);
        Cons.regMap.setRegCity(country);
        Cons.regMap.setReInstallChargeId(installChargeId);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}

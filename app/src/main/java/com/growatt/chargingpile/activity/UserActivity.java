package com.growatt.chargingpile.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.Cons;
import com.growatt.chargingpile.util.LoginUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.growatt.chargingpile.util.SmartHomeUtil;
import com.mylhyl.circledialog.CircleDialog;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class UserActivity extends BaseActivity {


    @BindView(R.id.status_bar_view)
    View statusBarView;
    @BindView(R.id.tv_title)
    AppCompatTextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.headerView)
    LinearLayout headerView;
    @BindView(R.id.iv_more)
    ImageView ivMore;
    @BindView(R.id.rl_edit_password)
    ConstraintLayout rlEditPassword;
    @BindView(R.id.tv_phone_title)
    AppCompatTextView tvPhoneTitle;
    @BindView(R.id.tv_phone)
    AppCompatTextView tvPhone;
    @BindView(R.id.iv_phone_more)
    ImageView ivPhoneMore;
    @BindView(R.id.frad_linear)
    View fradLinear;
    @BindView(R.id.rl_edit_phone)
    ConstraintLayout rlEditPhone;
    @BindView(R.id.tv_email_title)
    AppCompatTextView tvEmailTitle;
    @BindView(R.id.tv_email)
    AppCompatTextView tvEmail;
    @BindView(R.id.iv_email_more)
    ImageView ivEmailMore;
    @BindView(R.id.rl_edit_email)
    ConstraintLayout rlEditEmail;
    @BindView(R.id.tv_installemail_title)
    AppCompatTextView tvInstallemailTitle;
    @BindView(R.id.tv_installemail)
    AppCompatTextView tvInstallemail;
    @BindView(R.id.iv_installemail_more)
    ImageView ivInstallemailMore;
    @BindView(R.id.rl_edit_installemail)
    ConstraintLayout rlEditInstallemail;
    @BindView(R.id.tv_nstallphone_title)
    AppCompatTextView tvNstallphoneTitle;
    @BindView(R.id.tv_installphone)
    AppCompatTextView tvInstallphone;
    @BindView(R.id.iv_installphone_more)
    ImageView ivInstallphoneMore;
    @BindView(R.id.rl_edit_installphone)
    ConstraintLayout rlEditInstallphone;
    @BindView(R.id.tv_installaddress_title)
    AppCompatTextView tvInstalladdressTitle;
    @BindView(R.id.tv_installaddress)
    AppCompatTextView tvInstalladdress;
    @BindView(R.id.iv_installaddress)
    ImageView ivInstalladdress;
    @BindView(R.id.rl_edit_installaddress)
    ConstraintLayout rlEditInstalladdress;
    @BindView(R.id.tv_date_title)
    AppCompatTextView tvDateTitle;
    @BindView(R.id.tv_date)
    AppCompatTextView tvDate;
    @BindView(R.id.iv_date_more)
    ImageView ivDateMore;
    @BindView(R.id.rl_edit_date)
    ConstraintLayout rlEditDate;
    @BindView(R.id.logout)
    Button logout;

    private Unbinder bind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        bind = ButterKnife.bind(this);
        initHeaderView();
        initViews();
    }

    private void initViews() {
        String phone = Cons.userBean.getPhone();
        if (!TextUtils.isEmpty(phone)) {
            tvPhone.setText(phone);
        }
        String email = Cons.userBean.getEmail();
        if (!TextUtils.isEmpty(email)) {
            tvEmail.setText(email);
        }

        String installEmail = Cons.userBean.getInstallEmail();
        if (!TextUtils.isEmpty(installEmail)) {
            tvInstallemail.setText(installEmail);
        }


        String installPhone = Cons.userBean.getInstallPhone();
        if (!TextUtils.isEmpty(installPhone)) {
            tvInstallphone.setText(installPhone);
        }


        String installAddress = Cons.userBean.getInstallAddress();
        if (!TextUtils.isEmpty(installAddress)) {
            tvInstalladdress.setText(installAddress);
        }

        String date = Cons.userBean.getInstallDate();
        if (!TextUtils.isEmpty(date)) {
            tvDate.setText(date);
        }

    }


    private void initHeaderView() {
        initToobar(toolbar);
        tvTitle.setTextColor(ContextCompat.getColor(this,R.color.main_theme_text_color));
        tvTitle.setText(R.string.m51账号管理);

    }

    @OnClick({R.id.rl_edit_password, R.id.rl_edit_phone, R.id.rl_edit_email, R.id.rl_edit_installemail, R.id.rl_edit_installphone, R.id.rl_edit_installaddress, R.id.logout, R.id.rl_edit_date})
    public void onClickListners(View view) {
        switch (view.getId()) {
            case R.id.rl_edit_password:
                startActivity(new Intent(UserActivity.this, UpdatepwdActivity.class));
                break;
            case R.id.rl_edit_phone:
                toUpdate(1);
                break;
            case R.id.rl_edit_email:
                toUpdate(2);
                break;
            case R.id.rl_edit_installemail:
                toUpdate(3);
                break;
            case R.id.rl_edit_installphone:
                toUpdate(4);
                break;
            case R.id.rl_edit_installaddress:
                toUpdate(5);
                break;
            case R.id.rl_edit_date:
                toUpdate(6);
                break;
            case R.id.logout:
                LogoutUser();
                break;
        }
    }

    private void LogoutUser() {
        new CircleDialog.Builder()
                .setWidth(0.75f)
                .setTitle(getString(R.string.m318是否注销账户))
                .setText(getString(R.string.m319注销后账户将被删除))
                .configText(params -> {
                    params.textSize = 30;
                })
                .setGravity(Gravity.CENTER).setPositive(getString(R.string.m9确定), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        })
                .setNegative(getString(R.string.m7取消), null)
                .show(getSupportFragmentManager());

    }


    private void deleteUser() {
        JSONObject object = new JSONObject();
        try {
            object.put("command", "deleteUser");
            object.put("userId", SmartHomeUtil.getUserName());
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
                    if (code == 0) {
                        LoginUtil.logout(UserActivity.this);
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


    private void toUpdate(int type) {
        Intent intent = new Intent(UserActivity.this, AmendsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", String.valueOf(type));
        bundle.putString("PhoneNum", Cons.userBean.getPhone());
        bundle.putString("email", Cons.userBean.getEmail());
        bundle.putString("installEmail", Cons.userBean.getInstallEmail());
        bundle.putString("installPhone", Cons.userBean.getInstallPhone());
        bundle.putString("installAddress", Cons.userBean.getInstallAddress());
        bundle.putString("installer", Cons.userBean.getInstaller());
        bundle.putString("installerDate", Cons.userBean.getInstallDate());
        intent.putExtras(bundle);
        startActivityForResult(intent, 103);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 103) {
                String type = data.getStringExtra("type");
                String result = data.getStringExtra("result");
                String date = data.getStringExtra("date");
                if (!TextUtils.isEmpty(result) || !TextUtils.isEmpty(date)) {
                    if ("1".equals(type)) {
                        Cons.userBean.setPhone(result);
                        tvPhone.setText(result);
                    } else if ("2".equals(type)) {
                        Cons.userBean.setEmail(result);
                        tvEmail.setText(result);
                    } else if ("3".equals(type)) {
                        Cons.userBean.setInstallEmail(result);
                        tvInstallemail.setText(result);
                    } else if ("4".equals(type)) {
                        Cons.userBean.setInstallPhone(result);
                        tvInstallphone.setText(result);
                    } else if ("5".equals(type)) {
                        Cons.userBean.setInstallAddress(result);
                        tvInstalladdress.setText(result);
                    } else if ("6".equals(type)) {
                        Cons.userBean.setInstallDate(date);
                        tvDate.setText(date);
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

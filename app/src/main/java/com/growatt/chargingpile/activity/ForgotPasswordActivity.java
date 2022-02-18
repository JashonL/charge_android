package com.growatt.chargingpile.activity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.growatt.chargingpile.BaseActivity;
import com.growatt.chargingpile.R;
import com.growatt.chargingpile.connutil.PostUtil;
import com.growatt.chargingpile.util.MyUtil;
import com.growatt.chargingpile.util.Mydialog;
import com.growatt.chargingpile.util.SmartHomeUrlUtil;
import com.mylhyl.circledialog.CircleDialog;

import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ForgotPasswordActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;



    @BindView(R.id.et_username)
    EditText etUserName;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_phone)
    EditText etPhone;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        ButterKnife.bind(this);
        initHeaderView();
    }

    private void initHeaderView() {

        initToobar(toolbar);
        tvTitle.setText(getString(R.string.m22忘记密码));

     /*   tvTitle.setTextColor(ContextCompat.getColor(this, R.color.title_1));
        tvTitle.setText(getString(R.string.m22忘记密码));
        //设置字体加粗
        tvTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, v -> finish());*/
    }

    @OnClick(R.id.btFinish)
    public void onClickListner(View view) {
        switch (view.getId()) {
            case R.id.btFinish:
                repeatPassword();
                break;
        }

    }




    /**
     * 重置密码
     */
    public void repeatPassword() {
        String username = etUserName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            toast(R.string.m25请输入用户名);
            return;
        }
        if (TextUtils.isEmpty(email)){
            toast(R.string.m35请输入正确邮箱格式);
            return;
        }

        //校验邮箱
        if (!MyUtil.regexCheckEmail(email)) {
            toast(R.string.m35请输入正确邮箱格式);
            return;
        }

        Mydialog.Show(this);
        JSONObject object = new JSONObject();
        try {
            object.put("cmd", "invalidPassword");//cmd  注册
            object.put("userId", username);//用户名
            object.put("phone", phone);//密码
            object.put("email", email);//密码
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
                        String a = object.optString("data");
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        new CircleDialog.Builder()
                                .setWidth(0.75f)
                                .setTitle(getString(R.string.m27温馨提示))
                                .setText(getString(R.string.m密码已初始化)+":"+a)
                                .setGravity(Gravity.CENTER).setPositive(getString(R.string.m9确定), v -> {
                                    finish();

                        })
                                .setNegative(getString(R.string.m7取消), null)
                                .show(fragmentManager);

                    }else {
                        String errorMsg = object.optString("data");
                        if (!TextUtils.isEmpty(errorMsg))
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

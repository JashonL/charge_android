package com.yingli.chargingpile.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.yingli.chargingpile.BaseActivity;
import com.yingli.chargingpile.R;

import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AgreementActivity extends BaseActivity {

    @BindView(R.id.headerView)
    View headerView;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.textView1)
    TextView proText;

    private String agreement;
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        bind=ButterKnife.bind(this);
        initHeaderView();
        SetViews();
    }

    private void initHeaderView() {
        setHeaderImage(headerView, R.drawable.back, Position.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setHeaderTitle(headerView, getString(R.string.m54用户协议),R.color.title_1,true);
    }


    private void SetViews() {
        if (getLanguage() == 0) {
            agreement = "agreement_cn.txt";
        } else {
            agreement = "agreement.txt";
        }
        try {
            InputStream inputStream = getAssets().open(agreement);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            String resultString = new String(buffer, "utf-8");
            resultString = resultString.replace("\\n", "\n");
            proText.setText(resultString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

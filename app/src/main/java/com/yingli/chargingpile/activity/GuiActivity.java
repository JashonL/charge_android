package com.yingli.chargingpile.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.yingli.chargingpile.BaseActivity;
import com.yingli.chargingpile.R;
import com.yingli.chargingpile.application.MyApplication;
import com.yingli.chargingpile.util.Cons;
import com.yingli.chargingpile.util.SharedPreferencesUnit;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class GuiActivity extends BaseActivity {

    private ViewPager guideViewPager;
    private Button guideButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowTitleByActivity();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui);
        String chargeId=getIntent().getStringExtra("chargeId");
        int isFrist = SharedPreferencesUnit.getInstance(MyApplication.context).getInt("num");
        if (isFrist ==0){
            setUpView();
            addListener();
            SharedPreferencesUnit.getInstance(this).putInt("num",1);
        }else {
            if (Cons.userBean!=null){
                Intent intent=new Intent(this,ChargingMainActivity.class);
                intent.putExtra("chargeId",chargeId);
                jumpTo(intent,true);
            }else {
                jumpTo(YingliLoginActivity.class,true);

            }
        }

    }

    private void addListener() {
        guideViewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                if (arg0 == 2) {
                    guideButton.setVisibility(View.VISIBLE);
                } else {
                    guideButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        guideButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                GuiActivity.this.startActivity(new Intent(GuiActivity.this,
                        YingliLoginActivity.class));
                finish();
            }
        });
    }

    private void setUpView() {
        guideButton = (Button) findViewById(R.id.guide_button);
        guideViewPager = (ViewPager) findViewById(R.id.guide_viewpage);
        List<View> guides = new ArrayList<View>();

        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageBitmap(readBitMap(this, R.drawable.gui_1));
        guides.add(imageView);

        ImageView imageView1 = new ImageView(this);
        imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView1.setImageBitmap(readBitMap(this, R.drawable.gui_2));
        guides.add(imageView1);

        ImageView imageView11 = new ImageView(this);
        imageView11.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView11.setImageBitmap(readBitMap(this, R.drawable.gui_3));
        guides.add(imageView11);

        GuideAdapter guideAdapter = new GuideAdapter(guides);
        guideViewPager.setAdapter(guideAdapter);
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }


    class GuideAdapter extends PagerAdapter {
        private List<View> views;

        public GuideAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        // ɾ��view
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            object = null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }
    }
}
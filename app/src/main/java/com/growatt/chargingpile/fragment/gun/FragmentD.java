package com.growatt.chargingpile.fragment.gun;

import android.util.Log;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.fragment.BaseFragment;

/**
 * Created：2021/8/24 on 14:43:56
 * Author:gaideng on admin
 * Description:GUN D
 */

public class FragmentD extends BaseFragment {

    private static String TAG = FragmentD.class.getSimpleName();

    @Override
    protected Object setRootView() {
        return R.layout.fragment_gun;
    }

    @Override
    protected void initWidget() {
        Log.d(TAG, "initWidget: D");
    }

    @Override
    protected void getGunInfoData() {
        Log.d(TAG, "requestData: "+ pActivity.mDataBean.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: D");
    }
}

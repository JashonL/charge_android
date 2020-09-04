package com.growatt.chargingpile.util;


import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.growatt.chargingpile.R;
import com.growatt.chargingpile.listener.OnCirclerDialogListener;
import com.mylhyl.circledialog.CircleDialog;

/**
 * Created by dg on 2017/6/14.
 */

public class DialogUtil {


    public static void circlerDialog(final FragmentActivity act, String text, int result, final boolean isFinish, final OnCirclerDialogListener circlerDialogListener) {
        try{
            FragmentManager supportFragmentManager = act.getSupportFragmentManager();
            new CircleDialog.Builder()
                    .setCancelable(false)
                    .setWidth(0.7f)
                    .setTitle(act.getString(R.string.m27温馨提示))
                    .setText(text)
                    .setPositive(act.getString(R.string.m9确定), v -> {
                        if (isFinish){
                            act.finish();
                        }else {
                            if (circlerDialogListener != null){
                                circlerDialogListener.onCirclerPositive();
                            }
                        }
                    })
                    .show(supportFragmentManager);
        }catch (Exception e){
            e.printStackTrace();
        }
    }




}

package com.growatt.chargingpile.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.growatt.chargingpile.R;

import java.util.Arrays;

/**
 * Created：2021/8/27 on 9:39:07
 * Author: on admin
 * Description:时间设置
 */

public class TimeSetDialog extends DialogFragment {

    private static String TAG = TimeSetDialog.class.getSimpleName();

    private String mTitle;
    private String mTime;
    private int mType;
    private static TimeCallBack mTimeCallBack;

    private NumberPickerView mNumberHour;
    private NumberPickerView mNumberMinute;

    private TextView mTvClear;

    public TimeSetDialog() {
    }

    public static TimeSetDialog newInstance(int type, String title, String time, TimeCallBack timeCallBack) {
        TimeSetDialog fragment = new TimeSetDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("time", time);
        args.putInt("type", type);
        fragment.setArguments(args);
        mTimeCallBack = timeCallBack;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
        if (getArguments() != null) {
            mTitle = getArguments().getString("title");
            mTime = getArguments().getString("time");
            mType = getArguments().getInt("type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_setting_time, null);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mNumberHour = view.findViewById(R.id.np_hour);
        mNumberMinute = view.findViewById(R.id.np_minute);
        mTvClear = view.findViewById(R.id.tv_clear);
        ImageView ivCancel = view.findViewById(R.id.iv_cancel);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(mTitle);


        if (tvTitle.getText().toString().equals(getString(R.string.charging_time))) {
            view.findViewById(R.id.tv_hour).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tv_min).setVisibility(View.VISIBLE);

            if (mTime.length() < 4) {
                initPicker(mNumberHour, 0, 23, 0, "%02d", false);
                initPicker(mNumberMinute, 0, 11, 0, "%02d", true);
                mTvClear.setVisibility(View.INVISIBLE);
            } else {
                initPicker(mNumberHour, 0, 23, Integer.valueOf(mTime.substring(0, 2)), "%02d", false);
                initPicker(mNumberMinute, 0, 11, Integer.valueOf(mTime.substring(3, 5)), "%02d", true);
                mTvClear.setVisibility(View.VISIBLE);
            }
        } else {
            if (mTime.length() < 4) {
                initPicker(mNumberHour, 0, 23, 0, "%02d", false);
                initPicker(mNumberMinute, 0, 59, 0, "%02d", false);
            } else {
                initPicker(mNumberHour, 0, 23, Integer.valueOf(mTime.substring(0, 2)), "%02d", false);
                initPicker(mNumberMinute, 0, 59, Integer.valueOf(mTime.substring(4, 5)), "%02d", false);
            }
        }

        if (mType != 1) {
            mTvClear.setVisibility(View.INVISIBLE);
        }



        ivCancel.setOnClickListener(v -> {
            dismiss();
        });

        tvConfirm.setOnClickListener(v -> {
            mTimeCallBack.confirm(mNumberHour.getContentByCurrValue(), mNumberMinute.getContentByCurrValue());
            dismiss();
        });

        mTvClear.setOnClickListener(v -> {
            mTimeCallBack.confirm("", "");
            dismiss();
        });


    }

    /**
     * @param picker
     * @param maxValue
     * @param minValue
     */
    private void initPicker(NumberPickerView picker, int minValue, int maxValue, int showValue,
                            String format, boolean isProduct) {
        String[] arrayValue;
        int[] arrayInt = new int[maxValue - minValue + 1];

        if (isProduct) {
            for (int i = 0; i <= maxValue - minValue; i++) {
                arrayInt[i] = i * 5;
            }
            int position = Arrays.binarySearch(arrayInt, showValue);
            position = (position >= 0 && position < arrayInt.length) ? position : (arrayInt.length - 1);

            arrayValue = new String[maxValue - minValue + 1];
            for (int i = 0; i <arrayValue.length; i++) {
                String str = String.format(format, i * 5);
                arrayValue[i] = str;
            }

            picker.setDisplayedValuesAndPickedIndex(arrayValue, position, true);
        } else {

            for (int i = 0; i <= maxValue - minValue; i++) {
                arrayInt[i] = i + minValue;
            }
            int index = Arrays.binarySearch(arrayInt, showValue);
            index = (index >= 0 && index < arrayInt.length) ? index : (arrayInt.length - 1);


            arrayValue = new String[maxValue - minValue + 1];
            for (int i = 0; i <= maxValue - minValue; i++) {
                String str = String.format(format, i + minValue);
                arrayValue[i] = str;
            }
            picker.setDisplayedValuesAndPickedIndex(arrayValue, index, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.xa688);
        params.height = getResources().getDimensionPixelSize(R.dimen.xa683);
        params.gravity = Gravity.BOTTOM;
        params.y = getResources().getDimensionPixelSize(R.dimen.xa50);
        getDialog().getWindow().setAttributes(params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimeCallBack = null;
    }

    public interface TimeCallBack {
        void confirm(String hour, String minute);
//
//        void confirm(String hour, String minute);
    }


}

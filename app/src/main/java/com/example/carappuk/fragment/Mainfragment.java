package com.example.carappuk.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.carappuk.R;

import java.util.Calendar;
import java.util.TimeZone;


public class Mainfragment extends Fragment {

    private static final String Tag = "Mainfragment";

    private View mBaseView;
    private TextView mTextTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBaseView = inflater.inflate(R.layout.fragment_main, container, false);
        initView();
        return mBaseView;
    }

    private void initView() {
        mTextTime = mBaseView.findViewById(R.id.tx_time);
        setDate();
        new TimeThread().start();

    }
    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);// An msg is sent to the mHandler every 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    //Process messages and update the UI in the main thread
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("hh:mm:ss", sysTime);
                    mTextTime.setText(sysTimeStr);
                default:
                    break;
            }
        }
    };

    private void setDate() {
        String mYear;
        String mMonth;
        String mDay;
        String mWay;

        TextView mTxDate = mBaseView.findViewById(R.id.tx_date);
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR));
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "Sunday";
        } else if ("2".equals(mWay)) {
            mWay = "Monday";
        } else if ("3".equals(mWay)) {
            mWay = "Tuesday";
        } else if ("4".equals(mWay)) {
            mWay = "Wednesday";
        } else if ("5".equals(mWay)) {
            mWay = "Thursday";
        } else if ("6".equals(mWay)) {
            mWay = "Friday";
        } else if ("7".equals(mWay)) {
            mWay = "Saturday";
        }

        if ("1".equals(mMonth)) {
            mMonth = "Jan";
        } else if ("2".equals(mMonth)) {
            mMonth = "Feb";
        } else if ("3".equals(mMonth)) {
            mMonth = "Mar";
        } else if ("4".equals(mMonth)) {
            mMonth = "Apr";
        } else if ("5".equals(mMonth)) {
            mMonth = "May";
        } else if ("6".equals(mMonth)) {
            mMonth = "Jun";
        } else if ("7".equals(mMonth)) {
            mMonth = "Jul";
        } else if ("8".equals(mMonth)) {
            mMonth = "Aug";
        } else if ("9".equals(mMonth)) {
            mMonth = "Sept";
        } else if ("10".equals(mMonth)) {
            mMonth = "Oct";
        } else if ("11".equals(mMonth)) {
            mMonth = "Nov";
        } else if ("12".equals(mMonth)) {
            mMonth = "Dec";
        }
        mTxDate.setText(mMonth + '-' + mDay + "\n" + mWay);

    }

}
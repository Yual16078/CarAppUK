package com.example.carappuk.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.carappuk.R;
import com.example.carappuk.request.NetWorkInterface;
import com.example.carappuk.request.WeatherReturns;


import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Mainfragment extends Fragment {

    private static final String Tag = "Mainfragment";
    private View mBaseView;
    private TextView mTextTime;
    private TextView txTemperature;
    private TextView txAddress;
    private TextView txWindScale;
    private TextView txWeather;
    private TextView txVis;
    private TextView txHumidity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBaseView = inflater.inflate(R.layout.fragment_main, container, false);
        initView();
        return mBaseView;
    }

    private void initView() {
        txTemperature = mBaseView.findViewById(R.id.tx_temperature);
        txWindScale = mBaseView.findViewById(R.id.tx_windscale);
        txWeather = mBaseView.findViewById(R.id.tx_weather);
        txVis = mBaseView.findViewById(R.id.tx_vis);
        txHumidity = mBaseView.findViewById(R.id.tx_humidity);
        mTextTime = mBaseView.findViewById(R.id.tx_time);
        Spinner spinnerAddress = mBaseView.findViewById(R.id.spinner_address);
        spinnerAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String address = adapterView.getItemAtPosition(i).toString();
                switch (address) {
                    case "London":
                        setWeather("BA333");
                        break;
                    case "Edinburgh":
                        setWeather("7ADAF");
                        break;
                    case "Cardiff":
                        setWeather("7DDB7");
                        break;
                    case "Belfast":
                        setWeather("EB829");
                        break;
                    case "Birmingham":
                        setWeather("DAA51");
                        break;
                    case "Liverpool":
                        setWeather("EEC78");
                        break;
                    case "Oxford":
                        setWeather("9BAB9");
                        break;
                    case "Cambridge":
                        setWeather("7E2D");
                        break;
                    case "Glasgow":
                        setWeather("E2080");
                        break;
                    case "Sheffield":
                        setWeather("BAC6B");
                        break;
                    case "Plymouth":
                        setWeather("62FEF");
                        break;
                    case "Manchester":
                        setWeather("38660");
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setDate();
        setWeather("BA333");

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

    private void setWeather(String addressCode){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://devapi.qweather.com/v7/weather/") // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();

        // 步骤5:创建 网络请求接口 的实例
        NetWorkInterface request = retrofit.create(NetWorkInterface.class);

        //对 发送请求 进行封装(设置需要翻译的内容)
        Call<WeatherReturns> call = request.getCall(addressCode);

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<WeatherReturns>() {

            //请求成功时回调
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WeatherReturns> call, Response<WeatherReturns> response) {
                // 步骤7：处理返回的数据结果：输出翻译的内容
                assert response.body() != null;
                txVis.setText(response.body().getNow().getVis());
                txTemperature.setText(response.body().getNow().getTemp()+"°");
                txHumidity.setText(response.body().getNow().getHumidity()+"%");
                txWindScale.setText(response.body().getNow().getHumidity());
                txWeather.setText(response.body().getNow().getText());
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<WeatherReturns> call, Throwable throwable) {
                System.out.println("请求失败");
                System.out.println(throwable.getMessage());
            }
        });


    }


}
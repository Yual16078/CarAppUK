package com.example.carappuk.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carappuk.MainActivity;
import com.example.carappuk.R;
import com.example.carappuk.bluetooth.JsonParser;
import com.example.carappuk.bluetooth.VoiceControlActivity;
import com.example.carappuk.request.NetWorkInterface;
import com.example.carappuk.request.WeatherReturns;
import com.example.carappuk.util.VolumeUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.RecognizerResult;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Mainfragment extends Fragment {

    private static final String TAG = "Mainfragment";
    private View mBaseView;
    private TextView mTextTime;
    private TextView txTemperature;
    private TextView txAddress;
    private TextView txWindScale;
    private TextView txWeather;
    private TextView txVis;
    private TextView txHumidity;

    private SpeechRecognizer mIat;// ??????????????????
    private RecognizerDialog mIatDialog;// ????????????UI
    private TextToSpeech textToSpeech;
    private String voiceResult;
    private int temp = 0; // ??????????????????=???bug??????

    // ???HashMap??????????????????
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private SharedPreferences mSharedPreferences;//??????


    private String mEngineType = SpeechConstant.TYPE_CLOUD;// ????????????
    private String language = "en_us";//????????????

    private TextView tvResult;//????????????
    private ImageButton btnStart;//????????????
    private String resultType = "json";//????????????????????????

    private VolumeUtil volumeUtil;
    private View mMainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBaseView = inflater.inflate(R.layout.fragment_main, container, false);
        mMainActivity = inflater.inflate(R.layout.activity_main, container, false);
        initView();
        initvoiceBroadcast();
        return mBaseView;
    }

    private void initView() {
        txTemperature = mBaseView.findViewById(R.id.tx_temperature);
        txWindScale = mBaseView.findViewById(R.id.tx_windscale);
        txWeather = mBaseView.findViewById(R.id.tx_weather);
        txVis = mBaseView.findViewById(R.id.tx_vis);
        txHumidity = mBaseView.findViewById(R.id.tx_humidity);
        mTextTime = mBaseView.findViewById(R.id.tx_time);

        mBaseView.findViewById(R.id.view_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showClock();
            }
        });
        Spinner spinnerAddress = mBaseView.findViewById(R.id.spinner_address);

        volumeUtil = new VolumeUtil(getContext());
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
                    case "Brighton":
                        setWeather("857EC");
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


        // voice

        SpeechUtility.createUtility(getContext(), "appid=63014159");
        btnStart = mBaseView.findViewById(R.id.bt_voice_control);
        initPermission();//????????????
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                Toast.makeText(getActivity(), "Say what you want to do", Toast.LENGTH_LONG).show();
//                Toast.makeText(getActivity(), "Want to adjust the volume???Please say ???Turn the volume up/Turn the volume down???", Toast.LENGTH_LONG).show();
//                Toast.makeText(getActivity(), "Want to know the time???Please say ???What time is it???", Toast.LENGTH_LONG).show();
//                Toast.makeText(getActivity(), "Want to turn on/off the air conditioner???Please say ???Turn on/off the air conditioner???", Toast.LENGTH_LONG).show();
                showNormalDialog();

            }
        });//??????????????????
        // ??????SpeechRecognizer????????????????????????????????????????????????
        mIat = SpeechRecognizer.createRecognizer(getContext(), mInitListener);
        // ??????UI????????????????????????sdk??????????????????notice.txt,?????????????????????????????????
        mIatDialog = new RecognizerDialog(getContext(), mInitListener);
        mSharedPreferences = getActivity().getSharedPreferences("ASR", Activity.MODE_PRIVATE);


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
    private Handler mHandler = new Handler() {
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

    private void setWeather(String addressCode) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://devapi.qweather.com/v7/weather/") // ?????? ???????????? Url
                .addConverterFactory(GsonConverterFactory.create()) //????????????Gson??????(??????????????????)
                .build();

        // ??????5:?????? ?????????????????? ?????????
        NetWorkInterface request = retrofit.create(NetWorkInterface.class);

        //??? ???????????? ????????????(???????????????????????????)
        Call<WeatherReturns> call = request.getCall(addressCode);

        //??????6:??????????????????(??????)
        call.enqueue(new Callback<WeatherReturns>() {

            //?????????????????????
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<WeatherReturns> call, Response<WeatherReturns> response) {
                // ??????7??????????????????????????????????????????????????????
                assert response.body() != null;
                txVis.setText(response.body().getNow().getVis());
                txTemperature.setText(response.body().getNow().getTemp() + "??");
                txHumidity.setText(response.body().getNow().getHumidity() + "%");
                txWindScale.setText(response.body().getNow().getHumidity());
                txWeather.setText(response.body().getNow().getText());
            }

            //?????????????????????
            @Override
            public void onFailure(Call<WeatherReturns> call, Throwable throwable) {
                System.out.println("????????????");
                System.out.println(throwable.getMessage());
            }
        });


    }


    //voicecontrol


    /**
     * android 6.0 ??????????????????????????????
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(getActivity(), perm)) {
                toApplyList.add(perm);
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), toApplyList.toArray(tmpList), 123);
        }

    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // ?????????android 6.0???????????????????????????????????????????????????
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * ?????????????????????
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showMsg("??????????????????????????????" + code + "");
            }
        }
    };


    /**
     * ??????UI?????????
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            printResult(recognizerResult);//??????????????????
            temp += 1;

            if (temp == 2) {
                textToSpeech.speak(voiceResult,
                        TextToSpeech.QUEUE_ADD, null);
                temp = 0;
            }
        }

        /**
         * ??????????????????.
         */
        public void onError(SpeechError error) {
            showMsg(error.getPlainDescription(true));
        }

    };

    /**
     * ????????????
     *
     * @param msg
     */
    private void showMsg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * ????????????
     *
     * @param results
     */
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // ??????json????????????sn??????
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        //tvResult.setText(resultBuffer.toString());//??????????????????

        switch (resultBuffer.toString()) {
            case " Turn the volume up":
                volumeUtil.setMediaVolume(volumeUtil.getMediaVolume() + 3);
                voiceResult = "Ok,The volume has been increased";
                break;
            case " Turn the volume down":
                volumeUtil.setMediaVolume(volumeUtil.getMediaVolume() - 3);
                voiceResult = "Ok,The volume has been reduced";
                break;
            case " What time is it":
                voiceResult = "Now the time is " + mTextTime.getText().toString();
                break;
            case " Turn on the air condition":
                TextView t1 = getActivity().findViewById(R.id.tx_temperature_left);
                SeekBar s1 = getActivity().findViewById(R.id.seek_bar_left);
                t1.setText("22C??");
                s1.setProgress(22);
                TextView t2 = getActivity().findViewById(R.id.tx_temperature_right);
                SeekBar s2 = getActivity().findViewById(R.id.seek_bar_right);
                t2.setText("22C??");
                s2.setProgress(22);
                voiceResult = "Air conditioning is turned on";
                break;
            case " Turn off the air condition":
                TextView t3 = getActivity().findViewById(R.id.tx_temperature_left);
                t3.setText("OFF");
                TextView t4 = getActivity().findViewById(R.id.tx_temperature_right);
                t4.setText("OFF");
                voiceResult = "Air conditioning is turned off";
                break;
            default:
                voiceResult = "Sorry I don't understand";

                break;
        }

    }

    /**
     * ????????????
     *
     * @return
     */
    public void setParam() {
        // ????????????
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // ??????????????????
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // ????????????????????????
        mIat.setParameter(SpeechConstant.RESULT_TYPE, resultType);

        if (language.equals("en_us")) {
            String lag = mSharedPreferences.getString("iat_language_preference",
                    "english");
            Log.e(TAG, "language:" + language);// ????????????
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            // ??????????????????
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        } else {

            mIat.setParameter(SpeechConstant.LANGUAGE, language);
        }
        Log.e(TAG, "last language:" + mIat.getParameter(SpeechConstant.LANGUAGE));

        //??????????????????dialog???????????????????????????
        mIat.setParameter("view_tips_plain", "false");

        // ?????????????????????:????????????????????????????????????????????????????????????????????????
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // ?????????????????????:?????????????????????????????????????????????????????????????????????????????????????????? ??????????????????
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // ??????????????????,?????????"0"?????????????????????,?????????"1"?????????????????????
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "0"));

        // ???????????????????????????????????????????????????pcm???wav??????????????????sd????????????WRITE_EXTERNAL_STORAGE??????
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }


    // ????????????
    private void initvoiceBroadcast() {
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    Log.d(TAG, "init success");
                } else {
                    Log.d(TAG, "init fail");
                }
            }
        });
        //????????????
        //????????????
        textToSpeech.setPitch(1.0f);
        //???????????????1.0???????????????
        textToSpeech.setSpeechRate(0.8f);
    }

    // ?????????????????? ??????????????????

    private void showNormalDialog() {
        /* @setIcon ?????????????????????
         * @setTitle ?????????????????????
         * @setMessage ???????????????????????????
         * setXXX????????????Dialog???????????????????????????????????????
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getContext());
        normalDialog.setMessage("Say what you want to do\nWant to adjust the volume???Please say ???Turn the volume up/Turn the volume down???\nWant to know the time???Please say ???What time is it???\nWant to turn on/off the air conditioner???Please say ???Turn on/off the air conditioner???");
        normalDialog.setPositiveButton("Sure",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (null == mIat) {
                            // ???????????????????????? 21001 ??????????????????????????????
                            showMsg("?????????????????????????????? libmsc.so ??????????????????????????? createUtility ???????????????");
                            return;
                        }

                        mIatResults.clear();//????????????
                        setParam(); // ????????????
                        mIatDialog.setListener(mRecognizerDialogListener);//????????????
                        mIatDialog.show();// ???????????????
                        TextView txt = (TextView) mIatDialog.getWindow().getDecorView().findViewWithTag("textlink");
                        txt.setText("");
                    }
                });
        normalDialog.setNegativeButton("Close",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        normalDialog.show();
    }

    private void showClock() {

        // dialog
        final AlertDialog.Builder alertDialog7 = new AlertDialog.Builder(getContext());
        View view1 = View.inflate(getContext(), R.layout.analogclock, null);
        alertDialog7.setView(view1)
                    .create();
        final AlertDialog show = alertDialog7.show();


    }
}
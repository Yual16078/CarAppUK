package com.example.carappuk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.carappuk.fragment.AmusementFragment;
import com.example.carappuk.fragment.CameraFragment;
import com.example.carappuk.fragment.Mainfragment;
import com.example.carappuk.fragment.MusicFragment;
import com.example.carappuk.fragment.NavigationFragment;
import com.example.carappuk.util.VolumeUtil;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String Tag = "MainActivity";
    private ImageButton ic_bottom1;
    private ImageButton ic_bottom2;
    private ImageButton ic_bottom3;
    private ImageButton ic_bottom4;
    private ImageButton ic_bottom5;
    private int[] sign = {0,0,0,0,0};
    private SeekBar mSeekBarLeft;
    private SeekBar mSeekBarRight;
    private TextView mTextTemperatureLeft;
    private TextView mTextTemperatureRight;
    private ImageButton bt_home;
    private ImageButton bt_navigation;
    private ImageButton bt_amusement;
    private ImageButton bt_music;
    private ImageButton bt_camera;
    private LinearLayout control_volume;
    private TextView tx_volume;
    private SeekBar seekBarVolume;
    private VolumeUtil volumeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ic_bottom1 = findViewById(R.id.ic_bottom1);
        ic_bottom2 = findViewById(R.id.ic_bottom2);
        ic_bottom3 = findViewById(R.id.ic_bottom3);
        ic_bottom4 = findViewById(R.id.ic_bottom4);
        ic_bottom5 = findViewById(R.id.ic_bottom5);
        bt_home = findViewById(R.id.bt_home);
        bt_navigation = findViewById(R.id.bt_navigation);
        bt_amusement = findViewById(R.id.bt_amusement);
        bt_music = findViewById(R.id.bt_music);
        bt_camera = findViewById(R.id.bt_camera);
        bt_home.setOnClickListener(this);
        bt_navigation.setOnClickListener(this);
        bt_amusement.setOnClickListener(this);
        bt_music.setOnClickListener(this);
        bt_camera.setOnClickListener(this);


        seekBarVolume = findViewById(R.id.seek_bar_volume);
        seekBarVolume.setVisibility(View.INVISIBLE);
        volumeUtil = new VolumeUtil(MainActivity.this);


        ic_bottom1.setOnClickListener(this);
        ic_bottom2.setOnClickListener(this);
        ic_bottom3.setOnClickListener(this);
        ic_bottom4.setOnClickListener(this);
        ic_bottom5.setOnClickListener(this);
        mSeekBarLeft = findViewById(R.id.seek_bar_left);
        mSeekBarRight = findViewById(R.id.seek_bar_right);
        mTextTemperatureLeft = findViewById(R.id.tx_temperature_left);
        mTextTemperatureRight = findViewById(R.id.tx_temperature_right);
        replaceFragment(new Mainfragment());
        mSeekBarLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTextTemperatureLeft.setText(i + "C°");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekBarRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTextTemperatureRight.setText(i + "C°");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initBluetooth() {

        stopSource();
        startSink();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_bottom1:
                initBluetooth();
                if (sign[0] == 0){
                    ic_bottom1.setBackgroundResource(R.mipmap.ic_bottom1_ture);
                    sign[0] = 1;
                }
                else {
                    ic_bottom1.setBackgroundResource(R.mipmap.ic_bottom1);
                    sign[0] = 0;
                }
                break;
            case R.id.ic_bottom2:
                if (sign[1] == 0){
                    ic_bottom2.setBackgroundResource(R.mipmap.ic_bottom2_ture);
                    sign[1] = 1;
                }
                else {
                    ic_bottom2.setBackgroundResource(R.mipmap.ic_bottom2);
                    sign[1] = 0;
                }
                break;
            case R.id.ic_bottom3:
                if (sign[2] == 0){
                    ic_bottom3.setBackgroundResource(R.mipmap.ic_bottom3_ture);
                    sign[2] = 1;
                }
                else {
                    ic_bottom3.setBackgroundResource(R.mipmap.ic_bottom3);
                    sign[2] = 0;
                }
                break;
            case R.id.ic_bottom4:
                if (sign[3] == 0){
                    ic_bottom4.setBackgroundResource(R.mipmap.ic_bottom4_ture);
                    sign[3] = 1;
                }
                else {
                    ic_bottom4.setBackgroundResource(R.mipmap.ic_bottom4);
                    sign[3] = 0;
                }
                break;
            case R.id.ic_bottom5:
                if (sign[4] == 0){
                    seekBarVolume.setMax(volumeUtil.getMediaMaxVolume());
                    seekBarVolume.setProgress(volumeUtil.getMediaVolume());
                    seekBarVolume.setVisibility(View.VISIBLE);
                    sign[4] = 1;
                    seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            volumeUtil.setMediaVolume(i);

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                } else {
                    seekBarVolume.setVisibility(View.INVISIBLE);
                    sign[4] = 0;
                }
                break;
            case R.id.bt_navigation:
                replaceFragment(new NavigationFragment());
                break;
            case R.id.bt_home:
                replaceFragment(new Mainfragment());
                break;
            case R.id.bt_music:
                replaceFragment(new MusicFragment());
                break;
            case R.id.bt_camera:
                replaceFragment(new CameraFragment());
                break;
            case R.id.bt_amusement:
                replaceFragment(new AmusementFragment());
                break;
        }
    }
    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.framelayout_right,fragment);

        transaction.addToBackStack(null);

        transaction.commit();
    }

    // bluetooth

    public void stopSource() {
        System.out.println("stop");
        Intent intent = new Intent();
        intent.setAction("android.bluetooth.IBluetoothA2dp");
        intent.setPackage("com.android.bluetooth");
        intent.putExtra("action", "com.android.bluetooth.btservice.action.STATE_CHANGED");
        intent.putExtra(BluetoothAdapter.EXTRA_STATE, 10);
        startService(intent);
        System.out.println("222");
    }

    public void startSink() {
        Intent intent = new Intent();
        intent.setAction("android.bluetooth.IBluetoothA2dpSink");
        intent.setPackage("com.android.bluetooth");
        intent.putExtra("action", "com.android.bluetooth.btservice.action.STATE_CHANGED");
        intent.putExtra(BluetoothAdapter.EXTRA_STATE, 12);
        startService(intent);
        System.out.println("222333");
    }
}
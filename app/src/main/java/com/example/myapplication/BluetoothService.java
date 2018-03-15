package com.example.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.UUID;

/**
 * Created by 안탄 on 2018-03-15.
 */

// 블루투스와 관련된 모든 작업들을 처리
    // 처리를 마치고 상채값을 핸들러에게 넘겨주게 되면 핸들러는 그 값을 MainActivity로 전달

public class BluetoothService {

    // intent request code
    private static final int REQUEST_CONNEXT_DEVICE =1 ;
    private static final int REQUEST_ENABLE_BT = 2;

    private static final String TAG = "BluetoothService";

    //RFCOMM Protocol
    private static final UUID MY_UUID = UUID.fromString(("00001101-0000-1000-8000-00805F9B34FB"));

    public BluetoothAdapter btAdapter;
    private Activity mActivity;
    private Handler mHandler;

    //Constructors
    public BluetoothService(Activity ac, Handler h){
        mActivity = ac;
        mHandler = h;

        // BluetoothAdapter 얻기
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void scanDevice(){
        Log.d(TAG, "Scan Device");

        Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
        mActivity.startActivityForResult(serverIntent, REQUEST_CONNEXT_DEVICE);
    }


    public void enableBluetooth(){
        Log.i(TAG, "Check the enable Bluetooth");

        if(btAdapter.isEnabled()){
            //기기의 블루투스 상태가 On일 경우
            Log.d(TAG, "Bluetooth Enable Now");

            // 블루투스 장치 검색
            scanDevice();
        }
    }


}

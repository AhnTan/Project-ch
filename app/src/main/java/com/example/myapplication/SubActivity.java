package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SubActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter = null;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //디바이스가 발견됨 (이름이 밝혀졌다)
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                //인텐트에 포함된 Bluetooth 디바이스 오브젝트를 얻는다
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //디바이스명과 MAC 주소 출력
                Log.i("Log " , device.getName() + "," + device.getAddress());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        // Bluetooth 인텐트 필터 작성
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // 브로드캐스트 리시버 등록
        registerReceiver(mReceiver, filter);
        // BluetothAdapter 인스턴스를 얻는다
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 주변 Bluetooth 디바이스 검색 시작
        mBluetoothAdapter.startDiscovery();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 디바이스 검색 중지
        mBluetoothAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver);
    }


}

package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

   private BluetoothAdapter mBluetoothAdapter = null;

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;




    private static final String TAG = "MAIN";


    private BluetoothService bluetoothService_obj = null;


    //public String ip = "localhost";
    public String ip = "localhost";
    public int port = 30000;


    TextView textView;

    private final Handler mHandler = new Handler(){
        //핸들러 기능을 수행할 클래스(handleMessage)
        @Override
        public void handleMessage(Message msg){
            //BluetoothService로부터 메세지(msg)를 받는다.
            super.handleMessage(msg);

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("근처에 있는 블루투스 기기 검색을 위해 위치권한이 필요합니다.")
                .setDeniedMessage("거부하면 못쓴다 얄짤없다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();


        if(bluetoothService_obj == null) {
            bluetoothService_obj = new BluetoothService(this, mHandler);
            if(bluetoothService_obj.getDeviceState()==true){
                System.out.println("블루투스 활성?? 지원 ok");
            }
        }

       // BluetoothAdapter 인스턴스를 얻는다.
        mBluetoothAdapter = bluetoothService_obj.btAdapter;
        // 단말기가 Bluetooth를 지원하지않는다
        if(mBluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "블루투스으으으!!!", Toast.LENGTH_SHORT);
        }
        // Bluetooth는 지원되지만 활성화되어 있지 않다. BlueTooth를 활성화하는 인텐트 작성
        else if(!mBluetoothAdapter.isEnabled()){
            Toast.makeText(getApplicationContext(), "블루투스 is ok", Toast.LENGTH_SHORT);
            Intent intent = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);


        }


        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBluetoothAdapter.isEnabled()) {
                    bluetoothService_obj.enableBluetooth();
                }
            }
        });
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data){

        switch(requestCode){
            case REQUEST_ENABLE_BT:
                    // Bluetooth가 활성화되었음을 표시
                    if(resultCode == Activity.RESULT_OK){
                        Log.d(TAG, "Bluetooth is enable");
                        bluetoothService_obj.scanDevice(); // 기기 검색을 요청하는 메소드 추가
                    }
                    // Bluetooth를 활성화 할 수 없음(사용자가 취소한 경우)
                    else{
                        Log.d(TAG, "Bluetooth is not enable");
                    }
                     break;
            case REQUEST_CONNECT_DEVICE:  //DeviceListActivity reurn with a devices to connect
                if(resultCode == Activity.RESULT_OK){
                    //bluetoothService_obj.getDeviceinfo(data);
                }
                break;

        }

    }



}


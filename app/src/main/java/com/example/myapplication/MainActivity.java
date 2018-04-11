package com.example.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
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

    private Messenger mServiceMessenger = null;
    private boolean mIsBound;


   private BluetoothAdapter mBluetoothAdapter = null;

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private final int MY_PERMISSION_REQUEST_STORAGE = 100;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";


    private static final String TAG = "MAIN";


    private BluetoothService bluetoothService_obj = null;


    private DrawerLayout drawerLayout = null;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    MainFragment mainFragment;
    MainFragment2 mainFragment2;
    Intent ServerIntent;

    public String ip = "localhost";
    public int port = 30000;

    TextView textView;

    // 서비스 커넥션 해주는 바인더역할부분
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("test","onServiceConnected");
            mServiceMessenger = new Messenger(service);
            try {
                Message msg = Message.obtain(null, MyService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
            }
            catch (RemoteException e) {
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private final Handler mHandler = new Handler(){
        //핸들러 기능을 수행할 클래스(handleMessage)
        @Override
        public void handleMessage(Message msg){
            //BluetoothService로부터 메세지(msg)를 받는다.
            super.handleMessage(msg);
            System.out.println("안탄의 메세지 : " + msg.toString());

            switch (msg.what){

            }

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission(Contact.PERMISSIONS);           // 음성인식 권한 요청
        init();

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


        // 블루투스 수동적 연결버튼
        Button bluetooth_btn = (Button)findViewById(R.id.button);
        bluetooth_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBluetoothAdapter.isEnabled()) {
                    bluetoothService_obj.enableBluetooth();
                }
            }
        });

        // 서버 수동적 연결버튼
        Button server_btn = (Button)findViewById(R.id.button2);
        server_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메세지를 서비스로 전달
               sendMessageToService("from mainActivity");
            }
        });

        // 서버연결 해제버튼
        /*Button server_btn2 = (Button)findViewById(R.id.button3);
        server_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                *//*
                mIsBound = false;
                stopService(ServerIntent);
                unbindService(mConnection);
                *//*
            }
        });*/
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        // service 연결 해제
        mIsBound = false;
        stopService(ServerIntent);
        unbindService(mConnection);
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
                    bluetoothService_obj.getDeviceInfo(data);
                }
                break;

        }

    }


    public void init(){

        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        tabLayout = (TabLayout)findViewById(R.id.main_tabLayout);
        viewPager = (ViewPager)findViewById(R.id.main_viewPager);

        mainFragment = new MainFragment();
        mainFragment2 = new MainFragment2();

        tabLayout.addTab(tabLayout.newTab().setText("연결상태"));
        tabLayout.addTab(tabLayout.newTab().setText("지난 기보 보기"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                System.out.println("포지션 : " + tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        ServerIntent = new Intent(this, MyService.class);
        startService(ServerIntent);
        bindService(ServerIntent, mConnection, Context.BIND_AUTO_CREATE); // 서비스와 연결에 대한 정의
        mIsBound = true;
        //startService(ServerIntent);
    }


    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.i("testing0", "act : what " + msg.what);
            switch (msg.what){
                case MyService.MSG_SEND_TO_ACTIVITY:
                    int value1 = msg.getData().getInt("fromService");
                    String value2 = msg.getData().getString("testfromService");
                    Log.i("testing0-1", "act : value1 " + value1);
                    Log.i("testing0-2", "act : value2 " + value2);
            }

            return false;
        }
    }));

    // Service로 메세지를 보냄
    private void sendMessageToService(String str){
        if(mIsBound){
            if(mServiceMessenger != null){
                try{
                    Message msg = Message.obtain(null, MyService.MSG_SEND_TO_SERVICE, str);
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                }catch(RemoteException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public class PageAdapter extends FragmentStatePagerAdapter {
        public PageAdapter(FragmentManager manager){
            super(manager);
        }
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch(position){
                case 0:
                    return mainFragment;
                case 1:
                    return mainFragment2;
            }
            return null;
        }
    }


    // 음성인식 체크 부분



    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission(String[] permissions) {

        requestPermissions(permissions, MY_PERMISSION_REQUEST_STORAGE);
    }

    // Application permission 23
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                int cnt = permissions.length;
                for (int i = 0; i < cnt; i++) {

                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        //Log.i(LOG_TAG, "Permission[" + permissions[i] + "] = PERMISSION_GRANTED");
                    } else {

                    }
                }
                break;
        }
    }




}


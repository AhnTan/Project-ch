package com.example.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 안탄 on 2018-03-15.
 */

// 블루투스와 관련된 모든 작업들을 처리
    // 처리를 마치고 상채값을 핸들러에게 넘겨주게 되면 핸들러는 그 값을 MainActivity로 전달

public class BluetoothService {

    private int mState;
    // 검색한 기기에 연결하기 위해 Connect Thread와 ConnectedThread 클래스를 내부 클래스로 삽입.
    private ConnectTread mConnectThread;
    private ConnectedTread mConnectedThread;


    public static final int STATE_NONE = 1; // 아무것도 하지 않을 때
    public static final int STATE_LISTEN = 2; // 연결을 위해 리스닝에 들어갈 때
    public static final int STATE_CONNECTING = 3; // 연결 과정이 이루어 질 때
    public static final int STATE_CONNECTED = 4; // 기기 사이에서의 연결이 이루어 졌을 때
    public static final int STATE_FAIL = 7; // 연결이 실패 했을 때



    // intent request code
    private static final int REQUEST_CONNEXT_DEVICE = 1;
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
        if(btAdapter==null){
            Log.d(TAG, "Is not bluetooth");
        }
    }


    // 기기스캔 메소드
    public void scanDevice(){
        Log.d(TAG, "Scan Device");

        // 인텐트로 액티비티를 기기 검색 클래스로 넘김
        Intent serverIntent = new Intent(mActivity, DeviceListActivity.class);
        mActivity.startActivityForResult(serverIntent, REQUEST_CONNEXT_DEVICE);
        //새로운 액티비티를 띄워서 처리된 결과값을 mainActivity로 반환


    }


    public void enableBluetooth(){
        Log.i(TAG, "Check the enable Bluetooth");

        if(btAdapter.isEnabled()){
            //기기의 블루투스 상태가 On일 경우
            Log.d(TAG, "Bluetooth Enable Now");

            // 블루투스 장치 검색
            scanDevice();
        }
        else{
            // 기기의 블루투스 상태가 off일 경우
            Log.d(TAG, "Bluetooth Enable Request");

            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(i, REQUEST_ENABLE_BT);
        }
    }

    public boolean getDeviceState(){
        Log.d(TAG, "Check the Bluetooth support");

        if(btAdapter == null){
            Log.d(TAG, "Bluetooth is not available");
            return false;
        }
        else
        {
            Log.d(TAG, "Bluetooth is available");
            return true;
        }
    }

    // 기기의 정보를 가져온다.
    public void getDeviceInfo(Intent data){
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        // Get the BluettothDevice object
        // BluetoothDevice device = btAdapter.getRemoteDevice(address);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        Log.d(TAG, "Get Device info \n" + "address : " +address);

        connect(device);
    }

    private synchronized void setState(int state){
        Log.d(TAG, "setState() " + mState + "->" + state);
        mState = state;

        // 핸들러를 통해 상태를 메인에 넘겨준다.
        mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized  int getState(){
        return mState;
    }

    /*start() : Thread관련 service를 시작합니다.*/
    public synchronized  void start(){
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if(mConnectThread == null){

        }else{
            mConnectThread.cancel();
            mConnectThread = null;
        }
    }


    /*connect() : ConnectThread 초기화와 시작 device의 모든 연결 제거*/
    public synchronized void connect(BluetoothDevice device){
      Log.d(TAG, "connect to : " + device);

      // Cancel any thread attempting to make a connection
      if(mState == STATE_CONNECTING){
          if(mConnectThread == null){

          }else{
              mConnectThread.cancel();
              mConnectThread = null;
          }
      }

      // Cancel any thread currently running a connection
        if(mConnectedThread == null){

        }else{
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);

        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /*stop() : 모든 thread stop*/
    public synchronized void stop(){
        Log.d(TAG, "stop");

        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mConnectedThread != null){
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }


    private class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // 디바이스 정보를 얻어서 BluetoothScoket생성
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

            public void run(){
                Log.i(TAG, "BEGIN mConnectThread");
                setName("ConnectThread");

                // 연결을 시도하기 전에는 항상 기기검색을 중지한다.
                // 기기 검색이 계속되면 연결속도가 느려지기 때문이다.
            btAdapter.cancelDiscovery();

            // BluetoothSocket 연결 시도
            try{
                // BluetoothSocket 연결 시도에 대한 return 값은 succes 또는 exception 이다.
                mmSocket.connect();
                Log.d(TAG, "Connect Success");
            }catch(IOException e){
                connectionFailed(); // 연결 실패 시 불러오는 메소드
                Log.d(TAG, "Connect Fail");

                //소켓을 닫는다
                try{
                    mmSocket.close();
                }catch (IOException e2){
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // 연결 중 혹은 연결 대기상태인 메소드를 호출
                BluetoothService.this.start();
                return;
            }

            // ConnectThread 클래스를 reset한다.
            synchronized (BluetoothService.this){
                mConnectThread = null;
            }
            // ConnectThread를 시작한다.
            connected(mmSocket, mmDevice);
        }

        public void cancel(){
                try{
                    mmSocket.close();
                }
                catch(IOException e){
                    Log.e(TAG, "close() of connect socket failed", e);
                }
        }

    }
}

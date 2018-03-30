package com.example.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by 안탄 on 2018-03-15.
 */

// 블루투스와 관련된 모든 작업들을 처리
    // 처리를 마치고 상채값을 핸들러에게 넘겨주게 되면 핸들러는 그 값을 MainActivity로 전달

public class BluetoothService {

    private int mState;
    // 검색한 기기에 연결하기 위해 Connect Thread와 ConnectedThread 클래스를 내부 클래스로 삽입.
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;


    public static final int STATE_NONE = 1; // 아무것도 하지 않을 때
    public static final int STATE_LISTEN = 2; // 연결을 위해 리스닝에 들어갈 때
    public static final int STATE_CONNECTING = 3; // 연결 과정이 이루어 질 때
    public static final int STATE_CONNECTED = 4; // 기기 사이에서의 연결이 이루어 졌을 때
    public static final int STATE_FAIL = 7; // 연결이 실패 했을 때



    // intent request code
    private static final int REQUEST_CONNEXT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private static final String TAG = "BluetoothService";
    private static final String NAME = "BluetoothChat";

    //RFCOMM Protocol
    private static final UUID MY_UUID = UUID.fromString(("00000000-0000-1000-8000-00805F9B34FB"));
    //private static final UUID MY_UUID = UUID.fromString(("00000003-0000-1000-8000-00805f9b34fb"));
    //UUID uuid = UUID.randomUUID();
    //private UUID MY_UUID = uuid;



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
        //mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
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

        if(mConnectedThread == null){

        }else{
            mConnectedThread.cancel();
            mConnectedThread = null;
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

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
        Log.d(TAG, "connected");

        //Cancel the thread that completed the connection
        if(mConnectThread == null){

        }else{
            mConnectThread.cancel();
            mConnectThread = null;
        }

        //Cancel any thread currently running a connection
        if(mConnectedThread == null){

        }else{
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmisiions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
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


    // 값을 쓰는 부분(보내는 부분)
    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out)           // Create temporary object
    {
        ConnectedThread r; // Synchronize a copy of the ConnectedThread
        synchronized (this){
            if(mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        } // Perform the write unsychronized r.write(out);
        r.write(out);
    }


    // 연결 실패했을때
    private void connectionFailed(){
        setState(STATE_LISTEN);
    }

    // 연결을 잃었을 때
    private void connectionLost(){
        setState(STATE_LISTEN);
    }


    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            try{
                tmp = btAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            }catch(IOException e){
                Log.e(TAG, "listen() failed", e);
            }

            mmServerSocket = tmp;
        }

        public void run() {
            if(true)
                Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("Accept Thread");
            BluetoothSocket socket = null;

            while(mState != STATE_CONNECTED){
                try{
                    socket = mmServerSocket.accept();
                }catch(IOException e){
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                if(socket != null){
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            connected(socket, socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Could not close unwanted socket", e);
                            }
                            break;
                        }
                    }
                }
            }
            if (true)
                Log.i(TAG, "END mAcceptThread");
        }

        public void cancel(){
            if (true)
                Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }


    private class ConnectThread extends Thread{
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;

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
            Log.e(TAG, mmSocket.toString());
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
                try {
                    // 블루투스소켓 연결시 안드로이드 4.2 이상부터 블루투스 스택이 변경되었기 때문에 강제적으로 리턴 값을 1을 강제적으로 설정해줘야함
                    mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class})
                            .invoke(mmDevice, 1);
                    mmSocket.connect();
                }catch(Exception e2){
                    Log.d("TAG", "2번째 시도 " + e2);
                }

                //소켓을 닫는다
                try{
                    // connectionFailed(); // 연결 실패 시 불러오는 메소드
                    Log.d(TAG, "Connect Fail " + e);
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

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "create ConnectedThread");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // BluetoothSocket의 inputStream과 outputstream을 얻는다.
            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }catch (Exception e){
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while(true){
                try{
                    // InputStream으로부터 값을 받는 읽는 부분(값을 받는다)
                    bytes = mmInStream.read(buffer);

                    // 핸들러를 통해 상태를 메인에 넘겨준다.
                    mHandler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                }catch(Exception e){
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer){
            try{
                // 값을 쓰는 부분(값을 보낸다)
                mmOutStream.write(buffer);

                mHandler.obtainMessage(MainActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            }catch(Exception e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch (Exception e){
                Log.e(TAG, "Close() of connect socket failed", e);
            }
        }
    }
}

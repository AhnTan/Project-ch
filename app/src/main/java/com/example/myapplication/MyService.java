package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by 안탄 on 2018-04-10.
 */

public class MyService extends Service {

    public static final int MSG_REGISTER_CLIENT = 1;
    // public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SEND_TO_SERVICE = 3;
    public static final int MSG_SEND_TO_ACTIVITY = 4;

    private Messenger mClient = null; //Activity에서 가져온 Messenger

    // activity로부터 binding 된 메세지
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
        Log.w("testing", "ControlService - message what : " + msg.what + " msg.obj " + msg.obj);
        switch (msg.what) {
            case MSG_REGISTER_CLIENT:
                mClient = msg.replyTo;  // activity로부터 가져온
                sendMsgToActivity(1234);
                break;
        }
            return false;
        }
    }));

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return mMessenger.getBinder();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.d("test", "서비스의 onCreate");

        /*Button btn3 = (Button)((MainActivity)getApplicationContext()).findViewById(R.id.button3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsgToActivity(1234);
            }
        });*/
        //mHandler.obtainMessage(MainActivity.MESSAGE_READ, -1, -1, "서비스onCreate").sendToTarget();

        //mHandler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer).sendToTarget();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행
        Log.d("test", "서비스의 onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    // 할일이 끝나면 이걸로 알려야함
    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행

        Log.d("test", "서비스의 onDestroy");
    }

    private void sendMsgToActivity(int sendValue){
        try{
            Bundle bundle = new Bundle();
            bundle.putInt("fromService", sendValue);
            bundle.putString("testfromService", "Ahntan");
            Message msg = Message.obtain(null, MSG_SEND_TO_ACTIVITY);
            msg.setData(bundle);
            mClient.send(msg); // 메세지 보내기
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }


}

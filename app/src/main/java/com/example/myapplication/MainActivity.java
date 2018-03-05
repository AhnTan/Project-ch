package com.example.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
   private static final int REQUEST_ENABLE_BT = 1;
   private BluetoothAdapter mBluetoothAdapter = null;
    Intent next_intent;

    //public String ip = "localhost";
    public String ip = "localhost";
    public int port = 30000;


    TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        next_intent = new Intent(this, SubActivity.class);

       // BluetoothAdapter 인스턴스를 얻는다.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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



        textView = (TextView)findViewById(R.id.textview);

        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(next_intent);
            }
        });
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENABLE_BT){
            // Bluetooth가 활성화되었음을 표시
            if(resultCode == Activity.RESULT_OK){

            }
            // Bluetooth를 활성화 할 수 없음(사용자가 취소한 경우)
            else{

            }
        }
    }

}


 /*
        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Dialog.class);
                startActivity(intent);
            }
        });
        */
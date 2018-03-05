package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class Dialog extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_dialog);

    //Intent intent = getIntent();
    //String data = intent.getStringExtra("SubMenuSelect");

        /*
        FragmentManager fragmentManager = getFragmentManager();
        //MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.clock);
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.clock);
        */


    //tv.setText("위치나오는곳" + data);

}


    //확인 버튼 클릭 , 액티비티 닫기
    public void mOnClose(View v){
        finish();
    }

    //액티비티 이벤트
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return false;
    }

    //백버튼 막기
    public void onBackPressed(){
        return;
    }
}

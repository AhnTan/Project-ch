package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by 안탄 on 2018-03-29.
 */

public class SplashActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(2000);
        }catch(Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("state", "launch");
        startActivity(intent);
        finish();

    }
}

package com.example.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {

    public MainFragment() {
        // Required empty public constructor
    }

    // 생성자 다음으로 불리는 데이터 처리하는곳 (리사이클 처리)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        return view;
    }

}

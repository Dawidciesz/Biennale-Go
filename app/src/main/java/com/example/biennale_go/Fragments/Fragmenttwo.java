package com.example.biennale_go.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.biennale_go.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Fragmenttwo extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_slider_info, container, false);


        return view;
    }
}
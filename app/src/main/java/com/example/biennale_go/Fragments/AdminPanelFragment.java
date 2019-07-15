package com.example.biennale_go.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.biennale_go.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AdminPanelFragment extends Fragment {

    Button addQuiz;
    Button addPOI;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_panel, container, false);
        addQuiz = (Button) view.findViewById(R.id.addQuizButton);
        addQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddQuizFragment();
            }
        });
//        addPOI = (Button) view.findViewById(R.id.addPOIButton);
//        addPOI.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openAddPOIFragment();
//            }
//        });
        return view;
    }

    public void openAddQuizFragment() {
        Fragment testFragment = new AdminQuizListFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, testFragment);
        fragmentTransaction.commit();
    }

//    public void openAddPOIFragment() {
//        Fragment testFragment = new
//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_container, testFragment);
//        fragmentTransaction.commit();
//    }
}

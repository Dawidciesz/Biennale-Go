package com.example.biennale_go.Adapters;

import com.example.biennale_go.Fragments.AdminPOIListFragment;
import com.example.biennale_go.Fragments.AdminPanelFragment;
import com.example.biennale_go.Fragments.FragmentOne;
import com.example.biennale_go.Fragments.FragmentThree;
import com.example.biennale_go.Fragments.Fragmenttwo;
import com.example.biennale_go.Fragments.RegistrationLoginPageFragment;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> arrayList = new ArrayList<>();


    public ViewPagerFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentOne();
            case 1:
                return new Fragmenttwo();


        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
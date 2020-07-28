package com.example.biennale_go;

import android.app.Activity;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.biennale_go.Adapters.ViewPagerFragmentAdapter;
import com.example.biennale_go.Fragments.AdminPOIListFragment;
import com.example.biennale_go.Fragments.AdminPanelFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class LoginRegisterActivity extends FragmentActivity  implements View.OnClickListener {
    private Button registerButton, loginButton;
    private static final int NUM_PAGES = 5;
    private int dotscount;
    private ImageView[] dots;
    private ViewPager2 viewPager;
    private ViewPagerFragmentAdapter pagerAdapter;
    LinearLayout sliderDotspanel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);
        viewPager = (ViewPager2) findViewById(R.id.pagerr);
        ArrayList<Fragment> arrayList = new ArrayList<>();
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);

        arrayList.add(new AdminPOIListFragment());
        arrayList.add(new AdminPanelFragment());

        pagerAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(pagerAdapter);


        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });
        registerButton = (Button) findViewById(R.id.registrationButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationActivity();
            }
        });


        dotscount = 2;
        dots = new ImageView[dotscount];


        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }
        });
    }
//    @Override
//    public void onBackPressed() {
//        if (viewPager.getCurrentItem() == 0) {
//            // If the user is currently looking at the first step, allow the system to handle the
//            // Back button. This calls finish() on this activity and pops the back stack.
//            super.onBackPressed();
//        } else {
//            // Otherwise, select the previous step.
//            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
//        }
//    }
//
//    /**
//     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
//     * sequence.
//     */
//    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
//        public ScreenSlidePagerAdapter(FragmentActivity fa) {
//            super(fa);
//        }
//
//        @Override
//        public Fragment createFragment(int position) {
//            return new RegistrationLoginPageFragment();
//        }
//
//        @Override
//        public int getItemCount() {
//            return NUM_PAGES;
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
        //TODO auto logowanie :)
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (user != null
//                || account != null
        ) {
            Intent i = new Intent(LoginRegisterActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    public void openMenuActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openRegistrationActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivityGoogleFB.class);
        startActivity(intent);
    }

    public void onClick(View v) {
        openMenuActivity();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
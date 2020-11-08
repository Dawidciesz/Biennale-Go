package com.example.biennale_go;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

public class LoginRegisterActivity extends FragmentActivity  implements View.OnClickListener {
    private Button registerButton, loginButton;
    private int dotscount;
    private ImageView[] dots;
    private ViewPager2 viewPager;
    private ViewPagerFragmentAdapter pagerAdapter;
    LinearLayout sliderDotspanel;
    FirebaseFirestore db ;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_email_password);
        if (user != null
        ) {
            Intent i = new Intent(LoginRegisterActivity.this, MainActivity.class);
            startActivity(i);
        } else {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (user != null
        ) {
            Intent i = new Intent(LoginRegisterActivity.this, MainActivity.class);
            startActivity(i);
        } else {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

    }

    public void onClick(View v) {
        openMenuActivity();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
package com.example.biennale_go;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.biennale_go.Adapters.MenuListAdapter;
import com.example.biennale_go.Adapters.RankingListAdapter;
import com.example.biennale_go.Fragments.AdminPanelFragment;
import com.example.biennale_go.Fragments.PoiFragment;
import com.example.biennale_go.Fragments.ProfilFragment;
import com.example.biennale_go.Fragments.QuizListFragment;
import com.example.biennale_go.Fragments.RankingFragment;
import com.example.biennale_go.Fragments.RoutesListFragment;
import com.example.biennale_go.Utility.CurrentUser;
import com.example.biennale_go.Utility.MenuListItem;
import com.example.biennale_go.Utility.RankingItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements MenuListAdapter.OnMenuItemClick{
    private ImageView mapButton, upButton, mainMenuButton;
    private static final String TAG = "MainActivity";
    private String startFragment;
    private ArrayList scoresList;
    private ArrayList<String> poiScores;
    private TranslateAnimation animate;
    private int y;
    Button myButton;
    boolean isUp;
//    boolean isFinished;
    private int yDelta;
    private RecyclerView recyclerView;
    private RelativeLayout listView;
    private LinearLayout buttons;
    private List<MenuListItem> items = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText newQuizName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> scores = new ArrayList<>();
    ObjectAnimator oax;
    ObjectAnimator oay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PackageInfo info;
        Resources res = getResources();

//
        items.add(new MenuListItem("MAPA", res.getDrawable(R.drawable.ic_formy, getTheme()), 3, 3));
        items.add(new MenuListItem("QUIZ", res.getDrawable(R.drawable.ic_formy, getTheme()), 3, 3));
        items.add(new MenuListItem("FORMY",res.getDrawable(R.drawable.ic_formy, getTheme()), 3, 3));
        items.add(new MenuListItem("TRASY", res.getDrawable(R.drawable.ic_formy, getTheme()), 3, 3));
        items.add(new MenuListItem("PROFIL", res.getDrawable(R.drawable.ic_formy, getTheme()), 3, 3));
        items.add(new MenuListItem("RANKING", res.getDrawable(R.drawable.ic_formy, getTheme()),  3, 3));
//
        try {
            info = getPackageManager().getPackageInfo("com.example.biennale_go", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        if (!CurrentUser.isLogged) {

        }
        CurrentUser.setCurrentUser();
        setContentView(R.layout.activity_main);


        mapButton = (ImageView) findViewById(R.id.map);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });

        listView = (RelativeLayout) findViewById(R.id.list_view);
        buttons = (LinearLayout) findViewById(R.id.emailPasswordButtons);
        upButton = (ImageView) findViewById(R.id.upButton);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSlideViewButtonClick();
            }
        });
        mainMenuButton = (ImageView) findViewById(R.id.menu);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSlideViewButtonClick();
            }
        });

        isUp = false;
//        isFinished = true;
    }

    public void openMapActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void openAdminPanelFragment() {
        Fragment adminPanelFragment = new AdminPanelFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, adminPanelFragment);
        fragmentTransaction.commit();
    }

    public void openQuizList() {
        Fragment quizListFragment = new QuizListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, quizListFragment);
        fragmentTransaction.commit();
    }

    public void openRoutesList() {
        Fragment routesListFragment = new RoutesListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, routesListFragment);
        fragmentTransaction.commit();
    }

    public void openPoi() {
        Fragment poiFragment = new PoiFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, poiFragment);
        fragmentTransaction.commit();
    }

    public void openProfil() {
        Fragment profilFragment = new ProfilFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, profilFragment);
        fragmentTransaction.commit();
    }

    public void openRanking() {
        Fragment rankingFragment = new RankingFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, rankingFragment);
        fragmentTransaction.commit();
    }

    public void openMenuActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }


    // slide the view from top of itself to the current position
    public void slideDown(View view, int yHight){
        view.setVisibility(View.VISIBLE);
        int hightOfY = yHight;
//
        if (yHight > 1)
            hightOfY = yHight - view.getHeight();




        // Create animators for x and y axes
        oax = ObjectAnimator.ofInt(view, "translationX", 0, 0);
        oay = ObjectAnimator.ofFloat(view, "translationY", hightOfY, 0);


// Combine Animators and start them together
        AnimatorSet set = new AnimatorSet();
        set.setDuration(800);
        set.playTogether(oax, oay);
        set.start();
buttons.setZ(0);
view.setZ(1);
//
//
//
//
////        isFinished = false;
//        if (yHight ==0)
//          yHight = 0 - view.getHeight();
//        TranslateAnimation animate = new TranslateAnimation(
//                0,                 // fromXDelta
//                0,                 // toXDelta
//                0 - view.getHeight(),  // fromYDelta
//                0);
//        animate.setDuration(1111);
//        animate.setFillAfter(true);
////        animate.setAnimationListener(new Animation.AnimationListener() {
////            @Override
////            public void onAnimationStart(Animation animation) {
////                isFinished = false;
////            }
////            @Override
////            public void onAnimationEnd(Animation animation) {
////                isFinished = true;
////            }
////            @Override
////            public void onAnimationRepeat(Animation animation) {
////            }
////        });
//        view.startAnimation(animate);
        view.setZ(1);
//        if (animate.hasEnded()) {
//            isFinished = true;
//        }
    }

    // slide the view from its current position to below itself
    public void slideUp(View view, int yHight){





// Create animators for x and y axes
        oax = ObjectAnimator.ofInt(view, "translationX", 0, 0);
        oay = ObjectAnimator.ofFloat(view, "translationY", yHight, -view.getHeight());


// Combine Animators and start them together
        AnimatorSet set = new AnimatorSet();
        set.setDuration(800);
        set.playTogether(oax, oay);
        set.start();



        //        isFinished = false;
//        TranslateAnimation animate = new TranslateAnimation(
//                0,                 // fromXDelta
//                0,                 // toXDelta
//                yHight,                 // fromYDelta
//               0 - view.getHeight()); // toYDelta
//        animate.setDuration(1111);
//        animate.setFillAfter(true);
////        animate.setAnimationListener(new Animation.AnimationListener() {
////            @Override
////            public void onAnimationStart(Animation animation) {
////                isFinished = false;
////            }
////            @Override
////            public void onAnimationEnd(Animation animation) {
////                isFinished = true;
////            }
////            @Override
////            public void onAnimationRepeat(Animation animation) {
////            }
////        });
//        view.startAnimation(animate);
//        view.setZ(1);
//        if (animate.hasEnded()) {
////            isFinished = true;
//        }
    }

    public void onSlideViewButtonClick() {

        recyclerView = (RecyclerView) findViewById(R.id.ranking_list_recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MenuListAdapter(items, this);
        recyclerView.setAdapter(adapter);
        int[] location = new int[2];

        listView.getLocationOnScreen(location);

//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 500f);
////        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
////        valueAnimator.geta
        if (oax != null)
        y = Math.round((Float) oay.getAnimatedValue());
        else
            y = -2000;
        if (isUp
//                && isFinished
        ) {
            slideUp(listView, y);
        } else if (isUp == false
//                && isFinished
        ) {
          slideDown(listView , y);
        }
        isUp = !isUp;
    }

    @Override
    public void onMenuItemClick(int position) {

        if (items.get(position).getName() != null) {
            String fragmentName = items.get(position).getName();
            if (fragmentName.equals("QUIZ")) {
                openQuizList();
            } else if (fragmentName.equals("TRASY")) {
                openRoutesList();
            } else if (fragmentName.equals("FORMY")) {
                openPoi();
            } else if (fragmentName.equals("PROFIL")) {
                openProfil();
            } else if (fragmentName.equals("RANKING")) {
                openRanking();
            } else if (fragmentName.equals("ADMIN")) {
                openAdminPanelFragment();
            } else if (fragmentName.equals("MAPA")) {
                openMapActivity();
            }
        }
        slideUp(listView,0);
        isUp = !isUp;
    }
}

    //
//    private void revealFAB() {
////        View view = (RecyclerView) findViewById(R.id.ranking_list_recycler);
//        recyclerView = (RecyclerView) findViewById(R.id.ranking_list_recycler);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        adapter = new RankingListAdapter(items, null);
//        recyclerView.setAdapter(adapter);
//        newQuizName = (EditText) recyclerView.findViewById(R.id.fieldNewQuiz);
//
//
//
//        int cx = recyclerView.getWidth() / 2;
//        int cy = 0;
//
//        float finalRadius = (float) Math.hypot(cx,  recyclerView.getHeight()/2);
//        Animator anim = ViewAnimationUtils.createCircularReveal(recyclerView, 32, cy, 0, finalRadius);
//        anim.setDuration(2000);
//        recyclerView.setVisibility(View.VISIBLE);
//
//        anim.start();
//
//
//
//
//
////        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
//
//    }
//
//    private void hideFAB() {
//        final View view = findViewById(R.layout.fragment_ranking);
//
//        int cx = view.getWidth() / 2;
//        int cy = view.getHeight() / 2;
//
//        float initialRadius = (float) Math.hypot(cx, cy);
//
//        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
//
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                view.setVisibility(View.INVISIBLE);
//            }
//        });
//
//        anim.start();
//    }


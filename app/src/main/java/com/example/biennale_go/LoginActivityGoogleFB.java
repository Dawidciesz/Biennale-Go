package com.example.biennale_go;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class LoginActivityGoogleFB extends Activity implements View.OnClickListener {
    FirebaseAuth mAuth;
    private Button emailLogin;
    private Button googleLogin;
    private LoginButton facebookLogin;
    private RelativeLayout facebookCustomButton;
    private CallbackManager callbackManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        AppEventsLogger.activateApp(getApplication());
        callbackManager = CallbackManager.Factory.create();
        FirebaseApp.initializeApp(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1098753502328-2u950vhhekqvjolpkan0398npii5n1v7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleLogin = findViewById(R.id.googleSignIn);
        facebookCustomButton = findViewById(R.id.facebook_custom_buttom);
        facebookCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLogin.performClick();
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            }
        });
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idView = v.getId();
                if (idView == R.id.googleSignIn) {
                        signIn();
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                }
            }
        });
        emailLogin = findViewById(R.id.emailSignInButton);
        emailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idView = v.getId();
                if (idView == R.id.emailSignInButton) {
                    openLoginActivity();
                }
            }
        });
        facebookLogin = findViewById(R.id.facebookSignIn);
        facebookLogin.setPermissions("email", "public_profile");
        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {

                Log.d("", "facebook:onCancel");
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openMenuActivity() {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

      public void opeAccountSettingsActivity() {
            Intent intent = new Intent(this, AccountSettingsActivity.class);
            startActivity(intent);
    }

    public void onClick(View v) {
    }

    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    public  void createUser(int age, String name, String email) {
        Map<String, Object> data = new HashMap<>();
        data.put("age", age);
        data.put("name", name);
        data.put("distance_traveled", 0);
        data.put("score", 0);
        data.put("profile_img", "");
        data.put("profile_color", "");
        db.collection("users").document(email).set(data);

        DocumentReference docRef =   db.collection("users").document(email);
        Map<String, Object> info = new HashMap<>();
        info.put("name", name);
        info.put("visited_count", 0);
        docRef.collection("POI_visited").document(name).set(info);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("", "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            DocumentReference docRef = db.collection("users").document(user.getEmail());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            openMenuActivity();
                                        }
                                        else {
                                            createUser(0, user.getDisplayName(), user.getEmail());
                                            opeAccountSettingsActivity();
                                        }
                                    }}});

                        } else {
                            Log.w("", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
           final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection("users").document(account.getEmail());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        openMenuActivity();
                                    }
                                    else {
                                        createUser(0, account.getDisplayName(), account.getEmail());
                                        opeAccountSettingsActivity();
                                    }
                                }}});
                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                    }
                }
            });
        } catch (ApiException e) {
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
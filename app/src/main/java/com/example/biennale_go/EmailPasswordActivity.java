package com.example.biennale_go;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.biennale_go.Fragments.AdminPanelFragment;
import com.example.biennale_go.Fragments.LoginFragment;
import com.example.biennale_go.Fragments.RegistrationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class EmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    //todo fix auth
    FirebaseAuth mAuth;
    private Button registerButton, loginButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseApp.initializeApp(getApplicationContext());
        setContentView(R.layout.activity_email_password);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(EmailPasswordActivity.this, MainActivity.class);
            startActivity(i);
        } else {
            // User is signed out
        }
//        email = findViewById(R.id.fieldEmail);
//        age = findViewById(R.id.fieldAge);
//        name = findViewById(R.id.fieldName);
//        password = findViewById(R.id.fieldPassword);
//        successInfo = findViewById(R.id.successText);
//        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
//        findViewById(R.id.emailSignInButton).setOnClickListener(this);
//        mAuth = FirebaseAuth.getInstance();
        registerButton = (Button) findViewById(R.id.registrationButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegistrationFragment();
            }
        });

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginFragment();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        //TODO
    }

//    public void createAccount(String email, String password) {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser user = mAuth.getCurrentUser();
////                            createUser();
//                            successInfo.setText("zarejestrowano");
//                        } else {
//                            successInfo.setText("Cos poszlo nie tak - sproboj ponownie");
//                        }
//                    }
//                });
//    }

//    public void signIn(String email, String password) {
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUi(user);
//                            successInfo.setText("zalogowano");
//
//                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                            startActivity(intent);
//                        } else {
//                            updateUi(null);
//                            successInfo.setText("Cos poszlo nie tak - sproboj ponownie");
//                        }
//                    }
//                });
//    }

public void openMenuActivity(){
    Intent intent = new Intent(this, MenuActivity.class);
    startActivity(intent);
}

public void openRegistrationFragment(){
    Fragment registrationFragment = new RegistrationFragment();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.fragment_container, registrationFragment);
    fragmentTransaction.commit();
}

public void openLoginFragment(){
    Fragment loginFragment = new LoginFragment();
    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
    fragmentTransaction.replace(R.id.fragment_container, loginFragment);
    fragmentTransaction.commit();
}

    public void onClick(View v) {
//        int idView = v.getId();
//        if (idView == R.id.emailCreateAccountButton) {
//            createAccount(email.getText().toString(), password.getText().toString());
    openMenuActivity();
    }
// else if (idView == R.id.emailSignInButton) {
////            signIn(email.getText().toString(), password.getText().toString());
//        }
//  }

//    public void updateUi(FirebaseUser user) {
//        if (user == null) {
//            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
//            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
//        } else {
//            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
//            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
//    public  void createUser(int age, String gender, String name) {
//        Map<String, Object> data = new HashMap<>();
//        data.put( "age", age);
//        data.put( "avatar", "");
//        data.put("gender", gender);
//        data.put("name", name);
//        data.put("score", 0);
//        db.collection("users").document(email.getText().toString()).set(data);    }
}
package com.example.biennale_go;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

public class EmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    //todo fix auth
    FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private TextView successInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        setContentView(R.layout.activity_email_password);
        email = findViewById(R.id.fieldEmail);
        password = findViewById(R.id.fieldPassword);
        successInfo = findViewById(R.id.successText);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        // TEST FIRESTORM TODO
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> docData = new HashMap<>();
        docData.put("login", "roki130");
        docData.put("password", "test123");
        docData.put("email", "dawidcieszewski@gmail.com");
        docData.put("name", "Dawid");
        docData.put("surname", "Cieszewski");
        docData.put("age", "23");
        docData.put("gender", "AttackHelicopter");
        docData.put("is_admin", true);
        docData.put("avatar", "jakisURL");
        docData.put("description", "XD");

        db.collection("users").document("user_0").set(docData);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //TODO
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUi(user);
                            successInfo.setText("zarejestrowano");
                        } else {
                            updateUi(null);
                            successInfo.setText("Cos poszlo nie tak - sproboj ponownie");
                        }
                    }
                });
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUi(user);
                            successInfo.setText("zalogowano");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            updateUi(null);
                            successInfo.setText("Cos poszlo nie tak - sproboj ponownie");
                        }
                    }
                });
    }

    public void onClick(View v) {
        int idView = v.getId();
        if (idView == R.id.emailCreateAccountButton) {
            createAccount(email.getText().toString(), password.getText().toString());
        } else if (idView == R.id.emailSignInButton) {
            signIn(email.getText().toString(), password.getText().toString());
        }
  }

    public void updateUi(FirebaseUser user) {
        if (user == null) {
            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
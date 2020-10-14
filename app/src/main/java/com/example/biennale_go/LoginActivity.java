package com.example.biennale_go;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends Activity implements View.OnClickListener {
    FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;
    private Button loginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login_email);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        emailField = findViewById(R.id.fieldEmail);
        passwordField = findViewById(R.id.fieldPassword);
        loginUser = findViewById(R.id.emailSignInButton);
        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idView = v.getId();
                if (idView == R.id.emailSignInButton) {
                    if (validate()) {
                        signIn(emailField.getText().toString().toLowerCase(), passwordField.getText().toString());
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void openMenuActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClick(View v) {
        openMenuActivity();
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            openMenuActivity();
                        } else {
                            emailField.setError("Niepoprawny adres email bądź hasło");
                        }
                    }
                });
    }
    public boolean validate() {
        boolean isAllcorrect = true;
        if (emailField.getText().toString().length() == 0) {
            emailField.setError("Adres email jest wymagany");
            isAllcorrect = false;
        }
        if (passwordField.getText().toString().length() < 6) {
            passwordField.setError("Hasło jest wymagane");
            isAllcorrect = false;
        }
        return isAllcorrect;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
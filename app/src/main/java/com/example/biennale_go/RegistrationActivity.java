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
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.NonNull;

public class RegistrationActivity extends Activity implements View.OnClickListener {
    private EditText emailField;
    private EditText passwordField;
    private EditText ageField;
    private EditText gender;
    private EditText nameField;
    private Button registerButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.fragment_register);
        emailField = findViewById(R.id.fieldEmail);
        passwordField = findViewById(R.id.fieldPassword);
        nameField = findViewById(R.id.fieldName);
        registerButton = findViewById(R.id.emailCreateAccountButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idView = v.getId();
                if (idView == R.id.emailCreateAccountButton) {
                    if (validate()) {
                        openMenuActivity();


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
        Intent intent = new Intent(this, AccountSettingsActivity.class);
        intent.putExtra("name",nameField.getText().toString());
        intent.putExtra("email",emailField.getText().toString().toLowerCase());
        intent.putExtra("pass", passwordField.getText().toString());
        startActivity(intent);
    }

    public void onClick(View v) {
        openMenuActivity();
    }

    public boolean validate() {
        boolean isAllcorrect = true;
        if (emailField.getText().toString().length() == 0) {
            emailField.setError("Adres email jest wymagany");
            isAllcorrect = false;
        }
        if (passwordField.getText().toString().length() < 6) {
            passwordField.setError("Hasło jest wymagane i musi zawierać nie mniej niż 6 znaków");
            isAllcorrect = false;
        }
        if (nameField.getText().toString().length() == 0) {
            nameField.setError("nazwa jest wymagana");
            isAllcorrect = false;
        }
        return isAllcorrect;
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
}
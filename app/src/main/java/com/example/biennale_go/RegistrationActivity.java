package com.example.biennale_go;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.biennale_go.Utility.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends Activity implements View.OnClickListener {
    private EditText emailField;
    private EditText passwordField;
    private EditText ageField;
    private EditText gender;
    private EditText nameField;
    private Button registerButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
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
                        createAccount(emailField.getText().toString(), passwordField.getText().toString());
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
        intent.putExtra("email",emailField.getText().toString());
        startActivity(intent);
    }



    public void onClick(View v) {
        openMenuActivity();
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            createUser();
//                            CurrentUser.setCurrentUser();
                            openMenuActivity();
                        } else {
                            emailField.setError("Niepoprawny adres email");
                        }
                    }
                });
    }
//    public  void createUser() {
//        Map<String, Object> data = new HashMap<>();
//        data.put( "age", 0);
//        data.put("name", nameField.getText().toString());
//        data.put("distance_traveled", 0);
//        data.put("score", 0);
//        data.put("profile_img", "");
//        data.put("profile_color", "");
//        db.collection("users").document(emailField.getText().toString()).set(data);
//
//
//
//        DocumentReference docRef =   db.collection("users").document(emailField.getText().toString());
//        Map<String, Object> info = new HashMap<>();
//        info.put("name", nameField.getText().toString());
//        info.put("visited_count", 0);
//        docRef.collection("POI_visited").document(nameField.getText().toString()).set(info);
//    }

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
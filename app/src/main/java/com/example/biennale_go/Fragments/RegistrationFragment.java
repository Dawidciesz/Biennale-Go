package com.example.biennale_go.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.biennale_go.MainActivity;
import com.example.biennale_go.MenuActivity;
import com.example.biennale_go.R;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RegistrationFragment extends Fragment {
    private EditText email;
    private EditText password;
    private EditText age;
    private EditText gender;
    private EditText name;
    private Button registerButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        FirebaseApp.initializeApp(getContext());
        mAuth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        email = view.findViewById(R.id.fieldEmail);
        password = view.findViewById(R.id.fieldPassword);
        age = view.findViewById(R.id.fieldAge);
        name = view.findViewById(R.id.fieldName);
        registerButton = view.findViewById(R.id.emailCreateAccountButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idView = v.getId();
                if (idView == R.id.emailCreateAccountButton) {
                    createAccount(email.getText().toString(), password.getText().toString());
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
        //TODO
    }

    public void openMenuActivity(){
        Intent intent = new Intent(getActivity(), MenuActivity.class);
        startActivity(intent);
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            createUser();
                            openMenuActivity();
                        } else {
                        }
                    }
                });
    }
        public  void createUser() {
        Map<String, Object> data = new HashMap<>();
        data.put( "age", age.getText().toString());
        data.put( "avatar", "");
//        data.put("gender", gender.getText().toString());
    data.put("name", name.getText().toString());
        data.put("distance_traveled", 0);
        data.put("score", 0);
        db.collection("users").document(email.getText().toString()).set(data);    }
}
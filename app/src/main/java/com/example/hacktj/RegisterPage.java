package com.example.hacktj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity implements View.OnClickListener{

    private FirebaseFirestore db;
    private TextView logo;
    private EditText editFullName, editEmail, editPassword;
    private Button registerButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        logo = findViewById(R.id.registerPageLogo);
        editFullName = findViewById(R.id.registerPageFullName);
        editEmail = findViewById(R.id.registerPageEmail);
        editPassword = findViewById(R.id.registerPagePassword);
        registerButton = findViewById(R.id.registerPageButton);

        logo.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.registerPageButton:
                registerUser();
                break;
            case R.id.registerPageLogo:
                startActivity(new Intent(RegisterPage.this, StartingPage.class));
                break;
        }
    }
    public void registerUser(){
        String fullName = editFullName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        if (fullName.isEmpty()){
            editFullName.setError("Full Name is Required");
            editFullName.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Valid Email is Required");
            editEmail.requestFocus();
            return;
        }
        if (password.length() < 6){
            editPassword.setError("Password must be at least 6 characters");
            editPassword.requestFocus();
            return;
        }
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Map<String, Object> user = new HashMap<>();
                            user.put("Email", email);
                            user.put("fullName", fullName);
                            String uid = task.getResult().getUser().getUid();
                            db.collection("users").document(uid).set(user).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(RegisterPage.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterPage.this, StartingPage.class));
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(RegisterPage.this, "Failed To Register", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}